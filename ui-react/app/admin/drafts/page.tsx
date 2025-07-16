"use client"
import { useState, useEffect } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { fetchDraftList, DraftItem, DraftListQuery, DraftListResponse, batchPublishDrafts, updateDraft } from "@/lib/api"
import { useToast } from "@/hooks/use-toast"

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
        // eslint-disable-next-line
    }, [page, filters])

    const handleFilterChange = (key: keyof DraftListQuery, value: string) => {
        setFilters((prev) => ({ ...prev, [key]: value }))
        setPage(1)
    }

    const handleDelete = (id: string) => {
        setDrafts(drafts.filter((draft) => draft.id !== id))
    }

    const handleSave = async (draft: DraftItem) => {
        if (isAddingNew) {
            setDrafts([
                ...drafts,
                { ...draft, id: Date.now(), updateTime: new Date().toISOString().split("T")[0] },
            ])
            setIsAddingNew(false)
            setEditingDraft(null)
        } else {
            try {
                draft['toProcess'] = 0;
                await updateDraft({ ...draft, updateTime: new Date().toISOString().split("T")[0] })
                setDrafts(
                    drafts.map((d) =>
                        d.id === draft.id ? { ...draft, updateTime: new Date().toISOString().split("T")[0] } : d
                    )
                )
                setEditingDraft(null)
            } catch (e: any) {
                alert(e?.message || "草稿更新失败")
            }
        }
    }

    const handleSelectAll = (checked: boolean) => {
        if (checked) {
            setSelectedIds(drafts.map(d => d.id))
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
        if (selectedIds.length === 0) return
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
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex justify-between items-center h-16">
                        <h1 className="text-2xl font-bold text-gray-900">草稿列表</h1>
                        <Button onClick={handlePublish} disabled={publishLoading || selectedIds.length === 0} className="ml-4">
                            {publishLoading ? "发布中..." : "同步职位"}
                        </Button>
                    </div>
                </div>
            </header>
            <div className="relative w-full overflow-auto">
                <div className="px-4 pt-4 mb-6 flex flex-wrap gap-2 items-center">
                    <Input placeholder="公司名称" className="w-32" value={filters.companyName || ''} onChange={e => handleFilterChange('companyName', e.target.value)} />
                    <Input placeholder="公司类型" className="w-24" value={filters.companyType || ''} onChange={e => handleFilterChange('companyType', e.target.value)} />
                    <Input placeholder="工作地点" className="w-24" value={filters.jobLocation || ''} onChange={e => handleFilterChange('jobLocation', e.target.value)} />
                    <Input placeholder="招聘类型" className="w-24" value={filters.recruitmentType || ''} onChange={e => handleFilterChange('recruitmentType', e.target.value)} />
                    <Input placeholder="岗位" className="w-24" value={filters.position || ''} onChange={e => handleFilterChange('position', e.target.value)} />
                </div>
                {/* 只让表格区域可横向滚动 */}
                <div className="bg-white rounded-lg shadow">
                    <div className="overflow-x-auto " >
                        <Table className="min-w-[1200px] table-fixed">
                            <TableHeader>
                                <TableRow>
                                    <TableHead>
                                        <input
                                            type="checkbox"
                                            checked={drafts.length > 0 && selectedIds.length === drafts.length}
                                            onChange={e => handleSelectAll(e.target.checked)}
                                        />
                                    </TableHead>
                                    <TableHead>公司名称</TableHead>
                                    <TableHead>公司类型</TableHead>
                                    <TableHead>工作地点</TableHead>
                                    <TableHead>招聘类型</TableHead>
                                    <TableHead>招聘对象</TableHead>
                                    <TableHead>岗位</TableHead>
                                    <TableHead>更新时间</TableHead>
                                    <TableHead>状态</TableHead>
                                    <TableHead>待处理</TableHead>
                                    <TableHead>链接</TableHead>
                                    <TableHead>公告</TableHead>
                                    <TableHead className="sticky right-0 bg-white z-10 w-55">操作</TableHead>
                                </TableRow>
                            </TableHeader>
                            <TableBody>
                                {loading ? (
                                    <TableRow><TableCell colSpan={13}>加载中...</TableCell></TableRow>
                                ) : error ? (
                                    <TableRow><TableCell colSpan={13} className="text-red-600">{error}</TableCell></TableRow>
                                ) : drafts.length === 0 ? (
                                    <TableRow><TableCell colSpan={13}>暂无数据</TableCell></TableRow>
                                ) : (
                                    drafts.map((draft) => (
                                        <TableRow key={draft.id}>
                                            <TableCell>
                                                <input
                                                    type="checkbox"
                                                    checked={selectedIds.includes(draft.id)}
                                                    onChange={e => handleSelectOne(draft.id, e.target.checked)}
                                                />
                                            </TableCell>
                                            <TableCell>{draft.companyName}</TableCell>
                                            <TableCell>
                                                <Badge variant="secondary">{draft.companyType}</Badge>
                                            </TableCell>
                                            <TableCell>{draft.jobLocation}</TableCell>
                                            <TableCell>{draft.recruitmentType}</TableCell>
                                            <TableCell>{draft.recruitmentTarget}</TableCell>
                                            <TableCell>{draft.position}</TableCell>
                                            <TableCell>{draft.lastUpdatedTime}</TableCell>
                                            <TableCell>
                                                <Badge variant={draft.state == 1 ? "default" : "outline"}>{draft.state == 1 ? '已发布' : '未发布'}</Badge>
                                            </TableCell>
                                            <TableCell>
                                                <Badge variant={draft.toProcess == 1 ? "default" : "secondary"}>{draft.toProcess == 1 ? '已更新' : '待更新'}</Badge>
                                            </TableCell>
                                            <TableCell>
                                                <a
                                                    href={draft.relatedLink}
                                                    className="break-all text-sm text-blue-800"
                                                    target="_blank"
                                                    rel="noopener noreferrer"
                                                >
                                                    {draft.relatedLink}
                                                </a>
                                            </TableCell>
                                            <TableCell>
                                                <a
                                                    href={draft.jobAnnouncement}
                                                    className="break-all text-sm text-blue-800 "
                                                    target="_blank"
                                                    rel="noopener noreferrer"
                                                >
                                                    {draft.jobAnnouncement}
                                                </a>
                                            </TableCell>
                                            <TableCell className="sticky right-0 bg-white z-10">
                                                <div className="flex space-x-2">
                                                    <Button size="sm" className="bg-orange-500 hover:bg-orange-600 text-white" onClick={() => handlePublishOne(draft.id)} disabled={publishOneLoadingId === draft.id}>
                                                        {publishOneLoadingId === draft.id ? "发布中..." : "发布"}
                                                    </Button>
                                                    <Button size="sm" variant="outline" onClick={() => setEditingDraft(draft)}>
                                                        编辑
                                                    </Button>
                                                    <Button size="sm" variant="destructive" onClick={() => handleDelete(String(draft.id))}>
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
                <div className="flex justify-end items-center gap-2 mt-4">
                    <Button size="sm" variant="outline" disabled={page === 1} onClick={() => setPage(page - 1)}>上一页</Button>
                    <span> 共 {total} 条， 当前第 {page} 页</span>
                    <Button size="sm" variant="outline" disabled={drafts.length < PAGE_SIZE} onClick={() => setPage(page + 1)}>下一页</Button>
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
                                    <label className="text-sm font-medium">公司类型</label>
                                    <Input value={editingDraft.companyType} onChange={e => setEditingDraft({ ...editingDraft, companyType: e.target.value })} />
                                </div>
                                <div>
                                    <label className="text-sm font-medium">工作地点</label>
                                    <Input value={editingDraft.jobLocation} onChange={e => setEditingDraft({ ...editingDraft, jobLocation: e.target.value })} />
                                </div>
                                <div>
                                    <label className="text-sm font-medium">招聘类型</label>
                                    <Input value={editingDraft.recruitmentType} onChange={e => setEditingDraft({ ...editingDraft, recruitmentType: e.target.value })} />
                                </div>
                                <div>
                                    <label className="text-sm font-medium">招聘对象</label>
                                    <Input value={editingDraft.recruitmentTarget} onChange={e => setEditingDraft({ ...editingDraft, recruitmentTarget: e.target.value })} />
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
                                    <Input value={editingDraft.relatedLink} onChange={e => setEditingDraft({ ...editingDraft, relatedLink: e.target.value })} />
                                </div>
                                <div>
                                    <label className="text-sm font-medium">公告链接</label>
                                    <Input value={editingDraft.jobAnnouncement} onChange={e => setEditingDraft({ ...editingDraft, jobAnnouncement: e.target.value })} />
                                </div>
                                <div>
                                    <label className="text-sm font-medium">内推码</label>
                                    <Input value={editingDraft.internalReferralCode} onChange={e => setEditingDraft({ ...editingDraft, internalReferralCode: e.target.value })} />
                                </div>
                                <div>
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
                                <Button className="bg-orange-500 hover:bg-orange-600 text-white" onClick={() => handlePublishOne(editingDraft.id)} disabled={publishOneLoadingId === editingDraft.id}>
                                    {publishOneLoadingId === editingDraft.id ? "发布中..." : "发布"}
                                </Button>
                            </div>
                        </DialogContent>
                    </Dialog>
                )}
            </div>
        </div>
    )
} 