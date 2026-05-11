<script lang="ts" setup>
import {
  Edit, ArrowLeft, UserFilled
} from '@element-plus/icons-vue'
import {ref, reactive, onMounted, computed, provide, inject} from 'vue'
import axios from '@/network'
import {msg, notify, encryptRSA, encryptAES, generateRandomAESKey} from '@/utils/Utils'
import {ElMessage, ElMessageBox} from 'element-plus'
import type {FormInstance, FormRules, TableInstance, UploadFile} from 'element-plus'
import router from "@/router";
import {getLangData} from "@/i18n/locale";

const langData = getLangData()

onMounted(() => {
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

const total = ref(0)
const list = ref<any[]>([])
const pg = reactive({
  pageNum: 1,
  pageSize: 10
})

const query = () => {
  axios({
    url: '/user/list',
    method: 'get',
    params: pg
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
const permForm = reactive({
  userName: '',
  groupIds: [] as any[]
})
const formRef = ref<FormInstance>();
const rules = reactive<FormRules>({
  groupIds: [{required: true, message: langData.formValidateNotNull, trigger: 'blur'}],
})
const showUserPermDialog = (row: any) => {
  dialogFormVisible.value = true
  permForm.userName = row.username
  formRef.value?.clearValidate()
  fetchJobGroup()
  fetchUserPermission(row)
}

const fetchUserPermission = (row: any) => {
  axios({
    url: '/user/permission/list/' + row.username,
    method: 'get'
  }).then((res: any) => {
    if (res.data.state == 'OK') {
      permForm.groupIds = res.data.body || [] as number[]
    } else {
      msg(res.data.errorMessage, 'warning')
    }
  }).catch((err: any) => {
    console.log('', err)
    msg(err?.response.data.errorMessage, 'error')
  })
}

const updateUserPerm = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid, fields) => {
    if (valid) {
      axios({
        url: '/user/permission',
        method: 'post',
        data: permForm
      }).then((res: any) => {
        if (res.data.state == 'OK') {
          msg(res.data.body, 'success')
          dialogFormVisible.value = false
        } else {
          msg(res.data.errorMessage, 'success')
        }
      }).catch((err: any) => {
        console.log('', err)
        msg(err?.response.data.errorMessage, 'error')
      })
    }
  })
}

const jobGroupList = ref<any[]>([])
const fetchJobGroup = () => {
  axios({
    url: '/user/permission/list',
    method: 'get',
  }).then((res: any) => {
    if (res.data.state == 'OK') {
      jobGroupList.value = res.data.body || []
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
    <el-table :data="list" style="width: 100%" table-layout="fixed" :stripe="true"
              size="small" :highlight-current-row="true">
<!--      <el-table-column type="selection" header-align="center" align="center"/>-->
      <el-table-column fixed="left" :label="langData.tableHeaderOp" width="80" header-align="center" align="center">
        <template #default="scope">
          <el-tooltip :content="langData.permConfig" effect="dark" placement="top">
            <el-icon @click="showUserPermDialog(scope.row)" :color="'var(--el-color-primary)'" style="cursor: pointer; margin-left: 10px"
                     :size="14">
              <UserFilled/>
            </el-icon>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column prop="username" :label="langData.username" :show-overflow-tooltip="true" header-align="center"
                       align="center"/>
      <el-table-column prop="roleName" :label="langData.roleName" :show-overflow-tooltip="true" header-align="center"
                       align="center"/>
      <el-table-column prop="fmtAccStatus" :label="langData.triggerStatus" :show-overflow-tooltip="true" header-align="center"
                       align="center"/>
      <el-table-column prop="fmtCreatedAt" :label="langData.tableHeaderCreateTime" :show-overflow-tooltip="true" header-align="center"
                       align="center"/>
    </el-table>
    <el-pagination class="page" v-model:page-size="pg.pageSize" v-model:current-page="pg.pageNum"
                   layout="->, total, sizes, prev, pager, next, jumper" v-model:total="total"
                   @size-change="query()"
                   @current-change="query()" @prev-click="query()" @next-click="query()"
                   size="small" :background="true"
                   :page-sizes="[5, 10, 20, 50, 100]"/>
  </div>

  <el-dialog v-model="dialogFormVisible" :title="langData.permConfig" draggable width="30%">
    <el-form :model="permForm" label-position="right" size="small" :inline="false" ref="formRef" :rules="rules"
             label-width="20%">
      <el-form-item :label="langData.executor" prop="jobGroup">
        <el-select v-model="permForm.groupIds" :placeholder="langData.formSelectPlaceholder" size="small" clearable
                   filterable style="width: 100%" multiple>
          <el-option :key="item.id" :label="item.title" :value="item.id" v-for="item in jobGroupList"/>
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
              <span class="dialog-footer">
                <el-button @click="dialogFormVisible = false">{{ langData.btnCancel }}</el-button>
                <el-button type="primary" @click="updateUserPerm(formRef)">{{ langData.btnSave }}</el-button>
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