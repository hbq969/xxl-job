<script lang="ts" setup>
import {
  Edit, ArrowLeft, Plus, Delete, VideoPause, VideoPlay, DocumentCopy, Setting, UploadFilled, Printer, Document
} from '@element-plus/icons-vue'
import {ref, reactive, onMounted, computed, provide, inject} from 'vue'
import axios from '@/network'
import {msg, notify, encryptRSA, encryptAES, generateRandomAESKey} from '@/utils/Utils'
import {ElMessage, ElMessageBox} from 'element-plus'
import type {FormInstance, FormRules, TableInstance, UploadFile, UploadInstance} from 'element-plus'
import router from "@/router";
import {getLangData} from "@/i18n/locale";

const langData = getLangData()
const dictMap = ref<any>({})
const triggerStatusList = ref<any[]>([])
const scheduleTypeList = ref<any[]>([])
const glueTypeList = ref<any[]>([])
const executorRouteStrategyList = ref<any[]>([])
const misfireStrategyList = ref<any[]>([])
const executorBlockStrategyList = ref<any[]>([])

const queryDict = () => {
  axios({
    url: '/jobinfo/dict',
    method: 'get'
  }).then((res: any) => {
    if (res.data.state == 'OK') {
      dictMap.value = res.data.body || {}
      triggerStatusList.value = dictMap.value['xxl-job-admin,triggerStatus'] || [] as any[]
      scheduleTypeList.value = dictMap.value['xxl-job-admin,scheduleType'] || [] as any[]
      glueTypeList.value = dictMap.value['xxl-job-admin,glueType'] || [] as any[]
      executorRouteStrategyList.value = dictMap.value['xxl-job-admin,executorRouteStrategy'] || [] as any[]
      misfireStrategyList.value = dictMap.value['xxl-job-admin,misfireStrategy'] || [] as any[]
      executorBlockStrategyList.value = dictMap.value['xxl-job-admin,executorBlockStrategy'] || [] as any[]
    } else {
      msg(res.data.errorMessage, 'warning')
    }
  }).catch((err: any) => {
    console.log('', err)
    msg(err?.response.data.errorMessage, 'error')
  })
}

onMounted(() => {
  fetchJobGroup('')
  queryDict()
  query()
});

const multipleTableRef = ref<TableInstance>()
const selectedCount = ref(0)
const handleSelectionChange = (rows: any[]) => {
  selectedCount.value = rows.length
}

const debounce = (callback: (...args: any[]) => void, delay: number) => {
  let tid: any;
  return function (...args: any[]) {
    const ctx = self;
    tid && clearTimeout(tid);
    tid = setTimeout(() => {
      callback.apply(ctx, args);
    }, delay);
  };
};

const _ = (window as any).ResizeObserver;
(window as any).ResizeObserver = class ResizeObserver extends _ {
  constructor(callback: (...args: any[]) => void) {
    callback = debounce(callback, 20);
    super(callback);
  }
};

const form = reactive({
  jobGroup: null,
  triggerStatus: null,
  jobDesc: '',
  executorHandler: '',
  author: '',
  start: 1,
  length: 10
})
const total = ref(0)
const list = ref<any[]>([])

const query = () => {
  axios({
    url: '/jobinfo/pageList',
    method: 'get',
    params: form
  }).then((res: any) => {
    if (res.data.state == 'OK') {
      total.value = res.data.body.total || 0
      list.value = res.data.body.list || [] as any[]
    } else {
      msg(res.data.errorMessage, 'warning')
    }
  }).catch((err: any) => {
    console.log('', err)
    msg(err?.response.data.errorMessage, 'error')
  })
}

const formRef = ref<FormInstance>();
const rules = reactive<FormRules>({
  jobGroup: [{required: true, message: langData.formValidateNotNull, trigger: 'blur'}],
  jobDesc: [{required: true, message: langData.formValidateNotNull, trigger: 'blur'}],
  alarmEmail: [{required: true, message: langData.formValidateNotNull, trigger: 'blur'}],
  scheduleType: [{required: true, message: langData.formValidateNotNull, trigger: 'blur'}],
  scheduleConf: [{required: true, message: langData.formValidateNotNull, trigger: 'blur'}],
  glueType: [{required: true, message: langData.formValidateNotNull, trigger: 'blur'}],
  executorHandler: [{required: true, message: langData.formValidateNotNull, trigger: 'blur'}],
  executorRouteStrategy: [{required: true, message: langData.formValidateNotNull, trigger: 'blur'}],
  misfireStrategy: [{required: true, message: langData.formValidateNotNull, trigger: 'blur'}],
  executorBlockStrategy: [{required: true, message: langData.formValidateNotNull, trigger: 'blur'}],
  executorTimeout: [{required: true, message: langData.formValidateNotNull, trigger: 'blur'}],
  executorFailRetryCount: [{required: true, message: langData.formValidateNotNull, trigger: 'blur'}]
})
const taskForm = reactive({
  id: null,
  jobGroup: null,
  jobDesc: '',
  author: '',
  alarmEmail: '',
  scheduleType: '',
  scheduleConf: '',
  cronGen_display: '',
  schedule_conf_CRON: '',
  schedule_conf_FIX_RATE: '',
  schedule_conf_FIX_DELAY: '',
  glueType: '',
  executorHandler: '',
  executorParam: '',
  executorRouteStrategy: '',
  childJobId: '',
  misfireStrategy: '',
  executorBlockStrategy: '',
  executorTimeout: 5,
  executorFailRetryCount: 0,
  glueRemark: 'GLUE代码初始化',
  glueSource: '',
})
const drawer = ref(false)
const drawerTitle = ref(langData.addTask)
const submitting = ref(false)

