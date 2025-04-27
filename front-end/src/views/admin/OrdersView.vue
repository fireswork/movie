<script setup lang="jsx">
import { ref, reactive, onMounted, watch } from 'vue';
import axios from '@/services/request';
import { message } from 'ant-design-vue';
import dayjs from 'dayjs';

// 订单列表数据
const orderList = ref([]);

// 加载状态
const loading = ref(false);

const users = ref([]);
const movies = ref([]);

// 新增/编辑对话框
const modalVisible = ref(false);
const modalTitle = ref('新增订单');
const modalForm = reactive({
  id: null,
  userId: undefined,
  movieId: undefined,
  amount: 0,
  status: 'PENDING'
});

// 监听选择的电影变化，自动设置价格
watch(() => modalForm.movieId, async (newMovieId) => {
  if (newMovieId) {
    try {
      const { data } = await axios.get(`/movies/${newMovieId}`);
      if (data.isFree) {
        message.error('免费电影不能创建订单');
        modalForm.movieId = undefined;
        modalForm.amount = 0;
        return;
      }
      modalForm.amount = data.price;
    } catch (error) {
      message.error('获取电影价格失败');
      console.error('获取电影价格失败:', error);
    }
  } else {
    modalForm.amount = 0;
  }
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

// 订单详情抽屉
const drawerVisible = ref(false);
const currentOrder = ref(null);

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
    title: '订单金额',
    dataIndex: 'amount',
    key: 'amount',
    width: 100,
    customRender: ({ text }) => {
      return `¥${text.toFixed(2)}`;
    }
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
    title: '支付时间',
    dataIndex: 'createdAt',
    key: 'createdAt',
    width: 180,
    customRender: ({ text }) => {
      return text ? dayjs(text).format('YYYY-MM-DD HH:mm') : '-';
    }
  },
  {
    title: '操作',
    key: 'action',
    fixed: 'right',
    width: 200,
    customRender: ({ record }) => (
      <a-space>
        <a-button type="link" size="small" onClick={() => handleViewOrder(record)}>
          查看详情
        </a-button>
        {/* <a-button type="link" size="small" onClick={() => handleEdit(record)}>
          编辑
        </a-button> */}
        <a-popconfirm
          title="确定要删除这个订单吗？"
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

// 获取订单列表数据
const fetchOrders = async () => {
  loading.value = true;
  try {
    const { data } = await axios.get('/orders', {
      params: {
        ...filters,
        page: pagination.current - 1,
        size: pagination.pageSize
      }
    });
    orderList.value = data.content;
    pagination.total = data.totalElements;
  } catch (error) {
    message.error('获取订单列表失败');
    console.error('获取订单列表失败:', error);
  } finally {
    loading.value = false;
  }
};

// 表格分页变化时的回调
const handleTableChange = (pag) => {
  pagination.current = pag.current;
  pagination.pageSize = pag.pageSize;
  fetchOrders();
};

// 查询
const handleSearch = () => {
  pagination.current = 1;
  fetchOrders();
};

// 重置筛选
const handleReset = () => {
  filters.userId = undefined;
  filters.movieId = undefined;
  pagination.current = 1;
  fetchOrders();
};

// 查看订单详情
const handleViewOrder = (record) => {
  currentOrder.value = record;
  drawerVisible.value = true;
};

// 关闭订单详情抽屉
const onDrawerClose = () => {
  drawerVisible.value = false;
  currentOrder.value = null;
};

// 删除订单
const handleDelete = async (id) => {
  try {
    await axios.delete(`/orders/${id}`);
    message.success('删除成功');
    fetchOrders();
  } catch (error) {
    message.error('删除失败');
    console.error('删除失败:', error);
  }
};

// 打开新增对话框
const handleAdd = () => {
  modalTitle.value = '新增订单';
  Object.assign(modalForm, {
    id: null,
    userId: undefined,
    movieId: undefined,
    amount: 0,
    status: 'PENDING'
  });
  modalVisible.value = true;
};

// 打开编辑对话框
const handleEdit = (record) => {
  modalTitle.value = '编辑订单';
  Object.assign(modalForm, {
    id: record.id,
    userId: record.userId,
    movieId: record.movieId,
    amount: record.amount,
    status: record.status
  });
  modalVisible.value = true;
};

// 关闭对话框
const handleModalCancel = () => {
  modalVisible.value = false;
  Object.assign(modalForm, {
    id: null,
    userId: undefined,
    movieId: undefined,
    amount: 0,
    status: 'PENDING'
  });
};

// 提交表单
const handleModalOk = async () => {
  if (!modalForm.userId) {
    message.error('请选择用户');
    return;
  }
  if (!modalForm.movieId) {
    message.error('请选择电影');
    return;
  }

  try {
    if (modalForm.id) {
      // 编辑
      await axios.put(`/orders/${modalForm.id}`, modalForm);
      message.success('编辑成功');
    } else {
      // 新增
      await axios.post('/orders', modalForm);
      message.success('新增成功');
    }
    modalVisible.value = false;
    fetchOrders();
  } catch (error) {
    message.error(modalForm.id ? '编辑失败' : '新增失败');
    console.error(modalForm.id ? '编辑失败:' : '新增失败:', error);
  }
};

onMounted(() => {
  fetchOrders();
  getMovies();
  getUsers();
});
</script>

<template>
  <div class="orders-container page-container">
    <div class="content-card">
      <div class="orders-header">
        <div class="header-title">
          <h2>订单管理</h2>
        </div>
        <div class="header-actions">
          <a-button type="primary" @click="handleAdd">
            新增订单
          </a-button>
        </div>
      </div>
      
      <!-- 筛选表单 -->
      <div class="filter-form">
        <a-form layout="inline">
          <a-form-item label="选择用户">
            <a-select
              v-model:value="filters.userId"
              style="width: 160px"
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
              style="width: 160px"
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
      
      <!-- 订单表格 -->
      <a-table
        :columns="columns"
        :data-source="orderList"
        :loading="loading"
        :pagination="pagination"
        @change="handleTableChange"
        row-key="id"
        scroll={{ x: 1000 }}
      />
      
      <!-- 新增/编辑对话框 -->
      <a-modal
        :title="modalTitle"
        :visible="modalVisible"
        @ok="handleModalOk"
        @cancel="handleModalCancel"
        :confirmLoading="loading"
      >
        <a-form :model="modalForm" layout="vertical">
          <a-form-item 
            label="选择用户" 
            required
            :rules="[{ required: true, message: '请选择用户' }]"
          >
            <a-select
              v-model:value="modalForm.userId"
              style="width: 100%"
              placeholder="请选择用户"
              :disabled="!!modalForm.id"
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
              v-model:value="modalForm.movieId"
              style="width: 100%"
              placeholder="请选择电影"
              :disabled="!!modalForm.id"
            >
              <a-select-option v-for="movie in movies" :key="movie.value" :value="movie.value">
                {{ movie.label }}
              </a-select-option>
            </a-select>
          </a-form-item>
          
          <a-form-item label="订单金额">
            <a-input-number
              v-model:value="modalForm.amount"
              :min="0"
              :max="1000"
              :precision="2"
              style="width: 100%"
              disabled
              addon-before="¥"
            />
          </a-form-item>
        </a-form>
      </a-modal>

      <!-- 订单详情抽屉 -->
      <a-drawer
        title="订单详情"
        :width="500"
        :visible="drawerVisible"
        @close="onDrawerClose"
        :body-style="{ paddingBottom: '80px' }"
      >
        <template v-if="currentOrder">
          <a-descriptions bordered :column="1">
            <a-descriptions-item label="订单ID">{{ currentOrder.id }}</a-descriptions-item>
            <a-descriptions-item label="用户">{{ users?.find(item => item.value === currentOrder.userId)?.label }}</a-descriptions-item>
            <a-descriptions-item label="电影">{{ movies?.find(item => item.value === currentOrder.movieId)?.label }}</a-descriptions-item>
            <a-descriptions-item label="订单金额">¥{{ currentOrder.amount.toFixed(2) }}</a-descriptions-item>
            <a-descriptions-item label="创建时间">
              {{ currentOrder.createdAt ? dayjs(currentOrder.createdAt).format('YYYY-MM-DD HH:mm') : '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="支付时间">
              {{ currentOrder.createdAt ? dayjs(currentOrder.createdAt).format('YYYY-MM-DD HH:mm') : '-' }}
            </a-descriptions-item>
          </a-descriptions>
        </template>
      </a-drawer>
    </div>
  </div>
</template>

<style scoped>
.orders-header {
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

.orders-container :deep(.ant-table-body) {
  overflow-x: auto !important;
}

@media (max-width: 768px) {
  .orders-header {
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