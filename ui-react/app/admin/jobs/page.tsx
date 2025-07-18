"use client"
import { useEffect, useState } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { fetchJobList, JobListQuery, JobListResponse } from "@/lib/api"

interface JobOffer {
    id: string | number
    companyName: string
    companyType: string
    location: string
    recruitmentType: string
    recruitmentTarget: string
    position: string
    applicationProgress: string
    updateTime: string
    deadline: string
    relatedLinks: string
    recruitmentNotice: string
    referralCode: string
    notes: string
}

export default function JobsPage() {
    const [jobOffers, setJobOffers] = useState<any[]>([])
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)
    const [page, setPage] = useState(1)
    const [pageSize] = useState(10)
    const [total, setTotal] = useState(0)
    const [filters, setFilters] = useState<JobListQuery>({})
    const [editingOffer, setEditingOffer] = useState<JobOffer | null>(null)
    const [isAddingNew, setIsAddingNew] = useState(false)

    const handleDelete = (id: string) => {
        setJobOffers(jobOffers.filter((offer) => offer.id !== id))
    }

    const handleSave = (offer: JobOffer) => {
        if (isAddingNew) {
            setJobOffers([...jobOffers, { ...offer, id: Date.now().toString() }])
            setIsAddingNew(false)
        } else {
            setJobOffers(jobOffers.map((o) => (o.id === offer.id ? offer : o)))
        }
        setEditingOffer(null)
    }

    const fetchData = async (params: JobListQuery = {}) => {
        setLoading(true)
        setError(null)
        try {
            const res: JobListResponse = await fetchJobList({ page, size: pageSize, ...filters, ...params })
            setJobOffers(res.list)
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

    const handleFilterChange = (key: keyof JobListQuery, value: string) => {
        setFilters((prev) => ({ ...prev, [key]: value }))
        setPage(1)
    }

    return (
        <div className="min-h-screen bg-gray-50">
            <header className="bg-white border-b">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex justify-between items-center h-16">
                        <h1 className="text-2xl font-bold text-gray-900">职位管理</h1>
                    </div>
                </div>
            </header>

            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
                <div className="mb-6 flex flex-wrap gap-2 items-center">
                    <Input placeholder="公司名称" className="w-32" value={filters.companyName || ''} onChange={e => handleFilterChange('companyName', e.target.value)} />
                    <Input placeholder="公司类型" className="w-24" value={filters.companyType || ''} onChange={e => handleFilterChange('companyType', e.target.value)} />
                    <Input placeholder="工作地点" className="w-24" value={filters.jobLocation || ''} onChange={e => handleFilterChange('jobLocation', e.target.value)} />
                    <Input placeholder="招聘类型" className="w-24" value={filters.recruitmentType || ''} onChange={e => handleFilterChange('recruitmentType', e.target.value)} />
                    <Input placeholder="招聘对象" className="w-24" value={filters.recruitmentTarget || ''} onChange={e => handleFilterChange('recruitmentTarget', e.target.value)} />
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
                                <TableHead>操作</TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {loading ? (
                                <TableRow><TableCell colSpan={8}>加载中...</TableCell></TableRow>
                            ) : error ? (
                                <TableRow><TableCell colSpan={8} className="text-red-600">{error}</TableCell></TableRow>
                            ) : jobOffers.length === 0 ? (
                                <TableRow><TableCell colSpan={8}>暂无数据</TableCell></TableRow>
                            ) : (
                                jobOffers.map((offer) => (
                                    <TableRow key={String(offer.id)}>
                                        <TableCell className="font-medium">{offer.companyName}</TableCell>
                                        <TableCell>{offer.companyType}</TableCell>
                                        <TableCell>{offer.jobLocation || offer.location}</TableCell>
                                        <TableCell>{offer.recruitmentType}</TableCell>
                                        <TableCell>{offer.recruitmentTarget}</TableCell>
                                        <TableCell>{offer.position}</TableCell>
                                        <TableCell>{offer.updateTime || offer.lastUpdatedTime}</TableCell>
                                        <TableCell>
                                            <div className="flex space-x-2">
                                                <Button size="sm" variant="outline" onClick={() => setEditingOffer(offer)}>
                                                    编辑
                                                </Button>
                                                <Button size="sm" variant="destructive" onClick={() => handleDelete(String(offer.id))}>
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
                    <span> 第 {page} 页</span>
                    <Button size="sm" variant="outline" disabled={jobOffers.length < pageSize} onClick={() => setPage(page + 1)}>下一页</Button>
                </div>

                {editingOffer && (
                    <Dialog
                        open={true}
                        onOpenChange={() => {
                            setEditingOffer(null)
                            setIsAddingNew(false)
                        }}
                    >
                        <DialogContent className="max-w-4xl max-h-[80vh] overflow-y-auto">
                            <DialogHeader>
                                <DialogTitle>{isAddingNew ? "添加新职位" : "编辑职位"}</DialogTitle>
                            </DialogHeader>
                            <div className="grid grid-cols-2 gap-4 py-4">
                                <div>
                                    <label className="text-sm font-medium">公司名称</label>
                                    <Input
                                        value={editingOffer.companyName}
                                        onChange={(e) => setEditingOffer({ ...editingOffer, companyName: e.target.value })}
                                    />
                                </div>
                                <div>
                                    <label className="text-sm font-medium">公司类型</label>
                                    <Select
                                        value={editingOffer.companyType}
                                        onValueChange={(value) => setEditingOffer({ ...editingOffer, companyType: value })}
                                    >
                                        <SelectTrigger>
                                            <SelectValue />
                                        </SelectTrigger>
                                        <SelectContent>
                                            <SelectItem value="民企">民企</SelectItem>
                                            <SelectItem value="央国企">央国企</SelectItem>
                                            <SelectItem value="事业单位">事业单位</SelectItem>
                                            <SelectItem value="外企">外企</SelectItem>
                                        </SelectContent>
                                    </Select>
                                </div>
                                <div>
                                    <label className="text-sm font-medium">工作地点</label>
                                    <Input
                                        value={editingOffer.location}
                                        onChange={(e) => setEditingOffer({ ...editingOffer, location: e.target.value })}
                                    />
                                </div>
                                <div>
                                    <label className="text-sm font-medium">招聘类型</label>
                                    <Select
                                        value={editingOffer.recruitmentType}
                                        onValueChange={(value) => setEditingOffer({ ...editingOffer, recruitmentType: value })}
                                    >
                                        <SelectTrigger>
                                            <SelectValue />
                                        </SelectTrigger>
                                        <SelectContent>
                                            <SelectItem value="春招">春招</SelectItem>
                                            <SelectItem value="秋招">秋招</SelectItem>
                                            <SelectItem value="秋招提前批">秋招提前批</SelectItem>
                                            <SelectItem value="日常招聘">日常招聘</SelectItem>
                                        </SelectContent>
                                    </Select>
                                </div>
                                <div>
                                    <label className="text-sm font-medium">招聘对象</label>
                                    <Select
                                        value={editingOffer.recruitmentTarget}
                                        onValueChange={(value) => setEditingOffer({ ...editingOffer, recruitmentTarget: value })}
                                    >
                                        <SelectTrigger>
                                            <SelectValue />
                                        </SelectTrigger>
                                        <SelectContent>
                                            <SelectItem value="2025年毕业生">2025年毕业生</SelectItem>
                                            <SelectItem value="2026年毕业生">2026年毕业生</SelectItem>
                                            <SelectItem value="社会招聘">社会招聘</SelectItem>
                                        </SelectContent>
                                    </Select>
                                </div>
                                <div>
                                    <label className="text-sm font-medium">投递截止</label>
                                    <Input
                                        value={editingOffer.deadline}
                                        onChange={(e) => setEditingOffer({ ...editingOffer, deadline: e.target.value })}
                                    />
                                </div>
                                <div className="col-span-2">
                                    <label className="text-sm font-medium">岗位描述</label>
                                    <textarea
                                        className="w-full p-2 border rounded-md"
                                        rows={3}
                                        value={editingOffer.position}
                                        onChange={(e) => setEditingOffer({ ...editingOffer, position: e.target.value })}
                                    />
                                </div>
                            </div>
                            <div className="flex justify-end space-x-2">
                                <Button
                                    variant="outline"
                                    onClick={() => {
                                        setEditingOffer(null)
                                        setIsAddingNew(false)
                                    }}
                                >
                                    取消
                                </Button>
                                <Button onClick={() => handleSave(editingOffer)}>保存</Button>
                            </div>
                        </DialogContent>
                    </Dialog>
                )}
            </div>
        </div>
    )
} 