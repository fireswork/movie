<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 筛选表单
const filterForm = reactive({
  categories: [],
  region: undefined,
  yearRange: [1970, 2023],
  actor: '',
  durationRange: [0, 300],
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

// 模拟电影列表数据
const movies = ref([
  {
    id: 1,
    title: '肖申克的救赎',
    cover: 'https://via.placeholder.com/300x450?text=肖申克的救赎',
    rating: 9.7,
    price: 15,
    actors: ['蒂姆·罗宾斯', '摩根·弗里曼'],
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
    actors: ['张国荣', '巩俐'],
    categories: ['剧情', '爱情'],
    region: '中国香港',
    year: 1993,
    duration: 171,
  },
  // 添加更多电影...
])

for (let i = 3; i <= 20; i++) {
  const randomCategory = categories.value[Math.floor(Math.random() * categories.value.length)].label
  const randomRegion = regions.value[Math.floor(Math.random() * regions.value.length)].label
  const randomYear = Math.floor(Math.random() * (2023 - 1970)) + 1970
  const randomDuration = Math.floor(Math.random() * 100) + 90
  const randomPrice = Math.floor(Math.random() * 20) + 5
  const randomRating = (Math.random() * 2 + 7).toFixed(1)

  movies.value.push({
    id: i,
    title: `电影${i}`,
    cover: `https://via.placeholder.com/300x450?text=电影${i}`,
    rating: randomRating,
    price: randomPrice,
    actors: ['演员A', '演员B'],
    categories: [randomCategory],
    region: randomRegion,
    year: randomYear,
    duration: randomDuration,
  })
}

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
  total: movies.value.length,
  showSizeChanger: true,
  pageSizeOptions: ['12', '24', '48', '96'],
})

const handlePageChange = (page, pageSize) => {
  pagination.current = page
  pagination.pageSize = pageSize
}

const handleSortChange = (value) => {
  currentSort.value = value
  // 在这里实现排序逻辑
}

const handleResetFilter = () => {
  // 重置筛选条件
  filterForm.categories = []
  filterForm.region = undefined
  filterForm.yearRange = [1970, 2023]
  filterForm.actor = ''
  filterForm.durationRange = [0, 300]
  filterForm.priceRange = [0, 100]
}

const goToMovie = (id) => {
  router.push(`/movie/${id}`)
}

onMounted(() => {
  // 可以在这里加载电影列表数据
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
              <a-slider v-model:value="filterForm.yearRange" range :min="1970" :max="2023" />
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
              <a-button type="primary">筛选</a-button>
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
            <img :src="movie.cover" alt="" class="movie-cover" />
            <div class="movie-rating">
              <a-tag color="#f50">{{ movie.rating }}分</a-tag>
            </div>
          </div>
          <div class="movie-info">
            <h3 class="movie-title text-ellipsis">{{ movie.title }}</h3>
            <p class="movie-meta text-ellipsis">
              {{ movie.categories.join('/') }} | {{ movie.region }} | {{ movie.year }}
            </p>
            <p class="movie-actors text-ellipsis">{{ movie.actors.join(' / ') }}</p>
            <p class="movie-price price-tag">¥{{ movie.price }}</p>
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
  padding: 8px 4px;
}

.movie-title {
  margin: 8px 0 4px;
  font-size: 16px;
  font-weight: 500;
}

.movie-meta,
.movie-actors {
  font-size: 12px;
  color: var(--text-color-secondary);
  margin: 0 0 4px;
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
