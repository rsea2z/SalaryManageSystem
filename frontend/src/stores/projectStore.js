import { defineStore } from 'pinia';
import axios from 'axios';
import { ElMessage } from 'element-plus';
import { useAuthStore } from './authStore'; // To get adminOfDepartment

export const useProjectStore = defineStore('project', {
  state: () => ({
    projectsNodone: [],
    projectsDone: [],
    currentProject: null, // For admin project detail/edit
    loading: false,
    allDepartmentsList: [], // For dropdowns in AdminView
    adminProjectMonths: [], // For AdminView month selection
    teacherProjects: [], // For HomeView project list
    currentProjectDetailForTeacher: null, // For HomeView project detail modal
    // Filters can be kept in component or here if shared across multiple components
    // selectedMonth: 0, 
    // searchInput: '',
  }),
  actions: {
    async fetchProjects(adminOf, month = 0, name = '') {
      this.loading = true;
      try {
        let url = `/api/department/projects/${adminOf}/${month}`;
        if (name && name.trim() !== '') {
          url += `/${name.trim()}`;
        }
        const response = await axios.get(url);
        this.projectsDone = response.data.filter(p => p.completedOrNot === '是'); // Changed from completed_or_not
        this.projectsNodone = response.data.filter(p => p.completedOrNot === '否'); // Changed from completed_or_not
      } catch (error) {
        console.error('Error fetching projects:', error);
        ElMessage.error('加载项目列表失败');
      } finally {
        this.loading = false;
      }
    },
    async fetchProjectById(projectId) {
      this.loading = true;
      try {
        const response = await axios.get(`/api/department/project/${projectId}`);
        this.currentProject = response.data;
        return response.data; // Return for immediate use if needed
      } catch (error)
      {
        ElMessage.error('加载项目详情失败');
        throw error;
      } finally {
        this.loading = false;
      }
    },
    async createOrUpdateProject(projectData, isEditing) {
      this.loading = true;
      try {
        // Backend uses POST for both create and update based on original Flask app
        await axios.post('/api/department/project', projectData);
        ElMessage.success(isEditing ? '项目更新成功' : '项目添加成功');
        // Refresh list
        const authStore = useAuthStore();
        if (authStore.adminOfDepartment) {
            // Assuming filters are passed or stored elsewhere to refresh correctly
            await this.fetchProjects(authStore.adminOfDepartment /*, month, name */);
        }
        return true;
      } catch (error) {
        ElMessage.error(`项目提交失败: ${error.response?.data?.message || error.message}`);
        return false;
      } finally {
        this.loading = false;
      }
    },
    async deleteProject(projectId) {
      this.loading = true;
      try {
        await axios.delete(`/api/department/project/${projectId}`);
        ElMessage.success('项目删除成功');
        const authStore = useAuthStore();
        if (authStore.adminOfDepartment) {
            await this.fetchProjects(authStore.adminOfDepartment /*, month, name */);
        }
      } catch (error) {
        ElMessage.error('删除项目失败');
      } finally {
        this.loading = false;
      }
    },
    // New action for AdminView (department dropdown)
    async fetchAllDepartments() {
      this.loading = true;
      try {
        const response = await axios.get('/api/department/projects/all'); // Endpoint returns DepartmentDto list
        this.allDepartmentsList = response.data.map(dept => ({
            departmentNumber: dept.departmentNumber,
            departmentName: dept.departmentName
        }));
      } catch (error) {
        console.error('Error fetching all departments:', error);
        ElMessage.error('加载院系列表失败');
        this.allDepartmentsList = [];
      } finally {
        this.loading = false;
      }
    },
    // New action for AdminView (available months for projects)
    async fetchAdminAvailableProjectMonths(adminOfDepartment) {
      this.loading = true;
      try {
        const response = await axios.get(`/api/department/projects/month/${adminOfDepartment}`);
        this.adminProjectMonths = response.data; // Expects array like [202401, 202402]
        return response.data;
      } catch (error) {
        console.error('Error fetching admin available project months:', error);
        ElMessage.error('加载可用项目月份失败');
        this.adminProjectMonths = [];
        throw error;
      } finally {
        this.loading = false;
      }
    },
    // New action for HomeView (projects for a teacher)
    async fetchProjectsForTeacher({ teacherId, month = 0 }) {
        this.loading = true;
        try {
            let url = `/api/project/teacher/${teacherId}`;
            if (month !== 0) {
                url += `?month=${month}`;
            }
            const response = await axios.get(url);
            // Assuming the response is an array of projects the teacher is involved in
            // The component will filter by completedOrNot if needed
            this.teacherProjects = response.data;
            return response.data;
        } catch (error) {
            console.error('Error fetching projects for teacher:', error);
            ElMessage.error('加载教师项目列表失败');
            this.teacherProjects = [];
            throw error;
        } finally {
            this.loading = false;
        }
    },
    // New action for HomeView (project detail from a teacher's perspective)
    async fetchProjectDetailForTeacher({ projectId, teacherId }) {
        this.loading = true;
        try {
            const response = await axios.get(`/api/project/${projectId}/${teacherId}`);
            this.currentProjectDetailForTeacher = response.data;
            return response.data;
        } catch (error) {
            console.error('Error fetching project detail for teacher:', error);
            ElMessage.error('加载项目详情失败');
            this.currentProjectDetailForTeacher = null;
            throw error;
        } finally {
            this.loading = false;
        }
    },
    async downloadProjectTemplate() {
        try {
            const authStore = useAuthStore();
            const response = await axios.get('/api/files/download/template/project', {
                headers: {
                    'Authorization': `Bearer ${authStore.token}`,
                    'Accept': 'text/csv' 
                },
                responseType: 'blob' // Important for file downloads
            });
            
            if (response.status === 200) { // Axios typically uses response.status
                const blob = response.data; // Axios puts the blob directly in data when responseType is 'blob'
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = 'project_template.csv';
                document.body.appendChild(a);
                a.click();
                document.body.removeChild(a);
                window.URL.revokeObjectURL(url);
            } else {
                // This else block might not be reached if axios throws an error for non-2xx responses
                console.error('下载失败:', response.status, response.statusText);
                ElMessage.error(`下载项目模板失败: ${response.status} ${response.statusText}`);
            }
        } catch (error) {
            // Axios errors often have a response object
            const status = error.response ? error.response.status : '未知';
            const statusText = error.response ? error.response.statusText : '错误';
            ElMessage.error(`下载项目模板失败: ${status} ${statusText}`);
            console.error('下载项目模板失败:', error);
        }
    },
  }
});

