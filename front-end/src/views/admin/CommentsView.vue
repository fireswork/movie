<script setup lang="jsx">
import { ref, reactive, onMounted } from 'vue';
import axios from '@/services/request';
import { message } from 'ant-design-vue';
import dayjs from 'dayjs';

// 评论列表数据
const commentList = ref([]);

// 加载状态
const loading = ref(false);

const users = ref([]);

const movies = ref([]);

// 评论详情抽屉
const drawerVisible = ref(false);
const currentComment = ref(null);

// 编辑评论对话框
const editModalVisible = ref(false);
const editForm = reactive({
  id: null,
  userId: null,
  movieId: null,
  comment: '',
  rating: null
});

// 筛选条件
const filters = reactive({
  userId: undefined,
  movieId: undefined
});

// 分页参数
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  pageSizeOptions: ['10', '20', '50', '100']
});

// 新增评论对话框
const addModalVisible = ref(false);
const addForm = reactive({
  userId: null,
  movieId: null,
  comment: '',
  rating: null,
  liked: false
});

const getUsers = async () => {
  const { data } = await axios.get('/users')
  users.value = data.content.map(item => ({
    label: item.username,
    value: item.id
  }))
}

const getMovies = async () => {
  const { data } = await axios.get('/movies')
  movies.value = data.map(item => ({
    label: item.title,
    value: item.id
  }))
}

// 表格列定义
const columns = [
  {
    title: '用户',
    dataIndex: 'userId',
    key: 'userId',
    width: 100,
    customRender: ({ text }) => {
      return users?.value?.find(item => item.value === text)?.label
    }
  },
  {
    title: '电影',
    dataIndex: 'movieId',
    key: 'movieId',
    width: 100,
    customRender: ({ text }) => {
      return movies?.value?.find(item => item.value === text)?.label
    }
  },
  {
    title: '评分',
    dataIndex: 'rating',
    key: 'rating',
    width: 100,
    customRender: ({ text }) => {
      return text ? `${text}分` : '-';
    }
  },
  {
    title: '评论内容',
    dataIndex: 'comment',
    key: 'comment',
    width: 300,
    ellipsis: true
  },
  {
    title: '创建时间',
    dataIndex: 'createdAt',
    key: 'createdAt',
    width: 180,
    customRender: ({ text }) => {
      return text ? dayjs(text).format('YYYY-MM-DD HH:mm') : '-';
    }
  },
  {
    title: '更新时间',
    dataIndex: 'updatedAt',
    key: 'updatedAt',
    width: 180,
    customRender: ({ text }) => {
      return text ? dayjs(text).format('YYYY-MM-DD HH:mm') : '-';
    }
  },
  {
    title: '操作',
    key: 'action',
    fixed: 'right',
    width: 180,
    customRender: ({ record }) => (
      <a-space>
        <a-button type="link" size="small" onClick={() => handleViewComment(record)}>
          查看
        </a-button>
        <a-button type="link" size="small" onClick={() => handleEdit(record)}>
          编辑
        </a-button>
        <a-popconfirm
          title="确定要删除这条评论吗？"
          ok-text="是"
          cancel-text="否"
          onConfirm={() => handleDelete(record.id)}
        >
          <a-button type="link" danger size="small">
            删除
          </a-button>
        </a-popconfirm>
      </a-space>
    )
  }
];

// 获取评论列表数据
const fetchComments = async () => {
  loading.value = true;
  try {
    const { data } = await axios.get('/movie-interactions', {
      params: {
        ...filters,
        page: pagination.current - 1,
        size: pagination.pageSize
      }
    });
    commentList.value = data.content;
    pagination.total = data.totalElements;
  } catch (error) {
    message.error('获取评论列表失败');
    console.error('获取评论列表失败:', error);
  } finally {
    loading.value = false;
  }
};

// 表格分页变化时的回调
const handleTableChange = (pag) => {
  pagination.current = pag.current;
  pagination.pageSize = pag.pageSize;
  fetchComments();
};

// 查询
const handleSearch = () => {
  pagination.current = 1;
  fetchComments();
};

// 重置筛选
const handleReset = () => {
  filters.userId = undefined;
  filters.movieId = undefined;
  pagination.current = 1;
  fetchComments();
};

