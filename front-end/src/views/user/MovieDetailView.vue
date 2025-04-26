<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  HeartOutlined,
  HeartFilled,
  StarOutlined,
  StarFilled,
  MessageOutlined,
  PlayCircleOutlined,
  LockOutlined,
} from '@ant-design/icons-vue'

const route = useRoute()
const router = useRouter()
const movieId = route.params.id

// 模拟电影数据
const movie = ref({
  id: movieId,
  title: '肖申克的救赎',
  cover: 'https://via.placeholder.com/300x450?text=肖申克的救赎',
  poster: 'https://via.placeholder.com/1200x675?text=肖申克的救赎-海报',
  rating: 9.7,
  price: 15,
  isPaid: true,
  hasPurchased: false,
  actors: ['蒂姆·罗宾斯', '摩根·弗里曼', '鲍勃·冈顿', '威廉姆·赛德勒'],
  director: '弗兰克·德拉邦特',
  categories: ['剧情', '犯罪'],
  region: '美国',
  year: 1994,
  duration: 142,
  releaseDate: '1994-09-10',
  description:
    '一场谋杀案使银行家安迪（蒂姆·罗宾斯 Tim Robbins 饰）蒙冤入狱，谋杀妻子及其情人的指控将他带入了鼎鼎有名的肖申克监狱。在这里，监狱长昏庸无能，狱霸凶狠残暴，典狱长滥用职权。从一开始，安迪就面临着囚犯们的歧视和欺凌，但他依靠自己的智慧取得了监狱管理层的信任，特别是他为监狱长洗黑钱的本领，使他获得了大量物质优待和较为宽松的生活环境。另一方面，他也凭借自己的知识、谋略和教养，赢得了囚犯们的尊敬，并且跟瑞德（摩根·弗里曼 Morgan Freeman 饰）成为了朋友。瑞德为监狱内外搞到各种违禁品而闻名，他的故事和对安迪的观察成为影片的叙述主线...…',
  trailer: 'https://example.com/trailer.mp4',
  commentCount: 243,
  playCount: 1024,
  collectionCount: 512,
  isCollected: false,
  userRating: 0,
})

// 用户评分
const userRating = ref(0)
const hoverRating = ref(0)

// 评论相关
const commentForm = reactive({
  content: '',
})
const comments = ref([
  {
    id: 1,
    username: '用户A',
    avatar: 'https://via.placeholder.com/40',
    content: '这是我看过最好的电影之一，情节扣人心弦，表演精湛，绝对的经典！',
    time: '2023-04-15 14:23',
    likes: 45,
  },
  {
    id: 2,
    username: '用户B',
    avatar: 'https://via.placeholder.com/40',
    content: '摩根·弗里曼的配音太棒了，整个电影节奏把握得非常好。',
    time: '2023-04-10 09:15',
    likes: 32,
  },
  {
    id: 3,
    username: '用户C',
    avatar: 'https://via.placeholder.com/40',
    content: '虽然有些情节有点拖沓，但整体上这是部非常优秀的作品。',
    time: '2023-03-28 16:47',
    likes: 12,
  },
])

// 相似电影推荐
const similarMovies = ref([
  {
    id: 101,
    title: '阿甘正传',
    cover: 'https://via.placeholder.com/300x450?text=阿甘正传',
    rating: 9.5,
  },
  {
    id: 102,
    title: '绿里奇迹',
    cover: 'https://via.placeholder.com/300x450?text=绿里奇迹',
    rating: 9.2,
  },
  {
    id: 103,
    title: '当幸福来敲门',
    cover: 'https://via.placeholder.com/300x450?text=当幸福来敲门',
    rating: 9.1,
  },
  {
    id: 104,
    title: '楚门的世界',
    cover: 'https://via.placeholder.com/300x450?text=楚门的世界',
    rating: 9.3,
  },
])

// 方法
const handleCollection = () => {
  movie.value.isCollected = !movie.value.isCollected
  if (movie.value.isCollected) {
    movie.value.collectionCount += 1
  } else {
    movie.value.collectionCount -= 1
  }
}

const handleRate = (rating) => {
  userRating.value = rating
  movie.value.userRating = rating
}

const handleComment = () => {
  if (!commentForm.content.trim()) {
    return
  }

  comments.value.unshift({
    id: Date.now(),
    username: '当前用户',
    avatar: 'https://via.placeholder.com/40',
    content: commentForm.content,
    time: new Date().toLocaleString(),
    likes: 0,
  })

  commentForm.content = ''
  movie.value.commentCount += 1
}

