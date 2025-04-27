<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { DeleteOutlined, PlusOutlined } from '@ant-design/icons-vue'
import request from '@/services/request'

const userStore = JSON.parse(localStorage.getItem('user'))

const router = useRouter()

// 收藏夹列表
const collections = ref([])
// 当前选中的收藏夹
const currentCollection = ref(null)
// 当前收藏夹的电影列表
const collectionMovies = ref([])

// 新建收藏夹对话框
const createModalVisible = ref(false)
const newCollectionName = ref('')

// 获取用户的收藏夹列表
const fetchCollections = async () => {
  try {
    const { data } = await request.get('/collections', {
      params: {
        userId: userStore.userId
      },
    })
    collections.value = data
    if (data.length > 0 && !currentCollection.value) {
      currentCollection.value = data[0]
      fetchCollectionMovies(data[0].id)
    }
  } catch (error) {
    console.error('获取收藏夹列表失败:', error)
  }
}

// 获取收藏夹中的电影
const fetchCollectionMovies = async (collectionId) => {
  try {
    const { data } = await request.get(`/collections/${collectionId}/movies`)
    collectionMovies.value = data
  } catch (error) {
    console.error('获取收藏夹电影列表失败:', error)
  }
}

// 创建新收藏夹
const createCollection = async () => {
  if (!newCollectionName.value.trim()) {
    return
  }
  try {
    await request.post('/collections', {
      userId: userStore.userId,
      name: newCollectionName.value,
      movieIds: ''  // 新建收藏夹时默认为空
    })
    createModalVisible.value = false
    newCollectionName.value = ''
    await fetchCollections()
  } catch (error) {
    console.error('创建收藏夹失败:', error)
  }
}

// 删除收藏夹
const deleteCollection = async (id) => {
  if (!confirm('确定要删除这个收藏夹吗？')) {
    return
  }
  try {
    await request.delete(`/collections/${id}`)
    if (currentCollection.value?.id === id) {
      currentCollection.value = null
      collectionMovies.value = []
    }
    await fetchCollections()
  } catch (error) {
    console.error('删除收藏夹失败:', error)
  }
}

// 从收藏夹移除电影
const removeFromCollection = async (movieId) => {
  if (!currentCollection.value) return
  try {
    await request.delete(`/collections/${currentCollection.value.id}/movies/${movieId}`)
    await fetchCollectionMovies(currentCollection.value.id)
  } catch (error) {
    console.error('从收藏夹移除电影失败:', error)
  }
}

// 切换收藏夹
const switchCollection = (collection) => {
  currentCollection.value = collection
  fetchCollectionMovies(collection.id)
}

// 跳转到电影详情
const goToMovie = (id) => {
  router.push(`/movie/${id}`)
}

onMounted(() => {
  fetchCollections()
})
</script>

<template>
  <div class="collection-container page-container">
    <div class="collection-content content-card">
      <div class="content-header">
        <div class="collections-header">
          <h2>我的收藏夹</h2>
          <a-button type="primary" @click="createModalVisible = true">
            <template #icon><plus-outlined /></template>
            新建收藏夹
          </a-button>
        </div>

        <!-- 收藏夹列表 -->
        <div class="collections-list">
          <a-radio-group v-model:value="currentCollection" button-style="solid">
            <a-radio-button
              v-for="collection in collections"
              :key="collection.id"
              :value="collection"
              @click="switchCollection(collection)"
            >
              {{ collection.name }}
              <delete-outlined class="delete-icon" @click.stop="deleteCollection(collection.id)" />
            </a-radio-button>
          </a-radio-group>
        </div>
      </div>

      <!-- 电影列表 -->
      <div v-if="currentCollection && collectionMovies.length > 0" class="movie-grid">
        <div
          v-for="movie in collectionMovies"
          :key="movie.id"
          class="movie-card hover-scale"
          @click="goToMovie(movie.id)"
        >
          <div class="movie-cover-wrapper">
            <img :src="movie.coverBase64" alt="" class="movie-cover" />
            <div class="movie-actions">
              <a-button
                type="primary"
                danger
                shape="circle"
                @click.stop="removeFromCollection(movie.id)"
              >
                <template #icon><delete-outlined /></template>
              </a-button>
            </div>
            <div class="movie-rating">
              <a-tag color="#f50">{{ movie.rating }}分</a-tag>
            </div>
          </div>
          <div class="movie-info">
            <h3 class="movie-title text-ellipsis">{{ movie.title }}</h3>
            <p class="movie-meta text-ellipsis">{{ movie.duration }}分钟 | {{ movie.year }}</p>
            <p class="movie-actors text-ellipsis">{{ movie.actors || '暂无主演信息' }}</p>
            <p class="movie-stats">
              <span class="play-count">播放: {{ movie.playCount }}</span>
              <span class="price" :class="{ free: movie.isFree }">
                {{ movie.isFree ? '免费' : `¥${movie.price}` }}
              </span>
            </p>
          </div>
        </div>
      </div>

      <div v-else-if="currentCollection" class="empty-collection">
        <a-empty description="当前收藏夹暂无电影" />
      </div>

      <div v-else class="empty-collection">
        <a-empty description="请选择或创建一个收藏夹" />
      </div>
    </div>

    <!-- 创建收藏夹对话框 -->
    <a-modal v-model:open="createModalVisible" title="新建收藏夹" @ok="createCollection">
      <a-input
        v-model:value="newCollectionName"
        placeholder="请输入收藏夹名称"
        @pressEnter="createCollection"
      />
    </a-modal>
  </div>
</template>

<style scoped>
.collection-container {
  padding: 16px;
}

.collection-content {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
}

.content-header {
  margin-bottom: 24px;
}

.collections-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.collections-header h2 {
  margin: 0;
  font-size: 20px;
}

.collections-list {
  margin-bottom: 24px;
}

.delete-icon {
  margin-left: 8px;
  font-size: 12px;
  opacity: 0.7;
}

.delete-icon:hover {
  opacity: 1;
  color: #ff4d4f;
}

.movie-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 24px;
}

.movie-card {
  cursor: pointer;
  transition:
    transform 0.2s,
    box-shadow 0.3s;
  border-radius: 8px;
  overflow: hidden;
  background: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.movie-cover-wrapper {
  position: relative;
  overflow: hidden;
}

.movie-cover {
  width: 100%;
  aspect-ratio: 2/3;
  object-fit: cover;
}

.movie-actions {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  opacity: 0;
  transition: opacity 0.3s;
}

.movie-cover-wrapper:hover .movie-actions {
  opacity: 1;
}

.movie-rating {
  position: absolute;
  top: 8px;
  right: 8px;
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

.price {
  font-weight: bold;
  color: #ff4d4f;
}

.price.free {
  color: #52c41a;
}

.empty-collection {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400px;
}

@media (max-width: 768px) {
  .movie-grid {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
    gap: 16px;
  }
}
</style>
