<script setup lang="jsx">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import request from '@/services/request'

// 分类列表数据
const categoryList = ref([])
// 加载状态
const loading = ref(false)
// 是否显示添加/编辑分类的抽屉
const drawerVisible = ref(false)
// 抽屉标题
const drawerTitle = ref('添加分类')
// 编辑模式
const isEdit = ref(false)
// 当前编辑的分类ID
const currentId = ref(null)
// 表单引用
const formRef = ref(null)

// 分类表单数据
const categoryForm = reactive({
  name: '',
})

// 分类表单校验规则
const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
}

// 获取分类列表数据
const fetchCategories = async () => {
  loading.value = true
  try {
    const { data: res } = await request.get('/categories')
    categoryList.value = res
  } catch (error) {
    message.error('获取分类列表失败')
  } finally {
    loading.value = false
  }
}

// 打开添加分类抽屉
const handleAdd = () => {
  drawerTitle.value = '添加分类'
  isEdit.value = false
  categoryForm.name = ''
  drawerVisible.value = true
}

// 打开编辑分类抽屉
const handleEdit = (record) => {
  drawerTitle.value = '编辑分类'
  isEdit.value = true
  currentId.value = record.id
  categoryForm.name = record.name
  drawerVisible.value = true
}

// 提交分类表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    const method = isEdit.value ? 'put' : 'post'
    const url = isEdit.value ? `/categories/${currentId.value}` : '/categories'
    const { data: res } = await request[method](url, categoryForm)
    message.success(isEdit.value ? '编辑成功' : '添加成功')
    drawerVisible.value = false
    fetchCategories()
  } catch (error) {
    message.error('操作失败')
  }
}

// 删除分类
const handleDelete = async (record) => {
  await request.delete(`/categories/${record.id}`)
  message.success('删除成功')
  fetchCategories()
}

// 表格列定义
const columns = [
  {
    title: '分类ID',
    dataIndex: 'id',
    key: 'id',
    width: 80,
  },
  {
    title: '分类名称',
    dataIndex: 'name',
    key: 'name',
  },
  {
    title: '操作',
    key: 'action',
    width: 200,
  },
]

// 关闭抽屉
const onClose = () => {
  drawerVisible.value = false
}

onMounted(() => {
  // 加载分类列表数据
  fetchCategories()
})
</script>

<template>
  <div class="category-container page-container">
    <div class="content-card">
      <div class="category-header">
        <div class="header-title">
          <h2>分类管理</h2>
        </div>
        <div class="header-actions">
          <a-button type="primary" @click="handleAdd">
            <template #icon><plus-outlined /></template>
            添加分类
          </a-button>
        </div>
      </div>

      <!-- 分类表格 -->
      <a-table
        :columns="columns"
        :data-source="categoryList"
        :loading="loading"
        :pagination="false"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="() => handleEdit(record)">编辑</a-button>
              <a-popconfirm
                title="确定要删除这个分类吗?"
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

      <!-- 添加/编辑分类弹窗 -->
      <a-modal
        :title="drawerTitle"
        :open="drawerVisible"
        @cancel="onClose"
        :maskClosable="false"
        :width="400"
      >
        <a-form ref="formRef" :model="categoryForm" :rules="rules" layout="vertical">
          <a-form-item label="分类名称" name="name">
            <a-input v-model:value="categoryForm.name" placeholder="请输入分类名称" />
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
.category-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
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
  .category-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .header-actions {
    margin-top: 16px;
  }
}
</style>
