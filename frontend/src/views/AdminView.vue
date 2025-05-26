<template>
  <div class="admin-page-content">
    <div class="ui container" style="margin-top: 20px; text-align:center;">
        <h2 class="ui teal image header">
            <div class="content">西安石油大学工资管理系统 - 后台管理</div>
        </h2>
    </div>

    <el-tabs v-model="activeTab" type="border-card" class="admin-tabs" @tab-click="handleTabClick">
      <el-tab-pane label="项目管理" name="project">
        <div v-if="activeTab === 'project'">
          <el-container>
            <el-aside width="220px" class="month-sidebar">
              <el-menu
                :default-active="selectedProjectMonth.toString()"
                class="months-menu"
                @select="(index) => selectMonthForTab(index, 'project')"
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
              <h4 class="ui horizontal divider header"><el-icon><PriceTag /></el-icon> 项目管理</h4>
              <el-row :gutter="20" align="middle">
                <el-col :span="4">
                  <el-button type="primary" @click="openAddProjectModal"><el-icon><Plus /></el-icon> 添加</el-button>
                </el-col>
                <el-col :span="10">
                  <el-input v-model="searchProjectInput" placeholder="搜索项目..." clearable @keyup.enter="searchProjects" @clear="refreshProjectList">
                    <template #append>
                      <el-button @click="searchProjects"><el-icon><Search /></el-icon></el-button>
                    </template>
                  </el-input>
                </el-col>
                <el-col :span="4">
                  <el-button @click="refreshProjectList"><el-icon><Refresh /></el-icon> 刷新</el-button>
                </el-col>
                <el-col :span="6">
                    <el-upload
                        ref="projectUploadRef"
                        :auto-upload="false"
                        @change="handleProjectCsvSelectWrapper"
                        :show-file-list="false"
                        accept=".csv"
                        style="display: inline-block; margin-right: 10px;"
                        :disabled="isUploadingProjectsCsv"
                    >
                        <el-button type="success" :loading="isUploadingProjectsCsv"><el-icon><Upload /></el-icon> 上传项目CSV</el-button>
                    </el-upload>
                    <el-button @click="projectStore.downloadProjectTemplate()" type="info" :disabled="isUploadingProjectsCsv"><el-icon><Download /></el-icon>模板</el-button>
                </el-col>
              </el-row>

              <el-table :data="projectStore.projectsNodone" stripe style="width: 100%" v-loading="projectStore.loading">
                <el-table-column prop="projectNumber" label="项目编号" />
                <el-table-column prop="projectName" label="项目名称" />
                <el-table-column prop="leaderName" label="项目负责人" />
                <el-table-column prop="projectSalary" label="项目总金额" />
                <el-table-column label="操作" width="180">
                  <template #default="scope">
                    <el-button size="small" type="primary" @click="openProjectDetailModal(scope.row.projectNumber)">详情</el-button>
                    <el-button size="small" type="danger" @click="confirmDeleteProject(scope.row.projectNumber)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <el-divider content-position="left" v-if="projectStore.projectsDone.length > 0 && projectStore.projectsNodone.length > 0">已完成项目</el-divider>
              <el-table :data="projectStore.projectsDone" stripe style="width: 100%;" :row-style="{ backgroundColor: '#d4edda' }" v-loading="projectStore.loading">
                <el-table-column prop="projectNumber" label="项目编号">
                  <template #default="scope">
                    <el-tooltip content="该项目已完成" placement="top" :open-delay="500">
                      <span>{{ scope.row.projectNumber }}</span>
                    </el-tooltip>
                  </template>
                </el-table-column>
                <el-table-column prop="projectName" label="项目名称">
                  <template #default="scope">
                    <el-tooltip content="该项目已完成" placement="top" :open-delay="500">
                      <span>{{ scope.row.projectName }}</span>
                    </el-tooltip>
                  </template>
                </el-table-column>
                <el-table-column prop="leaderName" label="项目负责人">
                  <template #default="scope">
                    <el-tooltip content="该项目已完成" placement="top" :open-delay="500">
                      <span>{{ scope.row.leaderName }}</span>
                    </el-tooltip>
                  </template>
                </el-table-column>
                <el-table-column prop="projectSalary" label="项目总金额">
                  <template #default="scope">
                    <el-tooltip content="该项目已完成" placement="top" :open-delay="500">
                      <span>{{ scope.row.projectSalary }}</span>
                    </el-tooltip>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="180">
                  <template #default="scope">
                    <el-button size="small" type="primary" @click="openProjectDetailModal(scope.row.projectNumber)">详情</el-button>
                    <el-button size="small" type="danger" @click="confirmDeleteProject(scope.row.projectNumber)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-main>
          </el-container>
        </div>
      </el-tab-pane>

      <el-tab-pane label="教师与工资管理" name="employee">
        <div v-if="activeTab === 'employee'">
          <el-container>
            <el-aside width="220px" class="month-sidebar">
              <el-menu
                :default-active="selectedEmployeeMonth.toString()"
                class="months-menu"
                @select="(index) => selectMonthForTab(index, 'employee')"
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
              <h4 class="ui horizontal divider header"><el-icon><User /></el-icon> 教师与工资管理</h4>
              <el-row :gutter="20" align="middle">
                <el-col :span="4">
                  <el-button type="primary" @click="openAddEmployeeModal"><el-icon><Plus /></el-icon> 添加</el-button>
                </el-col>
                <el-col :span="10">
                  <el-input v-model="searchEmployeeInput" placeholder="搜索教师..." clearable @keyup.enter="searchEmployees" @clear="refreshEmployeeList">
                    <template #append>
                      <el-button @click="searchEmployees"><el-icon><Search /></el-icon></el-button>
                    </template>
                  </el-input>
                </el-col>
                <el-col :span="4">
                  <el-button @click="refreshEmployeeList"><el-icon><Refresh /></el-icon> 刷新</el-button>
                </el-col>
                <el-col :span="6">
                    <el-upload
                        ref="teacherUploadRef"
                        :auto-upload="false"
                        @change="handleTeacherCsvSelectWrapper"
                        :show-file-list="false"
                        accept=".csv"
                        style="display: inline-block; margin-right: 10px;"
                        :disabled="isUploadingTeachersCsv"
                    >
                        <el-button type="success" :loading="isUploadingTeachersCsv"><el-icon><Upload /></el-icon> 上传教师CSV</el-button>
                    </el-upload>
                    <el-button @click="teacherStore.downloadTeacherTemplate()" type="info" :disabled="isUploadingTeachersCsv"><el-icon><Download /></el-icon>模板</el-button>
                </el-col>
              </el-row>

              <el-table :data="teacherStore.employees" stripe style="width: 100%" v-loading="teacherStore.loading">
                <el-table-column prop="teacherNumber" label="教师号" />
                <el-table-column prop="teacherName" label="教师姓名" />
                <el-table-column prop="teacherGender" label="性别" />
                <el-table-column prop="teacherPosition" label="职称" />
                <el-table-column prop="teacherDuty" label="职务" />
                <el-table-column prop="teacherSalary" label="基本工资" />
                <el-table-column prop="projectSalary" label="项目工资" />
                <el-table-column label="操作" width="180">
                  <template #default="scope">
                    <el-button size="small" type="primary" @click="openEmployeeDetailModal(scope.row.teacherNumber)">编辑/详情</el-button>
                    <el-button size="small" type="danger" @click="confirmDeleteEmployee(scope.row.teacherNumber)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-main>
          </el-container>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- Project Detail Modal -->
    <el-dialog v-model="projectDetailModalVisible" title="项目详情" width="70%">
      <div v-if="projectStore.currentProject">
        <h3>{{ projectStore.currentProject.projectName }} #{{ projectStore.currentProject.projectNumber }}</h3>
        <p>主导院系: {{ projectStore.currentProject.departmentName }}</p>
        <h4>项目成员</h4>
        <el-table :data="projectStore.currentProject.collaborators" border> {/* Changed from assignments to collaborators */}
          <el-table-column prop="teacherNumber" label="教师号" />
          <el-table-column prop="teacherName" label="姓名" />
          <el-table-column prop="departmentName" label="所属院系" />
          <el-table-column prop="projectTask" label="任务" />
          <el-table-column prop="projectSalary" label="奖金" />
        </el-table>
        <el-descriptions :column="1" border style="margin-top:20px;">
            <el-descriptions-item label="项目总人数">{{ projectStore.currentProject.collaborators?.length || 0 }}</el-descriptions-item> {/* Changed from assignments to collaborators */}
            <el-descriptions-item label="项目总金额">{{ totalProjectSalary(projectStore.currentProject.collaborators) }}</el-descriptions-item> {/* Changed from assignments to collaborators */}
            <el-descriptions-item label="项目完成情况">{{ projectStore.currentProject.completedOrNot }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="openEditProjectModal(projectStore.currentProject.projectNumber)">编辑</el-button>
        <el-button type="primary" @click="projectDetailModalVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- Add/Edit Project Modal -->
    <el-dialog v-model="projectEditModalVisible" :title="isEditingProject ? '编辑项目' : '添加项目'" width="60%">
      <el-form ref="projectFormRef" :model="projectForm" label-width="100px">
        <el-form-item label="项目编号" prop="projectId" :rules="[{ required: true, message: '项目编号不能为空'}]">
          <el-input v-model.number="projectForm.projectId" :disabled="isEditingProject" />
        </el-form-item>
        <el-form-item label="项目名称" prop="projectName" :rules="[{ required: true, message: '项目名称不能为空'}]">
          <el-input v-model="projectForm.projectName" />
        </el-form-item>
        <el-form-item label="项目负责人" prop="projectManager" :rules="[{ required: true, message: '负责人不能为空'}]">
            <el-select v-model="projectForm.projectManager" filterable placeholder="搜索或选择教师号" style="width:100%;">
                 <el-option v-for="teacher in allTeachers" :key="teacher.teacherNumber" :label="`${teacher.teacherName} (${teacher.teacherNumber})`" :value="teacher.teacherNumber"></el-option>
            </el-select>
        </el-form-item>
        <el-form-item label="主导院系" prop="department" :rules="[{ required: true, message: '院系不能为空'}]">
             <el-select v-model="projectForm.department" placeholder="选择院系" style="width:100%;">
                <el-option v-for="dept in allDepartments" :key="dept.departmentNumber" :label="dept.departmentName" :value="dept.departmentNumber"></el-option> 
            </el-select>
        </el-form-item>
        <el-form-item label="计入月份" prop="monthDate" :rules="[{ required: true, message: '计入月份不能为空'}]">
            <el-date-picker
              v-model="projectForm.monthDate"
              type="month"
              placeholder="选择年月"
              format="YYYY年MM月"
              value-format="YYYYMM"
              style="width: 100%;"
            />
        </el-form-item>
        <h4>项目成员</h4>
        <div v-for="(member, index) in projectForm.projectMembers" :key="index" style="margin-bottom:10px; display:flex; gap:10px;">
            <el-select v-model.number="member.teacherId" filterable placeholder="教师号" style="flex:1;">
                 <el-option v-for="teacher in allTeachers" :key="`member-${teacher.teacherNumber}`" :label="`${teacher.teacherName} (${teacher.teacherNumber})`" :value="teacher.teacherNumber"></el-option>
            </el-select>
            <el-input v-model="member.task" placeholder="任务" style="flex:1;" />
            <el-input v-model.number="member.bonus" placeholder="奖金" type="number" style="flex:1;" />
            <el-button type="danger" @click="removeProjectMember(index)" icon="Delete" circle />
        </div>
        <el-button @click="addProjectMember" type="success" plain><el-icon><Plus/></el-icon> 添加成员</el-button>
        <el-form-item label="是否完成" prop="completed" style="margin-top:20px;">
            <el-switch v-model="projectForm.completed" active-value="是" inactive-value="否" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="projectEditModalVisible = false">取消</el-button>
        <el-button type="primary" @click="submitProjectForm">提交</el-button>
      </template>
    </el-dialog>

    <!-- Add/Edit Employee Modal -->
    <el-dialog v-model="employeeEditModalVisible" :key="employeeDialogKey" :title="isEditingEmployee ? '编辑教师' : '添加教师'" width="70%">
        <el-form ref="employeeFormRef" :model="employeeForm" label-position="top" class="employee-edit-form">
            <el-row :gutter="20">
                <el-col :span="8">
                    <el-form-item label="教师号" prop="employeeId" :rules="[{ required: true, message: '教师号不能为空'}]">
                        <el-input v-model.number="employeeForm.employeeId" :disabled="isEditingEmployee" />
                    </el-form-item>
                </el-col>
                <el-col :span="16">
                    <el-form-item label="教师姓名" prop="employeeName" :rules="[{ required: true, message: '姓名不能为空'}]">
                        <el-input v-model="employeeForm.employeeName" />
                    </el-form-item>
                </el-col>
            </el-row>
             <el-row :gutter="20">
                <el-col :span="8">
                    <el-form-item label="性别" prop="employeeGender">
                        <el-select v-model="employeeForm.employeeGender" placeholder="选择性别" style="width:100%;">
                            <el-option label="男" value="男"></el-option>
                            <el-option label="女" value="女"></el-option>
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="8">
                    <el-form-item label="年龄" prop="employeeAge">
                        <el-input-number v-model="employeeForm.employeeAge" :min="18" :max="100" style="width:100%;" />
                    </el-form-item>
                </el-col>
                <el-col :span="8">
                    <el-form-item label="所属院系" prop="employeeDepartment" :rules="[{ required: true, message: '院系不能为空'}]">
                        <el-select v-model="employeeForm.employeeDepartment" placeholder="选择院系" style="width:100%;">
                             <el-option v-for="dept in allDepartments" :key="`emp-dept-${dept.departmentNumber}`" :label="dept.departmentName" :value="dept.departmentNumber"></el-option> 
                        </el-select>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="手机号" prop="employeePhone">
                        <el-input v-model="employeeForm.employeePhone" />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="邮箱" prop="employeeEmail" :rules="[{type: 'email', message: '请输入有效邮箱'}]">
                        <el-input v-model="employeeForm.employeeEmail" />
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="职称" prop="employeePosition">
                        <el-input v-model="employeeForm.employeePosition" />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="职务" prop="employeeDuty">
                        <el-input v-model="employeeForm.employeeDuty" />
                    </el-form-item>
                </el-col>
            </el-row>
             <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="密码" prop="password">
                        <el-input v-model="employeeForm.password" type="password" placeholder="留空则不修改密码" />
                    </el-form-item>
                </el-col>
            </el-row>

            <h4>基本工资 (按月)</h4>
            <div v-for="(salary, index) in employeeForm.employeeBasic" :key="index" style="display:flex; gap:10px; margin-bottom:10px;">
                <el-date-picker
                  v-model="salary.monthDate"
                  type="month"
                  placeholder="选择年月"
                  format="YYYY年MM月"
                  value-format="YYYYMM"
                  style="flex:1;"
                />
                <el-input-number v-model="salary.salaryBasic" placeholder="基本工资" :min="0" style="flex:1;" />
                <el-button type="danger" @click="removeEmployeeSalaryMonth(index)" icon="Delete" circle />
            </div>
            <el-button @click="addEmployeeSalaryMonth" type="success" plain><el-icon><Plus/></el-icon> 添加工资月份</el-button>

            <div v-if="isEditingEmployee && employeeForm.employeeProjects?.length" style="margin-top:20px;">
                <h4>参与项目 (编辑奖金和备注)</h4>
                 <el-table :data="employeeForm.employeeProjects" border size="small">
                    <el-table-column prop="projectName" label="项目名称" />
                    <el-table-column prop="task" label="任务" />
                    <el-table-column label="奖金">
                        <template #default="scope"><el-input-number v-model="scope.row.bonus" size="small" :min="0"/></template>
                    </el-table-column>
                    <el-table-column label="额外奖金">
                        <template #default="scope"><el-input-number v-model="scope.row.extraBonus" size="small" :min="0"/></template>
                    </el-table-column>
                     <el-table-column label="额外奖金原因">
                        <template #default="scope"><el-input v-model="scope.row.extraBonusReason" size="small" /></template>
                    </el-table-column>
                    <el-table-column label="项目是否计薪" prop="completed">
                         <template #default="scope">
                            <el-switch v-model="scope.row.completed" active-value="是" inactive-value="否" />
                        </template>
                    </el-table-column>
                </el-table>
            </div>
        </el-form>
        <template #footer>
            <el-button @click="employeeEditModalVisible = false">取消</el-button>
            <el-button type="primary" @click="submitEmployeeForm">提交</el-button>
        </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, computed, watch, nextTick } from 'vue';
