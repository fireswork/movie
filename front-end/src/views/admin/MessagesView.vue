<script setup lang="jsx">
import { ref, reactive, onMounted } from 'vue';
import { message, notification } from 'ant-design-vue';
import axios from '@/services/request';
import dayjs from 'dayjs';

// 留言列表数据
const messageList = ref([]);
// 加载状态
const loading = ref(false);
// 回复抽屉状态
const drawerVisible = ref(false);
// 当前选中的留言
const currentMessage = ref(null);
// 回复提交状态
const replying = ref(false);

// 筛选条件
const filters = reactive({
  content: '',
  type: undefined,
  status: undefined,
  username: '',
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

// 留言类型选项
const messageTypeOptions = [
  { label: '全部', value: '' },
  { label: '功能建议', value: 'suggestion' },
  { label: '内容反馈', value: 'content' },
  { label: '技术问题', value: 'technical' },
  { label: '其他', value: 'other' }
];

// 留言状态选项
const statusOptions = [
  { label: '全部', value: '' },
  { label: '待处理', value: 'PENDING' },
  { label: '已处理', value: 'PROCESSED' },
  { label: '已关闭', value: 'CLOSED' }
];

// 表格列定义
const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    key: 'id',
    width: 80,
  },
  {
    title: '用户ID',
    dataIndex: 'userId',
    key: 'userId',
    width: 120,
  },
  {
    title: '留言类型',
    dataIndex: 'type',
    key: 'type',
    width: 120,
    customRender: ({ text }) => messageTypeText(text)
  },
  {
    title: '留言内容',
    dataIndex: 'content',
    key: 'content',
    ellipsis: true
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 100,
    customRender: ({ text }) => {
      const statusMap = {
        PENDING: { text: '待处理', color: 'orange' },
        PROCESSED: { text: '已处理', color: 'green' },
        CLOSED: { text: '已关闭', color: 'gray' }
      };
      const status = statusMap[text] || { text, color: 'default' };
      return <a-tag color={status.color}>{status.text}</a-tag>;
    }
  },
  {
    title: '创建时间',
    dataIndex: 'createdAt',
    key: 'createdAt',
    width: 180,
    customRender: ({ text }) => dayjs(text).format('YYYY-MM-DD HH:mm'),
  },
  {
    title: '操作',
    key: 'action',
    fixed: 'right',
    width: 160,
  }
];

// 映射留言类型到显示文本
const messageTypeText = (type) => {
  const map = {
    suggestion: '功能建议',
    content: '内容反馈',
    technical: '技术问题',
    other: '其他'
  }
  return map[type] || type
}

// 获取留言列表数据
const fetchMessages = async () => {
  loading.value = true;
  try {
    const { data } = await axios.get('/messages/admin/all');
    const filteredData = filterMessages(data);
    messageList.value = filteredData;
    pagination.total = filteredData.length;
  } catch (error) {
    message.error('获取留言列表失败');
    console.error('获取留言列表失败:', error);
  } finally {
    loading.value = false;
  }
};

// 前端搜索过滤
const filterMessages = (data) => {
  return data.filter(item => {
    // 按内容搜索
    if (filters.content && !item.content.toLowerCase().includes(filters.content.toLowerCase())) {
      return false;
    }
    // 按类型筛选
    if (filters.type && item.type !== filters.type) {
      return false;
    }
    // 按状态筛选
    if (filters.status && item.status !== filters.status) {
      return false;
    }
    return true;
  });
};

// 查询
const handleSearch = () => {
  fetchMessages();
};

// 重置筛选
const handleReset = () => {
  filters.content = '';
  filters.type = undefined;
  filters.status = undefined;
  fetchMessages();
};

// 查看留言详情并回复
const handleReply = (record) => {
  currentMessage.value = record;
  drawerVisible.value = true;
};

// 关闭留言详情抽屉
const onDrawerClose = () => {
  drawerVisible.value = false;
  currentMessage.value = null;
};

// 提交回复
const submitReply = async () => {
  if (!currentMessage.value) return;
  
  replying.value = true;
  try {
    await axios.put(`/messages/${currentMessage.value.id}/status`, null, {
      params: { status: 'PROCESSED' }
    });

    notification.success({
        message: '回复成功',
        description: `已成功处理留言 #${currentMessage.value.id}`
      });
      
      // 关闭抽屉
      drawerVisible.value = false;
      currentMessage.value = null;
      
      // 刷新留言列表
      fetchMessages();
  } catch (error) {
    message.error('回复失败');
    console.error('回复失败:', error);
  } finally {
    replying.value = false;
  }
};

// 关闭留言
const handleClose = async (record) => {
  try {
   await axios.put(`/messages/${record.id}/status`, null, {
      params: { status: 'CLOSED' }
    });

    message.success('留言已关闭');
    fetchMessages();
  } catch (error) {
    message.error('关闭留言失败');
    console.error('关闭留言失败:', error);
  }
};

