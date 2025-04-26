<script setup>
import { ref, reactive, onMounted } from 'vue'
import { message, notification } from 'ant-design-vue'

// 用户列表数据
const userList = ref([])
// 加载状态
const loading = ref(false)
// 用户详情抽屉
const drawerVisible = ref(false)
// 当前选中的用户
const currentUser = ref(null)
// 编辑状态
const isEditing = ref(false)

// 筛选条件
const filters = reactive({
  username: '',
  nickname: '',
  phone: '',
  status: undefined,
  job: undefined,
  timeRange: []
})

// 分页参数
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  pageSizeOptions: ['10', '20', '50', '100']
})

// 用户状态选项
const userStatusOptions = [
  { label: '全部', value: '' },
  { label: '正常', value: 'active' },
  { label: '禁用', value: 'disabled' },
  { label: '未验证', value: 'unverified' }
]

// 用户职业选项
const jobOptions = [
  { label: '全部', value: '' },
  { label: '学生', value: 'student' },
  { label: '教师', value: 'teacher' },
  { label: '程序员', value: 'programmer' },
  { label: '设计师', value: 'designer' },
  { label: '医生', value: 'doctor' },
  { label: '销售', value: 'sales' },
  { label: '公务员', value: 'official' },
  { label: '自由职业', value: 'freelancer' },
  { label: '其他', value: 'other' }
]

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
    title: '昵称',
    dataIndex: 'nickname',
    key: 'nickname',
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
    title: '手机号',
    dataIndex: 'phone',
    key: 'phone',
    width: 120
  },
  {
    title: '职业',
    dataIndex: 'job',
    key: 'job',
    width: 100,
    customRender: ({ text }) => {
      const jobMap = {
        student: '学生',
        teacher: '教师',
        programmer: '程序员',
        designer: '设计师',
        doctor: '医生',
        sales: '销售',
        official: '公务员',
        freelancer: '自由职业',
        other: '其他'
      }
      return jobMap[text] || text
    }
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 100,
    customRender: ({ text }) => {
      const statusMap = {
        active: { text: '正常', color: 'success' },
        disabled: { text: '禁用', color: 'error' },
        unverified: { text: '未验证', color: 'warning' }
      }
      const status = statusMap[text] || { text, color: 'default' }
      return <a-tag color={status.color}>{status.text}</a-tag>
    }
  },
  {
    title: '注册时间',
    dataIndex: 'registerTime',
    key: 'registerTime',
    width: 180
  },
  {
    title: '操作',
    key: 'action',
    fixed: 'right',
    width: 180
  }
]

// 获取用户列表数据
const fetchUsers = () => {
  loading.value = true

  // 模拟API请求
  setTimeout(() => {
    // 生成模拟数据
    const mockData = []
    const total = 125 // 模拟总数

    const genders = ['male', 'female']
    const statuses = ['active', 'disabled', 'unverified']
    const jobs = ['student', 'teacher', 'programmer', 'designer', 'doctor', 'sales', 'official', 'freelancer', 'other']

    for (let i = 1; i <= total; i++) {
      const gender = genders[Math.floor(Math.random() * genders.length)]
      const status = statuses[Math.floor(Math.random() * statuses.length)]
      const job = jobs[Math.floor(Math.random() * jobs.length)]

      // 随机生成注册时间，1年内
      const date = new Date()
      date.setDate(date.getDate() - Math.floor(Math.random() * 365))
      const registerTime = date.toISOString().replace('T', ' ').substring(0, 19)

      mockData.push({
        id: i,
        username: `user${i}`,
        nickname: `用户${i}`,
        gender,
        age: Math.floor(Math.random() * 50) + 15,
        phone: `1${Math.floor(Math.random() * 9) + 1}${Math.random().toString().slice(2, 11)}`,
        email: `user${i}@example.com`,
        job,
        status,
        registerTime
      })
    }

    // 筛选逻辑
    let filteredData = [...mockData]

    if (filters.username) {
      filteredData = filteredData.filter(user => user.username.includes(filters.username))
    }

    if (filters.nickname) {
      filteredData = filteredData.filter(user => user.nickname.includes(filters.nickname))
    }

    if (filters.phone) {
      filteredData = filteredData.filter(user => user.phone.includes(filters.phone))
    }

    if (filters.status) {
      filteredData = filteredData.filter(user => user.status === filters.status)
    }

    if (filters.job) {
      filteredData = filteredData.filter(user => user.job === filters.job)
    }

    if (filters.timeRange && filters.timeRange.length === 2) {
      const startDate = filters.timeRange[0].format('YYYY-MM-DD') + ' 00:00:00'
      const endDate = filters.timeRange[1].format('YYYY-MM-DD') + ' 23:59:59'

      filteredData = filteredData.filter(
        user => user.registerTime >= startDate && user.registerTime <= endDate
      )
    }

    // 按注册时间倒序排序
    filteredData.sort((a, b) => new Date(b.registerTime) - new Date(a.registerTime))

    // 分页
    const start = (pagination.current - 1) * pagination.pageSize
    const end = start + pagination.pageSize
    const paginatedData = filteredData.slice(start, end)

    userList.value = paginatedData
    pagination.total = filteredData.length
    loading.value = false
  }, 800)
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
  filters.nickname = ''
  filters.phone = ''
  filters.status = undefined
  filters.job = undefined
  filters.timeRange = []
  pagination.current = 1
  fetchUsers()
}

