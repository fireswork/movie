<script setup>
import { ref, reactive, onMounted } from 'vue'

// 地区列表数据
const regionList = ref([])
// 加载状态
const loading = ref(false)
// 是否显示添加/编辑地区的抽屉
const drawerVisible = ref(false)
// 抽屉标题
const drawerTitle = ref('添加地区')
// 编辑模式
const isEdit = ref(false)
// 当前编辑的地区ID
const currentId = ref(null)
// 表单引用
const formRef = ref(null)

// 地区表单数据
const regionForm = reactive({
  name: '',
  code: '',
  sort: 1,
  status: 'active',
})

// 地区表单校验规则
const rules = {
  name: [{ required: true, message: '请输入地区名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入地区代码', trigger: 'blur' }],
}

// 地区状态选项
const statusOptions = [
  { label: '启用', value: 'active' },
  { label: '禁用', value: 'inactive' },
]

// 获取地区列表数据
const fetchRegions = () => {
  loading.value = true

  // 模拟API请求
  setTimeout(() => {
    // 生成模拟数据
    const mockData = [
      {
        id: 1,
        name: '中国大陆',
        code: 'CN',
        sort: 1,
        status: 'active',
      },
      {
        id: 2,
        name: '中国香港',
        code: 'HK',
        sort: 2,
        status: 'active',
      },
      {
        id: 3,
        name: '中国台湾',
        code: 'TW',
        sort: 3,
        status: 'active',
      },
      {
        id: 4,
        name: '美国',
        code: 'US',
        sort: 4,
        status: 'active',
      },
      {
        id: 5,
        name: '日本',
        code: 'JP',
        sort: 5,
        status: 'active',
      },
      {
        id: 6,
        name: '韩国',
        code: 'KR',
        sort: 6,
        status: 'active',
      },
      {
        id: 7,
        name: '英国',
        code: 'GB',
        sort: 7,
        status: 'active',
      },
      {
        id: 8,
        name: '法国',
        code: 'FR',
        sort: 8,
        status: 'active',
      },
      {
        id: 9,
        name: '德国',
        code: 'DE',
        sort: 9,
        status: 'active',
      },
      {
        id: 10,
        name: '印度',
        code: 'IN',
        sort: 10,
        status: 'active',
      },
      {
        id: 11,
        name: '俄罗斯',
        code: 'RU',
        sort: 11,
        status: 'inactive',
      },
      {
        id: 12,
        name: '澳大利亚',
        code: 'AU',
        sort: 12,
        status: 'active',
      },
      {
        id: 13,
        name: '加拿大',
        code: 'CA',
        sort: 13,
        status: 'active',
      },
      {
        id: 14,
        name: '巴西',
        code: 'BR',
        sort: 14,
        status: 'inactive',
      },
      {
        id: 15,
        name: '意大利',
        code: 'IT',
        sort: 15,
        status: 'active',
      },
    ]

    regionList.value = mockData
    loading.value = false
  }, 800)
}

// 打开添加地区抽屉
const handleAdd = () => {
  drawerTitle.value = '添加地区'
  isEdit.value = false

  // 重置表单
  regionForm.name = ''
  regionForm.code = ''
  regionForm.sort = regionList.value.length + 1
  regionForm.status = 'active'

  drawerVisible.value = true
}

// 打开编辑地区抽屉
const handleEdit = (record) => {
  drawerTitle.value = '编辑地区'
  isEdit.value = true
  currentId.value = record.id

  // 填充表单数据
  regionForm.name = record.name
  regionForm.code = record.code
  regionForm.sort = record.sort
  regionForm.status = record.status

  drawerVisible.value = true
}

// 提交地区表单
const handleSubmit = () => {
  formRef.value.validate().then(() => {
    // 模拟表单提交
    console.log('提交地区表单数据：', regionForm)

    // 模拟提交成功
    drawerVisible.value = false

    // 刷新列表数据
    fetchRegions()
  })
}

// 删除地区
const handleDelete = (record) => {
  console.log('删除地区：', record)

  // 模拟删除成功
  // 刷新列表数据
  fetchRegions()
}

// 更新地区状态
const handleStatusChange = (record) => {
  const newStatus = record.status === 'active' ? 'inactive' : 'active'
  console.log(`更新地区 ${record.name} 的状态为 ${newStatus}`)

  // 模拟更新成功
  // 刷新列表数据
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
    title: '地区代码',
    dataIndex: 'code',
    key: 'code',
    width: 120,
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
    width: 220,
  },
]

// 关闭抽屉
const onClose = () => {
  drawerVisible.value = false
}

onMounted(() => {
  // 加载地区列表数据
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
              <a-switch
                :checked="record.status === 'active'"
                @change="() => handleStatusChange(record)"
                checked-children="启用"
                un-checked-children="禁用"
              />

              <a-button type="link" size="small" @click="() => handleEdit(record)"> 编辑 </a-button>

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

      <!-- 添加/编辑地区抽屉 -->
      <a-drawer
        :title="drawerTitle"
        :width="400"
        :visible="drawerVisible"
        @close="onClose"
        :body-style="{ paddingBottom: '80px' }"
      >
        <a-form ref="formRef" :model="regionForm" :rules="rules" layout="vertical">
          <a-form-item label="地区名称" name="name">
            <a-input v-model:value="regionForm.name" placeholder="请输入地区名称" />
          </a-form-item>

          <a-form-item label="地区代码" name="code">
            <a-input
              v-model:value="regionForm.code"
              placeholder="请输入地区代码，如CN、US"
              :disabled="isEdit"
            />
          </a-form-item>

          <a-form-item label="排序" name="sort">
            <a-input-number v-model:value="regionForm.sort" :min="1" style="width: 100%" />
          </a-form-item>

          <a-form-item label="状态" name="status">
            <a-radio-group v-model:value="regionForm.status">
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
.region-header {
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
  .region-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .header-actions {
    margin-top: 16px;
  }
}
</style>
