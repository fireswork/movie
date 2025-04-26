<script setup>
import { ref, computed, onMounted } from 'vue'
import { RouterView, useRouter, useRoute } from 'vue-router'
import 'animate.css'
import {
  HomeOutlined,
  UserOutlined,
  VideoCameraOutlined,
  AppstoreOutlined,
  CrownOutlined,
  RocketOutlined,
  HeartOutlined,
  DashboardOutlined,
  LogoutOutlined,
  MessageOutlined,
} from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()
const collapsed = ref(false)

const userInfos = JSON.parse(localStorage.getItem('user')) || {}
// 根据存储的用户信息判断是否是管理员
const isAdmin = ref(true)
const userInfo = ref(userInfos)

// 判断当前是否是登录/注册页面
const isAuthPage = computed(() => {
  return route.path === '/login' || route.path === '/register'
})

// 用户菜单
const userMenuItems = [
  { key: 'home', title: '首页', icon: HomeOutlined, path: '/' },
  { key: 'preview', title: '电影预告', icon: VideoCameraOutlined, path: '/preview' },
  { key: 'movies', title: '电影库', icon: AppstoreOutlined, path: '/movies' },
  { key: 'collection', title: '我的收藏', icon: HeartOutlined, path: '/collection' },
  { key: 'ranking', title: '电影榜单', icon: CrownOutlined, path: '/ranking' },
  { key: 'recommend', title: '智能推荐', icon: RocketOutlined, path: '/recommend' },
  { key: 'message', title: '留言反馈', icon: MessageOutlined, path: '/message' },
]

// 管理员菜单
const adminMenuItems = [
  { key: 'dashboard', title: '数据看板', icon: DashboardOutlined, path: '/admin/dashboard' },
  { key: 'admin-users', title: '用户管理', icon: UserOutlined, path: '/admin/users' },
  { key: 'admin-movies', title: '电影管理', icon: VideoCameraOutlined, path: '/admin/movies' },
  { key: 'admin-category', title: '分类管理', icon: AppstoreOutlined, path: '/admin/category' },
  { key: 'admin-region', title: '地区管理', icon: AppstoreOutlined, path: '/admin/region' },
  { key: 'admin-orders', title: '订单管理', icon: CrownOutlined, path: '/admin/orders' },
  { key: 'admin-comments', title: '评论管理', icon: RocketOutlined, path: '/admin/comments' },
  { key: 'admin-messages', title: '留言管理', icon: MessageOutlined, path: '/admin/messages' },
]

const handleMenuClick = (item) => {
  router.push(item.path)
}

const logout = () => {
  localStorage.removeItem('user')
  router.push('/login')
}
</script>

