<script lang="ts" setup>
import {
  Edit, ArrowLeft, Document, Operation
} from '@element-plus/icons-vue'
import {ref, reactive, onMounted, computed} from 'vue'
import type {FormInstance, FormRules} from 'element-plus'
import axios from '@/network'
import {msg, notify} from '@/utils/Utils'
import {getLangData} from "@/i18n/locale";
import router from '@/router'

const langData = getLangData()
const fromTaskListFlag = ref(false)
const dictMap = ref<any>({})
const logStatusList = ref<any[]>([])
const triggerStatusList = ref<any[]>([])
const scheduleTypeList = ref<any[]>([])
const glueTypeList = ref<any[]>([])
const executorRouteStrategyList = ref<any[]>([])
const misfireStrategyList = ref<any[]>([])
const executorBlockStrategyList = ref<any[]>([])
const logClearTypeList = ref<any[]>([])

const queryDict = () => {
  axios({
    url: '/jobinfo/dict',
    method: 'get'
  }).then((res: any) => {
    if (res.data.state == 'OK') {
      dictMap.value = res.data.body || {}
      logStatusList.value = dictMap.value['xxl-job-admin,logStatus'] || [] as any[]
      triggerStatusList.value = dictMap.value['xxl-job-admin,triggerStatus'] || [] as any[]
      scheduleTypeList.value = dictMap.value['xxl-job-admin,scheduleType'] || [] as any[]
      glueTypeList.value = dictMap.value['xxl-job-admin,glueType'] || [] as any[]
      executorRouteStrategyList.value = dictMap.value['xxl-job-admin,executorRouteStrategy'] || [] as any[]
      misfireStrategyList.value = dictMap.value['xxl-job-admin,misfireStrategy'] || [] as any[]
      executorBlockStrategyList.value = dictMap.value['xxl-job-admin,executorBlockStrategy'] || [] as any[]
      logClearTypeList.value = dictMap.value['xxl-job-admin,logClearType'] || [] as any[]
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
  fromTaskList()
  queryDict()
  query()
});

const fromTaskList = () => {
  let q = router.currentRoute.value.query
  if (q && q.jobGroup && q.jobId) {
    fromTaskListFlag.value = true
    form.jobGroup = Number.parseInt(q.jobGroup as string)
    form.jobId = Number.parseInt(q.jobId as string)
    fetchTaskList('')
  }
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

const taskList = ref<any[]>([])
const fetchTaskList = (query: string) => {
  if (!form.jobGroup || form.jobGroup == null || form.jobGroup == '') {
    msg(langData.pleaseSelectExecutor, 'warning')
    return
  }
  axios({
    url: '/joblog/tasks',
    method: 'get',
    params: {groupId: form.jobGroup}
  }).then((res: any) => {
    if (res.data.state == 'OK') {
      if (query && query != '') {
        let list = res.data.body || []
        taskList.value = list.filter((item: any) => item.value.includes(query))
      } else {
        taskList.value = res.data.body
      }
    } else {
      msg(res.data.errorMessage, 'warning')
    }
  }).catch((err: any) => {
    console.log('', err)
    msg(err?.response.data.errorMessage, 'error')
  })
}

const fetchTaskList2 = (query: string) => {
  if (!logClearForm.jobGroup || logClearForm.jobGroup == null || logClearForm.jobGroup == '') {
    msg(langData.pleaseSelectExecutor, 'warning')
    return
  }
  axios({
    url: '/joblog/tasks',
    method: 'get',
    params: {groupId: logClearForm.jobGroup}
  }).then((res: any) => {
    if (res.data.state == 'OK') {
      if (query && query != '') {
        let list = res.data.body || []
        taskList.value = list.filter((item: any) => item.value.includes(query))
      } else {
        taskList.value = res.data.body
      }
    } else {
      msg(res.data.errorMessage, 'warning')
    }
  }).catch((err: any) => {
    console.log('', err)
    msg(err?.response.data.errorMessage, 'error')
  })
}

const form = reactive({
  jobGroup: null as any,
  jobId: null as any,
  logStatus: null,
  time: [] as any[],
  filterTime: '',
  start: 1,
  length: 10,
  timeSet: function () {
    this.filterTime = ''
    if (this.time != null)
      this.filterTime = this.time.join(' - ')
  }
})
const list = ref<any[]>([])
const total = ref(0)
const query = () => {
  form.timeSet()
  axios({
    url: '/joblog/pageList',
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

const dialogFormVisible = ref(false)
const logClearForm = reactive({
  jobGroup: null as any,
  jobId: null as any,
  type: null as any
})
const formRef = ref<FormInstance>();
const rules = reactive<FormRules>({
  jobGroup: [{required: true, message: langData.formValidateNotNull, trigger: 'blur'}],
  type: [{required: true, message: langData.formValidateNotNull, trigger: 'blur'}]
})

const updateLogClear = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid, fields) => {
    if (valid) {
      axios({
        url: '/joblog/clearLog',
        method: 'get',
        params: logClearForm
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
const showLogClearDialog = () => {
  dialogFormVisible.value = true
  formRef.value?.clearValidate()
  logClearForm.jobGroup = form.jobGroup
  logClearForm.jobId = form.jobId
}

const drawer = ref(false)
const drawerTitle = ref(langData.logDetail)
const logDetail = ref()
const showLogDrawer = (row: any) => {
  axios({
    url: '/joblog/logDetailCat',
    method: 'get',
    params: {
      logId: row.id,
      fromLineNum: 1
    }
  }).then((res: any) => {
    if (res.data.state == 'OK') {
      logDetail.value = res.data.body.logContent
      drawer.value = true
    } else {
      msg(res.data.errorMessage, 'warning')
    }
  }).catch((err: any) => {
    console.log('', err)
    msg(err?.response.data.errorMessage, 'error')
  })
}
const formatLogDetail = computed(() => {
  return logDetail.value.replace(/\n/g, '<br/>')
})

const drawer1 = ref(false)
const drawerTitle1 = ref(langData.taskDetail)
const jobInfo = ref<any>({})
const showJobInfoDrawer = (row: any) => {
  axios({
    url: '/jobinfo/info',
    method: 'get',
    params: {id: row.jobId}
  }).then((res: any) => {
    if (res.data.state == 'OK') {
      jobInfo.value = res.data.body
      drawer1.value = true
    } else {
      msg(res.data.errorMessage, 'warning')
    }
  }).catch((err: any) => {
    console.log('', err)
    msg(err?.response.data.errorMessage, 'error')
  })
}

</script>

<template>
  <div class="container">
    <el-page-header :icon="ArrowLeft" @back="router.back()" v-if="fromTaskListFlag">
      <template #content>
        <span class="text-large font-600 mr-3"> {{ langData.taskLog }} </span>
      </template>
    </el-page-header>
    <el-divider content-position="left" v-if="fromTaskListFlag"></el-divider>
    <el-form :model="form" size="small" label-position="right" inline-message :inline="true">
      <el-form-item :label="langData.time" prop="time" size="small" style="width: 360px">
        <el-date-picker
            v-model="form.time"
            type="datetimerange"
            :start-placeholder="langData.startTime"
            :end-placeholder="langData.endTime"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            size="small"
        />
      </el-form-item>
      <el-form-item :label="langData.executor" prop="jobGroup" style="width: 180px">
        <el-select v-model="form.jobGroup" :placeholder="langData.formSelectPlaceholder" size="small" clearable
                   filterable style="width: 100%"
                   remote automatic-dropdown :remote-method="fetchJobGroup"
                   @change="()=>{ if (!form.jobGroup || form.jobGroup==null || form.jobGroup=='') { form.jobId=null; taskList=[] as any[];}}">
          <el-option :key="item.id" :label="item.title" :value="item.id" v-for="item in JobGroupList"/>
        </el-select>
      </el-form-item>
      <el-form-item :label="langData.task" prop="jobId" style="width: 180px">
        <el-select v-model="form.jobId" :placeholder="langData.formSelectPlaceholder" size="small" clearable
                   filterable style="width: 100%"
                   remote automatic-dropdown :remote-method="fetchTaskList">
          <el-option :key="item.id" :label="item.jobDesc" :value="item.id" v-for="item in taskList"/>
        </el-select>
      </el-form-item>
      <el-form-item :label="langData.logStatus" prop="logStatus" style="width: 180px">
        <el-select v-model="form.logStatus" :placeholder="langData.formSelectPlaceholder" size="small" clearable
                   filterable style="width: 100%"
                   automatic-dropdown>
          <el-option :key="item.key" :label="item.value" :value="item.key" v-for="item in logStatusList"/>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" size="small" @click="query()">{{ langData.btnSearch }}</el-button>
        <el-tooltip :content="langData.logClean" effect="dark" placement="bottom">
          <el-icon @click="showLogClearDialog" color="red" style="cursor: pointer; margin-left: 10px" :size="24">
            <template #default>
              <svg t="1761636296459" class="icon" viewBox="0 0 1024 1024" version="1.1"
                   xmlns="http://www.w3.org/2000/svg" p-id="6536" width="32" height="32">
                <path
                    d="M960.7 511.8c0 253.5-205.5 459-459 459s-459-205.5-459-459 205.5-459 459-459 459 205.5 459 459zM591.9 657.7c-3.8 3.4-7 6.2-10.3 9-20 17.1-41 32.9-65.4 43.5-14.5 6.3-28.5 8.6-43.3-0.1-11.4-6.7-23.6-12-35.7-18 19.2-21.5 32.9-40.7 34.4-48.3-16.7 15.4-36.6 24.1-58.2 29-2.6 0.6-6.3-0.1-8.5-1.6-10.9-8-21.6-16.3-32.1-24.8-5.6-4.5-10.7-9.7-16.7-15.2 2-0.4 2.9-0.6 3.7-0.7 42.3-6.8 82.8-18.9 120.4-39.8 24.9-13.9 47.4-31 65.2-53.5 7.3-9.2 13.5-19.3 20.4-29.4-13.5-7.6-25.7-14.5-37.5-21.1-6.3 8.9-11.7 17.8-18.4 25.7-23 27.2-52.9 44.4-85.8 56.8-36.3 13.7-73.8 21.5-112.7 22-9.1 0.1-16.5 4-20.3 12.4-4 8.8-2.2 17 3.9 24.5 51.1 62.2 115.6 105.4 190.5 133.6 41.2 15.5 83.7 25.5 127.5 29.7 11.4 1.1 20.5-1.9 26.9-11.7 2.8-4.3 6.1-8.2 8.9-12.5 34.5-50.9 60.3-106.1 81.1-163.9 2.4-6.8 4.7-13.7 7.2-20.9-13.9-4.8-27.1-9.3-40.6-14-0.7 2-1.4 3.7-2 5.4-6.2 16.6-11.9 33.3-18.6 49.7-17.1 41.8-37.4 81.9-62.7 119.5-2.2 3.3-4.3 4.8-8.4 4-12.6-2.3-25.2-4.1-37.8-6.4-6.9-1.3-13.7-3.1-20.8-4.8 26.2-37.5 42.5-65.3 45.7-78.1z m158.4-111.4c1.1-3 2.1-5.4 2.9-8 9.7-33-5.3-66.2-37.2-80.9-27.1-12.6-54.5-24.6-81.9-36.5-21.3-9.3-42.4-8-62.4 3.9-12 7.1-20.9 17.1-26.6 30.3 68.5 30.4 136.6 60.7 205.2 91.2z m-30.5-119.5c0.6-1.2 1-1.8 1.3-2.5 19.8-54.9 39.6-109.7 59.6-164.6 2.1-5.8 1.2-10.8-2.6-15.3-5.7-6.9-13.2-10.7-22.1-12-9.6-1.4-14.8 1.1-19.3 9.5-26.7 50.1-53.4 100.2-80.1 150.2l-2.4 4.8c0.6 0.6 0.9 1.1 1.4 1.3 21.3 9.5 42.6 19 64.2 28.6z m-325.7 52.8c0.7-29.2-23.7-54.2-53.2-54.8-28.9-0.5-54 23.8-54.6 52.9-0.6 29.4 23.3 54.2 53 55.1 29.1 0.9 54.1-23.4 54.8-53.2zM254 317.1c-17.9-0.1-32.5 14.4-32.5 32.2 0 17.7 14.9 32.5 32.5 32.4 17.5-0.1 32-14.6 32.2-32.1 0.2-17.8-14.3-32.4-32.2-32.5z m75.6 43c0 11.9 9.8 21.7 21.6 21.6 11.5-0.1 21.2-9.8 21.4-21.3 0.2-11.8-9.6-21.7-21.5-21.7-11.9-0.1-21.5 9.4-21.5 21.4z"
                    fill="currentColor" p-id="6537"></path>
              </svg>
            </template>
          </el-icon>
        </el-tooltip>
      </el-form-item>
    </el-form>

    <el-table :data="list" style="width: 100%" table-layout="fixed" :stripe="true"
              size="small" :highlight-current-row="true">
      <!--      <el-table-column type="selection" header-align="center" align="center"/>-->
      <el-table-column fixed="left" :label="langData.tableHeaderOp" width="80" header-align="center" align="center">
        <template #default="scope">
          <el-icon @click="showLogDrawer(scope.row)" style="cursor: pointer; margin-left: 10px"
                   :size="14" :title="langData.detailLog">
            <template #default>
              <svg t="1761640615592" class="icon" viewBox="0 0 1024 1024" version="1.1"
                   xmlns="http://www.w3.org/2000/svg" p-id="14178" width="32" height="32">
                <path
                    d="M909.756 182.402l-124.84-129.71C757.712 24.497 704.076 1.345 665.59 1.345H246.077c-38.488 0-69.891 32.686-69.891 72.588v351.385h55.92V73.933c0-7.902 6.496-14.503 14.03-14.503H665.64c4.127 0 8.926 0.55 13.964 1.556v230.765h222.273c0.993 5.25 1.519 33.915 1.519 38.339v619.977c0 7.899-6.5 14.493-14.033 14.493H246.077c-7.611 0-14.032-6.724-14.032-14.486V845.158h-55.934v104.916c0 39.963 31.41 72.578 69.895 72.578H889.3c38.499 0 69.898-32.615 69.898-72.578V330.168c0.065-39.98-22.218-119.565-49.442-147.766z m-173.094 57.443V94.727a66.072 66.072 0 0 1 9.807 8.264l125.44 126.795a77.107 77.107 0 0 1 8.236 10.059H736.662z"
                    p-id="14179"></path>
                <path
                    d="M824.32 783.36c0 22.62-18.34 40.96-40.96 40.96H87.04c-22.62 0-40.96-18.34-40.96-40.96V482.987c0-22.62 18.34-40.96 40.96-40.96h696.32c22.62 0 40.96 18.34 40.96 40.96V783.36zM318.976 674.372h-81.893v-139.12h-60.187V716.8h142.08v-42.428z m20.562-50.32c1.973 61.837 33.546 93.734 94.72 95.707 61.174-2.635 93.071-34.194 95.71-94.72-3.946-60.511-35.86-92.085-95.71-94.72-59.863 3.301-91.436 34.533-94.72 93.734z m131.226 0.987c-0.663 34.209-12.828 51.648-36.506 52.293-22.371-0.645-34.209-18.084-35.52-52.293 1.311-32.884 13.152-49.657 35.52-50.319 23.016 0 35.181 16.773 36.506 50.32z m184.354-7.891v39.468h26.64c-4.61 11.841-14.8 18.746-30.586 20.719-25.006 0-37.834-16.432-38.479-49.333 0.645-34.857 12.162-52.616 34.533-53.279 16.432 0 26.641 7.571 30.587 22.692h57.228c-9.22-43.414-38.158-65.768-86.829-67.092-59.863 2.635-91.436 35.198-94.72 97.68 1.973 58.551 31.232 89.121 87.815 91.76 24.99 0 43.414-6.905 55.255-20.72l1.973 17.76h38.479v-99.652h-81.896z"
                    p-id="14180"></path>
              </svg>
            </template>
          </el-icon>
          <el-icon @click="showJobInfoDrawer(scope.row)" color="red" style="cursor: pointer; margin-left: 10px"
                   :size="14" :title="langData.taskDetail">
            <template #default>
              <svg t="1761640468607" class="icon" viewBox="0 0 1024 1024" version="1.1"
                   xmlns="http://www.w3.org/2000/svg" p-id="11004" width="32" height="32">
                <path
                    d="M831.825474 63.940169H191.939717C121.2479 63.940169 63.940169 121.2479 63.940169 191.939717v639.885757C63.940169 902.517291 121.2479 959.825022 191.939717 959.825022h639.885757c70.691817 0 127.999548-57.307731 127.999548-127.999548V191.939717C959.825022 121.2479 902.517291 63.940169 831.825474 63.940169zM895.884854 831.998871A63.835408 63.835408 0 0 1 831.912173 895.884854H192.087827c-17.112123 0-33.270563-6.574639-45.372232-18.67631S127.880338 849.110994 127.880338 831.998871V192.001129A64.236389 64.236389 0 0 1 192.087827 127.880338h639.824346A64.037705 64.037705 0 0 1 895.884854 192.001129v639.997742z"
                    fill="currentColor" p-id="11005"></path>
                <path
                    d="M791.998335 351.851551h-255.999097a31.970084 31.970084 0 0 0 0 63.940169h255.999097a31.970084 31.970084 0 0 0 0-63.940169zM791.998335 607.973471h-255.999097a31.970084 31.970084 0 0 0 0 63.940169h255.999097a31.970084 31.970084 0 0 0 0-63.940169zM344.001722 527.997686c-61.855792 0-111.985607 50.144265-111.985607 111.985606s50.144265 111.985607 111.985607 111.985607 111.985607-50.144265 111.985606-111.985607-50.129815-111.985607-111.985606-111.985606z m33.982213 145.982269a48.045438 48.045438 0 1 1 14.088511-33.982213 47.745605 47.745605 0 0 1-14.088511 33.985826zM417.395643 297.394035L311.999125 402.78694 270.6078 361.392003a31.970084 31.970084 0 1 0-45.213286 45.213285l63.997968 64.001581a31.970084 31.970084 0 0 0 45.213286 0l127.999548-127.999549a31.970084 31.970084 0 0 0-45.209673-45.213285z"
                    fill="currentColor" p-id="11006"></path>
              </svg>
            </template>
          </el-icon>
        </template>
      </el-table-column>
      <el-table-column prop="id" :label="langData.taskId" :show-overflow-tooltip="true" header-align="center"
                       align="center" width="80"/>
      <!--      <el-table-column prop="jobGroup" label="执行器ID" :show-overflow-tooltip="true" header-align="center"-->
      <!--                       align="center" width="80"/>-->
      <el-table-column prop="executorAddress" :label="langData.executorAddress" :show-overflow-tooltip="true"
                       header-align="center"
                       align="left"/>
      <el-table-column prop="executorHandler" label="jobHandler" :show-overflow-tooltip="true" header-align="center"
                       align="center"/>
      <el-table-column prop="fmtTriggerCode" :label="langData.scheduleResult" :show-overflow-tooltip="true"
                       header-align="center"
                       align="center" width="120">
        <template #default="scope">
          <el-tag v-if="scope.row.triggerCode==200 || scope.row.triggerCode==500"
                  :type="scope.row.triggerCode==200?'success':'danger'"
                  effect="plain"
                  round
          >
            {{ scope.row.fmtTriggerCode }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="fmtTriggerTime" :label="langData.scheduleTime" :show-overflow-tooltip="true"
                       header-align="center"
                       align="left"/>
      <el-table-column prop="fmtHandleCode" :label="langData.executeResult" :show-overflow-tooltip="true"
                       header-align="center"
                       align="center" width="120">
        <template #default="scope">
          <el-tag v-if="scope.row.handleCode==200 || scope.row.handleCode==500"
                  :type="scope.row.handleCode==200?'success':'danger'"
                  effect="plain"
                  round
          >
            {{ scope.row.fmtHandleCode }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="fmtHandleTime" :label="langData.executeTime" :show-overflow-tooltip="true"
                       header-align="center"
                       align="left"/>
    </el-table>
    <el-pagination class="page" v-model:page-size="form.length" v-model:current-page="form.start"
                   layout="->, total, sizes, prev, pager, next, jumper" v-model:total="total"
                   @size-change="query()"
                   @current-change="query()" @prev-click="query()" @next-click="query()"
                   size="small" :background="true"
                   :page-sizes="[5, 10, 20, 50, 100]"/>

    <el-drawer
        v-model="drawer"
        :title="drawerTitle"
        direction="ltr"
        size="60%"
    >
      <p v-html="formatLogDetail" style="font-size: 12px">
      </p>
    </el-drawer>

    <el-drawer
        v-model="drawer1"
        :title="drawerTitle1"
        direction="ltr"
        size="50%"
    >
      <el-form size="small" label-position="right" inline-message :inline="false" label-width="30%">
        <el-divider content-position="left">{{ langData.baseConfig }}</el-divider>
        <el-form-item :label="langData.executor" prop="jobGroup">
          <el-select v-model="jobInfo.jobGroup" :placeholder="langData.formSelectPlaceholder" size="small" clearable
                     filterable style="width: 100%" :remote-method="fetchJobGroup">
            <el-option :key="item.id" :label="item.title" :value="item.id" v-for="item in JobGroupList"/>
          </el-select>
        </el-form-item>
        <el-form-item :label="langData.taskDesc" prop="jobDesc">
          <el-input v-model="jobInfo.jobDesc" type="text" clearable/>
        </el-form-item>
        <el-form-item :label="langData.alarmEmail" prop="alarmEmail">
          <el-input v-model="jobInfo.alarmEmail" type="textarea" clearable
                    :placeholder="langData.alarmEmailPlaceholder"
                    :rows="2"/>
        </el-form-item>
        <el-divider content-position="left">{{ langData.scheduleConfig }}</el-divider>
        <el-form-item :label="langData.scheduleType" prop="scheduleType">
          <el-select v-model="jobInfo.scheduleType" :placeholder="langData.formSelectPlaceholder" size="small" clearable
                     filterable style="width: 100%">
            <el-option :key="item.key" :label="item.value" :value="item.key" v-for="item in scheduleTypeList"/>
          </el-select>
        </el-form-item>
        <el-form-item label="CRON" prop="scheduleConf" v-if="jobInfo.scheduleType=='CRON'">
          <el-input v-model="jobInfo.scheduleConf" type="text" clearable placeholder="* * * * * ?"/>
        </el-form-item>
        <el-form-item :label="langData.fixedRate" prop="scheduleConf" v-if="jobInfo.scheduleType=='FIX_RATE'">
          <el-input v-model="jobInfo.scheduleConf" type="number" clearable placeholder="30"/>
        </el-form-item>
        <el-divider content-position="left">{{ langData.taskConfig }}</el-divider>
        <el-form-item :label="langData.glueType" prop="glueType">
          <el-select v-model="jobInfo.glueType" :placeholder="langData.formSelectPlaceholder" size="small" clearable
                     filterable style="width: 100%">
            <el-option :key="item.key" :label="item.value" :value="item.key" v-for="item in glueTypeList"/>
          </el-select>
        </el-form-item>
        <el-form-item :label="langData.taskHandler" prop="executorHandler" v-if="jobInfo.glueType=='BEAN'">
          <el-input v-model="jobInfo.executorHandler" type="text" clearable
                    :placeholder="langData.taskHandlerPlaceholder"/>
        </el-form-item>
        <el-form-item :label="langData.executeParam" prop="executorParam">
          <el-input v-model="jobInfo.executorParam" type="textarea" clearable :rows="5"
                    :placeholder="langData.taskParamsPlaceholder"/>
        </el-form-item>
        <el-divider content-position="left">{{ langData.advancedConfig }}</el-divider>
        <el-form-item :label="langData.routeStrategy" prop="executorRouteStrategy">
          <el-select v-model="jobInfo.executorRouteStrategy" :placeholder="langData.formSelectPlaceholder" size="small"
                     clearable filterable style="width: 100%"
                     automatic-dropdown>
            <el-option :key="item.key" :label="item.value" :value="item.key" v-for="item in executorRouteStrategyList"/>
          </el-select>
        </el-form-item>
        <el-form-item :label="langData.childTaskId" prop="childJobId">
          <el-input v-model="jobInfo.childJobId" type="text" clearable/>
        </el-form-item>
        <el-form-item :label="langData.misfireStrategy" prop="misfireStrategy">
          <el-select v-model="jobInfo.misfireStrategy" :placeholder="langData.formSelectPlaceholder" size="small"
                     clearable
                     filterable style="width: 100%"
                     automatic-dropdown>
            <el-option :key="item.key" :label="item.value" :value="item.key" v-for="item in misfireStrategyList"/>
          </el-select>
        </el-form-item>
        <el-form-item :label="langData.blockStrategy" prop="executorBlockStrategy">
          <el-select v-model="jobInfo.executorBlockStrategy" :placeholder="langData.formSelectPlaceholder" size="small"
                     clearable
                     filterable style="width: 100%"
                     automatic-dropdown>
            <el-option :key="item.key" :label="item.value" :value="item.key" v-for="item in executorBlockStrategyList"/>
          </el-select>
        </el-form-item>
        <el-form-item :label="langData.timeout" prop="executorTimeout">
          <el-input v-model="jobInfo.executorTimeout" type="number" clearable/>
        </el-form-item>
        <el-form-item :label="langData.failRetryCount" prop="executorFailRetryCount">
          <el-input v-model="jobInfo.executorFailRetryCount" type="number" clearable/>
        </el-form-item>
        <el-form-item label="" prop="glueRemark">
          <el-input v-model="jobInfo.glueRemark" type="hidden" clearable/>
        </el-form-item>
      </el-form>
    </el-drawer>
  </div>

  <el-dialog v-model="dialogFormVisible" :title="langData.logClean" draggable width="40%">
    <el-form :model="logClearForm" label-position="right" size="small" :inline="false" ref="formRef" :rules="rules"
             label-width="25%">
      <el-form-item :label="langData.executor" prop="vendor">
        <el-select v-model="logClearForm.jobGroup" :placeholder="langData.formSelectPlaceholder" size="small" clearable
                   filterable style="width: 100%"
                   remote automatic-dropdown :remote-method="fetchJobGroup">
          <el-option :key="item.id" :label="item.title" :value="item.id" v-for="item in JobGroupList"/>
        </el-select>
      </el-form-item>
      <el-form-item :label="langData.task" prop="jobId">
        <el-select v-model="logClearForm.jobId" :placeholder="langData.formSelectPlaceholder" size="small" clearable
                   filterable style="width: 100%"
                   remote automatic-dropdown :remote-method="fetchTaskList2">
          <el-option :key="item.id" :label="item.jobDesc" :value="item.id" v-for="item in taskList"/>
        </el-select>
      </el-form-item>
      <el-form-item :label="langData.cleanWay" prop="type">
        <el-select v-model="logClearForm.type" :placeholder="langData.formSelectPlaceholder" size="small" clearable
                   filterable style="width: 100%"
                   automatic-dropdown>
          <el-option :key="item.key" :label="item.value" :value="item.key" v-for="item in logClearTypeList"/>
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
              <span class="dialog-footer">
                <el-button @click="dialogFormVisible = false">{{ langData.btnCancel }}</el-button>
                <el-button type="warning" @click="updateLogClear(formRef)">{{ langData.btnConfirm }}</el-button>
              </span>
    </template>
  </el-dialog>
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