const handlePlay = () => {
  if (movie.value.isPaid && !movie.value.hasPurchased) {
    router.push(`/payment/${movie.value.id}`)
    return
  }

  // 播放电影的逻辑
  alert('开始播放电影')
  movie.value.playCount += 1
}

const goToPayment = () => {
  router.push(`/payment/${movie.value.id}`)
}

const goToMovie = (id) => {
  router.push(`/movie/${id}`)
}

onMounted(() => {
  // 可以在这里加载电影详情数据
})
</script>

<template>
  <div class="movie-detail-container page-container">
    <!-- 海报和基本信息 -->
    <div class="movie-header content-card">
      <div class="movie-poster-wrapper">
        <img :src="movie.poster" alt="" class="movie-poster" />
      </div>

      <div class="movie-basic-info">
        <div class="movie-cover-wrapper">
          <img :src="movie.cover" alt="" class="movie-cover" />
        </div>

        <div class="movie-info-detail">
          <h1 class="movie-title">{{ movie.title }}</h1>

          <div class="movie-meta">
            <div class="meta-item">
              <span class="meta-label">导演：</span>
              <span class="meta-value">{{ movie.director }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">主演：</span>
              <span class="meta-value">{{ movie.actors.join('、') }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">类型：</span>
              <span class="meta-value">{{ movie.categories.join('、') }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">地区：</span>
              <span class="meta-value">{{ movie.region }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">年份：</span>
              <span class="meta-value">{{ movie.year }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">片长：</span>
              <span class="meta-value">{{ movie.duration }}分钟</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">上映日期：</span>
              <span class="meta-value">{{ movie.releaseDate }}</span>
            </div>
          </div>

          <div class="movie-stats">
            <div class="stat-item">
              <span class="stat-value">{{ movie.rating }}</span>
              <span class="stat-label">评分</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ movie.playCount }}</span>
              <span class="stat-label">播放</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ movie.collectionCount }}</span>
              <span class="stat-label">收藏</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ movie.commentCount }}</span>
              <span class="stat-label">评论</span>
            </div>
          </div>

          <div class="movie-actions">
            <a-button
              type="primary"
              size="large"
              @click="handlePlay"
              :icon="movie.isPaid && !movie.hasPurchased ? LockOutlined : PlayCircleOutlined"
            >
              {{ movie.isPaid && !movie.hasPurchased ? '购买观看' : '立即播放' }}
            </a-button>

            <a-button
              :type="movie.isCollected ? 'primary' : 'default'"
              @click="handleCollection"
              :icon="movie.isCollected ? HeartFilled : HeartOutlined"
            >
              {{ movie.isCollected ? '已收藏' : '收藏' }}
            </a-button>

            <a-button v-if="movie.isPaid && !movie.hasPurchased" type="danger" @click="goToPayment">
              ¥{{ movie.price }}
            </a-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 电影详情 -->
    <div class="movie-content content-card">
      <a-tabs default-active-key="1">
        <a-tab-pane key="1" tab="剧情简介">
          <div class="movie-description">
            <h3>剧情简介</h3>
            <p>{{ movie.description }}</p>
          </div>

          <div class="trailer-section" v-if="movie.trailer">
            <h3>预告片</h3>
            <div class="trailer-player">
              <img
                src="https://via.placeholder.com/1200x675?text=视频预览"
                alt="预告片"
                style="width: 100%"
              />
              <a-button type="primary" shape="circle" size="large" class="play-button">
                <template #icon><play-circle-outlined /></template>
              </a-button>
            </div>
          </div>
        </a-tab-pane>

        <a-tab-pane key="2" tab="评分评论">
          <div class="rating-section">
            <h3>我的评分</h3>
            <div class="rating-stars">
              <div
                v-for="i in 10"
                :key="i"
                class="star-wrapper"
                @mouseenter="hoverRating = i"
                @mouseleave="hoverRating = 0"
                @click="handleRate(i)"
              >
                <component
                  :is="(hoverRating || userRating) >= i ? StarFilled : StarOutlined"
                  class="star-icon"
                  :class="{ active: (hoverRating || userRating) >= i }"
                />
              </div>
              <span class="rating-value">{{ hoverRating || userRating || '未评分' }}</span>
            </div>
          </div>

          <div class="comment-section">
            <h3>发表评论</h3>
            <div class="comment-form">
              <a-textarea
                v-model:value="commentForm.content"
                placeholder="说说你的看法..."
                :rows="4"
              />
              <div class="comment-form-footer">
                <a-button type="primary" @click="handleComment">发表评论</a-button>
              </div>
            </div>

            <div class="comments-list">
              <h3>全部评论 ({{ movie.commentCount }})</h3>
              <div v-for="comment in comments" :key="comment.id" class="comment-item">
                <div class="comment-avatar">
                  <a-avatar :src="comment.avatar" />
                </div>
                <div class="comment-content">
                  <div class="comment-header">
                    <span class="comment-username">{{ comment.username }}</span>
                    <span class="comment-time">{{ comment.time }}</span>
                  </div>
                  <div class="comment-text">{{ comment.content }}</div>
                  <div class="comment-footer">
                    <a-button type="text" size="small">
                      <template #icon><like-outlined /></template>
                      {{ comment.likes }}
                    </a-button>
                    <a-button type="text" size="small">
                      <template #icon><message-outlined /></template>
                      回复
                    </a-button>
                  </div>
                </div>
              </div>

              <div class="comments-pagination">
                <a-pagination :total="50" :pageSize="10" showSizeChanger />
              </div>
            </div>
          </div>
        </a-tab-pane>
      </a-tabs>
    </div>

    <!-- 相似电影推荐 -->
    <div class="similar-movies content-card">
      <h2 class="section-title">猜你喜欢</h2>
      <div class="movie-grid">
        <div
          v-for="movie in similarMovies"
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
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.movie-detail-container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
}

