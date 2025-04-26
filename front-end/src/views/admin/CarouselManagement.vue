<template>
  <div class="carousel-management">
    <a-card title="轮播图管理">
      <template #extra>
        <a-button type="primary" @click="showUploadModal">
          上传轮播图
        </a-button>
      </template>

      <a-table
        :dataSource="carousels"
        :columns="columns"
        rowKey="id"
        :pagination="false"
        :loading="loading"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'image'">
            <img :src="record.imageBase64" style="max-width: 200px; max-height: 100px;" />
          </template>
          <template v-if="column.key === 'action'">
            <a-space>
              <a-button type="link" @click="editCarousel(record)">编辑</a-button>
              <a-popconfirm
                title="确定要删除这个轮播图吗？"
                @confirm="deleteCarousel(record.id)"
              >
                <a-button type="link" danger>删除</a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>

      <a-modal
        v-model:open="modalVisible"
        :title="editingCarousel ? '编辑轮播图' : '上传轮播图'"
        @ok="handleModalOk"
        :confirmLoading="confirmLoading"
      >
        <a-form
          ref="formRef"
          :model="formState"
          :rules="rules"
          :label-col="{ span: 6 }"
          :wrapper-col="{ span: 16 }"
        >
          <a-form-item label="标题" name="title">
            <a-input v-model:value="formState.title" />
          </a-form-item>
          
          <a-form-item label="图片" name="imageBase64">
            <a-upload
              v-if="!formState.imageBase64"
              list-type="picture-card"
              :show-upload-list="false"
              :before-upload="beforeUpload"
            >
              <div>
                <plus-outlined />
                <div style="margin-top: 8px">上传</div>
              </div>
            </a-upload>
            <div v-else>
              <img :src="formState.imageBase64" style="max-width: 100%;" />
              <a-button type="link" @click="formState.imageBase64 = ''">重新上传</a-button>
            </div>
          </a-form-item>
        </a-form>
      </a-modal>
    </a-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { PlusOutlined } from '@ant-design/icons-vue'
import axios from '@/services/request'

const columns = [
  {
    title: '轮播图ID',
    dataIndex: 'id',
    key: 'id',
  },
  {
    title: '标题',
    dataIndex: 'title',
    key: 'title',
  },
  {
    title: '图片',
    dataIndex: 'imageBase64',
    key: 'image',
  },
  {
    title: '操作',
    key: 'action',
  },
]

const carousels = ref([])
const loading = ref(false)
const modalVisible = ref(false)
const confirmLoading = ref(false)
const editingCarousel = ref(null)
const formRef = ref(null)

const formState = ref({
  title: '',
  imageBase64: '',
})

const rules = {
  title: [{ required: true, message: '请输入标题' }],
  imageBase64: [{ required: true, message: '请上传图片' }],
}

const fetchCarousels = async () => {
  const response = await axios.get('/carousels')
  carousels.value = response.data
}

const showUploadModal = () => {
  editingCarousel.value = null
  formState.value = {
    title: '',
    imageBase64: '',
  }
  modalVisible.value = true
}

const editCarousel = (record) => {
  editingCarousel.value = record
  formState.value = {
    ...record
  }
  modalVisible.value = true
}

const beforeUpload = (file) => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = () => {
      formState.value.imageBase64 = reader.result
      resolve(false)
    }
    reader.onerror = (error) => {
      reject(error)
    }
  })
}

const handleModalOk = async () => {
  try {
    await formRef.value.validate()
    confirmLoading.value = true

    if (editingCarousel.value) {
      await axios.put(`/carousels/${editingCarousel.value.id}`, formState.value)
      message.success('更新成功')
    } else {
      await axios.post('/carousels', formState.value)
      message.success('创建成功')
    }

    modalVisible.value = false
    fetchCarousels()
  } catch (error) {
    message.error('操作失败')
  } finally {
    confirmLoading.value = false
  }
}

const deleteCarousel = async (id) => {
  try {
    await axios.delete(`/carousels/${id}`)
    message.success('删除成功')
    fetchCarousels()
  } catch (error) {
    message.error('删除失败')
  }
}

onMounted(() => {
  fetchCarousels()
})
</script>

<style scoped>
.carousel-management {
  padding: 24px;
}
</style> 