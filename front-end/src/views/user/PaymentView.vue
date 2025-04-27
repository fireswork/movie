<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import request from '@/services/request'
import { message } from 'ant-design-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const movie = ref(null)
const loading = ref(false)

const fetchMovieDetails = async () => {
  try {
    const { data } = await request.get(`/movies/${route.params.movieId}`)
    movie.value = data
  } catch (error) {
    console.error('获取电影详情失败:', error)
    message.error('获取电影详情失败，请稍后重试')
  }
}

const handlePayment = async () => {
  if (loading.value) return
  loading.value = true

  try {
    // 创建订单
    const { data } = await request.post('/orders', {
      userId: userStore.userId,
      movieId: movie.value.id
    })
    
    // 支付订单
    await request.post(`/orders/${data.id}/pay`)
    
    message.success('购买成功！')
    router.push(`/movies/${movie.value.id}`)
  } catch (error) {
    console.error('支付失败:', error)
    message.error(error.response?.data?.message || '支付失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchMovieDetails()
})
</script>

<template>
  <div class="payment-container page-container">
    <div class="content-card">
      <div v-if="movie" class="payment-content">
        <h1>购买电影</h1>
        
        <div class="movie-info">
          <img :src="movie.coverBase64" :alt="movie.title" class="movie-cover">
          <div class="movie-details">
            <h2>{{ movie.title }}</h2>
            <p class="price">价格: ¥{{ movie.price }}</p>
            <p class="duration">时长: {{ movie.duration }}分钟</p>
            <p class="actors">主演: {{ movie.actors }}</p>
            <p class="description">{{ movie.description }}</p>
            <div class="notice">
              <p>购买须知：</p>
              <ul>
                <li>购买后24小时内无限观看</li>
                <li>支持多端同步播放进度</li>
                <li>支持1080P高清画质</li>
              </ul>
            </div>
          </div>
        </div>

        <div class="payment-action">
          <a-button 
            type="primary" 
            size="large"
            :loading="loading"
            @click="handlePayment"
          >
            立即购买 ¥{{ movie.price }}
          </a-button>
        </div>
      </div>

      <div v-else class="loading">
        <a-spin size="large" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.payment-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.payment-content {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
}

h1 {
  margin-bottom: 24px;
  font-size: 24px;
  font-weight: 500;
}

.movie-info {
  display: flex;
  gap: 24px;
  margin-bottom: 32px;
}

.movie-cover {
  width: 200px;
  aspect-ratio: 2/3;
  object-fit: cover;
  border-radius: 8px;
}

.movie-details {
  flex: 1;
}

h2 {
  margin: 0 0 16px;
  font-size: 20px;
  font-weight: 500;
}

.price {
  font-size: 24px;
  color: #ff4d4f;
  margin: 16px 0;
  font-weight: bold;
}

.duration, .actors {
  color: #666;
  margin: 8px 0;
}

.description {
  color: #666;
  line-height: 1.6;
  margin: 16px 0;
}

.notice {
  background: #f5f5f5;
  padding: 16px;
  border-radius: 8px;
  margin-top: 24px;
}

.notice p {
  font-weight: 500;
  margin-bottom: 8px;
}

.notice ul {
  padding-left: 20px;
  margin: 0;
}

.notice li {
  color: #666;
  line-height: 1.6;
}

.payment-action {
  text-align: center;
  margin-top: 32px;
}

.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

@media (max-width: 768px) {
  .movie-info {
    flex-direction: column;
  }

  .movie-cover {
    width: 100%;
    max-width: 300px;
    margin: 0 auto;
  }
}
</style>
