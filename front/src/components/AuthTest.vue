<script setup>import { ref, reactive } from 'vue';
import { login, register, test } from '../utils/api';
const isLoginMode = ref(true);
const form = reactive({
 username: '',
 password: '',
 avatar: ''
});
const message = ref('');
const messageType = ref('');
const isLoggedIn = ref(false);
const user = ref(null);
function showMessage(msg, type = 'info') {
 message.value = msg;
 messageType.value = type;
 setTimeout(() => {
 message.value = '';
 }, 3000);
}
async function handleSubmit() {
 if (!form.username || !form.password) {
 showMessage('请填写用户名和密码', 'error');
 return;
 }
 try {
 let response;
 if (isLoginMode.value) {
 response = await login(form.username, form.password);
 }
 else {
 response = await register(form.username, form.password, form.avatar);
 }
 if (response.code === 200) {
 localStorage.setItem('token', response.data.token);
 localStorage.setItem('user', JSON.stringify(response.data));
 user.value = response.data;
 isLoggedIn.value = true;
 showMessage(response.message, 'success');
 }
 else {
 showMessage(response.message, 'error');
 }
 }
 catch (error) {
 showMessage('网络错误', 'error');
 }
}
async function handleTest() {
 try {
 const response = await test();
 if (response.code === 200) {
 showMessage('测试接口调用成功: ' + response.data, 'success');
 }
 else {
 showMessage(response.message, 'error');
 }
 }
 catch (error) {
 showMessage('网络错误', 'error');
 }
}
function logout() {
 localStorage.removeItem('token');
 localStorage.removeItem('user');
 isLoggedIn.value = false;
 user.value = null;
 showMessage('已退出登录', 'info');
}
function switchMode() {
 isLoginMode.value = !isLoginMode.value;
 form.username = '';
 form.password = '';
 form.avatar = '';
 message.value = '';
}
</script>

<template>
  <div class="auth-container">
    <div v-if="!isLoggedIn" class="auth-box">
      <h2>{{ isLoginMode ? '用户登录' : '用户注册' }}</h2>
      
      <div v-if="message" :class="['message', messageType]">
        {{ message }}
      </div>
      
      <form @submit.prevent="handleSubmit" class="form">
        <div class="form-group">
          <label>用户名</label>
          <input 
            v-model="form.username" 
            type="text" 
            placeholder="请输入用户名"
            class="input"
          />
        </div>
        
        <div class="form-group">
          <label>密码</label>
          <input 
            v-model="form.password" 
            type="password" 
            placeholder="请输入密码"
            class="input"
          />
        </div>
        
        <div v-if="!isLoginMode" class="form-group">
          <label>头像URL（可选）</label>
          <input 
            v-model="form.avatar" 
            type="text" 
            placeholder="请输入头像URL"
            class="input"
          />
        </div>
        
        <button type="submit" class="btn btn-primary">
          {{ isLoginMode ? '登录' : '注册' }}
        </button>
      </form>
      
      <button @click="switchMode" class="btn btn-secondary">
        {{ isLoginMode ? '切换到注册' : '切换到登录' }}
      </button>
    </div>
    
    <div v-else class="dashboard">
      <h2>登录成功</h2>
      <div class="user-info">
        <img v-if="user.avatar" :src="user.avatar" alt="头像" class="avatar" />
        <div class="user-details">
          <p><strong>用户ID:</strong> {{ user.id }}</p>
          <p><strong>用户名:</strong> {{ user.username }}</p>
          <p><strong>角色:</strong> {{ user.role === 0 ? '普通用户' : '管理员' }}</p>
        </div>
      </div>
      
      <div class="actions">
        <button @click="handleTest" class="btn btn-primary">
          测试接口
        </button>
        <button @click="logout" class="btn btn-danger">
          退出登录
        </button>
      </div>
      
      <div v-if="message" :class="['message', messageType]">
        {{ message }}
      </div>
    </div>
  </div>
</template>

<style scoped>
.auth-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.auth-box {
  background: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-weight: 600;
  color: #555;
}

.input {
  padding: 12px 16px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 16px;
  transition: border-color 0.3s;
}

.input:focus {
  outline: none;
  border-color: #667eea;
}

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

.btn-secondary {
  background: #f0f0f0;
  color: #333;
  margin-top: 10px;
}

.btn-secondary:hover {
  background: #e0e0e0;
}

.btn-danger {
  background: #e74c3c;
  color: white;
}

.btn-danger:hover {
  background: #c0392b;
}

.message {
  padding: 12px;
  border-radius: 8px;
  margin-bottom: 20px;
  text-align: center;
}

.message.success {
  background: #d4edda;
  color: #155724;
}

.message.error {
  background: #f8d7da;
  color: #721c24;
}

.message.info {
  background: #d1ecf1;
  color: #0c5460;
}

.dashboard {
  background: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 500px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
}

.user-details p {
  margin: 8px 0;
  color: #555;
}

.actions {
  display: flex;
  gap: 15px;
}

.actions .btn {
  flex: 1;
}</style>