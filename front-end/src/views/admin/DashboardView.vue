<script setup>
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import * as echarts from 'echarts'
import { UserOutlined, VideoCameraOutlined } from '@ant-design/icons-vue'
import axios from '@/services/request'

// 概览数据
const overview = ref({
  userCount: 0,
  movieCount: 0,
})

// 销售数据
const salesData = ref([])
// 电影互动数据
const movieInteractions = ref([])
// 图表引用
const salesChart = ref(null)
const preferencesChart = ref(null)

// 表格列定义
const movieColumns = [
  {
    title: '电影名称',
    dataIndex: 'title',
    key: 'title',
  },
  {
    title: '点赞数',
    dataIndex: 'likes',
    key: 'likes',
    sorter: (a, b) => a.likes - b.likes,
  },
]

// 用户数据映射
const userMap = ref({})

// 分类映射
const categoryMap = ref({})

// 获取分类数据
const fetchCategories = async () => {
  try {
    const { data } = await axios.get('/categories')
    // 构建分类ID到名称的映射
    categoryMap.value = data.reduce((acc, category) => {
      acc[category.id] = category.name
      return acc
    }, {})
  } catch (error) {
    console.error('获取分类数据失败:', error)
    message.error('获取分类数据失败')
  }
}

// 获取用户数据
const fetchUsers = async () => {
  try {
    const { data } = await axios.get('/users')
    // 构建用户ID到职业的映射
    userMap.value = data.content.reduce((acc, user) => {
      acc[user.id] = user.profession
      return acc
    }, {})
  } catch (error) {
    console.error('获取用户数据失败:', error)
    message.error('获取用户数据失败')
  }
}

// 获取概览数据
const fetchOverview = async () => {
  try {
    const { data } = await axios.get('/statistics/overview')
    overview.value = data
  } catch (error) {
    console.error('获取总览数据失败:', error)
    message.error('获取总览数据失败')
  } finally {
  }
}

// 获取销售数据
const fetchSalesData = async () => {
  try {
    const { data } = await axios.get('/statistics/sales')
    salesData.value = data
    initSalesChart()
  } catch (error) {
    console.error('获取销售数据失败:', error)
    message.error('获取销售数据失败')
  } finally {
  }
}

// 初始化销售图表
const initSalesChart = () => {
  const chartDom = salesChart.value
  if (!chartDom) return

  const myChart = echarts.init(chartDom)
  const dates = salesData.value.map((item) => item.date)
  const amounts = salesData.value.map((item) => item.amount)

  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: (params) => {
        const data = params[0]
        return `${data.name}<br/>销售额: ${formatCurrency(data.value)}`
      }
    },
    xAxis: {
      type: 'category',
      data: dates,
      axisLabel: {
        rotate: 45,
        interval: 0,
        fontSize: 12
      }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: (value) => formatCurrency(value),
        fontSize: 12
      }
    },
    series: [
      {
        name: '销售额',
        type: 'bar',
        data: amounts,
        itemStyle: {
          color: '#1890ff'
        },
        barWidth: '60%'
      }
    ]
  }

  myChart.setOption(option)
}

// 获取电影互动数据
const fetchMovieInteractions = async () => {
  try {
    const { data } = await axios.get('/statistics/movie-interactions')
    movieInteractions.value = data
  } catch (error) {
    console.error('获取电影互动数据失败:', error)
    message.error('获取电影互动数据失败')
  } finally {
  }
}

// 获取用户偏好数据
const fetchUserPreferences = async () => {
  try {
    const { data } = await axios.get('/statistics/user-preferences')
    initPreferencesChart(data)
  } catch (error) {
    console.error('获取用户偏好数据失败:', error)
    message.error('获取用户偏好数据失败')
  } finally {
  }
}

