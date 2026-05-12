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
  <div class="perm-page view-enter">
    <!-- 用户表格 -->
    <el-table :data="list" style="width: 100%" table-layout="fixed" :stripe="false"
              size="small" :highlight-current-row="true" class="data-table">
      <el-table-column fixed="left" :label="langData.tableHeaderOp" width="80" header-align="center" align="center">
        <template #default="scope">
          <div class="row-actions">
            <el-tooltip :content="langData.permConfig" effect="dark" placement="top">
              <el-icon @click="showUserPermDialog(scope.row)" :color="'var(--el-color-primary)'" :size="15"><UserFilled/></el-icon>
            </el-tooltip>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="username" :label="langData.username" :show-overflow-tooltip="true" header-align="center" align="center" min-width="140"/>
      <el-table-column prop="roleName" :label="langData.roleName" :show-overflow-tooltip="true" header-align="center" align="center" min-width="130"/>
      <el-table-column prop="fmtAccStatus" :label="langData.triggerStatus" :show-overflow-tooltip="true" header-align="center" align="center" width="130">
        <template #default="{row}">
          <el-tag size="small" effect="plain" :type="row.accStatus==1?'success':'info'">{{ row.fmtAccStatus }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="fmtCreatedAt" :label="langData.tableHeaderCreateTime" :show-overflow-tooltip="true" header-align="center" align="center" width="150"/>
    </el-table>
    <el-pagination class="page" v-model:page-size="pg.pageSize" v-model:current-page="pg.pageNum"
                   layout="->, total, sizes, prev, pager, next, jumper" v-model:total="total"
                   @size-change="query()" @current-change="query()" @prev-click="query()" @next-click="query()"
                   size="small" :background="true"
                   :page-sizes="[5, 10, 20, 50, 100]"/>
  </div>

  <!-- 权限配置弹窗 -->
  <el-dialog v-model="dialogFormVisible" :title="langData.permConfig" draggable width="420px">
    <el-form :model="permForm" label-position="right" size="small" :inline="false" ref="formRef" :rules="rules" label-width="80px">
      <el-form-item :label="langData.executor" prop="jobGroup">
        <el-select v-model="permForm.groupIds" :placeholder="langData.formSelectPlaceholder" size="small" clearable
                   filterable multiple>
          <el-option :key="item.id" :label="item.title" :value="item.id" v-for="item in jobGroupList"/>
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button size="small" @click="dialogFormVisible = false">{{ langData.btnCancel }}</el-button>
        <el-button type="primary" size="small" @click="updateUserPerm(formRef)">{{ langData.btnSave }}</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<style scoped>
/* ========================================
   Permission Page - Layout
   ======================================== */
.perm-page {
  flex-grow: 1;
  padding: 24px;
  overflow: auto;
  width: 100%;
  animation: fadeInUp 0.35s cubic-bezier(0.4, 0, 0.2, 1) both;
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

/* ========================================
   Dialog
   ======================================== */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>