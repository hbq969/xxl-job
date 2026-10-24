<script lang="ts" setup>
import {Icon} from '@iconify/vue'
import PageHead from '@/components/admin/PageHead.vue'
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
const filtersVisible = ref(true)
const filterCount = () => {
  let n = 0
  if (form.jobGroup) n++
  if (form.triggerStatus !== null && form.triggerStatus !== undefined && form.triggerStatus !== '') n++
  if (form.jobDesc) n++
  if (form.executorHandler) n++
  if (form.author) n++
  return n
}
const resetQuery = () => {
  form.jobGroup = null
  form.triggerStatus = null
  form.jobDesc = ''
  form.executorHandler = ''
  form.author = ''
  form.start = 1
  query()
}

const importDrawerFull = ref(false)
const importDrawerSize = computed(() => (importDrawerFull.value ? '100%' : '480px'))

const confirmBatch = (action: () => void, message: string) => {
  if (!selectedCount.value) return
  ElMessageBox.confirm(message, langData.notifyTitle || '', {
    confirmButtonText: langData.btnConfirm,
    cancelButtonText: langData.btnCancel,
    type: 'warning'
  }).then(() => action()).catch(() => {})
}

const selectedCount = ref(0)
const clearSelection = () => {
  multipleTableRef.value?.clearSelection()
  selectedCount.value = 0
}

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