const showAddTaskDialog = () => {
  drawer.value = true
  drawerTitle.value = langData.addTask
  formRef.value?.clearValidate()
  taskForm.id = null
  taskForm.jobGroup = null
  taskForm.jobDesc = ''
  taskForm.author = ''
  taskForm.alarmEmail = 'foo@bar.com'
  taskForm.scheduleType = 'CRON'
  taskForm.scheduleConf = '0 */30 * * * ?'
  taskForm.cronGen_display = ''
  taskForm.schedule_conf_CRON = ''
  taskForm.schedule_conf_FIX_RATE = ''
  taskForm.schedule_conf_FIX_DELAY = ''
  taskForm.glueType = 'BEAN'
  taskForm.executorHandler = ''
  taskForm.executorParam = ''
  taskForm.executorRouteStrategy = 'FIRST'
  taskForm.childJobId = ''
  taskForm.misfireStrategy = 'FIRE_ONCE_NOW'
  taskForm.executorBlockStrategy = 'SERIAL_EXECUTION'
  taskForm.executorTimeout = 5
  taskForm.executorFailRetryCount = 0
  taskForm.glueRemark = 'GLUE代码初始化'
  taskForm.glueSource = ''
}

const showEditTaskDialog = (row: any) => {
  drawer.value = true
  drawerTitle.value = langData.editTask
  formRef.value?.clearValidate()
  taskForm.id = row.id
  taskForm.jobGroup = row.jobGroup
  taskForm.jobDesc = row.jobDesc
  taskForm.author = row.author
  taskForm.alarmEmail = row.alarmEmail
  taskForm.scheduleType = row.scheduleType
  taskForm.scheduleConf = row.scheduleConf
  taskForm.cronGen_display = row.cronGen_display
  taskForm.schedule_conf_CRON = row.schedule_conf_CRON
  taskForm.schedule_conf_FIX_RATE = row.schedule_conf_FIX_RATE
  taskForm.schedule_conf_FIX_DELAY = row.schedule_conf_FIX_DELAY
  taskForm.glueType = row.glueType
  taskForm.executorHandler = row.executorHandler
  taskForm.executorParam = row.executorParam
  taskForm.executorRouteStrategy = row.executorRouteStrategy
  taskForm.childJobId = row.childJobId
  taskForm.misfireStrategy = row.misfireStrategy
  taskForm.executorBlockStrategy = row.executorBlockStrategy
  taskForm.executorTimeout = row.executorTimeout
  taskForm.executorFailRetryCount = row.executorFailRetryCount
  taskForm.glueRemark = row.glueRemark
  taskForm.glueSource = row.glueSource
}

const updateTask = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid, fields) => {
    if (valid) {
      submitting.value = true
      axios({
        url: drawerTitle.value == langData.addTask ? '/jobinfo/add' : '/jobinfo/update',
        method: 'post',
        data: taskForm,
      }).then((res: any) => {
        if (res.data.state == 'OK') {
          msg(res.data.body, 'success')
          drawer.value = false
          query()
        } else {
          msg(res.data.errorMessage, 'warning')
        }
      }).catch((err: any) => {
        console.log('', err)
        msg(err?.response.data.errorMessage, 'error')
      }).finally(() => {
        submitting.value = false
      })
    }
  })
}

const deleteTask = (row: any) => {
  axios({
    url: '/jobinfo/remove',
    method: 'delete',
    params: {id: row.id}
  }).then((res: any) => {
    if (res.data.state == 'OK') {
      msg(res.data.body, 'success')
      query()
    } else {
      msg(res.data.errorMessage, 'warning')
    }
  }).catch((err: any) => {
    console.log('', err)
    msg(err?.response.data.errorMessage, 'error')
  })
}

const startTask = (row: any) => {
  axios({
    url: '/jobinfo/start',
    method: 'post',
    params: {id: row.id}
  }).then((res: any) => {
    if (res.data.state == 'OK') {
      msg(res.data.body, 'success')
      query()
    } else {
      msg(res.data.errorMessage, 'warning')
    }
  }).catch((err: any) => {
    console.log('', err)
    msg(err?.response.data.errorMessage, 'error')
  })
}

const stopTask = (row: any) => {
  axios({
    url: '/jobinfo/stop',
    method: 'post',
    params: {id: row.id}
  }).then((res: any) => {
    if (res.data.state == 'OK') {
      msg(res.data.body, 'success')
      query()
    } else {
      msg(res.data.errorMessage, 'warning')
    }
  }).catch((err: any) => {
    console.log('', err)
    msg(err?.response.data.errorMessage, 'error')
  })
}

const copyTask = (row: any) => {
  drawer.value = true
  drawerTitle.value = langData.addTask
  formRef.value?.clearValidate()
  taskForm.id = null
  taskForm.jobGroup = row.jobGroup
  taskForm.jobDesc = row.jobDesc
  taskForm.author = row.author
  taskForm.alarmEmail = row.alarmEmail
  taskForm.scheduleType = row.scheduleType
  taskForm.scheduleConf = row.scheduleConf
  taskForm.cronGen_display = row.cronGen_display
  taskForm.schedule_conf_CRON = row.schedule_conf_CRON
  taskForm.schedule_conf_FIX_RATE = row.schedule_conf_FIX_RATE
  taskForm.schedule_conf_FIX_DELAY = row.schedule_conf_FIX_DELAY
  taskForm.glueType = row.glueType
  taskForm.executorHandler = row.executorHandler
  taskForm.executorParam = row.executorParam
  taskForm.executorRouteStrategy = row.executorRouteStrategy
  taskForm.childJobId = row.childJobId
  taskForm.misfireStrategy = row.misfireStrategy
  taskForm.executorBlockStrategy = row.executorBlockStrategy
  taskForm.executorTimeout = row.executorTimeout
  taskForm.executorFailRetryCount = row.executorFailRetryCount
  taskForm.glueRemark = row.glueRemark
  taskForm.glueSource = row.glueSource
}

