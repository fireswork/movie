<script setup>
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import * as echarts from 'echarts'

// 加载状态
const loading = ref(false)
// 日期范围
const dateRange = ref(['', ''])
// 时间选项
const timeOption = ref('month')

// 概览数据
const overviewData = reactive({
  userCount: 0,
  movieCount: 0,
  orderCount: 0,
  commentCount: 0,
  todayNewUsers: 0,
  todayNewOrders: 0,
  todayIncome: 0,
  conversion: 0,
})

// 销售数据
const salesData = ref([])
// 类型分布数据
const categoryData = ref([])
// 电影排行数据
const movieRankData = ref([])
// 职业偏好数据
const jobPreferenceData = ref([])

// 时间选项列表
const timeOptions = [
  { label: '今日', value: 'today' },
  { label: '本周', value: 'week' },
  { label: '本月', value: 'month' },
  { label: '本季度', value: 'quarter' },
  { label: '本年', value: 'year' },
]

// 获取概览数据
const fetchOverviewData = () => {
  // 模拟API请求
  setTimeout(() => {
    overviewData.userCount = 2546
    overviewData.movieCount = 358
    overviewData.orderCount = 15879
    overviewData.commentCount = 35624
    overviewData.todayNewUsers = 58
    overviewData.todayNewOrders = 196
    overviewData.todayIncome = 7895.5
    overviewData.conversion = 3.2
  }, 500)
}