// import axios from 'axios'; // Keep for direct calls if any, or rely on stores
import { ElMessage, ElMessageBox } from 'element-plus';
import { useAuthStore } from '@/stores/authStore';
import { useProjectStore, handleProjectCsvSelect } from '@/stores/projectStore';
import { useTeacherStore, handleTeacherCsvSelect } from '@/stores/teacherStore';
import { storeToRefs } from 'pinia';

const authStore = useAuthStore();
const projectStore = useProjectStore();
const teacherStore = useTeacherStore();

const { allDepartmentsList: allDepartmentsFromProjectStore, adminProjectMonths } = storeToRefs(projectStore);
const { allTeachersList: allTeachersFromTeacherStore } = storeToRefs(teacherStore);

const activeTab = ref('project');
// const availableMonths = ref([]); // Removed

// New ref for grouped months
const groupedYearMonths = ref({});

// Project Management State (local for UI control, data from store)
const selectedProjectMonth = ref(0);
const searchProjectInput = ref('');
const projectDetailModalVisible = ref(false);
const projectEditModalVisible = ref(false);
const projectFormRef = ref(null);
const projectForm = reactive({ 
  projectId: null, projectName: '', projectManager: null, department: '',
  projectMembers: [], completed: '否', monthDate: null // 改为monthDate用于日期选择器
});
const isEditingProject = ref(false);
const projectUploadRef = ref(null); // Ref for project el-upload
const isUploadingProjectsCsv = ref(false); // Loading state for project CSV upload

