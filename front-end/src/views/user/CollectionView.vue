<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { DeleteOutlined, PlusOutlined, EditOutlined } from '@ant-design/icons-vue'

const router = useRouter()

// 收藏夹列表
const collections = ref([
  { id: 1, name: '默认收藏夹', count: 5 },
  { id: 2, name: '待看电影', count: 3 },
  { id: 3, name: '已看过', count: 7 },
  { id: 4, name: '科幻电影', count: 4 },
])

// 当前选中的收藏夹
const activeCollection = ref(1)

// 模拟电影数据
const collectionMovies = reactive({
  1: [
    {
      id: 1,
      title: '肖申克的救赎',
      cover: 'https://via.placeholder.com/300x450?text=肖申克的救赎',
      rating: 9.7,
      price: 15,
      collectTime: '2023-05-01 15:23',
    },
    {
      id: 2,
      title: '霸王别姬',
      cover: 'https://via.placeholder.com/300x450?text=霸王别姬',
      rating: 9.6,
      price: 12,
      collectTime: '2023-05-02 10:15',
    },
    {
      id: 3,
      title: '阿甘正传',
      cover: 'https://via.placeholder.com/300x450?text=阿甘正传',
      rating: 9.5,
      price: 14,
      collectTime: '2023-05-03 18:42',
    },
    {
      id: 4,
      title: '泰坦尼克号',
      cover: 'https://via.placeholder.com/300x450?text=泰坦尼克号',
      rating: 9.4,
      price: 18,
      collectTime: '2023-05-04 20:11',
    },
    {
      id: 5,
      title: '这个杀手不太冷',
      cover: 'https://via.placeholder.com/300x450?text=这个杀手不太冷',
      rating: 9.4,
      price: 10,
      collectTime: '2023-05-05 09:30',
    },
  ],
  2: [
    {
      id: 6,
      title: '千与千寻',
      cover: 'https://via.placeholder.com/300x450?text=千与千寻',
      rating: 9.3,
      price: 16,
      collectTime: '2023-05-06 14:27',
    },
    {
      id: 7,
      title: '辛德勒的名单',
      cover: 'https://via.placeholder.com/300x450?text=辛德勒的名单',
      rating: 9.5,
      price: 15,
      collectTime: '2023-05-07 11:15',
    },
    {
      id: 8,
      title: '盗梦空间',
      cover: 'https://via.placeholder.com/300x450?text=盗梦空间',
      rating: 9.3,
      price: 17,
      collectTime: '2023-05-08 16:40',
    },
  ],
  3: [
    {
      id: 9,
      title: '星际穿越',
      cover: 'https://via.placeholder.com/300x450?text=星际穿越',
      rating: 9.3,
      price: 16,
      collectTime: '2023-05-09 09:12',
    },
    {
      id: 10,
      title: '楚门的世界',
      cover: 'https://via.placeholder.com/300x450?text=楚门的世界',
      rating: 9.2,
      price: 14,
      collectTime: '2023-05-10 13:24',
    },
    {
      id: 11,
      title: '海上钢琴师',
      cover: 'https://via.placeholder.com/300x450?text=海上钢琴师',
      rating: 9.2,
      price: 15,
      collectTime: '2023-05-11 17:38',
    },
    {
      id: 12,
      title: '三傻大闹宝莱坞',
      cover: 'https://via.placeholder.com/300x450?text=三傻大闹宝莱坞',
      rating: 9.2,
      price: 13,
      collectTime: '2023-05-12 19:51',
    },
    {
      id: 13,
      title: '放牛班的春天',
      cover: 'https://via.placeholder.com/300x450?text=放牛班的春天',
      rating: 9.2,
      price: 12,
      collectTime: '2023-05-13 08:45',
    },
    {
      id: 14,
      title: '大话西游之大圣娶亲',
      cover: 'https://via.placeholder.com/300x450?text=大话西游之大圣娶亲',
      rating: 9.2,
      price: 14,
      collectTime: '2023-05-14 14:32',
    },
    {
      id: 15,
      title: '熔炉',
      cover: 'https://via.placeholder.com/300x450?text=熔炉',
      rating: 9.3,
      price: 15,
      collectTime: '2023-05-15 16:27',
    },
  ],
  4: [
    {
      id: 16,
      title: '星际穿越',
      cover: 'https://via.placeholder.com/300x450?text=星际穿越',
      rating: 9.3,
      price: 16,
      collectTime: '2023-05-16 10:14',
    },
    {
      id: 17,
      title: '盗梦空间',
      cover: 'https://via.placeholder.com/300x450?text=盗梦空间',
      rating: 9.3,
      price: 17,
      collectTime: '2023-05-17 15:23',
    },
    {
      id: 18,
      title: '黑客帝国',
      cover: 'https://via.placeholder.com/300x450?text=黑客帝国',
      rating: 9.0,
      price: 15,
      collectTime: '2023-05-18 19:46',
    },
    {
      id: 19,
      title: '2001太空漫游',
      cover: 'https://via.placeholder.com/300x450?text=2001太空漫游',
      rating: 8.9,
      price: 14,
      collectTime: '2023-05-19 11:35',
    },
  ],
})

