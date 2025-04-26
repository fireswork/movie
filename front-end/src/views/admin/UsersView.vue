<script setup lang="jsx">
import { ref, reactive, onMounted } from 'vue'
import { message, notification } from 'ant-design-vue'
import request from '@/services/request'

// 用户列表数据
const userList = ref([])
// 加载状态
const loading = ref(false)
// 用户弹窗
const modalOpen = ref(false)
// 当前选中的用户
const currentUser = ref(null)
// 编辑状态
const isEditing = ref(false)

// 筛选条件
const filters = reactive({
  username: '',
  phone: ''
})

// 分页参数
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  pageSizeOptions: ['10', '20', '50', '100']
})

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
    title: '性别',
    dataIndex: 'gender',
    key: 'gender',
    width: 80,
    customRender: ({ text }) => {
      return text === 'male' ? '男' : text === 'female' ? '女' : '未知'
    }
  },
  {
    title: '年龄',
    dataIndex: 'age',
    key: 'age',
    width: 80
  },
  {
    title: '手机号',
    dataIndex: 'phone',
    key: 'phone',
    width: 120
  },
  {
    title: '职业',
    dataIndex: 'profession',
    key: 'profession',
    width: 100
  },
  {
    title: '操作',
    key: 'action',
    fixed: 'right',
    width: 200
  }
]

// 获取用户列表数据
const fetchUsers = async () => {
  loading.value = true
  try {
    const { data: res } = await request.get('/users', {
      params: {
        page: pagination.current - 1,
        size: pagination.pageSize,
        username: filters.username || '',
        phone: filters.phone || ''
      }
    })
    console.log('res', res)
    userList.value = res.content
    pagination.total = res.totalElements
  } catch (error) {
  } finally {
    loading.value = false
  }
}

// 表格分页、排序、筛选变化时的回调
const handleTableChange = (pag) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  fetchUsers()
}

// 查询
const handleSearch = () => {
  pagination.current = 1
  fetchUsers()
}

// 重置筛选
const handleReset = () => {
  filters.username = ''
  filters.phone = ''
  pagination.current = 1
  fetchUsers()
}

// 新增用户
const handleAddUser = () => {
  currentUser.value = {
    username: '',
    password: '',
    gender: 'male',
    age: 18,
    profession: '',
    phone: '',
  }
  isEditing.value = true
  modalOpen.value = true
}

// 查看用户详情
const handleViewUser = (record) => {
  currentUser.value = { ...record }
  isEditing.value = false
  modalOpen.value = true
}

// 编辑用户
const handleEditUser = (record) => {
  currentUser.value = { ...record }
  isEditing.value = true
  modalOpen.value = true
}

// 删除用户
const handleDeleteUser = async (record) => {
  try {
    const { data: res } = await request.delete(`/users/${record.id}`)
    message.success(res.message)
    fetchUsers()
  } catch (error) {
    message.error('删除用户失败')
  }
}

// 关闭用户弹窗
const handleModalClose = () => {
  modalOpen.value = false
  currentUser.value = null
  isEditing.value = false
}

// 保存用户编辑
const handleSaveUser = async () => {
  if (!currentUser.value.username || !currentUser.value.phone) {
    message.error('请完善必填信息')
    return
  }

  try {
    const method = currentUser.value.id ? 'put' : 'post'
    const url = currentUser.value.id ? `/users/${currentUser.value.id}` : '/users'
    
    const { data: res } = await request[method](url, currentUser.value)
    
    notification.success({
      message: '保存成功',
      description: res.message
    })

    modalOpen.value = false
    currentUser.value = null
    isEditing.value = false
    fetchUsers()
  } catch (error) {
    message.error('保存用户信息失败')
  }
}

onMounted(() => {
  fetchUsers()
})
</script>

<template>
  <div class="users-container page-container">
    <div class="content-card">
      <div class="users-header">
        <div class="header-title">
          <h2>用户管理</h2>
        </div>
        <div class="header-actions">
          <a-button type="primary" @click="handleAddUser">新增用户</a-button>
        </div>
      </div>

      <!-- 筛选表单 -->
      <div class="filter-form">
        <a-form layout="inline">
          <a-form-item label="用户名">
            <a-input v-model:value="filters.username" placeholder="请输入用户名" allowClear />
          </a-form-item>

          <a-form-item label="手机号">
            <a-input v-model:value="filters.phone" placeholder="请输入手机号" allowClear />
          </a-form-item>

          <a-form-item>
            <a-space>
              <a-button type="primary" @click="handleSearch">查询</a-button>
              <a-button @click="handleReset">重置</a-button>
            </a-space>
          </a-form-item>
        </a-form>
      </div>

      <!-- 用户表格 -->
      <a-table
        :columns="columns"
        :data-source="userList"
        :loading="loading"
        :pagination="pagination"
        @change="handleTableChange"
        row-key="id"
        scroll={{ x: 1200 }}
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="() => handleViewUser(record)">
                查看
              </a-button>

              <a-button type="link" size="small" @click="() => handleEditUser(record)">
                编辑
              </a-button>

              <a-button type="link" danger size="small" @click="() => handleDeleteUser(record)" v-if="!record.admin">
                删除
              </a-button>
            </a-space>
          </template>
        </template>
      </a-table>

      <!-- 用户详情/编辑弹窗 -->
      <a-modal
        :title="isEditing ? (currentUser?.id ? '编辑用户' : '新增用户') : '用户详情'"
        :open="modalOpen"
        @cancel="handleModalClose"
        :maskClosable="false"
        :width="500"
      >
        <template v-if="currentUser">
          <a-form layout="vertical">
            <a-form-item label="用户名" required>
              <a-input 
                v-model:value="currentUser.username" 
                placeholder="请输入用户名"
                :disabled="!isEditing || currentUser.id"
              />
            </a-form-item>

            <a-form-item label="密码" required v-if="isEditing && !currentUser.id">
              <a-input-password
                v-model:value="currentUser.password"
                placeholder="请输入密码"
              />
            </a-form-item>

            <a-form-item label="性别">
              <a-radio-group v-model:value="currentUser.gender" :disabled="!isEditing">
                <a-radio value="male">男</a-radio>
                <a-radio value="female">女</a-radio>
              </a-radio-group>
            </a-form-item>

            <a-form-item label="年龄">
              <a-input-number
                v-model:value="currentUser.age"
                :min="1"
                :max="120"
                style="width: 100%"
                :disabled="!isEditing"
              />
            </a-form-item>

            <a-form-item label="手机号" required>
              <a-input 
                v-model:value="currentUser.phone" 
                placeholder="请输入手机号"
                :disabled="!isEditing"
              />
            </a-form-item>

            <a-form-item label="职业">
              <a-input 
                v-model:value="currentUser.profession" 
                placeholder="请输入职业"
                :disabled="!isEditing"
              />
            </a-form-item>

          </a-form>
        </template>

        <template #footer>
          <a-button @click="handleModalClose">取消</a-button>
          <a-button v-if="isEditing" type="primary" @click="handleSaveUser">保存</a-button>
        </template>
      </a-modal>
    </div>
  </div>
</template>

<style scoped>
.users-header {
  margin-bottom: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-form {
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px dashed #f0f0f0;
}

@media (max-width: 768px) {
  .filter-form :deep(.ant-form-item) {
    margin-bottom: 16px;
  }
}
</style>