// 查看评论详情
const handleViewComment = (record) => {
  currentComment.value = record;
  drawerVisible.value = true;
};

// 关闭评论详情抽屉
const onDrawerClose = () => {
  drawerVisible.value = false;
  currentComment.value = null;
};

// 打开编辑对话框
const handleEdit = (record) => {
  Object.assign(editForm, record);
  editModalVisible.value = true;
};

// 关闭编辑对话框
const onEditModalClose = () => {
  editModalVisible.value = false;
  Object.assign(editForm, {
    id: null,
    userId: null,
    movieId: null,
    comment: '',
    rating: null
  });
};

// 提交编辑
const handleSubmitEdit = async () => {
  if (!editForm.comment?.trim()) {
    message.error('评论内容不能为空');
    return;
  }
  if (editForm.rating && (editForm.rating < 1 || editForm.rating > 5)) {
    message.error('评分必须在1-5之间');
    return;
  }

  try {
    await axios.put(`/movie-interactions/user/${editForm.userId}/movie/${editForm.movieId}`, {
      comment: editForm.comment.trim(),
      rating: editForm.rating
    });
    message.success('更新成功');
    editModalVisible.value = false;
    fetchComments();
  } catch (error) {
    message.error('更新失败');
    console.error('更新失败:', error);
  }
};

// 删除评论
const handleDelete = async (id) => {
  try {
    await axios.delete(`/movie-interactions/${id}`);
    message.success('删除成功');
    fetchComments();
  } catch (error) {
    message.error('删除失败');
    console.error('删除失败:', error);
  }
};

// 打开新增对话框
const handleAdd = () => {
  addModalVisible.value = true;
};

// 关闭新增对话框
const onAddModalClose = () => {
  addModalVisible.value = false;
  Object.assign(addForm, {
    userId: null,
    movieId: null,
    comment: '',
    rating: null,
    liked: false
  });
};

// 提交新增
const handleSubmitAdd = async () => {
  if (!addForm.userId) {
    message.error('请输入用户ID');
    return;
  }
  if (!addForm.movieId) {
    message.error('请输入电影ID');
    return;
  }
  if (!addForm.comment?.trim()) {
    message.error('评论内容不能为空');
    return;
  }
  if (addForm.rating && (addForm.rating < 1 || addForm.rating > 5)) {
    message.error('评分必须在1-5之间');
    return;
  }

  try {
    await axios.put(`/movie-interactions/user/${addForm.userId}/movie/${addForm.movieId}`, {
      comment: addForm.comment.trim(),
      rating: addForm.rating,
      liked: addForm.liked
    });
    message.success('添加成功');
    addModalVisible.value = false;
    fetchComments();
  } catch (error) {
    message.error('添加失败');
    console.error('添加失败:', error);
  }
};

onMounted(() => {
  fetchComments();
  getMovies()
  getUsers()
});
</script>