// Employee Management State (local for UI control, data from store)
const selectedEmployeeMonth = ref(0);
const searchEmployeeInput = ref('');
const employeeEditModalVisible = ref(false);
const employeeFormRef = ref(null);
const employeeDialogKey = ref(0); // 新增: 用于强制刷新 el-dialog
const employeeForm = reactive({ 
    employeeId: null, employeeName: '', employeeGender: '', employeeAge: null,
    employeeDepartment: '', employeePhone: '', employeeEmail: '', employeePosition: '', employeeDuty: '', password: '',
    employeeBasic: [], employeeProjects: []
});
const isEditingEmployee = ref(false);
const teacherUploadRef = ref(null); // Ref for teacher el-upload
const isUploadingTeachersCsv = ref(false); // Loading state for teacher CSV upload

const allTeachers = computed(() => allTeachersFromTeacherStore.value);
const allDepartments = computed(() => allDepartmentsFromProjectStore.value);

const chineseDigit = (num) => { 
  const digits = ['零', '一', '二', '三', '四', '五', '六', '七', '八', '九', '十', '十一', '十二'];
  if (num >= 1 && num <= 12) {
    return digits[num];
  }
  return num.toString(); 
};

const fetchAllTeachers = async () => { 
    await teacherStore.fetchAllTeachersSimpleList();
};
const fetchAllDepartments = async () => { 
    await projectStore.fetchAllDepartments();
};

