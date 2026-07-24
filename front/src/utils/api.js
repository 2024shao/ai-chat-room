const BASE_URL = 'http://localhost:8080/ceshi';

export async function request(url, options = {}) {
  const token = localStorage.getItem('token');
  
  const defaultOptions = {
    headers: {
      'Content-Type': 'application/json',
      ...(token && { Authorization: `Bearer ${token}` })
    },
    ...options
  };

  const response = await fetch(`${BASE_URL}${url}`, defaultOptions);
  const data = await response.json();
  
  if (data.code === 401) {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = '/';
  }
  
  return data;
}

export async function login(username, password) {
  return request('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify({ username, password })
  });
}

export async function register(username, password, avatar = '') {
  return request('/api/auth/register', {
    method: 'POST',
    body: JSON.stringify({ username, password, avatar })
  });
}

export async function test() {
  return request('/api/auth/test');
}

export async function createRoom(roomName, maxMembers) {
  return request('/api/room/create', {
    method: 'POST',
    body: JSON.stringify({ roomName, maxMembers })
  });
}

export async function joinRoom(roomCode) {
  return request('/api/room/join', {
    method: 'POST',
    body: JSON.stringify({ roomCode })
  });
}

export async function leaveRoom(roomCode) {
  return request('/api/room/leave', {
    method: 'POST',
    body: JSON.stringify({ roomCode })
  });
}

export async function disbandRoom(roomCode) {
  return request('/api/room/disband', {
    method: 'POST',
    body: JSON.stringify({ roomCode })
  });
}

export async function getRoom(roomCode) {
  return request(`/api/room/${roomCode}`);
}