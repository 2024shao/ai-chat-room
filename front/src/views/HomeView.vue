<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { createRoom, joinRoom, leaveRoom, disbandRoom, getRoom } from '../utils/api'

const router = useRouter()
const user = ref(null)
const message = ref('')
const messageType = ref('')

const createForm = reactive({ roomName: '', maxMembers: 10 })
const joinForm = reactive({ roomCode: '' })
const currentRoom = ref(null)
const rooms = ref([])

onMounted(() => {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    user.value = JSON.parse(userStr)
  }
})

function showMessage(msg, type = 'info') {
  message.value = msg
  messageType.value = type
  setTimeout(() => { message.value = '' }, 3000)
}

async function handleCreateRoom() {
  if (!createForm.roomName) {
    showMessage('请输入房间名称', 'error')
    return
  }
  try {
    const response = await createRoom(createForm.roomName, createForm.maxMembers)
    if (response.code === 200) {
      currentRoom.value = response.data
      showMessage(`房间创建成功！房间号：${response.data.roomCode}`, 'success')
      createForm.roomName = ''
    } else {
      showMessage(response.message, 'error')
    }
  } catch (error) {
    showMessage('网络错误', 'error')
  }
}

async function handleJoinRoom() {
  if (!joinForm.roomCode) {
    showMessage('请输入房间号', 'error')
    return
  }
  try {
    const response = await joinRoom(joinForm.roomCode)
    if (response.code === 200) {
      currentRoom.value = response.data
      showMessage('加入成功', 'success')
      joinForm.roomCode = ''
    } else {
      showMessage(response.message, 'error')
    }
  } catch (error) {
    showMessage('网络错误', 'error')
  }
}

async function handleLeaveRoom() {
  if (!currentRoom.value) return
  try {
    const response = await leaveRoom(currentRoom.value.roomCode)
    if (response.code === 200) {
      showMessage(response.message, 'success')
      currentRoom.value = null
    } else {
      showMessage(response.message, 'error')
    }
  } catch (error) {
    showMessage('网络错误', 'error')
  }
}

async function handleDisbandRoom() {
  if (!currentRoom.value) return
  try {
    const response = await disbandRoom(currentRoom.value.roomCode)
    if (response.code === 200) {
      showMessage('房间已解散', 'success')
      currentRoom.value = null
    } else {
      showMessage(response.message, 'error')
    }
  } catch (error) {
    showMessage('网络错误', 'error')
  }
}

async function handleRefreshRoom() {
  if (!currentRoom.value) return
  try {
    const response = await getRoom(currentRoom.value.roomCode)
    if (response.code === 200) {
      currentRoom.value = response.data
    }
  } catch (error) {
    showMessage('刷新失败', 'error')
  }
}

function logout() {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  router.push('/')
}
</script>