const processMonthsData = (rawMonths) => {
    const processedMonths = {};
    if (rawMonths && rawMonths.length > 0) {
      const sortedRawMonths = [...rawMonths].sort((a, b) => b - a); 
      for (const fullMonth of sortedRawMonths) {
        const year = Math.floor(fullMonth / 100).toString();
        const month = fullMonth % 100;
        if (!processedMonths[year]) {
          processedMonths[year] = [];
        }
        processedMonths[year].push({
          label: `${chineseDigit(month)}月`,
          fullValue: fullMonth 
        });
      }
      for (const year in processedMonths) {
          processedMonths[year].sort((a,b) => (a.fullValue % 100) - (b.fullValue % 100)); 
      }
    }
    return processedMonths;
};

const fetchAdminAvailableMonths = async () => { 
  if (!authStore.adminOfDepartment) {
    groupedYearMonths.value = {};
    return;
  }
  try {
    // Assuming adminProjectMonths is fetched and populated by the store action
    await projectStore.fetchAdminAvailableProjectMonths(authStore.adminOfDepartment);
    groupedYearMonths.value = processMonthsData(adminProjectMonths.value);
  } catch (error) {
    console.error('Error processing admin available months in component:', error);
    // ElMessage.error('加载可用月份失败'); // Store handles this
    groupedYearMonths.value = {}; // Clear on error
  }
};

