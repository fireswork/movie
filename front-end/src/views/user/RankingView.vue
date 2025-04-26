<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { PlayCircleOutlined } from '@ant-design/icons-vue'

const router = useRouter()

// 榜单类型
const rankingTypes = [
  { key: 'rating', name: '评分榜' },
  { key: 'popularity', name: '热度榜' },
]

const activeType = ref('rating')

// 模拟榜单数据
const ratingRanking = ref([
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
  },
  {
    id: 3,
    title: '阿甘正传',
    cover: 'https://via.placeholder.com/300x450?text=阿甘正传',
    rating: 9.5,
    price: 14,
    actors: ['汤姆·汉克斯', '罗宾·怀特'],
    categories: ['剧情', '爱情'],
    region: '美国',
    year: 1994,
  },
  // 添加更多电影...
])

for (let i = 4; i <= 20; i++) {
  const randomRating = (9.5 - (i - 3) * 0.1).toFixed(1)
  const randomPrice = Math.floor(Math.random() * 20) + 5

  ratingRanking.value.push({
    id: i,
    title: `电影${i}`,
    cover: `https://via.placeholder.com/300x450?text=电影${i}`,
    rating: randomRating,
    price: randomPrice,
    actors: ['演员A', '演员B'],
    categories: ['剧情'],
    region: '美国',
    year: 2000 + i,
  })
}

const popularityRanking = ref([
  {
    id: 101,
    title: '泰坦尼克号',
    cover: 'https://via.placeholder.com/300x450?text=泰坦尼克号',
    rating: 9.4,
    price: 18,
    actors: ['莱昂纳多·迪卡普里奥', '凯特·温斯莱特'],
    categories: ['剧情', '爱情', '灾难'],
    region: '美国',
    year: 1997,
    playCount: 24582,
  },
  {
    id: 102,
    title: '盗梦空间',
    cover: 'https://via.placeholder.com/300x450?text=盗梦空间',
    rating: 9.3,
    price: 16,
    actors: ['莱昂纳多·迪卡普里奥', '约瑟夫·高登-莱维特'],
    categories: ['科幻', '悬疑', '冒险'],
    region: '美国',
    year: 2010,
    playCount: 19876,
  },
  {
    id: 103,
    title: '星际穿越',
    cover: 'https://via.placeholder.com/300x450?text=星际穿越',
    rating: 9.2,
    price: 17,
    actors: ['马修·麦康纳', '安妮·海瑟薇'],
    categories: ['科幻', '冒险'],
    region: '美国',
    year: 2014,
    playCount: 18654,
  },
  // 添加更多电影...
])

for (let i = 4; i <= 20; i++) {
  const randomRating = (9.2 - (i - 3) * 0.1).toFixed(1)
  const randomPrice = Math.floor(Math.random() * 20) + 5
  const randomPlays = Math.floor(Math.random() * 10000) + 5000

  popularityRanking.value.push({
    id: 100 + i,
    title: `热门电影${i}`,
    cover: `https://via.placeholder.com/300x450?text=热门电影${i}`,
    rating: randomRating,
    price: randomPrice,
    actors: ['演员A', '演员B'],
    categories: ['动作'],
    region: '美国',
    year: 2000 + i,
    playCount: randomPlays,
  })
}

const handleTypeChange = (type) => {
  activeType.value = type
}

const goToMovie = (id) => {
  router.push(`/movie/${id}`)
}

onMounted(() => {
  // 可以在这里加载榜单数据
})
</script>

