<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { PlayCircleOutlined } from '@ant-design/icons-vue'
import request from '@/services/request'

const router = useRouter()
const categories = ref([{ key: 'all', name: '全部' }])
const activeCategory = ref('all')
const movies = ref([])

const pagination = reactive({
  current: 1,
  pageSize: 8,
  total: 0,
  onChange: (page, pageSize) => {
    pagination.current = page
    pagination.pageSize = pageSize
    fetchMovies()
  },
})

// 获取分类列表
const fetchCategories = async () => {
  try {
    const { data } = await request.get('/categories')
    categories.value = [
      { key: 'all', name: '全部' },
      ...data.map(item => ({
        key: item.id.toString(),
        name: item.name
      }))
    ]
  } catch (error) {
    console.error('获取分类列表失败:', error)
  }
}

// 获取电影列表
const fetchMovies = async () => {
  try {
    const { data } = await request.get('/movies')
    // 如果选择了特定分类，进行过滤
    if (activeCategory.value !== 'all') {
      movies.value = data.filter(movie => 
        movie.categories.includes(activeCategory.value)
      )
    } else {
      movies.value = data
    }
    pagination.total = movies.value.length
  } catch (error) {
    console.error('获取电影列表失败:', error)
  }
}

const handleCategoryChange = (key) => {
  activeCategory.value = key
  pagination.current = 1
  fetchMovies()
}

const goToMovie = (id) => {
  router.push(`/movie/${id}`)
}

onMounted(() => {
  fetchCategories()
  fetchMovies()
})
</script>

<template>
  <div class="preview-container page-container">
    <div class="content-card">
      <div class="category-tabs">
        <a-radio-group v-model:value="activeCategory" button-style="solid">
          <a-radio-button
            v-for="category in categories"
            :key="category.key"
            :value="category.key"
            @click="() => handleCategoryChange(category.key)"
          >
            {{ category.name }}
          </a-radio-button>
        </a-radio-group>
      </div>

      <div class="preview-list">
        <div v-for="movie in movies" :key="movie.id" class="preview-card hover-scale">
          <div class="preview-cover-wrapper" @click="goToMovie(movie.id)">
            <img :src="movie.coverBase64" alt="" class="preview-cover" />
            <div class="preview-price" :class="{ 'free': movie.isFree }">
              {{ movie.isFree ? '免费' : `¥${movie.price}` }}
            </div>
            <div class="preview-play-button">
              <play-circle-outlined />
            </div>
          </div>
          <div class="preview-info">
            <h3 class="preview-title text-ellipsis" @click="goToMovie(movie.id)">
              {{ movie.title }}
            </h3>
            <div class="preview-meta">
              <span>评分：{{ movie.rating }}分</span>
              <span>时长：{{ movie.duration }}分钟</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.preview-container {
  padding: 16px;
}

.category-tabs {
  margin-bottom: 24px;
}

.preview-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
  margin-bottom: 24px;
}

.preview-card {
  border-radius: 8px;
  overflow: hidden;
  transition: transform 0.3s;
  background: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.preview-cover-wrapper {
  position: relative;
  cursor: pointer;
}

.preview-cover {
  width: 100%;
  aspect-ratio: 16/9;
  object-fit: cover;
  display: block;
}

.preview-price {
  position: absolute;
  bottom: 8px;
  right: 8px;
  background-color: rgba(0, 0, 0, 0.7);
  color: #ff4d4f;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: bold;
}

.preview-price.free {
  color: #52c41a;
}

.preview-play-button {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: white;
  font-size: 48px;
  opacity: 0;
  transition: opacity 0.3s;
}

.preview-cover-wrapper:hover .preview-play-button {
  opacity: 1;
}

.preview-info {
  padding: 12px;
}

.preview-title {
  margin: 0 0 8px;
  font-size: 16px;
  cursor: pointer;
}

.preview-title:hover {
  color: var(--primary-color);
}

.preview-meta {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: var(--text-color-secondary);
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

@media (max-width: 768px) {
  .preview-list {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 16px;
  }

  .category-tabs {
    overflow-x: auto;
    white-space: nowrap;
    -webkit-overflow-scrolling: touch;
    margin-bottom: 16px;
  }
}
</style>