// 收藏夹管理相关
const collectionFormVisible = ref(false)
const collectionForm = reactive({
  id: null,
  name: '',
})
const editingCollection = ref(false)

// 电影选择状态
const selectedMovies = ref([])
const isSelecting = ref(false)

// 方法
const handleCollectionClick = (id) => {
  activeCollection.value = id
  selectedMovies.value = []
  isSelecting.value = false
}

const showAddCollection = () => {
  editingCollection.value = false
  collectionForm.id = null
  collectionForm.name = ''
  collectionFormVisible.value = true
}

const showEditCollection = (id) => {
  const collection = collections.value.find((item) => item.id === id)
  if (collection) {
    editingCollection.value = true
    collectionForm.id = collection.id
    collectionForm.name = collection.name
    collectionFormVisible.value = true
  }
}

const handleCollectionFormSubmit = () => {
  if (!collectionForm.name.trim()) {
    return
  }

  if (editingCollection.value) {
    // 编辑收藏夹
    const index = collections.value.findIndex((item) => item.id === collectionForm.id)
    if (index !== -1) {
      collections.value[index].name = collectionForm.name
    }
  } else {
    // 添加收藏夹
    const newId =
      collections.value.length > 0 ? Math.max(...collections.value.map((item) => item.id)) + 1 : 1

    collections.value.push({
      id: newId,
      name: collectionForm.name,
      count: 0,
    })

    // 初始化新收藏夹的电影列表
    collectionMovies[newId] = []
  }

  collectionFormVisible.value = false
}

const handleDeleteCollection = (id) => {
  if (collections.value.length <= 1) {
    // 至少保留一个收藏夹
    return
  }

  // 确认删除
  if (
    confirm(`确定要删除收藏夹 "${collections.value.find((item) => item.id === id)?.name}" 吗？`)
  ) {
    const index = collections.value.findIndex((item) => item.id === id)
    if (index !== -1) {
      collections.value.splice(index, 1)
      delete collectionMovies[id]

      // 如果删除的是当前选中的收藏夹，则选中第一个收藏夹
      if (activeCollection.value === id) {
        activeCollection.value = collections.value[0]?.id
      }
    }
  }
}

const toggleSelectMode = () => {
  isSelecting.value = !isSelecting.value
  selectedMovies.value = []
}

const handleMovieClick = (movie) => {
  if (isSelecting.value) {
    const index = selectedMovies.value.findIndex((item) => item === movie.id)
    if (index === -1) {
      selectedMovies.value.push(movie.id)
    } else {
      selectedMovies.value.splice(index, 1)
    }
  } else {
    goToMovie(movie.id)
  }
}

const isMovieSelected = (id) => {
  return selectedMovies.value.includes(id)
}

const deleteSelectedMovies = () => {
  if (selectedMovies.value.length === 0) {
    return
  }

  // 确认删除
  if (confirm(`确定要删除选中的 ${selectedMovies.value.length} 部电影吗？`)) {
    // 从当前收藏夹中删除选中的电影
    collectionMovies[activeCollection.value] = collectionMovies[activeCollection.value].filter(
      (movie) => !selectedMovies.value.includes(movie.id),
    )

    // 更新收藏夹电影数量
    const collectionIndex = collections.value.findIndex(
      (item) => item.id === activeCollection.value,
    )
    if (collectionIndex !== -1) {
      collections.value[collectionIndex].count = collectionMovies[activeCollection.value].length
    }

    // 清空选中状态
    selectedMovies.value = []
    isSelecting.value = false
  }
}

const goToMovie = (id) => {
  router.push(`/movie/${id}`)
}

onMounted(() => {
  // 可以在这里加载用户收藏数据
})
</script>

