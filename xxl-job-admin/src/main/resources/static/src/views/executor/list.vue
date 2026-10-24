<script lang="ts" setup>
import {Icon} from '@iconify/vue'
import PageHead from '@/components/admin/PageHead.vue'
import {ref, reactive, onMounted, computed, provide, inject} from 'vue'
import axios from '@/network'
import {msg, notify, encryptRSA, encryptAES, generateRandomAESKey} from '@/utils/Utils'
import {ElMessage, ElMessageBox} from 'element-plus'
import type {FormInstance, FormRules, TableInstance, UploadFile} from 'element-plus'
import router from "@/router";
import {getLangData} from "@/i18n/locale";

const langData = getLangData()
const dictMap = ref<any>({})
const addressTypeList = ref<any[]>([])

const queryDict = () => {
  axios({
    url: '/jobinfo/dict',
    method: 'get'
  }).then((res: any) => {
    if (res.data.state == 'OK') {
      dictMap.value = res.data.body || {}
      addressTypeList.value = dictMap.value['xxl-job-admin,addressType'] || [] as any[]
    } else {
      msg(res.data.errorMessage, 'warning')
    }
  }).catch((err: any) => {
    console.log('', err)
    msg(err?.response.data.errorMessage, 'error')
  })
}

onMounted(() => {
  queryDict()
  query()
});

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
  start: 1,
  length: 10,
  appname: '',
  title: '',
  addressType: 1,
  addressList: ''
})
const total = ref(0)
const list = ref<any[]>()

