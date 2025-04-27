<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { PlayCircleOutlined } from '@ant-design/icons-vue'
import request from '@/services/request'

const router = useRouter()

const userInfo = JSON.parse(localStorage.getItem('user'))

// 榜单类型
const rankingTypes = [
  { key: 'rating', name: '评分榜' },
  { key: 'popularity', name: '播放榜' },
]

const activeType = ref('rating')

// 榜单数据
const ratingRanking = ref([])
const popularityRanking = ref([])
const allMovies = ref([])
const regions = ref([])

// 获取地区数据
const fetchRegions = async () => {
  try {
    const { data } = await request.get('/regions')
    regions.value = data
  } catch (error) {
    console.error('获取地区列表失败:', error)
  }
}

// 根据地区ID获取地区名称
const getRegionName = (regionId) => {
  const region = regions.value.find(r => r.id === regionId)
  return region ? region.name : '未知地区'
}

// 格式化价格显示
const formatPrice = (movie) => {
  if (movie.isFree) {
    return '免费'
  }
  if (!movie.price && movie.price !== 0) {
    return '待定价'
  }
  return `¥${movie.price.toFixed(2)}`
}

// 获取所有电影数据
const fetchMovies = async () => {
  try {
    const { data } = await request.get('/movies')
    allMovies.value = data
    // 按播放量排序
    popularityRanking.value = [...data].sort((a, b) => b.playCount - a.playCount)
  } catch (error) {
    console.error('获取电影列表失败:', error)
  }
}

// 获取评分榜数据
const fetchRatingRanking = async () => {
  try {
    const [moviesRes, interactionsRes] = await Promise.all([
      request.get('/movies'),
      request.get(`/movie-interactions/user/${userInfo.userId}`)
    ])
    
    // 创建电影ID到电影信息的映射
    const movieMap = {}
    moviesRes.data.forEach(movie => {
      movieMap[movie.id] = movie
    })
    
    // 过滤出有评分的互动记录，并按评分排序
    const ratedMovies = interactionsRes.data
      .filter(interaction => interaction.rating > 0)
      .sort((a, b) => b.rating - a.rating)
      .map(interaction => ({
        ...movieMap[interaction.movieId],
        userRating: interaction.rating
      }))
      .filter(movie => movie.id) // 过滤掉不存在的电影
    
    ratingRanking.value = ratedMovies
  } catch (error) {
    console.error('获取评分榜数据失败:', error)
  }
}

const handleTypeChange = (type) => {
  activeType.value = type
}

onMounted(async () => {
  await Promise.all([
    fetchMovies(),
    fetchRatingRanking(),
    fetchRegions()
  ])
})
</script>

<template>
  <div class="ranking-container page-container">
    <div class="content-card">
      <div class="ranking-header">
        <a-radio-group v-model:value="activeType" button-style="solid">
          <a-radio-button
            v-for="type in rankingTypes"
            :key="type.key"
            :value="type.key"
            @click="() => handleTypeChange(type.key)"
          >
            {{ type.name }}
          </a-radio-button>
        </a-radio-group>
      </div>

      <div class="ranking-list">
        <!-- 评分榜 -->
        <template v-if="activeType === 'rating'">
          <div
            v-for="(movie, index) in ratingRanking"
            :key="movie.id"
            class="ranking-item"
          >
            <div class="ranking-number" :class="{ 'top-three': index < 3 }">{{ index + 1 }}</div>
            <div class="movie-cover-wrapper">
              <img :src="movie.coverBase64" alt="" class="movie-cover" />
            </div>
            <div class="movie-info">
              <h3 class="movie-title text-ellipsis">{{ movie.title }}</h3>
              <p class="movie-meta text-ellipsis">
                {{ getRegionName(movie.region) }} | {{ movie.year }}
              </p>
              <p class="movie-actors text-ellipsis">{{ movie.actors }}</p>
              <div class="movie-rating">
                <a-rate :value="movie.userRating" disabled allow-half :count="5" />
                <span class="rating-value">{{ movie.userRating }}分</span>
              </div>
            </div>
            <div class="movie-price" :class="{ free: movie.isFree }">{{ formatPrice(movie) }}</div>
          </div>
          <div v-if="ratingRanking.length === 0" class="empty-tip">暂无评分数据</div>
        </template>

        <!-- 播放量榜 -->
        <template v-else-if="activeType === 'popularity'">
          <div
            v-for="(movie, index) in popularityRanking"
            :key="movie.id"
            class="ranking-item"
          >
            <div class="ranking-number" :class="{ 'top-three': index < 3 }">{{ index + 1 }}</div>
            <div class="movie-cover-wrapper">
              <img :src="movie.coverBase64" alt="" class="movie-cover" />
            </div>
            <div class="movie-info">
              <h3 class="movie-title text-ellipsis">{{ movie.title }}</h3>
              <p class="movie-meta text-ellipsis">
                {{ getRegionName(movie.region) }} | {{ movie.year }}
              </p>
              <p class="movie-actors text-ellipsis">{{ movie.actors }}</p>
              <div class="movie-play-count">
                <play-circle-outlined />
                <span>{{ movie.playCount }}次播放</span>
              </div>
            </div>
            <div class="movie-price" :class="{ free: movie.isFree }">{{ formatPrice(movie) }}</div>
          </div>
          <div v-if="popularityRanking.length === 0" class="empty-tip">暂无播放数据</div>
        </template>
      </div>
    </div>
  </div>
</template>

<style scoped>
.ranking-header {
  margin-bottom: 24px;
}

.ranking-list {
  counter-reset: ranking;
}

.ranking-item {
  display: flex;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.3s;
}

.ranking-item:hover {
  background-color: #fafafa;
}

.ranking-item:last-child {
  border-bottom: none;
}

.ranking-number {
  width: 40px;
  font-size: 18px;
  font-weight: 600;
  text-align: center;
  color: var(--text-color-secondary);
}

.ranking-number.top-three {
  color: #ff4d4f;
}

.movie-cover-wrapper {
  width: 60px;
  height: 90px;
  margin-right: 16px;
  flex-shrink: 0;
}

.movie-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}

.movie-info {
  flex: 1;
}

.movie-title {
  margin: 0 0 8px;
  font-size: 16px;
  font-weight: 500;
}

.movie-meta,
.movie-actors {
  margin: 0 0 8px;
  font-size: 12px;
  color: var(--text-color-secondary);
}

.movie-rating,
.movie-play-count {
  display: flex;
  align-items: center;
  font-size: 12px;
}

.rating-value {
  margin-left: 8px;
  color: #fadb14;
  font-weight: 500;
}

.movie-play-count span {
  margin-left: 8px;
  color: var(--text-color-secondary);
}

.movie-price {
  margin-left: 16px;
  font-size: 16px;
  width: 80px;
  text-align: right;
  flex-shrink: 0;
  color: #ff4d4f;
}

.movie-price.free {
  color: #52c41a;
}

.empty-tip {
  text-align: center;
  padding: 32px;
  color: var(--text-color-secondary);
  font-size: 14px;
}

@media (max-width: 768px) {
  .ranking-item {
    flex-wrap: wrap;
  }

  .movie-cover-wrapper {
    order: 1;
  }

  .ranking-number {
    order: 0;
  }

  .movie-info {
    order: 2;
    width: calc(100% - 60px - 40px - 16px);
  }

  .movie-price {
    order: 3;
    margin-left: 56px;
    margin-top: 8px;
    width: auto;
    text-align: left;
  }
}
</style>
