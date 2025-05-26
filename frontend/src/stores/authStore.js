import { defineStore } from 'pinia'
import axios from 'axios'
import router from '@/router' // Assuming your router is in src/router

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || null,
    user: JSON.parse(localStorage.getItem('user')) || null, // Store more user info if needed
    isAdmin: localStorage.getItem('isAdmin') === 'true' || false,
    userId: localStorage.getItem('userId') || null,
    adminOfDepartment: localStorage.getItem('adminOfDepartment') || null,
    loading: false, // Add a loading state
  }),
  getters: {
    isAuthenticated: (state) => !!state.token,
    // isAdmin: (state) => state.user?.roles?.includes('ROLE_ADMIN'), // Example if roles are used
  },
  actions: {
    async login(credentials) {
      this.loading = true;
      try {
        const response = await axios.post('/api/auth/login', credentials);
        this.token = response.data.accessToken;
        this.userId = response.data.userId; // Assuming username from response is the teacherId/employeeId
        this.isAdmin = response.data.admin; // Use isAdmin from response
        this.adminOfDepartment = response.data.departmentNumber; // Use departmentNumber from response

        localStorage.setItem('token', this.token);
        localStorage.setItem('userId', this.userId);
        localStorage.setItem('isAdmin', this.isAdmin.toString());
        if (this.adminOfDepartment) {
          localStorage.setItem('adminOfDepartment', this.adminOfDepartment);
        }
        
        axios.defaults.headers.common['Authorization'] = `Bearer ${this.token}`;
        // No need to call fetchUserDetails here if login response has all info
        // If not, then call it: await this.fetchUserDetails(); 
        return true;
      } catch (error) {
        console.error('Login API call failed in authStore:', error.response?.data || error.message);
        // 发生错误时清除任何部分状态
        this.token = null;
        this.user = null;
        this.isAdmin = false;
        this.userId = null;
        this.adminOfDepartment = null;
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        localStorage.removeItem('isAdmin');
        localStorage.removeItem('userId');
        localStorage.removeItem('adminOfDepartment');
        delete axios.defaults.headers.common['Authorization'];
        throw error; // 重新抛出错误，由组件的 catch 块处理
      } finally {
        this.loading = false;
      }
    },
    logout() {
      this.token = null;
      this.user = null;
      this.isAdmin = false;
      this.userId = null;
      this.adminOfDepartment = null;

      localStorage.removeItem('token');
      localStorage.removeItem('user');
      localStorage.removeItem('isAdmin');
      localStorage.removeItem('userId');
      localStorage.removeItem('adminOfDepartment');
      delete axios.defaults.headers.common['Authorization'];
      router.push('/login');
    },
    // Action to fetch and set admin status if not available at login
    // Or to refresh user details
    async fetchUserDetails() {
      if (!this.userId) return; // Should have userId from login
      this.loading = true;
      try {
        // This endpoint gives more detailed teacher info if needed after login
        // For isAdmin and adminOfDepartment, it's better to get them from a dedicated admin check endpoint or login response
        const infoResponse = await axios.get(`/api/teacher/information/${this.userId}`);
        if (infoResponse.data) {
            // this.isAdmin = infoResponse.data.isAdmin; // Already set from login
            // localStorage.setItem('isAdmin', this.isAdmin.toString());
        }

        // Fetch department admin status separately if not in login/info response comprehensively
        const adminDeptResponse = await axios.get(`/api/teacher/admin_of_department/${this.userId}`);
        if (adminDeptResponse.data && adminDeptResponse.data.admin) {
            this.isAdmin = true; // Re-confirm admin status
            this.adminOfDepartment = adminDeptResponse.data.departmentNumber;
            localStorage.setItem('isAdmin', 'true');
            if (this.adminOfDepartment) { // Corrected: added parentheses
                localStorage.setItem('adminOfDepartment', this.adminOfDepartment);
            }
        } else {
            // If not admin of any department, ensure adminOfDepartment is cleared
            // this.adminOfDepartment = null; 
            // localStorage.removeItem('adminOfDepartment');
        }

      } catch (error) {
        console.error('Error fetching user details:', error);
        // ElMessage.error('获取用户详情失败'); 
        // Avoid error message if this is a background update
      } finally {
        this.loading = false;
      }
    }
  },
})
