<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from '@/services/request'

const carousels = ref([])
const router = useRouter()

const topRatedMovies = ref([])
const freeMovies = ref([])
const latestMovies = ref([])

// 获取轮播图
const fetchCarousels = async () => {
  try {
    const response = await axios.get('/carousels')
    carousels.value = response.data
  } catch (error) {
    console.error('获取轮播图失败:', error)
  }
}

// 获取所有电影数据并处理
const fetchAllMovies = async () => {
  try {
    const { data } = await axios.get('/movies')
    
    // 高分榜：按评分排序，取前5个
    topRatedMovies.value = [...data]
      .sort((a, b) => b.rating - a.rating)
      .slice(0, 5)
    
    // 特价区：筛选免费电影
    freeMovies.value = data.filter(movie => movie.isFree)
    
    // 最新发布：按ID倒序排序，取前4个
    latestMovies.value = [...data]
      .sort((a, b) => b.id - a.id)
      .slice(0, 4)
  } catch (error) {
    console.error('获取电影数据失败:', error)
  }
}

const goToMovie = (id) => {
  router.push(`/movie/${id}`)
}

onMounted(() => {
  fetchCarousels()
  fetchAllMovies()
})
</script>

<template>
  <div class="home-container page-container">
    <!-- 轮播图 -->
    <div class="carousel-section content-card">
      <a-carousel autoplay>
        <div v-for="item in carousels" :key="item.id" class="carousel-item">
          <div class="carousel-content">
            <img :src="item.imageBase64" :alt="item.title" class="carousel-image" />
            <div class="carousel-title">{{ item.title }}</div>
          </div>
        </div>
      </a-carousel>
    </div>

    <!-- 高分榜 -->
    <div class="section content-card">
      <div class="section-header">
        <h2 class="section-title">高分榜</h2>
      </div>
      <div class="movie-grid">
        <div
          v-for="movie in topRatedMovies"
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
            <p class="movie-price" :class="{ 'free': movie.isFree }">
              {{ movie.isFree ? '免费' : `¥${movie.price}` }}
            </p>
          </div>
        </div>
      </div>
    </div>

    <!-- 免费专区 -->
    <div class="section content-card">
      <div class="section-header">
        <h2 class="section-title">特价专区</h2>
      </div>
      <div class="movie-grid">
        <div
          v-for="movie in freeMovies"
          :key="movie.id"
          class="movie-card hover-scale"
          @click="() => goToMovie(movie.id)"
        >
          <div class="movie-cover-wrapper">
            <img :src="movie.coverBase64" alt="" class="movie-cover" />
            <div class="movie-discount">
              <a-tag color="#87d068">免费</a-tag>
            </div>
          </div>
          <div class="movie-info">
            <h3 class="movie-title text-ellipsis">{{ movie.title }}</h3>
            <p class="movie-price free">免费</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 最新发布 -->
    <div class="section content-card">
      <div class="section-header">
        <h2 class="section-title">最新发布</h2>
      </div>
      <div class="movie-grid">
        <div
          v-for="movie in latestMovies"
          :key="movie.id"
          class="movie-card hover-scale"
          @click="() => goToMovie(movie.id)"
        >
          <div class="movie-cover-wrapper">
            <img :src="movie.coverBase64" alt="" class="movie-cover" />
          </div>
          <div class="movie-info">
            <h3 class="movie-title text-ellipsis">{{ movie.title }}</h3>
            <p class="movie-price" :class="{ 'free': movie.isFree }">
              {{ movie.isFree ? '免费' : `¥${movie.price}` }}
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.home-container {
  padding: 16px;
}

.section {
  margin-bottom: 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.carousel-section {
  margin-bottom: 24px;
}

.carousel-item {
  height: 400px;
  line-height: 400px;
  text-align: center;
  cursor: pointer;
}

.carousel-content {
  position: relative;
  width: 100%;
  height: 100%;
}

.carousel-title {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 16px;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
  color: white;
  font-size: 18px;
  text-align: left;
}

.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.movie-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 16px;
}

.movie-card {
  cursor: pointer;
  transition: transform 0.2s;
  overflow: hidden;
}

.movie-cover-wrapper {
  position: relative;
  overflow: hidden;
  border-radius: 8px;
}

.movie-cover {
  width: 100%;
  aspect-ratio: 2/3;
  object-fit: cover;
  transition: transform 0.3s;
}

.movie-rating,
.movie-discount {
  position: absolute;
  top: 8px;
  right: 8px;
}

.movie-info {
  padding: 8px 0;
}

.movie-title {
  margin: 8px 0 4px;
  font-size: 16px;
  font-weight: 500;
}

.movie-price {
  margin: 4px 0 0;
  font-weight: bold;
  font-size: 16px;
  color: #ff4d4f;  /* 付费电影显示红色 */
}

.movie-price.free {
  color: #52c41a;  /* 免费电影显示绿色 */
}

@media (max-width: 576px) {
  .movie-grid {
    grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
    gap: 12px;
  }

  .carousel-item {
    height: 200px;
    line-height: 200px;
  }
}
</style>
