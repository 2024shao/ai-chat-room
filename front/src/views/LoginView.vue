<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { login, register } from '../utils/api'

const router = useRouter()
const isLoginMode = ref(true)
const form = reactive({
  username: '',
  password: '',
  avatar: ''
})
const message = ref('')
const messageType = ref('')

function showMessage(msg, type = 'info') {
  message.value = msg
  messageType.value = type
  setTimeout(() => { message.value = '' }, 3000)
}

async function handleSubmit() {
  if (!form.username || !form.password) {
    showMessage('请填写用户名和密码', 'error')
    return
  }
  try {
    let response
    if (isLoginMode.value) {
      response = await login(form.username, form.password)
    } else {
      response = await register(form.username, form.password, form.avatar)
    }
    if (response.code === 200) {
      localStorage.setItem('token', response.data.token)
      localStorage.setItem('user', JSON.stringify(response.data))
      showMessage(response.message, 'success')
      setTimeout(() => router.push('/home'), 500)
    } else {
      showMessage(response.message, 'error')
    }
  } catch (error) {
    showMessage('网络错误', 'error')
  }
}

function switchMode() {
  isLoginMode.value = !isLoginMode.value
  form.username = ''
  form.password = ''
  form.avatar = ''
  message.value = ''
}
</script>

<template>
  <div class="auth-container">
    <div class="auth-box">
      <h2>{{ isLoginMode ? '用户登录' : '用户注册' }}</h2>
      <div v-if="message" :class="['message', messageType]">{{ message }}</div>
      <form @submit.prevent="handleSubmit" class="form">
        <div class="form-group">
          <label>用户名</label>
          <input v-model="form.username" type="text" placeholder="请输入用户名" class="input" />
        </div>
        <div class="form-group">
          <label>密码</label>
          <input v-model="form.password" type="password" placeholder="请输入密码" class="input" />
        </div>
        <div v-if="!isLoginMode" class="form-group">
          <label>头像URL（可选）</label>
          <input v-model="form.avatar" type="text" placeholder="请输入头像URL" class="input" />
        </div>
        <button type="submit" class="btn btn-primary">
          {{ isLoginMode ? '登录' : '注册' }}
        </button>
      </form>
      <button @click="switchMode" class="btn btn-link">
        {{ isLoginMode ? '没有账号？去注册' : '已有账号？去登录' }}
      </button>
    </div>
  </div>
</template>

<style scoped>
.auth-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}
.auth-box {
  background: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  width: 100%;
  max-width: 400px;
}
h2 { text-align: center; margin-bottom: 30px; color: #333; }
.form { display: flex; flex-direction: column; gap: 20px; }
.form-group { display: flex; flex-direction: column; gap: 8px; }
.form-group label { font-weight: 600; color: #555; }
.input {
  padding: 12px 16px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 16px;
  transition: border-color 0.3s;
}
.input:focus { outline: none; border-color: #667eea; }
.btn {
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}
.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}
.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 20px rgba(102, 126, 234, 0.4);
}
.btn-link {
  background: none;
  color: #667eea;
  margin-top: 15px;
  width: 100%;
  text-align: center;
}
.btn-link:hover { color: #764ba2; }
.message {
  padding: 12px;
  border-radius: 8px;
  margin-bottom: 20px;
  text-align: center;
}
.message.success { background: #d4edda; color: #155724; }
.message.error { background: #f8d7da; color: #721c24; }
.message.info { background: #d1ecf1; color: #0c5460; }
</style>