// 查看用户详情
const handleViewUser = (record) => {
  currentUser.value = { ...record }
  isEditing.value = false
  drawerVisible.value = true
}

// 编辑用户
const handleEditUser = (record) => {
  currentUser.value = { ...record }
  isEditing.value = true
  drawerVisible.value = true
}

// 更新用户状态
const handleStatusChange = (record, status) => {
  // 模拟API请求
  setTimeout(() => {
    message.success(`已${status === 'active' ? '启用' : '禁用'}用户：${record.username}`)
    fetchUsers()
  }, 500)
}

// 保存用户编辑
const handleSaveUser = () => {
  // 简单的表单验证
  if (!currentUser.value.nickname || !currentUser.value.phone) {
    message.error('请完善必填信息')
    return
  }

  // 模拟API请求
  setTimeout(() => {
    notification.success({
      message: '保存成功',
      description: `用户 ${currentUser.value.username} 的信息已更新`
    })

    // 关闭抽屉
    drawerVisible.value = false
    currentUser.value = null
    isEditing.value = false

    // 刷新用户列表
    fetchUsers()
  }, 1000)
}

// 关闭用户详情抽屉
const onDrawerClose = () => {
  drawerVisible.value = false
  currentUser.value = null
  isEditing.value = false
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
      </div>

      <!-- 筛选表单 -->
      <div class="filter-form">
        <a-form layout="inline">
          <a-form-item label="用户名">
            <a-input v-model:value="filters.username" placeholder="请输入用户名" allowClear />
          </a-form-item>

          <a-form-item label="昵称">
            <a-input v-model:value="filters.nickname" placeholder="请输入昵称" allowClear />
          </a-form-item>

          <a-form-item label="手机号">
            <a-input v-model:value="filters.phone" placeholder="请输入手机号" allowClear />
          </a-form-item>

          <a-form-item label="状态">
            <a-select
              v-model:value="filters.status"
              placeholder="请选择状态"
              style="width: 120px"
              allowClear
              :options="userStatusOptions"
            />
          </a-form-item>

          <a-form-item label="职业">
            <a-select
              v-model:value="filters.job"
              placeholder="请选择职业"
              style="width: 120px"
              allowClear
              :options="jobOptions"
            />
          </a-form-item>

          <a-form-item label="注册时间">
            <a-range-picker v-model:value="filters.timeRange" style="width: 250px" />
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

              <template v-if="record.status === 'active'">
                <a-button
                  type="link"
                  danger
                  size="small"
                  @click="() => handleStatusChange(record, 'disabled')"
                >
                  禁用
                </a-button>
              </template>

              <template v-else>
                <a-button
                  type="link"
                  size="small"
                  @click="() => handleStatusChange(record, 'active')"
                >
                  启用
                </a-button>
              </template>
            </a-space>
          </template>
        </template>
      </a-table>

      <!-- 用户详情/编辑抽屉 -->
      <a-drawer
        :title="isEditing ? '编辑用户' : '用户详情'"
        width="400"
        :visible="drawerVisible"
        @close="onDrawerClose"
        :body-style="{ paddingBottom: '80px' }"
      >
        <template v-if="currentUser">
          <a-descriptions bordered :column="1" :colon="false">
            <a-descriptions-item label="用户ID"> {{ currentUser.id }} </a-descriptions-item>
            <a-descriptions-item label="用户名"> {{ currentUser.username }} </a-descriptions-item>

            <a-descriptions-item v-if="!isEditing" label="昵称">
              {{ currentUser.nickname }}
            </a-descriptions-item>
            <a-descriptions-item v-else label="昵称">
              <a-input v-model:value="currentUser.nickname" placeholder="请输入昵称" />
            </a-descriptions-item>

            <a-descriptions-item v-if="!isEditing" label="性别">
              {{ currentUser.gender === 'male' ? '男' : currentUser.gender === 'female' ? '女' : '未知' }}
            </a-descriptions-item>
            <a-descriptions-item v-else label="性别">
              <a-radio-group v-model:value="currentUser.gender">
                <a-radio value="male">男</a-radio>
                <a-radio value="female">女</a-radio>
              </a-radio-group>
            </a-descriptions-item>

            <a-descriptions-item v-if="!isEditing" label="年龄">
              {{ currentUser.age }}
            </a-descriptions-item>
            <a-descriptions-item v-else label="年龄">
              <a-input-number
                v-model:value="currentUser.age"
                :min="1"
                :max="120"
                style="width: 100%"
              />
            </a-descriptions-item>

            <a-descriptions-item v-if="!isEditing" label="手机号">
              {{ currentUser.phone }}
            </a-descriptions-item>
            <a-descriptions-item v-else label="手机号">
              <a-input v-model:value="currentUser.phone" placeholder="请输入手机号" />
            </a-descriptions-item>

            <a-descriptions-item v-if="!isEditing" label="邮箱">
              {{ currentUser.email }}
            </a-descriptions-item>
            <a-descriptions-item v-else label="邮箱">
              <a-input v-model:value="currentUser.email" placeholder="请输入邮箱" />
            </a-descriptions-item>

            <a-descriptions-item v-if="!isEditing" label="职业">
              {{ 
                {
                  student: '学生',
                  teacher: '教师',
                  programmer: '程序员',
                  designer: '设计师',
                  doctor: '医生',
                  sales: '销售',
                  official: '公务员',
                  freelancer: '自由职业',
                  other: '其他'
                }[currentUser.job] || currentUser.job 
              }}
            </a-descriptions-item>
            <a-descriptions-item v-else label="职业">
              <a-select
                v-model:value="currentUser.job"
                placeholder="请选择职业"
                style="width: 100%"
                :options="jobOptions.filter(o => o.value !== '')"
              />
            </a-descriptions-item>

            <a-descriptions-item v-if="!isEditing" label="状态">
              <a-tag 
                :color="
                  currentUser.status === 'active' ? 'success' : 
                  currentUser.status === 'disabled' ? 'error' : 'warning'
                "
              >
                {{ 
                  currentUser.status === 'active' ? '正常' : 
                  currentUser.status === 'disabled' ? '禁用' : '未验证' 
                }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item v-else label="状态">
              <a-select
                v-model:value="currentUser.status"
                placeholder="请选择状态"
                style="width: 100%"
              >
                <a-select-option value="active">正常</a-select-option>
                <a-select-option value="disabled">禁用</a-select-option>
                <a-select-option value="unverified">未验证</a-select-option>
              </a-select>
            </a-descriptions-item>

            <a-descriptions-item label="注册时间">
              {{ currentUser.registerTime }}
            </a-descriptions-item>
          </a-descriptions>

          <div v-if="isEditing" class="drawer-footer">
            <a-button style="margin-right: 8px" @click="onDrawerClose">取消</a-button>
            <a-button type="primary" @click="handleSaveUser">保存</a-button>
          </div>
        </template>
      </a-drawer>
    </div>
  </div>
</template>

<style scoped>
.users-header {
  margin-bottom: 24px;
}

.filter-form {
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px dashed #f0f0f0;
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
  .filter-form :deep(.ant-form-item) {
    margin-bottom: 16px;
  }
}
</style>