const immediateTaskForm = reactive({
  id: '',
  executorParam: '',
  addressList: ''
})
const dialogFormVisible = ref(false)
const formRef1 = ref<FormInstance>();
const rules1 = reactive<FormRules>({
  id: [{required: true, message: langData.formValidateNotNull, trigger: 'blur'}],
})
const showImmediateTaskDialog = (row: any) => {
  dialogFormVisible.value = true
  formRef1.value?.clearValidate()
  immediateTaskForm.id = row.id
  immediateTaskForm.executorParam = ''
  immediateTaskForm.addressList = ''
}

const startTaskImmediate = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid, fields) => {
    if (valid) {
      axios({
        url: '/jobinfo/trigger',
        method: 'post',
        params: immediateTaskForm
      }).then((res: any) => {
        if (res.data.state == 'OK') {
          msg(res.data.body, 'success')
          dialogFormVisible.value = false
          query()
        } else {
          msg(res.data.errorMessage, 'warning')
        }
      }).catch((err: any) => {
        console.log('', err)
        msg(err?.response.data.errorMessage, 'error')
      })
    }
  })
}

const toTaskLogList = (row: any) => {
  router.push({
    path: '/log/list',
    query: {
      jobGroup: row.jobGroup,
      jobId: row.id
    }
  })
}

const JobGroupList = ref<any[]>([])
const fetchJobGroup = (query: string) => {
  axios({
    url: '/user/permission/list',
    method: 'get',
  }).then((res: any) => {
    if (res.data.state == 'OK') {
      if (query && query != '') {
        let list = res.data.body || []
        JobGroupList.value = list.filter((item: any) => item.value.includes(query))
      } else {
        JobGroupList.value = res.data.body
      }
    } else {
      notify(langData.notifyTitle, res.data.errorMessage, 'warning')
    }
  }).catch((err: Error) => {
    console.log('', err)
    notify(langData.notifyTitle, langData.axiosRequestErr, 'error')
  })
}

const getSelectedIds = (): number[] => {
  return multipleTableRef.value?.getSelectionRows().map((row: any) => row.id) || []
}

const executeBatchAction = (url: string, ids: number[], onSuccess?: () => void) => {
  axios({
    url,
    method: 'post',
    data: ids
  }).then((res: any) => {
    if (res.data.state == 'OK') {
      msg(res.data.body, 'success')
      query()
      if (onSuccess) onSuccess()
    } else {
      msg(res.data.errorMessage, 'warning')
    }
  }).catch((err: any) => {
    console.log('', err)
    msg(err?.response.data.errorMessage, 'error')
  })
}

const batchStartTasks = () => executeBatchAction('/jobinfo/batchStart', getSelectedIds())
const batchStopTasks = () => executeBatchAction('/jobinfo/batchStop', getSelectedIds())
const batchDeleteTasks = () => {
  const ids = getSelectedIds()
  executeBatchAction('/jobinfo/batchDelete', ids)
}

const batchExecutorDialogVisible = ref(false)
const batchExecutorForm = reactive({ jobGroup: null as number | null })

const showBatchModifyExecutorDialog = () => {
  batchExecutorForm.jobGroup = null
  batchExecutorDialogVisible.value = true
}

const batchUpdateJobGroup = () => {
  if (!batchExecutorForm.jobGroup) {
    msg(langData.pleaseSelectExecutor, 'warning')
    return
  }
  const ids = getSelectedIds()
  axios({
    url: '/jobinfo/batchUpdateJobGroup',
    method: 'post',
    data: { ids, jobGroup: batchExecutorForm.jobGroup }
  }).then((res: any) => {
    if (res.data.state == 'OK') {
      msg(res.data.body, 'success')
      batchExecutorDialogVisible.value = false
      query()
    } else {
      msg(res.data.errorMessage, 'warning')
    }
  }).catch((err: any) => {
    console.log('', err)
    msg(err?.response.data.errorMessage, 'error')
  })
}

const importDialogVisible = ref(false)
const importForm = reactive({ file: null as any | null })
const uploadRef = ref<UploadInstance>()

const showImportDialog = () => {
  importDialogVisible.value = true
  uploadRef.value?.clearFiles()
}

const fileChange = (uploadFile: UploadFile) => {
  importForm.file = uploadFile.raw
}

const fileRemove = () => {
  importForm.file = null
}

const fileImport = () => {
  if (importForm.file == null) {
    msg('导入文件为空', 'warning')
    return
  }
  const formData = new FormData()
  formData.append('file', importForm.file)
  axios({
    url: '/jobinfo/import',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  }).then((res: any) => {
    if (res.data.state == 'OK') {
      msg(res.data.body, 'success')
      importDialogVisible.value = false
      uploadRef.value!.clearFiles()
      query()
    } else {
      msg(res.data.errorMessage, 'warning')
      uploadRef.value!.clearFiles()
    }
  }).catch((err: any) => {
    console.log('', err)
    msg(err?.response.data.errorMessage, 'error')
    uploadRef.value!.clearFiles()
  })
}

const exportData = () => {
  const exportParams: any = {}
  if (form.jobGroup) exportParams.jobGroup = form.jobGroup
  if (form.triggerStatus != null) exportParams.triggerStatus = form.triggerStatus
  if (form.jobDesc) exportParams.jobDesc = form.jobDesc
  if (form.executorHandler) exportParams.executorHandler = form.executorHandler
  if (form.author) exportParams.author = form.author
  axios({
    url: '/jobinfo/export',
    method: 'post',
    data: exportParams,
    responseType: 'blob'
  }).then((res: any) => {
    const blob = new Blob([res.data], { type: 'text/csv' })
    const fileName = 'xxl-job任务数据.csv'
    if ('download' in document.createElement('a')) {
      const elink = document.createElement('a')
      elink.download = fileName
      elink.style.display = 'none'
      elink.href = URL.createObjectURL(blob)
      document.body.appendChild(elink)
      elink.click()
      URL.revokeObjectURL(elink.href)
      document.body.removeChild(elink)
    }
  }).catch((err: any) => {
    console.log('', err)
    msg(langData.axiosRequestErr, 'error')
  })
}

