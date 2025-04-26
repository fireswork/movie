<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

// 电影列表数据
const movieList = ref([]);

// 加载状态
const loading = ref(false);

// 筛选条件
const filters = reactive({
  title: '',
  category: undefined,
  year: undefined,
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

// 电影状态选项
const movieStatusOptions = [
  { label: '全部', value: '' },
  { label: '上映中', value: 'showing' },
  { label: '即将上映', value: 'upcoming' },
  { label: '已下架', value: 'ended' }
];

// 分类选项
const categoryOptions = [
  { label: '全部', value: '' },
  { label: '剧情', value: 'drama' },
  { label: '喜剧', value: 'comedy' },
  { label: '动作', value: 'action' },
  { label: '科幻', value: 'sci-fi' },
  { label: '恐怖', value: 'horror' },
  { label: '爱情', value: 'romance' },
  { label: '动画', value: 'animation' },
  { label: '战争', value: 'war' }
];

// 年份选项
const yearOptions = [
  { label: '全部', value: '' },
  { label: '2023', value: '2023' },
  { label: '2022', value: '2022' },
  { label: '2021', value: '2021' },
  { label: '2020', value: '2020' },
  { label: '2019', value: '2019' },
  { label: '2018', value: '2018' },
  { label: '2017', value: '2017' },
  { label: '2016', value: '2016' },
  { label: '2015', value: '2015' },
  { label: '2010-2014', value: '2010-2014' },
  { label: '2000-2009', value: '2000-2009' },
  { label: '90年代', value: '1990-1999' },
  { label: '80年代', value: '1980-1989' },
  { label: '更早', value: 'earlier' }
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
    title: '电影封面',
    dataIndex: 'poster',
    key: 'poster',
    width: 100,
    customRender: ({ text }) => {
      return text ? <img src={text} alt="海报" style="width: 50px; height: 70px; object-fit: cover;" /> : '无封面';
    }
  },
  {
    title: '电影名称',
    dataIndex: 'title',
    key: 'title',
    width: 200
  },
  {
    title: '导演',
    dataIndex: 'director',
    key: 'director',
    width: 150
  },
  {
    title: '主演',
    dataIndex: 'actors',
    key: 'actors',
    width: 200,
    customRender: ({ text }) => {
      if (Array.isArray(text)) {
        return text.join('、');
      }
      return text;
    }
  },
  {
    title: '分类',
    dataIndex: 'category',
    key: 'category',
    width: 150,
    customRender: ({ text }) => {
      if (Array.isArray(text)) {
        return text.join('、');
      }
      return text;
    }
  },
  {
    title: '上映年份',
    dataIndex: 'year',
    key: 'year',
    width: 100
  },
  {
    title: '评分',
    dataIndex: 'rating',
    key: 'rating',
    width: 100
  },
  {
    title: '价格',
    dataIndex: 'price',
    key: 'price',
    width: 100,
    customRender: ({ text }) => {
      return `¥${text}`;
    }
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 120,
    customRender: ({ text }) => {
      const statusColors = {
        showing: 'success',
        upcoming: 'processing',
        ended: 'default'
      };
      
      const statusTexts = {
        showing: '上映中',
        upcoming: '即将上映',
        ended: '已下架'
      };
      
      return (
        <a-tag color={statusColors[text]}>
          {statusTexts[text] || text}
        </a-tag>
      );
    }
  },
  {
    title: '操作',
    key: 'action',
    fixed: 'right',
    width: 250
  }
];

// 获取电影列表数据
const fetchMovies = () => {
  loading.value = true;
  
  // 模拟API请求
  setTimeout(() => {
    // 生成模拟数据
    const mockData = [];
    const total = 105; // 模拟总数
    
    for (let i = 1; i <= total; i++) {
      const statusOptions = ['showing', 'upcoming', 'ended'];
      const status = statusOptions[Math.floor(Math.random() * statusOptions.length)];
      
      const categories = [];
      const categoryCount = Math.floor(Math.random() * 3) + 1;
      for (let j = 0; j < categoryCount; j++) {
        const randomCategory = categoryOptions[Math.floor(Math.random() * (categoryOptions.length - 1)) + 1].value;
        if (!categories.includes(randomCategory)) {
          categories.push(randomCategory);
        }
      }
      
      const actors = ['张艺谋', '刘德华', '张曼玉', '周星驰', '葛优', '章子怡', '巩俐', '黄渤', '姜文', '周冬雨', '王宝强', '赵薇'];
      const randomActors = [];
      const actorCount = Math.floor(Math.random() * 3) + 2;
      for (let j = 0; j < actorCount; j++) {
        const randomActor = actors[Math.floor(Math.random() * actors.length)];
        if (!randomActors.includes(randomActor)) {
          randomActors.push(randomActor);
        }
      }
      
      const directors = ['张艺谋', '冯小刚', '姜文', '王家卫', '陈凯歌', '周星驰', '徐克', '贾樟柯'];
      const randomDirector = directors[Math.floor(Math.random() * directors.length)];
      
      const years = ['2023', '2022', '2021', '2020', '2019', '2018', '2017', '2016'];
      const randomYear = years[Math.floor(Math.random() * years.length)];
      
      // 设置随机海报
      const posterIndex = Math.floor(Math.random() * 10) + 1;
      const poster = `https://picsum.photos/id/${(i * 10) + posterIndex}/200/300`;
      
      mockData.push({
        id: i,
        title: `测试电影${i}`,
        director: randomDirector,
        actors: randomActors,
        category: categories,
        year: randomYear,
        rating: (Math.random() * 2 + 7).toFixed(1),
        price: Math.floor(Math.random() * 20) + 10,
        status,
        poster
      });
    }
    
    // 筛选逻辑
    let filteredData = [...mockData];
    
    if (filters.title) {
      filteredData = filteredData.filter(movie => movie.title.includes(filters.title));
    }
    
    if (filters.category) {
      filteredData = filteredData.filter(movie => movie.category.includes(filters.category));
    }
    
    if (filters.year) {
      filteredData = filteredData.filter(movie => movie.year === filters.year);
    }
    
    if (filters.status) {
      filteredData = filteredData.filter(movie => movie.status === filters.status);
    }
    
    // 分页
    const start = (pagination.current - 1) * pagination.pageSize;
    const end = start + pagination.pageSize;
    const paginatedData = filteredData.slice(start, end);
    
    movieList.value = paginatedData;
    pagination.total = filteredData.length;
    loading.value = false;
  }, 800);
};

// 表格分页、排序、筛选变化时的回调
const handleTableChange = (pag) => {
  pagination.current = pag.current;
  pagination.pageSize = pag.pageSize;
  fetchMovies();
};

// 查询
const handleSearch = () => {
  pagination.current = 1;
  fetchMovies();
};

// 重置筛选
const handleReset = () => {
  filters.title = '';
  filters.category = undefined;
  filters.year = undefined;
  filters.status = undefined;
  pagination.current = 1;
  fetchMovies();
};

// 添加电影
const handleAdd = () => {
  router.push('/admin/movie/edit');
};

// 编辑电影
const handleEdit = (record) => {
  router.push(`/admin/movie/edit/${record.id}`);
};

// 删除电影
const handleDelete = (record) => {
  console.log('删除电影', record);
  // TODO: 实现删除电影功能
};

// 修改电影状态
const handleChangeStatus = (record, newStatus) => {
  console.log('修改电影状态', record, newStatus);
  // TODO: 实现修改电影状态功能
};

onMounted(() => {
  fetchMovies();
});
</script>

<template>
  <div class="movies-container page-container">
    <div class="content-card">
      <div class="movies-header">
        <div class="header-title">
          <h2>电影管理</h2>
        </div>
        <div class="header-actions">
          <a-button type="primary" @click="handleAdd">
            <template #icon><plus-outlined /></template>
            添加电影
          </a-button>
        </div>
      </div>
      
      <!-- 筛选表单 -->
      <div class="filter-form">
        <a-form layout="inline">
          <a-form-item label="电影名称">
            <a-input v-model:value="filters.title" placeholder="请输入电影名称" allowClear />
          </a-form-item>
          
          <a-form-item label="分类">
            <a-select
              v-model:value="filters.category"
              placeholder="请选择分类"
              style="width: 120px"
              allowClear
              :options="categoryOptions"
            />
          </a-form-item>
          
          <a-form-item label="年份">
            <a-select
              v-model:value="filters.year"
              placeholder="请选择年份"
              style="width: 120px"
              allowClear
              :options="yearOptions"
            />
          </a-form-item>
          
          <a-form-item label="状态">
            <a-select
              v-model:value="filters.status"
              placeholder="请选择状态"
              style="width: 120px"
              allowClear
              :options="movieStatusOptions"
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
      
      <!-- 电影表格 -->
      <a-table
        :columns="columns"
        :data-source="movieList"
        :loading="loading"
        :pagination="pagination"
        @change="handleTableChange"
        row-key="id"
        scroll={{ x: 1300 }}
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="() => handleEdit(record)">编辑</a-button>
              
              <a-dropdown>
                <a-button type="link" size="small">
                  状态
                  <down-outlined />
                </a-button>
                <template #overlay>
                  <a-menu>
                    <a-menu-item @click="() => handleChangeStatus(record, 'showing')">
                      <check-circle-outlined style="color: #52c41a; margin-right: 8px;" />
                      上映中
                    </a-menu-item>
                    <a-menu-item @click="() => handleChangeStatus(record, 'upcoming')">
                      <clock-circle-outlined style="color: #1890ff; margin-right: 8px;" />
                      即将上映
                    </a-menu-item>
                    <a-menu-item @click="() => handleChangeStatus(record, 'ended')">
                      <stop-outlined style="color: #bfbfbf; margin-right: 8px;" />
                      已下架
                    </a-menu-item>
                  </a-menu>
                </template>
              </a-dropdown>
              
              <a-popconfirm
                title="确定要删除这部电影吗?"
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
.movies-header {
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

.movies-container :deep(.ant-table-body) {
  overflow-x: auto !important;
}

@media (max-width: 768px) {
  .movies-header {
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