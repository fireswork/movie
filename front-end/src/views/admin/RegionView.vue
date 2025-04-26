<script setup lang="jsx">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import request from '@/services/request'

// 地区列表数据
const regionList = ref([])
// 加载状态
const loading = ref(false)
// 是否显示添加/编辑地区的弹窗
const modalOpen = ref(false)
// 弹窗标题
const modalTitle = ref('添加地区')
// 编辑模式
const isEdit = ref(false)
// 当前编辑的地区ID
const currentId = ref(null)
// 表单引用
const formRef = ref(null)

// 地区表单数据
const regionForm = reactive({
  name: '',
})

// 地区表单校验规则
const rules = {
  name: [{ required: true, message: '请输入地区名称', trigger: 'blur' }],
}

// 获取地区列表数据
const fetchRegions = async () => {
  loading.value = true
  try {
    const { data: res } = await request.get('/regions')
    regionList.value = res
  } catch (error) {
    message.error('获取地区列表失败')
  } finally {
    loading.value = false
  }
}

// 打开添加地区弹窗
const handleAdd = () => {
  modalTitle.value = '添加地区'
  isEdit.value = false
  regionForm.name = ''
  modalOpen.value = true
}

// 打开编辑地区弹窗
const handleEdit = (record) => {
  modalTitle.value = '编辑地区'
  isEdit.value = true
  currentId.value = record.id
  regionForm.name = record.name
  modalOpen.value = true
}

// 提交地区表单
const handleSubmit = async () => {
  await formRef.value.validate()
  const method = isEdit.value ? 'put' : 'post'
  const url = isEdit.value ? `/regions/${currentId.value}` : '/regions'
  await request[method](url, regionForm)
  message.success(isEdit.value ? '编辑成功' : '添加成功')
  modalOpen.value = false
  fetchRegions()
}

// 删除地区
const handleDelete = async (record) => {
  await request.delete(`/regions/${record.id}`)
  message.success('删除成功')
  fetchRegions()
}

// 表格列定义
const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    key: 'id',
    width: 80,
  },
  {
    title: '地区名称',
    dataIndex: 'name',
    key: 'name',
  },
  {
    title: '操作',
    key: 'action',
    width: 200,
  },
]

// 关闭弹窗
const onClose = () => {
  modalOpen.value = false
}

onMounted(() => {
  fetchRegions()
})
</script>

<template>
  <div class="region-container page-container">
    <div class="content-card">
      <div class="region-header">
        <div class="header-title">
          <h2>地区管理</h2>
        </div>
        <div class="header-actions">
          <a-button type="primary" @click="handleAdd">
            <template #icon><plus-outlined /></template>
            添加地区
          </a-button>
        </div>
      </div>

      <!-- 地区表格 -->
      <a-table
        :columns="columns"
        :data-source="regionList"
        :loading="loading"
        :pagination="{ pageSize: 10, showSizeChanger: true, pageSizeOptions: ['10', '20', '50'] }"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="() => handleEdit(record)">编辑</a-button>
              <a-popconfirm
                title="确定要删除这个地区吗?"
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

      <!-- 添加/编辑地区弹窗 -->
      <a-modal
        :title="modalTitle"
        :open="modalOpen"
        @cancel="onClose"
        :maskClosable="false"
        :width="400"
      >
        <a-form ref="formRef" :model="regionForm" :rules="rules" layout="vertical">
          <a-form-item label="地区名称" name="name">
            <a-input v-model:value="regionForm.name" placeholder="请输入地区名称" />
          </a-form-item>
        </a-form>

        <template #footer>
          <a-button @click="onClose">取消</a-button>
          <a-button type="primary" @click="handleSubmit">确定</a-button>
        </template>
      </a-modal>
    </div>
  </div>
</template>

<style scoped>
.region-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

@media (max-width: 768px) {
  .region-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .header-actions {
    margin-top: 16px;
  }
}
</style>