const scrollToSection = (id: string) => {
  const el = document.getElementById(id)
  if (el) el.scrollIntoView({behavior: 'smooth', block: 'start'})
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
  <div class="admin-page">
    <PageHead :title="langData.taskPageTitle" :desc="langData.taskPageDesc">
      <template #actions>
        <span class="admin-muted">{{ total }} {{ langData.totalUnit }}</span>
      </template>
    </PageHead>

    <section class="admin-panel">
      <!-- 查询区 -->
      <div class="admin-filter" v-show="filtersVisible">
        <el-form :model="form" inline-message :inline="true" label-position="right" @submit.prevent>
          <el-form-item :label="langData.executor" prop="jobGroup">
            <el-select v-model="form.jobGroup" :placeholder="langData.formSelectPlaceholder" clearable
                       filterable remote automatic-dropdown :remote-method="fetchJobGroup">
              <el-option :key="item.id" :label="item.title+' ('+item.addressList+')'" :value="item.id" v-for="item in JobGroupList"/>
            </el-select>
          </el-form-item>
          <el-form-item :label="langData.taskStatus" prop="triggerStatus">
            <el-select v-model="form.triggerStatus" :placeholder="langData.formSelectPlaceholder" clearable
                       filterable automatic-dropdown class="admin-filter__select">
              <el-option :key="item.key" :label="item.value" :value="item.key" v-for="item in triggerStatusList"/>
            </el-select>
          </el-form-item>
          <el-form-item :label="langData.taskDesc" prop="jobDesc">
            <el-input v-model="form.jobDesc" type="text" clearable :placeholder="langData.formInputPlaceholder"/>
          </el-form-item>
          <el-form-item :label="langData.taskHandler" prop="executorHandler">
            <el-input v-model="form.executorHandler" type="text" clearable :placeholder="langData.formInputPlaceholder"/>
          </el-form-item>
          <el-form-item :label="langData.tableHeaderCreator" prop="author">
            <el-input v-model="form.author" type="text" clearable :placeholder="langData.formInputPlaceholder"/>
          </el-form-item>
        </el-form>
        <div class="admin-filter__actions">
          <el-button @click="resetQuery">
            <Icon icon="lucide:rotate-ccw" class="ic"/>{{ langData.btnReset }}
          </el-button>
          <el-button type="primary" @click="query()">
            <Icon icon="lucide:search" class="ic"/>{{ langData.btnSearch }}
          </el-button>
        </div>
      </div>

      <!-- 工具栏 -->
      <div class="admin-toolbar">
        <div class="tb-left">
          <el-tooltip :show-after="1000" :content="langData.btnAdd" placement="bottom">
            <el-button type="primary" circle :aria-label="langData.btnAdd" @click="showAddTaskDialog">
              <Icon icon="lucide:plus"/>
            </el-button>
          </el-tooltip>
          <span class="admin-toolbar__divider"></span>
          <el-tooltip :show-after="1000" :content="filtersVisible ? langData.filterHide : langData.filterShow" placement="bottom">
            <el-button class="admin-toolbar__icon" :class="{ 'is-active': filtersVisible }" circle text
                       :aria-label="langData.filterHide" @click="filtersVisible = !filtersVisible">
              <Icon icon="lucide:filter"/>
            </el-button>
          </el-tooltip>
          <span class="admin-toolbar__divider"></span>
          <el-tooltip :show-after="1000" :content="langData.importTask" placement="bottom">
            <el-button class="admin-toolbar__icon" circle text :aria-label="langData.importTask" @click="showImportDialog">
              <Icon icon="lucide:upload"/>
            </el-button>
          </el-tooltip>
          <el-tooltip :show-after="1000" :content="langData.exportTask" placement="bottom">
            <el-button class="admin-toolbar__icon" circle text :aria-label="langData.exportTask" @click="exportData">
              <Icon icon="lucide:download"/>
            </el-button>
          </el-tooltip>
          <el-tooltip :show-after="1000" :content="langData.downloadTemplate" placement="bottom">
            <el-button class="admin-toolbar__icon" circle text :aria-label="langData.downloadTemplate" @click="downloadTemplate">
              <Icon icon="lucide:file-down"/>
            </el-button>
          </el-tooltip>
          <span class="admin-toolbar__divider"></span>
          <el-tooltip :show-after="1000" :content="langData.startTask" placement="bottom">
            <el-button class="admin-toolbar__icon" circle text :disabled="!selectedCount" :aria-label="langData.startTask"
                       @click="confirmBatch(batchStartTasks, langData.confirmOpera)">
              <Icon icon="lucide:play"/>
            </el-button>
          </el-tooltip>
          <el-tooltip :show-after="1000" :content="langData.stopTask" placement="bottom">
            <el-button class="admin-toolbar__icon" circle text :disabled="!selectedCount" :aria-label="langData.stopTask"
                       @click="confirmBatch(batchStopTasks, langData.confirmOpera)">
              <Icon icon="lucide:pause"/>
            </el-button>
          </el-tooltip>
          <el-tooltip :show-after="1000" :content="langData.batchModifyExecutor" placement="bottom">
            <el-button class="admin-toolbar__icon" circle text :disabled="!selectedCount" :aria-label="langData.batchModifyExecutor"
                       @click="showBatchModifyExecutorDialog">
              <Icon icon="lucide:cpu"/>
            </el-button>
          </el-tooltip>
          <el-tooltip :show-after="1000" :content="langData.btnDelete" placement="bottom">
            <el-button class="admin-toolbar__icon" circle text type="danger" :disabled="!selectedCount" :aria-label="langData.btnDelete"
                       @click="confirmBatch(batchDeleteTasks, langData.confirmDelete)">
              <Icon icon="lucide:trash-2"/>
            </el-button>
          </el-tooltip>
        </div>
        <div class="tb-right">
          <el-tooltip :show-after="1000" :content="langData.btnRefresh" placement="bottom">
            <el-button class="admin-toolbar__icon" circle text :aria-label="langData.btnRefresh" @click="query()">
              <Icon icon="lucide:refresh-ccw"/>
            </el-button>
          </el-tooltip>
        </div>
      </div>

      <!-- 摘要栏：筛选状态 + 选择摘要 -->
      <div class="admin-sumbar" v-if="(!filtersVisible && filterCount() > 0) || selectedCount > 0">
        <span class="admin-sumbar__seg" v-if="!filtersVisible && filterCount() > 0">
          <Icon icon="lucide:filter" class="ic"/>
          {{ langData.filteredPrefix }} {{ filterCount() }} {{ langData.filteredSuffix }}
          <span class="admin-sumbar__lnk" @click="filtersVisible = true">{{ langData.filterExpand }}</span>
        </span>
        <span class="admin-sumbar__vsep" v-if="!filtersVisible && filterCount() > 0 && selectedCount > 0"></span>
        <span class="admin-sumbar__seg" v-if="selectedCount > 0">
          <Icon icon="lucide:check-square" class="ic"/>
          {{ langData.selectedPrefix }} {{ selectedCount }} {{ langData.selectedSuffix }}
          <span class="admin-sumbar__lnk" @click="clearSelection">{{ langData.selectedClear }}</span>
        </span>
      </div>

      <!-- 表格 -->
      <div class="admin-table">
        <el-table :data="list" table-layout="fixed" highlight-current-row ref="multipleTableRef"
                  @selection-change="handleSelectionChange" class="data-table">
          <el-table-column type="selection" fixed="left" header-align="center" align="center" width="44"/>
          <el-table-column fixed="left" :label="langData.tableHeaderOp" width="220" header-align="center" align="center">
            <template #default="scope">
              <div class="row-actions">
                <el-tooltip :show-after="1000" :content="langData.btnEdit" placement="top">
                  <el-button link type="primary" class="admin-op" @click="showEditTaskDialog(scope.row)">
                    <Icon icon="lucide:pencil"/>
                  </el-button>
                </el-tooltip>
                <el-tooltip :show-after="1000" :content="langData.runImmediate" placement="top">
                  <el-button link type="primary" class="admin-op" @click="showImmediateTaskDialog(scope.row)">
                    <Icon icon="lucide:zap"/>
                  </el-button>
                </el-tooltip>
                <el-popconfirm :title="langData.confirmOpera" confirm-button-type="warning" v-if="scope.row.triggerStatus==0"
                               @confirm="startTask(scope.row)">
                  <template #reference>
                    <el-tooltip :show-after="1000" :content="langData.startTask" placement="top">
                      <el-button link type="success" class="admin-op" :aria-label="langData.startTask">
                        <Icon icon="lucide:play"/>
                      </el-button>
                    </el-tooltip>
                  </template>
                </el-popconfirm>
                <el-popconfirm :title="langData.confirmOpera" confirm-button-type="warning" v-if="scope.row.triggerStatus==1"
                               @confirm="stopTask(scope.row)">
                  <template #reference>
                    <el-tooltip :show-after="1000" :content="langData.stopTask" placement="top">
                      <el-button link type="warning" class="admin-op" :aria-label="langData.stopTask">
                        <Icon icon="lucide:pause"/>
                      </el-button>
                    </el-tooltip>
                  </template>
                </el-popconfirm>
                <el-tooltip :show-after="1000" :content="langData.copyTask" placement="top">
                  <el-button link class="admin-op admin-op--muted" @click="copyTask(scope.row)">
                    <Icon icon="lucide:copy"/>
                  </el-button>
                </el-tooltip>
                <el-tooltip :show-after="1000" :content="langData.detailTaskLog" placement="top">
                  <el-button link class="admin-op admin-op--muted" @click="toTaskLogList(scope.row)">
                    <Icon icon="lucide:scroll-text"/>
                  </el-button>
                </el-tooltip>
                <el-popconfirm :title="langData.confirmDelete" @confirm="deleteTask(scope.row)" confirm-button-type="danger">
                  <template #reference>
                    <el-tooltip :show-after="1000" :content="langData.btnDelete" placement="top">
                      <el-button link type="danger" class="admin-op" :aria-label="langData.btnDelete">
                        <Icon icon="lucide:trash-2"/>
                      </el-button>
                    </el-tooltip>
                  </template>
                </el-popconfirm>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="id" :label="langData.taskId" header-align="center" align="center" width="70"/>
          <el-table-column prop="jobDesc" :label="langData.taskDesc" header-align="left" align="left" min-width="150" show-overflow-tooltip/>
          <el-table-column :label="langData.scheduleType" header-align="left" align="left" min-width="170">
            <template #default="{row}">
              <span>{{ row.scheduleType }}</span>
              <span class="cell-sub">{{ row.scheduleConf }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="langData.glueType" header-align="left" align="left" min-width="190">
            <template #default="{row}">
              <span>{{ row.glueType }}</span>
              <span class="cell-sub">{{ row.executorHandler }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="langData.executor" header-align="left" align="left" min-width="160">
            <template #default="{row}">
              <span>{{ row.groupTitle }}</span>
              <span class="cell-sub">{{ row.groupAddressList }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="fmtTriggerStatus" :label="langData.triggerStatus" header-align="center" align="center" width="110">
            <template #default="scope">
              <el-tag :type="scope.row.triggerStatus==0?'info':'success'" effect="light" round>
                {{ scope.row.fmtTriggerStatus }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="fmtUpdateTime" :label="langData.tableHeaderUpdateTime" header-align="center" align="center" width="180"/>
          <el-table-column prop="author" :label="langData.tableHeaderCreator" header-align="center" align="center" width="110"/>
          <el-table-column prop="fmtTriggerLastTime" :label="langData.triggerLastTime" header-align="center" align="center" width="180"/>
          <el-table-column prop="fmtTriggerNextTime" :label="langData.triggerNextTime" header-align="center" align="center" width="180"/>
        </el-table>
      </div>

      <!-- 分页 -->
      <div class="admin-foot">
        <el-pagination v-model:page-size="form.length" v-model:current-page="form.start"
                       layout="total, sizes, prev, pager, next, jumper" v-model:total="total"
                       @size-change="query()" @current-change="query()" @prev-click="query()" @next-click="query()"
                       :background="true" :page-sizes="[10, 20, 50, 100]"/>
      </div>
    </section>

    <!-- 立即执行弹窗 -->
    <el-dialog v-model="dialogFormVisible" :title="langData.executeTask" draggable width="460px">
      <el-form :model="immediateTaskForm" label-position="top" :inline="false" ref="formRef1" :rules="rules1">
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
          <el-button @click="dialogFormVisible = false">{{ langData.btnCancel }}</el-button>
          <el-button type="primary" @click="startTaskImmediate(formRef1)">{{ langData.btnConfirm }}</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 新增/编辑任务：左侧步骤导航 + 右侧表单 -->
    <el-dialog v-model="drawer" fullscreen :close-on-click-modal="false" :show-close="false" class="task-dialog">
      <div class="te">
        <!-- 左：导航 -->
        <aside class="te-nav">
          <div class="te-nav__head">
            <span class="te-nav__title">{{ drawerTitle }}</span>
            <span class="te-nav__sub">{{ langData.taskPageDesc }}</span>
          </div>
          <ul class="te-nav__list">
            <li class="te-nav__item is-active" @click="scrollToSection('te-sec-1')">
              <span class="te-nav__num">01</span>
              <span class="te-nav__label">
                <span class="te-nav__name">{{ langData.baseConfig }}</span>
                <span class="te-nav__desc">执行器与任务基本信息</span>
              </span>
            </li>
            <li class="te-nav__item" @click="scrollToSection('te-sec-2')">
              <span class="te-nav__num">02</span>
              <span class="te-nav__label">
                <span class="te-nav__name">{{ langData.scheduleConfig }}</span>
                <span class="te-nav__desc">触发周期与调度类型</span>
              </span>
            </li>
            <li class="te-nav__item" @click="scrollToSection('te-sec-3')">
              <span class="te-nav__num">03</span>
              <span class="te-nav__label">
                <span class="te-nav__name">{{ langData.taskConfig }}</span>
                <span class="te-nav__desc">运行模式与处理器</span>
              </span>
            </li>
            <li class="te-nav__item" @click="scrollToSection('te-sec-4')">
              <span class="te-nav__num">04</span>
              <span class="te-nav__label">
                <span class="te-nav__name">{{ langData.advancedConfig }}</span>
                <span class="te-nav__desc">路由、超时与重试策略</span>
              </span>
            </li>
          </ul>
          <div class="te-nav__foot">
            <span class="te-chip" v-if="taskForm.id">ID {{ taskForm.id }}</span>
            <span class="te-chip te-chip--primary" v-if="taskForm.scheduleType">{{ taskForm.scheduleType }}</span>
          </div>
        </aside>

        <!-- 右：表单 -->
        <div class="te-main">
          <header class="te-head">
            <span class="te-head__title">{{ drawerTitle }}</span>
            <span class="te-head__hint">{{ langData.taskPageDesc }}</span>
            <div class="te-head__right">
              <el-tooltip :show-after="1000" :content="langData.btnCancel" placement="bottom">
                <el-button link class="te-head__btn" :aria-label="langData.btnCancel" @click="drawer = false">
                  <Icon icon="lucide:x"/>
                </el-button>
              </el-tooltip>
            </div>
          </header>

          <div class="te-body">
            <el-form :model="taskForm" label-position="top" :inline="false" ref="formRef" :rules="rules">
              <section class="te-sec" id="te-sec-1">
                <div class="te-sec__head">
                  <span class="te-sec__num">01</span>
                  <span class="te-sec__title">{{ langData.baseConfig }}</span>
                  <span class="te-sec__desc">选择执行器并填写任务基本信息</span>
                </div>
                <div class="te-grid">
                  <el-form-item :label="langData.executor" prop="jobGroup">
                    <el-select v-model="taskForm.jobGroup" :placeholder="langData.formSelectPlaceholder" clearable
                               filterable :remote-method="fetchJobGroup">
                      <el-option :key="item.id" :label="item.title+'('+item.addressList+')'" :value="item.id" v-for="item in JobGroupList"/>
                    </el-select>
                  </el-form-item>
                  <el-form-item :label="langData.taskDesc" prop="jobDesc">
                    <el-input v-model="taskForm.jobDesc" type="text" clearable :placeholder="langData.taskDescPlaceholder"/>
                  </el-form-item>
                  <el-form-item :label="langData.alarmEmail" prop="alarmEmail" class="te-span-1">
                    <el-input v-model="taskForm.alarmEmail" type="textarea" clearable
                              :placeholder="langData.alarmEmailPlaceholder" :rows="3"/>
                  </el-form-item>
                </div>
              </section>

              <section class="te-sec" id="te-sec-2">
                <div class="te-sec__head">
                  <span class="te-sec__num">02</span>
                  <span class="te-sec__title">{{ langData.scheduleConfig }}</span>
                  <span class="te-sec__desc">设置任务的触发周期与策略</span>
                </div>
                <div class="te-grid">
                  <el-form-item :label="langData.scheduleType" prop="scheduleType">
                    <el-select v-model="taskForm.scheduleType" :placeholder="langData.formSelectPlaceholder" clearable filterable>
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
              </section>

              <section class="te-sec" id="te-sec-3">
                <div class="te-sec__head">
                  <span class="te-sec__num">03</span>
                  <span class="te-sec__title">{{ langData.taskConfig }}</span>
                  <span class="te-sec__desc">选择运行模式与处理器</span>
                </div>
                <div class="te-grid">
                  <el-form-item :label="langData.glueType" prop="glueType">
                    <el-select v-model="taskForm.glueType" :placeholder="langData.formSelectPlaceholder" clearable filterable>
                      <el-option :key="item.key" :label="item.value" :value="item.key" v-for="item in glueTypeList"/>
                    </el-select>
                  </el-form-item>
                  <el-form-item :label="langData.taskHandler" prop="executorHandler" v-if="taskForm.glueType=='BEAN'">
                    <el-input v-model="taskForm.executorHandler" type="text" clearable :placeholder="langData.taskHandlerPlaceholder"/>
                  </el-form-item>
                  <el-form-item :label="langData.executeParam" prop="executorParam" class="te-span-all">
                    <el-input v-model="taskForm.executorParam" type="textarea" clearable :rows="3" :placeholder="langData.taskParamsPlaceholder"/>
                  </el-form-item>
                </div>
              </section>

              <section class="te-sec" id="te-sec-4">
                <div class="te-sec__head">
                  <span class="te-sec__num">04</span>
                  <span class="te-sec__title">{{ langData.advancedConfig }}</span>
                  <span class="te-sec__desc">路由策略、超时控制与失败重试等高级参数</span>
                </div>
                <div class="te-grid">
                  <el-form-item :label="langData.routeStrategy" prop="executorRouteStrategy">
                    <el-select v-model="taskForm.executorRouteStrategy" :placeholder="langData.formSelectPlaceholder" clearable filterable>
                      <el-option :key="item.key" :label="item.value" :value="item.key" v-for="item in executorRouteStrategyList"/>
                    </el-select>
                  </el-form-item>
                  <el-form-item :label="langData.misfireStrategy" prop="misfireStrategy">
                    <el-select v-model="taskForm.misfireStrategy" :placeholder="langData.formSelectPlaceholder" clearable filterable>
                      <el-option :key="item.key" :label="item.value" :value="item.key" v-for="item in misfireStrategyList"/>
                    </el-select>
                  </el-form-item>
                  <el-form-item :label="langData.blockStrategy" prop="executorBlockStrategy">
                    <el-select v-model="taskForm.executorBlockStrategy" :placeholder="langData.formSelectPlaceholder" clearable filterable>
                      <el-option :key="item.key" :label="item.value" :value="item.key" v-for="item in executorBlockStrategyList"/>
                    </el-select>
                  </el-form-item>
                  <el-form-item :label="langData.childTaskId" prop="childJobId">
                    <el-input v-model="taskForm.childJobId" type="text" clearable :placeholder="langData.childTaskIdPlaceholder"/>
                  </el-form-item>
                  <el-form-item :label="langData.timeout" prop="executorTimeout">
                    <el-input v-model="taskForm.executorTimeout" type="number" clearable placeholder="5"/>
                  </el-form-item>
                  <el-form-item :label="langData.failRetryCount" prop="executorFailRetryCount">
                    <el-input v-model="taskForm.executorFailRetryCount" type="number" clearable placeholder="0"/>
                  </el-form-item>
                  <el-form-item label="" prop="glueRemark" style="display:none">
                    <el-input v-model="taskForm.glueRemark" type="hidden" clearable/>
                  </el-form-item>
                </div>
              </section>
            </el-form>
          </div>

          <footer class="te-foot">
            <span class="te-foot__hint">带 * 为必填项</span>
            <div class="te-foot__actions">
              <el-button @click="drawer = false">{{ langData.btnCancel }}</el-button>
              <el-button type="primary" :loading="submitting" @click="updateTask(formRef)">{{ langData.btnSave }}</el-button>
            </div>
          </footer>
        </div>
      </div>
    </el-dialog>

    <!-- 批量修改执行器 -->
    <el-dialog v-model="batchExecutorDialogVisible" :title="langData.batchModifyExecutor" draggable width="440px">
      <el-form :model="batchExecutorForm" label-position="top">
        <el-form-item :label="langData.executor" prop="jobGroup">
          <el-select v-model="batchExecutorForm.jobGroup" :placeholder="langData.formSelectPlaceholder" clearable filterable remote automatic-dropdown :remote-method="fetchJobGroup" class="full-width">
            <el-option :key="item.id" :label="item.title+' ('+item.addressList+')'" :value="item.id" v-for="item in JobGroupList"/>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="batchExecutorDialogVisible = false">{{ langData.btnCancel }}</el-button>
        <el-button type="primary" @click="batchUpdateJobGroup">{{ langData.btnSave }}</el-button>
      </template>
    </el-dialog>

    <!-- 导入抽屉 -->
    <el-drawer v-model="importDialogVisible" :size="importDrawerSize" direction="ltr">
      <template #header>
        <div class="drawer-head">
          <span class="drawer-head__title">{{ langData.importTaskTitle }}</span>
          <el-tooltip :show-after="1000" :content="importDrawerFull ? langData.drawerRestore : langData.drawerMaximize" placement="bottom">
            <el-button link class="drawer-head__btn" :aria-label="langData.drawerMaximize" @click="importDrawerFull = !importDrawerFull">
              <Icon :icon="importDrawerFull ? 'lucide:minimize-2' : 'lucide:maximize-2'"/>
            </el-button>
          </el-tooltip>
        </div>
      </template>
      <el-form :model="importForm" label-position="top" :inline="false">
        <el-upload action="#" name="file" ref="uploadRef" class="upload-demo" drag
                   :multiple="false" :auto-upload="false" :limit="1"
                   @change="fileChange" @remove="fileRemove" accept=".csv">
          <Icon icon="lucide:upload-cloud" class="upload-icon"/>
          <div class="el-upload__text">拖拽文件到此处，或 <em>点击上传</em></div>
          <template #tip>
            <div class="el-upload__tip">{{ langData.importFileTip }}</div>
          </template>
        </el-upload>
        <div class="admin-form-actions">
          <el-button @click="importDialogVisible = false">{{ langData.btnCancel }}</el-button>
          <el-button type="primary" @click="fileImport()">{{ langData.btnSubmit }}</el-button>
        </div>
      </el-form>
    </el-drawer>
  </div>
</template>

<style scoped>
.data-table :deep(.cell) { white-space: nowrap; }

.row-actions {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0;
}

.row-actions :deep(.el-button) {
  padding: 2px 3px;
  margin: 0;
}

.cell-sub {
  display: block;
  font-family: var(--font-mono);
  font-size: var(--el-font-size-extra-small);
  color: var(--el-text-color-secondary);
}

.drawer-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.drawer-head__title {
  font-size: var(--el-font-size-medium);
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.drawer-head__btn { font-size: var(--el-font-size-base); }

.full-width { width: 100%; }

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.upload-icon {
  font-size: 2.4em;
  color: var(--el-text-color-placeholder);
}

/* ============================================================
   任务编辑器：左侧步骤导航 + 右侧表单
   ============================================================ */
.te {
  display: grid;
  grid-template-columns: 248px minmax(0, 1fr);
  height: 100%;
  min-height: 0;
  background: var(--el-bg-color);
}

/* 左侧导航 */
.te-nav {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 18px 14px;
  border-right: 1px solid var(--el-border-color-lighter);
  background: var(--el-fill-color-light);
  overflow: hidden;
}

.te-nav__head {
  display: flex;
  flex-direction: column;
  gap: 3px;
  padding: 0 6px 12px;
}

.te-nav__title {
  font-size: var(--el-font-size-medium);
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.te-nav__sub {
  font-size: var(--el-font-size-extra-small);
  line-height: 1.5;
  color: var(--el-text-color-secondary);
}

.te-nav__list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.te-nav__item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 9px 10px;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s ease, color 0.2s ease;
}

.te-nav__item:hover { background: var(--el-fill-color); }

.te-nav__item.is-active {
  background: var(--el-color-primary-light-9);
}

.te-nav__item.is-active .te-nav__num,
.te-nav__item.is-active .te-nav__name { color: var(--el-color-primary); }

.te-nav__num {
  flex: none;
  font-family: var(--font-mono);
  font-size: var(--el-font-size-extra-small);
  font-weight: 600;
  line-height: 1.6;
  color: var(--el-text-color-placeholder);
}

.te-nav__label { display: flex; flex-direction: column; gap: 2px; min-width: 0; }

.te-nav__name {
  font-size: var(--el-font-size-small);
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.te-nav__desc {
  font-size: var(--el-font-size-extra-small);
  line-height: 1.4;
  color: var(--el-text-color-secondary);
}

.te-nav__foot {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: auto;
  padding: 12px 6px 0;
  border-top: 1px solid var(--el-border-color-lighter);
}

.te-chip {
  display: inline-flex;
  align-items: center;
  height: 20px;
  padding: 0 8px;
  font-family: var(--font-mono);
  font-size: var(--el-font-size-extra-small);
  color: var(--el-text-color-secondary);
  background: var(--el-bg-color);
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 999px;
}

.te-chip--primary {
  color: var(--el-color-primary);
  border-color: var(--el-color-primary-light-7);
  background: var(--el-color-primary-light-9);
}

/* 右侧主区 */
.te-main {
  display: flex;
  flex-direction: column;
  min-height: 0;
  min-width: 0;
}

.te-head {
  display: flex;
  align-items: baseline;
  gap: 8px;
  flex: none;
  padding: 14px 20px;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.te-head__title {
  font-size: var(--el-font-size-medium);
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.te-head__hint {
  font-size: var(--el-font-size-extra-small);
  color: var(--el-text-color-secondary);
}

.te-head__right { margin-left: auto; }

.te-head__btn { font-size: var(--el-font-size-base); }

.te-body {
  flex: 1;
  min-height: 0;
  overflow: auto;
  overscroll-behavior: contain;
  padding: 6px 20px 20px;
}

.te-sec { padding: 16px 0 4px; }

.te-sec + .te-sec { border-top: 1px solid var(--el-border-color-lighter); }

.te-sec__head {
  display: flex;
  align-items: baseline;
  gap: 8px;
  margin-bottom: 12px;
}

.te-sec__num {
  font-family: var(--font-mono);
  font-size: var(--el-font-size-extra-small);
  font-weight: 700;
  color: var(--el-color-primary);
}

.te-sec__title {
  font-size: var(--el-font-size-base);
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.te-sec__desc {
  font-size: var(--el-font-size-extra-small);
  color: var(--el-text-color-secondary);
}

.te-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 4px 16px;
}

.te-grid :deep(.el-form-item) { margin-bottom: 14px; }

.te-grid :deep(.el-select),
.te-grid :deep(.el-input),
.te-grid :deep(.el-textarea) { width: 100%; }

.te-span-all { grid-column: 1 / -1; }

.te-foot {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: none;
  padding: 10px 20px;
  border-top: 1px solid var(--el-border-color);
  background: var(--el-bg-color);
}

.te-foot__hint {
  font-size: var(--el-font-size-extra-small);
  color: var(--el-text-color-placeholder);
}

.te-foot__actions { margin-left: auto; display: flex; gap: 10px; }

@media (max-width: 1280px) {
  .te { grid-template-columns: 210px minmax(0, 1fr); }
  .te-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); }
}

@media (max-width: 900px) {
  .te { grid-template-columns: 1fr; }
  .te-nav { display: none; }
  .te-grid { grid-template-columns: 1fr; }
}
</style>

<style>
/* 任务编辑弹窗：el-dialog 被 teleport 到 body，且 class 落在 .el-dialog 自身上，
   scoped + :deep(.el-dialog) 匹配不到，必须用非 scoped 且写成 .el-dialog.task-dialog */
.el-dialog.task-dialog {
  display: flex;
  flex-direction: column;
  height: 100vh;
  margin: 0;
  padding: 0;
  border-radius: 0;
  overflow: hidden;
}

.el-dialog.task-dialog .el-dialog__header { display: none; }

.el-dialog.task-dialog .el-dialog__body {
  flex: 1;
  min-height: 0;
  padding: 0;
  overflow: hidden;
}

.el-dialog.task-dialog .el-dialog__footer { display: none; }
</style>
