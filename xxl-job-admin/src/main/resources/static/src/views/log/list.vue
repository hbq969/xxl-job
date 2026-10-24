<script lang="ts" setup>
import {Icon} from '@iconify/vue'
import PageHead from '@/components/admin/PageHead.vue'
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

const filtersVisible = ref(true)
const filterCount = () => {
  let n = 0
  if (form.jobGroup) n++
  if (form.jobId) n++
  if (form.logStatus) n++
  if (form.time && form.time.length) n++
  return n
}
const resetQuery = () => {
  form.jobGroup = null
  form.jobId = null
  form.logStatus = null
  form.time = []
  form.start = 1
  query()
}

const logDrawerFull = ref(false)
const jobDrawerFull = ref(false)
const logDrawerSize = computed(() => (logDrawerFull.value ? '100%' : '60%'))
const jobDrawerSize = computed(() => (jobDrawerFull.value ? '100%' : '480px'))

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
  <div class="admin-page">
    <PageHead :title="langData.logPageTitle" :desc="langData.logPageDesc">
      <template #lead>
        <template v-if="fromTaskListFlag">
          <el-tooltip :show-after="0" :content="langData.btnBack" placement="bottom">
            <el-button class="page-back" circle text :aria-label="langData.btnBack" @click="router.back()">
              <Icon icon="lucide:circle-arrow-left"/>
            </el-button>
          </el-tooltip>
          <span class="page-divider"></span>
        </template>
      </template>
      <template #actions>
        <span class="admin-muted">{{ total }} {{ langData.totalUnit }}</span>
      </template>
    </PageHead>

    <section class="admin-panel">
      <!-- 查询区 -->
      <div class="admin-filter" v-show="filtersVisible">
        <el-form :model="form" inline-message :inline="true" label-position="right" class="search-form" @submit.prevent>
          <el-form-item :label="langData.time" prop="time">
            <el-date-picker v-model="form.time" type="datetimerange"
                            :start-placeholder="langData.startTime" :end-placeholder="langData.endTime"
                            format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss"
                            class="admin-filter__input--wide"/>
          </el-form-item>
          <el-form-item :label="langData.executor" prop="jobGroup">
            <el-select v-model="form.jobGroup" :placeholder="langData.formSelectPlaceholder" clearable
                       filterable remote automatic-dropdown :remote-method="fetchJobGroup"
                       @change="()=>{ if (!form.jobGroup || form.jobGroup==null || form.jobGroup=='') { form.jobId=null; taskList=[] as any[];}}">
              <el-option :key="item.id" :label="item.title" :value="item.id" v-for="item in JobGroupList"/>
            </el-select>
          </el-form-item>
          <el-form-item :label="langData.task" prop="jobId">
            <el-select v-model="form.jobId" :placeholder="langData.formSelectPlaceholder" clearable
                       filterable remote automatic-dropdown :remote-method="fetchTaskList">
              <el-option :key="item.id" :label="item.jobDesc" :value="item.id" v-for="item in taskList"/>
            </el-select>
          </el-form-item>
          <el-form-item :label="langData.logStatus" prop="logStatus">
            <el-select v-model="form.logStatus" :placeholder="langData.formSelectPlaceholder" clearable
                       filterable automatic-dropdown>
              <el-option :key="item.key" :label="item.value" :value="item.key" v-for="item in logStatusList"/>
            </el-select>
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
          <el-tooltip :show-after="0" :content="langData.logClean" placement="bottom">
            <el-button type="danger" plain circle :aria-label="langData.logClean" @click="showLogClearDialog">
              <Icon icon="lucide:trash-2"/>
            </el-button>
          </el-tooltip>
          <span class="admin-toolbar__divider"></span>
          <el-tooltip :show-after="0" :content="filtersVisible ? langData.filterHide : langData.filterShow" placement="bottom">
            <el-button class="admin-toolbar__icon" :class="{ 'is-active': filtersVisible }" circle text
                       :aria-label="langData.filterHide" @click="filtersVisible = !filtersVisible">
              <Icon icon="lucide:filter"/>
            </el-button>
          </el-tooltip>
        </div>
        <div class="tb-right">
          <el-tooltip :show-after="0" :content="langData.btnRefresh" placement="bottom">
            <el-button class="admin-toolbar__icon" circle text :aria-label="langData.btnRefresh" @click="query()">
              <Icon icon="lucide:refresh-ccw"/>
            </el-button>
          </el-tooltip>
        </div>
      </div>

      <!-- 摘要栏 -->
      <div class="admin-sumbar" v-if="!filtersVisible && filterCount() > 0">
        <span class="admin-sumbar__seg">
          <Icon icon="lucide:filter" class="ic"/>
          {{ langData.filteredPrefix }} {{ filterCount() }} {{ langData.filteredSuffix }}
          <span class="admin-sumbar__lnk" @click="filtersVisible = true">{{ langData.filterExpand }}</span>
        </span>
      </div>

      <!-- 表格 -->
      <div class="admin-table">
        <el-table :data="list" table-layout="fixed" highlight-current-row class="data-table">
          <el-table-column fixed="left" :label="langData.tableHeaderOp" width="100" header-align="center" align="center">
            <template #default="scope">
              <div class="row-actions">
                <el-tooltip :show-after="500" :content="langData.detailLog" placement="top">
                  <el-button link type="primary" class="admin-op" @click="showLogDrawer(scope.row)">
                    <Icon icon="lucide:file-text"/>
                  </el-button>
                </el-tooltip>
                <el-tooltip :show-after="500" :content="langData.taskDetail" placement="top">
                  <el-button link class="admin-op admin-op--muted" @click="showJobInfoDrawer(scope.row)">
                    <Icon icon="lucide:list-tree"/>
                  </el-button>
                </el-tooltip>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="id" :label="langData.taskId" :show-overflow-tooltip="true" header-align="center" align="center" width="80"/>
          <el-table-column prop="executorAddress" :label="langData.executorAddress" :show-overflow-tooltip="true" header-align="left" align="left" min-width="140"/>
          <el-table-column prop="executorHandler" label="jobHandler" :show-overflow-tooltip="true" header-align="left" align="left" min-width="130">
            <template #default="{row}">
              <span class="cell-mono">{{ row.executorHandler }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="langData.scheduleResult" :show-overflow-tooltip="true" header-align="center" align="center" width="130">
            <template #default="scope">
              <el-tag v-if="scope.row.triggerCode==200 || scope.row.triggerCode==500"
                      :type="scope.row.triggerCode==200?'success':'danger'" effect="light" round>
                {{ scope.row.fmtTriggerCode }}
              </el-tag>
              <span v-else class="cell-muted">--</span>
            </template>
          </el-table-column>
          <el-table-column prop="fmtTriggerTime" :label="langData.scheduleTime" :show-overflow-tooltip="true" header-align="center" align="center" width="180"/>
          <el-table-column :label="langData.executeResult" :show-overflow-tooltip="true" header-align="center" align="center" width="130">
            <template #default="scope">
              <el-tag v-if="scope.row.handleCode==200 || scope.row.handleCode==500"
                      :type="scope.row.handleCode==200?'success':'danger'" effect="light" round>
                {{ scope.row.fmtHandleCode }}
              </el-tag>
              <span v-else class="cell-muted">--</span>
            </template>
          </el-table-column>
          <el-table-column prop="fmtHandleTime" :label="langData.executeTime" :show-overflow-tooltip="true" header-align="center" align="center" width="180"/>
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

    <!-- 日志详情抽屉 -->
    <el-drawer v-model="drawer" :size="logDrawerSize" direction="ltr">
      <template #header>
        <div class="drawer-head">
          <span class="drawer-head__title">{{ drawerTitle }}</span>
          <el-tooltip :show-after="0" :content="logDrawerFull ? langData.drawerRestore : langData.drawerMaximize" placement="bottom">
            <el-button link class="drawer-head__btn" :aria-label="langData.drawerMaximize" @click="logDrawerFull = !logDrawerFull">
              <Icon :icon="logDrawerFull ? 'lucide:minimize-2' : 'lucide:maximize-2'"/>
            </el-button>
          </el-tooltip>
        </div>
      </template>
      <div class="log-content" v-html="formatLogDetail"></div>
    </el-drawer>

    <!-- 任务详情抽屉 -->
    <el-drawer v-model="drawer1" :size="jobDrawerSize" direction="ltr">
      <template #header>
        <div class="drawer-head">
          <span class="drawer-head__title">{{ drawerTitle1 }}</span>
          <el-tooltip :show-after="0" :content="jobDrawerFull ? langData.drawerRestore : langData.drawerMaximize" placement="bottom">
            <el-button link class="drawer-head__btn" :aria-label="langData.drawerMaximize" @click="jobDrawerFull = !jobDrawerFull">
              <Icon :icon="jobDrawerFull ? 'lucide:minimize-2' : 'lucide:maximize-2'"/>
            </el-button>
          </el-tooltip>
        </div>
      </template>
      <el-form label-position="top" :inline="false" class="detail-form">
        <el-divider content-position="left">{{ langData.baseConfig }}</el-divider>
        <el-form-item :label="langData.executor" prop="jobGroup">
          <el-select v-model="jobInfo.jobGroup" :placeholder="langData.formSelectPlaceholder" clearable filterable :remote-method="fetchJobGroup">
            <el-option :key="item.id" :label="item.title" :value="item.id" v-for="item in JobGroupList"/>
          </el-select>
        </el-form-item>
        <el-form-item :label="langData.taskDesc" prop="jobDesc">
          <el-input v-model="jobInfo.jobDesc" type="text" clearable/>
        </el-form-item>
        <el-form-item :label="langData.alarmEmail" prop="alarmEmail">
          <el-input v-model="jobInfo.alarmEmail" type="textarea" clearable :placeholder="langData.alarmEmailPlaceholder" :rows="2"/>
        </el-form-item>
        <el-divider content-position="left">{{ langData.scheduleConfig }}</el-divider>
        <el-form-item :label="langData.scheduleType" prop="scheduleType">
          <el-select v-model="jobInfo.scheduleType" :placeholder="langData.formSelectPlaceholder" clearable filterable>
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
          <el-select v-model="jobInfo.glueType" :placeholder="langData.formSelectPlaceholder" clearable filterable>
            <el-option :key="item.key" :label="item.value" :value="item.key" v-for="item in glueTypeList"/>
          </el-select>
        </el-form-item>
        <el-form-item :label="langData.taskHandler" prop="executorHandler" v-if="jobInfo.glueType=='BEAN'">
          <el-input v-model="jobInfo.executorHandler" type="text" clearable :placeholder="langData.taskHandlerPlaceholder"/>
        </el-form-item>
        <el-form-item :label="langData.executeParam" prop="executorParam">
          <el-input v-model="jobInfo.executorParam" type="textarea" clearable :rows="5" :placeholder="langData.taskParamsPlaceholder"/>
        </el-form-item>
        <el-divider content-position="left">{{ langData.advancedConfig }}</el-divider>
        <el-form-item :label="langData.routeStrategy" prop="executorRouteStrategy">
          <el-select v-model="jobInfo.executorRouteStrategy" :placeholder="langData.formSelectPlaceholder" clearable filterable>
            <el-option :key="item.key" :label="item.value" :value="item.key" v-for="item in executorRouteStrategyList"/>
          </el-select>
        </el-form-item>
        <el-form-item :label="langData.childTaskId" prop="childJobId">
          <el-input v-model="jobInfo.childJobId" type="text" clearable/>
        </el-form-item>
        <el-form-item :label="langData.misfireStrategy" prop="misfireStrategy">
          <el-select v-model="jobInfo.misfireStrategy" :placeholder="langData.formSelectPlaceholder" clearable filterable>
            <el-option :key="item.key" :label="item.value" :value="item.key" v-for="item in misfireStrategyList"/>
          </el-select>
        </el-form-item>
        <el-form-item :label="langData.blockStrategy" prop="executorBlockStrategy">
          <el-select v-model="jobInfo.executorBlockStrategy" :placeholder="langData.formSelectPlaceholder" clearable filterable>
            <el-option :key="item.key" :label="item.value" :value="item.key" v-for="item in executorBlockStrategyList"/>
          </el-select>
        </el-form-item>
        <el-form-item :label="langData.timeout" prop="executorTimeout">
          <el-input v-model="jobInfo.executorTimeout" type="number" clearable/>
        </el-form-item>
        <el-form-item :label="langData.failRetryCount" prop="executorFailRetryCount">
          <el-input v-model="jobInfo.executorFailRetryCount" type="number" clearable/>
        </el-form-item>
        <el-form-item label="" prop="glueRemark" style="display:none">
          <el-input v-model="jobInfo.glueRemark" type="hidden" clearable/>
        </el-form-item>
      </el-form>
    </el-drawer>

    <!-- 日志清理弹窗 -->
    <el-dialog v-model="dialogFormVisible" :title="langData.logClean" draggable width="440px">
      <el-form :model="logClearForm" label-position="top" :inline="false" ref="formRef" :rules="rules">
        <el-form-item :label="langData.executor" prop="jobGroup">
          <el-select v-model="logClearForm.jobGroup" :placeholder="langData.formSelectPlaceholder" clearable filterable remote automatic-dropdown :remote-method="fetchJobGroup" class="full-width">
            <el-option :key="item.id" :label="item.title" :value="item.id" v-for="item in JobGroupList"/>
          </el-select>
        </el-form-item>
        <el-form-item :label="langData.task" prop="jobId">
          <el-select v-model="logClearForm.jobId" :placeholder="langData.formSelectPlaceholder" clearable filterable remote automatic-dropdown :remote-method="fetchTaskList2" class="full-width">
            <el-option :key="item.id" :label="item.jobDesc" :value="item.id" v-for="item in taskList"/>
          </el-select>
        </el-form-item>
        <el-form-item :label="langData.cleanWay" prop="type">
          <el-select v-model="logClearForm.type" :placeholder="langData.formSelectPlaceholder" clearable filterable automatic-dropdown class="full-width">
            <el-option :key="item.key" :label="item.value" :value="item.key" v-for="item in logClearTypeList"/>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogFormVisible = false">{{ langData.btnCancel }}</el-button>
          <el-button type="warning" @click="updateLogClear(formRef)">{{ langData.btnConfirm }}</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page-back {
  font-size: var(--el-font-size-medium);
  color: var(--el-text-color-regular);
  flex-shrink: 0;
}

.page-back:hover { color: var(--el-color-primary); background: var(--el-fill-color-light); }

.page-divider { width: 1px; height: 14px; background: var(--el-border-color); flex-shrink: 0; }

.data-table :deep(.cell) { white-space: nowrap; }

.row-actions {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 2px;
}

.cell-mono {
  font-family: var(--font-mono);
  font-size: var(--el-font-size-extra-small);
  color: var(--el-text-color-regular);
}

.cell-muted { color: var(--el-text-color-placeholder); }

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

.log-content {
  font-family: var(--font-mono);
  font-size: var(--el-font-size-extra-small);
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-all;
  color: var(--el-text-color-regular);
}

.detail-form :deep(.el-select),
.detail-form :deep(.el-input),
.detail-form :deep(.el-textarea) { width: 100%; }

.full-width { width: 100%; }

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>