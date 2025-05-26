<template>
  <div class="login-container" :style="containerStyle">
    <div class="background-dim-overlay" :class="{ 'active': isPasswordFocused }"></div>
    <el-row justify="center" align="middle" style="height: 100vh; position: relative; z-index: 2;">
      <el-col :xs="22" :sm="16" :md="12" :lg="8" :xl="6">
        <div class="login-form-wrapper">
          <div style="text-align: center; margin-bottom: 20px;">
            <el-icon :size="50" color="#409EFC"><Platform /></el-icon>
             <h2 class="login-title">工资管理系统登录</h2>
          </div>

          <el-form
            ref="loginFormRef"
            :model="loginForm"
            :rules="loginRules"
            label-position="top"
            class="login-form"
            @submit.prevent="handleLogin"
          >
            <el-form-item prop="userid">
              <el-input
                v-model="loginForm.userid"
                placeholder="用户名"
                prefix-icon="User"
                clearable
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="密码"
                prefix-icon="Lock"
                show-password
                clearable
                @focus="isPasswordFocused = true"
                @blur="isPasswordFocused = false"
              />
            </el-form-item>
            <el-form-item>
              <el-button
                type="primary"
                native-type="submit"
                class="login-button"
                :class="{
                  'status-success': loginStatus === 'success',
                  'status-error': loginStatus === 'error'
                }"
                :loading="loading && loginStatus === 'idle'"
                :disabled="loading || loginStatus === 'success' || loginStatus === 'error'"
              >
                <span class="button-text-content">{{ buttonText }}</span>
              </el-button>
            </el-form-item>
          </el-form>
          <div class="login-footer-message">
            <el-text type="info">任何问题请联系管理员</el-text>
          </div>
        </div>
      </el-col>
    </el-row>
    <Footer style="position: relative; z-index: 2;" />
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import Footer from '@/components/Footer.vue';
import { useAuthStore } from '@/stores/authStore';
import bannerJpg from '@/assets/banner.jpg';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

const loginFormRef = ref(null);
const loginForm = reactive({
  userid: '',
  password: '',
});
const loading = ref(false);
const isPasswordFocused = ref(false);
const loginStatus = ref('idle'); // 'idle', 'success', 'error'

const containerStyle = computed(() => ({
  backgroundImage: `url(${bannerJpg})`,
}));

const loginRules = {
  userid: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
};

const buttonText = computed(() => {
  if (loginStatus.value === 'success') return '登录成功';
  if (loginStatus.value === 'error') return '密码错误';
  return '登录';
});

const handleLogin = async () => {
  if (!loginFormRef.value) return;
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      loginStatus.value = 'idle'; // Reset status before new attempt
      try {
        const loginSuccess = await authStore.login({
          userid: loginForm.userid,
          password: loginForm.password,
        });
        loading.value = false;

        if (loginSuccess) {
          loginStatus.value = 'success';
          ElMessage.success('登录成功');
          setTimeout(() => {
            const redirectPath = route.query.redirect || '/';
            router.push(redirectPath);
            // loginStatus.value = 'idle'; // Reset on navigation or component destroy
          }, 1500); // Animation (0.5s for success) + display time
        } else {
          // This case might not be hit if authStore.login throws an error for failed login
          loginStatus.value = 'error';
          ElMessage.error('登录失败，请检查凭据'); // This message might be redundant if error object has one
           setTimeout(() => {
            loginStatus.value = 'idle';
          }, 1500); // Duration of error animation + display
        }
      } catch (error) {
        loading.value = false;
        loginStatus.value = 'error';
        const errorMessage = error.response?.data?.message || error.response?.data?.error || '登录失败，请稍后再试';
        ElMessage.error(errorMessage);
        setTimeout(() => {
          loginStatus.value = 'idle';
        }, 1500); // Duration of error animation (1.5s)
      }
    } else {
      ElMessage.error('请完整填写登录信息');
      return false;
    }
  });
};
</script>

