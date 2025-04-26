<script setup lang="jsx">
import { ref, reactive, onMounted } from 'vue';

// 评论列表数据
const commentList = ref([]);

// 加载状态
const loading = ref(false);

// 筛选条件
const filters = reactive({
  content: '',
  movieName: '',
  username: '',
  status: undefined
});

// 分页参数
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  pageSizeOptions: ['10', '20', '50', '100']
});

// 评论状态选项
const commentStatusOptions = [
  { label: '全部', value: '' },
  { label: '已通过', value: 'approved' },
  { label: '待审核', value: 'pending' },
  { label: '已拒绝', value: 'rejected' }
];

// 模拟电影列表
const movies = [
  { id: 1, title: '肖申克的救赎' },
  { id: 2, title: '霸王别姬' },
  { id: 3, title: '阿甘正传' },
  { id: 4, title: '泰坦尼克号' },
  { id: 5, title: '这个杀手不太冷' },
  { id: 6, title: '美丽人生' },
  { id: 7, title: '千与千寻' },
  { id: 8, title: '辛德勒的名单' },
  { id: 9, title: '盗梦空间' },
  { id: 10, title: '星际穿越' }
];

// 模拟用户列表
const users = [
  { id: 1, username: '用户1' },
  { id: 2, username: '用户2' },
  { id: 3, username: '用户3' },
  { id: 4, username: '用户4' },
  { id: 5, username: '用户5' },
  { id: 6, username: '用户6' },
  { id: 7, username: '用户7' },
  { id: 8, username: '用户8' },
  { id: 9, username: '用户9' },
  { id: 10, username: '用户10' }
];