// 初始化偏好图表
const initPreferencesChart = (data) => {
  const chartDom = preferencesChart.value
  if (!chartDom) return

  const myChart = echarts.init(chartDom)
  
  // 获取所有职业和分类
  const professions = [...new Set(data.map(item => item.job))]
  const allCategories = new Set()
  data.forEach(item => {
    Object.keys(item.preferences).forEach(categoryId => {
      allCategories.add(categoryId)
    })
  })
  const categories = [...allCategories]

  // 准备数据
  const series = categories.map(categoryId => {
    const categoryName = categoryMap.value[categoryId] || `分类${categoryId}`
    return {
      name: categoryName,
      type: 'bar',
      stack: 'total',
      label: {
        show: true,
        position: 'inside',
        formatter: (params) => {
          if (params.value === 0) return ''
          return params.value
        }
      },
      emphasis: {
        focus: 'series'
      },
      data: professions.map(profession => {
        const professionData = data.find(item => item.job === profession)
        return professionData?.preferences[categoryId] || 0
      })
    }
  })

  const option = {
    title: {
      text: '不同职业群体电影偏好分布',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {
      type: 'scroll',
      orient: 'horizontal',
      top: 30,
      left: 'center'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '80px',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: professions,
      axisLabel: {
        interval: 0,
        rotate: 30
      }
    },
    yAxis: {
      type: 'value',
      name: '观影数量'
    },
    series
  }

  myChart.setOption(option)
}

// 格式化金额
const formatCurrency = (amount) => {
  return '¥' + amount.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,')
}

// 加载所有数据
const fetchData = async () => {
  await fetchCategories() // 先获取分类数据
  await fetchUsers() // 获取用户数据
  fetchOverview()
  fetchSalesData()
  fetchMovieInteractions()
  fetchUserPreferences()
}

onMounted(() => {
  fetchData()

  window.addEventListener('resize', () => {
    const salesChartInstance = echarts.getInstanceByDom(salesChart.value)
    const preferencesChartInstance = echarts.getInstanceByDom(preferencesChart.value)
    if (salesChartInstance) {
      salesChartInstance.resize()
    }
    if (preferencesChartInstance) {
      preferencesChartInstance.resize()
    }
  })
})
</script>

<template>
  <div class="dashboard-container page-container">
    <div class="content-card">
      <div class="dashboard-header">
        <div class="header-title">
          <h2>数据看板</h2>
        </div>
      </div>

      <a-spin :spinning="false">
        <a-row :gutter="[16, 16]">
          <a-col :span="12">
            <a-card class="overview-card">
              <template #title>
                <div class="card-title">
                  <user-outlined />
                  <span>用户总数</span>
                </div>
              </template>
              <div class="overview-value">
                <span class="number">{{ overview.userCount }}</span>
                <span class="label">位注册用户</span>
              </div>
            </a-card>
          </a-col>
          <a-col :span="12">
            <a-card class="overview-card">
              <template #title>
                <div class="card-title">
                  <video-camera-outlined />
                  <span>电影总数</span>
                </div>
              </template>
              <div class="overview-value">
                <span class="number">{{ overview.movieCount }}</span>
                <span class="label">部电影</span>
              </div>
            </a-card>
          </a-col>
        </a-row>

        <a-row :gutter="[16, 16]" style="margin-top: 16px">
          <a-col :span="24">
            <a-card class="stat-card">
              <template #title>最受欢迎电影</template>
              <a-table
                :columns="movieColumns"
                :data-source="movieInteractions"
                :pagination="false"
                size="small"
              />
            </a-card>
          </a-col>
        </a-row>

        <a-row :gutter="[16, 16]" style="margin-top: 16px">
          <a-col :span="24">
            <a-card class="chart-card">
              <template #title>最近30天销售额</template>
              <div ref="salesChart" style="height: 400px"></div>
            </a-card>
          </a-col>
        </a-row>

        <a-row :gutter="[16, 16]" style="margin-top: 16px">
          <a-col :span="24">
            <a-card class="chart-card">
              <div ref="preferencesChart" style="height: 500px"></div>
            </a-card>
          </a-col>
        </a-row>
      </a-spin>
    </div>
  </div>
</template>

<style scoped>
.dashboard-container {
  padding: 24px;
  background: #f0f2f5;
}

.content-card {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.dashboard-header {
  margin-bottom: 24px;
}

.header-title h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 500;
  color: rgba(0, 0, 0, 0.85);
}

.overview-card {
  height: 100%;
  border-radius: 8px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.overview-card :deep(.ant-card-head) {
  border-bottom: none;
  padding: 16px 24px 0;
}

.overview-card :deep(.ant-card-body) {
  padding: 24px;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: rgba(0, 0, 0, 0.45);
}

.card-title .anticon {
  font-size: 16px;
}

.overview-value {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.overview-value .number {
  font-size: 36px;
  font-weight: 600;
  color: #1890ff;
  line-height: 1;
}

.overview-value .label {
  font-size: 14px;
  color: rgba(0, 0, 0, 0.45);
}

.stat-card {
  height: 100%;
  border-radius: 8px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.stat-card :deep(.ant-card-head) {
  border-bottom: 1px solid #f0f0f0;
  padding: 0 16px;
}

.stat-card :deep(.ant-card-body) {
  padding: 16px;
}

.chart-card {
  height: 100%;
  border-radius: 8px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.chart-card :deep(.ant-card-body) {
  padding: 24px;
}

@media (max-width: 768px) {
  .dashboard-container {
    padding: 16px;
  }

  .content-card {
    padding: 16px;
  }
}
</style>
