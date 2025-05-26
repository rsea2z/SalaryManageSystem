import { defineStore } from 'pinia';
import axios from 'axios';
import { ElMessage } from 'element-plus';
import { useAuthStore } from './authStore';

export const useTeacherStore = defineStore('teacher', {
  state: () => ({
    employees: [],
    currentEmployee: null, // For admin editing
    loading: false,
    currentTeacherProfile: null, // For ProfileView and HomeView (user name part)
    allTeachersList: [], // For dropdowns in AdminView project form
    teacherSalaryMonths: [], // For HomeView month selection
    teacherMonthlySalaryDetail: null, // For HomeView salary details
  }),
  actions: {
    async fetchEmployees(adminOf, month = 0, name = '') {
      this.loading = true;
      try {
        let url = `/api/department/teachers/${adminOf}/${month}`;
        if (name && name.trim() !== '') {
          url += `/${name.trim()}`;
        }
        const response = await axios.get(url);
        this.employees = response.data;
      } catch (error) {
        console.error('Error fetching employees:', error);
        ElMessage.error('加载教师列表失败');
      } finally {
        this.loading = false;
      }
    },
    async fetchTeacherById(employeeId) {
        this.loading = true;
        try {
            const response = await axios.get(`/api/department/teacher/detail/${employeeId}`);
            this.currentEmployee = response.data;
            return response.data; // Return for immediate use
        } catch (error) {
            ElMessage.error('加载教师详情失败');
            throw error;
        } finally {
            this.loading = false;
        }
    },
    async createOrUpdateTeacher(employeeData, isEditing) {
        this.loading = true;
        try {
            await axios.post('/api/department/teacher', employeeData);
            ElMessage.success(isEditing ? '教师信息更新成功' : '教师添加成功');
            const authStore = useAuthStore();
            if (authStore.adminOfDepartment) {
                 await this.fetchEmployees(authStore.adminOfDepartment /*, month, name */);
            }
            return true;
        } catch (error) {
            ElMessage.error(`教师信息提交失败: ${error.response?.data?.message || error.message}`);
            return false;
        } finally {
            this.loading = false;
        }
    },
    async deleteTeacher(employeeId) {
        this.loading = true;
        try {
            await axios.delete(`/api/department/teacher/${employeeId}`);
            ElMessage.success('教师删除成功');
            const authStore = useAuthStore();
            if (authStore.adminOfDepartment) {
                await this.fetchEmployees(authStore.adminOfDepartment /*, month, name */);
            }
        } catch (error) {
            ElMessage.error('删除教师失败');
        } finally {
            this.loading = false;
        }
    },
    // New action for ProfileView and HomeView (user name)
    async fetchTeacherProfile(teacherId) {
      this.loading = true;
      try {
        const response = await axios.get(`/api/teacher/information/${teacherId}`);
        this.currentTeacherProfile = response.data;
        return response.data;
      } catch (error) {
        console.error('Error fetching teacher profile:', error);
        ElMessage.error('加载用户基本信息失败');
        this.currentTeacherProfile = null;
        throw error;
      } finally {
        this.loading = false;
      }
    },
    // New action for AdminView (e.g., project manager dropdown)
    async fetchAllTeachersSimpleList() {
      this.loading = true;
      try {
        const response = await axios.get('/api/teacher/all');
        this.allTeachersList = response.data;
      } catch (error) {
        console.error('Error fetching all teachers list:', error);
        ElMessage.error('加载教师列表失败');
        this.allTeachersList = [];
      } finally {
        this.loading = false;
      }
    },
    // New action for HomeView (available months for salary)
    async fetchTeacherAvailableSalaryMonths(teacherId) {
      this.loading = true;
      try {
        const response = await axios.get(`/teacher/salary/month/${teacherId}`);
        this.teacherSalaryMonths = response.data; // Expects array like [202401, 202402]
        return response.data;
      } catch (error) {
        console.error('Error fetching teacher available salary months:', error);
        ElMessage.error('加载可用工资月份失败');
        this.teacherSalaryMonths = [];
        throw error;
      } finally {
        this.loading = false;
      }
    },
    // New action for HomeView (salary details for a month)
    async fetchTeacherSalaryDetails(teacherId, month) {
      this.loading = true;
      try {
        const response = await axios.get(`/api/teacher/salary/${teacherId}/${month}`);
        this.teacherMonthlySalaryDetail = response.data;
        return response.data;
      } catch (error) {
        console.error(`Error fetching salary details for month ${month}:`, error);
        ElMessage.error('加载工资详情失败');
        this.teacherMonthlySalaryDetail = null;
        throw error;
      } finally {
        this.loading = false;
      }
    },
    async downloadTeacherTemplate() {
      try {
        const authStore = useAuthStore();
        const response = await axios.get('/api/files/download/template/teacher', {
            headers: {
                'Authorization': `Bearer ${authStore.token}`,
                'Accept': 'text/csv' // Axios might not strictly need Accept for blob, but good to keep
            },
            responseType: 'blob' // Important for handling file downloads
        });
        
        // Axios wraps the response in a data property
        const blob = response.data; 
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'teacher_template.csv';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
        // No explicit ElMessage.success needed unless specified, as it's a download action
      } catch (error) {
          // Axios error handling
          const errorMessage = error.response ? `${error.response.status} ${error.response.statusText}` : error.message;
          ElMessage.error(`下载教师模板失败: ${errorMessage}`);
          console.error('下载教师模板失败:', error);
      }
    },
  }
});

