<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message, notification } from 'ant-design-vue'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const isLoading = ref(false)
const isSubmitting = ref(false)
const movieId = ref(route.params.id)
const isEdit = ref(!!movieId.value)

// 表单数据
const formData = reactive({
  title: '',
  director: '',
  actors: '',
  plot: '',
  poster: '',
  category: [],
  year: '',
  duration: '',
  rating: 0,
  price: 0,
  stock: 0,
  status: 'active',
})

// 类别选项
const categoryOptions = ref([
  { value: 'action', label: '动作' },
  { value: 'comedy', label: '喜剧' },
  { value: 'drama', label: '剧情' },
  { value: 'horror', label: '恐怖' },
  { value: 'sci-fi', label: '科幻' },
  { value: 'animation', label: '动画' },
  { value: 'romance', label: '爱情' },
  { value: 'documentary', label: '纪录片' },
])

// 状态选项
const statusOptions = ref([
  { value: 'active', label: '上架' },
  { value: 'inactive', label: '下架' },
])

// 用于生成年份选项
const yearOptions = ref([])
const currentYear = new Date().getFullYear()
for (let year = currentYear; year >= 1900; year--) {
  yearOptions.value.push({ value: year.toString(), label: year.toString() })
}

// 获取电影详情
const fetchMovieDetails = async () => {
  if (!isEdit.value) return

  try {
    isLoading.value = true
    // 在实际项目中，替换为真实的API调用
    // const response = await axios.get(`/api/movies/${movieId.value}`);
    // const data = response.data;

    // 模拟API返回数据
    const data = {
      id: movieId.value,
      title: '电影标题',
      director: '导演名称',
      actors: '演员1, 演员2',
      plot: '电影简介内容...',
      poster: 'https://via.placeholder.com/300x450',
      category: ['action', 'sci-fi'],
      year: '2023',
      duration: '120',
      rating: 8.5,
      price: 39.9,
      stock: 100,
      status: 'active',
    }

    // 填充表单数据
    Object.keys(formData).forEach((key) => {
      if (data[key] !== undefined) {
        formData[key] = data[key]
      }
    })
  } catch (error) {
    notification.error({
      message: '获取电影详情失败',
      description: error.message || '请稍后重试',
    })
  } finally {
    isLoading.value = false
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    isSubmitting.value = true

    // 表单验证
    if (!formData.title) {
      message.error('请输入电影标题')
      return
    }

    if (!formData.category || formData.category.length === 0) {
      message.error('请选择电影类别')
      return
    }

    // 在实际项目中，替换为真实的API调用
    // if (isEdit.value) {
    //   await axios.put(`/api/movies/${movieId.value}`, formData);
    // } else {
    //   await axios.post('/api/movies', formData);
    // }

    // 模拟API调用延迟
    await new Promise((resolve) => setTimeout(resolve, 1000))

    message.success(`${isEdit.value ? '更新' : '创建'}电影成功`)
    router.push('/admin/movies')
  } catch (error) {
    notification.error({
      message: `${isEdit.value ? '更新' : '创建'}电影失败`,
      description: error.message || '请稍后重试',
    })
  } finally {
    isSubmitting.value = false
  }
}

// 取消编辑
const handleCancel = () => {
  router.push('/admin/movies')
}

// 上传海报处理函数
const handlePosterChange = (info) => {
  if (info.file.status === 'done') {
    // 实际项目中处理文件上传后的URL
    formData.poster = info.file.response.url || 'https://via.placeholder.com/300x450'
    message.success(`${info.file.name} 上传成功`)
  } else if (info.file.status === 'error') {
    message.error(`${info.file.name} 上传失败`)
  }
}

// 自定义上传函数
const customUpload = ({ file, onSuccess, onError }) => {
  // 模拟上传
  setTimeout(() => {
    onSuccess({ url: URL.createObjectURL(file) })
  }, 1000)

  // 实际项目中使用axios上传
  // const formData = new FormData();
  // formData.append('file', file);
  // axios.post('/api/upload', formData).then(res => {
  //   onSuccess(res.data);
  // }).catch(err => {
  //   onError(err);
  // });
}

onMounted(() => {
  fetchMovieDetails()
})
</script>