// Function to handle month selection from the new sidebar
const selectMonthForTab = (fullMonthValueStr, tabName) => {
  const fullMonthValue = parseInt(fullMonthValueStr);
  if (tabName === 'project') {
    selectedProjectMonth.value = fullMonthValue;
    filterProjectsByMonth(fullMonthValue);
  } else if (tabName === 'employee') {
    selectedEmployeeMonth.value = fullMonthValue;
    filterEmployeesByMonth(fullMonthValue);
  }
};

// --- Project Management ---
const refreshProjectList = () => {
    searchProjectInput.value = '';
    selectedProjectMonth.value = 0;
    if (authStore.adminOfDepartment) projectStore.fetchProjects(authStore.adminOfDepartment);
};
const filterProjectsByMonth = (month) => {
    if (authStore.adminOfDepartment) projectStore.fetchProjects(authStore.adminOfDepartment, month, searchProjectInput.value);
};
const searchProjects = () => {
    if (authStore.adminOfDepartment) projectStore.fetchProjects(authStore.adminOfDepartment, selectedProjectMonth.value, searchProjectInput.value);
};

const openProjectDetailModal = async (projectId) => {
  await projectStore.fetchProjectById(projectId);
  if (projectStore.currentProject) projectDetailModalVisible.value = true;
};

const totalProjectSalary = (members) => { 
    if (!members || members.length === 0) return 0;
    return members.reduce((sum, member) => sum + (member.projectSalary || 0), 0); // Changed Project_salary to projectSalary
};

