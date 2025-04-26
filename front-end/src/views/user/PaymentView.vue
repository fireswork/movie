<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const movieId = route.params.id

// 模拟电影数据
const movie = ref({
  id: movieId,
  title: '肖申克的救赎',
  cover: 'https://via.placeholder.com/300x450?text=肖申克的救赎',
  price: 15,
  rating: 9.7,
  duration: 142,
  year: 1994,
})

// 支付方式
const paymentMethods = [
  { id: 1, name: '支付宝', icon: 'alipay-circle-outlined', color: '#1677FF' },
  { id: 2, name: '微信支付', icon: 'wechat-outlined', color: '#07C160' },
  { id: 3, name: '银行卡', icon: 'credit-card-outlined', color: '#FA8C16' },
]

const selectedPayment = ref(paymentMethods[0].id)
const loading = ref(false)
const orderNo = ref('')

// 生成随机订单号
const generateOrderNo = () => {
  const date = new Date()
  const timestamp = date.getTime().toString()
  const random = Math.floor(Math.random() * 1000)
    .toString()
    .padStart(3, '0')
  return `ORD${timestamp}${random}`
}

const handlePayment = () => {
  loading.value = true

  // 模拟支付过程
  setTimeout(() => {
    loading.value = false

    // 假设支付成功
    const success = Math.random() > 0.2 // 80%概率支付成功

    if (success) {
      router.push({
        path: '/payment/success',
        query: {
          orderNo: orderNo.value,
          movieId: movie.value.id,
          movieTitle: movie.value.title,
          amount: movie.value.price,
        },
      })
    } else {
      // 支付失败
      router.push({
        path: '/payment/fail',
        query: {
          orderNo: orderNo.value,
        },
      })
    }
  }, 2000)
}

const goBack = () => {
  router.go(-1)
}

onMounted(() => {
  // 生成订单号
  orderNo.value = generateOrderNo()

  // 可以在这里加载电影数据
})
</script>

<template>
  <div class="payment-container page-container">
    <div class="content-card">
      <div class="payment-header">
        <h2>订单确认</h2>
        <a-button @click="goBack">返回</a-button>
      </div>

      <div class="order-info">
        <div class="movie-info">
          <img :src="movie.cover" alt="" class="movie-cover" />
          <div class="movie-detail">
            <h3 class="movie-title">{{ movie.title }}</h3>
            <p class="movie-meta">{{ movie.year }} | {{ movie.duration }}分钟</p>
            <p class="movie-rating">评分：{{ movie.rating }}</p>
          </div>
        </div>

        <div class="price-info">
          <div class="price-item">
            <span class="price-label">商品金额</span>
            <span class="price-value">¥{{ movie.price }}</span>
          </div>
          <div class="price-item">
            <span class="price-label">优惠</span>
            <span class="price-value">-¥0</span>
          </div>
          <a-divider style="margin: 12px 0" />
          <div class="price-item total-price">
            <span class="price-label">应付金额</span>
            <span class="price-value price-tag">¥{{ movie.price }}</span>
          </div>
        </div>
      </div>

      <div class="payment-method">
        <h3>支付方式</h3>
        <a-radio-group v-model:value="selectedPayment">
          <a-radio
            v-for="method in paymentMethods"
            :key="method.id"
            :value="method.id"
            class="payment-method-item"
          >
            <div class="payment-method-content">
              <a-avatar :style="{ backgroundColor: method.color }">
                <template #icon>
                  <component :is="method.icon" />
                </template>
              </a-avatar>
              <span>{{ method.name }}</span>
            </div>
          </a-radio>
        </a-radio-group>
      </div>

      <div class="order-detail">
        <h3>订单详情</h3>
        <div class="order-item">
          <span class="order-label">订单编号</span>
          <span class="order-value">{{ orderNo }}</span>
        </div>
        <div class="order-item">
          <span class="order-label">商品名称</span>
          <span class="order-value">{{ movie.title }} (24小时观看权限)</span>
        </div>
        <div class="order-item">
          <span class="order-label">创建时间</span>
          <span class="order-value">{{ new Date().toLocaleString() }}</span>
        </div>
      </div>

      <div class="payment-action">
        <a-button type="primary" size="large" @click="handlePayment" :loading="loading" block>
          确认支付 ¥{{ movie.price }}
        </a-button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.payment-container {
  max-width: 800px;
  margin: 0 auto;
}

.payment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.payment-header h2 {
  margin: 0;
}

.order-info {
  margin-bottom: 24px;
}

.movie-info {
  display: flex;
  margin-bottom: 24px;
}

.movie-cover {
  width: 120px;
  height: 180px;
  object-fit: cover;
  border-radius: 4px;
  margin-right: 16px;
}

.movie-detail {
  flex: 1;
}

.movie-title {
  margin: 0 0 8px;
  font-size: 18px;
}

.movie-meta,
.movie-rating {
  margin: 0 0 8px;
  font-size: 14px;
  color: var(--text-color-secondary);
}

.price-info {
  background-color: #f9f9f9;
  border-radius: 4px;
  padding: 16px;
}

.price-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.total-price {
  font-weight: 500;
}

.payment-method {
  margin-bottom: 24px;
}

.payment-method h3,
.order-detail h3 {
  margin-bottom: 16px;
}

.payment-method-item {
  display: block;
  margin-bottom: 16px;
}

.payment-method-content {
  display: flex;
  align-items: center;
}

.payment-method-content .ant-avatar {
  margin-right: 8px;
}

.order-detail {
  margin-bottom: 24px;
}

.order-item {
  display: flex;
  margin-bottom: 12px;
}

.order-label {
  width: 80px;
  color: var(--text-color-secondary);
}

.payment-action {
  margin-top: 40px;
}

@media (max-width: 576px) {
  .movie-info {
    flex-direction: column;
  }

  .movie-cover {
    margin-right: 0;
    margin-bottom: 16px;
    width: 100px;
    height: 150px;
  }
}
</style>