// 表格列定义
const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    key: 'id',
    width: 80
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
    width: 150
  },
  {
    title: '评论内容',
    dataIndex: 'content',
    key: 'content',
    ellipsis: true
  },
  {
    title: '评分',
    dataIndex: 'rating',
    key: 'rating',
    width: 80,
    customRender: ({ text }) => {
      // 使用星星图标显示评分
      const fullStars = Math.floor(text);
      const halfStar = text % 1 >= 0.5;
      const emptyStars = 5 - fullStars - (halfStar ? 1 : 0);
      
      return (
        <div>
          {Array(fullStars).fill().map((_, i) => (
            <star-filled key={`full-${i}`} style="color: #fadb14; margin-right: 2px;" />
          ))}
          {halfStar && <star-half-filled style="color: #fadb14; margin-right: 2px;" />}
          {Array(emptyStars).fill().map((_, i) => (
            <star-outlined key={`empty-${i}`} style="color: #fadb14; margin-right: 2px;" />
          ))}
          <span style="margin-left: 4px;">{text}</span>
        </div>
      );
    }
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 100,
    customRender: ({ text }) => {
      const statusColors = {
        approved: 'success',
        pending: 'processing',
        rejected: 'error'
      };
      
      const statusTexts = {
        approved: '已通过',
        pending: '待审核',
        rejected: '已拒绝'
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

// 获取评论列表数据
const fetchComments = () => {
  loading.value = true;
  
  // 模拟API请求
  setTimeout(() => {
    // 生成模拟数据
    const mockData = [];
    const total = 152; // 模拟总数
    
    for (let i = 1; i <= total; i++) {
      const statusOptions = ['approved', 'pending', 'rejected'];
      const status = statusOptions[Math.floor(Math.random() * statusOptions.length)];
      
      const randomMovieIndex = Math.floor(Math.random() * movies.length);
      const movie = movies[randomMovieIndex];
      
      const randomUserIndex = Math.floor(Math.random() * users.length);
      const user = users[randomUserIndex];
      
      const randomRating = (Math.random() * 2 + 3).toFixed(1); // 3.0-5.0之间的随机评分
      
      const commentTemplates = [
        '这部电影真的很棒，剧情紧凑，演员表演出色！',
        '画面很美，配乐也很棒，但是剧情有点拖沓。',
        '非常感人的一部电影，看完之后久久不能平静。',
        '演员的表演非常到位，特别是主角，演技炸裂。',
        '这部电影节奏有点慢，但是细节处理得很好。',
        '剧情设计很巧妙，结局也很惊喜，推荐观看。',
        '特效做得很棒，但是故事情节有点薄弱。',
        '配乐非常出色，为电影增色不少。',
        '这部电影给我带来了很多思考，值得一看。',
        '角色刻画得很深入，能让人产生共鸣。'
      ];
      
      const randomComment = commentTemplates[Math.floor(Math.random() * commentTemplates.length)];
      
      // 随机生成1-3个月内的时间
      const date = new Date();
      date.setMonth(date.getMonth() - Math.floor(Math.random() * 3));
      date.setDate(Math.floor(Math.random() * 28) + 1);
      date.setHours(Math.floor(Math.random() * 24));
      date.setMinutes(Math.floor(Math.random() * 60));
      const createTime = date.toISOString().replace('T', ' ').substring(0, 19);
      
      mockData.push({
        id: i,
        userId: user.id,
        username: user.username,
        movieId: movie.id,
        movieName: movie.title,
        content: randomComment,
        rating: parseFloat(randomRating),
        status,
        createTime
      });
    }
    
    // 筛选逻辑
    let filteredData = [...mockData];
    
    if (filters.content) {
      filteredData = filteredData.filter(comment => comment.content.includes(filters.content));
    }
    
    if (filters.movieName) {
      filteredData = filteredData.filter(comment => comment.movieName.includes(filters.movieName));
    }
    
    if (filters.username) {
      filteredData = filteredData.filter(comment => comment.username.includes(filters.username));
    }
    
    if (filters.status) {
      filteredData = filteredData.filter(comment => comment.status === filters.status);
    }
    
    // 分页
    const start = (pagination.current - 1) * pagination.pageSize;
    const end = start + pagination.pageSize;
    const paginatedData = filteredData.slice(start, end);
    
    commentList.value = paginatedData;
    pagination.total = filteredData.length;
    loading.value = false;
  }, 800);
};

// 表格分页、排序、筛选变化时的回调
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
  filters.content = '';
  filters.movieName = '';
  filters.username = '';
  filters.status = undefined;
  pagination.current = 1;
  fetchComments();
};

// 查看评论详情
const handleView = (record) => {
  console.log('查看评论详情：', record);
  // TODO: 实现查看评论详情功能
};

// 审核评论
const handleApprove = (record) => {
  console.log('审核通过评论：', record);
  // TODO: 实现审核通过评论功能
  fetchComments();
};

// 拒绝评论
const handleReject = (record) => {
  console.log('拒绝评论：', record);
  // TODO: 实现拒绝评论功能
  fetchComments();
};

// 删除评论
const handleDelete = (record) => {
  console.log('删除评论：', record);
  // TODO: 实现删除评论功能
  fetchComments();
};

onMounted(() => {
  fetchComments();
});
</script>

<template>
  <div class="comments-container page-container">
    <div class="content-card">
      <div class="comments-header">
        <h2>评论管理</h2>
      </div>
      
      <!-- 筛选表单 -->
      <div class="filter-form">
        <a-form layout="inline">
          <a-form-item label="评论内容">
            <a-input v-model:value="filters.content" placeholder="请输入评论内容" allowClear />
          </a-form-item>
          
          <a-form-item label="电影名称">
            <a-input v-model:value="filters.movieName" placeholder="请输入电影名称" allowClear />
          </a-form-item>
          
          <a-form-item label="用户名">
            <a-input v-model:value="filters.username" placeholder="请输入用户名" allowClear />
          </a-form-item>
          
          <a-form-item label="状态">
            <a-select
              v-model:value="filters.status"
              placeholder="请选择状态"
              style="width: 120px"
              allowClear
              :options="commentStatusOptions"
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
      
      <!-- 评论表格 -->
      <a-table
        :columns="columns"
        :data-source="commentList"
        :loading="loading"
        :pagination="pagination"
        @change="handleTableChange"
        row-key="id"
        scroll={{ x: 1200 }}
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="() => handleView(record)">查看</a-button>
              
              <template v-if="record.status === 'pending'">
                <a-button type="link" size="small" @click="() => handleApprove(record)">通过</a-button>
                <a-button type="link" danger size="small" @click="() => handleReject(record)">拒绝</a-button>
              </template>
              
              <a-popconfirm
                title="确定要删除这条评论吗?"
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
    </div>
  </div>
</template>

<style scoped>
.comments-header {
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

@media (max-width: 768px) {
  .filter-form :deep(.ant-form-item) {
    margin-bottom: 16px;
  }
}
</style> 