.movie-header {
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 24px;
}

.movie-poster-wrapper {
  position: relative;
  height: 400px;
  overflow: hidden;
}

.movie-poster {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.movie-basic-info {
  padding: 24px;
  display: flex;
  margin-top: -120px;
  position: relative;
  z-index: 1;
}

.movie-cover-wrapper {
  flex: 0 0 200px;
  margin-right: 24px;
}

.movie-cover {
  width: 100%;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.movie-info-detail {
  flex: 1;
}

.movie-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 16px;
}

.movie-meta {
  margin-bottom: 16px;
}

.meta-item {
  margin-bottom: 8px;
}

.meta-label {
  color: var(--text-color-secondary);
  margin-right: 8px;
}

.movie-stats {
  display: flex;
  gap: 24px;
  margin-bottom: 24px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: var(--primary-color);
}

.stat-label {
  font-size: 12px;
  color: var(--text-color-secondary);
}

.movie-actions {
  display: flex;
  gap: 16px;
}

.movie-content {
  margin-bottom: 24px;
}

.movie-description {
  margin-bottom: 24px;
}

.movie-description h3 {
  margin-bottom: 16px;
}

.movie-description p {
  line-height: 1.8;
  text-align: justify;
}

.trailer-section {
  margin-top: 24px;
}

.trailer-section h3 {
  margin-bottom: 16px;
}

.trailer-player {
  position: relative;
  border-radius: 8px;
  overflow: hidden;
}

.play-button {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 48px;
  width: 80px;
  height: 80px;
  opacity: 0.9;
}

.rating-section {
  margin-bottom: 24px;
}

.rating-stars {
  display: flex;
  align-items: center;
  margin-top: 8px;
}

.star-wrapper {
  padding: 4px;
  cursor: pointer;
}

.star-icon {
  font-size: 24px;
  color: #d9d9d9;
}

.star-icon.active {
  color: #fadb14;
}

.rating-value {
  margin-left: 16px;
  font-size: 18px;
  color: #fadb14;
}

.comment-form {
  margin-bottom: 24px;
}

.comment-form-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.comments-list h3 {
  margin-bottom: 16px;
}

.comment-item {
  display: flex;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.comment-avatar {
  margin-right: 16px;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.comment-username {
  font-weight: 500;
}

.comment-time {
  font-size: 12px;
  color: var(--text-color-secondary);
}

.comment-text {
  margin-bottom: 8px;
  line-height: 1.6;
}

.comment-footer {
  display: flex;
  gap: 16px;
}

.comments-pagination {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

.similar-movies h2 {
  margin-bottom: 16px;
}

.movie-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 16px;
}

@media (max-width: 768px) {
  .movie-basic-info {
    flex-direction: column;
  }

  .movie-cover-wrapper {
    margin-right: 0;
    margin-bottom: 16px;
    max-width: 160px;
  }

  .movie-actions {
    flex-wrap: wrap;
  }

  .movie-stats {
    justify-content: space-between;
  }
}
</style>
