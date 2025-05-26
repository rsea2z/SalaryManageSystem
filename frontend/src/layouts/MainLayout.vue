<template>
  <el-container class="main-layout-container">
    <el-header height="auto" style="padding: 0;">
      <NavBar />
    </el-header>
    <el-main class="main-content">
      <router-view />
    </el-main>
    <el-footer height="auto" style="padding: 0;">
      <Footer />
    </el-footer>
  </el-container>
</template>

<script setup>
import NavBar from '@/components/NavBar.vue';
import Footer from '@/components/Footer.vue';
</script>

<style scoped>
.main-layout-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
.main-content {
  flex-grow: 1; /* Ensures main content takes up available space */
  padding: 20px; /* Add some padding around the content */
  background-color: #f4f4f4; /* Match body background */
}
.el-header, .el-footer {
  width: 100%;
}
</style>
