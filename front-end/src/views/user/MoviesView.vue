<script setup>
import { ref, reactive, onMounted, watch, computed } from 'vue'
import { useRouter } from 'vue-router'
import {
  PlayCircleOutlined,
  HeartOutlined,
  HeartFilled,
  LikeOutlined,
  LikeFilled,
  MessageOutlined,
  StarOutlined,
  StarFilled,
  MessageFilled,
} from '@ant-design/icons-vue'
import request from '@/services/request'
import { message, Modal } from 'ant-design-vue'
import axios from '@/services/request'
const router = useRouter()

const userStore = JSON.parse(localStorage.getItem('user'))

// 筛选表单
const filterForm = reactive({
  categories: [],
  region: undefined,
  year: '',
  duration: '',
  price: '',
  actor: '',
})

// 分类和地区选项
const categories = ref([])
const regions = ref([])

// 所有电影数据
const allMovies = ref([])

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 12,
  total: 0,
  showSizeChanger: true,
  pageSizeOptions: ['12', '24', '48', '96'],
})

// 添加播放相关的状态
const playModalVisible = ref(false)
const currentMovie = ref(null)
const loading = ref(false)

// 收藏相关状态
const collectionModalVisible = ref(false)
const selectedMovie = ref(null)
const userCollections = ref([])
const collectionStatus = ref({})
const selectedCollectionId = ref(null)

// 互动相关状态
const interactionStatus = ref({})
const ratingModalVisible = ref(false)
const commentModalVisible = ref(false)
const currentRating = ref(0)
const currentComment = ref('')

// 筛选后的电影列表
const filteredMovies = computed(() => {
  let result = [...allMovies.value]

  // 按分类筛选
  if (filterForm.categories.length > 0) {
    result = result.filter((movie) =>
      filterForm.categories.some((cat) => movie.categories.includes(cat)),
    )
  }

  // 按地区筛选
  if (filterForm.region) {
    result = result.filter((movie) => String(movie.region) === filterForm.region)
  }

  // 按年份筛选
  if (filterForm.year) {
    result = result.filter((movie) => movie.year === Number(filterForm.year))
  }

  // 按时长筛选
  if (filterForm.duration) {
    result = result.filter((movie) => movie.duration === Number(filterForm.duration))
  }

  // 按价格筛选
  if (filterForm.price) {
    result = result.filter((movie) => {
      const moviePrice = movie.isFree ? 0 : movie.price
      return moviePrice === Number(filterForm.price)
    })
  }

  // 按主演筛选
  if (filterForm.actor) {
    result = result.filter(
      (movie) =>
        movie.actors && movie.actors.toLowerCase().includes(filterForm.actor.toLowerCase()),
    )
  }

  return result
})

// 当前页的电影
const currentPageMovies = computed(() => {
  const start = (pagination.current - 1) * pagination.pageSize
  const end = start + pagination.pageSize
  return filteredMovies.value.slice(start, end)
})

// 获取分类列表
const fetchCategories = async () => {
  try {
    const { data } = await request.get('/categories')
    categories.value = data.map((item) => ({
      label: item.name,
      value: item.id.toString(),
    }))
  } catch (error) {
    console.error('获取分类列表失败:', error)
  }
}

// 获取地区列表
const fetchRegions = async () => {
  try {
    const { data } = await request.get('/regions')
    regions.value = [
      { label: '全部', value: '' },
      ...data.map((item) => ({
        label: item.name,
        value: item.id.toString(),
      })),
    ]
  } catch (error) {
    console.error('获取地区列表失败:', error)
  }
}

// 根据地区ID获取地区名称
const getRegionName = (regionId) => {
  const region = regions.value.find((r) => r.value === String(regionId))
  return region ? region.label : '未知地区'
}

// 获取所有电影的互动状态
const fetchAllInteractions = async () => {
  try {
    const { data } = await request.get(`/movie-interactions/user/${userStore.userId}`)
    // 将数组转换为以movieId为key的对象
    const interactionsMap = {}
    data.forEach((interaction) => {
      interactionsMap[interaction.movieId] = interaction
    })
    interactionStatus.value = interactionsMap
    console.log('所有互动状态:', interactionStatus.value)
  } catch (error) {
    console.error('获取互动状态失败:', error)
  }
}

