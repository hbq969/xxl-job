<script lang="ts" setup>
import {computed, onMounted, reactive, ref} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {Icon} from '@iconify/vue'
import type {FormInstance, FormRules} from 'element-plus'
import axios from '@/network'
import {msg} from '@/utils/Utils'
import {getLangData} from '@/i18n/locale'
import PageHead from '@/components/admin/PageHead.vue'

const langData = getLangData()
const route = useRoute()
const router = useRouter()

const isEdit = computed(() => !!route.query.id)
const isCopy = computed(() => route.query.mode === 'copy')
const pageTitle = computed(() => (isEdit.value ? langData.editTask : langData.addTask))

const formRef = ref<FormInstance>()
const submitting = ref(false)

const taskForm = reactive({
  id: null as any,
  jobGroup: null as any,
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

const rules = reactive<FormRules>({
  jobGroup: [{required: true, message: langData.formValidateNotNull, trigger: 'blur'}],
  jobDesc: [{required: true, message: langData.formValidateNotNull, trigger: 'blur'}],
  alarmEmail: [{required: true, message: langData.formValidateNotNull, trigger: 'blur'}],
  scheduleType: [{required: true, message: langData.formValidateNotNull, trigger: 'blur'}],
  scheduleConf: [{required: true, message: langData.formValidateNotNull, trigger: 'blur'}],
  glueType: [{required: true, message: langData.formValidateNotNull, trigger: 'blur'}],
  executorRouteStrategy: [{required: true, message: langData.formValidateNotNull, trigger: 'blur'}],
})

const scheduleTypeList = ref<any[]>([])
const glueTypeList = ref<any[]>([])
const executorRouteStrategyList = ref<any[]>([])
const misfireStrategyList = ref<any[]>([])
const executorBlockStrategyList = ref<any[]>([])
const JobGroupList = ref<any[]>([])

const queryDict = () => {
  axios({url: '/jobinfo/dict', method: 'get'}).then((res: any) => {
    if (res.data.state == 'OK') {
      const dictMap = res.data.body || {}
      scheduleTypeList.value = dictMap['xxl-job-admin,scheduleType'] || []
      glueTypeList.value = dictMap['xxl-job-admin,glueType'] || []
      executorRouteStrategyList.value = dictMap['xxl-job-admin,executorRouteStrategy'] || []
      misfireStrategyList.value = dictMap['xxl-job-admin,misfireStrategy'] || []
      executorBlockStrategyList.value = dictMap['xxl-job-admin,executorBlockStrategy'] || []
    } else {
      msg(res.data.errorMessage, 'warning')
    }
  }).catch((err: any) => {
    console.log('', err)
    msg(err?.response?.data?.errorMessage, 'error')
  })
}

const fetchJobGroup = () => {
  axios({url: '/user/permission/list', method: 'get'}).then((res: any) => {
    if (res.data.state == 'OK') {
      JobGroupList.value = res.data.body || []
    }
  }).catch(() => {
    JobGroupList.value = []
  })
}

const resetForm = () => {
  taskForm.id = null
  taskForm.jobGroup = null
  taskForm.jobDesc = ''
  taskForm.author = ''
  taskForm.alarmEmail = 'foo@bar.com'
  taskForm.scheduleType = 'CRON'
  taskForm.scheduleConf = '0 */30 * * * ?'
  taskForm.glueType = 'BEAN'
  taskForm.executorHandler = ''
  taskForm.executorParam = ''
  taskForm.executorRouteStrategy = 'FIRST'
  taskForm.childJobId = ''
  taskForm.misfireStrategy = 'FIRE_ONCE_NOW'
  taskForm.executorBlockStrategy = 'SERIAL_EXECUTION'
  taskForm.executorTimeout = 5
  taskForm.executorFailRetryCount = 0
}

const fillForm = (row: any) => {
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

const loadTaskInfo = (id: string) => {
  axios({url: '/jobinfo/info', method: 'get', params: {id}}).then((res: any) => {
    if (res.data.state == 'OK') {
      fillForm(res.data.body || {})
      if (isCopy.value) taskForm.id = null
    } else {
      msg(res.data.errorMessage, 'warning')
    }
  }).catch((err: any) => {
    console.log('', err)
    msg(err?.response?.data?.errorMessage, 'error')
  })
}

const goBack = () => router.push({path: '/task/list'})

const save = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid) => {
    if (!valid) return
    submitting.value = true
    axios({
      url: isEdit.value ? '/jobinfo/update' : '/jobinfo/add',
      method: 'post',
      data: taskForm,
    }).then((res: any) => {
      if (res.data.state == 'OK') {
        msg(res.data.body, 'success')
        goBack()
      } else {
        msg(res.data.errorMessage, 'warning')
      }
    }).catch((err: any) => {
      console.log('', err)
      msg(err?.response?.data?.errorMessage, 'error')
    }).finally(() => {
      submitting.value = false
    })
  })
}

const scrollToSection = (id: string) => {
  const el = document.getElementById(id)
  if (el) el.scrollIntoView({behavior: 'smooth', block: 'start'})
}

/** 页面高度按「最外层可见窗口」计算：门户里 iframe 被内容撑高，用 100vh 会超出屏幕导致左侧导航被滚走 */
const applyPageHeight = () => {
  let vh = window.innerHeight
  try {
    const host: any = (window.parent && window.parent !== window) ? window.parent : window
    vh = host.innerHeight || vh
  } catch (e) {
    vh = window.innerHeight
  }
  // 门户顶部标题栏 + 标签栏约 114px，由 h-sm 统一预留
  const h = Math.max(420, vh - 114)
  document.documentElement.style.setProperty('--te-page-h', h + 'px')
}

