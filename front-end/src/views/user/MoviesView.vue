<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/services/request'

const router = useRouter()

// 筛选表单
const filterForm = reactive({
  categories: [],
  region: undefined,
  yearRange: [1970, new Date().getFullYear()],
  actor: '',
  durationRange: [0, 300],
  priceRange: [0, 100],
})

// 分类和地区选项
const categories = ref([])
const regions = ref([])

// 电影列表
const movies = ref([])
const allMovies = ref([]) // 用于存储所有电影，便于筛选

// 排序选项
const sortOptions = [
  { label: '热度排序', value: 'popularity' },
  { label: '评分排序', value: 'rating' },
  { label: '时长排序', value: 'duration' },
  { label: '价格升序', value: 'price-asc' },
  { label: '价格降序', value: 'price-desc' },
]
const currentSort = ref('popularity')

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 12,
  total: 0,
  showSizeChanger: true,
  pageSizeOptions: ['12', '24', '48', '96'],
})

// 获取分类列表
const fetchCategories = async () => {
  try {
    const { data } = await request.get('/categories')
    categories.value = data.map(item => ({
      label: item.name,
      value: item.id.toString()
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
      ...data.map(item => ({
        label: item.name,
        value: item.id.toString()
      }))
    ]
  } catch (error) {
    console.error('获取地区列表失败:', error)
  }
}

// 获取电影列表
const fetchMovies = async () => {
  try {
    const { data } = await request.get('/movies')
    allMovies.value = data
    filterMovies()
  } catch (error) {
    console.error('获取电影列表失败:', error)
  }
}

// 筛选电影
const filterMovies = () => {
  let filteredMovies = [...allMovies.value]

  // 按分类筛选
  if (filterForm.categories.length > 0) {
    filteredMovies = filteredMovies.filter(movie => 
      filterForm.categories.some(cat => movie.categories.includes(cat))
    )
  }

  // 按地区筛选
  if (filterForm.region) {
    filteredMovies = filteredMovies.filter(movie => 
      movie.region === filterForm.region
    )
  }

  // 按年份范围筛选
  filteredMovies = filteredMovies.filter(movie => 
    movie.year >= filterForm.yearRange[0] && movie.year <= filterForm.yearRange[1]
  )

  // 按时长范围筛选
  filteredMovies = filteredMovies.filter(movie => 
    movie.duration >= filterForm.durationRange[0] && movie.duration <= filterForm.durationRange[1]
  )

  // 按价格范围筛选
  filteredMovies = filteredMovies.filter(movie => {
    const price = movie.isFree ? 0 : movie.price
    return price >= filterForm.priceRange[0] && price <= filterForm.priceRange[1]
  })

  // 按排序方式排序
  switch (currentSort.value) {
    case 'rating':
      filteredMovies.sort((a, b) => b.rating - a.rating)
      break
    case 'duration':
      filteredMovies.sort((a, b) => b.duration - a.duration)
      break
    case 'price-asc':
      filteredMovies.sort((a, b) => (a.isFree ? 0 : a.price) - (b.isFree ? 0 : b.price))
      break
    case 'price-desc':
      filteredMovies.sort((a, b) => (b.isFree ? 0 : b.price) - (a.isFree ? 0 : a.price))
      break
    default:
      // 默认按ID倒序（最新）排序
      filteredMovies.sort((a, b) => b.id - a.id)
  }

  movies.value = filteredMovies
  pagination.total = filteredMovies.length
}

const handlePageChange = (page, pageSize) => {
  pagination.current = page
  pagination.pageSize = pageSize
}

const handleSortChange = (value) => {
  currentSort.value = value
  filterMovies()
}

const handleFilter = () => {
  filterMovies()
}

const handleResetFilter = () => {
  filterForm.categories = []
  filterForm.region = undefined
  filterForm.yearRange = [1970, new Date().getFullYear()]
  filterForm.actor = ''
  filterForm.durationRange = [0, 300]
  filterForm.priceRange = [0, 100]
  filterMovies()
}

const goToMovie = (id) => {
  router.push(`/movie/${id}`)
}

onMounted(() => {
  fetchCategories()
  fetchRegions()
  fetchMovies()
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
            <a-form-item label="年份范围">
              <a-slider v-model:value="filterForm.yearRange" range :min="1970" :max="2024" />
            </a-form-item>
          </a-col>

          <a-col :span="24" :md="8">
            <a-form-item label="主演">
              <a-input v-model:value="filterForm.actor" placeholder="请输入演员名称" />
            </a-form-item>
          </a-col>

          <a-col :span="24" :md="8">
            <a-form-item label="时长范围(分钟)">
              <a-slider v-model:value="filterForm.durationRange" range :min="0" :max="300" />
            </a-form-item>
          </a-col>

          <a-col :span="24" :md="8">
            <a-form-item label="价格范围(元)">
              <a-slider v-model:value="filterForm.priceRange" range :min="0" :max="100" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col :span="24">
            <div class="filter-buttons">
              <a-button type="primary" @click="handleFilter">筛选</a-button>
              <a-button @click="handleResetFilter">重置</a-button>
            </div>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <div class="movies-section content-card">
      <div class="sort-section">
        <a-select
          v-model:value="currentSort"
          :options="sortOptions"
          @change="handleSortChange"
          style="width: 160px"
        />
      </div>

      <div class="movie-list">
        <div
          v-for="movie in movies"
          :key="movie.id"
          class="movie-card hover-scale"
          @click="() => goToMovie(movie.id)"
        >
          <div class="movie-cover-wrapper">
            <img :src="movie.coverBase64" alt="" class="movie-cover" />
            <div class="movie-rating">
              <a-tag color="#f50">{{ movie.rating }}分</a-tag>
            </div>
          </div>
          <div class="movie-info">
            <h3 class="movie-title text-ellipsis">{{ movie.title }}</h3>
            <p class="movie-meta text-ellipsis">
              {{ movie.duration }}分钟 | {{ movie.year }}
            </p>
            <p class="movie-price" :class="{ 'free': movie.isFree }">
              {{ movie.isFree ? '免费' : `¥${movie.price}` }}
            </p>
          </div>
        </div>
      </div>

      <div class="pagination-section">
        <a-pagination
          v-model:current="pagination.current"
          v-model:pageSize="pagination.pageSize"
          :total="pagination.total"
          :showSizeChanger="pagination.showSizeChanger"
          :pageSizeOptions="pagination.pageSizeOptions"
          @change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.filter-section {
  margin-bottom: 16px;
}

.filter-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.sort-section {
  margin-bottom: 16px;
  display: flex;
  justify-content: flex-end;
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

.movie-price {
  font-size: 16px;
  font-weight: bold;
  color: #ff4d4f;
  margin: 0;
}

.movie-price.free {
  color: #52c41a;
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
</style>