const openAddProjectModal = () => { 
    isEditingProject.value = false;
    Object.assign(projectForm, {
        projectId: null, projectName: '', projectManager: null, department: '',
        projectMembers: [{ teacherId: null, task: '', bonus: null }],
        completed: '否', monthDate: null // 使用monthDate
    });
    if(projectFormRef.value) projectFormRef.value.resetFields(); 
    projectEditModalVisible.value = true;
};
const openEditProjectModal = async (projectId) => { 
    isEditingProject.value = true;
    try {
        const projectData = await projectStore.fetchProjectById(projectId); 
        if (projectData) {
            projectForm.projectId = projectData.projectNumber;
            projectForm.projectName = projectData.projectName;
            projectForm.projectManager = projectData.leaderId;
            // Use allDepartments computed property which comes from the store
            const department = allDepartments.value.find(d => d.departmentName === projectData.departmentName);
            projectForm.department = department ? department.departmentNumber : ''; 
            // 直接使用YYYYMM格式的month数据
            projectForm.monthDate = projectData.month.toString();
            projectForm.completed = projectData.completedOrNot;
            projectForm.projectMembers = projectData.collaborators?.map(c => ({
                teacherId: c.teacherNumber,
                task: c.projectTask,
                bonus: c.projectSalary 
            })) || [{ teacherId: null, task: '', bonus: null }];

            projectDetailModalVisible.value = false;
            projectEditModalVisible.value = true;
        }
    } catch (error) { ElMessage.error('加载项目数据以编辑失败'); }
};
const addProjectMember = () => projectForm.projectMembers.push({ teacherId: null, task: '', bonus: null });
const removeProjectMember = (index) => projectForm.projectMembers.splice(index, 1);

const submitProjectForm = async () => {
    if (!projectFormRef.value) return;
    await projectFormRef.value.validate(async (valid) => {
        if (valid) {
            // 转换数据格式用于POST
            const submitData = {
                ...projectForm,
                month: parseInt(projectForm.monthDate) // 转换为数字格式YYYYMM
            };
            delete submitData.monthDate; // 删除UI用的字段
            
            const success = await projectStore.createOrUpdateProject(submitData, isEditingProject.value);
            if (success) projectEditModalVisible.value = false;
        }
    });
};
const confirmDeleteProject = (projectId) => { /* ... as before, use projectStore.deleteProject ... */ 
  ElMessageBox.confirm('确定要删除该项目吗？此操作不可撤销。', '警告', { /* ... */ })
  .then(async () => { await projectStore.deleteProject(projectId); })
  .catch(() => { /* User cancelled */ });
};

// --- Employee Management ---
const refreshEmployeeList = () => { /* ... use teacherStore.fetchEmployees ... */ 
    searchEmployeeInput.value = '';
    selectedEmployeeMonth.value = 0;
    if(authStore.adminOfDepartment) teacherStore.fetchEmployees(authStore.adminOfDepartment);
};
const filterEmployeesByMonth = (month) => { /* ... use teacherStore.fetchEmployees ... */ 
    if(authStore.adminOfDepartment) teacherStore.fetchEmployees(authStore.adminOfDepartment, month, searchEmployeeInput.value);
};
const searchEmployees = () => { /* ... use teacherStore.fetchEmployees ... */ 
    if(authStore.adminOfDepartment) teacherStore.fetchEmployees(authStore.adminOfDepartment, selectedEmployeeMonth.value, searchEmployeeInput.value);
};

const openAddEmployeeModal = () => { 
    isEditingEmployee.value = false;

    employeeForm.employeeId = null;
    employeeForm.employeeName = '';
    employeeForm.employeeGender = '';
    employeeForm.employeeAge = null;
    employeeForm.employeeDepartment = '';
    employeeForm.employeePhone = '';
    employeeForm.employeeEmail = '';
    employeeForm.employeePosition = '';
    employeeForm.employeeDuty = '';
    employeeForm.password = '';
    employeeForm.employeeBasic = [{ monthDate: null, salaryBasic: null }]; // 使用monthDate
    employeeForm.employeeProjects = [];

    employeeDialogKey.value++;
    employeeEditModalVisible.value = true;

    nextTick(() => {
        if (employeeFormRef.value) {
            employeeFormRef.value.clearValidate();
        }
    });
};
const openEmployeeDetailModal = async (employeeId) => { 
    isEditingEmployee.value = true;
    try {
        const employeeData = await teacherStore.fetchTeacherById(employeeId); 
        if (employeeData) {
            employeeForm.employeeId = employeeData.teacherNumber;
            employeeForm.employeeName = employeeData.teacherName;
            employeeForm.employeeGender = employeeData.teacherGender;
            employeeForm.employeeAge = employeeData.teacherAge;
            // Use allDepartments computed property
            employeeForm.employeeDepartment = allDepartments.value.find(d => d.departmentName === employeeData.departmentName)?.departmentNumber || '';
            employeeForm.employeePhone = employeeData.phone;
            employeeForm.employeeEmail = employeeData.email;
            employeeForm.employeePosition = employeeData.post;
            employeeForm.employeeDuty = employeeData.job;
            employeeForm.password = ''; 

            employeeForm.employeeBasic = employeeData.monthlySalaries?.map(s => ({
                monthDate: s.month.toString(), // 确保转换为字符串格式
                salaryBasic: s.salaryBasic
            })) || [{ monthDate: null, salaryBasic: null }];
            
            employeeForm.employeeProjects = employeeData.projectAssignments?.map(p => ({
                projectId: p.projectNumber,
                projectName: p.projectName,
                task: p.projectTask,
                bonus: p.projectSalary,
                extraBonus: p.projectSalaryAddition,
                extraBonusReason: p.comment,
                completed: p.completedOrNot
            })) || [];
            
            employeeDialogKey.value++;
            employeeEditModalVisible.value = true;
        }
    } catch (error) { ElMessage.error('加载教师详情失败'); }
};
const addEmployeeSalaryMonth = () => employeeForm.employeeBasic.push({ monthDate: null, salaryBasic: null });