<template>
  <div class="comments-container page-container">
    <div class="content-card">
      <div class="comments-header">
        <div class="header-title">
          <h2>评论管理</h2>
        </div>
        <div class="header-actions">
          <a-button type="primary" @click="handleAdd">
            新增评论
          </a-button>
        </div>
      </div>
      
      <!-- 筛选表单 -->
      <div class="filter-form">
        <a-form layout="inline">
          <a-form-item label="选择用户">
            <a-select
              v-model:value="filters.userId"
              placeholder="请选择用户"
            >
              <a-select-option v-for="user in users" :key="user.value" :value="user.value">
                {{ user.label }}
              </a-select-option>
            </a-select>
          </a-form-item>
      
          
          <a-form-item label="选择电影">
            <a-select
              v-model:value="filters.movieId"
              placeholder="请选择电影"
            >
              <a-select-option v-for="movie in movies" :key="movie.value" :value="movie.value">
                {{ movie.label }}
              </a-select-option>
            </a-select>
          </a-form-item>
        
          <a-form-item>
            <a-space>
              <a-button type="primary" @click="handleSearch">查询</a-button>
              <a-button @click="handleReset">重置</a-button>
            </a-space>
          </a-form-item>
        </a-form>
      </div>
      
      <!-- 评论表格 -->
      <a-table
        :columns="columns"
        :data-source="commentList"
        :loading="loading"
        :pagination="pagination"
        @change="handleTableChange"
        row-key="id"
        scroll={{ x: 1200 }}
      />
      
      <!-- 评论详情抽屉 -->
      <a-drawer
        title="评论详情"
        :width="500"
        :open="drawerVisible"
        @close="onDrawerClose"
        :body-style="{ paddingBottom: '80px' }"
      >
        <template v-if="currentComment">
          <a-descriptions bordered :column="1">
            <a-descriptions-item label="评论ID">{{ currentComment.id }}</a-descriptions-item>
            <a-descriptions-item label="用户">{{ currentComment.userId }}</a-descriptions-item>
            <a-descriptions-item label="电影">{{ currentComment.movieId }}</a-descriptions-item>
            <a-descriptions-item label="评分">{{ currentComment.rating ? `${currentComment.rating}分` : '-' }}</a-descriptions-item>
            <a-descriptions-item label="评论内容">{{ currentComment.comment || '-' }}</a-descriptions-item>
            <a-descriptions-item label="创建时间">
              {{ currentComment.createdAt ? dayjs(currentComment.createdAt).format('YYYY-MM-DD HH:mm') : '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="更新时间">
              {{ currentComment.updatedAt ? dayjs(currentComment.updatedAt).format('YYYY-MM-DD HH:mm') : '-' }}
            </a-descriptions-item>
          </a-descriptions>
        </template>
      </a-drawer>

      <!-- 编辑评论对话框 -->
      <a-modal
        title="编辑评论"
        :open="editModalVisible"
        @ok="handleSubmitEdit"
        @cancel="onEditModalClose"
        :confirmLoading="loading"
      >
        <a-form :model="editForm" layout="vertical">
          <a-form-item label="评分">
            <a-input-number
              v-model:value="editForm.rating"
              :min="1"
              :max="5"
              placeholder="请输入评分(1-5)"
              style="width: 100%"
            />
          </a-form-item>
          <a-form-item label="评论内容">
            <a-textarea
              v-model:value="editForm.comment"
              :rows="4"
              placeholder="请输入评论内容"
              :maxLength="1000"
              showCount
            />
          </a-form-item>
        </a-form>
      </a-modal>

      <!-- 新增评论对话框 -->
      <a-modal
        title="新增评论"
        :open="addModalVisible"
        @ok="handleSubmitAdd"
        @cancel="onAddModalClose"
        :confirmLoading="loading"
      >
        <a-form :model="addForm" layout="vertical">
          <a-form-item 
            label="选择用户" 
            required
            :rules="[{ required: true, message: '请选择用户' }]"
          >
          <a-select
              v-model:value="addForm.userId"
              placeholder="请选择用户"
            >
              <a-select-option v-for="user in users" :key="user.value" :value="user.value">
                {{ user.label }}
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item 
            label="选择电影"
            required
            :rules="[{ required: true, message: '请选择电影' }]"
          >
            <a-select
              v-model:value="addForm.movieId"
              placeholder="请选择电影"
            >
              <a-select-option v-for="movie in movies" :key="movie.value" :value="movie.value">
                {{ movie.label }}
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="评分">
            <a-input-number
              v-model:value="addForm.rating"
              :min="1"
              :max="5"
              placeholder="请输入评分(1-5)"
              style="width: 100%"
            />
          </a-form-item>
          <a-form-item 
            label="评论内容"
            required
            :rules="[{ required: true, message: '请输入评论内容' }]"
          >
            <a-textarea
              v-model:value="addForm.comment"
              :rows="4"
              placeholder="请输入评论内容"
              :maxLength="1000"
              showCount
            />
          </a-form-item>
          <a-form-item label="点赞状态">
            <a-switch v-model:checked="addForm.liked" />
          </a-form-item>
        </a-form>
      </a-modal>
    </div>
  </div>
</template>

<style scoped>
.comments-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.filter-form {
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px dashed #f0f0f0;
}

.comments-container :deep(.ant-table-body) {
  overflow-x: auto !important;
}

.ant-select {
  width: 100%;
}

@media (max-width: 768px) {
  .comments-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .header-actions {
    margin-top: 16px;
  }
  
  .filter-form :deep(.ant-form-item) {
    margin-bottom: 16px;
  }
}
</style> 