const query = () => {
  axios({
    url: '/jobgroup/pageList',
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
  if (form.appname) n++
  if (form.title) n++
  return n
}
const resetQuery = () => {
  form.appname = ''
  form.title = ''
  form.start = 1
  query()
}

const drawerFull = ref(false)
const drawerSize = computed(() => (drawerFull.value ? '100%' : '440px'))
const toggleDrawer = () => { drawerFull.value = !drawerFull.value }

const drawer = ref(false)
const drawerTitle = ref(langData.addExecutor)
const executorForm = reactive({
  id: '',
  appname: '',
  title: '',
  addressType: null as any,
  addressList: 'http://localhost:8080'
})
const showAddExecutor = (row: any) => {
  drawer.value = true
  drawerTitle.value = langData.addExecutor
  executorForm.id = ''
  executorForm.appname = ''
  executorForm.title = ''
  executorForm.addressType = '1'
  executorForm.addressList = 'http://localhost:8080'
}

const showEditExecutor = (row: any) => {
  drawer.value = true
  drawerTitle.value = langData.editExecutor
  executorForm.id = row.id
  executorForm.appname = row.appname
  executorForm.title = row.title
  executorForm.addressType = row.addressType+''
  executorForm.addressList = row.addressList
}

const formRef = ref<FormInstance>();
const rules = reactive<FormRules>({
  appname: [{required: true, message: langData.formValidateNotNull, trigger: 'blur'}],
  title: [{required: true, message: langData.formValidateNotNull, trigger: 'blur'}],
  addressType: [{required: true, message: langData.formValidateNotNull, trigger: 'blur'}],
})

const updateExecutor = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid, fields) => {
    if (valid) {
      axios({
        url: drawerTitle.value == langData.addExecutor ? '/jobgroup/save' : '/jobgroup/update',
        method: 'post',
        data: executorForm,
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

const deleteExecutor = (row: any) => {
  axios({
    url: '/jobgroup/remove',
    method: 'delete',
    params: {
      id: row.id
    }
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


</script>

<template>
  <div class="admin-page">
    <PageHead :title="langData.executorPageTitle" :desc="langData.executorPageDesc"/>

    <section class="admin-panel">
      <!-- 查询区 -->
      <div class="admin-filter" v-show="filtersVisible">
        <el-form :model="form" inline-message inline label-position="right" @submit.prevent>
          <el-form-item :label="langData.appname" prop="appname">
            <el-input v-model="form.appname" type="text" clearable :placeholder="langData.formInputPlaceholder"/>
          </el-form-item>
          <el-form-item :label="langData.appDesc" prop="title">
            <el-input v-model="form.title" type="text" clearable :placeholder="langData.formInputPlaceholder"/>
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
          <el-tooltip :show-after="0" :content="langData.btnAdd" placement="bottom">
            <el-button type="primary" circle :aria-label="langData.btnAdd" @click="showAddExecutor">
              <Icon icon="lucide:plus"/>
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
        <el-table :data="list" class="data-table" table-layout="fixed" highlight-current-row v-loading="false">
          <el-table-column fixed="left" :label="langData.tableHeaderOp" width="90" header-align="center" align="center">
            <template #default="scope">
              <div class="row-actions">
                <el-tooltip :show-after="500" :content="langData.btnEdit" placement="top">
                  <el-button link type="primary" class="admin-op" @click="showEditExecutor(scope.row)">
                    <Icon icon="lucide:pencil"/>
                  </el-button>
                </el-tooltip>
                <el-popconfirm :title="langData.confirmDelete" @confirm="deleteExecutor(scope.row)" confirm-button-type="danger">
                  <template #reference>
                    <el-tooltip :show-after="500" :content="langData.btnDelete" placement="top">
                      <el-button link type="danger" class="admin-op" :aria-label="langData.btnDelete">
                        <Icon icon="lucide:trash-2"/>
                      </el-button>
                    </el-tooltip>
                  </template>
                </el-popconfirm>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="id" label="ID" header-align="center" align="center" width="80"/>
          <el-table-column prop="appname" :label="langData.executorName" header-align="left" align="left" min-width="180">
            <template #default="{row}">
              <span class="cell-mono">{{ row.appname }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="title" :label="langData.executorDesc" header-align="left" align="left" min-width="180"/>
          <el-table-column prop="fmtAddressType" :label="langData.registerType" header-align="center" align="center" width="120">
            <template #default="{row}">
              <el-tag effect="light" type="info" round>{{ row.fmtAddressType }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="addressList" :label="langData.registerAddress" header-align="left" align="left" min-width="200" show-overflow-tooltip/>
          <el-table-column prop="fmtUpdateTime" :label="langData.updateTime" header-align="center" align="center" width="180"/>
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

    <!-- 新增/编辑执行器 -->
    <el-drawer v-model="drawer" :size="drawerSize" direction="ltr">
      <template #header>
        <div class="drawer-head">
          <span class="drawer-head__title">{{ drawerTitle }}</span>
          <el-tooltip :show-after="0" :content="drawerFull ? langData.drawerRestore : langData.drawerMaximize" placement="bottom">
            <el-button link class="drawer-head__btn" :aria-label="langData.drawerMaximize" @click="toggleDrawer">
              <Icon :icon="drawerFull ? 'lucide:minimize-2' : 'lucide:maximize-2'"/>
            </el-button>
          </el-tooltip>
        </div>
      </template>
      <el-form :model="executorForm" label-position="top" :inline="false" ref="formRef" :rules="rules" class="executor-form">
        <el-form-item label="ID" prop="id" v-if="drawerTitle == langData.editExecutor">
          <el-input v-model="executorForm.id" type="text" clearable disabled/>
        </el-form-item>
        <el-form-item :label="langData.executorName" prop="appname">
          <el-input v-model="executorForm.appname" type="text" clearable/>
        </el-form-item>
        <el-form-item :label="langData.executorDesc" prop="title">
          <el-input v-model="executorForm.title" type="text" clearable/>
        </el-form-item>
        <el-form-item :label="langData.registerType" prop="addressType">
          <el-select v-model="executorForm.addressType" :placeholder="langData.formSelectPlaceholder" clearable filterable
                     @change="executorForm.addressType==0?executorForm.addressList='':executorForm.addressList='http://localhost:8080'">
            <el-option :key="item.key" :label="item.value" :value="item.key" v-for="item in addressTypeList"/>
          </el-select>
        </el-form-item>
        <el-form-item :label="langData.registerAddress" prop="addressList" v-if="executorForm.addressType==1">
          <el-input v-model="executorForm.addressList" type="textarea" :rows="3"/>
        </el-form-item>
        <div class="admin-form-actions">
          <el-button @click="drawer = false">{{ langData.btnCancel }}</el-button>
          <el-button type="primary" @click="updateExecutor(formRef)">{{ langData.btnSave }}</el-button>
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
  gap: 2px;
}

.cell-mono {
  font-family: var(--font-mono);
  font-size: var(--el-font-size-extra-small);
  color: var(--el-text-color-regular);
}

.executor-form :deep(.el-select),
.executor-form :deep(.el-input),
.executor-form :deep(.el-textarea) {
  width: 100%;
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
</style>