// Helper to get current month in YYYYMM format
const getCurrentYearMonth = () => {
    const date = new Date();
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    return parseInt(`${year}${month}`);
};

// Teacher CSV Processing
export const handleTeacherCsvSelect = async (uploadFile, store, isUploadingRef, uploadComponentRef, refreshCallback) => {
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
            const teachers = parseTeacherCsv(csvText); // parseTeacherCsv will use the local getCurrentYearMonth
             if (teachers.length === 0 && csvText.trim().split(/\r\n|\n/).length > 1) {
                 ElMessage.warning('教师CSV解析未成功或文件内容不符合模板格式。');
            } else if (teachers.length > 0) {
                ElMessage.info(`开始上传 ${teachers.length} 位教师信息...`);
                let successCount = 0;
                let failCount = 0;
                for (const teacherData of teachers) {
                    const success = await store.createOrUpdateTeacher(teacherData, false);
                    if (success) {
                        successCount++;
                    } else {
                        failCount++;
                    }
                }
                ElMessage.success(`教师信息上传完成: ${successCount} 位成功, ${failCount} 位失败。`);
                 if (failCount > 0) {
                    ElMessage.warning('部分教师信息上传失败，请检查控制台或数据。');
                }
                if (refreshCallback) refreshCallback(); // Refresh list after upload
            } else if (csvText.trim().split(/\r\n|\n/).length <= 1 && csvText.trim() !== '') {
                 ElMessage.warning('CSV文件似乎只包含表头或为空。');
            }
        } catch (error) {
            console.error("CSV教师解析或上传失败:", error);
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

function parseTeacherCsv(csvText) {
    const lines = csvText.trim().split(/\r\n|\n/);
    if (lines.length < 2) return [];

    const headerLine = lines[0].trim();
    // Expected headers from NEW teacher_template.csv
    const expectedHeaderString = "工号,姓名,性别,出生日期,职称,职务,手机号,邮箱,院系编号,基本工资,密码"; // Updated
     if (headerLine !== expectedHeaderString) {
        ElMessage.error('教师CSV表头与模板不匹配，请下载最新模板。');
        console.error('Expected header:', expectedHeaderString);
        console.error('Actual header:', headerLine);
        return [];
    }
    const headers = headerLine.split(',');
    
    const teachers = [];
    const currentMonthYYYYMM = getCurrentYearMonth();

    for (let i = 1; i < lines.length; i++) {
        if (lines[i].trim() === '') continue;
        const values = lines[i].split(',');
        const rawTeacher = {};
        headers.forEach((header, index) => {
            rawTeacher[header.trim()] = values[index] ? values[index].trim() : '';
        });

        let age = null;
        if (rawTeacher['出生日期']) {
            // Date parsing logic for YYYY/MM/DD or YYYY-MM-DD
            let birthDateStr = rawTeacher['出生日期'];
            let birthDate;
            if (birthDateStr.includes('/')) {
                birthDate = new Date(birthDateStr.replace(/\//g, '-'));
            } else {
                birthDate = new Date(birthDateStr);
            }

            if (!isNaN(birthDate.getTime())) {
                const today = new Date();
                age = today.getFullYear() - birthDate.getFullYear();
                const m = today.getMonth() - birthDate.getMonth();
                if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
                    age--;
                }
            } else {
                ElMessage.error(`第 ${i + 1} 行教师数据的出生日期 "${rawTeacher['出生日期']}" 格式无效。请使用 YYYY/MM/DD 或 YYYY-MM-DD 格式。`);
                continue;
            }
        }

        const basicSalary = parseFloat(rawTeacher['基本工资']) || 0;
        
        if (isNaN(basicSalary)) { // Updated: only check basicSalary
            ElMessage.error(`第 ${i + 1} 行教师数据的基本工资 "${rawTeacher['基本工资']}" 不是有效数字。`);
            continue;
        }
        // totalBasicSalary is now just basicSalary from the CSV
        const totalBasicSalary = basicSalary;

        const teacherData = {
            employeeId: rawTeacher['工号'], 
            employeeName: rawTeacher['姓名'],
            employeeGender: rawTeacher['性别'],
            employeeAge: age,
            employeePosition: rawTeacher['职称'],
            employeeDuty: rawTeacher['职务'], // Added
            employeePhone: rawTeacher['手机号'], // Added
            employeeEmail: rawTeacher['邮箱'], // Added
            // Use the department ID string directly from CSV, without parseInt
            employeeDepartment: rawTeacher['院系编号'] ? rawTeacher['院系编号'].trim() : null,
            password: rawTeacher['密码'],
            employeeBasic: [{
                month: currentMonthYYYYMM, 
                salaryBasic: totalBasicSalary
            }],
            // employeePhone: '', // Removed, now from CSV
            // employeeEmail: '', // Removed, now from CSV
            // employeeDuty: '',  // Removed, now from CSV
            employeeProjects: [] 
        };
        
        // Validate required fields
        // The check for employeeDepartment will now use the string value (e.g., "DPT001")
        if (!teacherData.employeeId || !teacherData.employeeName || !teacherData.employeeDepartment) {
             ElMessage.error(`第 ${i + 1} 行教师数据不完整 (工号、姓名、院系编号为必填)。`);
            continue;
        }
        if (teacherData.employeeAge === null && rawTeacher['出生日期']) { // Age calculation failed but date was present
            // Error already shown by date parsing
            continue;
        }

        teachers.push(teacherData);
    }
    return teachers;
}