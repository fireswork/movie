<script setup lang="jsx">
import { ref, onMounted } from 'vue';
import { message } from 'ant-design-vue';
import { PlusOutlined } from '@ant-design/icons-vue';
import request from '@/services/request';

const movieList = ref([]);
const loading = ref(false);
const modalVisible = ref(false);
const confirmLoading = ref(false);
const isEdit = ref(false);
const formRef = ref(null);
const categoryOptions = ref([]);
const regionOptions = ref([]);

// 筛选条件
const filters = ref({
  title: '',
  categoryId: undefined,
  regionId: undefined,
  year: undefined
});

const formState = ref({
  id: undefined,
  title: '',
  coverBase64: '',
  categories: [],
  region: undefined,
  year: undefined,
  duration: undefined,
  rating: undefined,
  isFree: false,
  price: undefined,
  actors: '',
  trailerUrl: '',
  playCount: 0
});

/**
 * @typedef {Object} UploadFile
 * @property {string} uid
 * @property {string} name
 * @property {string} [url]
 */

/** @type {import('vue').Ref<UploadFile[]>} */
const fileList = ref([]);

const rules = {
  title: [{ required: true, message: '请输入电影名称' }],
  coverBase64: [{ required: true, message: '请上传封面图' }],
  categories: [{ required: true, message: '请选择分类' }],
  region: [{ required: true, message: '请选择地区' }],
  year: [{ required: true, message: '请输入上映年份' }],
  duration: [{ required: true, message: '请输入电影时长' }],
  rating: [{ required: true, message: '请输入评分' }],
  actors: [{ required: true, message: '请输入主演' }],
  price: [{ 
    required: true, 
    message: '请输入价格',
    validator: (rule, value) => {
      if (!formState.value.isFree && (!value || value <= 0)) {
        return Promise.reject('付费电影必须设置价格')
      }
      return Promise.resolve()
    }
  }],
  trailerUrl: [{ required: true, message: '请输入预告片链接' }]
};

const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    key: 'id',
    width: 80
  },
  {
    title: '封面',
    dataIndex: 'coverBase64',
    key: 'cover',
    width: 100
  },
  {
    title: '电影名称',
    dataIndex: 'title',
    key: 'title'
  },
  {
    title: '主演',
    dataIndex: 'actors',
    key: 'actors',
    ellipsis: true
  },
  {
    title: '分类',
    dataIndex: 'categories',
    key: 'categories',
    customRender: ({ text }) => {
      return categoryOptions.value.filter(c => text.includes(String(c.value))).map(c => c.label).join('/');
    }
  },
  {
    title: '地区',
    dataIndex: 'region',
    key: 'region',
    customRender: ({ text }) => {
      return regionOptions.value.find(r => r.value === text)?.label || '未知';
    }
  },
  {
    title: '年份',
    dataIndex: 'year',
    key: 'year'
  },
  {
    title: '时长',
    dataIndex: 'duration',
    key: 'duration'
  },
  {
    title: '评分',
    dataIndex: 'rating',
    key: 'rating'
  },
  {
    title: '价格',
    dataIndex: 'price',
    key: 'price'
  },
  {
    title: '播放次数',
    dataIndex: 'playCount',
    key: 'playCount',
    width: 100,
  },
  {
    title: '操作',
    key: 'action',
    fixed: 'right',
    width: 150
  }
];

const yearOptions = Array.from({ length: 2024 - 1900 + 1 }, (_, i) => ({
  label: `${1900 + i}`,
  value: 1900 + i
})).reverse();

const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0
});

// 获取电影列表
const fetchMovies = async () => {
  const { data } = await request.get('/movies');
  movieList.value = data;
  pagination.value.total = data.length;
};

// 获取分类列表
const fetchCategories = async () => {
  const { data } = await request.get('/categories');
  categoryOptions.value = data.map(item => ({
    label: item.name,
    value: item.id
  }));
};

// 获取地区列表
const fetchRegions = async () => {
  const { data } = await request.get('/regions');
  regionOptions.value = data.map(item => ({
    label: item.name,
    value: item.id
  }));
};

const beforeUpload = (file) => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => {
      formState.value.coverBase64 = reader.result;
      resolve(false);
    };
    reader.onerror = (error) => {
      reject(error);
    };
  });
};

const resetForm = () => {
  formState.value.id = null;
  formState.value.title = '';
  formState.value.coverBase64 = '';
  formState.value.categories = [];
  formState.value.region = undefined;
  formState.value.year = new Date().getFullYear();
  formState.value.duration = 90;
  formState.value.rating = 0;
  formState.value.isFree = false;
  formState.value.price = 0;
  formState.value.actors = '';
  formState.value.trailerUrl = '';
  formState.value.playCount = 0;
};

const handleAdd = () => {
  isEdit.value = false;
  resetForm();
  modalVisible.value = true;
};

const handleEdit = (record) => {
  isEdit.value = true;
  Object.assign(formState.value, {
    ...record,
    categories: record.categories.split(',').map(Number),
    region: record.region
  });
  modalVisible.value = true;
};

