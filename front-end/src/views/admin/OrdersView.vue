<script setup lang="jsx">
import { ref, reactive, onMounted } from 'vue';
import axios from '@/services/request';
import { message } from 'ant-design-vue';
import dayjs from 'dayjs';

// 订单列表数据
const orderList = ref([]);

// 加载状态
const loading = ref(false);

const users = ref([]);

const movies = ref([]);

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
    title: '用户ID',
    dataIndex: 'userId',
    key: 'userId',
    width: 100
  },
  {
    title: '电影ID',
    dataIndex: 'movieId',
    key: 'movieId',
    width: 100
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
      return text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : '-';
    }
  },
  {
    title: '支付时间',
    dataIndex: 'paidAt',
    key: 'paidAt',
    width: 180,
    customRender: ({ text }) => {
      return text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : '-';
    }
  },
  {
    title: '操作',
    key: 'action',
    fixed: 'right',
    width: 120,
    customRender: ({ record }) => (
      <a-space>
        <a-button type="link" size="small" onClick={() => handleViewOrder(record)}>
          查看详情
        </a-button>
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

onMounted(() => {
  fetchOrders();
  getMovies()
  getUsers()
});
</script>

<template>
  <div class="orders-container page-container">
    <div class="content-card">
      <div class="orders-header">
        <div class="header-title">
          <h2>订单管理</h2>
        </div>
      </div>
      
      <!-- 筛选表单 -->
      <div class="filter-form">
        <a-form layout="inline">
          <a-form-item label="用户ID">
            <a-input-number
              v-model:value="filters.userId"
              placeholder="请输入用户ID"
              style="width: 160px"
            />
          </a-form-item>
          
          <a-form-item label="电影ID">
            <a-input-number
              v-model:value="filters.movieId"
              placeholder="请输入电影ID"
              style="width: 160px"
            />
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
            <a-descriptions-item label="用户ID">{{ currentOrder.userId }}</a-descriptions-item>
            <a-descriptions-item label="电影ID">{{ currentOrder.movieId }}</a-descriptions-item>
            <a-descriptions-item label="订单金额">¥{{ currentOrder.amount.toFixed(2) }}</a-descriptions-item>
            <a-descriptions-item label="创建时间">
              {{ currentOrder.createdAt ? dayjs(currentOrder.createdAt).format('YYYY-MM-DD HH:mm:ss') : '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="支付时间">
              {{ currentOrder.paidAt ? dayjs(currentOrder.paidAt).format('YYYY-MM-DD HH:mm:ss') : '-' }}
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