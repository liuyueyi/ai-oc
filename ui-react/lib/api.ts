import axios from "axios"

const api = axios.create({
  baseURL: "http://localhost:8080",
  timeout: 360000,
})

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

export async function submitAIEntry(params: { content: string; model: string; type: string }) {
  const res = await api.post("/admin/gather/submit", params)
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
  const res = await api.get("/admin/draft/list", { params })
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
  const res = await api.post("/admin/draft/toOc", ids)
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
  const res = await api.post("/admin/draft/update", draft)
  if (res.data && res.data.code === 0) {
    return true
  }
  throw new Error(res.data?.msg || "草稿更新失败")
}