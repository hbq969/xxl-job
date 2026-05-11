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
  <div class="container">
    <el-form :model="form" size="small" label-position="right" inline-message :inline="true">
      <el-form-item :label="langData.executor" prop="jobGroup" style="width: 180px">
        <el-select v-model="form.jobGroup" :placeholder="langData.formSelectPlaceholder" size="small" clearable
                   filterable style="width: 100%"
                   remote automatic-dropdown :remote-method="fetchJobGroup">
          <el-option :key="item.id" :label="item.title+' ('+item.addressList+')'" :value="item.id" v-for="item in JobGroupList"/>
        </el-select>
      </el-form-item>
      <el-form-item :label="langData.taskStatus" prop="triggerStatus" style="width: 180px">
        <el-select v-model="form.triggerStatus" :placeholder="langData.formSelectPlaceholder" size="small" clearable
                   filterable style="width: 100%"
                   automatic-dropdown>
          <el-option :key="item.key" :label="item.value" :value="item.key" v-for="item in triggerStatusList"/>
        </el-select>
      </el-form-item>
      <el-form-item :label="langData.taskDesc" prop="jobDesc" style="width: 180px">
        <el-input v-model="form.jobDesc" type="text" clearable/>
      </el-form-item>
      <el-form-item :label="langData.taskHandler" prop="executorHandler" style="width: 180px">
        <el-input v-model="form.executorHandler" type="text" clearable/>
      </el-form-item>
      <el-form-item :label="langData.tableHeaderCreator" prop="author" style="width: 180px">
        <el-input v-model="form.author" type="text" clearable/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" size="small" @click="query()">{{ langData.btnSearch }}</el-button>
        <el-button type="success" :icon="Plus" circle @click="showAddTaskDialog" :title="langData.btnAdd"/>
      </el-form-item>
    </el-form>

    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 5px">
      <div style="display: flex; align-items: center;">
        <template v-if="selectedCount">
          <el-popconfirm :title="langData.confirmOpera" @confirm="batchStartTasks" confirm-button-type="warning">
            <template #reference>
              <el-icon :color="'var(--el-color-success)'" style="cursor: pointer; margin-left: 5px" :size="18" :title="langData.batchStartTask"><VideoPlay/></el-icon>
            </template>
          </el-popconfirm>
          <el-popconfirm :title="langData.confirmOpera" @confirm="batchStopTasks" confirm-button-type="warning">
            <template #reference>
              <el-icon :color="'var(--el-color-warning)'" style="cursor: pointer; margin-left: 5px" :size="18" :title="langData.batchStopTask"><VideoPause/></el-icon>
            </template>
          </el-popconfirm>
          <el-popconfirm :title="langData.confirmDelete" @confirm="batchDeleteTasks" confirm-button-type="danger">
            <template #reference>
              <el-icon color="red" style="cursor: pointer; margin-left: 5px" :size="18" :title="langData.batchDeleteTask"><Delete/></el-icon>
            </template>
          </el-popconfirm>
        </template>
        <template v-else>
          <el-icon :color="'var(--el-text-color-placeholder)'" style="margin-left: 5px" :size="18" :title="langData.batchStartTask"><VideoPlay/></el-icon>
          <el-icon :color="'var(--el-text-color-placeholder)'" style="margin-left: 5px" :size="18" :title="langData.batchStopTask"><VideoPause/></el-icon>
          <el-icon :color="'var(--el-text-color-placeholder)'" style="margin-left: 5px" :size="18" :title="langData.batchDeleteTask"><Delete/></el-icon>
        </template>
        <el-icon v-if="selectedCount" :color="'var(--el-color-primary)'" style="cursor: pointer; margin-left: 5px" :size="18"
                 :title="langData.batchModifyExecutor" @click="showBatchModifyExecutorDialog"><Setting/></el-icon>
        <el-icon v-else :color="'var(--el-text-color-placeholder)'" style="margin-left: 5px" :size="18"
                 :title="langData.batchModifyExecutor"><Setting/></el-icon>
      </div>
      <div style="display: flex; align-items: center;">
        <el-tooltip :content="langData.importTask" effect="dark" placement="top">
          <el-icon :color="'var(--el-color-success)'" style="cursor: pointer; margin-left: 5px" :size="18" @click="showImportDialog"><UploadFilled/></el-icon>
        </el-tooltip>
        <el-tooltip :content="langData.exportTask" effect="dark" placement="top">
          <el-icon :color="'var(--el-color-primary)'" style="cursor: pointer; margin-left: 5px" :size="18" @click="exportData"><Printer/></el-icon>
        </el-tooltip>
        <el-tooltip :content="langData.downloadTemplate" effect="dark" placement="top">
          <el-icon :color="'var(--el-color-warning)'" style="cursor: pointer; margin-left: 5px" :size="18" @click="downloadTemplate"><Document/></el-icon>
        </el-tooltip>
      </div>
    </div>

    <el-table :data="list" style="width: 100%" table-layout="fixed" :stripe="true"
              size="small" :highlight-current-row="true" ref="multipleTableRef"
              @selection-change="handleSelectionChange">
      <el-table-column type="selection" header-align="center" align="center"/>
      <el-table-column fixed="left" :label="langData.tableHeaderOp" width="100" header-align="center" align="center">
        <template #default="scope">
          <el-icon @click="showEditTaskDialog(scope.row)" :color="'var(--el-color-primary)'" style="cursor: pointer; margin-left: 10px" :size="14"><Edit/></el-icon>
          <el-popconfirm :title="langData.confirmDelete" @confirm="deleteTask(scope.row)" icon-color="red" confirm-button-type="danger">
            <template #reference>
              <el-icon color="red" style="cursor: pointer; margin-left: 10px" :size="14"><Delete/></el-icon>
            </template>
          </el-popconfirm>
          <el-popconfirm :title="langData.confirmOpera" @confirm="startTask(scope.row)" icon-color="red" confirm-button-type="danger" v-if="scope.row.triggerStatus==0">
            <template #reference>
              <el-icon :color="'var(--el-color-primary)'" style="cursor: pointer; margin-left: 10px" :size="14" :title="langData.startTask"><VideoPlay/></el-icon>
            </template>
          </el-popconfirm>
          <el-popconfirm :title="langData.confirmOpera" @confirm="stopTask(scope.row)" icon-color="red" confirm-button-type="danger" v-if="scope.row.triggerStatus==1">
            <template #reference>
              <el-icon color="orange" style="cursor: pointer; margin-left: 10px" :size="14" :title="langData.stopTask"><VideoPause/></el-icon>
            </template>
          </el-popconfirm>
          <el-icon :color="'var(--el-color-primary)'" style="cursor: pointer; margin-left: 10px" :size="14" :title="langData.copyTask" @click="copyTask(scope.row)"><DocumentCopy/></el-icon>
          <el-icon :color="'var(--el-color-primary)'" style="cursor: pointer; margin-left: 10px" :size="14" :title="langData.runImmediate" @click="showImmediateTaskDialog(scope.row)">
            <template #default>
              <svg t="1761619792188" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="4669" width="32" height="32">
                <path
                    d="M317.9 80.6l531.7 354.5c25.7 17.2 41.2 46 41.2 76.9 0 30.9-15.4 59.8-41.2 76.9L317.9 943.4c-28.4 18.9-64.9 20.7-95 4.6-30.1-16.1-48.9-47.5-48.8-81.7V157.6c0-34.1 18.7-65.5 48.8-81.6 30.1-16.2 66.6-14.4 95 4.6z m-38.5 57.7c-7.1-4.7-16.2-5.2-23.7-1.1s-12.2 11.9-12.2 20.4v708.8c0 8.5 4.7 16.4 12.2 20.4 7.5 4 16.7 3.6 23.7-1.2l531.7-354.4c6.4-4.3 10.3-11.5 10.3-19.3 0-7.7-3.9-15-10.3-19.2L279.4 138.3zM440 454.2h-87.7c-4.7 0-8.9-2.8-10.7-7.1-1.8-4.3-0.8-9.3 2.5-12.6l95.9-95.9h57.8v346.9H440V454.2z m0 0"
                    fill="currentColor" p-id="4670"></path>
              </svg>
            </template>
          </el-icon>
          <el-icon @click="toTaskLogList(scope.row)" style="cursor: pointer; margin-left: 10px" :size="14" :title="langData.detailTaskLog">
            <template #default>
              <svg t="1761632063541" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="5500" width="32" height="32">
                <path
                    d="M768 960H256A106.666667 106.666667 0 0 1 149.333333 853.333333V170.666667A106.666667 106.666667 0 0 1 256 64h512A106.666667 106.666667 0 0 1 874.666667 170.666667v682.666666a106.666667 106.666667 0 0 1-106.666667 106.666667zM256 106.666667c-35.413333 0-64 28.586667-64 64v682.666666c0 35.413333 28.586667 64 64 64h512c35.413333 0 64-28.586667 64-64V170.666667c0-35.413333-28.586667-64-64-64H256z"
                    fill="currentColor" p-id="5501"></path>
                <path
                    d="M725.333333 362.666667H298.666667c-11.946667 0-21.333333-9.386667-21.333334-21.333334s9.386667-21.333333 21.333334-21.333333h426.666666c11.946667 0 21.333333 9.386667 21.333334 21.333333s-9.386667 21.333333-21.333334 21.333334zM725.333333 533.333333H298.666667c-11.946667 0-21.333333-9.386667-21.333334-21.333333s9.386667-21.333333 21.333334-21.333333h426.666666c11.946667 0 21.333333 9.386667 21.333334 21.333333s-9.386667 21.333333-21.333334 21.333333zM725.333333 704H298.666667c-11.946667 0-21.333333-9.386667-21.333334-21.333333s9.386667-21.333333 21.333334-21.333334h426.666666c11.946667 0 21.333333 9.386667 21.333334 21.333334s-9.386667 21.333333-21.333334 21.333333z"
                    fill="currentColor" p-id="5502"></path>
              </svg>
            </template>
          </el-icon>
        </template>
      </el-table-column>
      <el-table-column prop="id" :label="langData.taskId" :show-overflow-tooltip="true" header-align="center" align="center" width="80"/>
      <el-table-column prop="jobDesc" :label="langData.taskDesc" :show-overflow-tooltip="true" header-align="center" align="left" width="150"/>
      <el-table-column prop="scheduleType" :label="langData.scheduleType" :show-overflow-tooltip="true" header-align="center" align="left"
                       :formatter="(row: any, column: any, cellValue: any, index: number)=>row.scheduleType+': '+row.scheduleConf" width="150"/>
      <el-table-column prop="glueType" :label="langData.glueType" :show-overflow-tooltip="true" header-align="center" align="left"
                       :formatter="(row: any, column: any, cellValue: any, index: number)=>row.glueType+': '+row.executorHandler"/>
      <el-table-column prop="title" :label="langData.executor" :show-overflow-tooltip="true" header-align="center" align="left"
                       :formatter="(row: any, column: any, cellValue: any, index: number)=>row.groupTitle+' ('+row.groupAddressList+')'"/>
      <el-table-column prop="fmtTriggerStatus" :label="langData.triggerStatus" :show-overflow-tooltip="true" header-align="center" align="center" w="80">
        <template #default="scope">
          <el-tag :type="scope.row.triggerStatus==0?'info':'success'" effect="plain">
            {{ scope.row.fmtTriggerStatus }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="fmtUpdateTime" :label="langData.tableHeaderUpdateTime" :show-overflow-tooltip="true" header-align="center" align="center" width="120"/>
      <el-table-column prop="author" :label="langData.tableHeaderCreator" :show-overflow-tooltip="true" header-align="center" align="center" width="100"/>
      <el-table-column prop="fmtTriggerLastTime" :label="langData.triggerLastTime" :show-overflow-tooltip="true" header-align="center" align="center" width="120"/>
      <el-table-column prop="fmtTriggerNextTime" :label="langData.triggerNextTime" :show-overflow-tooltip="true" header-align="center" align="center" width="120"/>
    </el-table>
    <el-pagination class="page" v-model:page-size="form.length" v-model:current-page="form.start"
                   layout="->, total, sizes, prev, pager, next, jumper" v-model:total="total"
                   @size-change="query()"
                   @current-change="query()" @prev-click="query()" @next-click="query()"
                   size="small" :background="true"
                   :page-sizes="[5, 10, 20, 50, 100]"/>
  </div>

  <el-dialog v-model="dialogFormVisible" :title="langData.executeTask" draggable width="40%">
    <el-form :model="immediateTaskForm" label-position="right" size="small" :inline="false" ref="formRef1"
             :rules="rules1" label-width="20%">
      <el-form-item :label="langData.taskId" prop="id">
        <el-input v-model="immediateTaskForm.id" type="text" style="width: 100%" disabled/>
      </el-form-item>
      <el-form-item :label="langData.executeParam" prop="executorParam">
        <el-input v-model="immediateTaskForm.executorParam" type="textarea" :rows="5" style="width: 100%"/>
      </el-form-item>
      <el-form-item :label="langData.executeAddress" prop="addressList">
        <el-input v-model="immediateTaskForm.addressList" type="textarea" :rows="5" style="width: 100%"/>
      </el-form-item>
    </el-form>
    <template #footer>
              <span class="dialog-footer">
                <el-popconfirm :title="langData.confirmOpera" confirm-button-type="warning"
                               @confirm="startTaskImmediate(formRef1)">
                    <template #reference>
                      <el-button type="primary" size="small">执行</el-button>
                    </template>
                </el-popconfirm>
              </span>
    </template>
  </el-dialog>

  <el-dialog
      v-model="drawer"
      :title="drawerTitle"
      fullscreen
      :close-on-click-modal="false"
      class="task-dialog"
  >
    <div class="form-container">
      <el-form :model="taskForm" size="small" label-position="right" inline-message :inline="false" ref="formRef"
               :rules="rules" label-width="100px">
        <el-row :gutter="20" class="form-top-row">
          <el-col :span="8">
            <div class="form-section fill-height">
              <div class="section-title">
                <span class="section-dot base"></span>
                <span>{{langData.baseConfig}}</span>
              </div>
              <div class="section-body">
                <el-form-item :label="langData.executor" prop="jobGroup">
                  <el-select v-model="taskForm.jobGroup" :placeholder="langData.formSelectPlaceholder" size="small" clearable
                             filterable style="width: 100%" :remote-method="fetchJobGroup">
                    <el-option :key="item.id" :label="item.title+'('+item.addressList+')'" :value="item.id" v-for="item in JobGroupList"/>
                  </el-select>
                </el-form-item>
                <el-form-item :label="langData.taskDesc" prop="jobDesc">
                  <el-input v-model="taskForm.jobDesc" type="text" clearable/>
                </el-form-item>
                <el-form-item :label="langData.alarmEmail" prop="alarmEmail">
                  <el-input v-model="taskForm.alarmEmail" type="textarea" clearable
                            :placeholder="langData.alarmEmailPlaceholder" :rows="4"/>
                </el-form-item>
              </div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="form-section fill-height">
              <div class="section-title">
                <span class="section-dot schedule"></span>
                <span>{{langData.scheduleConfig}}</span>
              </div>
              <div class="section-body">
                <el-form-item :label="langData.scheduleType" prop="scheduleType">
                  <el-select v-model="taskForm.scheduleType" :placeholder="langData.formSelectPlaceholder" size="small" clearable
                             filterable style="width: 100%">
                    <el-option :key="item.key" :label="item.value" :value="item.key" v-for="item in scheduleTypeList"/>
                  </el-select>
                </el-form-item>
                <el-form-item label="CRON" prop="scheduleConf" v-if="taskForm.scheduleType=='CRON'">
                  <el-input v-model="taskForm.scheduleConf" type="text" clearable placeholder="* * * * * ?"/>
                </el-form-item>
                <el-form-item :label="langData.fixedRate" prop="scheduleConf" v-if="taskForm.scheduleType=='FIX_RATE'">
                  <el-input v-model="taskForm.scheduleConf" type="number" clearable placeholder="30"/>
                </el-form-item>
              </div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="form-section fill-height">
              <div class="section-title">
                <span class="section-dot task"></span>
                <span>{{langData.taskConfig}}</span>
              </div>
              <div class="section-body">
                <el-form-item :label="langData.glueType" prop="glueType">
                  <el-select v-model="taskForm.glueType" :placeholder="langData.formSelectPlaceholder" size="small" clearable
                             filterable style="width: 100%">
                    <el-option :key="item.key" :label="item.value" :value="item.key" v-for="item in glueTypeList"/>
                  </el-select>
                </el-form-item>
                <el-form-item :label="langData.taskHandler" prop="executorHandler" v-if="taskForm.glueType=='BEAN'">
                  <el-input v-model="taskForm.executorHandler" type="text" clearable
                            :placeholder="langData.taskHandlerPlaceholder"/>
                </el-form-item>
                <el-form-item :label="langData.executeParam" prop="executorParam">
                  <el-input v-model="taskForm.executorParam" type="textarea" clearable :rows="4"
                            :placeholder="langData.taskParamsPlaceholder"/>
                </el-form-item>
              </div>
            </div>
          </el-col>
        </el-row>
        <!-- 高级配置 -->
        <div class="form-section" style="margin-top:16px">
          <div class="section-title">
            <span class="section-dot advanced"></span>
            <span>{{langData.advancedConfig}}</span>
          </div>
          <div class="section-body">
            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item :label="langData.routeStrategy" prop="executorRouteStrategy">
                  <el-select v-model="taskForm.executorRouteStrategy" :placeholder="langData.formSelectPlaceholder" size="small"
                             clearable filterable style="width: 100%" automatic-dropdown>
                    <el-option :key="item.key" :label="item.value" :value="item.key" v-for="item in executorRouteStrategyList"/>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item :label="langData.misfireStrategy" prop="misfireStrategy">
                  <el-select v-model="taskForm.misfireStrategy" :placeholder="langData.formSelectPlaceholder" size="small"
                             clearable filterable style="width: 100%" automatic-dropdown>
                    <el-option :key="item.key" :label="item.value" :value="item.key" v-for="item in misfireStrategyList"/>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item :label="langData.blockStrategy" prop="executorBlockStrategy">
                  <el-select v-model="taskForm.executorBlockStrategy" :placeholder="langData.formSelectPlaceholder" size="small"
                             clearable filterable style="width: 100%" automatic-dropdown>
                    <el-option :key="item.key" :label="item.value" :value="item.key" v-for="item in executorBlockStrategyList"/>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item :label="langData.childTaskId" prop="childJobId">
                  <el-input v-model="taskForm.childJobId" type="text" clearable/>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item :label="langData.timeout" prop="executorTimeout">
                  <el-input v-model="taskForm.executorTimeout" type="number" clearable/>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item :label="langData.failRetryCount" prop="executorFailRetryCount">
                  <el-input v-model="taskForm.executorFailRetryCount" type="number" clearable/>
                </el-form-item>
              </el-col>
            </el-row>
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

  <el-dialog v-model="batchExecutorDialogVisible" :title="langData.batchModifyExecutor" draggable width="30%">
    <el-form :model="batchExecutorForm" label-position="right" size="small" label-width="20%">
      <el-form-item :label="langData.executor" prop="jobGroup">
        <el-select v-model="batchExecutorForm.jobGroup" :placeholder="langData.formSelectPlaceholder"
                   size="small" clearable filterable style="width: 100%"
                   remote automatic-dropdown :remote-method="fetchJobGroup">
          <el-option :key="item.id" :label="item.title+' ('+item.addressList+')'" :value="item.id" v-for="item in JobGroupList"/>
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button type="primary" size="small" @click="batchUpdateJobGroup">{{ langData.btnSave }}</el-button>
    </template>
  </el-dialog>

  <el-drawer v-model="importDialogVisible" :title="langData.importTaskTitle" direction="ltr" size="50%">
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
.container {
  flex-grow: 1;
  padding: 20px 2%;
  overflow: auto;
  width: 96%;
}

:deep(.el-table) {
  .cell {
    white-space: pre-line !important;
  }
}

:deep(.el-table) {
  .warning-row {
    --el-table-tr-bg-color: var(--el-color-danger-light-9) !important;
  }

  .success-row {
    --el-table-tr-bg-color: var(--el-color-success-light-9) !important;
  }
}

/* 全屏 Dialog */
:deep(.task-dialog) {
  .el-dialog__body {
    display: flex;
    flex-direction: column;
    flex: 1;
    overflow: hidden;
    padding: 16px 24px 8px 24px;
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
}

.form-top-row {
  flex: 1;
  min-height: 0;
}

.form-top-row .el-col {
  display: flex;
}

/* 任务表单 - 卡片分区 */
.form-section {
  border-radius: 6px;
  border: 1px solid var(--el-border-color);
  overflow: hidden;
  transition: border-color 0.2s;
  display: flex;
  flex-direction: column;
  width: 100%;
}

.form-section:hover {
  border-color: var(--el-border-color-light);
}

.fill-height {
  flex: 1;
}

.fill-height .section-body {
  flex: 1;
  overflow: auto;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 14px;
  background: var(--el-fill-color-light);
  border-bottom: 1px solid var(--el-border-color);
  font-size: 13px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  flex-shrink: 0;
}

.section-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  flex-shrink: 0;
}

.section-dot.base {
  background: var(--el-color-primary);
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.12);
}

.section-dot.schedule {
  background: var(--el-color-success);
  box-shadow: 0 0 0 3px rgba(103, 194, 58, 0.12);
}

.section-dot.task {
  background: var(--el-color-warning);
  box-shadow: 0 0 0 3px rgba(230, 162, 60, 0.12);
}

.section-dot.advanced {
  background: var(--el-color-info);
  box-shadow: 0 0 0 3px rgba(144, 147, 153, 0.12);
}

.section-body {
  padding: 8px 14px 2px 14px;
}

.section-body :deep(.el-form-item) {
  margin-bottom: 10px;
}

.section-body :deep(.el-form-item__label) {
  width: 120px !important;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>