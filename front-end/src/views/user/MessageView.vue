<script setup>
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import axios from '@/services/request'
import { DeleteOutlined } from '@ant-design/icons-vue'
import dayjs from 'dayjs'

const userInfo = ref(JSON.parse(localStorage.getItem('user')))

// 留言列表数据
const messageList = ref([])
// 加载状态
const loading = ref(false)
// 提交状态
const submitting = ref(false)

// 留言表单数据
const messageForm = reactive({
  content: '',
  type: 'suggestion', // 默认为建议类型
})

// 留言类型选项
const messageTypeOptions = [
  { label: '功能建议', value: 'suggestion' },
  { label: '内容反馈', value: 'content' },
  { label: '技术问题', value: 'technical' },
  { label: '其他', value: 'other' },
]

// 表单校验规则
const rules = {
  content: [{ required: true, message: '请输入留言内容', trigger: 'blur' }],
  type: [{ required: true, message: '请选择留言类型', trigger: 'change' }],
}

// 分页参数
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  pageSizeOptions: ['10', '20', '50'],
})

// 获取留言历史
const fetchMessages = async () => {
  loading.value = true
  try {
    const userId = userInfo.value.userId
    const { data } = await axios.get(`/messages/user/${userId}`)
    messageList.value = data
    pagination.total = data.length
  } catch (error) {
    message.error('获取留言列表失败')
    console.error('获取留言列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 提交留言
const handleSubmit = async () => {
  // 表单验证
  if (!messageForm.content) {
    message.error('请输入留言内容')
    return
  }

  submitting.value = true
  try {
    const userId = userInfo.value.userId
    await axios.post('/messages', {
      userId,
      content: messageForm.content,
      status: 'PENDING',
      type: messageForm.type,
    })

    message.success('留言提交成功')
    // 清空表单
    messageForm.content = ''
    // 重新获取留言列表
    fetchMessages()
  } catch (error) {
    message.error('留言提交失败')
    console.error('留言提交失败:', error)
  } finally {
    submitting.value = false
  }
}

// 表格分页变化时的回调
const handleTableChange = (pag) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  fetchMessages()
}

// 映射留言类型到显示文本
const messageTypeText = (type) => {
  const map = {
    suggestion: '功能建议',
    content: '内容反馈',
    technical: '技术问题',
    other: '其他',
  }
  return map[type] || type
}

// 映射留言状态到显示文本和颜色
const messageStatusInfo = (status) => {
  const map = {
    PENDING: { text: '待处理', color: 'orange' },
    PROCESSED: { text: '已回复', color: 'green' },
    CLOSED: { text: '已关闭', color: 'gray' },
  }
  return map[status] || { text: status, color: 'default' }
}

const handleDelete = async (id) => {
  await axios.delete(`/messages/${id}`)
  message.success('留言删除成功')
  fetchMessages()
}

onMounted(() => {
  fetchMessages()
})
</script>

<template>
  <div class="message-container page-container">
    <div class="content-card">
      <h1 class="page-title">留言反馈</h1>

      <!-- 留言表单 -->
      <div class="message-form">
        <a-form layout="vertical">
          <a-form-item label="留言类型" required>
            <a-radio-group v-model:value="messageForm.type">
              <a-radio-button
                v-for="item in messageTypeOptions"
                :key="item.value"
                :value="item.value"
              >
                {{ item.label }}
              </a-radio-button>
            </a-radio-group>
          </a-form-item>

          <a-form-item label="留言内容" required>
            <a-textarea
              v-model:value="messageForm.content"
              placeholder="请输入您的留言内容，我们会尽快回复..."
              :rows="4"
              :maxLength="500"
              show-count
            />
          </a-form-item>

          <a-form-item>
            <a-button type="primary" :loading="submitting" @click="handleSubmit">
              提交留言
            </a-button>
          </a-form-item>
        </a-form>
      </div>

      <a-divider orientation="left">留言历史</a-divider>

      <!-- 留言历史列表 -->
      <div class="message-list">
        <a-spin :spinning="loading">
          <a-empty v-if="messageList.length === 0" description="暂无留言记录" />

          <a-list
            v-else
            item-layout="vertical"
            :data-source="messageList"
            @change="handleTableChange"
          >
            <template #renderItem="{ item }">
              <a-list-item>
                <template #extra>
                  <a-tag :color="messageStatusInfo(item.status).color">
                    {{ messageStatusInfo(item.status).text }}
                  </a-tag>
                  <a-button type="link" @click="handleDelete(item.id)">
                      <delete-outlined />
                    </a-button>
                </template>

                <a-list-item-meta>
                  
                  <template #title>
                    <div class="message-meta">
                      <span class="message-type">[ {{ messageTypeText(item.type) }} ]</span>
                      <span class="message-time">{{ dayjs(item.createdAt).format('YYYY-MM-DD HH:mm') }}</span>
                    </div>
                  </template>
                  <template #description>
                    <div class="message-content">{{ item.content }}</div>
                  </template>
                </a-list-item-meta>

                <!-- 回复内容 -->
                <div v-if="item.replyContent" class="message-reply">
                  <div class="reply-header">
                    <a-tag color="blue">管理员回复</a-tag>
                    <span class="reply-time">{{ item.replyTime }}</span>
                  </div>
                  <div class="reply-content">{{ item.replyContent }}</div>
                </div>
              </a-list-item>
            </template>
          </a-list>
        </a-spin>
      </div>
    </div>
  </div>
</template>

<style scoped>
.message-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 24px;
}

.page-title {
  font-size: 24px;
  margin-bottom: 24px;
  color: #333;
}

.message-form {
  margin-bottom: 24px;
  padding: 24px;
  background: #fff;
  border-radius: 4px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.message-list {
  background: #fff;
  border-radius: 4px;
  padding: 0 16px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.message-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.message-type {
  font-weight: bold;
  color: #666;
}

.message-time {
  font-size: 12px;
  color: #999;
}

.message-content {
  white-space: pre-wrap;
  margin-top: 8px;
  color: #333;
}

.message-reply {
  background-color: #f9f9f9;
  padding: 12px 16px;
  margin-top: 12px;
  border-radius: 4px;
  border-left: 3px solid #1890ff;
}

.reply-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.reply-time {
  font-size: 12px;
  color: #999;
}

.reply-content {
  color: #333;
}
</style>
