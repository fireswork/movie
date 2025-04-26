<script setup lang="jsx">
import { ref, reactive, onMounted } from 'vue';

// 订单列表数据
const orderList = ref([]);

// 加载状态
const loading = ref(false);

// 订单详情抽屉
const drawerVisible = ref(false);
const currentOrder = ref(null);

// 筛选条件
const filters = reactive({
  orderNo: '',
  movieName: '',
  username: '',
  status: undefined,
  timeRange: []
});

// 分页参数
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  pageSizeOptions: ['10', '20', '50', '100']
});

// 订单状态选项
const orderStatusOptions = [
  { label: '全部', value: '' },
  { label: '已支付', value: 'paid' },
  { label: '已取消', value: 'cancelled' },
  { label: '已退款', value: 'refunded' }
];

// 支付方式选项
const paymentMethods = {
  'alipay': '支付宝',
  'wechat': '微信支付',
  'creditcard': '银行卡'
};

// 表格列定义
const columns = [
  {
    title: '订单号',
    dataIndex: 'orderNo',
    key: 'orderNo',
    width: 180
  },
  {
    title: '用户名',
    dataIndex: 'username',
    key: 'username',
    width: 120
  },
  {
    title: '电影名称',
    dataIndex: 'movieName',
    key: 'movieName',
    width: 200
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
    title: '支付方式',
    dataIndex: 'paymentMethod',
    key: 'paymentMethod',
    width: 120,
    customRender: ({ text }) => {
      return paymentMethods[text] || text;
    }
  },
  {
    title: '订单状态',
    dataIndex: 'status',
    key: 'status',
    width: 120,
    customRender: ({ text }) => {
      const statusColors = {
        paid: 'success',
        cancelled: 'default',
        refunded: 'warning'
      };
      
      const statusTexts = {
        paid: '已支付',
        cancelled: '已取消',
        refunded: '已退款'
      };
      
      return (
        <a-tag color={statusColors[text]}>
          {statusTexts[text] || text}
        </a-tag>
      );
    }
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
    width: 180
  },
  {
    title: '操作',
    key: 'action',
    fixed: 'right',
    width: 180
  }
];

// 获取订单列表数据
const fetchOrders = () => {
  loading.value = true;
  
  // 模拟API请求
  setTimeout(() => {
    // 生成模拟数据
    const mockData = [];
    const total = 128; // 模拟总数
    
    const movieNames = [
      '肖申克的救赎',
      '霸王别姬',
      '阿甘正传',
      '泰坦尼克号',
      '这个杀手不太冷',
      '美丽人生',
      '千与千寻',
      '辛德勒的名单',
      '盗梦空间',
      '星际穿越'
    ];
    
    const usernames = Array.from({ length: 20 }, (_, i) => `用户${i + 1}`);
    
    for (let i = 1; i <= total; i++) {
      const statusOptions = ['paid', 'cancelled', 'refunded'];
      const status = statusOptions[Math.floor(Math.random() * statusOptions.length)];
      
      const paymentMethodOptions = ['alipay', 'wechat', 'creditcard'];
      const paymentMethod = paymentMethodOptions[Math.floor(Math.random() * paymentMethodOptions.length)];
      
      const randomMovieName = movieNames[Math.floor(Math.random() * movieNames.length)];
      const randomUsername = usernames[Math.floor(Math.random() * usernames.length)];
      
      // 随机金额，10-50之间
      const amount = Math.floor(Math.random() * 40) + 10;
      
      // 随机生成订单号
      const orderNo = `ORD${Date.now().toString().substring(5)}${Math.floor(Math.random() * 1000).toString().padStart(3, '0')}`;
      
      // 随机生成1-3个月内的时间
      const date = new Date();
      date.setMonth(date.getMonth() - Math.floor(Math.random() * 3));
      date.setDate(Math.floor(Math.random() * 28) + 1);
      date.setHours(Math.floor(Math.random() * 24));
      date.setMinutes(Math.floor(Math.random() * 60));
      const createTime = date.toISOString().replace('T', ' ').substring(0, 19);
      
      // 支付时间稍晚于创建时间
      const payTime = new Date(date.getTime() + Math.floor(Math.random() * 300000)); // 0-5分钟后
      const paymentTime = payTime.toISOString().replace('T', ' ').substring(0, 19);
      
      mockData.push({
        id: i,
        orderNo,
        userId: Math.floor(Math.random() * 100) + 1,
        username: randomUsername,
        movieId: Math.floor(Math.random() * 100) + 1,
        movieName: randomMovieName,
        amount,
        paymentMethod,
        status,
        createTime,
        paymentTime: status !== 'cancelled' ? paymentTime : null,
        refundTime: status === 'refunded' ? new Date(payTime.getTime() + 86400000 + Math.floor(Math.random() * 86400000)).toISOString().replace('T', ' ').substring(0, 19) : null // 退款时间，支付后1-2天
      });
    }
    
    // 筛选逻辑
    let filteredData = [...mockData];
    
    if (filters.orderNo) {
      filteredData = filteredData.filter(order => order.orderNo.includes(filters.orderNo));
    }
    
    if (filters.movieName) {
      filteredData = filteredData.filter(order => order.movieName.includes(filters.movieName));
    }
    
    if (filters.username) {
      filteredData = filteredData.filter(order => order.username.includes(filters.username));
    }
    
    if (filters.status) {
      filteredData = filteredData.filter(order => order.status === filters.status);
    }
    
    if (filters.timeRange && filters.timeRange.length === 2) {
      const startDate = filters.timeRange[0].format('YYYY-MM-DD') + ' 00:00:00';
      const endDate = filters.timeRange[1].format('YYYY-MM-DD') + ' 23:59:59';
      
      filteredData = filteredData.filter(order => 
        order.createTime >= startDate && order.createTime <= endDate
      );
    }
    
    // 按创建时间倒序排序
    filteredData.sort((a, b) => new Date(b.createTime) - new Date(a.createTime));
    
    // 分页
    const start = (pagination.current - 1) * pagination.pageSize;
    const end = start + pagination.pageSize;
    const paginatedData = filteredData.slice(start, end);
    
    orderList.value = paginatedData;
    pagination.total = filteredData.length;
    loading.value = false;
  }, 800);
};

