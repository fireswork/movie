<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { register } from '@/services/api'
import 'animate.css'

const router = useRouter()
const loading = ref(false)
const formRef = ref(null)

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  gender: 'male',
  age: null,
  phone: '',
})

const handleRegister = () => {
  formRef.value
    .validate()
    .then(async () => {
      loading.value = true

      try {
        const registerData = { ...registerForm }
        delete registerData.confirmPassword

        await register(registerData)

        message.success('注册成功，请登录')
        router.push('/login')
      } catch (error) {
        console.error('注册失败:', error)
        message.error(error.message || '注册失败，请稍后再试')
      } finally {
        loading.value = false
      }
    })
    .catch((error) => {
      console.error('注册验证失败:', error)
    })
}

const validateConfirmPassword = (_, value) => {
  if (!value) {
    return Promise.reject('请确认密码')
  }
  if (value !== registerForm.password) {
    return Promise.reject('两次输入的密码不一致')
  }
  return Promise.resolve()
}

const goToLogin = () => {
  router.push('/login')
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, message: '用户名长度不少于3个字符', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不少于6个字符', trigger: 'blur' },
  ],
  confirmPassword: [{ validator: validateConfirmPassword, trigger: 'blur' }],
  age: [{ required: true, message: '请输入年龄', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'blur' }],
  phone: [
    { required: true, pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' },
  ],
}
</script>

<template>
  <div class="register-page">
    <div class="register-container">
      <a-card title="用户注册" class="register-card animate__animated animate__fadeIn">
        <a-form
          ref="formRef"
          :model="registerForm"
          :rules="registerRules"
          @finish="handleRegister"
          layout="vertical"
        >
          <a-form-item name="username" label="用户名">
            <a-input
              v-model:value="registerForm.username"
              placeholder="请输入用户名"
              size="large"
            />
          </a-form-item>

          <a-form-item name="password" label="密码">
            <a-input-password
              v-model:value="registerForm.password"
              placeholder="请输入密码"
              size="large"
            />
          </a-form-item>

          <a-form-item name="confirmPassword" label="确认密码">
            <a-input-password
              v-model:value="registerForm.confirmPassword"
              placeholder="请再次输入密码"
              size="large"
            />
          </a-form-item>

          <a-form-item name="age" label="年龄">
            <a-input-number
              v-model:value="registerForm.age"
              placeholder="请输入年龄"
              size="large"
            />
          </a-form-item>

          <a-form-item name="gender" label="性别">
            <a-radio-group v-model:value="registerForm.gender" size="large">
              <a-radio value="male">男</a-radio>
              <a-radio value="female">女</a-radio>
            </a-radio-group>
          </a-form-item>

          <a-form-item name="phone" label="手机号码">
            <a-input v-model:value="registerForm.phone" placeholder="请输入手机号码" size="large" />
          </a-form-item>

          <a-form-item>
            <a-button type="primary" html-type="submit" :loading="loading" block size="large">
              注册
            </a-button>
          </a-form-item>

          <a-form-item>
            <a-button type="link" @click="goToLogin" block>已有账号？立即登录</a-button>
          </a-form-item>
        </a-form>
      </a-card>
    </div>
  </div>
</template>

<style scoped lang="less">
.register-page {
  min-height: 100vh;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #43cea2 0%, #185a9d 100%);
  position: relative;
  overflow: hidden;
}

.register-page::before {
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

.register-container {
  position: relative;
  z-index: 10;
  width: 100%;
  max-width: 500px;
  padding: 20px;
}

.register-card {
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.2);
  backdrop-filter: blur(8px);
  border-radius: 10px;
  border: 1px solid rgba(255, 255, 255, 0.18);
}

.ant-input-number {
  width: 100%;
}

@media (max-width: 576px) {
  .register-container {
    width: 90%;
  }
}
</style>
