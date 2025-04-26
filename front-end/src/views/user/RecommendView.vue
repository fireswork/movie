<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 筛选表单
const filterForm = reactive({
  categories: [],
  region: undefined,
  yearRange: [1970, 2023],
  duration: undefined,
  priceRange: [0, 100],
})

// 模拟分类数据
const categories = ref([
  { label: '动作', value: 'action' },
  { label: '喜剧', value: 'comedy' },
  { label: '爱情', value: 'romance' },
  { label: '科幻', value: 'scifi' },
  { label: '恐怖', value: 'horror' },
  { label: '动画', value: 'animation' },
  { label: '剧情', value: 'drama' },
  { label: '战争', value: 'war' },
])

// 模拟地区数据
const regions = ref([
  { label: '全部', value: '' },
  { label: '中国大陆', value: 'china' },
  { label: '中国香港', value: 'hongkong' },
  { label: '中国台湾', value: 'taiwan' },
  { label: '美国', value: 'usa' },
  { label: '韩国', value: 'korea' },
  { label: '日本', value: 'japan' },
  { label: '法国', value: 'france' },
  { label: '英国', value: 'uk' },
])

// 时长选项
const durations = ref([
  { label: '全部', value: '' },
  { label: '90分钟以下', value: '<90' },
  { label: '90-120分钟', value: '90-120' },
  { label: '120分钟以上', value: '>120' },
])

// 模拟推荐电影数据
const recommendedMovies = ref([
  {
    id: 1,
    title: '肖申克的救赎',
    cover: 'https://via.placeholder.com/300x450?text=肖申克的救赎',
    rating: 9.7,
    price: 15,
    matchScore: 98,
    categories: ['剧情', '犯罪'],
    region: '美国',
    year: 1994,
    duration: 142,
  },
  {
    id: 2,
    title: '霸王别姬',
    cover: 'https://via.placeholder.com/300x450?text=霸王别姬',
    rating: 9.6,
    price: 12,
    matchScore: 96,
    categories: ['剧情', '爱情'],
    region: '中国香港',
    year: 1993,
    duration: 171,
  },
  {
    id: 3,
    title: '阿甘正传',
    cover: 'https://via.placeholder.com/300x450?text=阿甘正传',
    rating: 9.5,
    price: 14,
    matchScore: 95,
    categories: ['剧情', '爱情'],
    region: '美国',
    year: 1994,
    duration: 142,
  },
])

// 生成更多模拟数据
for (let i = 4; i <= 20; i++) {
  const randomCategory = categories.value[Math.floor(Math.random() * categories.value.length)].label
  const randomRegion = regions.value[Math.floor(Math.random() * regions.value.length)].label
  const randomYear = Math.floor(Math.random() * (2023 - 1970)) + 1970
  const randomDuration = Math.floor(Math.random() * 100) + 90
  const randomPrice = Math.floor(Math.random() * 20) + 5
  const randomRating = (Math.random() * 2 + 7).toFixed(1)
  const randomMatchScore = Math.floor(Math.random() * 30) + 65

  recommendedMovies.value.push({
    id: i,
    title: `电影${i}`,
    cover: `https://via.placeholder.com/300x450?text=电影${i}`,
    rating: randomRating,
    price: randomPrice,
    matchScore: randomMatchScore,
    categories: [randomCategory],
    region: randomRegion,
    year: randomYear,
    duration: randomDuration,
  })
}

const loading = ref(false)

// 方法
const handleRecommend = () => {
  loading.value = true

  // 模拟推荐请求
  setTimeout(() => {
    loading.value = false
    // 在实际应用中，这里会根据筛选条件获取推荐结果
  }, 1000)
}

const handleResetFilter = () => {
  filterForm.categories = []
  filterForm.region = undefined
  filterForm.yearRange = [1970, 2023]
  filterForm.duration = undefined
  filterForm.priceRange = [0, 100]
}

const goToMovie = (id) => {
  router.push(`/movie/${id}`)
}

onMounted(() => {
  // 可以在这里加载初始推荐数据
})
</script>

