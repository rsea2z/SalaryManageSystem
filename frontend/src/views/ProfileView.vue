<template>
  <div class="profile-page">
    <el-container class="profile-container ui container">
      <el-main>
        <el-card class="profile-card ui segment">
          <template #header>
            <div class="card-header">
              <el-icon><UserFilled /></el-icon>
              <span style="margin-left: 8px;">个人信息</span>
            </div>
          </template>
          <el-descriptions v-if="!loading && teacherInfo" :column="1" border>
            <el-descriptions-item label="姓名">{{ teacherInfo.teacherName }}</el-descriptions-item>
            <el-descriptions-item label="年龄">{{ teacherInfo.teacherAge }}</el-descriptions-item>
            <el-descriptions-item label="性别">{{ teacherInfo.teacherGender }}</el-descriptions-item>
            <el-descriptions-item label="所属院系">{{ teacherInfo.departmentName }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ teacherInfo.phone }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ teacherInfo.email }}</el-descriptions-item>
            <el-descriptions-item label="职称">{{ teacherInfo.post }}</el-descriptions-item>
            <el-descriptions-item label="职务">{{ teacherInfo.job }}</el-descriptions-item>
          </el-descriptions>
          <el-skeleton :rows="8" animated v-if="loading" />
          <el-empty description="无法加载教师信息" v-if="!loading && !teacherInfo" />
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { useTeacherStore } from '@/stores/teacherStore';
import { storeToRefs } from 'pinia';

const teacherStore = useTeacherStore();
const { currentTeacherProfile, loading: storeLoading } = storeToRefs(teacherStore);

// Use a computed property to map store's currentTeacherProfile to teacherInfo
const teacherInfo = computed(() => currentTeacherProfile.value);
// Use a computed property for loading state based on store's loading
const loading = computed(() => storeLoading.value);

// const isAdmin = ref(false); // This state is now managed in NavBar.vue

onMounted(async () => {
  const teacherId = localStorage.getItem('userId'); 

  if (!teacherId) {
    ElMessage.error('无法获取用户ID，请重新登录。');
    // loading.value = false; // Handled by store's loading state
    // router.push('/login'); // Optionally redirect
    return;
  }

  try {
    await teacherStore.fetchTeacherProfile(teacherId);
    // teacherInfo.value is now reactive via computed property from store
    if (!teacherInfo.value) { // Check after fetch
      ElMessage.error('未找到教师信息。');
    }
  } catch (error) {
    // Error messages are handled within the store action,
    // but you can add component-specific error handling if needed.
    // console.error('Error fetching teacher information in component:', error);
  }
  // loading state is managed by the store
});
</script>

<style scoped>
.profile-page {
  display: flex;
  flex-direction: column;
  min-height: calc(100vh - 0px); /* Adjust if you have a fixed navbar height */
  padding: 20px; 
  box-sizing: border-box;
  /* background: linear-gradient(to bottom right, #fdfbfb, #ebedee); */ /* Soft page gradient */
}
.profile-container {
  flex-grow: 1;
  width: 100%;
  max-width: 800px; 
  margin: 0 auto; 
}
.profile-card {
  margin-top: 0; 
  /* box-shadow, border-radius, background handled by global style in App.vue */
  /* Add specific border or subtle gradient if needed */
  /* border: 1px solid transparent; */
  /* background-image: linear-gradient(rgba(255,255,255,0.95), rgba(255,255,255,0.95)), linear-gradient(135deg, #a1c4fd 0%, #c2e9fb 100%); */
  /* background-origin: border-box; */
  /* background-clip: content-box, border-box; */
}
.card-header {
  display: flex;
  align-items: center;
  font-size: 1.5em; /* Larger */
  font-weight: 600; 
  color: #34495e; /* Professional dark blue */
  padding-bottom: 10px; /* Space below header */
  border-bottom: 1px solid rgba(0,0,0,0.05); /* Subtle separator */
  margin-bottom: 15px; /* Space above descriptions */
}
.card-header .el-icon {
  color: #3498db; /* Icon color */
}

.el-descriptions-item__label {
  width: 120px; 
  font-weight: 500;
  color: #555; /* Darker label color */
}
.el-descriptions-item__content {
  color: #2c3e50; /* Content color */
}

/* Add a subtle entrance animation for the card */
.profile-card {
  animation: fadeInScale 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94) forwards;
}

@keyframes fadeInScale {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}
</style>
