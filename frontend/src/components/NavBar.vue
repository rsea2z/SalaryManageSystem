<template>
  <el-menu
    :default-active="activeIndex"
    class="app-navbar"
    mode="horizontal"
    @select="handleSelect"
    router
  >
    <el-menu-item index="/">
      <el-icon><House /></el-icon>主页
    </el-menu-item>
    <el-menu-item index="/admin" v-if="authStore.isAdmin" id="admin-button">
      <el-icon><Setting /></el-icon>后台管理
    </el-menu-item>
    <div class="flex-grow" />
    <el-menu-item index="/profile">
      <el-icon><User /></el-icon>个人信息
    </el-menu-item>
    <el-menu-item @click="handleLogout">
      <el-icon><SwitchButton /></el-icon>登出
    </el-menu-item>
  </el-menu>
</template>

<script setup>
import { computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { useAuthStore } from '@/stores/authStore';

const route = useRoute();
const authStore = useAuthStore();

const activeIndex = computed(() => route.path);

const handleSelect = (key, keyPath) => {
  // console.log(key, keyPath); // Router mode handles navigation
};

const handleLogout = () => {
  authStore.logout();
};

onMounted(() => {
  // If isAdmin status might change or needs to be fetched post-login separately
  // authStore.fetchUserDetails(); // Call this if needed, e.g., on page refresh
});
</script>

<style scoped>
.app-navbar {
  border-bottom: solid 1px var(--el-menu-border-color);
  padding: 0 20px; 
  background-color: rgba(255, 255, 255, 0.85); /* Slightly transparent navbar */
  backdrop-filter: blur(10px); /* Frosted glass effect */
  box-shadow: 0 2px 10px rgba(0,0,0,0.05); /* Subtle shadow */
}
.flex-grow {
  flex-grow: 1;
}

:deep(.el-menu-item) {
  font-weight: 500;
  transition: all 0.3s ease !important;
  border-bottom: 2px solid transparent !important; /* Prepare for active indicator */
}

:deep(.el-menu-item:hover) {
  background-color: rgba(64, 158, 255, 0.1) !important; /* Light blue hover */
  color: var(--el-color-primary) !important;
  border-bottom-color: var(--el-color-primary-light-5) !important;
}

:deep(.el-menu-item.is-active) {
  color: var(--el-color-primary) !important;
  border-bottom-color: var(--el-color-primary) !important; /* Active indicator line */
  background-color: rgba(64, 158, 255, 0.05) !important;
}

/* Icon styling */
:deep(.el-menu-item .el-icon) {
  transition: transform 0.3s ease;
}
:deep(.el-menu-item:hover .el-icon) {
  transform: scale(1.1);
}
</style>