<template>
  <div class="movie-edit-container">
    <a-page-header
      :title="isEdit ? '编辑电影' : '添加电影'"
      :sub-title="isEdit ? `ID: ${movieId}` : ''"
      @back="handleCancel"
    />

    <a-spin :spinning="isLoading">
      <a-form :model="formData" layout="vertical" class="movie-form">
        <a-row :gutter="24">
          <a-col :span="16">
            <a-card title="基本信息" class="form-card">
              <a-form-item label="电影标题" required>
                <a-input v-model:value="formData.title" placeholder="请输入电影标题" />
              </a-form-item>

              <a-row :gutter="16">
                <a-col :span="8">
                  <a-form-item label="导演">
                    <a-input v-model:value="formData.director" placeholder="请输入导演姓名" />
                  </a-form-item>
                </a-col>
                <a-col :span="8">
                  <a-form-item label="上映年份">
                    <a-select
                      v-model:value="formData.year"
                      placeholder="请选择上映年份"
                      :options="yearOptions"
                    />
                  </a-form-item>
                </a-col>
                <a-col :span="8">
                  <a-form-item label="片长(分钟)">
                    <a-input-number
                      v-model:value="formData.duration"
                      placeholder="请输入片长"
                      style="width: 100%"
                      :min="0"
                    />
                  </a-form-item>
                </a-col>
              </a-row>

              <a-form-item label="演员">
                <a-textarea
                  v-model:value="formData.actors"
                  placeholder="请输入演员，多个演员用逗号分隔"
                  :rows="2"
                />
              </a-form-item>

              <a-form-item label="电影简介">
                <a-textarea v-model:value="formData.plot" placeholder="请输入电影简介" :rows="4" />
              </a-form-item>

              <a-form-item label="电影类别" required>
                <a-select
                  v-model:value="formData.category"
                  mode="multiple"
                  placeholder="请选择电影类别"
                  :options="categoryOptions"
                />
              </a-form-item>
            </a-card>

            <a-card title="销售信息" class="form-card">
              <a-row :gutter="16">
                <a-col :span="8">
                  <a-form-item label="评分(0-10)">
                    <a-input-number
                      v-model:value="formData.rating"
                      placeholder="请输入评分"
                      style="width: 100%"
                      :min="0"
                      :max="10"
                      :step="0.1"
                    />
                  </a-form-item>
                </a-col>
                <a-col :span="8">
                  <a-form-item label="价格(元)">
                    <a-input-number
                      v-model:value="formData.price"
                      placeholder="请输入价格"
                      style="width: 100%"
                      :min="0"
                      :step="0.1"
                    />
                  </a-form-item>
                </a-col>
                <a-col :span="8">
                  <a-form-item label="库存">
                    <a-input-number
                      v-model:value="formData.stock"
                      placeholder="请输入库存"
                      style="width: 100%"
                      :min="0"
                    />
                  </a-form-item>
                </a-col>
              </a-row>

              <a-form-item label="状态">
                <a-radio-group v-model:value="formData.status">
                  <a-radio-button
                    v-for="option in statusOptions"
                    :key="option.value"
                    :value="option.value"
                  >
                    {{ option.label }}
                  </a-radio-button>
                </a-radio-group>
              </a-form-item>
            </a-card>
          </a-col>

          <a-col :span="8">
            <a-card title="电影海报" class="form-card">
              <div class="poster-upload-container">
                <a-upload
                  v-model:file-list="fileList"
                  list-type="picture-card"
                  class="poster-uploader"
                  :show-upload-list="false"
                  :custom-request="customUpload"
                  @change="handlePosterChange"
                >
                  <div v-if="!formData.poster">
                    <div class="ant-upload-text">
                      <upload-outlined />
                      <div>上传海报</div>
                    </div>
                  </div>
                  <img v-else :src="formData.poster" alt="电影海报" class="poster-preview" />
                </a-upload>
                <div class="poster-tip">建议尺寸: 300x450px</div>
              </div>
            </a-card>
          </a-col>
        </a-row>

        <div class="form-actions">
          <a-button @click="handleCancel">取消</a-button>
          <a-button type="primary" :loading="isSubmitting" @click="handleSubmit">
            {{ isEdit ? '更新' : '创建' }}
          </a-button>
        </div>
      </a-form>
    </a-spin>
  </div>
</template>

<style scoped>
.movie-edit-container {
  padding: 0 24px 24px;
}

.form-card {
  margin-bottom: 24px;
}

.movie-form {
  max-width: 100%;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}

.poster-upload-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.poster-uploader {
  width: 100%;
  text-align: center;
}

.poster-tip {
  color: rgba(0, 0, 0, 0.45);
  margin-top: 8px;
  font-size: 12px;
}

.poster-preview {
  width: 100%;
  max-height: 350px;
  object-fit: cover;
}

:deep(.ant-upload-picture-card-wrapper) {
  width: 100%;
}

:deep(.ant-upload.ant-upload-select-picture-card) {
  width: 80%;
  height: 350px;
  margin: 0 auto;
}
</style>
