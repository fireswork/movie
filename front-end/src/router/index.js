import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // 用户端路由
    {
      path: '/',
      name: 'home',
      component: () => import('../views/user/HomeView.vue'),
      meta: { title: '首页' },
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/auth/LoginView.vue'),
      meta: { title: '登录' },
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/auth/RegisterView.vue'),
      meta: { title: '注册' },
    },
    {
      path: '/preview',
      name: 'preview',
      component: () => import('../views/user/PreviewView.vue'),
      meta: { title: '电影预告' },
    },
    {
      path: '/movies',
      name: 'movies',
      component: () => import('../views/user/MoviesView.vue'),
      meta: { title: '电影库' },
    },
    {
      path: '/movie/:id',
      name: 'movie-detail',
      component: () => import('../views/user/MovieDetailView.vue'),
      meta: { title: '电影详情' },
    },
    {
      path: '/collection',
      name: 'collection',
      component: () => import('../views/user/CollectionView.vue'),
      meta: { title: '我的收藏' },
    },
    {
      path: '/ranking',
      name: 'ranking',
      component: () => import('../views/user/RankingView.vue'),
      meta: { title: '电影榜单' },
    },
    {
      path: '/recommend',
      name: 'recommend',
      component: () => import('../views/user/RecommendView.vue'),
      meta: { title: '智能推荐' },
    },
    {
      path: '/payment/:movieId',
      name: 'payment',
      component: () => import('../views/user/PaymentView.vue'),
      meta: {
        requiresAuth: true
      }
    },
    {
      path: '/message',
      name: 'message',
      component: () => import('../views/user/MessageView.vue'),
      meta: { title: '留言反馈' },
    },

    // 管理员路由
    {
      path: '/admin/dashboard',
      name: 'dashboard',
      component: () => import('../views/admin/DashboardView.vue'),
      meta: { title: '数据看板', requireAdmin: true },
    },
    {
      path: '/admin/users',
      name: 'admin-users',
      component: () => import('../views/admin/UsersView.vue'),
      meta: { title: '用户管理', requireAdmin: true },
    },
    {
      path: '/admin/movies',
      name: 'admin-movies',
      component: () => import('../views/admin/MoviesView.vue'),
      meta: { title: '电影管理', requireAdmin: true },
    },
    {
      path: '/admin/movie/edit',
      name: 'admin-movie-add',
      component: () => import('../views/admin/MovieEditView.vue'),
      meta: { title: '添加电影', requireAdmin: true },
    },
    {
      path: '/admin/movie/edit/:id',
      name: 'admin-movie-edit',
      component: () => import('../views/admin/MovieEditView.vue'),
      meta: { title: '编辑电影', requireAdmin: true },
    },
    {
      path: '/admin/category',
      name: 'admin-category',
      component: () => import('../views/admin/CategoryView.vue'),
      meta: { title: '分类管理', requireAdmin: true },
    },
    {
      path: '/admin/region',
      name: 'admin-region',
      component: () => import('../views/admin/RegionView.vue'),
      meta: { title: '地区管理', requireAdmin: true },
    },
    {
      path: '/admin/orders',
      name: 'admin-orders',
      component: () => import('../views/admin/OrdersView.vue'),
      meta: { title: '订单管理', requireAdmin: true },
    },
    {
      path: '/admin/comments',
      name: 'admin-comments',
      component: () => import('../views/admin/CommentsView.vue'),
      meta: { title: '评论管理', requireAdmin: true },
    },
    {
      path: '/admin/messages',
      name: 'admin-messages',
      component: () => import('../views/admin/MessagesView.vue'),
      meta: { title: '留言管理', requireAdmin: true },
    },
    {
      path: '/admin/carousel',
      name: 'admin-carousel',
      component: () => import('../views/admin/CarouselManagement.vue'),
      meta: { title: '轮播管理', requireAdmin: true },
    },

    // 404路由
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('../views/NotFoundView.vue'),
      meta: { title: '页面未找到' },
    },
  ],
})

// 路由前置守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 电影管理系统` : '电影管理系统'

  // 这里可以添加登录验证和权限验证
  // TODO: 实现登录验证和权限验证

  next()
})

export default router
