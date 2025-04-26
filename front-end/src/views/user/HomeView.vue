<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from '@/services/request'

const carousels = ref([])
const router = useRouter()

const topRatedMovies = ref([
  {
    id: 1,
    title: '肖申克的救赎',
    cover: 'https://via.placeholder.com/300x450?text=肖申克的救赎',
    rating: 9.7,
    price: 15,
    actors: ['蒂姆·罗宾斯', '摩根·弗里曼'],
  },
  {
    id: 2,
    title: '霸王别姬',
    cover: 'https://via.placeholder.com/300x450?text=霸王别姬',
    rating: 9.6,
    price: 12,
    actors: ['张国荣', '巩俐'],
  },
  {
    id: 3,
    title: '阿甘正传',
    cover: 'https://via.placeholder.com/300x450?text=阿甘正传',
    rating: 9.5,
    price: 14,
    actors: ['汤姆·汉克斯', '罗宾·怀特'],
  },
  {
    id: 4,
    title: '泰坦尼克号',
    cover: 'https://via.placeholder.com/300x450?text=泰坦尼克号',
    rating: 9.4,
    price: 18,
    actors: ['莱昂纳多·迪卡普里奥', '凯特·温斯莱特'],
  },
  {
    id: 5,
    title: '这个杀手不太冷',
    cover: 'https://via.placeholder.com/300x450?text=这个杀手不太冷',
    rating: 9.4,
    price: 10,
    actors: ['让·雷诺', '娜塔莉·波特曼'],
  },
])

const specialOffers = ref([
  {
    id: 6,
    title: '当幸福来敲门',
    cover: 'https://via.placeholder.com/300x450?text=当幸福来敲门',
    originalPrice: 20,
    price: 5,
    discount: 7.5,
  },
  {
    id: 7,
    title: '楚门的世界',
    cover: 'https://via.placeholder.com/300x450?text=楚门的世界',
    originalPrice: 18,
    price: 4.5,
    discount: 7.5,
  },
  {
    id: 8,
    title: '星际穿越',
    cover: 'https://via.placeholder.com/300x450?text=星际穿越',
    originalPrice: 22,
    price: 6,
    discount: 7.3,
  },
])

const latestMovies = ref([
  {
    id: 9,
    title: '电影9',
    cover: 'https://via.placeholder.com/300x450?text=电影9',
    releaseDate: '2023-05-15',
  },
  {
    id: 10,
    title: '电影10',
    cover: 'https://via.placeholder.com/300x450?text=电影10',
    releaseDate: '2023-05-10',
  },
  {
    id: 11,
    title: '电影11',
    cover: 'https://via.placeholder.com/300x450?text=电影11',
    releaseDate: '2023-05-05',
  },
  {
    id: 12,
    title: '电影12',
    cover: 'https://via.placeholder.com/300x450?text=电影12',
    releaseDate: '2023-05-01',
  },
])

const fetchCarousels = async () => {
  try {
    const response = await axios.get('/carousels')
    carousels.value = response.data
  } catch (error) {
    console.error('获取轮播图失败:', error)
  }
}

const goToMovie = (id) => {
  router.push(`/movie/${id}`)
}

onMounted(() => {
  fetchCarousels()
  // 在这里可以加载数据
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
        <a-button type="link" @click="router.push('/ranking')">查看全部</a-button>
      </div>
      <div class="movie-grid">
        <div
          v-for="movie in topRatedMovies"
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
            <p class="movie-actors text-ellipsis">{{ movie.actors.join(' / ') }}</p>
            <p class="movie-price price-tag">¥{{ movie.price }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 特价区 -->
    <div class="section content-card">
      <div class="section-header">
        <h2 class="section-title">特价优惠</h2>
      </div>
      <div class="movie-grid">
        <div
          v-for="movie in specialOffers"
          :key="movie.id"
          class="movie-card hover-scale"
          @click="() => goToMovie(movie.id)"
        >
          <div class="movie-cover-wrapper">
            <img :src="movie.cover" alt="" class="movie-cover" />
            <div class="movie-discount">
              <a-tag color="#87d068">{{ movie.discount }}折</a-tag>
            </div>
          </div>
          <div class="movie-info">
            <h3 class="movie-title text-ellipsis">{{ movie.title }}</h3>
            <p class="movie-price">
              <span class="price-tag">¥{{ movie.price }}</span>
              <span class="original-price">¥{{ movie.originalPrice }}</span>
            </p>
          </div>
        </div>
      </div>
    </div>

    <!-- 最新发布 -->
    <div class="section content-card">
      <div class="section-header">
        <h2 class="section-title">最新发布</h2>
        <a-button type="link" @click="router.push('/movies')">查看全部</a-button>
      </div>
      <div class="movie-grid">
        <div
          v-for="movie in latestMovies"
          :key="movie.id"
          class="movie-card hover-scale"
          @click="() => goToMovie(movie.id)"
        >
          <div class="movie-cover-wrapper">
            <img :src="movie.cover" alt="" class="movie-cover" />
            <div class="movie-date">
              <a-tag color="#108ee9">{{ movie.releaseDate }}</a-tag>
            </div>
          </div>
          <div class="movie-info">
            <h3 class="movie-title text-ellipsis">{{ movie.title }}</h3>
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
.movie-discount,
.movie-date {
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

.movie-actors {
  font-size: 12px;
  color: var(--text-color-secondary);
  margin: 0 0 4px;
}

.movie-price {
  margin: 4px 0 0;
}

.original-price {
  margin-left: 8px;
  font-size: 12px;
  color: var(--text-color-secondary);
  text-decoration: line-through;
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
