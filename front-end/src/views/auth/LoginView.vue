<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { login } from '@/services/api'
import 'animate.css'

const router = useRouter()
const loginForm = reactive({
  username: '',
  password: '',
})

const loading = ref(false)
const formRef = ref(null)

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不少于6个字符', trigger: 'blur' },
  ],
}

const handleLogin = () => {
  formRef.value
    .validate()
    .then(async () => {
      loading.value = true

      try {
        // 发送登录请求
        const response = await login(loginForm.username, loginForm.password)

        // 保存用户信息
        localStorage.setItem('user', JSON.stringify(response.data))

        message.success('登录成功')
        location.href = response.data.admin ? '/admin/dashboard' : '/'
      } catch (error) {
        console.error('登录失败:', error)
      } finally {
        loading.value = false
      }
    })
    .catch((error) => {
      console.error('登录验证失败:', error)
    })
}

const goToRegister = () => {
  router.push('/register')
}
</script>

<template>
  <div class="login-page">
    <div class="login-container">
      <a-card title="用户登录" class="login-card animate__animated animate__fadeIn">
        <a-form ref="formRef" :model="loginForm" :rules="loginRules" @finish="handleLogin">
          <a-form-item name="username">
            <a-input v-model:value="loginForm.username" placeholder="用户名" size="large">
              <template #prefix>
                <user-outlined />
              </template>
            </a-input>
          </a-form-item>

          <a-form-item name="password">
            <a-input-password v-model:value="loginForm.password" placeholder="密码" size="large">
              <template #prefix>
                <lock-outlined />
              </template>
            </a-input-password>
          </a-form-item>

          <a-form-item>
            <a-button type="primary" html-type="submit" :loading="loading" block size="large">
              登录
            </a-button>
          </a-form-item>

          <a-form-item>
            <a-button type="link" @click="goToRegister" block> 没有账号？立即注册 </a-button>
          </a-form-item>
        </a-form>
      </a-card>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

.login-page::before {
  content: '';
  position: absolute;
  width: 300%;
  height: 300%;
  background: radial-gradient(rgba(255, 255, 255, 0.1) 2px, transparent 0);
  background-size: 50px 50px;
  top: -100%;
  left: -100%;
  transform: rotate(45deg);
  animation: moveBackground 60s linear infinite;
}

@keyframes moveBackground {
  0% {
    transform: translateY(0) rotate(45deg);
  }
  100% {
    transform: translateY(500px) rotate(45deg);
  }
}

.login-container {
  position: relative;
  z-index: 10;
  width: 100%;
  max-width: 450px;
  padding: 20px;
}

.login-card {
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.2);
  backdrop-filter: blur(8px);
  border-radius: 10px;
  border: 1px solid rgba(255, 255, 255, 0.18);
}

@media (max-width: 480px) {
  .login-container {
    width: 90%;
  }
}
</style>