// 获取电影列表
const fetchMovies = async () => {
  try {
    const { data } = await request.get('/movies')
    allMovies.value = data
    // 检查每部电影的收藏状态
    await Promise.all(data.map((movie) => checkCollectionStatus(movie.id)))
    // 一次性获取所有互动状态
    await fetchAllInteractions()
    updatePagination()
  } catch (error) {
    console.error('获取电影列表失败:', error)
  }
}

// 更新分页信息
const updatePagination = () => {
  pagination.total = filteredMovies.value.length
  // 如果当前页超出范围，重置为第一页
  const maxPage = Math.ceil(pagination.total / pagination.pageSize)
  if (pagination.current > maxPage) {
    pagination.current = 1
  }
}

const deleteMovie = async (movieId) => {
  const collection = userCollections.value.find((c) => c.movieIds.includes(String(movieId)))
  console.log(collection)
  await request.delete(`/collections/${collection.id}/movies/${movieId}`)
  checkCollectionStatus(movieId)
}

// 播放电影
const playMovie = async (movie, event) => {
  event.stopPropagation()
  if (!movie.isFree) {
    try {
      // 先检查是否已购买
      const { data: isPurchased } = await request.get(`/orders/check`, {
        params: {
          userId: userStore.userId,
          movieId: movie.id,
        },
      })

      if (!isPurchased) {
        // 弹出购买确认框
        const confirmResult = await new Promise((resolve) => {
          Modal.confirm({
            title: '购买确认',
            content: `确定要以 ¥${movie.price} 购买《${movie.title}》吗？`,
            okText: '确认购买',
            cancelText: '取消',
            onOk: () => resolve(true),
            onCancel: () => resolve(false),
          })
        })

        if (!confirmResult) {
          return
        }

        // 用户确认购买，创建并支付订单
        try {
          loading.value = true
          // 创建订单
          const { data: order } = await request.post('/orders', {
            userId: userStore.userId,
            movieId: movie.id,
            amount: movie.price,
          })

          // 立即支付
          await request.post(`/orders/${order.id}/pay`)
          message.success('购买成功！')

          // 播放电影
          movie.playCount += 1
          axios.put(`/movies/${movie.id}`, {
            ...movie,
            playCount: movie.playCount,
          })
          playModalVisible.value = true
          currentMovie.value = movie
        } catch (error) {
          console.error('购买失败:', error)
          message.error(error.response?.data?.message || '购买失败，请稍后重试')
        } finally {
          loading.value = false
        }
        return
      }
    } catch (error) {
      console.error('检查购买状态失败:', error)
      message.error('检查购买状态失败，请稍后重试')
      return
    }
  }

  try {
    // 更新播放次数
    // 免费电影或已购买的电影，直接播放
    movie.playCount += 1
    axios.put(`/movies/${movie.id}`, {
      ...movie,
      playCount: movie.playCount,
    })
    playModalVisible.value = true
    currentMovie.value = movie
  } catch (error) {
    console.error('更新播放次数失败:', error)
    message.error('播放失败，请稍后重试')
  }
}

// 检查电影是否已收藏
const checkCollectionStatus = async (movieId) => {
  try {
    const { data } = await request.get('/collections/check', {
      params: {
        userId: userStore.userId,
        movieId,
      },
    })
    collectionStatus.value[movieId] = data
  } catch (error) {
    console.error('检查收藏状态失败:', error)
  }
}

// 获取用户的收藏夹列表
const fetchUserCollections = async () => {
  try {
    const { data } = await request.get('/collections', {
      params: {
        userId: userStore.userId,
      },
    })
    userCollections.value = data
  } catch (error) {
    console.error('获取收藏夹列表失败:', error)
  }
}

// 显示收藏对话框
const showCollectionModal = (movie, event) => {
  event.stopPropagation()
  selectedMovie.value = movie
  selectedCollectionId.value = null
  collectionModalVisible.value = true
  fetchUserCollections()
}

// 确认收藏
const confirmCollection = async () => {
  if (!selectedCollectionId.value) {
    return
  }

  try {
    await request.post(`/collections/${selectedCollectionId.value}/movies`, null, {
      params: {
        movieId: selectedMovie.value.id,
      },
    })
    checkCollectionStatus(selectedMovie.value.id)
    collectionModalVisible.value = false
    selectedMovie.value = null
    selectedCollectionId.value = null
  } catch (error) {
    console.error('添加到收藏夹失败:', error)
  }
}