onMounted(() => {
  applyPageHeight()
  window.addEventListener('resize', applyPageHeight)
  queryDict()
  fetchJobGroup()
  resetForm()
  const id = route.query.id as string
  const fromId = route.query.fromId as string
  if (id) loadTaskInfo(id)
  else if (fromId) loadTaskInfo(fromId)
})
</script>

<template>
  <div class="admin-page task-edit-page">
    <PageHead :title="pageTitle" :desc="langData.taskPageDesc">
      <template #lead>
        <el-tooltip :show-after="0" :content="langData.btnBack" placement="bottom">
          <el-button class="page-back" circle text :aria-label="langData.btnBack" @click="goBack">
            <Icon icon="lucide:circle-arrow-left"/>
          </el-button>
        </el-tooltip>
        <span class="page-divider"></span>
      </template>
      <template #actions>
        <span v-if="isEdit" class="te-chip">ID {{ taskForm.id }}</span>
      </template>
    </PageHead>

    <section class="admin-panel te-shell">
      <div class="te">
        <!-- 左侧步骤导航（sticky 固定） -->
        <aside class="te-nav">
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
        </aside>

        <!-- 右侧表单 -->
        <div class="te-main">
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
                    <el-select v-model="taskForm.jobGroup" :placeholder="langData.formSelectPlaceholder" clearable filterable>
                      <el-option :key="item.id" :label="item.title+'('+item.addressList+')'" :value="item.id" v-for="item in JobGroupList"/>
                    </el-select>
                  </el-form-item>
                  <el-form-item :label="langData.taskDesc" prop="jobDesc">
                    <el-input v-model="taskForm.jobDesc" type="text" clearable :placeholder="langData.taskDescPlaceholder"/>
                  </el-form-item>
                  <el-form-item :label="langData.alarmEmail" prop="alarmEmail">
                    <el-input v-model="taskForm.alarmEmail" type="text" clearable :placeholder="langData.alarmEmailPlaceholder"/>
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
                </div>
              </section>
            </el-form>
          </div>

          <!-- 底部固定操作条 -->
          <footer class="te-foot">
            <span class="te-foot__hint">带 * 为必填项</span>
            <div class="te-foot__actions">
              <el-button @click="goBack">{{ langData.btnCancel }}</el-button>
              <el-button type="primary" :loading="submitting" @click="save(formRef)">{{ langData.btnSave }}</el-button>
            </div>
          </footer>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.task-edit-page {
  height: var(--te-page-h, calc(100vh - 114px));
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding-bottom: 10px;
  overflow: hidden;
}

.te-shell {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
  padding: 0;
  overflow: hidden;
}

.page-back {
  font-size: var(--el-font-size-medium);
  color: var(--el-text-color-regular);
  flex-shrink: 0;
}

.page-back:hover { color: var(--el-color-primary); background: var(--el-fill-color-light); }

.page-divider { width: 1px; height: 14px; background: var(--el-border-color); flex-shrink: 0; }

.te {
  flex: 1;
  min-height: 0;
  display: grid;
  grid-template-columns: 248px minmax(0, 1fr);
}

/* 左侧导航：粘在页面顶部，不随右侧表单滚动 */
/* 左侧导航：占满列高、独立滚动，右侧内容滚动时它不动 */
.te-nav {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 16px 14px;
  border-right: 1px solid var(--el-border-color-lighter);
  background: var(--el-fill-color-light);
  min-height: 0;
  overflow: auto;
  overscroll-behavior: contain;
}

.te-nav__list { list-style: none; margin: 0; padding: 0; display: flex; flex-direction: column; gap: 2px; }

.te-nav__item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 9px 10px;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.te-nav__item:hover { background: var(--el-fill-color); }

.te-nav__item.is-active { background: var(--el-color-primary-light-9); }

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

.te-nav__name { font-size: var(--el-font-size-small); font-weight: 600; color: var(--el-text-color-primary); }

.te-nav__desc { font-size: var(--el-font-size-extra-small); line-height: 1.4; color: var(--el-text-color-secondary); }

.te-nav__foot {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 12px;
  padding-top: 12px;
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

.te-main {
  display: flex;
  flex-direction: column;
  min-width: 0;
  min-height: 0;
}

/* 右侧表单区是唯一滚动区域 */
.te-body {
  flex: 1;
  min-height: 0;
  overflow: auto;
  overscroll-behavior: contain;
  padding: 6px 20px 12px;
}

.te-sec { padding: 16px 0 4px; }

.te-sec + .te-sec { border-top: 1px solid var(--el-border-color-lighter); }

.te-sec__head { display: flex; align-items: baseline; gap: 8px; margin-bottom: 12px; }

.te-sec__num { font-family: var(--font-mono); font-size: var(--el-font-size-extra-small); font-weight: 700; color: var(--el-color-primary); }

.te-sec__title { font-size: var(--el-font-size-base); font-weight: 600; color: var(--el-text-color-primary); }

.te-sec__desc { font-size: var(--el-font-size-extra-small); color: var(--el-text-color-secondary); }

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

/* 底部操作条：粘在视口底部，始终可点 */
.te-foot {
  flex: none;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 20px;
  border-top: 1px solid var(--el-border-color);
  background: var(--el-bg-color);
}

.te-foot__hint { font-size: var(--el-font-size-extra-small); color: var(--el-text-color-placeholder); }

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
