<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

// 影院列表数据
const cinemaList = ref([]);

// 加载状态
const loading = ref(false);

// 筛选条件
const filters = reactive({
  name: '',
  city: undefined,
  district: undefined,
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

// 影院状态选项
const cinemaStatusOptions = [
  { label: '全部', value: '' },
  { label: '营业中', value: 'operating' },
  { label: '临时关闭', value: 'closed' },
  { label: '即将开业', value: 'coming' }
];

// 城市选项
const cityOptions = [
  { label: '全部', value: '' },
  { label: '北京', value: 'beijing' },
  { label: '上海', value: 'shanghai' },
  { label: '广州', value: 'guangzhou' },
  { label: '深圳', value: 'shenzhen' },
  { label: '成都', value: 'chengdu' },
  { label: '杭州', value: 'hangzhou' },
  { label: '武汉', value: 'wuhan' },
  { label: '南京', value: 'nanjing' },
  { label: '西安', value: 'xian' }
];

// 区域选项 (动态基于城市选择)
const districtOptions = reactive({
  beijing: [
    { label: '全部', value: '' },
    { label: '朝阳区', value: 'chaoyang' },
    { label: '海淀区', value: 'haidian' },
    { label: '东城区', value: 'dongcheng' },
    { label: '西城区', value: 'xicheng' },
    { label: '丰台区', value: 'fengtai' }
  ],
  shanghai: [
    { label: '全部', value: '' },
    { label: '浦东新区', value: 'pudong' },
    { label: '静安区', value: 'jingan' },
    { label: '徐汇区', value: 'xuhui' },
    { label: '长宁区', value: 'changning' },
    { label: '黄浦区', value: 'huangpu' }
  ],
  guangzhou: [
    { label: '全部', value: '' },
    { label: '天河区', value: 'tianhe' },
    { label: '海珠区', value: 'haizhu' },
    { label: '越秀区', value: 'yuexiu' },
    { label: '白云区', value: 'baiyun' },
    { label: '番禺区', value: 'panyu' }
  ],
  shenzhen: [
    { label: '全部', value: '' },
    { label: '南山区', value: 'nanshan' },
    { label: '福田区', value: 'futian' },
    { label: '罗湖区', value: 'luohu' },
    { label: '宝安区', value: 'baoan' },
    { label: '龙岗区', value: 'longgang' }
  ]
});

// 当前可用的区域选项
const currentDistrictOptions = ref([{ label: '全部', value: '' }]);

// 监听城市变化，更新区域选项
const handleCityChange = (value) => {
  filters.district = undefined;
  if (value && districtOptions[value]) {
    currentDistrictOptions.value = districtOptions[value];
  } else {
    currentDistrictOptions.value = [{ label: '全部', value: '' }];
  }
};

// 表格列定义
const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    key: 'id',
    width: 80
  },
  {
    title: '影院名称',
    dataIndex: 'name',
    key: 'name',
    width: 220
  },
  {
    title: '所在城市',
    dataIndex: 'city',
    key: 'city',
    width: 120,
    customRender: ({ text }) => {
      const city = cityOptions.find(city => city.value === text);
      return city ? city.label : text;
    }
  },
  {
    title: '所在区域',
    dataIndex: 'district',
    key: 'district',
    width: 120,
    customRender: ({ text, record }) => {
      if (record.city && districtOptions[record.city]) {
        const district = districtOptions[record.city].find(district => district.value === text);
        return district ? district.label : text;
      }
      return text;
    }
  },
  {
    title: '详细地址',
    dataIndex: 'address',
    key: 'address',
    width: 250
  },
  {
    title: '联系电话',
    dataIndex: 'phone',
    key: 'phone',
    width: 150
  },
  {
    title: '影厅数量',
    dataIndex: 'screenCount',
    key: 'screenCount',
    width: 100
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 120,
    customRender: ({ text }) => {
      const statusColors = {
        operating: 'success',
        closed: 'default',
        coming: 'processing'
      };
      
      const statusTexts = {
        operating: '营业中',
        closed: '临时关闭',
        coming: '即将开业'
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

// 获取影院列表数据
const fetchCinemas = () => {
  loading.value = true;
  
  // 模拟API请求
  setTimeout(() => {
    // 生成模拟数据
    const mockData = [];
    const total = 86; // 模拟总数
    
    for (let i = 1; i <= total; i++) {
      const statusOptions = ['operating', 'closed', 'coming'];
      const status = statusOptions[Math.floor(Math.random() * statusOptions.length)];
      
      const cityIndex = Math.floor(Math.random() * (cityOptions.length - 1)) + 1;
      const city = cityOptions[cityIndex].value;
      
      let district = '';
      if (districtOptions[city]) {
        const districtIndex = Math.floor(Math.random() * (districtOptions[city].length - 1)) + 1;
        district = districtOptions[city][districtIndex].value;
      }
      
      const prefixes = ['华谊兄弟', '万达', 'CGV', '金逸', '博纳', 'UME', '星美', '大地', '横店', '橙天嘉禾'];
      const suffix = ['影城', '影院', '国际影城', 'IMAX影院', '电影城'];
      
      const prefix = prefixes[Math.floor(Math.random() * prefixes.length)];
      const suf = suffix[Math.floor(Math.random() * suffix.length)];
      const cityName = cityOptions.find(c => c.value === city)?.label || '';
      const districtName = district ? (districtOptions[city]?.find(d => d.value === district)?.label || '') : '';
      
      const name = `${prefix}${cityName}${districtName}${suf}`;
      
      // 生成随机的详细地址
      const buildings = ['购物中心', '广场', '大厦', '商业街', '生活中心', '商场'];
      const building = buildings[Math.floor(Math.random() * buildings.length)];
      const floor = Math.floor(Math.random() * 5) + 1;
      const address = `${cityName}${districtName}${Math.floor(Math.random() * 100) + 1}号${building}${floor}楼`;
      
      // 生成随机的电话号码
      const phone = `${['010', '021', '020', '0755'][Math.floor(Math.random() * 4)]}-${Math.floor(Math.random() * 90000000) + 10000000}`;
      
      // 生成随机的影厅数量
      const screenCount = Math.floor(Math.random() * 10) + 5;
      
      mockData.push({
        id: i,
        name,
        city,
        district,
        address,
        phone,
        screenCount,
        status
      });
    }
    
    // 筛选逻辑
    let filteredData = [...mockData];
    
    if (filters.name) {
      filteredData = filteredData.filter(cinema => cinema.name.includes(filters.name));
    }
    
    if (filters.city) {
      filteredData = filteredData.filter(cinema => cinema.city === filters.city);
    }
    
    if (filters.district) {
      filteredData = filteredData.filter(cinema => cinema.district === filters.district);
    }
    
    if (filters.status) {
      filteredData = filteredData.filter(cinema => cinema.status === filters.status);
    }
    
    // 分页
    const start = (pagination.current - 1) * pagination.pageSize;
    const end = start + pagination.pageSize;
    const paginatedData = filteredData.slice(start, end);
    
    cinemaList.value = paginatedData;
    pagination.total = filteredData.length;
    loading.value = false;
  }, 800);
};

// 表格分页、排序、筛选变化时的回调
const handleTableChange = (pag) => {
  pagination.current = pag.current;
  pagination.pageSize = pag.pageSize;
  fetchCinemas();
};

// 查询
const handleSearch = () => {
  pagination.current = 1;
  fetchCinemas();
};

// 重置筛选
const handleReset = () => {
  filters.name = '';
  filters.city = undefined;
  filters.district = undefined;
  filters.status = undefined;
  currentDistrictOptions.value = [{ label: '全部', value: '' }];
  pagination.current = 1;
  fetchCinemas();
};

// 添加影院
const handleAdd = () => {
  router.push('/admin/cinema/edit');
};

// 编辑影院
const handleEdit = (record) => {
  router.push(`/admin/cinema/edit/${record.id}`);
};

// 查看影厅
const handleViewScreens = (record) => {
  router.push(`/admin/cinema/screens/${record.id}`);
};

// 删除影院
const handleDelete = (record) => {
  console.log('删除影院', record);
  // TODO: 实现删除影院功能
};

// 修改影院状态
const handleChangeStatus = (record, newStatus) => {
  console.log('修改影院状态', record, newStatus);
  // TODO: 实现修改影院状态功能
};

onMounted(() => {
  fetchCinemas();
});
</script>

<template>
  <div class="cinemas-container page-container">
    <div class="content-card">
      <div class="cinemas-header">
        <div class="header-title">
          <h2>影院管理</h2>
        </div>
        <div class="header-actions">
          <a-button type="primary" @click="handleAdd">
            <template #icon><plus-outlined /></template>
            添加影院
          </a-button>
        </div>
      </div>
      
      <!-- 筛选表单 -->
      <div class="filter-form">
        <a-form layout="inline">
          <a-form-item label="影院名称">
            <a-input v-model:value="filters.name" placeholder="请输入影院名称" allowClear />
          </a-form-item>
          
          <a-form-item label="城市">
            <a-select
              v-model:value="filters.city"
              placeholder="请选择城市"
              style="width: 120px"
              allowClear
              :options="cityOptions"
              @change="handleCityChange"
            />
          </a-form-item>
          
          <a-form-item label="区域">
            <a-select
              v-model:value="filters.district"
              placeholder="请选择区域"
              style="width: 120px"
              allowClear
              :options="currentDistrictOptions"
              :disabled="!filters.city"
            />
          </a-form-item>
          
          <a-form-item label="状态">
            <a-select
              v-model:value="filters.status"
              placeholder="请选择状态"
              style="width: 120px"
              allowClear
              :options="cinemaStatusOptions"
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
      
      <!-- 影院表格 -->
      <a-table
        :columns="columns"
        :data-source="cinemaList"
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
              
              <a-button type="link" size="small" @click="() => handleViewScreens(record)">
                查看影厅
              </a-button>
              
              <a-dropdown>
                <a-button type="link" size="small">
                  状态
                  <down-outlined />
                </a-button>
                <template #overlay>
                  <a-menu>
                    <a-menu-item @click="() => handleChangeStatus(record, 'operating')">
                      <check-circle-outlined style="color: #52c41a; margin-right: 8px;" />
                      营业中
                    </a-menu-item>
                    <a-menu-item @click="() => handleChangeStatus(record, 'closed')">
                      <stop-outlined style="color: #bfbfbf; margin-right: 8px;" />
                      临时关闭
                    </a-menu-item>
                    <a-menu-item @click="() => handleChangeStatus(record, 'coming')">
                      <clock-circle-outlined style="color: #1890ff; margin-right: 8px;" />
                      即将开业
                    </a-menu-item>
                  </a-menu>
                </template>
              </a-dropdown>
              
              <a-popconfirm
                title="确定要删除这家影院吗?"
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
.cinemas-header {
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

.cinemas-container :deep(.ant-table-body) {
  overflow-x: auto !important;
}

@media (max-width: 768px) {
  .cinemas-header {
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