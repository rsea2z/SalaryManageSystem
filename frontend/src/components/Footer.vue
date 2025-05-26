<template>
  <el-footer class="app-footer">
    <el-row justify="center" align="middle">
      <el-col>
        <el-text size="small">西安石油大学</el-text>
        <el-divider direction="vertical" />
        <el-text size="small">计算机科学与技术学院</el-text>
        <el-divider direction="vertical" />
        <el-text size="small">计算机科学与技术</el-text>
        <el-divider direction="vertical" />
        <el-text size="small">计2201 刘俊杰</el-text>
      </el-col>
    </el-row>
  </el-footer>
</template>

<script setup>
</script>

<style scoped>
.app-footer {
  height: 50px; /* Slightly taller */
  line-height: 50px;
  text-align: center;
  /* background-color: #f4f4f4; */ /* Match original background */
  background-color: rgba(240, 242, 245, 0.8); /* Subtle transparency, matches light theme */
  backdrop-filter: blur(5px);
  border-top: 1px solid rgba(0, 0, 0, 0.06); /* Softer top border */
  width: 100%;
  /* position: fixed; */ /* Consider removing if it causes layout issues with long content */
  /* bottom: 0; */
  /* left: 0; */
  /* If not fixed, it will naturally be at the bottom of the content flow */
  /* For a fixed footer, ensure main content has enough bottom padding */
  color: #888; /* Lighter text color */
  font-size: 0.85em;
}
.el-text {
  color: #777; /* Adjust text color as needed */
  transition: color 0.3s ease;
}
.el-text:hover {
  color: #333;
}
.el-divider--vertical {
  border-left: 1px solid #ccc; /* Lighter divider */
}
</style>
