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
  <div class="admin-page">
    <PageHead :title="langData.permPageTitle" :desc="langData.permPageDesc"/>

    <section class="admin-panel">
      <div class="admin-toolbar">
        <div class="tb-left">
          <el-tooltip :show-after="0" :content="langData.btnRefresh" placement="bottom">
            <el-button class="admin-toolbar__icon" circle text :aria-label="langData.btnRefresh" @click="query()">
              <Icon icon="lucide:refresh-ccw"/>
            </el-button>
          </el-tooltip>
        </div>
      </div>

      <div class="admin-table">
        <el-table :data="list" class="data-table" table-layout="fixed" highlight-current-row>
          <el-table-column fixed="left" :label="langData.tableHeaderOp" width="90" header-align="center" align="center">
            <template #default="scope">
              <div class="row-actions">
                <el-tooltip :show-after="500" :content="langData.permConfig" placement="top">
                  <el-button link type="primary" class="admin-op" @click="showUserPermDialog(scope.row)">
                    <Icon icon="lucide:shield-check"/>
                  </el-button>
                </el-tooltip>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="username" :label="langData.username" :show-overflow-tooltip="true"
                           header-align="left" align="left" min-width="160"/>
          <el-table-column :label="langData.roleName" :show-overflow-tooltip="true" header-align="left" align="left" min-width="160">
            <template #default="{row}">
              <span>{{ row.roleNames?.join(', ') }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="fmtAccStatus" :label="langData.triggerStatus" :show-overflow-tooltip="true"
                           header-align="center" align="center" width="140">
            <template #default="{row}">
              <el-tag effect="light" round :type="row.accStatus==1?'success':'info'">{{ row.fmtAccStatus }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="fmtCreatedAt" :label="langData.tableHeaderCreateTime" :show-overflow-tooltip="true"
                           header-align="center" align="center" width="180"/>
        </el-table>
      </div>

      <div class="admin-foot">
        <el-pagination v-model:page-size="pg.pageSize" v-model:current-page="pg.pageNum"
                       layout="total, sizes, prev, pager, next, jumper" v-model:total="total"
                       @size-change="query()" @current-change="query()" @prev-click="query()" @next-click="query()"
                       :background="true" :page-sizes="[10, 20, 50, 100]"/>
      </div>
    </section>

    <!-- 权限配置弹窗 -->
    <el-dialog v-model="dialogFormVisible" :title="langData.permConfig" draggable width="460px">
      <el-form :model="permForm" label-position="top" :inline="false" ref="formRef" :rules="rules">
        <el-form-item :label="langData.executor" prop="groupIds">
          <el-select v-model="permForm.groupIds" :placeholder="langData.formSelectPlaceholder" clearable
                     filterable multiple class="full-width">
            <el-option :key="item.id" :label="item.title" :value="item.id" v-for="item in jobGroupList"/>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogFormVisible = false">{{ langData.btnCancel }}</el-button>
          <el-button type="primary" @click="updateUserPerm(formRef)">{{ langData.btnSave }}</el-button>
        </div>
      </template>
    </el-dialog>
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

.full-width { width: 100%; }

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>