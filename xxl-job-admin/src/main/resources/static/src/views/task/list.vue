<script lang="ts" setup>
import {
  Edit, ArrowLeft, Plus, Delete, VideoPause, VideoPlay, DocumentCopy
} from '@element-plus/icons-vue'
import {ref, reactive, onMounted, computed, provide, inject} from 'vue'
import axios from '@/network'
import {msg, notify, encryptRSA, encryptAES, generateRandomAESKey} from '@/utils/Utils'
import {ElMessage, ElMessageBox} from 'element-plus'
import type {FormInstance, FormRules, TableInstance, UploadFile} from 'element-plus'
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

const headerCellStyle = () => {
  // 添加表头颜色
  return {backgroundColor: '#f5f5f5', color: '#333', fontWeight: 'bold'};
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

const showAddTaskDialog = () => {
  drawer.value = true
  drawerTitle.value = langData.addTask
  formRef.value?.clearValidate()
  taskForm.id = null
  taskForm.jobGroup = null
  taskForm.jobDesc = ''
  taskForm.author = ''
  taskForm.alarmEmail = ''
  taskForm.scheduleType = ''
  taskForm.scheduleConf = ''
  taskForm.cronGen_display = ''
  taskForm.schedule_conf_CRON = ''
  taskForm.schedule_conf_FIX_RATE = ''
  taskForm.schedule_conf_FIX_DELAY = ''
  taskForm.glueType = ''
  taskForm.executorHandler = ''
  taskForm.executorParam = ''
  taskForm.executorRouteStrategy = ''
  taskForm.childJobId = ''
  taskForm.misfireStrategy = ''
  taskForm.executorBlockStrategy = ''
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

    <el-table :data="list" style="width: 100%" table-layout="fixed" :stripe="true"
              size="small" :highlight-current-row="true" :header-cell-style="headerCellStyle">
      <!--      <el-table-column type="selection" header-align="center" align="center"/>-->
      <el-table-column fixed="left" :label="langData.tableHeaderOp" width="100" header-align="center" align="center">
        <template #default="scope">
          <el-icon @click="showEditTaskDialog(scope.row)" color="#3F9EFF" style="cursor: pointer; margin-left: 10px" :size="14"><Edit/></el-icon>
          <el-popconfirm :title="langData.confirmDelete" @confirm="deleteTask(scope.row)" icon-color="red" confirm-button-type="danger">
            <template #reference>
              <el-icon color="red" style="cursor: pointer; margin-left: 10px" :size="14"><Delete/></el-icon>
            </template>
          </el-popconfirm>
          <el-popconfirm :title="langData.confirmOpera" @confirm="startTask(scope.row)" icon-color="red" confirm-button-type="danger" v-if="scope.row.triggerStatus==0">
            <template #reference>
              <el-icon color="#3F9EFF" style="cursor: pointer; margin-left: 10px" :size="14" :title="langData.startTask"><VideoPlay/></el-icon>
            </template>
          </el-popconfirm>
          <el-popconfirm :title="langData.confirmOpera" @confirm="stopTask(scope.row)" icon-color="red" confirm-button-type="danger" v-if="scope.row.triggerStatus==1">
            <template #reference>
              <el-icon color="orange" style="cursor: pointer; margin-left: 10px" :size="14" :title="langData.stopTask"><VideoPause/></el-icon>
            </template>
          </el-popconfirm>
          <el-icon color="#3F9EFF" style="cursor: pointer; margin-left: 10px" :size="14" :title="langData.copyTask" @click="copyTask(scope.row)"><DocumentCopy/></el-icon>
          <el-icon color="#3F9EFF" style="cursor: pointer; margin-left: 10px" :size="14" :title="langData.runImmediate" @click="showImmediateTaskDialog(scope.row)">
            <template #default>
              <svg t="1761619792188" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="4669" width="32" height="32">
                <path
                    d="M317.9 80.6l531.7 354.5c25.7 17.2 41.2 46 41.2 76.9 0 30.9-15.4 59.8-41.2 76.9L317.9 943.4c-28.4 18.9-64.9 20.7-95 4.6-30.1-16.1-48.9-47.5-48.8-81.7V157.6c0-34.1 18.7-65.5 48.8-81.6 30.1-16.2 66.6-14.4 95 4.6z m-38.5 57.7c-7.1-4.7-16.2-5.2-23.7-1.1s-12.2 11.9-12.2 20.4v708.8c0 8.5 4.7 16.4 12.2 20.4 7.5 4 16.7 3.6 23.7-1.2l531.7-354.4c6.4-4.3 10.3-11.5 10.3-19.3 0-7.7-3.9-15-10.3-19.2L279.4 138.3zM440 454.2h-87.7c-4.7 0-8.9-2.8-10.7-7.1-1.8-4.3-0.8-9.3 2.5-12.6l95.9-95.9h57.8v346.9H440V454.2z m0 0"
                    fill="#1296db" p-id="4670"></path>
              </svg>
            </template>
          </el-icon>
          <el-icon @click="toTaskLogList(scope.row)" style="cursor: pointer; margin-left: 10px" :size="14" :title="langData.detailTaskLog">
            <template #default>
              <svg t="1761632063541" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="5500" width="32" height="32">
                <path
                    d="M768 960H256A106.666667 106.666667 0 0 1 149.333333 853.333333V170.666667A106.666667 106.666667 0 0 1 256 64h512A106.666667 106.666667 0 0 1 874.666667 170.666667v682.666666a106.666667 106.666667 0 0 1-106.666667 106.666667zM256 106.666667c-35.413333 0-64 28.586667-64 64v682.666666c0 35.413333 28.586667 64 64 64h512c35.413333 0 64-28.586667 64-64V170.666667c0-35.413333-28.586667-64-64-64H256z"
                    fill="#666666" p-id="5501"></path>
                <path
                    d="M725.333333 362.666667H298.666667c-11.946667 0-21.333333-9.386667-21.333334-21.333334s9.386667-21.333333 21.333334-21.333333h426.666666c11.946667 0 21.333333 9.386667 21.333334 21.333333s-9.386667 21.333333-21.333334 21.333334zM725.333333 533.333333H298.666667c-11.946667 0-21.333333-9.386667-21.333334-21.333333s9.386667-21.333333 21.333334-21.333333h426.666666c11.946667 0 21.333333 9.386667 21.333334 21.333333s-9.386667 21.333333-21.333334 21.333333zM725.333333 704H298.666667c-11.946667 0-21.333333-9.386667-21.333334-21.333333s9.386667-21.333333 21.333334-21.333334h426.666666c11.946667 0 21.333333 9.386667 21.333334 21.333334s-9.386667 21.333333-21.333334 21.333333z"
                    fill="#666666" p-id="5502"></path>
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

  <el-drawer
      v-model="drawer"
      :title="drawerTitle"
      direction="ltr"
      size="50%"
  >
    <el-form :model="taskForm" size="small" label-position="right" inline-message :inline="false" ref="formRef"
             :rules="rules" label-width="30%">
      <el-divider content-position="left">{{langData.baseConfig}}</el-divider>
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
                  :placeholder="langData.alarmEmailPlaceholder"
                  :rows="2"/>
      </el-form-item>
      <el-divider content-position="left">{{langData.scheduleConfig}}</el-divider>
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
      <el-divider content-position="left">{{langData.taskConfig}}</el-divider>
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
        <el-input v-model="taskForm.executorParam" type="textarea" clearable :rows="5"
                  :placeholder="langData.taskParamsPlaceholder"/>
      </el-form-item>
      <el-divider content-position="left">{{ langData.advancedConfig }}</el-divider>
      <el-form-item :label="langData.routeStrategy" prop="executorRouteStrategy">
        <el-select v-model="taskForm.executorRouteStrategy" :placeholder="langData.formSelectPlaceholder" size="small"
                   clearable filterable style="width: 100%"
                   automatic-dropdown>
          <el-option :key="item.key" :label="item.value" :value="item.key" v-for="item in executorRouteStrategyList"/>
        </el-select>
      </el-form-item>
      <el-form-item :label="langData.childTaskId" prop="childJobId">
        <el-input v-model="taskForm.childJobId" type="text" clearable/>
      </el-form-item>
      <el-form-item :label="langData.misfireStrategy" prop="misfireStrategy">
        <el-select v-model="taskForm.misfireStrategy" :placeholder="langData.formSelectPlaceholder" size="small"
                   clearable
                   filterable style="width: 100%"
                   automatic-dropdown>
          <el-option :key="item.key" :label="item.value" :value="item.key" v-for="item in misfireStrategyList"/>
        </el-select>
      </el-form-item>
      <el-form-item :label="langData.blockStrategy" prop="executorBlockStrategy">
        <el-select v-model="taskForm.executorBlockStrategy" :placeholder="langData.formSelectPlaceholder" size="small"
                   clearable
                   filterable style="width: 100%"
                   automatic-dropdown>
          <el-option :key="item.key" :label="item.value" :value="item.key" v-for="item in executorBlockStrategyList"/>
        </el-select>
      </el-form-item>
      <el-form-item :label="langData.timeout" prop="executorTimeout">
        <el-input v-model="taskForm.executorTimeout" type="number" clearable/>
      </el-form-item>
      <el-form-item :label="langData.failRetryCount" prop="executorFailRetryCount">
        <el-input v-model="taskForm.executorFailRetryCount" type="number" clearable/>
      </el-form-item>
      <el-form-item label="" prop="glueRemark">
        <el-input v-model="taskForm.glueRemark" type="hidden" clearable/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" size="small" @click="updateTask(formRef)">{{ langData.btnSave }}</el-button>
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
</style>