const submitEmployeeForm = async () => { 
    if (!employeeFormRef.value) return;
     await employeeFormRef.value.validate(async (valid) => {
        if (valid) {
            // 转换数据格式用于POST
            const submitData = {
                ...employeeForm,
                employeeBasic: employeeForm.employeeBasic.map(basic => ({
                    month: parseInt(basic.monthDate), // 转换为数字格式YYYYMM
                    salaryBasic: basic.salaryBasic
                }))
            };
            
            const success = await teacherStore.createOrUpdateTeacher(submitData, isEditingEmployee.value);
            if (success) employeeEditModalVisible.value = false;
        }
     });
};
const confirmDeleteEmployee = (employeeId) => {
  ElMessageBox.confirm('确定要删除该教师吗？', '警告', { /* ... */ })
  .then(async () => { await teacherStore.deleteTeacher(employeeId); })
  .catch(() => { /* User cancelled */ });
};

// --- CSV Upload Logic ---
const handleProjectCsvSelectWrapper = (file) => {
    handleProjectCsvSelect(file, projectStore, isUploadingProjectsCsv, projectUploadRef, refreshProjectList);
};

const handleTeacherCsvSelectWrapper = (file) => {
    handleTeacherCsvSelect(file, teacherStore, isUploadingTeachersCsv, teacherUploadRef, refreshEmployeeList);
};

const handleTabClick = (tab) => {
  if (!authStore.adminOfDepartment) {
      ElMessage.warn("未配置管理部门，无法加载数据。");
      return;
  }
  // Data fetching will use the currently selected month for the respective tab
  if (tab.paneName === 'project') {
    // Ensure months are available for project tab if not already fetched for the current adminOf
    if (Object.keys(groupedYearMonths.value).length === 0 && authStore.adminOfDepartment) {
        fetchAdminAvailableMonths();
    }
    projectStore.fetchProjects(authStore.adminOfDepartment, selectedProjectMonth.value, searchProjectInput.value);
  } else if (tab.paneName === 'employee') {
    // Ensure months are available for employee tab (uses same months as project for now)
     if (Object.keys(groupedYearMonths.value).length === 0 && authStore.adminOfDepartment) {
        fetchAdminAvailableMonths();
    }
    teacherStore.fetchEmployees(authStore.adminOfDepartment, selectedEmployeeMonth.value, searchEmployeeInput.value);
  }
};

watch(() => authStore.adminOfDepartment, (newAdminOf) => {
    if (newAdminOf) {
        fetchAdminAvailableMonths(); // Fetch and process months for the new sidebar
        if (activeTab.value === 'project') {
            projectStore.fetchProjects(newAdminOf, selectedProjectMonth.value, searchProjectInput.value);
        } else if (activeTab.value === 'employee') {
            teacherStore.fetchEmployees(newAdminOf, selectedEmployeeMonth.value, searchEmployeeInput.value);
        }
    } else {
        groupedYearMonths.value = {}; // Clear months if adminOfDepartment is unset
    }
}, { immediate: true });


onMounted(() => {
  fetchAllTeachers();
  fetchAllDepartments();
  // Initial fetch is handled by the watcher on authStore.adminOfDepartment
  // However, ensure months are fetched if adminOfDepartment is already set on mount
  if (authStore.adminOfDepartment && Object.keys(groupedYearMonths.value).length === 0) {
    fetchAdminAvailableMonths();
  }
});

</script>