<style scoped>
.login-container {
  min-height: 100vh; 
  display: flex; 
  flex-direction: column;
  align-items: center; 
  justify-content: center; 
  overflow: hidden;
  background-size: cover;
  background-position: center;
  position: relative; /* For overlay positioning */
}

.background-dim-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0);
  transition: background-color 0.3s ease-in-out;
  z-index: 1; /* Behind form content, on top of container's background image */
}

.background-dim-overlay.active {
  background-color: rgba(0, 0, 0, 0.4); /* Semi-transparent dark overlay */
}

.login-form-wrapper {
  background-color: rgba(255, 255, 255, 0.9); /* More pronounced transparency */
  backdrop-filter: blur(15px) saturate(150%); /* Stronger glass effect */
  padding: 40px 50px; /* Increased padding */
  border-radius: 16px; /* Softer, more modern radius */
  box-shadow: 0 12px 35px rgba(0, 0, 0, 0.15); /* More pronounced shadow */
  width: 100%; 
  max-width: 450px; 
  margin: auto; 
  animation: slideInUp 0.7s cubic-bezier(0.25, 0.46, 0.45, 0.94) forwards, fadeIn 0.7s ease-out forwards; 
  border: 1px solid rgba(255, 255, 255, 0.2);
  position: relative; /* Ensure it's above the dim overlay if z-index issues arise with el-row */
  z-index: 2; /* Explicitly above the .background-dim-overlay */
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Added fadeIn for smoother appearance */
@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes errorSwipeEffect {
  0% {
    width: 0%;
    left: 0;
  }
  33% { /* Swipe in complete */
    width: 100%;
    left: 0;
  }
  66% { /* Hold */
    width: 100%;
    left: 0;
  }
  100% { /* Swipe out to left complete */
    width: 0%;
    left: 0;
  }
}

.login-title {
  font-size: 2em; /* Larger title */
  font-weight: 700; /* Bolder */
  color: #2c3e50; /* Darker, more professional color */
  margin-bottom: 30px; /* More space */
  letter-spacing: 1px; /* Added letter spacing */
}

.el-form-item {
  margin-bottom: 25px; /* Consistent spacing */
}

/* Input styling is now largely handled by App.vue global styles */
/* .el-input :deep(.el-input__wrapper) { ... } */
/* .el-input :deep(.el-input__wrapper.is-focus) { ... } */

/* Button styling is now largely handled by App.vue global styles */
.login-button {
  width: 100%;
  padding: 14px 20px;
  font-size: 1.15em;
  font-weight: 600;
  position: relative; /* For pseudo-element and text wrapper */
  overflow: hidden; /* To clip the pseudo-element animation */
  /* Removed base transition for background-color, color here, rely on specific states or default ElButton styles */
}

.login-button .button-text-content {
  position: relative;
  z-index: 2; /* Text on top of the pseudo-element */
  transition: color 0.2s ease-in-out; 
}

.login-button::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0; 
  width: 0;   
  height: 100%;
  /* Removed general transition: width 0.5s ease-in-out; Will be specific to success/error states */
  z-index: 1; 
}

.login-button.status-success::before {
  background-color: #67c23a; 
  width: 100%;
  transition: width 0.5s ease-in-out; /* Success swipe in */
}

.login-button.status-error::before {
  background-color: #f56c6c; 
  /* width is handled by animation */
  animation: errorSwipeEffect 1.5s ease-in-out forwards;
}

/* Change text color when background is filled by ::before */
.login-button.status-success .button-text-content,
.login-button.status-error .button-text-content {
  color: white;
}

.login-footer-message {
  margin-top: 25px;
  text-align: center;
}
.login-footer-message .el-text {
  font-size: 0.9em;
}

/* Ensure Footer is at the bottom */
.login-container > .el-row {
    flex-grow: 1; 
    width: 100%; 
}
</style>