// 切换点赞状态
const toggleLike = async (movie, event) => {
  event.stopPropagation()
  try {
    const currentStatus = interactionStatus.value[movie.id] || {}
    const interaction = {
      liked: !currentStatus.liked,
      rating: currentStatus.rating,
      comment: currentStatus.comment,
    }

    await request.put(`/movie-interactions/user/${userStore.userId}/movie/${movie.id}`, interaction)
    // 重新获取所有互动状态
    await fetchAllInteractions()
  } catch (error) {
    console.error('点赞操作失败:', error)
  }
}

// 显示评分对话框
const showRatingModal = (movie, event) => {
  event.stopPropagation()
  selectedMovie.value = movie
  currentRating.value = interactionStatus.value[movie.id]?.rating || 0
  ratingModalVisible.value = true
}

// 提交评分
const submitRating = async () => {
  if (!currentRating.value) return

  try {
    const currentStatus = interactionStatus.value[selectedMovie.value.id] || {}
    const interaction = {
      liked: currentStatus.liked || false,
      rating: currentRating.value,
      comment: currentStatus.comment,
    }

    await request.put(
      `/movie-interactions/user/${userStore.userId}/movie/${selectedMovie.value.id}`,
      interaction,
    )
    // 重新获取所有互动状态
    await fetchAllInteractions()
    ratingModalVisible.value = false
    selectedMovie.value = null
  } catch (error) {
    console.error('评分失败:', error)
  }
}

// 显示评论对话框
const showCommentModal = (movie, event) => {
  event.stopPropagation()
  selectedMovie.value = movie
  currentComment.value = interactionStatus.value[movie.id]?.comment || ''
  commentModalVisible.value = true
}

// 提交评论
const submitComment = async () => {
  if (!currentComment.value.trim()) return

  try {
    const currentStatus = interactionStatus.value[selectedMovie.value.id] || {}
    const interaction = {
      liked: currentStatus.liked || false,
      rating: currentStatus.rating,
      comment: currentComment.value.trim(),
    }

    await request.put(
      `/movie-interactions/user/${userStore.userId}/movie/${selectedMovie.value.id}`,
      interaction,
    )
    // 重新获取所有互动状态
    await fetchAllInteractions()
    commentModalVisible.value = false
    selectedMovie.value = null
    currentComment.value = ''
  } catch (error) {
    console.error('评论失败:', error)
  }
}

// 删除评论
const deleteComment = async (movie, event) => {
  event.stopPropagation()
  try {
    const currentStatus = interactionStatus.value[movie.id] || {}
    const interaction = {
      liked: currentStatus.liked || false,
      rating: currentStatus.rating,
      comment: null,
    }

    await request.put(`/movie-interactions/user/${userStore.userId}/movie/${movie.id}`, interaction)
    // 重新获取所有互动状态
    await fetchAllInteractions()
  } catch (error) {
    console.error('删除评论失败:', error)
  }
}

// 监听筛选条件变化
watch(
  filterForm,
  () => {
    pagination.current = 1 // 重置页码
    updatePagination()
  },
  { deep: true },
)

onMounted(() => {
  fetchCategories()
  fetchRegions()
  fetchMovies()
  fetchUserCollections()
})
</script>

