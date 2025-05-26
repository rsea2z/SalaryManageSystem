import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia' // Import Pinia
import axios from 'axios'
import './style.css' 
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const app = createApp(App)

const pinia = createPinia() // Create Pinia instance
app.use(pinia) // Use Pinia

app.use(router)
app.use(ElementPlus)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

axios.defaults.baseURL = 'http://localhost:8080'; // Base URL for Spring Boot backend

// Axios Request Interceptor to add JWT token
axios.interceptors.request.use(config => {
  const token = localStorage.getItem('token'); // Or get from authStore
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  // Ensure requests to /api/... are prefixed if not already
  if (config.url && !config.url.startsWith('/api/') && !config.url.startsWith('http')) {
    config.url = `/api${config.url.startsWith('/') ? '' : '/'}${config.url}`;
  } else if (config.url && config.url.startsWith('/api/api/')) { // Fix double /api/
    config.url = config.url.replace('/api/api/', '/api/');
  }
  return config;
}, error => {
  return Promise.reject(error);
});

// Axios Response Interceptor for handling 401 errors
axios.interceptors.response.use(response => {
  return response;
}, error => {
  if (error.response && error.response.status === 401) {
    // Assuming you have an authStore for logout
    // import { useAuthStore } from './stores/authStore'; // Adjust path
    // const authStore = useAuthStore();
    // authStore.logout(); // Clear auth state
    localStorage.removeItem('token');
    localStorage.removeItem('userId');
    localStorage.removeItem('isAdmin');
    localStorage.removeItem('adminOfDepartment');
    router.push('/login').then(() => {
        // Optionally show a message
        // ElMessage.error('会话已过期，请重新登录。');
    });
  }
  return Promise.reject(error);
});


app.mount('#app')
