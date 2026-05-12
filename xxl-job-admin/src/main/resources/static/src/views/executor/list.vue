<script lang="ts" setup>
import {
  Edit, ArrowLeft, Delete, Plus
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
  <div class="executor-page view-enter">
    <!-- 搜索卡片 -->
    <div class="search-card">
      <div class="card-header">
        <span class="card-title">执行器管理</span>
        <span class="card-badge">{{ total }} 个执行器</span>
      </div>
      <el-form :model="form" size="small" label-position="right" inline-message inline>
        <el-form-item :label="langData.appname" prop="appname">
          <el-input v-model="form.appname" type="text" clearable/>
        </el-form-item>
        <el-form-item :label="langData.appDesc" prop="title">
          <el-input v-model="form.title" type="text" clearable/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="small" @click="query()">{{ langData.btnSearch }}</el-button>
          <el-button type="success" :icon="Plus" circle @click="showAddExecutor" :title="langData.btnAdd"/>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格 -->
    <el-table :data="list" style="width: 100%" table-layout="fixed" :stripe="false"
              size="small" :highlight-current-row="true" class="data-table">
      <el-table-column fixed="left" :label="langData.tableHeaderOp" width="80" header-align="center" align="center">
        <template #default="scope">
          <div class="row-actions">
            <el-icon @click="showEditExecutor(scope.row)" :color="'var(--el-color-primary)'" :size="15" :title="langData.btnEdit"><Edit/></el-icon>
            <el-popconfirm :title="langData.confirmDelete" @confirm="deleteExecutor(scope.row)" icon-color="red" confirm-button-type="danger">
              <template #reference>
                <el-icon color="red" :size="15"><Delete/></el-icon>
              </template>
            </el-popconfirm>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="id" label="ID" header-align="center" align="center" width="80"/>
      <el-table-column prop="appname" :label="langData.executorName" header-align="center" align="center" min-width="160">
        <template #default="{row}">
          <code class="cell-mono">{{ row.appname }}</code>
        </template>
      </el-table-column>
      <el-table-column prop="title" :label="langData.executorDesc" header-align="center" align="center" min-width="160"/>
      <el-table-column prop="fmtAddressType" :label="langData.registerType" header-align="center" align="center" width="110">
        <template #default="{row}">
          <el-tag size="small" effect="plain" type="info">{{ row.fmtAddressType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="addressList" :label="langData.registerAddress" header-align="center" align="center" min-width="160"/>
      <el-table-column prop="fmtUpdateTime" :label="langData.updateTime" header-align="center" align="center"/>
    </el-table>
    <el-pagination class="page" v-model:page-size="form.length" v-model:current-page="form.start"
                   layout="->, total, sizes, prev, pager, next, jumper" v-model:total="total"
                   @size-change="query()" @current-change="query()" @prev-click="query()" @next-click="query()"
                   size="small" :background="true"
                   :page-sizes="[5, 10, 20, 50, 100]"/>
  </div>

  <!-- 新增/编辑执行器 -->
  <el-drawer v-model="drawer" :title="drawerTitle" direction="ltr" size="440px">
    <el-form :model="executorForm" size="small" label-position="right" inline-message :inline="false" label-width="110px"
             ref="formRef" :rules="rules" class="executor-form">
      <el-form-item label="ID" prop="id" v-if="drawerTitle==langData.editExecutor">
        <el-input v-model="executorForm.id" type="text" clearable disabled/>
      </el-form-item>
      <el-form-item :label="langData.executorName" prop="appname">
        <el-input v-model="executorForm.appname" type="text" clearable/>
      </el-form-item>
      <el-form-item :label="langData.executorDesc" prop="title">
        <el-input v-model="executorForm.title" type="text" clearable/>
      </el-form-item>
      <el-form-item :label="langData.registerType" prop="addressType">
        <el-select v-model="executorForm.addressType" :placeholder="langData.formSelectPlaceholder" size="small"
                   clearable filterable
                   @change="executorForm.addressType==0?executorForm.addressList='':executorForm.addressList='http://localhost:8080'">
          <el-option :key="item.key" :label="item.value" :value="item.key" v-for="item in addressTypeList"/>
        </el-select>
      </el-form-item>
      <el-form-item :label="langData.registerAddress" prop="addressList" v-if="executorForm.addressType==1">
        <el-input v-model="executorForm.addressList" type="textarea" :rows="3"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" size="small" @click="updateExecutor(formRef)">{{ langData.btnSave }}</el-button>
      </el-form-item>
    </el-form>
  </el-drawer>
</template>

<style scoped>
/* ========================================
   Executor Page - Layout
   ======================================== */
.executor-page {
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

.search-card .el-form-item {
  margin-bottom: 12px;
}

.search-card .el-input {
  width: 160px;
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
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.row-actions .el-icon {
  transition: all var(--transition-fast);
}

.row-actions .el-icon:hover {
  transform: scale(1.15);
}

.cell-mono {
  font-family: var(--font-mono);
  font-size: 12px;
  font-weight: 500;
  background: var(--surface-inset);
  padding: 2px 8px;
  border-radius: 4px;
  color: var(--el-text-color-regular);
}

/* ========================================
   Drawer Form
   ======================================== */
.executor-form :deep(.el-select) {
  width: 100%;
}
</style>