// 表格分页、排序、筛选变化时的回调
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
  filters.orderNo = '';
  filters.movieName = '';
  filters.username = '';
  filters.status = undefined;
  filters.timeRange = [];
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
};

// 导出订单数据
const handleExport = () => {
  console.log('导出订单数据');
  // TODO: 实现导出订单数据功能
};

// 退款订单
const handleRefund = (record) => {
  console.log('退款订单：', record);
  // TODO: 实现退款订单功能
  fetchOrders();
};

onMounted(() => {
  fetchOrders();
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
          <a-button type="primary" @click="handleExport">
            <template #icon><download-outlined /></template>
            导出数据
          </a-button>
        </div>
      </div>
      
      <!-- 筛选表单 -->
      <div class="filter-form">
        <a-form layout="inline">
          <a-form-item label="订单号">
            <a-input v-model:value="filters.orderNo" placeholder="请输入订单号" allowClear />
          </a-form-item>
          
          <a-form-item label="电影名称">
            <a-input v-model:value="filters.movieName" placeholder="请输入电影名称" allowClear />
          </a-form-item>
          
          <a-form-item label="用户名">
            <a-input v-model:value="filters.username" placeholder="请输入用户名" allowClear />
          </a-form-item>
          
          <a-form-item label="订单状态">
            <a-select
              v-model:value="filters.status"
              placeholder="请选择状态"
              style="width: 120px"
              allowClear
              :options="orderStatusOptions"
            />
          </a-form-item>
          
          <a-form-item label="订单时间">
            <a-range-picker v-model:value="filters.timeRange" style="width: 250px" />
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
        scroll={{ x: 1200 }}
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="() => handleViewOrder(record)">
                查看详情
              </a-button>
              
              <template v-if="record.status === 'paid'">
                <a-popconfirm
                  title="确定要对此订单进行退款吗?"
                  ok-text="是"
                  cancel-text="否"
                  @confirm="() => handleRefund(record)"
                >
                  <a-button type="link" danger size="small">退款</a-button>
                </a-popconfirm>
              </template>
            </a-space>
          </template>
        </template>
      </a-table>
      
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
            <a-descriptions-item label="订单号">{{ currentOrder.orderNo }}</a-descriptions-item>
            <a-descriptions-item label="用户名">{{ currentOrder.username }}</a-descriptions-item>
            <a-descriptions-item label="电影名称">{{ currentOrder.movieName }}</a-descriptions-item>
            <a-descriptions-item label="订单金额">¥{{ currentOrder.amount.toFixed(2) }}</a-descriptions-item>
            <a-descriptions-item label="支付方式">{{ paymentMethods[currentOrder.paymentMethod] }}</a-descriptions-item>
            <a-descriptions-item label="订单状态">
              <a-tag 
                :color="currentOrder.status === 'paid' ? 'success' : currentOrder.status === 'refunded' ? 'warning' : 'default'"
              >
                {{ currentOrder.status === 'paid' ? '已支付' : currentOrder.status === 'refunded' ? '已退款' : '已取消' }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="创建时间">{{ currentOrder.createTime }}</a-descriptions-item>
            <a-descriptions-item v-if="currentOrder.paymentTime" label="支付时间">
              {{ currentOrder.paymentTime }}
            </a-descriptions-item>
            <a-descriptions-item v-if="currentOrder.refundTime" label="退款时间">
              {{ currentOrder.refundTime }}
            </a-descriptions-item>
          </a-descriptions>
          
          <div class="action-buttons" v-if="currentOrder.status === 'paid'">
            <a-popconfirm
              title="确定要对此订单进行退款吗?"
              ok-text="是"
              cancel-text="否"
              @confirm="() => handleRefund(currentOrder)"
            >
              <a-button type="primary" danger>退款</a-button>
            </a-popconfirm>
          </div>
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

.action-buttons {
  margin-top: 24px;
  text-align: right;
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