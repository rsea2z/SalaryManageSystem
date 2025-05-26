<template>
  <div class="home-page">
    <el-container class="home-container ui container">
      <el-main>
        <el-card class="ui segment welcome-card">
          <div style="text-align: center;">
            <el-icon :size="40" color="#00b5ad"><UserFilled /></el-icon>
            <h1 class="ui center aligned header teal" style="margin-top: 10px;">
              <template v-if="userName">
                <span>{{ userName }}，</span>您好！
              </template>
              <el-skeleton :rows="1" animated v-else style="display: inline-block; width: 150px;" />
            </h1>
          </div>
        </el-card>

        <el-card class="ui segment salary-details-card">
          <template #header>
            <h2 class="ui center aligned header teal">工资详情</h2>
          </template>
          
          <el-container>
            <el-aside width="180px" class="home-month-sidebar">
              <el-menu
                :default-active="selectedMonth.toString()"
                class="home-months-menu"
                @select="handleMonthChange" 
              >
                <el-menu-item index="0">
                  <span>所有月份</span>
                </el-menu-item>
                <template v-for="year in Object.keys(groupedYearMonths).sort((a,b) => parseInt(b) - parseInt(a))" :key="year">
                  <el-sub-menu :index="year">
                    <template #title><span>{{ year }}年</span></template>
                    <el-menu-item v-for="monthItem in groupedYearMonths[year]" :key="monthItem.fullValue" :index="monthItem.fullValue.toString()">
                      <span>{{ monthItem.label }}</span>
                    </el-menu-item>
                  </el-sub-menu>
                </template>
              </el-menu>
            </el-aside>
            <el-main>
              <el-row :gutter="20">
                <el-col :xs="24" :sm="10" :md="10">
                  <div v-if="salaryDetails">
                    <el-descriptions :column="1" border style="width: 100%;">
                      <el-descriptions-item label="基本工资">{{ salaryDetails.salaryBasic }}元</el-descriptions-item>
                      <el-descriptions-item label="项目工资">{{ salaryDetails.salaryProject }}元</el-descriptions-item>
                      <el-descriptions-item label="总工资">{{ salaryDetails.salaryPayable }}元</el-descriptions-item>
                      <!-- <el-descriptions-item label="已发工资">{{ salaryDetails.salaryPaied }}元</el-descriptions-item> -->
                    </el-descriptions>
                  </div>
                  <el-skeleton :rows="4" animated v-if="loadingSalaryDetails && !salaryDetails" style="width: 100%;" />
                </el-col>
                <el-col :xs="24" :sm="10" :md="10">
                  <div id="salary-chart-container" style="height: 300px; min-height: 300px;">
                    <canvas id="salary-chart"></canvas>
                  </div>
                </el-col>
              </el-row>
            </el-main>
          </el-container>
        </el-card>

        <el-card class="ui segment project-details-card">
           <template #header>
            <h2 class="ui center aligned header teal">项目详情</h2>
          </template>
          <el-table :data="projectListNodone" stripe style="width: 100%" v-loading="loadingProjects">
            <el-table-column prop="projectNumber" label="项目编号" />
            <el-table-column prop="projectName" label="项目名称" />
            <el-table-column prop="departmentName" label="主导院系" />
            <el-table-column prop="leaderName" label="项目负责人" />
            <el-table-column prop="projectSalary" label="金额">
              <template #default="scope">{{ scope.row.projectSalary }}元</template>
            </el-table-column>
            <el-table-column label="操作">
              <template #default="scope">
                <el-button type="primary" size="small" @click="showProjectDetailModal(scope.row)">查看详情</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-divider content-position="left" v-if="projectListDone.length > 0 && projectListNodone.length > 0">已完成项目</el-divider>
          <el-table :data="projectListDone" stripe style="width: 100%; background-color: #d4edda;" row-class-name="done-project-row" v-loading="loadingProjects">
            <el-table-column prop="projectNumber" label="项目编号" />
            <el-table-column prop="projectName" label="项目名称" />
            <el-table-column prop="departmentName" label="主导院系" />
            <el-table-column prop="leaderName" label="项目负责人" />
            <el-table-column prop="projectSalary" label="金额">
              <template #default="scope">{{ scope.row.projectSalary }}元</template>
            </el-table-column>
            <el-table-column label="操作">
              <template #default="scope">
                <el-button type="primary" size="small" @click="showProjectDetailModal(scope.row)">查看详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-main>
    </el-container>

    <el-dialog v-model="projectDetailModalVisible" title="项目详情" width="60%">
      <div v-if="currentProjectDetail">
        <h3 style="text-align: center;">
          {{ currentProjectDetail.projectName }} #{{ currentProjectDetail.projectNumber }}
        </h3>
        <p style="text-align: center;">主导院系：{{ currentProjectDetail.departmentName }}</p>
        
        <h4>项目成员</h4>
        <el-table :data="currentProjectDetail.collaborators" border :row-class-name="collaboratorRowClassName">
          <el-table-column prop="teacherNumber" label="教师号" />
          <el-table-column prop="teacherName" label="姓名" />
          <el-table-column prop="departmentName" label="所属院系" />
          <el-table-column prop="projectTask" label="任务" />
        </el-table>

        <el-descriptions :column="1" border style="margin-top: 20px;">
          <el-descriptions-item label="本人任务">{{ currentProjectDetail.myProjectTask }}</el-descriptions-item>
          <el-descriptions-item label="本人项目奖金">{{ currentProjectDetail.myProjectSalary }}元</el-descriptions-item>
          <el-descriptions-item label="额外奖金">{{ currentProjectDetail.myProjectSalaryAddition }}元</el-descriptions-item>
          <el-descriptions-item label="备注">{{ currentProjectDetail.myComment }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="projectDetailModalVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, nextTick, computed } from 'vue';
// import axios from 'axios'; // No longer directly needed for these operations
import { ElMessage } from 'element-plus';
import Chart from 'chart.js/auto'; // Using Chart.js v3+
import { useTeacherStore } from '@/stores/teacherStore';
import { useProjectStore } from '@/stores/projectStore';
import { storeToRefs } from 'pinia';

const teacherStore = useTeacherStore();
const projectStore = useProjectStore();

const teacherId = ref(localStorage.getItem('userId'));

const { 
  currentTeacherProfile, 
  teacherSalaryMonths: rawSalaryMonths, 
  teacherMonthlySalaryDetail,
  loading: teacherStoreLoading 
} = storeToRefs(teacherStore);

const { 
  teacherProjects, 
  currentProjectDetailForTeacher,
  loading: projectStoreLoading 
} = storeToRefs(projectStore);


const userName = computed(() => currentTeacherProfile.value?.teacherName || '');
const selectedMonth = ref(0); 
const groupedYearMonths = ref({});

const salaryDetails = computed(() => teacherMonthlySalaryDetail.value);
const loadingSalaryDetails = computed(() => teacherStoreLoading.value); // Or a more specific loading state if added

const projectListNodone = computed(() => teacherProjects.value.filter(p => p.completedOrNot === '否'));
const projectListDone = computed(() => teacherProjects.value.filter(p => p.completedOrNot === '是'));
const loadingProjects = computed(() => projectStoreLoading.value); // Or a more specific loading state

const projectDetailModalVisible = ref(false);
const currentProjectDetail = computed(() => currentProjectDetailForTeacher.value);

let salaryChartInstance = null;

const chineseDigit = (num) => {
  const digits = ['零', '一', '二', '三', '四', '五', '六', '七', '八', '九', '十', '十一', '十二'];
  // Ensure month conversion is for 1-12, otherwise return original number (for year or other cases)
  if (num >= 1 && num <= 12) {
    return digits[num];
  }
  return num.toString();
};

const fetchUserName = async () => {
  if (!teacherId.value) return;
  try {
    await teacherStore.fetchTeacherProfile(teacherId.value);
  } catch (error) {
    // Error handled in store
  }
};

const processAvailableMonths = (rawMonthsData) => {
    const processed = {};
    if (rawMonthsData && rawMonthsData.length > 0) {
        const sortedRawMonths = [...rawMonthsData].sort((a, b) => b - a);
        for (const fullMonth of sortedRawMonths) {
            const year = Math.floor(fullMonth / 100).toString();
            const month = fullMonth % 100;
            if (!processed[year]) {
                processed[year] = [];
            }
            processed[year].push({
                label: `${chineseDigit(month)}月`,
                fullValue: fullMonth 
            });
        }
        for (const year in processed) {
            processed[year].sort((a,b) => (a.fullValue % 100) - (b.fullValue % 100));
        }
    }
    return processed;
};

const fetchAvailableMonths = async () => {
  if (!teacherId.value) return;
  try {
    const monthsData = await teacherStore.fetchTeacherAvailableSalaryMonths(teacherId.value);
    groupedYearMonths.value = processAvailableMonths(monthsData);
  } catch (error) {
    // Error handled in store
    groupedYearMonths.value = {};
  }
};

const fetchSalaryDetails = async (month) => {
  if (!teacherId.value) return;
  try {
    await teacherStore.fetchTeacherSalaryDetails(teacherId.value, month);
  } catch (error) {
    // Error handled in store
  }
};

const fetchProjectsForTable = async () => {
    if (!teacherId.value) return;
    try {
        // Fetches all projects for the teacher, not filtered by month for the main table display
        await projectStore.fetchProjectsForTeacher({ teacherId: teacherId.value });
    } catch (error) {
        // Error handled in store
    }
};


const updateChart = async (month) => {
  if (!teacherId.value) return;
  
  try {
    // Salary details should already be fetched or can be re-fetched if necessary
    // For simplicity, let's assume salaryDetails.value is up-to-date from fetchSalaryDetails
    const currentSalaryDetails = salaryDetails.value || await teacherStore.fetchTeacherSalaryDetails(teacherId.value, month);
    if (!currentSalaryDetails) {
        ElMessage.error('无法获取工资数据以更新图表');
        return;
    }
    const basicSalary = currentSalaryDetails.salaryBasic;

    // Fetch projects specifically for the selected month for the chart
    const projectsForChart = await projectStore.fetchProjectsForTeacher({ teacherId: teacherId.value, month: month });
    
    const projectNames = projectsForChart.map(project => project.projectName);
    const projectSalaries = projectsForChart.map(project => project.projectSalary);

    projectNames.push('基本工资');
    projectSalaries.push(basicSalary);

    await nextTick(); 

    const ctx = document.getElementById('salary-chart')?.getContext('2d');
    if (!ctx) {
        console.error('Canvas context not found for salary chart.');
        return;
    }

    if (salaryChartInstance) {
      salaryChartInstance.destroy();
    }

    salaryChartInstance = new Chart(ctx, {
      type: 'pie',
      data: {
        labels: projectNames,
        datasets: [{
          data: projectSalaries,
          backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF', '#FF9F40'].slice(0, projectSalaries.length),
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          tooltip: {
            callbacks: {
              label: function (context) {
                let label = context.label || '';
                if (label) {
                  label += ': ';
                }
                if (context.parsed !== null) {
                  label += new Intl.NumberFormat('zh-CN', { style: 'currency', currency: 'CNY' }).format(context.parsed);
                }
                return label;
              }
            }
          }
        }
      }
    });
  } catch (error) {
    console.error('Error updating chart:', error);
    ElMessage.error('更新图表失败');
  }
};


const handleMonthChange = (newMonthIndexStr) => {
  const newMonth = parseInt(newMonthIndexStr);
  selectedMonth.value = newMonth; 
  fetchSalaryDetails(newMonth);
  updateChart(newMonth);
  // Project table is not filtered by month in this view, so no need to re-fetch projects for table here
};

const showProjectDetailModal = async (projectRow) => {
  if (!teacherId.value) return;
  const projectId = projectRow.projectNumber;
  try {
    await projectStore.fetchProjectDetailForTeacher({ projectId, teacherId: teacherId.value });
    // currentProjectDetail is now a computed property reacting to store changes
    if (currentProjectDetail.value) {
        projectDetailModalVisible.value = true;
    } else {
        ElMessage.error('无法加载项目详情。');
    }
  } catch (error) {
    // Error handled in store
  }
};

const collaboratorRowClassName = ({ row }) => {
    if (row.teacherNumber.toString() === teacherId.value) { // Use camelCase
        return 'highlight-self-row';
    }
    return '';
};


onMounted(() => {
  if (!teacherId.value) {
    ElMessage.error('用户未登录或会话已过期。');
    // router.push('/login'); // Optionally redirect
    return;
  }
  fetchUserName();
  fetchAvailableMonths();
  fetchSalaryDetails(selectedMonth.value); // Load "所有" by default
  updateChart(selectedMonth.value);
  fetchProjectsForTable(); // Load all projects for the table
});

</script>

<style scoped>
.home-page {
  display: flex;
  flex-direction: column;
  min-height: calc(100vh - 0px); 
  padding: 20px;
  box-sizing: border-box;
}
.home-container {
  flex-grow: 1;
  width: 100%;
  max-width: 1200px; 
  margin: 0 auto; 
}
.welcome-card, .salary-details-card, .project-details-card {
  margin-bottom: 30px; /* Increased spacing */
  /* box-shadow and border-radius handled globally in App.vue */
  /* background and backdrop-filter handled globally */
  animation: fadeInUp 0.6s ease-out forwards;
  opacity: 0; 
}

/* Staggered animation for cards */
.salary-details-card {
  animation-delay: 0.15s;
}
.project-details-card {
  animation-delay: 0.3s;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.ui.header.teal {
  color: #00796b !important; /* Darker, richer teal */
  font-weight: 600;
}
.ui.center.aligned.header {
  text-align: center;
}
#salary-chart-container {
  position: relative; 
  /* height: 380px; */ /* Will be overridden by inline style for specific instance */
  width: 100%;
  padding: 15px; /* Adjusted padding for smaller container */
  background-color: rgba(255, 255, 255, 0.9); /* Match card background */
  border-radius: 10px; /* Consistent with cards */
  box-shadow: 0 4px 15px rgba(0,0,0,0.07); /* Subtle shadow */
  backdrop-filter: blur(5px); /* Subtle blur if cards have it */
}

/* Style for highlighting the current teacher in the modal table */
:deep(.el-table .highlight-self-row) {
  background-color: rgba(230, 255, 240, 0.8) !important; /* Lighter, more subtle green, with transparency */
  font-weight: bold;
}
:deep(.el-table .done-project-row) {
  background-color: rgba(240, 249, 235, 0.8) !important; /* Element's success light, with transparency */
}
.el-divider--horizontal {
    margin: 30px 0; /* Increased margin */
}

.el-select {
  width: 200px; 
}

/* Table styling is now largely handled by App.vue global styles */
/* .el-table { ... } */

.salary-details-card .el-main {
  padding: 0 0 0 20px; 
}

.home-month-sidebar {
  border-right: 1px solid rgba(0,0,0,0.08); /* Softer border */
  background-color: rgba(255, 255, 255, 0.7); /* Slightly transparent sidebar */
  backdrop-filter: blur(10px);
  border-radius: 8px 0 0 8px; /* Rounded corners on one side */
  padding: 10px 0;
  max-height: 340px; /* Adjusted to better align with new content height */
  overflow-y: auto;
}

.home-months-menu {
  border-right: none;
  background-color: transparent; /* Menu itself is transparent */
}

.home-months-menu .el-sub-menu__title,
.home-months-menu .el-menu-item {
  font-size: 14px; 
  color: #333;
  transition: background-color 0.3s ease, color 0.3s ease;
}
.home-months-menu .el-menu-item:hover,
.home-months-menu .el-sub-menu__title:hover {
  background-color: rgba(0, 121, 107, 0.1) !important; /* Teal accent hover */
  color: #00796b !important;
}
.home-months-menu .el-menu-item.is-active {
  background-color: rgba(0, 121, 107, 0.2) !important; /* Stronger teal accent for active */
  color: #004d40 !important;
  font-weight: 600;
}

.salary-details-card .el-container {
  margin-top: 20px;
}
</style>
