<script setup>
import { ref, reactive, onMounted } from 'vue'

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
  parentId: undefined,
  sort: 1,
  status: 'active',
})

// 分类表单校验规则
const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
}

// 分类状态选项
const statusOptions = [
  { label: '启用', value: 'active' },
  { label: '禁用', value: 'inactive' },
]

// 获取分类列表数据
const fetchCategories = () => {
  loading.value = true

  // 模拟API请求
  setTimeout(() => {
    // 生成模拟数据
    const mockData = [
      {
        id: 1,
        name: '剧情',
        parentId: 0,
        sort: 1,
        status: 'active',
        children: [
          { id: 11, name: '家庭剧情', parentId: 1, sort: 1, status: 'active' },
          { id: 12, name: '犯罪剧情', parentId: 1, sort: 2, status: 'active' },
          { id: 13, name: '传记剧情', parentId: 1, sort: 3, status: 'inactive' },
        ],
      },
      {
        id: 2,
        name: '动作',
        parentId: 0,
        sort: 2,
        status: 'active',
        children: [
          { id: 21, name: '功夫动作', parentId: 2, sort: 1, status: 'active' },
          { id: 22, name: '枪战动作', parentId: 2, sort: 2, status: 'active' },
          { id: 23, name: '科幻动作', parentId: 2, sort: 3, status: 'active' },
        ],
      },
      {
        id: 3,
        name: '喜剧',
        parentId: 0,
        sort: 3,
        status: 'active',
        children: [
          { id: 31, name: '浪漫喜剧', parentId: 3, sort: 1, status: 'active' },
          { id: 32, name: '黑色喜剧', parentId: 3, sort: 2, status: 'active' },
        ],
      },
      {
        id: 4,
        name: '科幻',
        parentId: 0,
        sort: 4,
        status: 'active',
        children: [
          { id: 41, name: '太空科幻', parentId: 4, sort: 1, status: 'active' },
          { id: 42, name: '未来科幻', parentId: 4, sort: 2, status: 'active' },
          { id: 43, name: '机器人科幻', parentId: 4, sort: 3, status: 'active' },
        ],
      },
      {
        id: 5,
        name: '恐怖',
        parentId: 0,
        sort: 5,
        status: 'active',
        children: [
          { id: 51, name: '惊悚恐怖', parentId: 5, sort: 1, status: 'active' },
          { id: 52, name: '超自然恐怖', parentId: 5, sort: 2, status: 'active' },
        ],
      },
      {
        id: 6,
        name: '爱情',
        parentId: 0,
        sort: 6,
        status: 'active',
        children: [
          { id: 61, name: '纯爱', parentId: 6, sort: 1, status: 'active' },
          { id: 62, name: '悲情', parentId: 6, sort: 2, status: 'inactive' },
        ],
      },
      {
        id: 7,
        name: '动画',
        parentId: 0,
        sort: 7,
        status: 'active',
        children: [
          { id: 71, name: '日本动画', parentId: 7, sort: 1, status: 'active' },
          { id: 72, name: '美国动画', parentId: 7, sort: 2, status: 'active' },
          { id: 73, name: '中国动画', parentId: 7, sort: 3, status: 'active' },
        ],
      },
      {
        id: 8,
        name: '战争',
        parentId: 0,
        sort: 8,
        status: 'active',
        children: [
          { id: 81, name: '二战题材', parentId: 8, sort: 1, status: 'active' },
          { id: 82, name: '现代战争', parentId: 8, sort: 2, status: 'active' },
        ],
      },
    ]

    categoryList.value = mockData
    loading.value = false
  }, 800)
}

// 获取所有父级分类
const getParentCategories = () => {
  return [
    { label: '顶级分类', value: 0 },
    ...categoryList.value.map((item) => ({
      label: item.name,
      value: item.id,
    })),
  ]
}

// 打开添加分类抽屉
const handleAdd = (parentId = 0) => {
  drawerTitle.value = '添加分类'
  isEdit.value = false

  // 重置表单
  categoryForm.name = ''
  categoryForm.parentId = parentId
  categoryForm.sort = 1
  categoryForm.status = 'active'

  drawerVisible.value = true
}

// 打开编辑分类抽屉
const handleEdit = (record) => {
  drawerTitle.value = '编辑分类'
  isEdit.value = true
  currentId.value = record.id

  // 填充表单数据
  categoryForm.name = record.name
  categoryForm.parentId = record.parentId
  categoryForm.sort = record.sort
  categoryForm.status = record.status

  drawerVisible.value = true
}

// 提交分类表单
const handleSubmit = () => {
  formRef.value.validate().then(() => {
    // 模拟表单提交
    console.log('提交分类表单数据：', categoryForm)

    // 模拟提交成功
    drawerVisible.value = false

    // 刷新列表数据
    fetchCategories()
  })
}

// 删除分类
const handleDelete = (record) => {
  console.log('删除分类：', record)

  // 模拟删除成功
  // 刷新列表数据
  fetchCategories()
}

// 表格列定义
const columns = [
  {
    title: '分类名称',
    dataIndex: 'name',
    key: 'name',
  },
  {
    title: '排序',
    dataIndex: 'sort',
    key: 'sort',
    width: 100,
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 120,
    customRender: ({ text }) => {
      return (
        <a-tag color={text === 'active' ? 'success' : 'default'}>
          {text === 'active' ? '启用' : '禁用'}
        </a-tag>
      )
    },
  },
  {
    title: '操作',
    key: 'action',
    width: 250,
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
          <a-button type="primary" @click="() => handleAdd()">
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
        :expand-row-by-click="true"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="() => handleAdd(record.id)">
                添加子分类
              </a-button>

              <a-button type="link" size="small" @click="() => handleEdit(record)"> 编辑 </a-button>

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

      <!-- 添加/编辑分类抽屉 -->
      <a-drawer
        :title="drawerTitle"
        :width="420"
        :visible="drawerVisible"
        @close="onClose"
        :body-style="{ paddingBottom: '80px' }"
      >
        <a-form ref="formRef" :model="categoryForm" :rules="rules" layout="vertical">
          <a-form-item label="分类名称" name="name">
            <a-input v-model:value="categoryForm.name" placeholder="请输入分类名称" />
          </a-form-item>

          <a-form-item label="父级分类" name="parentId">
            <a-select
              v-model:value="categoryForm.parentId"
              :options="getParentCategories()"
              placeholder="请选择父级分类"
              :disabled="isEdit"
            />
          </a-form-item>

          <a-form-item label="排序" name="sort">
            <a-input-number v-model:value="categoryForm.sort" :min="1" style="width: 100%" />
          </a-form-item>

          <a-form-item label="状态" name="status">
            <a-radio-group v-model:value="categoryForm.status">
              <a-radio v-for="option in statusOptions" :key="option.value" :value="option.value">
                {{ option.label }}
              </a-radio>
            </a-radio-group>
          </a-form-item>
        </a-form>

        <div class="drawer-footer">
          <a-button style="margin-right: 8px" @click="onClose">取消</a-button>
          <a-button type="primary" @click="handleSubmit">提交</a-button>
        </div>
      </a-drawer>
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
