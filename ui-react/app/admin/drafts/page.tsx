"use client"
import { useState, useEffect } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { fetchDraftList, DraftItem, DraftListQuery, DraftListResponse } from "@/lib/api"

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

    const handleSave = (draft: DraftItem) => {
        if (isAddingNew) {
            setDrafts([
                ...drafts,
                { ...draft, id: Date.now().toString(), updateTime: new Date().toISOString().split("T")[0] },
            ])
            setIsAddingNew(false)
        } else {
            setDrafts(
                drafts.map((d) =>
                    d.id === draft.id ? { ...draft, updateTime: new Date().toISOString().split("T")[0] } : d
                )
            )
        }
        setEditingDraft(null)
    }

    return (
        <div className="min-h-screen bg-gray-50">
            <header className="bg-white border-b">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex justify-between items-center h-16">
                        <h1 className="text-2xl font-bold text-gray-900">草稿列表</h1>
                    </div>
                </div>
            </header>
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
                <div className="mb-6 flex flex-wrap gap-2 items-center">
                    <Input placeholder="公司名称" className="w-32" value={filters.companyName || ''} onChange={e => handleFilterChange('companyName', e.target.value)} />
                    <Input placeholder="公司类型" className="w-24" value={filters.companyType || ''} onChange={e => handleFilterChange('companyType', e.target.value)} />
                    <Input placeholder="工作地点" className="w-24" value={filters.jobLocation || ''} onChange={e => handleFilterChange('jobLocation', e.target.value)} />
                    <Input placeholder="招聘类型" className="w-24" value={filters.recruitmentType || ''} onChange={e => handleFilterChange('recruitmentType', e.target.value)} />
                    <Input placeholder="岗位" className="w-24" value={filters.position || ''} onChange={e => handleFilterChange('position', e.target.value)} />
                </div>
                <div className="bg-white rounded-lg shadow overflow-hidden">
                    <Table>
                        <TableHeader>
                            <TableRow>
                                <TableHead>公司名称</TableHead>
                                <TableHead>公司类型</TableHead>
                                <TableHead>工作地点</TableHead>
                                <TableHead>招聘类型</TableHead>
                                <TableHead>招聘对象</TableHead>
                                <TableHead>岗位</TableHead>
                                <TableHead>更新时间</TableHead>
                                <TableHead>链接</TableHead>
                                <TableHead>公告</TableHead>
                                <TableHead>操作</TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {loading ? (
                                <TableRow><TableCell colSpan={8}>加载中...</TableCell></TableRow>
                            ) : error ? (
                                <TableRow><TableCell colSpan={8} className="text-red-600">{error}</TableCell></TableRow>
                            ) : drafts.length === 0 ? (
                                <TableRow><TableCell colSpan={8}>暂无数据</TableCell></TableRow>
                            ) : (
                                drafts.map((draft) => (
                                    <TableRow key={draft.id}>
                                        <TableCell>{draft.companyName}</TableCell>
                                        <TableCell>{draft.companyType}</TableCell>
                                        <TableCell>{draft.jobLocation}</TableCell>
                                        <TableCell>{draft.recruitmentType}</TableCell>
                                        <TableCell>{draft.recruitmentTarget}</TableCell>
                                        <TableCell>{draft.position}</TableCell>
                                        <TableCell>{draft.lastUpdatedTime}</TableCell>
                                        <TableCell>
                                            <a
                                                href={draft.relatedLink}
                                                className="inline-flex items-center justify-center rounded-md text-sm font-medium transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:opacity-50 disabled:pointer-events-none bg-blue-500 hover:bg-blue-600 text-white h-8 px-3"
                                                target="_blank"
                                                rel="noopener noreferrer"
                                            >
                                                投递
                                            </a>
                                        </TableCell>
                                        <TableCell>
                                            <a
                                                href={draft.jobAnnouncement}
                                                className="inline-flex items-center justify-center rounded-md text-sm font-medium transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:opacity-50 disabled:pointer-events-none bg-blue-500 hover:bg-blue-600 text-white h-8 px-3"
                                                target="_blank"
                                                rel="noopener noreferrer"
                                            >
                                                公告
                                            </a>
                                        </TableCell>
                                        <TableCell>
                                            <div className="flex space-x-2">
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
                {/* 分页 */}
                <div className="flex justify-end items-center gap-2 mt-4">
                    <Button size="sm" variant="outline" disabled={page === 1} onClick={() => setPage(page - 1)}>上一页</Button>
                    <span>第 {page} 页</span>
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
                            </div>
                        </DialogContent>
                    </Dialog>
                )}
            </div>
        </div>
    )
} 