// 获取销售数据
const fetchSalesData = () => {
  // 模拟API请求
  setTimeout(() => {
    const data = []
    const today = new Date()
    const monthDays = new Date(today.getFullYear(), today.getMonth() + 1, 0).getDate()

    for (let i = 1; i <= monthDays; i++) {
      const date = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(i).padStart(2, '0')}`

      // 随机生成销售额，渐进增长
      let sales = 1000 + Math.floor(Math.random() * 500) + i * 30

      // 周末销售额增加
      const dayOfWeek = new Date(date).getDay()
      if (dayOfWeek === 0 || dayOfWeek === 6) {
        sales = sales * 1.5
      }

      data.push({
        date,
        sales: Math.floor(sales),
        orders: Math.floor(sales / 50 + Math.random() * 10),
      })
    }

    salesData.value = data
  }, 500)
}

// 获取电影类型分布数据
const fetchCategoryData = () => {
  // 模拟API请求
  setTimeout(() => {
    categoryData.value = [
      { type: '动作', value: 28, color: '#1890ff' },
      { type: '喜剧', value: 22, color: '#13c2c2' },
      { type: '爱情', value: 18, color: '#f5222d' },
      { type: '科幻', value: 15, color: '#faad14' },
      { type: '动画', value: 10, color: '#722ed1' },
      { type: '恐怖', value: 7, color: '#eb2f96' },
      { type: '其他', value: 5, color: '#bfbfbf' },
    ]
  }, 500)
}

// 获取电影排行数据
const fetchMovieRankData = () => {
  // 模拟API请求
  setTimeout(() => {
    movieRankData.value = [
      { name: '肖申克的救赎', count: 856, percent: 100 },
      { name: '霸王别姬', count: 745, percent: 87 },
      { name: '阿甘正传', count: 689, percent: 80 },
      { name: '泰坦尼克号', count: 612, percent: 71 },
      { name: '这个杀手不太冷', count: 578, percent: 67 },
      { name: '美丽人生', count: 542, percent: 63 },
      { name: '千与千寻', count: 501, percent: 58 },
      { name: '辛德勒的名单', count: 473, percent: 55 },
      { name: '盗梦空间', count: 445, percent: 52 },
      { name: '星际穿越', count: 423, percent: 49 },
    ]
  }, 500)
}

// 获取职业电影偏好数据
const fetchJobPreferenceData = () => {
  // 模拟API请求
  setTimeout(() => {
    // 模拟热力图数据
    const jobs = ['学生', '程序员', '教师', '医生', '公务员', '销售', '设计师', '自由职业']
    const categories = ['动作', '喜剧', '爱情', '科幻', '动画', '恐怖']

    const data = []

    for (let i = 0; i < jobs.length; i++) {
      for (let j = 0; j < categories.length; j++) {
        // 随机生成偏好度
        let value = Math.floor(Math.random() * 100)

        // 为特定职业和电影类型设置偏好
        if (jobs[i] === '程序员' && categories[j] === '科幻') {
          value = 85 + Math.floor(Math.random() * 15)
        } else if (jobs[i] === '学生' && categories[j] === '动画') {
          value = 80 + Math.floor(Math.random() * 15)
        } else if (jobs[i] === '销售' && categories[j] === '喜剧') {
          value = 75 + Math.floor(Math.random() * 15)
        }

        data.push({
          job: jobs[i],
          category: categories[j],
          value,
        })
      }
    }

    jobPreferenceData.value = data
  }, 500)
}

// 时间范围变化
const handleTimeRangeChange = (value) => {
  timeOption.value = value

  // 根据选择的时间范围重新加载数据
  fetchData()
}

// 加载所有数据
const fetchData = () => {
  loading.value = true

  Promise.all([
    fetchOverviewData(),
    fetchSalesData(),
    fetchCategoryData(),
    fetchMovieRankData(),
    fetchJobPreferenceData(),
  ])
    .then(() => {
      loading.value = false
    })
    .catch((error) => {
      message.error('加载数据失败')
      loading.value = false
    })
}

// 格式化数字
const formatNumber = (num) => {
  return num.toString().replace(/(\d)(?=(?:\d{3})+$)/g, '$1,')
}

// 格式化金额
const formatCurrency = (amount) => {
  return '¥' + amount.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,')
}

onMounted(() => {
  fetchData()
})
</script>

<template>
  <div class="dashboard-container page-container">
    <div class="content-card">
      <div class="dashboard-header">
        <div class="header-title">
          <h2>数据看板</h2>
        </div>
        <div class="header-actions">
          <a-radio-group v-model:value="timeOption" @change="handleTimeRangeChange">
            <a-radio-button v-for="option in timeOptions" :key="option.value" :value="option.value">
              {{ option.label }}
            </a-radio-button>
          </a-radio-group>
        </div>
      </div>

      <a-spin :spinning="loading">
        <!-- 数据概览卡片 -->
        <div class="overview-cards">
          <a-row :gutter="16">
            <a-col :xs="24" :sm="12" :md="6">
              <a-card class="overview-card" :bodyStyle="{ padding: '20px' }">
                <div class="overview-item">
                  <div class="overview-item-title">注册用户数</div>
                  <div class="overview-item-value">{{ formatNumber(overviewData.userCount) }}</div>
                </div>
              </a-card>
            </a-col>

            <a-col :xs="24" :sm="12" :md="6">
              <a-card class="overview-card" :bodyStyle="{ padding: '20px' }">
                <div class="overview-item">
                  <div class="overview-item-title">电影总数</div>
                  <div class="overview-item-value">{{ formatNumber(overviewData.movieCount) }}</div>
                </div>
              </a-card>
            </a-col>

            <a-col :xs="24" :sm="12" :md="6">
              <a-card class="overview-card" :bodyStyle="{ padding: '20px' }">
                <div class="overview-item">
                  <div class="overview-item-title">订单总数</div>
                  <div class="overview-item-value">{{ formatNumber(overviewData.orderCount) }}</div>
                </div>
              </a-card>
            </a-col>

            <a-col :xs="24" :sm="12" :md="6">
              <a-card class="overview-card" :bodyStyle="{ padding: '20px' }">
                <div class="overview-item">
                  <div class="overview-item-title">今日销售额</div>
                  <div class="overview-item-value">
                    {{ formatCurrency(overviewData.todayIncome) }}
                  </div>
                </div>
              </a-card>
            </a-col>
          </a-row>
        </div>

        <!-- 图表区域 -->
        <div class="chart-section">
          <a-row :gutter="16">
            <a-col :span="24">
              <a-card title="月度销售趋势" class="chart-card">
                <div id="sales-chart" class="chart-container">
                  <!-- 这里可以使用 ECharts 或其他图表库渲染折线图 -->
                  <div class="chart-placeholder">
                    <div class="chart-placeholder-line">
                      <div
                        class="chart-placeholder-point"
                        v-for="(item, index) in salesData"
                        :key="index"
                        :style="{ height: item.sales / 20 + 'px' }"
                      ></div>
                    </div>
                    <div class="chart-placeholder-label">折线图：月度销售额和订单数趋势</div>
                  </div>
                </div>
              </a-card>
            </a-col>
          </a-row>

          <a-row :gutter="16" style="margin-top: 16px">
            <a-col :xs="24" :md="12">
              <a-card title="电影类型分布" class="chart-card">
                <div id="category-chart" class="chart-container">
                  <!-- 这里可以使用 ECharts 或其他图表库渲染饼图 -->
                  <div class="chart-placeholder">
                    <div class="chart-placeholder-pie">
                      <div class="chart-placeholder-label">饼图：电影类型分布占比</div>
                      <div class="chart-placeholder-legend">
                        <div
                          v-for="item in categoryData"
                          :key="item.type"
                          class="chart-legend-item"
                        >
                          <span
                            class="chart-legend-color"
                            :style="{ backgroundColor: item.color }"
                          ></span>
                          <span>{{ item.type }}: {{ item.value }}%</span>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </a-card>
            </a-col>

            <a-col :xs="24" :md="12">
              <a-card title="播放量TOP10" class="chart-card">
                <div id="ranking-chart" class="chart-container">
                  <!-- 这里可以使用 ECharts 或其他图表库渲染柱状图 -->
                  <div class="chart-placeholder">
                    <div
                      v-for="item in movieRankData"
                      :key="item.name"
                      class="chart-placeholder-bar"
                    >
                      <div class="chart-bar-label">{{ item.name }}</div>
                      <div class="chart-bar-body">
                        <div class="chart-bar-fill" :style="{ width: item.percent + '%' }"></div>
                      </div>
                      <div class="chart-bar-value">{{ item.count }}</div>
                    </div>
                  </div>
                </div>
              </a-card>
            </a-col>
          </a-row>

          <a-row :gutter="16" style="margin-top: 16px">
            <a-col :span="24">
              <a-card title="职业-电影偏好分析" class="chart-card">
                <div id="heatmap-chart" class="chart-container">
                  <!-- 这里可以使用 ECharts 或其他图表库渲染热力图 -->
                  <div class="chart-placeholder">
                    <div class="chart-placeholder-heatmap">
                      <div class="chart-placeholder-label">热力图：不同职业人群的电影类型偏好</div>
                      <div class="heatmap-legend">
                        <div class="heatmap-color-scale">
                          <div class="color-low"></div>
                          <div class="color-mid"></div>
                          <div class="color-high"></div>
                        </div>
                        <div class="heatmap-scale-labels">
                          <span>低</span>
                          <span>偏好程度</span>
                          <span>高</span>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </a-card>
            </a-col>
          </a-row>
        </div>
      </a-spin>
    </div>
  </div>
</template>

<style scoped>
.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.overview-cards {
  margin-bottom: 24px;
}

.overview-card {
  margin-bottom: 16px;
  border-radius: 4px;
  overflow: hidden;
}

.overview-item {
  display: flex;
  flex-direction: column;
}

.overview-item-title {
  font-size: 14px;
  color: rgba(0, 0, 0, 0.65);
  margin-bottom: 4px;
}

.overview-item-value {
  font-size: 24px;
  color: rgba(0, 0, 0, 0.85);
  margin-bottom: 16px;
  font-weight: 500;
}

.overview-item-footer {
  font-size: 12px;
  color: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: center;
}

.chart-card {
  border-radius: 4px;
  overflow: hidden;
}

.chart-container {
  height: 280px;
  padding: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 图表占位符样式，实际项目中应使用真实图表库 */
.chart-placeholder {
  width: 100%;
  height: 100%;
  padding: 20px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
  position: relative;
}

.chart-placeholder-label {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: rgba(0, 0, 0, 0.45);
  text-align: center;
  font-size: 14px;
  background: rgba(255, 255, 255, 0.8);
  padding: 4px 8px;
  border-radius: 4px;
  white-space: nowrap;
}

.chart-placeholder-line {
  width: 100%;
  height: 150px;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
}

.chart-placeholder-point {
  width: 8px;
  height: 50px;
  background-color: #1890ff;
  border-radius: 2px;
  margin: 0 2px;
}

.chart-placeholder-pie {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
}

.chart-placeholder-legend {
  display: flex;
  flex-wrap: wrap;
  margin-top: 16px;
  justify-content: center;
  gap: 12px;
}

.chart-legend-item {
  display: flex;
  align-items: center;
  margin-right: 16px;
  font-size: 12px;
}

.chart-legend-color {
  width: 12px;
  height: 12px;
  border-radius: 2px;
  margin-right: 4px;
}

.chart-placeholder-bar {
  width: 100%;
  height: 24px;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
}

.chart-bar-label {
  width: 120px;
  text-align: right;
  padding-right: 12px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-size: 12px;
}

.chart-bar-body {
  flex: 1;
  height: 16px;
  background-color: #f0f0f0;
  border-radius: 4px;
  overflow: hidden;
}

.chart-bar-fill {
  height: 100%;
  background-color: #1890ff;
}

.chart-bar-value {
  width: 40px;
  text-align: right;
  padding-left: 8px;
  font-size: 12px;
  color: rgba(0, 0, 0, 0.65);
}

.chart-placeholder-heatmap {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
}

.heatmap-legend {
  position: absolute;
  bottom: 24px;
  right: 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.heatmap-color-scale {
  width: 120px;
  height: 16px;
  display: flex;
  border-radius: 4px;
  overflow: hidden;
}

.color-low {
  flex: 1;
  background: linear-gradient(to right, #e6f7ff, #69c0ff);
}

.color-mid {
  flex: 1;
  background: linear-gradient(to right, #69c0ff, #1890ff);
}

.color-high {
  flex: 1;
  background: linear-gradient(to right, #1890ff, #096dd9);
}

.heatmap-scale-labels {
  width: 120px;
  display: flex;
  justify-content: space-between;
  margin-top: 4px;
  font-size: 12px;
  color: rgba(0, 0, 0, 0.45);
}

@media (max-width: 768px) {
  .dashboard-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .header-actions {
    margin-top: 16px;
  }

  .chart-container {
    height: 240px;
  }

  .chart-bar-label {
    width: 80px;
  }
}
</style>
