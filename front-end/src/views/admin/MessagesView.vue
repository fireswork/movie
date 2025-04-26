<script setup lang="jsx">
import { ref, reactive, onMounted } from 'vue';
import { message, notification } from 'ant-design-vue';

// 留言列表数据
const messageList = ref([]);
// 加载状态
const loading = ref(false);
// 回复抽屉状态
const drawerVisible = ref(false);
// 当前选中的留言
const currentMessage = ref(null);
// 回复内容
const replyContent = ref('');
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
  { label: '待处理', value: 'pending' },
  { label: '已回复', value: 'replied' },
  { label: '已关闭', value: 'closed' }
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
    title: '用户名',
    dataIndex: 'username',
    key: 'username',
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
        pending: { text: '待处理', color: 'orange' },
        replied: { text: '已回复', color: 'green' },
        closed: { text: '已关闭', color: 'gray' }
      };
      const status = statusMap[text] || { text, color: 'default' };
      return <a-tag color={status.color}>{status.text}</a-tag>;
    }
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
    width: 180,
  },
  {
    title: '操作',
    key: 'action',
    fixed: 'right',
    width: 160,
  }
];

// 获取留言列表数据
const fetchMessages = () => {
  loading.value = true;
  
  // 模拟API请求
  setTimeout(() => {
    // 生成模拟数据
    const mockMessages = [];
    const total = 153; // 模拟总数
    
    const users = Array.from({ length: 20 }, (_, i) => ({
      id: i + 1,
      username: `user${i + 1}`,
      nickname: `用户${i + 1}`,
    }));
    
    for (let i = 1; i <= total; i++) {
      const statusOptions = ['pending', 'replied', 'closed'];
      const status = statusOptions[Math.floor(Math.random() * statusOptions.length)];
      
      const typeOptions = ['suggestion', 'content', 'technical', 'other'];
      const type = typeOptions[Math.floor(Math.random() * typeOptions.length)];
      
      const user = users[Math.floor(Math.random() * users.length)];
      
      const messageTemplates = [
        '希望能增加更多的电影分类，比如体育类电影专区',
        '最近上映的某某电影质量不太好，希望能提高上架电影的筛选标准',
        '网站加载速度有些慢，希望能优化一下',
        '能否增加更多支付方式，比如支持支付宝',
        '页面上的部分链接点击无反应',
        '希望能增加电影推荐功能，根据我看过的电影推荐新片',
        '有些电影描述与实际内容不符，请审核',
        '能否增加电影预告片功能',
        '希望可以增加电影评分排行榜功能',
        '个人中心页面布局有点混乱，希望优化'
      ];
      
      const randomMessage = messageTemplates[Math.floor(Math.random() * messageTemplates.length)];
      
      // 随机生成一个月内的时间
      const date = new Date();
      date.setDate(date.getDate() - Math.floor(Math.random() * 30));
      const createTime = date.toISOString().replace('T', ' ').substring(0, 19);
      
      // 如果状态为已回复，生成回复内容和时间
      let replyContent = null;
      let replyTime = null;
      
      if (status === 'replied') {
        const replyTemplates = [
          '感谢您的反馈，我们会认真考虑您的建议',
          '您好，我们已经收到您的反馈，技术团队正在处理相关问题',
          '非常感谢您的建议，我们会在下一版本中考虑添加该功能',
          '您反馈的问题已经修复，请刷新页面后重试',
          '我们已经将您的意见转达给相关部门，感谢您的支持'
        ];
        
        replyContent = replyTemplates[Math.floor(Math.random() * replyTemplates.length)];
        
        // 回复时间比创建时间晚1-3天
        const replyDate = new Date(date);
        replyDate.setDate(replyDate.getDate() + Math.floor(Math.random() * 3) + 1);
        replyTime = replyDate.toISOString().replace('T', ' ').substring(0, 19);
      }
      
      mockMessages.push({
        id: i,
        userId: user.id,
        username: user.username,
        content: randomMessage,
        type,
        status,
        createTime,
        replyContent,
        replyTime
      });
    }
    
    // 筛选逻辑
    let filteredData = [...mockMessages];
    
    if (filters.content) {
      filteredData = filteredData.filter(item => item.content.includes(filters.content));
    }
    
    if (filters.type) {
      filteredData = filteredData.filter(item => item.type === filters.type);
    }
    
    if (filters.status) {
      filteredData = filteredData.filter(item => item.status === filters.status);
    }
    
    if (filters.username) {
      filteredData = filteredData.filter(item => item.username.includes(filters.username));
    }
    
    if (filters.timeRange && filters.timeRange.length === 2) {
      const startDate = filters.timeRange[0].format('YYYY-MM-DD') + ' 00:00:00';
      const endDate = filters.timeRange[1].format('YYYY-MM-DD') + ' 23:59:59';
      
      filteredData = filteredData.filter(item => 
        item.createTime >= startDate && item.createTime <= endDate
      );
    }
    
    // 按创建时间倒序排序
    filteredData.sort((a, b) => new Date(b.createTime) - new Date(a.createTime));
    
    // 分页
    const start = (pagination.current - 1) * pagination.pageSize;
    const end = start + pagination.pageSize;
    const paginatedData = filteredData.slice(start, end);
    
    messageList.value = paginatedData;
    pagination.total = filteredData.length;
    loading.value = false;
  }, 800);
};

