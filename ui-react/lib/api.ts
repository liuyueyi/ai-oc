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
  page?: number
  size?: number
}

export interface JobListResponse {
  list: any[]
  hasMore: boolean
  page: number
  size: number
  total: number
}

export async function fetchJobList(params?: JobListQuery): Promise<JobListResponse> {
  const res = await api.get("/api/oc/list", { params })
  if (res.data && res.data.code === 0) {
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


export async function submitAIEntry(params: { content: string; model: string; type: string, file: any }) {
  if (params.file) {
    // 传文件的方式
    const formData = new FormData();
    formData.append("file", params.file);
    formData.append("model", params.model);
    formData.append("type", params.type);
    const ans = await api.post("/api/admin/gather/submit", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
    if (ans.data && ans.data.code === 0) {
      return ans.data.data
    } else {
      throw new Error(ans.data?.msg || "AI录入失败")
    }
  }

  const res = await api.post("/api/admin/gather/submit", params, {
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
  })
  if (res.data && res.data.code === 0) {
    return res.data.data
  }
  throw new Error(res.data?.msg || "AI录入失败")
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
  state?: number
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
      return { list: res.data.data, total: res.data.data.length }
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