<template>
  <div class="collection-container page-container">
    <div class="collection-layout">
      <!-- 收藏夹侧边栏 -->
      <div class="collection-sidebar content-card">
        <div class="sidebar-header">
          <h2>我的收藏夹</h2>
          <a-button type="primary" shape="circle" @click="showAddCollection">
            <template #icon><plus-outlined /></template>
          </a-button>
        </div>

        <div class="collection-list">
          <div
            v-for="collection in collections"
            :key="collection.id"
            class="collection-item"
            :class="{ active: activeCollection === collection.id }"
            @click="() => handleCollectionClick(collection.id)"
          >
            <div class="collection-item-info">
              <span class="collection-name">{{ collection.name }}</span>
              <span class="collection-count">{{ collection.count }}部</span>
            </div>
            <div class="collection-item-actions">
              <a-button
                type="text"
                size="small"
                @click.stop="() => showEditCollection(collection.id)"
              >
                <template #icon><edit-outlined /></template>
              </a-button>
              <a-button
                type="text"
                size="small"
                danger
                @click.stop="() => handleDeleteCollection(collection.id)"
                v-if="collections.length > 1"
              >
                <template #icon><delete-outlined /></template>
              </a-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 电影列表 -->
      <div class="collection-content content-card">
        <div class="content-header">
          <h2>{{ collections.find((item) => item.id === activeCollection)?.name }}</h2>
          <div class="header-actions">
            <a-button v-if="isSelecting" type="primary" @click="deleteSelectedMovies" danger>
              删除选中({{ selectedMovies.length }})
            </a-button>
            <a-button @click="toggleSelectMode">
              {{ isSelecting ? '取消选择' : '选择' }}
            </a-button>
          </div>
        </div>

        <div v-if="collectionMovies[activeCollection]?.length" class="movie-grid">
          <div
            v-for="movie in collectionMovies[activeCollection]"
            :key="movie.id"
            class="movie-card hover-scale"
            :class="{ selected: isMovieSelected(movie.id) }"
            @click="() => handleMovieClick(movie)"
          >
            <div class="movie-cover-wrapper">
              <img :src="movie.cover" alt="" class="movie-cover" />
              <div class="movie-select-overlay" v-if="isSelecting">
                <a-checkbox :checked="isMovieSelected(movie.id)" />
              </div>
              <div class="movie-rating">
                <a-tag color="#f50">{{ movie.rating }}分</a-tag>
              </div>
            </div>
            <div class="movie-info">
              <h3 class="movie-title text-ellipsis">{{ movie.title }}</h3>
              <p class="movie-price price-tag">¥{{ movie.price }}</p>
              <p class="movie-collect-time">收藏于 {{ movie.collectTime }}</p>
            </div>
          </div>
        </div>

        <div v-else class="empty-collection">
          <a-empty description="暂无收藏电影" />
        </div>
      </div>
    </div>

    <!-- 收藏夹表单弹窗 -->
    <a-modal
      v-model:visible="collectionFormVisible"
      :title="editingCollection ? '编辑收藏夹' : '新建收藏夹'"
      @ok="handleCollectionFormSubmit"
    >
      <a-form layout="vertical">
        <a-form-item label="收藏夹名称">
          <a-input v-model:value="collectionForm.name" placeholder="请输入收藏夹名称" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<style scoped>
.collection-container {
  padding: 16px;
}

.collection-layout {
  display: flex;
  gap: 16px;
}

.collection-sidebar {
  width: 300px;
  flex-shrink: 0;
}

.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.sidebar-header h2 {
  margin: 0;
  font-size: 18px;
}

.collection-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.collection-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.collection-item:hover {
  background-color: #f5f5f5;
}

.collection-item.active {
  background-color: #e6f7ff;
  border-left: 3px solid var(--primary-color);
}

.collection-item-info {
  display: flex;
  flex-direction: column;
}

.collection-name {
  font-weight: 500;
}

.collection-count {
  font-size: 12px;
  color: var(--text-color-secondary);
}

.collection-item-actions {
  display: none;
}

.collection-item:hover .collection-item-actions {
  display: flex;
  gap: 4px;
}

.collection-content {
  flex: 1;
}

.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.content-header h2 {
  margin: 0;
  font-size: 20px;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.movie-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 24px;
}

.movie-card {
  cursor: pointer;
  transition:
    transform 0.2s,
    box-shadow 0.3s;
  border-radius: 8px;
  overflow: hidden;
}

.movie-card.selected {
  box-shadow: 0 0 0 2px var(--primary-color);
}

.movie-cover-wrapper {
  position: relative;
  overflow: hidden;
}

.movie-select-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.movie-select-overlay .ant-checkbox {
  transform: scale(1.5);
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
  padding: 12px;
}

.movie-title {
  margin: 0 0 8px;
  font-size: 16px;
  font-weight: 500;
}

.movie-price {
  margin: 0 0 8px;
}

.movie-collect-time {
  margin: 0;
  font-size: 12px;
  color: var(--text-color-secondary);
}

.empty-collection {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400px;
}

@media (max-width: 768px) {
  .collection-layout {
    flex-direction: column;
  }

  .collection-sidebar {
    width: 100%;
  }

  .movie-grid {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
    gap: 16px;
  }
}
</style>