<template>
  <div class="movies-container page-container">
    <div class="filter-section content-card">
      <a-form :model="filterForm" layout="vertical">
        <a-row :gutter="16">
          <a-col :span="24" :md="8">
            <a-form-item label="分类">
              <a-select
                v-model:value="filterForm.categories"
                mode="multiple"
                :options="categories"
                placeholder="请选择分类"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>

          <a-col :span="24" :md="8">
            <a-form-item label="地区">
              <a-select
                v-model:value="filterForm.region"
                :options="regions"
                placeholder="请选择地区"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>

          <a-col :span="24" :md="8">
            <a-form-item label="年份">
              <a-input-number
                v-model:value="filterForm.year"
                placeholder="请输入年份"
                style="width: 100%"
                :min="1970"
                :max="2025"
              />
            </a-form-item>
          </a-col>

          <a-col :span="24" :md="8">
            <a-form-item label="时长(分钟)">
              <a-input-number
                v-model:value="filterForm.duration"
                placeholder="请输入时长"
                style="width: 100%"
                :min="0"
              />
            </a-form-item>
          </a-col>

          <a-col :span="24" :md="8">
            <a-form-item label="价格(元)">
              <a-input-number
                v-model:value="filterForm.price"
                placeholder="请输入价格"
                style="width: 100%"
                :min="0"
                :precision="2"
              />
            </a-form-item>
          </a-col>

          <a-col :span="24" :md="8">
            <a-form-item label="主演">
              <a-input v-model:value="filterForm.actor" placeholder="请输入主演姓名" allowClear />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <div class="movies-section content-card">
      <div class="movie-list">
        <div v-for="movie in currentPageMovies" :key="movie.id" class="movie-card hover-scale">
          <div class="movie-cover-wrapper">
            <img :src="movie.coverBase64" alt="" class="movie-cover" />
            <div class="movie-rating">
              <a-tag color="#f50">{{ movie.rating }}分</a-tag>
              <a-tag v-if="!movie.isFree" color="#108ee9">¥{{ movie.price }}</a-tag>
            </div>
            <div class="movie-actions">
              <div class="movie-play-btn" @click="(e) => playMovie(movie, e)">
                <a-button type="primary" shape="circle" :loading="loading">
                  <template #icon><play-circle-outlined /></template>
                </a-button>
              </div>
            </div>
          </div>
          <div class="movie-info">
            <h3 class="movie-title text-ellipsis">{{ movie.title }}</h3>
            <p class="movie-meta text-ellipsis">{{ movie.duration }}分钟 | {{ movie.year }}</p>
            <p class="movie-actors text-ellipsis">{{ movie.actors || '暂无主演信息' }}</p>
            <p>{{ getRegionName(movie.region) }}</p>
            <div class="movie-footer">
              <div class="movie-stats">
                <span class="play-count">播放: {{ movie.playCount }}</span>
                <span class="price" :class="{ free: movie.isFree }">
                  {{ movie.isFree ? '免费' : `¥${movie.price}` }}
                </span>
              </div>
              <div class="movie-actions-bottom">
                <a-button
                  type="text"
                  :danger="collectionStatus[movie.id]"
                  @click="
                    (e) =>
                      collectionStatus[movie.id]
                        ? deleteMovie(movie.id)
                        : showCollectionModal(movie, e)
                  "
                >
                  <template #icon>
                    <heart-filled v-if="collectionStatus[movie.id]" />
                    <heart-outlined v-else />
                  </template>
                </a-button>
                <a-button
                  type="text"
                  :danger="interactionStatus[movie.id]?.isLiked"
                  @click="(e) => toggleLike(movie, e)"
                >
                  <template #icon>
                    <like-filled v-if="interactionStatus[movie.id]?.liked" />
                    <like-outlined v-else />
                  </template>
                </a-button>
                <a-button type="text" @click="(e) => showRatingModal(movie, e)">
                  <template #icon>
                    <star-filled v-if="interactionStatus[movie.id]?.rating" />
                    <star-outlined v-else />
                  </template>
                </a-button>
                <a-button type="text" @click="(e) => showCommentModal(movie, e)">
                  <template #icon>
                    <message-filled v-if="interactionStatus[movie.id]?.comment" />
                    <message-outlined v-else />
                  </template>
                </a-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 播放弹窗 -->
    <a-modal
      v-model:open="playModalVisible"
      title="预告片播放"
      width="800px"
      :footer="null"
      @cancel="currentMovie = null"
    >
      <div v-if="currentMovie" class="video-container">
        <iframe
          width="100%"
          height="400"
          :src="`https://www.youtube.com/embed/${currentMovie.trailerUrl}?autoplay=0&controls=1&rel=0`"
          frameborder="0"
          allow="accelerometer; encrypted-media; gyroscope; picture-in-picture"
          allowfullscreen
        >
        </iframe>
      </div>
    </a-modal>

    <!-- 收藏对话框 -->
    <a-modal
      v-model:open="collectionModalVisible"
      title="添加到收藏夹"
      @cancel="selectedMovie = null"
      @ok="confirmCollection"
      :okButtonProps="{ disabled: !selectedCollectionId }"
    >
      <div v-if="userCollections.length === 0" class="empty-collections">
        <p>您还没有创建任何收藏夹</p>
        <a-button type="primary" @click="router.push('/collection')"> 去创建收藏夹 </a-button>
      </div>
      <div v-else class="collections-list">
        <a-radio-group v-model:value="selectedCollectionId" class="collection-radio-group">
          <a-radio
            v-for="collection in userCollections"
            :key="collection.id"
            :value="collection.id"
            class="collection-radio-item"
          >
            {{ collection.name }}
          </a-radio>
        </a-radio-group>
      </div>
    </a-modal>

    <!-- 评分对话框 -->
    <a-modal
      v-model:open="ratingModalVisible"
      title="电影评分"
      @ok="submitRating"
      :okButtonProps="{ disabled: !currentRating }"
    >
      <div class="rating-content">
        <h3>{{ selectedMovie?.title }}</h3>
        <a-rate v-model:value="currentRating" />
      </div>
    </a-modal>

    <!-- 评论对话框 -->
    <a-modal
      v-model:open="commentModalVisible"
      title="电影评论"
      @ok="submitComment"
      :okButtonProps="{ disabled: !currentComment.trim() }"
    >
      <div class="comment-content">
        <h3>{{ selectedMovie?.title }}</h3>
        <a-textarea
          v-model:value="currentComment"
          :rows="4"
          placeholder="写下你的观后感..."
          :maxLength="1000"
          showCount
        />
      </div>
    </a-modal>
  </div>
