<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { PlayCircleOutlined } from '@ant-design/icons-vue'

const router = useRouter()

// 分类
const categories = ref([
  { key: 'all', name: '全部' },
  { key: 'action', name: '动作' },
  { key: 'comedy', name: '喜剧' },
  { key: 'romance', name: '爱情' },
  { key: 'scifi', name: '科幻' },
  { key: 'horror', name: '恐怖' },
  { key: 'animation', name: '动画' },
  { key: 'drama', name: '剧情' },
  { key: 'war', name: '战争' },
])

const activeCategory = ref('all')

// 模拟预告片数据
const previews = ref([
  {
    id: 1,
    title: '阿凡达：水之道',
    cover: 'https://via.placeholder.com/800x450?text=阿凡达：水之道',
    duration: '3:26',
    views: 1568,
    date: '2023-05-15',
    category: 'scifi',
  },
  {
    id: 2,
    title: '速度与激情10',
    cover: 'https://via.placeholder.com/800x450?text=速度与激情10',
    duration: '2:45',
    views: 1247,
    date: '2023-05-12',
    category: 'action',
  },
  {
    id: 3,
    title: '变形金刚：超能勇士崛起',
    cover: 'https://via.placeholder.com/800x450?text=变形金刚：超能勇士崛起',
    duration: '2:15',
    views: 986,
    date: '2023-05-10',
    category: 'action',
  },
  {
    id: 4,
    title: '碟中谍7：致命清算',
    cover: 'https://via.placeholder.com/800x450?text=碟中谍7：致命清算',
    duration: '3:01',
    views: 876,
    date: '2023-05-08',
    category: 'action',
  },
  {
    id: 5,
    title: '灌篮高手',
    cover: 'https://via.placeholder.com/800x450?text=灌篮高手',
    duration: '2:32',
    views: 754,
    date: '2023-05-05',
    category: 'animation',
  },
  {
    id: 6,
    title: '蜘蛛侠：纵横宇宙',
    cover: 'https://via.placeholder.com/800x450?text=蜘蛛侠：纵横宇宙',
    duration: '2:56',
    views: 632,
    date: '2023-05-03',
    category: 'animation',
  },
  {
    id: 7,
    title: '蚁人与黄蜂女：量子狂潮',
    cover: 'https://via.placeholder.com/800x450?text=蚁人与黄蜂女：量子狂潮',
    duration: '2:17',
    views: 521,
    date: '2023-05-01',
    category: 'scifi',
  },
  {
    id: 8,
    title: '保你平安',
    cover: 'https://via.placeholder.com/800x450?text=保你平安',
    duration: '2:29',
    views: 478,
    date: '2023-04-28',
    category: 'drama',
  },
])

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 8,
  total: previews.value.length,
  onChange: (page) => {
    pagination.current = page
    // 在这里加载对应页码的数据
  },
})

const filteredPreviews = computed(() => {
  if (activeCategory.value === 'all') {
    return previews.value
  }
  return previews.value.filter((item) => item.category === activeCategory.value)
})

const handleCategoryChange = (key) => {
  activeCategory.value = key
  pagination.current = 1
}

const playPreview = (preview) => {
  // 播放预告片逻辑
  console.log('播放预告片:', preview.title)
}

const goToMovie = (id) => {
  router.push(`/movie/${id}`)
}

onMounted(() => {
  // 可以在这里加载预告片数据
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
        <div v-for="preview in filteredPreviews" :key="preview.id" class="preview-card hover-scale">
          <div class="preview-cover-wrapper" @click="playPreview(preview)">
            <img :src="preview.cover" alt="" class="preview-cover" />
            <div class="preview-duration">{{ preview.duration }}</div>
            <div class="preview-play-button">
              <play-circle-outlined />
            </div>
          </div>
          <div class="preview-info">
            <h3 class="preview-title text-ellipsis" @click="goToMovie(preview.id)">
              {{ preview.title }}
            </h3>
            <div class="preview-meta">
              <span>{{ preview.views }}次播放</span>
              <span>{{ preview.date }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="pagination">
        <a-pagination
          v-model:current="pagination.current"
          :pageSize="pagination.pageSize"
          :total="filteredPreviews.length"
          @change="pagination.onChange"
          showSizeChanger
          :pageSizeOptions="['8', '16', '24', '32']"
        />
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

.preview-duration {
  position: absolute;
  bottom: 8px;
  right: 8px;
  background-color: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
}

.preview-play-button {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: white;
  font-size: 48px;
  opacity: 0.8;
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
  font-size: 12px;
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
