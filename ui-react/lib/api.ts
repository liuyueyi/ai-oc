import axios from "axios"

const BASE_URL = "http://localhost:8080"

const api = axios.create({
  baseURL: BASE_URL,
  timeout: 360000,
})

// 全局请求拦截器，自动带上 X-OC-TOKEN
api.interceptors.request.use((config) => {
  if (typeof window !== 'undefined') {
    const token = localStorage.getItem('oc-token');
    if (token) {
      config.headers = config.headers || {};
      config.headers['X-OC-TOKEN'] = token;
    }

  }
  return config;
});


/**
 * 获取微信扫码登录 SSE 订阅 URL
 */
export function getWxSseUrl() {
  return `${BASE_URL}/api/wx/subscribe`;
}

/**
 * 微信扫码登录 callback
 * @param xml xml 字符串
 * @returns Promise<void>
 */
export async function postWxCallback(xml: string): Promise<void> {
  await api.post("/api/wx/callback", xml, {
    headers: { "content-type": "application/xml" },
  });
}

export interface JobListQuery {
  companyName?: string
  companyType?: string
  jobLocation?: string
  recruitmentType?: string
  recruitmentTarget?: string
  position?: string
  deliveryProgress?: string
  state?: number
  page?: number
  size?: number
}

export interface JobListResponse {
  list: any[]
  hasMore: boolean
  page: number
  size: number
  total: number
  online?: number
}

export async function fetchJobList(params?: JobListQuery): Promise<JobListResponse> {
  const res = await api.get("/api/oc/list", { params })
  if (res.data && res.data.code === 0) {
    // 将外层的在线人数写到内部
    res.data.data.online = res.data.online
    return res.data.data
  }
  throw new Error(res.data?.msg || "获取岗位列表失败")
}


export async function jobDetail(id: number) {
  const res = await api.get(`/api/oc/detail?id=${id}`)
  if (res.data && res.data.code === 0) {
    return res.data.data
  }
  throw new Error(res.data?.msg || "获取岗位信息失败")
}


export async function fetchAdminJobList(params?: JobListQuery): Promise<JobListResponse> {
  const res = await api.get("/api/admin/oc/list", { params })
  if (res.data && res.data.code === 0) {
    return res.data.data
  }
  throw new Error(res.data?.msg || "获取岗位列表失败")
}

export async function submitOcEntry(params: any) {
  const res = await api.post("/api/admin/oc/save", params)
  if (res.data && res.data.code === 0) {
    return res.data.data
  }
  throw new Error(res.data?.msg || "提交岗位信息失败")
}

export async function updateOcState(params: { id: number, state: number }) {
  console.log('这里啦');
  const res = await api.get(`/api/admin/oc/updateState?id=${params.id}&state=${params.state}`)
  if (res.data && res.data.code === 0) {
    return res.data.data
  }
  throw new Error(res.data?.msg || "更新岗位状态失败")
}


//  ---------------------------- gather 相关


function getSubmitPath(async: boolean) {
  if (async) {
    // 异步执行
    return "/api/admin/gather/asyncSubmit"
  } else {
    // 同步执行
    return "/api/admin/gather/submit"
  }
}


