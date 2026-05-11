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
  <div class="container">
    <el-form :model="form" size="small" label-position="right" inline-message inline>
      <el-form-item :label="langData.appname" prop="appname" style="width: 180px">
        <el-input v-model="form.appname" type="text" clearable/>
      </el-form-item>
      <el-form-item :label="langData.appDesc" prop="title" style="width: 180px">
        <el-input v-model="form.title" type="text" clearable/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" size="small" @click="query()">{{ langData.btnSearch }}</el-button>
        <el-button type="success" :icon="Plus" circle @click="showAddExecutor" :title="langData.btnAdd"/>
      </el-form-item>
    </el-form>
    <el-table :data="list" style="width: 100%" table-layout="fixed" :stripe="true"
              size="small" :highlight-current-row="true">
      <!--      <el-table-column type="selection" header-align="center" align="center"/>-->
      <el-table-column fixed="left" :label="langData.tableHeaderOp" width="80" header-align="center" align="center">
        <template #default="scope">
          <el-icon @click="showEditExecutor(scope.row)" :color="'var(--el-color-primary)'" style="cursor: pointer; margin-left: 10px"
                   :size="14">
            <Edit/>
          </el-icon>
          <el-popconfirm :title="langData.confirmDelete" @confirm="deleteExecutor(scope.row)"
                         icon-color="red"
                         confirm-button-type="danger">
            <template #reference>
              <el-icon @click="" color="red" style="cursor: pointer; margin-left: 10px" :size="14">
                <Delete/>
              </el-icon>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
      <el-table-column prop="id" label="ID" :show-overflow-tooltip="true" header-align="center"
                       align="center"/>
      <el-table-column prop="appname" :label="langData.executorName" :show-overflow-tooltip="true" header-align="center"
                       align="center"/>
      <el-table-column prop="title" :label="langData.executorDesc" :show-overflow-tooltip="true" header-align="center"
                       align="center"/>
      <el-table-column prop="fmtAddressType" :label="langData.registerType" :show-overflow-tooltip="true" header-align="center"
                       align="center"/>
      <el-table-column prop="addressList" :label="langData.registerAddress" :show-overflow-tooltip="true" header-align="center"
                       align="center"/>
      <el-table-column prop="fmtUpdateTime" :label="langData.updateTime" :show-overflow-tooltip="true" header-align="center"
                       align="center"/>
    </el-table>
    <el-pagination class="page" v-model:page-size="form.length" v-model:current-page="form.start"
                   layout="->, total, sizes, prev, pager, next, jumper" v-model:total="total"
                   @size-change="query()"
                   @current-change="query()" @prev-click="query()" @next-click="query()"
                   size="small" :background="true"
                   :page-sizes="[5, 10, 20, 50, 100]"/>
  </div>

  <el-drawer
      v-model="drawer"
      :title="drawerTitle"
      direction="ltr"
      size="40%"
  >
    <el-form :model="executorForm" size="small" label-position="right" inline-message :inline="false" label-width="20%"
             ref="formRef" :rules="rules">
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
                   clearable filterable style="width: 100%"
                   @change="executorForm.addressType==0?executorForm.addressList='':executorForm.addressList='http://localhost:8080'">
          <el-option :key="item.key" :label="item.value" :value="item.key" v-for="item in addressTypeList"/>
        </el-select>
      </el-form-item>
      <el-form-item :label="langData.registerAddress" prop="addressList" v-if="executorForm.addressType==1">
        <el-input v-model="executorForm.addressList" type="textarea"
                  :rows="3" style="width: 100%"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" size="small" @click="updateExecutor(formRef)">{{ langData.btnSave }}</el-button>
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

.addBtn {
  margin-top: 5px;
  text-align: center;
}
</style>