<template>
  <!-- 登录/注册页面不显示布局 -->
  <template v-if="isAuthPage">
    <RouterView />
  </template>

  <!-- 管理员使用标准管理系统布局 -->
  <a-layout v-else-if="isAdmin" class="admin-layout" style="min-height: 100vh">
    <a-layout-sider
      v-model:collapsed="collapsed"
      collapsible
      class="animate__animated animate__slideInLeft"
    >
      <div class="logo">
        <h2 v-if="!collapsed">电影管理系统</h2>
        <h2 v-else>影</h2>
      </div>
      <a-menu theme="dark" mode="inline" :selected-keys="[route.name]">
        <a-menu-item
          v-for="item in adminMenuItems"
          :key="item.key"
          @click="() => handleMenuClick(item)"
        >
          <template #icon><component :is="item.icon" /></template>
          <span>{{ item.title }}</span>
        </a-menu-item>
      </a-menu>
    </a-layout-sider>
    <a-layout>
      <a-layout-header class="admin-header">
        <a-space>
          <a-dropdown>
            <a-button type="text">
              <a-space>
                <a-avatar size="small" :style="{ backgroundColor: '#87d068' }" icon="user" />
                <span>管理员</span>
              </a-space>
            </a-button>
            <template #overlay>
              <a-menu>
                <a-menu-item key="profile">
                  <user-outlined />
                  个人资料
                </a-menu-item>
                <a-menu-item key="logout" @click="logout">
                  <logout-outlined />
                  退出登录
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </a-space>
      </a-layout-header>
      <a-layout-content class="app-content">
        <RouterView />
      </a-layout-content>
    </a-layout>
  </a-layout>

  <!-- 用户使用C端风格布局 -->
  <div v-else class="user-layout">
    <!-- 顶部导航栏 -->
    <header class="user-header animate__animated animate__fadeInDown">
      <div class="header-container">
        <!-- 左侧Logo部分 -->
        <div class="logo-section">
          <div class="logo-title">电影世界</div>
        </div>

        <!-- 中间菜单部分 -->
        <div class="menu-section">
          <a-menu mode="horizontal" :selected-keys="[route.name]">
            <a-menu-item
              v-for="item in userMenuItems"
              :key="item.key"
              @click="() => handleMenuClick(item)"
            >
              <template #icon><component :is="item.icon" /></template>
              <span>{{ item.title }}</span>
            </a-menu-item>
          </a-menu>
        </div>

        <!-- 右侧用户部分 -->
        <div class="user-section">
          <a-dropdown>
            <a-button type="text" class="user-button">
              <a-space>
                <a-avatar :style="{ backgroundColor: '#1890ff' }">
                  {{ userInfo?.username?.substring(0, 1)?.toUpperCase() || 'U' }}
                </a-avatar>
                <span>{{ userInfo?.username || '用户' }}</span>
              </a-space>
            </a-button>
            <template #overlay>
              <a-menu>
                <a-menu-item key="profile">
                  <user-outlined />
                  个人资料
                </a-menu-item>
                <a-menu-item key="logout" @click="logout">
                  <logout-outlined />
                  退出登录
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
      </div>
    </header>

    <!-- 主内容区 -->
    <main class="user-content">
      <div class="content-container">
        <RouterView />
      </div>
    </main>
  </div>
</template>

<style lang="less">
body {
  margin: 0;
  padding: 0;
  font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
  background-color: #f5f5f5;
}

// 管理员样式
.admin-layout {
  .logo {
    height: 32px;
    margin: 16px;
    color: white;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .admin-header {
    background: #fff;
    padding: 0 16px;
    display: flex;
    justify-content: flex-end;
    align-items: center;
  }
}

// 用户界面样式
.user-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.user-header {
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 0;
  height: 64px;
  position: fixed;
  width: 100%;
  top: 0;
  z-index: 1000;
}

.header-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
}

.logo-section {
  width: 150px;
}

.logo-title {
  font-size: 20px;
  font-weight: bold;
  background: linear-gradient(90deg, #1890ff, #722ed1);
}

.menu-section {
  flex: 1;
  display: flex;
  justify-content: center;

  .ant-menu-horizontal {
    border-bottom: none;
  }
}

.user-section {
  width: 150px;
  display: flex;
  justify-content: flex-end;
}

.user-content {
  margin-top: 64px;
  flex: 1;
  padding: 24px 0;
}

.content-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
  background: #fff;
  min-height: calc(100vh - 112px);
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.app-content {
  margin: 16px;
  padding: 24px;
  background: #fff;
  min-height: 280px;
  overflow-y: auto;
  height: calc(100vh - 184px);
}

.user-button {
  height: auto;
}

// 媒体查询适配移动端
@media screen and (max-width: 768px) {
  .header-container {
    padding: 0 12px;
    flex-direction: column;
    height: auto;
  }

  .logo-section,
  .menu-section,
  .user-section {
    width: 100%;
    justify-content: center;
    margin: 8px 0;
  }

  .user-header {
    height: auto;
    padding: 8px 0;
  }

  .user-content {
    margin-top: 120px;
  }
}
</style>
