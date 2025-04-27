<template>
  <!-- No changes to template section -->
</template>

<script>
import { ElMessage } from 'element-plus'
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { request } from '../utils/request'

export default {
  setup() {
    const router = useRouter()
    const movies = ref([])

    async function playMovie(movie) {
      if (!movie) return
      
      try {
        if (movie.price > 0) {
          // 检查用户是否已购买
          const { data } = await request.get(`/orders/check?movieId=${movie.id}`)
          if (!data.purchased) {
            // 未购买，跳转到支付页面
            router.push(`/payment/${movie.id}`)
            return
          }
        }
        
        // 免费电影或已购买的电影，直接播放
        router.push(`/play/${movie.id}`)
      } catch (error) {
        ElMessage.error('播放失败，请稍后重试')
        console.error('播放电影时出错:', error)
      }
    }

    return {
      movies,
      playMovie
    }
  }
}
</script>

<style>
  /* No changes to style section */
</style> 