<template>
  <div class="recommend-container page-container">
    <div class="filter-section content-card">
      <h2>智能推荐</h2>
      <p class="filter-description">根据您的观影历史和偏好，选择以下条件获取个性化推荐</p>

      <a-form :model="filterForm" layout="vertical">
        <a-row :gutter="16">
          <a-col :span="24" :md="8">
            <a-form-item label="类型偏好">
              <a-select
                v-model:value="filterForm.categories"
                mode="multiple"
                :options="categories"
                placeholder="请选择喜欢的类型"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>

          <a-col :span="24" :md="8">
            <a-form-item label="地区偏好">
              <a-select
                v-model:value="filterForm.region"
                :options="regions"
                placeholder="请选择喜欢的地区"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>

          <a-col :span="24" :md="8">
            <a-form-item label="电影时长">
              <a-select
                v-model:value="filterForm.duration"
                :options="durations"
                placeholder="请选择时长"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>

          <a-col :span="24" :md="12">
            <a-form-item label="年份范围">
              <a-slider v-model:value="filterForm.yearRange" range :min="1970" :max="2023" />
            </a-form-item>
          </a-col>

          <a-col :span="24" :md="12">
            <a-form-item label="价格范围(元)">
              <a-slider v-model:value="filterForm.priceRange" range :min="0" :max="100" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col :span="24">
            <div class="filter-buttons">
              <a-button type="primary" @click="handleRecommend" :loading="loading"
                >生成推荐</a-button
              >
              <a-button @click="handleResetFilter">重置</a-button>
            </div>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <div class="recommend-result content-card">
      <h2>为您推荐</h2>

      <div class="movie-grid">
        <div
          v-for="movie in recommendedMovies"
          :key="movie.id"
          class="movie-card hover-scale"
          @click="() => goToMovie(movie.id)"
        >
          <div class="movie-cover-wrapper">
            <img :src="movie.cover" alt="" class="movie-cover" />
            <div class="movie-match-score">
              <a-progress
                type="circle"
                :percent="movie.matchScore"
                :width="40"
                :strokeColor="
                  movie.matchScore >= 90
                    ? '#52c41a'
                    : movie.matchScore >= 80
                      ? '#1677ff'
                      : '#faad14'
                "
                :format="(percent) => `${percent}%`"
              />
            </div>
          </div>
          <div class="movie-info">
            <h3 class="movie-title text-ellipsis">{{ movie.title }}</h3>
            <p class="movie-meta text-ellipsis">
              {{ movie.categories.join('/') }} | {{ movie.region }}
            </p>
            <p class="movie-meta text-ellipsis">{{ movie.year }} | {{ movie.duration }}分钟</p>
            <div class="movie-rating-price">
              <span class="movie-rating">{{ movie.rating }}分</span>
              <span class="movie-price price-tag">¥{{ movie.price }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.recommend-container {
  padding: 16px;
}

.filter-section,
.recommend-result {
  margin-bottom: 24px;
}

.filter-section h2,
.recommend-result h2 {
  margin-top: 0;
  margin-bottom: 16px;
}

.filter-description {
  margin-bottom: 24px;
  color: var(--text-color-secondary);
}

.filter-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.movie-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 24px;
}

.movie-card {
  cursor: pointer;
  transition: transform 0.2s;
  border-radius: 8px;
  overflow: hidden;
  background-color: #fff;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.movie-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.movie-cover-wrapper {
  position: relative;
}

.movie-cover {
  width: 100%;
  aspect-ratio: 2/3;
  object-fit: cover;
  display: block;
}

.movie-match-score {
  position: absolute;
  top: 8px;
  right: 8px;
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  padding: 2px;
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
  margin: 0 0 4px;
  font-size: 12px;
  color: var(--text-color-secondary);
}

.movie-rating-price {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}

.movie-rating {
  color: #faad14;
  font-weight: 500;
}

@media (max-width: 768px) {
  .movie-grid {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
    gap: 16px;
  }

  .movie-title {
    font-size: 14px;
  }

  .movie-meta {
    font-size: 10px;
  }

  .movie-match-score {
    transform: scale(0.8);
    transform-origin: top right;
  }
}
</style>