export async function submitAIEntry(params: { content: string; model: string; type: string, file: any }) {
  const async = true;
  if (params.file) {
    // 传文件的方式
    const formData = new FormData();
    formData.append("file", params.file);
    formData.append("model", params.model);
    formData.append("type", params.type);
    const ans = await api.post(getSubmitPath(async), formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
    if (ans.data && ans.data.code === 0) {
      return ans.data.data
    } else {
      throw new Error(ans.data?.msg || "AI录入失败")
    }
  }

  const res = await api.post(getSubmitPath(async), params, {
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
  })
  if (res.data && res.data.code === 0) {
    return res.data.data
  }
  throw new Error(res.data?.msg || "AI录入失败")
}


export interface TaskListQuery {
  page?: number;
  size?: number;
  taskId?: number;
  model?: string;
  type?: number;
  state?: number;
}

export interface TaskListItem {
  taskId: number;
  type: number;
  model: string;
  state: number;
  content: string;
  cnt: number;
  result: string;
  processTime: string;
  createTime: string;
  updateTime: string;
}

export interface TaskListResponse {
  list: TaskListItem[];
  hasMore: boolean;
  page: number;
  size: number;
  total: number;
}

export async function fetchTaskList(params: TaskListQuery): Promise<TaskListResponse> {
  const res = await api.post("/api/admin/gather/list", params, {
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
  });
  if (res.data && res.data.code === 0) {
    return res.data.data;
  }
  throw new Error(res.data?.msg || "获取任务列表失败");
}


export async function reRunTask(taskId: number): Promise<boolean> {
  const res = await api.get(`/api/admin/gather/reRun?taskId=${taskId}`);
  if (res.data && res.data.code === 0) {
    return res.data.data === true;
  }
  throw new Error(res.data?.msg || "重跑任务失败");
}


export interface DraftListQuery {
  page?: number
  size?: number
  companyName?: string
  companyType?: string
  jobLocation?: string
  recruitmentType?: string
  recruitmentTarget?: string
  position?: string
  lastUpdatedTimeAfter?: number
  lastUpdatedTimeBefore?: number
  state?: number,
  toProcess?: string,
}

export interface DraftItem {
  id: number
  companyName: string
  companyType: string
  jobLocation: string
  recruitmentType: string
  recruitmentTarget: string
  position: string
  deliveryProgress: string
  lastUpdatedTime: string
  deadline: string
  relatedLink: string
  jobAnnouncement: string
  internalReferralCode: string
  remarks: string
  state: number
  toProcess: number
  createTime: string
  updateTime: string
}

export interface DraftListResponse {
  list: DraftItem[];
  total: number;
}

export async function fetchDraftList(params: DraftListQuery): Promise<DraftListResponse> {
  const res = await api.get("/api/admin/draft/list", { params })
  if (res.data && res.data.code === 0) {
    // 兼容 data 直接为数组或为对象
    if (Array.isArray(res.data.data)) {
      return { list: res.data.data, total: res.data.data?.length }
    }
    return res.data.data
  }
  throw new Error(res.data?.msg || "获取草稿列表失败")
}

/**
 * 批量发布草稿
 * @param ids 草稿 id 数组
 * @returns 发布结果
 */
export async function batchPublishDrafts(ids: number[]): Promise<void> {
  const res = await api.post("/api/admin/draft/toOc", ids)
  if (res.data && res.data.code === 0) {
    return
  }
  throw new Error(res.data?.msg || "发布失败")
}

/**
 * 更新草稿
 * @param draft DraftItem
 * @returns 是否成功
 */
export async function updateDraft(draft: DraftItem): Promise<boolean> {
  const res = await api.post("/api/admin/draft/update", draft)
  if (res.data && res.data.code === 0) {
    return true
  }
  throw new Error(res.data?.msg || "草稿更新失败")
}


/**
 * 删除草稿数据
 * @param id 
 * @returns 
 */
export async function deleteDraft(id: number): Promise<boolean> {
  const res = await api.get("/api/admin/draft/delete?draftId=" + id)
  if (res.data && res.data.code === 0) {
    return true
  }
  throw new Error(res.data?.msg || "草稿删除失败")
}



// -------------------------- 用户相关

export interface UserListQuery {
  userId?: number;
  displayName?: string;
  role?: number;
  page?: number;
  size?: number;
}

export interface UserListItem {
  userId: number;
  displayName: string;
  avatar: string;
  wxId: string;
  role: number;
  state: number;
  email: string;
  intro: string;
  expireTime: number | null;
  createTime: number;
  updateTime: number;
}

export interface UserListResponse {
  list: UserListItem[];
  hasMore: boolean;
  page: number;
  size: number;
  total: number;
}

export async function fetchUserList(params?: UserListQuery): Promise<UserListResponse> {
  const res = await api.get("/api/admin/user/list", { params });
  if (res.data && res.data.code === 0) {
    return res.data.data;
  }
  throw new Error(res.data?.msg || "获取用户列表失败");
}

/**
 * 更新用户角色
 * @param params 包含 userId, role, expireTime
 * @returns 是否成功
 */
export async function updateUserRole(params: { userId: number; role: number; expireTime: number }): Promise<boolean> {
  const formData = new URLSearchParams();
  formData.append('userId', String(params.userId));
  formData.append('role', String(params.role));
  formData.append('expireTime', String(params.expireTime));
  const res = await api.post("/api/admin/user/updateRole", formData, {
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
  });
  if (res.data && res.data.code === 0) {
    return res.data.data === true;
  }
  throw new Error(res.data?.msg || "用户角色更新失败");
}


// -------------------------- 字典相关

export interface DictListQuery {
  app?: string;
  key?: string;
  page?: number;
  size?: number;
}

export interface DictListItem {
  id: number;
  app: string;
  scope: number;
  key: string;
  value: string;
  intro: string;
  remark: string;
  state: number;
  createTime: number;
  updateTime: number;
}

export interface DictListResponse {
  list: DictListItem[];
  hasMore: boolean;
  page: number;
  size: number;
  total: number;
}

export async function fetchDictList(params?: DictListQuery): Promise<DictListResponse> {
  const res = await api.get("/api/admin/dict/list", { params });
  if (res.data && res.data.code === 0) {
    return res.data.data;
  }
  throw new Error(res.data?.msg || "获取字典列表失败");
}

export interface DictSaveReq {
  id?: number;
  app: string;
  scope: number;
  key: string;
  value: string;
  intro: string;
  remark: string;
  state: number;
}


export async function saveDict(params: DictSaveReq): Promise<boolean> {
  const res = await api.post("/api/admin/dict/save", params);
  if (res.data && res.data.code === 0) {
    return true;
  }
  throw new Error(res.data?.msg || "保存字典失败");
}

export async function updateDictState(id: number, state: number): Promise<boolean> {
  const res = await api.post("/api/admin/dict/updateState", {
    "id": id, "state": state
  }, {
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
  });
  if (res.data && res.data.code === 0) {
    return true;
  }
  throw new Error(res.data?.msg || "更新状态失败");
}

export async function deleteDict(id: number): Promise<boolean> {
  const res = await api.get("/api/admin/dict/delete?id=" + id);
  if (res.data && res.data.code === 0) {
    return true;
  }
  throw new Error(res.data?.msg || "删除失败");
}



// ---------------- 个人用户相关

export interface UserSaveReq {
  userId: number;
  displayName: string;
  avatar: string;
  email: string;
  intro: string;
}

export async function getUserDetail() {
  const res = await api.get("/api/user/detail");
  if (res.data && res.data.code === 0) {
    return res.data.data;
  }
  throw new Error(res.data?.msg || "获取用户信息失败");
}

export async function updateUserDetail(params: UserSaveReq) {
  const res = await api.post("/api/user/update", params);
  if (res.data && res.data.code === 0) {
    return res.data.data;
  }
  throw new Error(res.data?.msg || "更新用户信息失败");
}

export async function toPay(vipLevel: number | string | String) {
  const res = await api.get(`/api/recharge/toPay?vipPrice=${vipLevel}`);
  if (res.data && res.data.code === 0) {
    return res.data.data;
  }
  throw new Error(res.data?.msg || "获取支付信息失败");
}

export async function markPaying(id: any) {
  // 告诉后端已经支付成功
  const res = await api.get(`/api/recharge/paying?rechargeId=${id}`);
  if (res.data && res.data.code === 0) {
    return res.data.data;
  }
  throw new Error(res.data?.msg || "同步支付状态失败~ 到购买记录看看吧");
}

export async function refreshPay(id: any) {
  // 告诉后端已经支付成功
  const res = await api.get(`/api/recharge/refreshPay?rechargeId=${id}`);
  if (res.data && res.data.code === 0) {
    return res.data.data;
  }
  throw new Error(res.data?.msg || "重置状态失败~");
}

export interface RechageListItem {
  payId: number;
  tradeNo: string;
  amount: string;
  level: number;
  status: number;
  payTime: number;
  transactionId: string;
}

export interface RechageListResponse {
  list: RechageListItem[];
  hasMore: boolean;
  page: number;
  size: number;
  total: number;
}

// 查询用户充值记录
export async function getRechargeList(): Promise<RechageListResponse> {
  const res = await api.get("/api/recharge/listRecords");
  if (res.data && res.data.code === 0) {
    return res.data.data;
  }
  throw new Error(res.data?.msg || "获取用户信息失败");
}


// ---------------- 全局配置


export interface GlobalConfigItem {
  app: String;
  items: GlobalConfigItemValue[];
}

export interface GlobalConfigItemValue {
  key: String;
  value: String;
  intro: String;
}


export async function getGlobalConfig(): Promise<{ [key: string]: GlobalConfigItem }> {
  const res = await api.get("/api/common/dict");
  if (res.data && res.data.code === 0) {
    const data = res.data.data;
    const result: { [key: string]: GlobalConfigItem } = {};
    for (const item of data) {
      result[item.app] = item;
    }
    return result;
  }
  throw new Error(res.data?.msg || "获取全局配置失败");
}