<template>
  <div class="ranking-container page-container">
    <div class="content-card">
      <div class="ranking-header">
        <a-radio-group v-model:value="activeType" button-style="solid">
          <a-radio-button
            v-for="type in rankingTypes"
            :key="type.key"
            :value="type.key"
            @click="() => handleTypeChange(type.key)"
          >
            {{ type.name }}
          </a-radio-button>
        </a-radio-group>
      </div>

      <div class="ranking-list">
        <!-- 评分榜 -->
        <template v-if="activeType === 'rating'">
          <div
            v-for="(movie, index) in ratingRanking"
            :key="movie.id"
            class="ranking-item"
            @click="() => goToMovie(movie.id)"
          >
            <div class="ranking-number" :class="{ 'top-three': index < 3 }">{{ index + 1 }}</div>
            <div class="movie-cover-wrapper">
              <img :src="movie.cover" alt="" class="movie-cover" />
            </div>
            <div class="movie-info">
              <h3 class="movie-title text-ellipsis">{{ movie.title }}</h3>
              <p class="movie-meta text-ellipsis">
                {{ movie.categories.join('/') }} | {{ movie.region }} | {{ movie.year }}
              </p>
              <p class="movie-actors text-ellipsis">{{ movie.actors.join(' / ') }}</p>
              <div class="movie-rating">
                <a-rate :value="movie.rating / 2" disabled allow-half :count="5" />
                <span class="rating-value">{{ movie.rating }}</span>
              </div>
            </div>
            <div class="movie-price price-tag">¥{{ movie.price }}</div>
          </div>
        </template>

        <!-- 热度榜 -->
        <template v-else-if="activeType === 'popularity'">
          <div
            v-for="(movie, index) in popularityRanking"
            :key="movie.id"
            class="ranking-item"
            @click="() => goToMovie(movie.id)"
          >
            <div class="ranking-number" :class="{ 'top-three': index < 3 }">{{ index + 1 }}</div>
            <div class="movie-cover-wrapper">
              <img :src="movie.cover" alt="" class="movie-cover" />
            </div>
            <div class="movie-info">
              <h3 class="movie-title text-ellipsis">{{ movie.title }}</h3>
              <p class="movie-meta text-ellipsis">
                {{ movie.categories.join('/') }} | {{ movie.region }} | {{ movie.year }}
              </p>
              <p class="movie-actors text-ellipsis">{{ movie.actors.join(' / ') }}</p>
              <div class="movie-play-count">
                <play-circle-outlined />
                <span>{{ movie.playCount }}</span>
              </div>
            </div>
            <div class="movie-price price-tag">¥{{ movie.price }}</div>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<style scoped>
.ranking-header {
  margin-bottom: 24px;
}

.ranking-list {
  counter-reset: ranking;
}

.ranking-item {
  display: flex;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.3s;
}

.ranking-item:hover {
  background-color: #fafafa;
}

.ranking-item:last-child {
  border-bottom: none;
}

.ranking-number {
  width: 40px;
  font-size: 18px;
  font-weight: 600;
  text-align: center;
  color: var(--text-color-secondary);
}

.ranking-number.top-three {
  color: #ff4d4f;
}

.movie-cover-wrapper {
  width: 60px;
  height: 90px;
  margin-right: 16px;
  flex-shrink: 0;
}

.movie-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}

.movie-info {
  flex: 1;
}

.movie-title {
  margin: 0 0 8px;
  font-size: 16px;
  font-weight: 500;
}

.movie-meta,
.movie-actors {
  margin: 0 0 8px;
  font-size: 12px;
  color: var(--text-color-secondary);
}

.movie-rating,
.movie-play-count {
  display: flex;
  align-items: center;
  font-size: 12px;
}

.rating-value {
  margin-left: 8px;
  color: #fadb14;
  font-weight: 500;
}

.movie-play-count span {
  margin-left: 8px;
  color: var(--text-color-secondary);
}

.movie-price {
  margin-left: 16px;
  font-size: 16px;
  width: 60px;
  text-align: right;
  flex-shrink: 0;
}

@media (max-width: 768px) {
  .ranking-item {
    flex-wrap: wrap;
  }

  .movie-cover-wrapper {
    order: 1;
  }

  .ranking-number {
    order: 0;
  }

  .movie-info {
    order: 2;
    width: calc(100% - 60px - 40px - 16px);
  }

  .movie-price {
    order: 3;
    margin-left: 56px;
    margin-top: 8px;
    width: auto;
    text-align: left;
  }
}
</style>