// 映射留言类型到显示文本
const messageTypeText = (type) => {
  const map = {
    suggestion: '功能建议',
    content: '内容反馈',
    technical: '技术问题',
    other: '其他'
  };
  return map[type] || type;
};

// 表格分页、排序、筛选变化时的回调
const handleTableChange = (pag) => {
  pagination.current = pag.current;
  pagination.pageSize = pag.pageSize;
  fetchMessages();
};

// 查询
const handleSearch = () => {
  pagination.current = 1;
  fetchMessages();
};

// 重置筛选
const handleReset = () => {
  filters.content = '';
  filters.type = undefined;
  filters.status = undefined;
  filters.username = '';
  filters.timeRange = [];
  pagination.current = 1;
  fetchMessages();
};

// 查看留言详情并回复
const handleReply = (record) => {
  currentMessage.value = record;
  replyContent.value = record.replyContent || '';
  drawerVisible.value = true;
};

// 关闭留言详情抽屉
const onDrawerClose = () => {
  drawerVisible.value = false;
  currentMessage.value = null;
  replyContent.value = '';
};

// 提交回复
const submitReply = () => {
  if (!replyContent.value.trim()) {
    message.error('请输入回复内容');
    return;
  }
  
  replying.value = true;
  
  // 模拟API请求
  setTimeout(() => {
    notification.success({
      message: '回复成功',
      description: `已成功回复用户 ${currentMessage.value.username} 的留言`
    });
    
    // 关闭抽屉
    drawerVisible.value = false;
    currentMessage.value = null;
    replyContent.value = '';
    replying.value = false;
    
    // 刷新留言列表
    fetchMessages();
  }, 1000);
};

// 关闭留言
const handleClose = (record) => {
  // 模拟API请求
  setTimeout(() => {
    message.success('留言已关闭');
    fetchMessages();
  }, 500);
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
          
          <a-form-item label="用户名">
            <a-input v-model:value="filters.username" placeholder="请输入用户名" allowClear />
          </a-form-item>
          
          <a-form-item label="留言时间">
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
              >
                {{ record.status === 'replied' ? '查看回复' : '回复' }}
              </a-button>
              
              <a-popconfirm
                v-if="record.status !== 'closed'"
                title="确定要关闭这条留言吗?"
                ok-text="是"
                cancel-text="否"
                @confirm="() => handleClose(record)"
              >
                <a-button type="link" danger size="small">关闭</a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
      
      <!-- 回复抽屉 -->
      <a-drawer
        :title="`留言详情 - ${currentMessage?.username || ''}`"
        width="500"
        :visible="drawerVisible"
        @close="onDrawerClose"
        :body-style="{ paddingBottom: '80px' }"
      >
        <template v-if="currentMessage">
          <a-descriptions bordered :column="1">
            <a-descriptions-item label="留言类型">
              {{ messageTypeText(currentMessage.type) }}
            </a-descriptions-item>
            <a-descriptions-item label="留言内容">
              {{ currentMessage.content }}
            </a-descriptions-item>
            <a-descriptions-item label="留言时间">
              {{ currentMessage.createTime }}
            </a-descriptions-item>
            <a-descriptions-item label="状态">
              <a-tag :color="
                currentMessage.status === 'pending' ? 'orange' : 
                currentMessage.status === 'replied' ? 'green' : 'gray'
              ">
                {{ 
                  currentMessage.status === 'pending' ? '待处理' : 
                  currentMessage.status === 'replied' ? '已回复' : '已关闭'
                }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item v-if="currentMessage.replyTime" label="回复时间">
              {{ currentMessage.replyTime }}
            </a-descriptions-item>
          </a-descriptions>
          
          <div class="reply-form">
            <div class="reply-title">
              <span>回复内容</span>
              <a-tag 
                v-if="currentMessage.status === 'closed'" 
                color="gray"
              >
                已关闭，无法回复
              </a-tag>
            </div>
            <a-textarea
              v-model:value="replyContent"
              placeholder="请输入回复内容..."
              :rows="4"
              :disabled="currentMessage.status === 'closed'"
            />
            
            <div class="drawer-footer">
              <a-button style="margin-right: 8px" @click="onDrawerClose">取消</a-button>
              <a-button 
                type="primary" 
                :loading="replying"
                @click="submitReply"
                :disabled="currentMessage.status === 'closed'"
              >
                提交回复
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