<style scoped>
.admin-page-content {
  padding: 20px;
  box-sizing: border-box;
  min-height: calc(100vh - 0px); 
}
.ui.container {
    max-width: 1400px; 
    margin-left: auto !important;
    margin-right: auto !important;
}

.ui.teal.image.header .content {
  font-size: 1.8em; /* Larger */
  font-weight: 700; /* Bolder */
  color: #00695c; /* Darker teal */
  text-shadow: 1px 1px 2px rgba(0,0,0,0.1); /* Subtle text shadow */
}

.admin-tabs {
  margin-top: 30px; /* More space */
  border-radius: 12px; /* Softer radius */
  box-shadow: 0 6px 25px rgba(0,0,0,0.08); /* Enhanced shadow */
  animation: fadeIn 0.7s ease-out;
  background-color: rgba(255, 255, 255, 0.8); /* Tabs background */
  backdrop-filter: blur(10px);
}
:deep(.el-tabs__header) {
  background-color: rgba(255, 255, 255, 0.5);
  border-top-left-radius: 12px;
  border-top-right-radius: 12px;
}
:deep(.el-tabs__item) {
  transition: all 0.3s ease;
}
:deep(.el-tabs__item.is-active) {
  color: #00796b !important; /* Teal for active tab */
  font-weight: 600;
}
:deep(.el-tabs__item:hover) {
  color: #004d40 !important;
}


@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.el-tab-pane > div { 
  animation: tabContentFadeIn 0.5s ease-out; /* Slightly longer animation */
}

@keyframes tabContentFadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}


.ui.horizontal.divider.header {
    display: flex;
    align-items: center;
    font-size: 1.3em; 
    margin-bottom: 2em; 
    margin-top: 1.2em;
    color: #00796b; /* Teal color */
    font-weight: 600; /* Bolder */
}
.ui.horizontal.divider.header .el-icon {
    margin-right: 12px; /* More space */
    font-size: 1.2em; /* Larger icon */
}

.el-row {
  align-items: center; 
}

.el-dialog { /* Global dialog enhancements can be here or in App.vue */
  border-radius: 12px !important;
  background-color: rgba(255, 255, 255, 0.95) !important;
  backdrop-filter: blur(15px) saturate(180%) !important;
  box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.37) !important;
}
.el-dialog h3, .el-dialog h4 {
    text-align: center;
    margin-bottom: 18px; 
    color: #2c3e50; /* Dark blue */
}
.el-dialog h3 {
    font-size: 1.6em; 
    font-weight: 700;
}
.el-dialog h4 {
    margin-top: 22px;
    font-size: 1.2em; 
    color: #34495e; /* Slightly lighter dark blue */
    font-weight: 600;
}

.el-form-item {
    margin-bottom: 20px; /* More space */
}

/* .el-dialog .el-button { ... } */ /* Handled by global button styles */

.el-upload, .el-button[type="info"] {
    margin-left: 10px;
}

.month-sidebar {
  border-right: 1px solid rgba(0,0,0,0.08); 
  background-color: rgba(255, 255, 255, 0.7); 
  backdrop-filter: blur(10px);
  border-radius: 8px 0 0 8px;
  padding: 10px 0;
  height: calc(100vh - 220px); /* Adjust as needed */
  min-height: 400px; 
  overflow-y: auto;
}

.months-menu {
  border-right: none; 
  background-color: transparent;
}

.months-menu :deep(.el-sub-menu__title),
.months-menu :deep(.el-menu-item) { /* Use :deep for scoped styles affecting child components */
  font-size: 14px;
  color: #333;
  transition: background-color 0.3s ease, color 0.3s ease;
}
.months-menu :deep(.el-menu-item:hover),
.months-menu :deep(.el-sub-menu__title:hover) {
  background-color: rgba(0, 121, 107, 0.1) !important; 
  color: #00796b !important;
}
.months-menu :deep(.el-menu-item.is-active) {
  background-color: rgba(0, 121, 107, 0.2) !important; 
  color: #004d40 !important;
  font-weight: 600;
}

.admin-tabs .el-tab-pane {
  padding: 0; 
}

.el-main {
  padding: 25px; /* More padding in main content */
}

.admin-page-content .el-row {
  margin-bottom: 25px; /* More space between rows */
}

/* Added style for plain success buttons */
:deep(.el-button--success.is-plain:not(:hover):not(:focus):not(:active)) {
  color: #ffffff; /* Darker green text for better contrast on plain success buttons */
}

/* Reduce margin-bottom for el-row within the employee edit form */
.employee-edit-form .el-row {
  margin-bottom: 0px;
}
</style>
