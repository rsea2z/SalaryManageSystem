<script setup>
import { onMounted, onUnmounted, ref } from 'vue';

const appContainerRef = ref(null);

const handleMouseMove = (event) => {
  if (appContainerRef.value) {
    const { clientX, clientY } = event;
    const { offsetWidth, offsetHeight } = appContainerRef.value;
    const xPercent = (clientX / offsetWidth) * 100;
    const yPercent = (clientY / offsetHeight) * 100;
    appContainerRef.value.style.setProperty('--mouse-x', `${xPercent}%`);
    appContainerRef.value.style.setProperty('--mouse-y', `${yPercent}%`);
  }
};

onMounted(() => {
  if (appContainerRef.value) { // Check if appContainerRef.value is not null
    appContainerRef.value.addEventListener('mousemove', handleMouseMove);
  }
});

onUnmounted(() => {
  if (appContainerRef.value) { // Check if appContainerRef.value is not null
    appContainerRef.value.removeEventListener('mousemove', handleMouseMove);
  }
});
</script>

<template>
  <div id="app-container" ref="appContainerRef">
    <router-view v-slot="{ Component }">
      <transition name="fade" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
  </div>
</template>

<style>
/* Global styles, including body and html for full-page background */
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
  width: 100%;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB',
               'Microsoft YaHei', '微软雅黑', Arial, sans-serif; /* Modern font stack */
}

#app-container {
  min-height: 100vh; /* Ensure it covers full viewport height */
  /* background: linear-gradient(135deg, #ece9e6 0%, #ffffff 100%); */ /* Subtle gradient */
  /* New background with mouse tracking */
  background-image: radial-gradient(
    circle farthest-corner at var(--mouse-x) var(--mouse-y),
    rgba(230, 230, 250, 0.3) 0%, /* Lavender blush subtle */
    rgba(240, 248, 255, 0.2) 25%, /* Alice blue subtle */
    rgba(245, 245, 245, 0.1) 50%, /* White smoke subtle */
    rgba(255, 255, 255, 0) 100%
  ), linear-gradient(135deg, #e6e9f0 0%, #eef1f5 100%); /* Base soft gradient */
  background-size: cover;
  background-position: center;
  transition: background-position 0.1s ease-out; /* Smooth transition for mouse follow */
  --mouse-x: 50%; /* Default center */
  --mouse-y: 50%; /* Default center */
}

/* Fade transition for router views */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* Global Element Plus component enhancements (optional) */
.el-card {
  transition: all 0.3s ease-in-out;
  border-radius: 12px !important; /* Softer corners */
  background-color: rgba(255, 255, 255, 0.85) !important; /* Slightly transparent card */
  backdrop-filter: blur(10px); /* Frosted glass effect */
  border: 1px solid rgba(255, 255, 255, 0.18);
}
.el-card:hover {
  transform: translateY(-5px) scale(1.01); /* More pronounced hover */
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12) !important;
}

.el-button {
  transition: all 0.25s ease-in-out !important;
  border-radius: 6px !important; /* Consistent border radius */
}
.el-button:hover {
  transform: translateY(-2px) scale(1.03); /* Enhanced hover */
  filter: brightness(1.1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}
.el-button:active {
  transform: translateY(0px) scale(1); /* Reset transform on active */
  filter: brightness(0.9);
}

/* Specific button types enhancements */
.el-button--primary {
  background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%) !important;
  border: none !important;
}
.el-button--primary:hover {
  background: linear-gradient(135deg, #53a8ff 0%, #79bbff 100%) !important;
}

.el-button--success {
  background: linear-gradient(135deg, #67C23A 0%, #85ce61 100%) !important;
  border: none !important;
}
.el-button--success:hover {
  background: linear-gradient(135deg, #7ec658 0%, #95d47a 100%) !important;
}

.el-button--danger {
  background: linear-gradient(135deg, #F56C6C 0%, #f78989 100%) !important;
  border: none !important;
}
.el-button--danger:hover {
  background: linear-gradient(135deg, #f67e7e 0%, #f89a9a 100%) !important;
}

/* Input field enhancements */
.el-input__wrapper {
  border-radius: 6px !important;
  transition: all 0.3s ease !important;
  background-color: rgba(255, 255, 255, 0.8) !important; /* Slightly transparent inputs */
}
.el-input__wrapper.is-focus {
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.3) !important; /* Softer focus glow */
  background-color: rgba(255, 255, 255, 0.95) !important;
}
.el-select .el-input.is-focus .el-input__wrapper {
   box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.3) !important;
}

/* Table enhancements */
.el-table {
  border-radius: 8px !important;
  overflow: hidden; /* Ensures border-radius applies to header */
  background-color: rgba(255, 255, 255, 0.8) !important; /* Slightly transparent table */
}
.el-table th {
  background-color: rgba(245, 247, 250, 0.8) !important; /* Lighter, slightly transparent header */
  color: #303133 !important;
  font-weight: 600 !important;
}
.el-table tr:hover > td {
  background-color: rgba(236, 245, 255, 0.7) !important; /* Element blue hover, slightly transparent */
}

/* Scrollbar styling for a more modern look */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}
::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.05);
  border-radius: 10px;
}
::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #b8c6db 0%, #f5f7fa 100%);
  border-radius: 10px;
  transition: background 0.3s ease;
}
::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(135deg, #a0aec0 0%, #e2e8f0 100%);
}
</style>
