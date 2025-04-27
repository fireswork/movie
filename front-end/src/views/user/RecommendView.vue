<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from '@/services/request'
import { message } from 'ant-design-vue'

const router = useRouter()
const userInfo = ref(JSON.parse(localStorage.getItem('user')))

// 筛选表单
const filterForm = reactive({
  categoryId: undefined,
  regionId: undefined,
  year: undefined,
  maxPrice: undefined
})

// 分类数据
const categories = ref([])
const regions = ref([])

// 获取分类和地区数据
const fetchOptions = async () => {
  try {
    const [categoriesRes, regionsRes] = await Promise.all([
      axios.get('/categories'),
      axios.get('/regions')
    ])
    categories.value = categoriesRes.data.map(item => ({
      label: item.name,
      value: item.id
    }))
    regions.value = regionsRes.data.map(item => ({
      label: item.name,
      value: item.id
    }))
  } catch (error) {
    message.error('获取筛选选项失败')
    console.error('获取筛选选项失败:', error)
  }
}

// 推荐电影数据
const recommendedMovies = ref([])
const loading = ref(false)

// 获取推荐电影
const handleRecommend = async () => {
  if (!userInfo.value) {
    message.error('请先登录')
    return
  }

  loading.value = true
  try {
    const { data } = await axios.post(`/recommend?userId=${userInfo.value.userId}`, filterForm)
    recommendedMovies.value = data
  } catch (error) {
    message.error('获取推荐失败')
    console.error('获取推荐失败:', error)
  } finally {
    loading.value = false
  }
}

const handleResetFilter = () => {
  filterForm.categoryId = undefined
  filterForm.regionId = undefined
  filterForm.year = undefined
  filterForm.maxPrice = undefined
}


onMounted(() => {
  fetchOptions()
  handleRecommend()
})
</script>

<template>
  <div class="recommend-container page-container">
    <div class="filter-section content-card">
      <h2>智能推荐</h2>
      <p class="filter-description">根据您的观影历史和偏好，选择以下条件获取个性化推荐</p>

      <a-form :model="filterForm" layout="vertical">
        <a-row :gutter="16">
          <a-col :span="24" :md="12">
            <a-form-item label="类型">
              <a-select
                v-model:value="filterForm.categoryId"
                :options="categories"
                placeholder="请选择类型"
                allowClear
                style="width: 100%"
              />
            </a-form-item>
          </a-col>

          <a-col :span="24" :md="12">
            <a-form-item label="地区">
              <a-select
                v-model:value="filterForm.regionId"
                :options="regions"
                placeholder="请选择地区"
                allowClear
                style="width: 100%"
              />
            </a-form-item>
          </a-col>

          <a-col :span="24" :md="12">
            <a-form-item label="年份">
              <a-input-number
                v-model:value="filterForm.year"
                :min="1970"
                :max="2025"
                placeholder="请选择年份"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>

          <a-col :span="24" :md="12">
            <a-form-item label="最高价格">
              <a-input-number
                v-model:value="filterForm.maxPrice"
                :min="0"
                :max="1000"
                placeholder="请输入最高价格"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col :span="24">
            <div class="filter-buttons">
              <a-button type="primary" @click="handleRecommend" :loading="loading">生成推荐</a-button>
              <a-button @click="handleResetFilter">重置</a-button>
            </div>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <div class="recommend-result content-card">
      <h2>为您推荐</h2>

      <a-empty v-if="recommendedMovies.length === 0" description="暂无推荐电影" />

      <div v-else class="movie-grid">
        <div
          v-for="movie in recommendedMovies"
          :key="movie.id"
          class="movie-card hover-scale"
        >
          <div class="movie-cover-wrapper">
            <img :src="movie.coverBase64" alt="" class="movie-cover" />
          </div>
          <div class="movie-info">
            <h3 class="movie-title text-ellipsis">{{ movie.title }}</h3>
            <p class="movie-meta text-ellipsis">
              {{ movie.categories }} | {{ movie.region }}
            </p>
            <p class="movie-meta text-ellipsis">{{ movie.year }} | {{ movie.duration }}分钟</p>
            <div class="movie-rating-price">
              <span class="movie-rating">{{ movie.rating }}分</span>
              <span class="movie-price price-tag">{{ movie.isFree ? '免费' : `¥${movie.price}` }}</span>
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
}
</style>
