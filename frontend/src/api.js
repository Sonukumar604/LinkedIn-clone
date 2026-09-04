import axios from 'axios'

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api/v1',
  headers: { 'Content-Type': 'application/json' },
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('linkedin_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

export const usersApi = {
  login: (credentials) => api.post('/users/auth/login', credentials),
  signup: (details) => api.post('/users/auth/signup', details),
}

export const postsApi = {
  byUser: (userId) => api.get(`/posts/core/users/${userId}/allPosts`),
  create: (content, file) => {
    const data = new FormData()
    data.append('post', new Blob([JSON.stringify({ content })], { type: 'application/json' }))
    if (file) data.append('file', file)
    return api.post('/posts/core', data, { headers: { 'Content-Type': 'multipart/form-data' } })
  },
}

export const connectionsApi = {
  firstDegree: (userId) => api.get(`/connections/core/${userId}/first-degree`),
}

export const notificationsApi = {
  list: (userId) => api.get(`/notifications/${userId}`),
}