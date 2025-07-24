"use client"
import { useState, useEffect } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { fetchDraftList, DraftItem, DraftListQuery, DraftListResponse, batchPublishDrafts, updateDraft, deleteDraft, GlobalConfigItemValue } from "@/lib/api"
import { useToast } from "@/hooks/use-toast"
import { getConfigValue } from "@/lib/config"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import {
    Pagination,
    PaginationContent,
    PaginationEllipsis,
    PaginationItem,
    PaginationLink,
    PaginationNext,
    PaginationPrevious,
} from "@/components/ui/pagination"

const PAGE_SIZE = 10

export default function DraftsPage() {
    const [drafts, setDrafts] = useState<DraftItem[]>([])
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)
    const [editingDraft, setEditingDraft] = useState<DraftItem | null>(null)
    const [isAddingNew, setIsAddingNew] = useState(false)
    const [page, setPage] = useState(1)
    const [total, setTotal] = useState(0)
    const [filters, setFilters] = useState<DraftListQuery>({})
    const [selectedIds, setSelectedIds] = useState<number[]>([])
    const [publishLoading, setPublishLoading] = useState(false)
    const [publishOneLoadingId, setPublishOneLoadingId] = useState<number | null>(null)
    const { toast } = useToast();
    const [companyTypes, setCompanyTypes] = useState<GlobalConfigItemValue[]>([]);
    const [recruitmentTypes, setRecruitmentTypes] = useState<GlobalConfigItemValue[]>([]);
    const [recruitmentTarget, setRecruitmentTarget] = useState<GlobalConfigItemValue[]>([]);
    const [processStates, setProcessStates] = useState<GlobalConfigItemValue[]>([]);

    const fetchData = async (params: DraftListQuery = {}) => {
        setLoading(true)
        setError(null)
        try {
            const res: DraftListResponse = await fetchDraftList({ page, size: PAGE_SIZE, ...filters, ...params })
            setDrafts(res.list)
            setTotal(res.total)
        } catch (e: any) {
            setError(e.message)
        } finally {
            setLoading(false)
        }
    }

    useEffect(() => {
        fetchData()
        getConfigValue('oc', 'CompanyTypeEnum').then(setCompanyTypes);
        getConfigValue('oc', 'RecruitmentTypeEnum').then(setRecruitmentTypes);
        getConfigValue('oc', 'RecruitmentTargetEnum').then(setRecruitmentTarget);
        getConfigValue('oc', 'DraftProcessEnum').then(setProcessStates);
        // eslint-disable-next-line
    }, [page, filters])

    const handleFilterChange = (key: keyof DraftListQuery, value: string) => {
        if (value == '-1') {
            value = ''
        }
        setFilters((prev) => ({ ...prev, [key]: value }))
        setPage(1)
    }

    const handleDelete = async (id: number) => {
        await deleteDraft(id).then(e => {
            // 删除数据
            setDrafts(drafts.filter((draft) => draft.id !== id))
            toast({ title: "删除成功！" })
        }).catch(err => {
            toast({ title: "删除失败！", description: err.message, variant: "destructive" })
        })
    }

    const ifUrlValide = (url: string) => {
        const urlRegex = /^https?:\/\/.+|^$|^-$/;
        return urlRegex.test(url);
    }

    const validateUrl = (url: string) => {
        return ifUrlValide(url) ? url : "-";
    }

    const handleSave = async (draft: DraftItem) => {
        // 在保存时，需要校验链接的合法性
        setEditingDraft(validateDraftUrls(draft));

        if (isAddingNew) {
            setDrafts([
                ...drafts,
                { ...draft, id: Date.now(), updateTime: new Date().toISOString().split("T")[0] },
            ])
            setIsAddingNew(false)
            setEditingDraft(null)
        } else {

            await updateDraft({ ...draft, updateTime: new Date().toISOString().split("T")[0] })
                .then(res => {
                    draft['toProcess'] = 0;
                    setDrafts(
                        drafts.map((d) =>
                            d.id === draft.id ? { ...draft, updateTime: new Date().toISOString().split("T")[0] } : d
                        )
                    )
                    setEditingDraft(null)
                    toast({ title: "保存成功！" })

                }).catch(err => {
                    toast({ title: "保存失败！", description: err.message, variant: "destructive" })
                })
        }
    }

    const validateDraftUrls = (draft: DraftItem | null): DraftItem | null => {
        if (!draft) return null;
        return {
            ...draft,
            relatedLink: validateUrl(draft.relatedLink),
            jobAnnouncement: validateUrl(draft.jobAnnouncement)
        };
    }

    const checkSelectAll = () => {
        // 判断是否全选了
        if (drafts.length > 0) {
            return selectedIds.length === drafts.filter(d => d.toProcess !== 1).length
        } else {
            return false;
        }
    }

    const handleSelectAll = (checked: boolean) => {
        if (checked) {
            setSelectedIds(drafts.filter(d => d.toProcess !== 1).map(d => d.id))
        } else {
            setSelectedIds([])
        }
    }
    const handleSelectOne = (id: number, checked: boolean) => {
        setSelectedIds(prev => checked ? [...prev, id] : prev.filter(i => i !== id))
    }

    const handlePublishOne = async (id: number) => {
        setPublishOneLoadingId(id)
        try {
            await doPublish([id])
        } finally {
            setPublishOneLoadingId(null)
        }
    }

    const handlePublish = async () => {
        if (selectedIds?.length === 0) return
        setPublishLoading(true)
        try {
            doPublish(selectedIds)
            setSelectedIds([])
        } finally {
            setPublishLoading(false)
        }
    }

    const doPublish = async (ids: number[]) => {
        try {
            await batchPublishDrafts(ids)
            toast({ title: "发布成功！" })
            await fetchData() // 发布后刷新列表
        } catch (e: any) {
            toast({ title: "发布失败", description: e?.message || "未知错误", variant: "destructive" })
        }
    }

    return (
        <div className="min-h-screen bg-gray-50">
            <header className="bg-white border-b">
                <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex justify-between items-center h-16">
                        <h1 className="text-2xl font-bold text-gray-900">草稿列表</h1>
                        <Button onClick={handlePublish} disabled={publishLoading || selectedIds?.length === 0} className="ml-4">
                            {publishLoading ? "发布中..." : "同步职位"}
                        </Button>
                    </div>
                </div>
            </header>
            <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
                <div className=" pt-4 mb-6 flex flex-wrap gap-2 items-center">
                    <Input placeholder="公司名称" className="w-32" value={filters.companyName || ''} onChange={e => handleFilterChange('companyName', e.target.value)} />
                    {
                        companyTypes && (
                            <Select value={filters.companyType} onValueChange={value => handleFilterChange('companyType', value)}>
                                <SelectTrigger className="w-32"><SelectValue placeholder="公司类型" /></SelectTrigger>
                                <SelectContent>
                                    <SelectItem value="-1">全部</SelectItem>
                                    {companyTypes.map(option => (
                                        <SelectItem key={option.intro as string} value={option.intro as string}>{option.intro}</SelectItem>
                                    ))}
                                </SelectContent>
                            </Select>
                        )
                    }

                    {recruitmentTypes && (
                        <Select value={filters.recruitmentType || ''} onValueChange={value => handleFilterChange('recruitmentType', value)}>
                            <SelectTrigger className="w-32"><SelectValue placeholder="招聘类型" /></SelectTrigger>
                            <SelectContent>
                                <SelectItem value="-1">全部</SelectItem>
                                {recruitmentTypes.map(type => (
                                    <SelectItem key={type.intro as string} value={type.intro as string}>
                                        {type.intro}
                                    </SelectItem>
                                ))}
                            </SelectContent>
                        </Select>
                    )}
                    <Input placeholder="工作地点" className="w-24" value={filters.jobLocation || ''} onChange={e => handleFilterChange('jobLocation', e.target.value)} />
                    <Input placeholder="岗位" className="w-24" value={filters.position || ''} onChange={e => handleFilterChange('position', e.target.value)} />
                    <Select value={filters.toProcess || ''} onValueChange={value => handleFilterChange('toProcess', value)}>
                        <SelectTrigger className="w-32"><SelectValue placeholder="处理状态" /></SelectTrigger>
                        <SelectContent>
                            <SelectItem value="-1">全部</SelectItem>
                            {
                                processStates.map(option => (
                                    <SelectItem key={option.value as string} value={option.value as string}>{option.intro}</SelectItem>
                                ))
                            }
                        </SelectContent>
                    </Select>
                </div>
                {/* 只让表格区域可横向滚动 */}
                <div className="bg-white rounded-lg shadow overflow-x-auto">
                    <Table className="min-w-[1600px] table-fixed text-sm">
                        <TableHeader className="bg-gray-100">
                            <TableRow>
                                <TableHead className="whitespace-nowrap text-center">
                                    <input type="checkbox" checked={checkSelectAll()}
                                        onChange={e => handleSelectAll(e.target.checked)} />
                                </TableHead>
                                <TableHead className="whitespace-nowrap text-center">公司名称</TableHead>
                                <TableHead className="whitespace-nowrap text-center">公司类型</TableHead>
                                <TableHead className="whitespace-nowrap text-center">工作地点</TableHead>
                                <TableHead className="whitespace-nowrap text-center">招聘类型</TableHead>
                                <TableHead className="whitespace-nowrap text-center">招聘对象</TableHead>
                                <TableHead className="whitespace-nowrap text-center">岗位</TableHead>
                                <TableHead className="whitespace-nowrap text-center">更新时间</TableHead>
                                <TableHead className="whitespace-nowrap text-center">状态</TableHead>
                                <TableHead className="whitespace-nowrap text-center">待处理</TableHead>
                                <TableHead className="whitespace-nowrap text-center">链接</TableHead>
                                <TableHead className="whitespace-nowrap text-center">公告</TableHead>
                                <TableHead className="sticky right-0 bg-white z-10 whitespace-nowrap text-center w-[220px] text-white bg-gray-400">操作</TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {loading ? (
                                <TableRow><TableCell colSpan={13}>加载中...</TableCell></TableRow>
                            ) : error ? (
                                <TableRow><TableCell colSpan={13} className="text-red-600">{error}</TableCell></TableRow>
                            ) : drafts?.length === 0 ? (
                                <TableRow><TableCell colSpan={13}>暂无数据</TableCell></TableRow>
                            ) : (
                                drafts.map((draft) => (
                                    <TableRow key={draft.id}>
                                        <TableCell className="whitespace-nowrap text-center"><input type="checkbox" disabled={draft.toProcess == 1} checked={selectedIds.includes(draft.id)} onChange={e => handleSelectOne(draft.id, e.target.checked)} /></TableCell>
                                        <TableCell className="whitespace-nowrap text-center max-w-[320px] truncate break-all" title={draft.companyName}>{draft.companyName}</TableCell>
                                        <TableCell className="whitespace-nowrap text-center"><Badge variant="secondary">{draft.companyType}</Badge></TableCell>
                                        <TableCell className="whitespace-nowrap text-center max-w-[240px] truncate" title={draft.jobLocation}>{draft.jobLocation}</TableCell>
                                        <TableCell className="whitespace-nowrap text-center max-w-[240px] truncate" title={draft.recruitmentType}>{draft.recruitmentType}</TableCell>
                                        <TableCell className="whitespace-nowrap text-center max-w-[240px] truncate" title={draft.recruitmentTarget}>{draft.recruitmentTarget}</TableCell>
                                        <TableCell className="whitespace-nowrap text-center max-w-[360px] truncate break-all" title={draft.position}>{draft.position}</TableCell>
                                        <TableCell className="whitespace-nowrap text-center max-w-[240px] truncate" title={draft.lastUpdatedTime}>{draft.lastUpdatedTime}</TableCell>
                                        <TableCell className="whitespace-nowrap text-center"><Badge variant={draft.state == 1 ? "default" : "outline"}>{draft.state == 1 ? '同步过' : '未同步'}</Badge></TableCell>
                                        <TableCell className="whitespace-nowrap text-center"><Badge variant={draft.toProcess == 1 ? "default" : "secondary"}>{draft.toProcess == 1 ? '已更新' : '待更新'}</Badge></TableCell>
                                        <TableCell className="whitespace-nowrap text-center max-w-[360px] truncate break-all" title={draft.relatedLink}>
                                            <a href={draft.relatedLink} className="text-blue-800 underline" target="_blank" rel="noopener noreferrer">{draft.relatedLink}</a>
                                        </TableCell>
                                        <TableCell className="whitespace-nowrap text-center max-w-[360px] truncate break-all" title={draft.jobAnnouncement}>
                                            <a href={draft.jobAnnouncement} className="text-blue-800 underline" target="_blank" rel="noopener noreferrer">{draft.jobAnnouncement}</a>
                                        </TableCell>
                                        <TableCell className='bg-white sticky right-0 z-10 whitespace-nowrap text-center w-[220px]'>
                                            <div className="flex justify-center space-x-2">
                                                {
                                                    draft.toProcess != 1 && (
                                                        <Button size="sm" className="bg-orange-500 hover:bg-orange-600 text-white" onClick={() => handlePublishOne(draft.id)} disabled={publishOneLoadingId === draft.id}>
                                                            {publishOneLoadingId === draft.id ? "发布中..." : "发布"}
                                                        </Button>
                                                    )
                                                }

                                                <Button size="sm" variant="outline" onClick={() => setEditingDraft(draft)}>
                                                    编辑
                                                </Button>
                                                <Button size="sm" variant="destructive" onClick={() => handleDelete(draft.id)}>
                                                    删除
                                                </Button>
                                            </div>
                                        </TableCell>
                                    </TableRow>
                                ))
                            )}
                        </TableBody>
                    </Table>
                </div>
            </div>
            {/* 分页 */}
            <div className="mt-4 flex justify-end">
                <Pagination>
                    <PaginationContent>
                        <PaginationItem>
                            <PaginationPrevious
                                href="#"
                                onClick={(e) => {
                                    e.preventDefault()
                                    if (page > 1) {
                                        setPage(page - 1)
                                    }
                                }}
                                className={page <= 1 ? "pointer-events-none opacity-50" : ""}
                            />
                        </PaginationItem>
                        <PaginationItem>
                            <span className="text-sm text-muted-foreground">
                                第 {page} /{Math.ceil(total / PAGE_SIZE)} 页
                            </span>
                        </PaginationItem>
                        <PaginationItem>
                            <PaginationNext
                                href="#"
                                onClick={(e) => {
                                    e.preventDefault()
                                    if (page * PAGE_SIZE < total) {
                                        setPage(page + 1)
                                    }
                                }}
                                className={page * PAGE_SIZE >= total ? "pointer-events-none opacity-50" : ""}
                            />
                        </PaginationItem>
                    </PaginationContent>
                </Pagination>
            </div>
            {/* 编辑/新增弹窗保持原样 */}
            {editingDraft && (
                <Dialog
                    open={true}
                    onOpenChange={() => {
                        setEditingDraft(null)
                        setIsAddingNew(false)
                    }}
                >
                    <DialogContent className="max-w-2xl max-h-[80vh] overflow-y-auto">
                        <DialogHeader>
                            <DialogTitle>{isAddingNew ? "添加新草稿" : "编辑草稿"}</DialogTitle>
                        </DialogHeader>
                        <div className="grid grid-cols-2 gap-4 py-4">
                            <div>
                                <label className="text-sm font-medium">业务主键ID</label>
                                <Input value={editingDraft.id} disabled />
                            </div>
                            <div>
                                <label className="text-sm font-medium">公司名称</label>
                                <Input value={editingDraft.companyName} onChange={e => setEditingDraft({ ...editingDraft, companyName: e.target.value })} />
                            </div>
                            <div>
                                <label className={companyTypes.some(item => item.intro === editingDraft.companyType) ? "text-sm font-medium" : "text-sm font-medium text-red-500"}>公司类型</label>
                                <Select value={editingDraft?.companyType || ""} onValueChange={(v) => setEditingDraft({ ...editingDraft, companyType: v })}>
                                    <SelectTrigger>
                                        <SelectValue placeholder="请选择公司类型" />
                                    </SelectTrigger>
                                    <SelectContent>
                                        {companyTypes.map((type) => (
                                            <SelectItem value={type.intro as string} key={type.intro as string}>{type.intro}</SelectItem>
                                        ))}
                                    </SelectContent>
                                </Select>
                            </div>
                            <div>
                                <label className="text-sm font-medium">工作地点</label>
                                <Input value={editingDraft.jobLocation} onChange={e => setEditingDraft({ ...editingDraft, jobLocation: e.target.value })} />
                            </div>
                            <div>
                                <label className={recruitmentTypes.some(item => item.intro === editingDraft.recruitmentType) ? "text-sm font-medium" : "text-sm font-medium text-red-500"}>招聘类型</label>
                                <Select value={editingDraft.recruitmentType || ""} onValueChange={(value) => setEditingDraft({ ...editingDraft, recruitmentType: value })}>
                                    <SelectTrigger>
                                        <SelectValue placeholder="请选择招聘类型" />
                                    </SelectTrigger>
                                    <SelectContent>
                                        {recruitmentTypes.map((type) => (
                                            <SelectItem key={type.intro as string} value={type.intro as string}>
                                                {type.intro}
                                            </SelectItem>
                                        ))}
                                    </SelectContent>
                                </Select>
                            </div>
                            <div>
                                <label className={recruitmentTarget.some(item => item.intro === editingDraft.recruitmentTarget) ? "text-sm font-medium" : "text-sm font-medium text-red-500"}>招聘对象</label>
                                <Select value={editingDraft.recruitmentTarget || ""}
                                    onValueChange={(value) => setEditingDraft({ ...editingDraft, recruitmentTarget: value })}>
                                    <SelectTrigger>
                                        <SelectValue placeholder="请选择招聘对象" />
                                    </SelectTrigger>
                                    <SelectContent>
                                        {recruitmentTarget.map((type) => (
                                            <SelectItem key={type.intro as string} value={type.intro as string}>
                                                {type.intro}
                                            </SelectItem>
                                        ))}
                                    </SelectContent>
                                </Select>
                            </div>
                            <div>
                                <label className="text-sm font-medium">岗位</label>
                                <Input value={editingDraft.position} onChange={e => setEditingDraft({ ...editingDraft, position: e.target.value })} />
                            </div>
                            <div>
                                <label className="text-sm font-medium">更新时间</label>
                                <Input value={editingDraft.lastUpdatedTime} onChange={e => setEditingDraft({ ...editingDraft, lastUpdatedTime: e.target.value })} />
                            </div>
                            <div>
                                <label className="text-sm font-medium">投递链接</label>
                                <Input value={editingDraft.relatedLink} onChange={e => {
                                    const value = e.target.value;
                                    setEditingDraft({ ...editingDraft, relatedLink: value });
                                }} className={!ifUrlValide(editingDraft.relatedLink) ? "border-red-500" : ""} />
                            </div>
                            <div>
                                <label className="text-sm font-medium">公告链接</label>
                                <Input value={editingDraft.jobAnnouncement} onChange={e => {
                                    const value = e.target.value;
                                    setEditingDraft({ ...editingDraft, jobAnnouncement: value });
                                }} className={!ifUrlValide(editingDraft.jobAnnouncement) ? "border-red-500" : ""} />
                            </div>
                            <div className="w-full">
                                <label className="text-sm font-medium">内推码</label>
                                <Input value={editingDraft.internalReferralCode} onChange={e => setEditingDraft({ ...editingDraft, internalReferralCode: e.target.value })} />
                            </div>
                            <div className="w-full">
                                <label className="text-sm font-medium">备注</label>
                                <Input value={editingDraft.remarks} onChange={e => setEditingDraft({ ...editingDraft, remarks: e.target.value })} />
                            </div>
                        </div>
                        <div className="flex justify-end space-x-2">
                            <Button
                                variant="outline"
                                onClick={() => {
                                    setEditingDraft(null)
                                    setIsAddingNew(false)
                                }}
                            >
                                取消
                            </Button>
                            <Button onClick={() => handleSave(editingDraft)}>保存</Button>
                            <Button className="bg-orange-500 hover:bg-orange-600 text-white" onClick={() => {
                                handlePublishOne(editingDraft.id)
                                setEditingDraft(null)
                            }} disabled={publishOneLoadingId === editingDraft.id}>
                                {publishOneLoadingId === editingDraft.id ? "发布中..." : "发布"}
                            </Button>
                        </div>
                    </DialogContent>
                </Dialog>
            )}
        </div>
    )
}