// Project CSV Processing
export const handleProjectCsvSelect = async (uploadFile, store, isUploadingRef, uploadComponentRef, refreshCallback) => {
    if (!uploadFile || !uploadFile.raw) {
        ElMessage.error('未选择文件或文件无效。');
        return;
    }
    isUploadingRef.value = true;
    const file = uploadFile.raw;
    const reader = new FileReader();

    reader.onload = async (e) => {
        const csvText = e.target.result;
        try {
            const projects = parseProjectCsv(csvText);
            if (projects.length === 0 && csvText.trim().split(/\r\n|\n/).length > 1) { // Check if parsing returned empty but there was content
                 ElMessage.warning('CSV解析未成功或文件内容不符合模板格式。');
            } else if (projects.length > 0) {
                ElMessage.info(`开始上传 ${projects.length} 个项目...`);
                let successCount = 0;
                let failCount = 0;
                for (const projectData of projects) {
                    const success = await store.createOrUpdateProject(projectData, false);
                    if (success) {
                        successCount++;
                    } else {
                        failCount++;
                    }
                }
                ElMessage.success(`项目上传完成: ${successCount} 个成功, ${failCount} 个失败。`);
                if (failCount > 0) {
                    ElMessage.warning('部分项目上传失败，请检查控制台或数据。');
                }
                if (refreshCallback) refreshCallback(); // Refresh list after upload
            } else if (csvText.trim().split(/\r\n|\n/).length <= 1 && csvText.trim() !== '') {
                 ElMessage.warning('CSV文件似乎只包含表头或为空。');
            }
        } catch (error) {
            console.error("CSV项目解析或上传失败:", error);
            ElMessage.error(`CSV处理失败: ${error.message}`);
        } finally {
            isUploadingRef.value = false;
            if (uploadComponentRef.value) {
                uploadComponentRef.value.clearFiles(); // Clear file list
            }
        }
    };
    reader.onerror = () => {
        ElMessage.error('读取文件失败。');
        isUploadingRef.value = false;
        if (uploadComponentRef.value) {
            uploadComponentRef.value.clearFiles();
        }
    };
    reader.readAsText(file, 'UTF-8'); // Specify UTF-8 encoding
};