const downloadTemplate = () => {
  axios({
    url: '/jobinfo/template',
    method: 'get',
    responseType: 'blob'
  }).then((res: any) => {
    const blob = new Blob([res.data], { type: 'text/csv' })
    const fileName = 'xxl-job导入模版.csv'
    if ('download' in document.createElement('a')) {
      const elink = document.createElement('a')
      elink.download = fileName
      elink.style.display = 'none'
      elink.href = URL.createObjectURL(blob)
      document.body.appendChild(elink)
      elink.click()
      URL.revokeObjectURL(elink.href)
      document.body.removeChild(elink)
    }
  }).catch((err: any) => {
    console.log('', err)
    msg(langData.axiosRequestErr, 'error')
  })
}

</script>

<template>
  <div class="task-page view-enter">
    <!-- 搜索卡片 -->
    <div class="search-card">
      <div class="card-header">
        <span class="card-title">任务搜索</span>
        <span class="card-badge">{{ total }} 个任务</span>
      </div>
      <el-form :model="form" size="small" label-position="right" inline-message :inline="true" class="search-form">
        <el-form-item :label="langData.executor" prop="jobGroup">
          <el-select v-model="form.jobGroup" :placeholder="langData.formSelectPlaceholder" size="small" clearable
                     filterable remote automatic-dropdown :remote-method="fetchJobGroup">
            <el-option :key="item.id" :label="item.title+' ('+item.addressList+')'" :value="item.id" v-for="item in JobGroupList"/>
          </el-select>
        </el-form-item>
        <el-form-item :label="langData.taskStatus" prop="triggerStatus">
          <el-select v-model="form.triggerStatus" :placeholder="langData.formSelectPlaceholder" size="small" clearable
                     filterable automatic-dropdown>
            <el-option :key="item.key" :label="item.value" :value="item.key" v-for="item in triggerStatusList"/>
          </el-select>
        </el-form-item>
        <el-form-item :label="langData.taskDesc" prop="jobDesc">
          <el-input v-model="form.jobDesc" type="text" clearable/>
        </el-form-item>
        <el-form-item :label="langData.taskHandler" prop="executorHandler">
          <el-input v-model="form.executorHandler" type="text" clearable/>
        </el-form-item>
        <el-form-item :label="langData.tableHeaderCreator" prop="author">
          <el-input v-model="form.author" type="text" clearable/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="small" @click="query()">{{ langData.btnSearch }}</el-button>
          <el-button type="success" :icon="Plus" circle @click="showAddTaskDialog" :title="langData.btnAdd"/>
        </el-form-item>
      </el-form>
    </div>

    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <div class="batch-actions" :class="{ active: selectedCount > 0 }">
          <template v-if="selectedCount">
            <el-popconfirm :title="langData.confirmOpera" @confirm="batchStartTasks" confirm-button-type="warning">
              <template #reference>
                <span class="tool-btn start"><el-icon :size="16"><VideoPlay/></el-icon> 启动</span>
              </template>
            </el-popconfirm>
            <el-popconfirm :title="langData.confirmOpera" @confirm="batchStopTasks" confirm-button-type="warning">
              <template #reference>
                <span class="tool-btn pause"><el-icon :size="16"><VideoPause/></el-icon> 暂停</span>
              </template>
            </el-popconfirm>
            <el-popconfirm :title="langData.confirmDelete" @confirm="batchDeleteTasks" confirm-button-type="danger">
              <template #reference>
                <span class="tool-btn delete"><el-icon :size="16"><Delete/></el-icon> 删除</span>
              </template>
            </el-popconfirm>
            <span class="tool-btn setting" :title="langData.batchModifyExecutor" @click="showBatchModifyExecutorDialog"><el-icon :size="16"><Setting/></el-icon> 修改执行器</span>
          </template>
          <template v-else>
            <span class="selected-hint">选中项进行操作</span>
          </template>
        </div>
      </div>
      <div class="toolbar-right">
        <el-tooltip :content="langData.importTask" effect="dark" placement="top">
          <span class="tool-btn ghost" @click="showImportDialog"><el-icon :size="14"><UploadFilled/></el-icon> 导入</span>
        </el-tooltip>
        <el-tooltip :content="langData.exportTask" effect="dark" placement="top">
          <span class="tool-btn ghost" @click="exportData"><el-icon :size="14"><Printer/></el-icon> 导出</span>
        </el-tooltip>
        <el-tooltip :content="langData.downloadTemplate" effect="dark" placement="top">
          <span class="tool-btn ghost" @click="downloadTemplate"><el-icon :size="14"><Document/></el-icon> 模板</span>
        </el-tooltip>
      </div>
    </div>

    <!-- 表格 -->
    <el-table :data="list" style="width: 100%" table-layout="fixed" :stripe="false"
              size="small" :highlight-current-row="true" ref="multipleTableRef"
              @selection-change="handleSelectionChange" class="data-table">
      <el-table-column type="selection" header-align="center" align="center" width="40"/>
      <el-table-column fixed="left" :label="langData.tableHeaderOp" width="90" header-align="center" align="center">
        <template #default="scope">
          <div class="row-actions">
            <el-icon @click="showEditTaskDialog(scope.row)" :color="'var(--el-color-primary)'" :size="14" :title="langData.btnEdit"><Edit/></el-icon>
            <el-popconfirm :title="langData.confirmDelete" @confirm="deleteTask(scope.row)" icon-color="red" confirm-button-type="danger">
              <template #reference>
                <el-icon color="red" :size="14"><Delete/></el-icon>
              </template>
            </el-popconfirm>
            <el-popconfirm :title="langData.confirmOpera" @confirm="startTask(scope.row)" confirm-button-type="danger" v-if="scope.row.triggerStatus==0">
              <template #reference>
                <el-icon :color="'var(--el-color-success)'" :size="14" :title="langData.startTask"><VideoPlay/></el-icon>
              </template>
            </el-popconfirm>
            <el-popconfirm :title="langData.confirmOpera" @confirm="stopTask(scope.row)" confirm-button-type="danger" v-if="scope.row.triggerStatus==1">
              <template #reference>
                <el-icon :color="'var(--el-color-warning)'" :size="14" :title="langData.stopTask"><VideoPause/></el-icon>
              </template>
            </el-popconfirm>
            <el-icon :color="'var(--el-color-info)'" :size="14" :title="langData.copyTask" @click="copyTask(scope.row)"><DocumentCopy/></el-icon>
            <el-icon :color="'var(--el-color-primary)'" :size="14" :title="langData.runImmediate" @click="showImmediateTaskDialog(scope.row)">
              <svg t="1761619792188" class="icon-svg" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="4669" width="14" height="14">
                <path d="M317.9 80.6l531.7 354.5c25.7 17.2 41.2 46 41.2 76.9 0 30.9-15.4 59.8-41.2 76.9L317.9 943.4c-28.4 18.9-64.9 20.7-95 4.6-30.1-16.1-48.9-47.5-48.8-81.7V157.6c0-34.1 18.7-65.5 48.8-81.6 30.1-16.2 66.6-14.4 95 4.6z m-38.5 57.7c-7.1-4.7-16.2-5.2-23.7-1.1s-12.2 11.9-12.2 20.4v708.8c0 8.5 4.7 16.4 12.2 20.4 7.5 4 16.7 3.6 23.7-1.2l531.7-354.4c6.4-4.3 10.3-11.5 10.3-19.3 0-7.7-3.9-15-10.3-19.2L279.4 138.3zM440 454.2h-87.7c-4.7 0-8.9-2.8-10.7-7.1-1.8-4.3-0.8-9.3 2.5-12.6l95.9-95.9h57.8v346.9H440V454.2z m0 0" fill="currentColor" p-id="4670"></path>
              </svg>
            </el-icon>
            <el-icon @click="toTaskLogList(scope.row)" :color="'var(--el-color-primary)'" :size="14" :title="langData.detailTaskLog">
              <svg t="1761632063541" class="icon-svg" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="5500" width="14" height="14">
                <path d="M768 960H256A106.666667 106.666667 0 0 1 149.333333 853.333333V170.666667A106.666667 106.666667 0 0 1 256 64h512A106.666667 106.666667 0 0 1 874.666667 170.666667v682.666666a106.666667 106.666667 0 0 1-106.666667 106.666667zM256 106.666667c-35.413333 0-64 28.586667-64 64v682.666666c0 35.413333 28.586667 64 64 64h512c35.413333 0 64-28.586667 64-64V170.666667c0-35.413333-28.586667-64-64-64H256z" fill="currentColor" p-id="5501"></path>
                <path d="M725.333333 362.666667H298.666667c-11.946667 0-21.333333-9.386667-21.333334-21.333334s9.386667-21.333333 21.333334-21.333333h426.666666c11.946667 0 21.333333 9.386667 21.333334 21.333333s-9.386667 21.333333-21.333334 21.333334zM725.333333 533.333333H298.666667c-11.946667 0-21.333333-9.386667-21.333334-21.333333s9.386667-21.333333 21.333334-21.333333h426.666666c11.946667 0 21.333333 9.386667 21.333334 21.333333s-9.386667 21.333333-21.333334 21.333333zM725.333333 704H298.666667c-11.946667 0-21.333333-9.386667-21.333334-21.333333s9.386667-21.333333 21.333334-21.333334h426.666666c11.946667 0 21.333333 9.386667 21.333334 21.333334s-9.386667 21.333333-21.333334 21.333333z" fill="currentColor" p-id="5502"></path>
              </svg>
            </el-icon>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="id" :label="langData.taskId" header-align="center" align="center" width="60"/>
      <el-table-column prop="jobDesc" :label="langData.taskDesc" header-align="center" align="left" width="120"/>
      <el-table-column :label="langData.scheduleType" header-align="center" align="left" width="120">
        <template #default="{row}">
          {{ row.scheduleType }}: <code class="cell-sub">{{ row.scheduleConf }}</code>
        </template>
      </el-table-column>
      <el-table-column :label="langData.glueType" header-align="center" align="left" width="200">
        <template #default="{row}">
          {{ row.glueType }}: <code class="cell-sub">{{ row.executorHandler }}</code>
        </template>
      </el-table-column>
      <el-table-column :label="langData.executor" header-align="center" align="left" width="130">
        <template #default="{row}">
          <span>{{ row.groupTitle }}</span>
          <span class="cell-sub">{{ row.groupAddressList }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="fmtTriggerStatus" :label="langData.triggerStatus" header-align="center" align="center" width="90">
        <template #default="scope">
          <el-tag :type="scope.row.triggerStatus==0?'info':'success'" effect="plain" size="small">
            {{ scope.row.fmtTriggerStatus }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="fmtUpdateTime" :label="langData.tableHeaderUpdateTime" header-align="center" align="center" width="120"/>
      <el-table-column prop="author" :label="langData.tableHeaderCreator" header-align="center" align="center" width="90"/>
      <el-table-column prop="fmtTriggerLastTime" :label="langData.triggerLastTime" header-align="center" align="center"/>
      <el-table-column prop="fmtTriggerNextTime" :label="langData.triggerNextTime" header-align="center" align="center"/>
    </el-table>
    <el-pagination class="page" v-model:page-size="form.length" v-model:current-page="form.start"
                   layout="->, total, sizes, prev, pager, next, jumper" v-model:total="total"
                   @size-change="query()" @current-change="query()" @prev-click="query()" @next-click="query()"
                   size="small" :background="true"
                   :page-sizes="[5, 10, 20, 50, 100]"/>
  </div>

  <!-- 立即执行弹窗 -->
  <el-dialog v-model="dialogFormVisible" :title="langData.executeTask" draggable width="420px" class="exec-dialog">
    <el-form :model="immediateTaskForm" label-position="right" size="small" :inline="false" ref="formRef1"
             :rules="rules1" label-width="100px">
      <el-form-item :label="langData.taskId" prop="id">
        <el-input v-model="immediateTaskForm.id" type="text" disabled/>
      </el-form-item>
      <el-form-item :label="langData.executeParam" prop="executorParam">
        <el-input v-model="immediateTaskForm.executorParam" type="textarea" :rows="4"/>
      </el-form-item>
      <el-form-item :label="langData.executeAddress" prop="addressList">
        <el-input v-model="immediateTaskForm.addressList" type="textarea" :rows="4"/>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button size="small" @click="dialogFormVisible = false">{{ langData.btnCancel }}</el-button>
        <el-popconfirm :title="langData.confirmOpera" confirm-button-type="warning" @confirm="startTaskImmediate(formRef1)">
          <template #reference>
            <el-button type="primary" size="small">执行</el-button>
          </template>
        </el-popconfirm>
      </div>
    </template>
  </el-dialog>

  <!-- 新增/编辑任务 —— 全屏 -->
  <el-dialog
      v-model="drawer"
      :title="drawerTitle"
      fullscreen
      :close-on-click-modal="false"
      class="task-dialog"
  >
    <div class="form-container">
      <el-form :model="taskForm" size="small" label-position="right" inline-message :inline="false" ref="formRef"
               :rules="rules" label-width="110px">
        <!-- 核心配置区 — 三列 -->
        <div class="core-sections">
          <!-- 01 基础配置 -->
          <div class="section-card card-base">
            <div class="sc-accent"></div>
            <div class="sc-header">
              <span class="sc-num">01</span>
              <div class="sc-headline">
                <span class="sc-label">{{langData.baseConfig}}</span>
                <span class="sc-desc">选择执行器并填写任务基本信息</span>
              </div>
            </div>
            <div class="sc-body">
              <el-form-item :label="langData.executor" prop="jobGroup">
                <el-select v-model="taskForm.jobGroup" :placeholder="langData.formSelectPlaceholder" size="small" clearable
                           filterable :remote-method="fetchJobGroup">
                  <el-option :key="item.id" :label="item.title+'('+item.addressList+')'" :value="item.id" v-for="item in JobGroupList"/>
                </el-select>
              </el-form-item>
              <el-form-item :label="langData.taskDesc" prop="jobDesc">
                <el-input v-model="taskForm.jobDesc" type="text" clearable placeholder="输入任务描述…"/>
              </el-form-item>
              <el-form-item :label="langData.alarmEmail" prop="alarmEmail">
                <el-input v-model="taskForm.alarmEmail" type="textarea" clearable
                          :placeholder="langData.alarmEmailPlaceholder" :rows="3"/>
              </el-form-item>
            </div>
          </div>

          <!-- 02 调度配置 -->
          <div class="section-card card-schedule">
            <div class="sc-accent"></div>
            <div class="sc-header">
              <span class="sc-num">02</span>
              <div class="sc-headline">
                <span class="sc-label">{{langData.scheduleConfig}}</span>
                <span class="sc-desc">设置任务的触发周期与策略</span>
              </div>
            </div>
            <div class="sc-body">
              <el-form-item :label="langData.scheduleType" prop="scheduleType">
                <el-select v-model="taskForm.scheduleType" :placeholder="langData.formSelectPlaceholder" size="small" clearable filterable>
                  <el-option :key="item.key" :label="item.value" :value="item.key" v-for="item in scheduleTypeList"/>
                </el-select>
              </el-form-item>
              <el-form-item label="CRON" prop="scheduleConf" v-if="taskForm.scheduleType=='CRON'">
                <el-input v-model="taskForm.scheduleConf" type="text" clearable placeholder="* * * * * ?">
                  <template #prefix><code class="input-prefix">cron</code></template>
                </el-input>
              </el-form-item>
              <el-form-item :label="langData.fixedRate" prop="scheduleConf" v-if="taskForm.scheduleType=='FIX_RATE'">
                <el-input v-model="taskForm.scheduleConf" type="number" clearable placeholder="30">
                  <template #prefix><code class="input-prefix">秒</code></template>
                </el-input>
              </el-form-item>
            </div>
          </div>

          <!-- 03 任务配置 -->
          <div class="section-card card-task">
            <div class="sc-accent"></div>
            <div class="sc-header">
              <span class="sc-num">03</span>
              <div class="sc-headline">
                <span class="sc-label">{{langData.taskConfig}}</span>
                <span class="sc-desc">选择运行模式与处理器</span>
              </div>
            </div>
            <div class="sc-body">
              <el-form-item :label="langData.glueType" prop="glueType">
                <el-select v-model="taskForm.glueType" :placeholder="langData.formSelectPlaceholder" size="small" clearable filterable>
                  <el-option :key="item.key" :label="item.value" :value="item.key" v-for="item in glueTypeList"/>
                </el-select>
              </el-form-item>
              <el-form-item :label="langData.taskHandler" prop="executorHandler" v-if="taskForm.glueType=='BEAN'">
                <el-input v-model="taskForm.executorHandler" type="text" clearable :placeholder="langData.taskHandlerPlaceholder"/>
              </el-form-item>
              <el-form-item :label="langData.executeParam" prop="executorParam">
                <el-input v-model="taskForm.executorParam" type="textarea" clearable :rows="3" :placeholder="langData.taskParamsPlaceholder"/>
              </el-form-item>
            </div>
          </div>
        </div>

        <!-- 04 高级配置 -->
        <div class="section-card card-advanced">
          <div class="sc-accent"></div>
          <div class="sc-header">
            <span class="sc-num">04</span>
            <div class="sc-headline">
              <span class="sc-label">{{langData.advancedConfig}}</span>
              <span class="sc-desc">路由策略、超时控制与失败重试等高级参数</span>
            </div>
          </div>
          <div class="sc-body">
            <div class="sc-grid cols-3">
              <el-form-item :label="langData.routeStrategy" prop="executorRouteStrategy">
                <el-select v-model="taskForm.executorRouteStrategy" :placeholder="langData.formSelectPlaceholder" size="small" clearable filterable>
                  <el-option :key="item.key" :label="item.value" :value="item.key" v-for="item in executorRouteStrategyList"/>
                </el-select>
              </el-form-item>
              <el-form-item :label="langData.misfireStrategy" prop="misfireStrategy">
                <el-select v-model="taskForm.misfireStrategy" :placeholder="langData.formSelectPlaceholder" size="small" clearable filterable>
                  <el-option :key="item.key" :label="item.value" :value="item.key" v-for="item in misfireStrategyList"/>
                </el-select>
              </el-form-item>
              <el-form-item :label="langData.blockStrategy" prop="executorBlockStrategy">
                <el-select v-model="taskForm.executorBlockStrategy" :placeholder="langData.formSelectPlaceholder" size="small" clearable filterable>
                  <el-option :key="item.key" :label="item.value" :value="item.key" v-for="item in executorBlockStrategyList"/>
                </el-select>
              </el-form-item>
              <el-form-item :label="langData.childTaskId" prop="childJobId">
                <el-input v-model="taskForm.childJobId" type="text" clearable placeholder="子任务ID"/>
              </el-form-item>
              <el-form-item :label="langData.timeout" prop="executorTimeout">
                <el-input v-model="taskForm.executorTimeout" type="number" clearable placeholder="秒"/>
              </el-form-item>
              <el-form-item :label="langData.failRetryCount" prop="executorFailRetryCount">
                <el-input v-model="taskForm.executorFailRetryCount" type="number" clearable placeholder="次"/>
              </el-form-item>
            </div>
            <el-form-item label="" prop="glueRemark" style="display:none">
              <el-input v-model="taskForm.glueRemark" type="hidden" clearable/>
            </el-form-item>
          </div>
        </div>
      </el-form>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button size="small" @click="drawer = false">{{langData.btnCancel}}</el-button>
        <el-button type="primary" size="small" @click="updateTask(formRef)" :loading="submitting">{{langData.btnSave}}</el-button>
      </div>
    </template>
  </el-dialog>

  <!-- 批量修改执行器 -->
  <el-dialog v-model="batchExecutorDialogVisible" :title="langData.batchModifyExecutor" draggable width="380px">
    <el-form :model="batchExecutorForm" label-position="right" size="small" label-width="100px">
      <el-form-item :label="langData.executor" prop="jobGroup">
        <el-select v-model="batchExecutorForm.jobGroup" :placeholder="langData.formSelectPlaceholder"
                   size="small" clearable filterable remote automatic-dropdown :remote-method="fetchJobGroup">
          <el-option :key="item.id" :label="item.title+' ('+item.addressList+')'" :value="item.id" v-for="item in JobGroupList"/>
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button size="small" @click="batchExecutorDialogVisible = false">{{ langData.btnCancel }}</el-button>
      <el-button type="primary" size="small" @click="batchUpdateJobGroup">{{ langData.btnSave }}</el-button>
    </template>
  </el-dialog>

  <!-- 导入抽屉 -->
  <el-drawer v-model="importDialogVisible" :title="langData.importTaskTitle" direction="ltr" size="480px">
    <el-form :model="importForm" label-position="left" :inline="false" label-width="25%">
      <div>
        <el-upload action="#" name="file" ref="uploadRef" class="upload-demo" drag
            :multiple="false" :auto-upload="false" :limit="1"
            @change="fileChange" @remove="fileRemove" accept=".csv" style="height:230px">
          <el-icon class="el-icon--upload"><UploadFilled/></el-icon>
          <div class="el-upload__text">Drop file here or <em> click to upload </em></div>
          <template #tip>
            <div class="el-upload__tip">{{ langData.importFileTip }}</div>
          </template>
        </el-upload>
      </div>
      <br/>
      <el-form-item label-width="0%">
        <el-button type="primary" @click="fileImport()">{{ langData.btnSubmit }}</el-button>
      </el-form-item>
    </el-form>
  </el-drawer>
</template>

<style scoped>
/* ========================================
   Task Page - Layout
   ======================================== */
.task-page {
  flex-grow: 1;
  padding: 24px;
  overflow: auto;
  width: 100%;
  animation: fadeInUp 0.35s cubic-bezier(0.4, 0, 0.2, 1) both;
}

/* ========================================
   Search Card
   ======================================== */
.search-card {
  background: var(--surface-elevated);
  border: 1px solid var(--el-border-color);
  border-radius: var(--radius-md);
  padding: 16px 20px 4px 20px;
  margin-bottom: 16px;
  box-shadow: var(--shadow-sm);
  transition: box-shadow var(--transition-smooth);
}

.search-card:hover {
  box-shadow: var(--shadow-md);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
  padding-bottom: 10px;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.card-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  letter-spacing: -0.01em;
}

.card-badge {
  font-size: 11px;
  font-weight: 500;
  color: var(--el-text-color-secondary);
  font-family: var(--font-mono);
  background: var(--surface-inset);
  padding: 2px 10px;
  border-radius: 12px;
}

.search-form .el-form-item {
  margin-bottom: 12px;
}

.search-form .el-select,
.search-form .el-input {
  width: 160px;
}

/* ========================================
   Toolbar
   ======================================== */
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding: 8px 12px;
  background: var(--surface-elevated);
  border: 1px solid var(--el-border-color);
  border-radius: var(--radius-sm);
  box-shadow: var(--shadow-sm);
}

.batch-actions {
  display: flex;
  align-items: center;
  gap: 2px;
}

.selected-hint {
  font-size: 12px;
  color: var(--el-text-color-placeholder);
  padding: 4px 8px;
}

.tool-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 5px 10px;
  border-radius: var(--radius-sm);
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition-fast);
  user-select: none;
}