<template>
  <div class="home">
    <header class="header">
      <h1>AI Chat Room</h1>
      <div class="header-right">
        <span class="username">{{ user?.username }}</span>
        <button @click="logout" class="btn btn-outline">退出</button>
      </div>
    </header>

    <div v-if="message" :class="['toast', messageType]">{{ message }}</div>

    <div class="main-content">
      <div class="card">
        <h3>创建房间</h3>
        <div class="form-group">
          <label>房间名称</label>
          <input v-model="createForm.roomName" type="text" placeholder="输入房间名称" class="input" />
        </div>
        <div class="form-group">
          <label>最大人数</label>
          <input v-model.number="createForm.maxMembers" type="number" min="2" max="100" class="input" />
        </div>
        <button @click="handleCreateRoom" class="btn btn-primary btn-block">创建房间</button>
      </div>

      <div class="card">
        <h3>加入房间</h3>
        <div class="form-group">
          <label>房间号</label>
          <input v-model="joinForm.roomCode" type="text" placeholder="输入6位房间号" maxlength="6" class="input" />
        </div>
        <button @click="handleJoinRoom" class="btn btn-primary btn-block">加入房间</button>
      </div>

      <div v-if="currentRoom" class="card room-detail">
        <h3>当前房间</h3>
        <div class="room-info">
          <div class="room-code">{{ currentRoom.roomCode }}</div>
          <div class="room-name">{{ currentRoom.roomName }}</div>
          <div class="room-meta">
            <span>房主：{{ currentRoom.ownerName }}</span>
            <span>人数：{{ currentRoom.currentMembers }}/{{ currentRoom.maxMembers }}</span>
          </div>
          <div v-if="currentRoom.members" class="member-list">
            <h4>成员列表</h4>
            <div v-for="member in currentRoom.members" :key="member.userId" class="member-item">
              <span>{{ member.username }}</span>
              <span v-if="member.role === 1" class="owner-badge">房主</span>
            </div>
          </div>
        </div>
        <div class="room-actions">
          <button @click="handleRefreshRoom" class="btn btn-secondary">刷新</button>
          <button @click="handleLeaveRoom" class="btn btn-warning">退出</button>
          <button v-if="currentRoom.ownerId === user?.id" @click="handleDisbandRoom" class="btn btn-danger">解散</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.home {
  min-height: 100vh;
  background: #f5f7fa;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 32px;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
.header h1 {
  font-size: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}
.username { color: #555; font-weight: 600; }
.toast {
  position: fixed;
  top: 80px;
  left: 50%;
  transform: translateX(-50%);
  padding: 12px 24px;
  border-radius: 8px;
  z-index: 1000;
  animation: fadeIn 0.3s;
}
.toast.success { background: #d4edda; color: #155724; }
.toast.error { background: #f8d7da; color: #721c24; }
.toast.info { background: #d1ecf1; color: #0c5460; }
@keyframes fadeIn {
  from { opacity: 0; transform: translateX(-50%) translateY(-10px); }
  to { opacity: 1; transform: translateX(-50%) translateY(0); }
}
.main-content {
  max-width: 900px;
  margin: 32px auto;
  padding: 0 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
}
.card {
  background: white;
  padding: 28px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  flex: 1;
  min-width: 280px;
}
.card h3 {
  margin-bottom: 20px;
  color: #333;
  font-size: 18px;
}
.form-group {
  margin-bottom: 16px;
}
.form-group label {
  display: block;
  margin-bottom: 6px;
  font-weight: 600;
  color: #555;
  font-size: 14px;
}
.input {
  width: 100%;
  padding: 10px 14px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 15px;
  transition: border-color 0.3s;
  box-sizing: border-box;
}
.input:focus { outline: none; border-color: #667eea; }
.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}
.btn-block { width: 100%; }
.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}
.btn-primary:hover { opacity: 0.9; }
.btn-secondary { background: #f0f0f0; color: #333; }
.btn-secondary:hover { background: #e0e0e0; }
.btn-outline { background: none; border: 2px solid #667eea; color: #667eea; }
.btn-outline:hover { background: #667eea; color: white; }
.btn-warning { background: #f39c12; color: white; }
.btn-warning:hover { background: #e67e22; }
.btn-danger { background: #e74c3c; color: white; }
.btn-danger:hover { background: #c0392b; }
.room-detail { flex: 2; }
.room-code {
  font-size: 32px;
  font-weight: 700;
  letter-spacing: 4px;
  color: #667eea;
  text-align: center;
  margin-bottom: 8px;
}
.room-name {
  font-size: 18px;
  color: #333;
  text-align: center;
  margin-bottom: 16px;
}
.room-meta {
  display: flex;
  justify-content: center;
  gap: 24px;
  color: #777;
  font-size: 14px;
  margin-bottom: 20px;
}
.member-list { border-top: 1px solid #eee; padding-top: 16px; }
.member-list h4 { margin-bottom: 10px; color: #555; }
.member-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #f5f5f5;
}
.owner-badge {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 12px;
}
.room-actions {
  display: flex;
  gap: 12px;
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #eee;
}
</style>