function parseProjectCsv(csvText) {
    const lines = csvText.trim().split(/\r\n|\n/);
    if (lines.length < 2) return []; // Needs header + at least one data row

    const headerLine = lines[0].trim();
    // Expected headers from project_template.csv - UPDATED
    const expectedHeaderString = "项目编号,项目名称,项目负责人工号,主导院系编号,计入月份,项目总奖金,项目成员工号1,任务1,奖金比例1,项目成员工号2,任务2,奖金比例2,项目成员工号3,任务3,奖金比例3,项目成员工号4,任务4,奖金比例4,项目成员工号5,任务5,奖金比例5,项目成员工号6,任务6,奖金比例6,项目成员工号7,任务7,奖金比例7,项目成员工号8,任务8,奖金比例8,项目成员工号9,任务9,奖金比例9,项目成员工号10,任务10,奖金比例10,是否完成";
    if (headerLine !== expectedHeaderString) {
        ElMessage.error('项目CSV表头与模板不匹配，请下载最新模板。');
        console.error('Expected header:', expectedHeaderString);
        console.error('Actual header:', headerLine);
        return [];
    }
    const headers = headerLine.split(',');

    const projects = [];
    for (let i = 1; i < lines.length; i++) {
        if (lines[i].trim() === '') continue;
        const values = lines[i].split(',');
        const rawProject = {};
        headers.forEach((header, index) => {
            rawProject[header.trim()] = values[index] ? values[index].trim() : '';
        });

        const totalBonusString = rawProject['项目总奖金'];
        const totalBonus = parseFloat(totalBonusString);

        if (isNaN(totalBonus) || totalBonus < 0) {
            ElMessage.error(`第 ${i + 1} 行项目数据的 "项目总奖金" (${totalBonusString}) 不是有效的非负数字。`);
            continue;
        }

        const projectData = {
            projectId: rawProject['项目编号'] ? parseInt(rawProject['项目编号']) : null,
            projectName: rawProject['项目名称'],
            projectManager: rawProject['项目负责人工号'],
            department: rawProject['主导院系编号'] || null, // Removed parseInt, use string value directly
            month: rawProject['计入月份'] ? parseInt(rawProject['计入月份']) : null, // Assuming YYYYMM format
            totalBonus: totalBonus, // Store total bonus
            projectMembers: [],
            completed: rawProject['是否完成'] === '是' ? '是' : '否',
        };

        // Validate required fields
        if (!projectData.projectId || !projectData.projectName || !projectData.projectManager || !projectData.department || projectData.month === null) {
            ElMessage.error(`第 ${i + 1} 行项目数据不完整 (项目编号、名称、负责人、院系为必填项；或月份数据列未能正确读取)。请检查CSV文件。`);
            continue; 
        }
        if (isNaN(projectData.month) || projectData.month.toString().length !== 6) {
            ElMessage.error(`第 ${i + 1} 行项目数据的 "计入月份" (${rawProject['计入月份']}) 格式不正确，应为YYYYMM数字。`);
            continue;
        }


        let accumulatedRatio = 0;
        for (let j = 1; j <= 10; j++) {
            const memberId = rawProject[`项目成员工号${j}`];
            const task = rawProject[`任务${j}`];
            const bonusRatioString = rawProject[`奖金比例${j}`]; // Changed from 奖金 to 奖金比例

            if (memberId && task && bonusRatioString) {
                const bonusRatio = parseFloat(bonusRatioString);
                if (isNaN(bonusRatio) || bonusRatio < 0 || bonusRatio > 1) { // Ratio should be between 0 and 1
                     ElMessage.error(`第 ${i + 1} 行项目成员 ${j} 的奖金比例 "${bonusRatioString}" 不是0到1之间的有效数字。`);
                     continue; 
                }
                
                const memberBonus = projectData.totalBonus * bonusRatio;
                accumulatedRatio += bonusRatio;

                projectData.projectMembers.push({
                    teacherId: memberId,
                    task: task,
                    bonus: parseFloat(memberBonus.toFixed(2)), // Calculate actual bonus, ensure 2 decimal places
                });
            } else if (memberId || task || bonusRatioString) { // Partial member data
                 ElMessage.warn(`第 ${i + 1} 行项目成员 ${j} 的数据不完整（工号、任务、奖金比例需同时提供），已跳过该成员。`);
            }
        }

        // Optional: Validate if sum of ratios is close to 1 (e.g., within a small tolerance)
        if (projectData.projectMembers.length > 0 && Math.abs(accumulatedRatio - 1.0) > 0.01) { // Tolerance of 1%
            ElMessage.warn(`第 ${i + 1} 行项目 "${projectData.projectName}" 的成员奖金比例总和 (${accumulatedRatio.toFixed(4)}) 与1相差较大。请检查数据。`);
        }


        if (projectData.projectMembers.length === 0 && projectData.totalBonus > 0) {
             ElMessage.warn(`第 ${i + 1} 行项目 "${projectData.projectName}" 设置了总奖金但没有有效的项目成员分配奖金。`);
        } else if (projectData.projectMembers.length > 0 && projectData.totalBonus === 0) {
             ElMessage.warn(`第 ${i + 1} 行项目 "${projectData.projectName}" 有项目成员但总奖金为0。`);
        }


        projects.push(projectData);
    }
    return projects;
}