// 删除留言
const handleDelete = async (record) => {
  await axios.delete(`/messages/${record.id}`);
  fetchMessages();
};

onMounted(() => {
  fetchMessages();
});
</script>

<template>
  <div class="messages-container page-container">
    <div class="content-card">
      <div class="messages-header">
        <div class="header-title">
          <h2>留言管理</h2>
        </div>
      </div>
      
      <!-- 筛选表单 -->
      <div class="filter-form">
        <a-form layout="inline">
          <a-form-item label="留言内容">
            <a-input v-model:value="filters.content" placeholder="请输入留言内容" allowClear />
          </a-form-item>
          
          <a-form-item label="留言类型">
            <a-select
              v-model:value="filters.type"
              placeholder="请选择留言类型"
              style="width: 120px"
              allowClear
              :options="messageTypeOptions"
            />
          </a-form-item>
          
          <a-form-item label="状态">
            <a-select
              v-model:value="filters.status"
              placeholder="请选择状态"
              style="width: 120px"
              allowClear
              :options="statusOptions"
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
      
      <!-- 留言表格 -->
      <a-table
        :columns="columns"
        :data-source="messageList"
        :loading="loading"
        :pagination="pagination"
        @change="handleTableChange"
        row-key="id"
        scroll={{ x: 1200 }}
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'action'">
            <a-space>
              <a-button 
                type="link" 
                size="small" 
                @click="() => handleReply(record)"
                v-if="record.status === 'PENDING'"
                :disabled="record.status === 'CLOSED'"
              >
                {{ record.status === 'PROCESSED' ? '已处理' : '处理' }}
              </a-button>
              
              <a-popconfirm
                v-if="record.status !== 'CLOSED'"
                title="确定要关闭这条留言吗?"
                ok-text="是"
                cancel-text="否"
                @confirm="() => handleClose(record)"
              >
                <a-button type="link" danger size="small">关闭</a-button>
              </a-popconfirm>

              <a-popconfirm
                v-if="record.status === 'CLOSED'"
                title="确定要删除这条留言吗?"
                ok-text="是"
                cancel-text="否"
                @confirm="() => handleDelete(record)"
              >
                <a-button type="link" danger size="small">删除</a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
      
      <!-- 留言详情抽屉 -->
      <a-drawer
        :title="`留言详情 #${currentMessage?.id || ''}`"
        width="500"
        :visible="drawerVisible"
        @close="onDrawerClose"
        :body-style="{ paddingBottom: '80px' }"
      >
        <template v-if="currentMessage">
          <a-descriptions bordered :column="1">
            <a-descriptions-item label="用户ID">
              {{ currentMessage.userId }}
            </a-descriptions-item>
            <a-descriptions-item label="留言类型">
              {{ messageTypeText(currentMessage.type) }}
            </a-descriptions-item>
            <a-descriptions-item label="留言内容">
              {{ currentMessage.content }}
            </a-descriptions-item>
            <a-descriptions-item label="留言时间">
              {{ currentMessage.createdAt }}
            </a-descriptions-item>
            <a-descriptions-item label="状态">
              <a-tag :color="
                currentMessage.status === 'PENDING' ? 'orange' : 
                currentMessage.status === 'PROCESSED' ? 'green' : 'gray'
              ">
                {{ 
                  currentMessage.status === 'PENDING' ? '待处理' : 
                  currentMessage.status === 'PROCESSED' ? '已处理' : '已关闭'
                }}
              </a-tag>
            </a-descriptions-item>
          </a-descriptions>
          
          <div class="reply-form">
            <div class="reply-title">
              <span>处理留言</span>
              <a-tag 
                v-if="currentMessage.status === 'CLOSED'" 
                color="gray"
              >
                已关闭，无法处理
              </a-tag>
            </div>
            
            <div class="drawer-footer">
              <a-button style="margin-right: 8px" @click="onDrawerClose">取消</a-button>
              <a-button 
                type="primary" 
                :loading="replying"
                @click="submitReply"
                :disabled="currentMessage.status !== 'PENDING'"
              >
                标记为已处理
              </a-button>
            </div>
          </div>
        </template>
      </a-drawer>
    </div>
  </div>
</template>

<style scoped>
.messages-header {
  margin-bottom: 24px;
}

.filter-form {
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px dashed #f0f0f0;
}

.reply-form {
  margin-top: 24px;
}

.reply-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  font-weight: bold;
}

.drawer-footer {
  position: absolute;
  bottom: 0;
  width: 100%;
  border-top: 1px solid #e9e9e9;
  padding: 10px 16px;
  text-align: right;
  left: 0;
  background: #fff;
}

@media (max-width: 768px) {
  .filter-form :deep(.ant-form-item) {
    margin-bottom: 16px;
  }
}
</style> 