.tool-btn.start:hover { color: var(--el-color-success); background: rgba(5,150,105,0.08); }
.tool-btn.pause:hover { color: var(--el-color-warning); background: rgba(217,119,6,0.08); }
.tool-btn.delete:hover { color: var(--el-color-danger); background: rgba(220,38,38,0.08); }
.tool-btn.setting:hover { color: var(--el-color-primary); background: rgba(79,70,229,0.08); }
.tool-btn.ghost {
  color: var(--el-text-color-secondary);
  padding: 4px 10px;
}
.tool-btn.ghost:hover {
  color: var(--el-color-primary);
  background: var(--surface-hover);
}

.toolbar-right {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* ========================================
   Data Table
   ======================================== */
.data-table {
  border-radius: var(--radius-md) !important;
}

.data-table :deep(.cell) {
  white-space: pre-line !important;
}

.row-actions {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 6px 4px;
  justify-items: center;
  align-items: center;
}

.row-actions .el-icon {
  transition: all var(--transition-fast);
  display: flex;
  align-items: center;
  justify-content: center;
}

.row-actions .el-icon:hover {
  transform: scale(1.15);
}

.cell-mono {
  font-family: var(--font-mono);
  font-size: 12px;
  font-weight: 50;
  background: var(--surface-inset);
  padding: 2px 2px;
  border-radius: 4px;
  color: var(--el-text-color-regular);
}

.cell-sub {
  display: block;
  font-size: 11px;
  color: var(--el-text-color-placeholder);
  margin-top: 1px;
}

.icon-svg {
  vertical-align: middle;
}

/* ========================================
   Dialog / Drawer
   ======================================== */
.exec-dialog :deep(.el-dialog__body) {
  padding: 8px 24px 16px 24px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* ========================================
   Fullscreen Task Dialog
   ======================================== */
:deep(.task-dialog) {
  .el-dialog__header {
    padding: 20px 32px 0 32px;
  }
  .el-dialog__body {
    display: flex;
    flex-direction: column;
    flex: 1;
    overflow: hidden;
    padding: 20px 28px 12px 28px;
    background: var(--el-bg-color-page);
    /* 微妙网格背景 */
    background-image:
      linear-gradient(rgba(128,128,128,0.03) 1px, transparent 1px),
      linear-gradient(90deg, rgba(128,128,128,0.03) 1px, transparent 1px);
    background-size: 24px 24px;
  }
}

.form-container {
  display: flex;
  flex-direction: column;
  flex: 1;
  overflow: hidden;
}

.form-container .el-form {
  display: flex;
  flex-direction: column;
  flex: 1;
  overflow: hidden;
  gap: 16px;
}

/* ========================================
   Core Sections — 三列等宽
   ======================================== */
.core-sections {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  flex: 1;
  min-height: 0;
}

/* ========================================
   Section Card — 蓝图卡片
   ======================================== */
.section-card {
  position: relative;
  border-radius: var(--radius-md);
  background: var(--surface-elevated);
  border: 1px solid var(--el-border-color-lighter);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  transition: all var(--transition-smooth);
  box-shadow: var(--shadow-sm);
}

.section-card:hover {
  box-shadow: var(--shadow-md);
}

/* 左侧彩色强调线 */
.sc-accent {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 3px;
  border-radius: 3px 0 0 3px;
  transition: width var(--transition-smooth);
}

.card-base     .sc-accent { background: var(--el-color-primary); }
.card-schedule .sc-accent { background: var(--el-color-success); }
.card-task     .sc-accent { background: var(--el-color-warning); }
.card-advanced .sc-accent { background: var(--el-color-info); }

.section-card:hover .sc-accent { width: 4px; }

/* 卡片头部 */
.sc-header {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 14px 16px 12px 20px;
  border-bottom: 1px solid var(--el-border-color-lighter);
  flex-shrink: 0;
}

/* 大号编号 */
.sc-num {
  font-family: var(--font-mono);
  font-size: 26px;
  font-weight: 600;
  line-height: 1;
  flex-shrink: 0;
  opacity: 0.18;
  margin-top: -2px;
}

.card-base     .sc-num { color: var(--el-color-primary); }
.card-schedule .sc-num { color: var(--el-color-success); }
.card-task     .sc-num { color: var(--el-color-warning); }
.card-advanced .sc-num { color: var(--el-color-info); }

/* 标题文本组 */
.sc-headline {
  display: flex;
  flex-direction: column;
  gap: 1px;
}

.sc-label {
  font-size: 13px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  letter-spacing: 0.02em;
}

.sc-desc {
  font-size: 11px;
  color: var(--el-text-color-placeholder);
  font-weight: 400;
  letter-spacing: 0.01em;
}

/* 卡片内容区 */
.sc-body {
  padding: 14px 16px 10px 20px;
  flex: 1;
  overflow: auto;
}

.sc-body :deep(.el-form-item) {
  margin-bottom: 14px;
}

.sc-body :deep(.el-form-item__label) {
  width: 110px !important;
  font-size: 12.5px;
  font-weight: 500;
  color: var(--el-text-color-secondary);
  white-space: nowrap;
}

.sc-body :deep(.el-select) {
  width: 100%;
}

/* 高级配置网格 */
.sc-grid {
  display: grid;
  gap: 0 20px;
}

.sc-grid.cols-3 {
  grid-template-columns: repeat(3, 1fr);
}

.sc-grid :deep(.el-form-item) {
  margin-bottom: 14px;
}

.sc-grid :deep(.el-select) {
  width: 100%;
}

/* 输入框内联前缀 */
.input-prefix {
  font-family: var(--font-mono);
  font-size: 10px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: var(--el-text-color-placeholder);
  background: var(--surface-inset);
  padding: 1px 6px;
  border-radius: 3px;
}
</style>