const handleSubmit = async () => {
  try {
    await formRef.value.validate();
    confirmLoading.value = true;

    const data = {
      ...formState.value,
      price: formState.value.isFree ? 0 : formState.value.price
    };

    if (isEdit.value) {
      await request.put(`/movies/${formState.value.id}`, {
        ...data,
        categories: String(data.categories)
      });
      message.success('更新成功');
    } else {
      await request.post('/movies', {
        ...data,
        categories: String(data.categories)
      });
      message.success('创建成功');
    }

    modalVisible.value = false;
    fetchMovies();
  } catch (error) {
    console.error('操作失败:', error);
  } finally {
    confirmLoading.value = false;
  }
};

const handleDelete = async (record) => {
  try {
    await request.delete(`/movies/${record.id}`);
    message.success('删除成功');
    fetchMovies();
  } catch (error) {
    message.error('删除失败');
  }
};

const handleSearch = () => {
  pagination.value.current = 1;
  fetchMovies();
};

const handleReset = () => {
  filters.value.title = '';
  filters.value.categoryId = undefined;
  filters.value.regionId = undefined;
  filters.value.year = undefined;
  pagination.value.current = 1;
  fetchMovies();
};

const closeModal = () => {
  modalVisible.value = false;
  formRef.value?.resetFields();
};

const handleTableChange = (pag) => {
  pagination.value.current = pag.current;
  pagination.value.pageSize = pag.pageSize;
  fetchMovies();
};

onMounted(() => {
  fetchMovies();
  fetchCategories();
  fetchRegions();
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
              v-model:value="filters.categoryId"
              placeholder="请选择分类"
              style="width: 120px"
              allowClear
              :options="categoryOptions"
            />
          </a-form-item>
          
          <a-form-item label="地区">
            <a-select
              v-model:value="filters.regionId"
              placeholder="请选择地区"
              style="width: 120px"
              allowClear
              :options="regionOptions"
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
        :scroll="{ x: 1300 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'cover'">
            <img 
              v-if="record.coverBase64" 
              :src="record.coverBase64" 
              alt="封面" 
              style="width: 50px; height: 70px; object-fit: cover;"
            />
            <span v-else>无封面</span>
          </template>
          <template v-if="column.key === 'duration'">
            {{ record.duration }}分钟
          </template>
          <template v-if="column.key === 'price'">
            {{ record.isFree ? '免费' : `¥${record.price}` }}
          </template>
          <template v-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="() => handleEdit(record)">编辑</a-button>
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

      <!-- 添加/编辑电影弹窗 -->
      <a-modal
        :title="isEdit ? '编辑电影' : '添加电影'"
        :open="modalVisible"
        :confirm-loading="confirmLoading"
        @ok="handleSubmit"
        @cancel="closeModal"
        width="800px"
      >
        <a-form
          ref="formRef"
          :model="formState"
          :rules="rules"
          :label-col="{ span: 4 }"
          :wrapper-col="{ span: 20 }"
        >
          <a-form-item label="电影名称" name="title">
            <a-input v-model:value="formState.title" placeholder="请输入电影名称" />
          </a-form-item>

          <a-form-item label="主演" name="actors">
            <a-input v-model:value="formState.actors" placeholder="请输入主演姓名，多个主演用逗号分隔" />
          </a-form-item>

          <a-form-item label="电影封面" name="coverBase64">
            <a-upload
              v-model:file-list="fileList"
              list-type="picture-card"
              :show-upload-list="false"
              :before-upload="beforeUpload"
            >
              <img 
                v-if="formState.coverBase64" 
                :src="formState.coverBase64" 
                alt="封面"
                style="width: 100%; height: 100%; object-fit: cover;"
              />
              <div v-else>
                <plus-outlined />
                <div style="margin-top: 8px">上传封面</div>
              </div>
            </a-upload>
          </a-form-item>

          <a-form-item label="分类" name="categories">
            <a-select
              v-model:value="formState.categories"
              mode="multiple"
              placeholder="请选择分类"
              :options="categoryOptions"
            />
          </a-form-item>

          <a-form-item label="地区" name="region">
            <a-select
              v-model:value="formState.region"
              placeholder="请选择地区"
              :options="regionOptions"
            />
          </a-form-item>

          <a-form-item label="上映年份" name="year">
            <a-select
              v-model:value="formState.year"
              placeholder="请选择年份"
              :options="yearOptions"
            />
          </a-form-item>

          <a-form-item label="时长" name="duration">
            <a-input-number 
              v-model:value="formState.duration" 
              placeholder="请输入电影时长"
              addon-after="分钟"
              :min="1"
            />
          </a-form-item>

          <a-form-item label="评分" name="rating">
            <a-input-number 
              v-model:value="formState.rating" 
              placeholder="请输入评分"
              :min="0"
              :max="10"
              :step="0.1"
            />
          </a-form-item>

          <a-form-item label="价格设置">
            <a-space>
              <a-switch 
                v-model:checked="formState.isFree"
                checked-children="免费"
                un-checked-children="付费"
              />
              <a-input-number
                v-if="!formState.isFree"
                v-model:value="formState.price"
                placeholder="请输入价格"
                :min="0.01"
                :step="0.01"
                addon-before="¥"
              />
            </a-space>
          </a-form-item>

          <a-form-item label="预告片链接" name="trailerUrl">
            <a-input
              v-model:value="formState.trailerUrl"
              placeholder="请输入预告片链接（支持mp4或其他视频格式）"
            />
          </a-form-item>

          <a-form-item v-if="isEdit" label="播放次数">
            <span>{{ formState.playCount }}</span>
          </a-form-item>
        </a-form>
      </a-modal>
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