</template>

<style scoped>
.filter-section {
  margin-bottom: 16px;
}

.movie-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 24px;
  margin-bottom: 24px;
}

.movie-card {
  cursor: pointer;
  transition: transform 0.2s;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.movie-cover-wrapper {
  position: relative;
  border-radius: 8px;
  overflow: hidden;
}

.movie-cover {
  width: 100%;
  aspect-ratio: 2/3;
  object-fit: cover;
}

.movie-rating {
  position: absolute;
  top: 8px;
  right: 8px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.movie-info {
  padding: 12px;
}

.movie-title {
  margin: 0 0 8px;
  font-size: 16px;
  font-weight: 500;
}

.movie-meta {
  font-size: 13px;
  color: var(--text-color-secondary);
  margin: 0 0 8px;
}

.movie-actors {
  font-size: 13px;
  color: var(--text-color-secondary);
  margin: 0 0 8px;
}

.movie-price {
  font-size: 16px;
  font-weight: bold;
  color: #ff4d4f;
  margin: 0;
}

.movie-price.free {
  color: #52c41a;
}

.movie-actions {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  display: flex;
  gap: 16px;
  opacity: 0;
  transition: opacity 0.3s;
  z-index: 2;
}

.movie-cover-wrapper:hover .movie-actions {
  opacity: 1;
}

.movie-play-btn {
  color: white;
  font-size: 32px;
  cursor: pointer;
  transition: transform 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.movie-play-btn .ant-btn {
  width: 48px;
  height: 48px;
  font-size: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.movie-play-btn:hover {
  transform: scale(1.2);
}

.movie-stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 0;
  font-size: 14px;
}

.play-count {
  color: var(--text-color-secondary);
}

.video-container {
  position: relative;
  width: 100%;
}

.video-container video {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: #000;
}

.pagination-section {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

@media (max-width: 768px) {
  .movie-list {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 16px;
  }
}

.empty-collections {
  text-align: center;
  padding: 24px 0;
}

.empty-collections p {
  margin-bottom: 16px;
  color: var(--text-color-secondary);
}

.collections-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.collection-item {
  text-align: left;
  height: 40px;
}

.movie-footer {
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px solid #f0f0f0;
}

.movie-actions-bottom {
  margin-top: 8px;
  display: flex;
  justify-content: space-between;
}

.movie-actions-bottom .ant-btn {
  padding: 0 8px;
  flex: 1;
}

.movie-actions-bottom .ant-btn:hover {
  background: transparent;
}

.movie-actions-bottom .anticon {
  margin-right: 4px;
}

.rating-content,
.comment-content {
  text-align: center;
}

.rating-content h3,
.comment-content h3 {
  margin-bottom: 16px;
}

.comment-content .ant-input {
  margin-top: 16px;
}
</style>
