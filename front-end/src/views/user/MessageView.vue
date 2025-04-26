<script setup>
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'

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
const fetchMessages = () => {
  loading.value = true

  // 模拟API请求
  setTimeout(() => {
    // 模拟留言数据
    const mockMessages = []
    const total = 23 // 模拟总数

    for (let i = 1; i <= total; i++) {
      const statusOptions = ['pending', 'replied', 'closed']
      const status = statusOptions[Math.floor(Math.random() * statusOptions.length)]

      const typeOptions = ['suggestion', 'content', 'technical', 'other']
      const type = typeOptions[Math.floor(Math.random() * typeOptions.length)]

      const messageTemplates = [
        '希望能增加更多的电影分类，比如体育类电影专区',
        '最近上映的某某电影质量不太好，希望能提高上架电影的筛选标准',
        '网站加载速度有些慢，希望能优化一下',
        '能否增加更多支付方式，比如支持支付宝',
        '页面上的部分链接点击无反应',
        '希望能增加电影推荐功能，根据我看过的电影推荐新片',
        '有些电影描述与实际内容不符，请审核',
        '能否增加电影预告片功能',
        '希望可以增加电影评分排行榜功能',
        '个人中心页面布局有点混乱，希望优化',
      ]

      const randomMessage = messageTemplates[Math.floor(Math.random() * messageTemplates.length)]

      // 随机生成一个月内的时间
      const date = new Date()
      date.setDate(date.getDate() - Math.floor(Math.random() * 30))
      const createTime = date.toISOString().replace('T', ' ').substring(0, 19)

      // 如果状态为已回复，生成回复内容和时间
      let replyContent = null
      let replyTime = null

      if (status === 'replied') {
        const replyTemplates = [
          '感谢您的反馈，我们会认真考虑您的建议',
          '您好，我们已经收到您的反馈，技术团队正在处理相关问题',
          '非常感谢您的建议，我们会在下一版本中考虑添加该功能',
          '您反馈的问题已经修复，请刷新页面后重试',
          '我们已经将您的意见转达给相关部门，感谢您的支持',
        ]

        replyContent = replyTemplates[Math.floor(Math.random() * replyTemplates.length)]

        // 回复时间比创建时间晚1-3天
        const replyDate = new Date(date)
        replyDate.setDate(replyDate.getDate() + Math.floor(Math.random() * 3) + 1)
        replyTime = replyDate.toISOString().replace('T', ' ').substring(0, 19)
      }

      mockMessages.push({
        id: i,
        content: randomMessage,
        type,
        status,
        createTime,
        replyContent,
        replyTime,
      })
    }

    // 按创建时间倒序排序
    mockMessages.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))

    // 分页
    const start = (pagination.current - 1) * pagination.pageSize
    const end = start + pagination.pageSize
    const paginatedData = mockMessages.slice(start, end)

    messageList.value = paginatedData
    pagination.total = mockMessages.length
    loading.value = false
  }, 800)
}

// 提交留言
const handleSubmit = () => {
  // 表单验证
  if (!messageForm.content) {
    message.error('请输入留言内容')
    return
  }

  submitting.value = true

  // 模拟API请求
  setTimeout(() => {
    message.success('留言提交成功')

    // 清空表单
    messageForm.content = ''

    // 重新获取留言列表
    fetchMessages()

    submitting.value = false
  }, 1000)
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
    pending: { text: '待处理', color: 'orange' },
    replied: { text: '已回复', color: 'green' },
    closed: { text: '已关闭', color: 'gray' },
  }
  return map[status] || { text: status, color: 'default' }
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
            :pagination="pagination"
            :data-source="messageList"
            @change="handleTableChange"
          >
            <template #renderItem="{ item }">
              <a-list-item>
                <template #extra>
                  <a-tag :color="messageStatusInfo(item.status).color">
                    {{ messageStatusInfo(item.status).text }}
                  </a-tag>
                </template>

                <a-list-item-meta>
                  <template #title>
                    <div class="message-meta">
                      <span class="message-type">[ {{ messageTypeText(item.type) }} ]</span>
                      <span class="message-time">{{ item.createTime }}</span>
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
