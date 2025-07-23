"use client"
import { useEffect, useState } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from "@/components/ui/dialog"
import {
    AlertDialog,
    AlertDialogAction,
    AlertDialogCancel,
    AlertDialogContent,
    AlertDialogDescription,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogTitle,
    AlertDialogTrigger,
} from "@/components/ui/alert-dialog"
import { JobListQuery, JobListResponse, GlobalConfigItemValue, fetchAdminJobList, submitOcEntry, updateOcState } from "@/lib/api"
import { formatDateTime } from '@/lib/utils'
import { useToast } from "@/hooks/use-toast"
import { getConfigValue } from "@/lib/config"
import { Switch } from "@/components/ui/switch"
import {
    Pagination,
    PaginationContent,
    PaginationEllipsis,
    PaginationItem,
    PaginationLink,
    PaginationNext,
    PaginationPrevious,
} from "@/components/ui/pagination"

interface JobOffer {
    id: string | number
    companyName: string
    companyType: string
    location: string
    recruitmentType: string
    recruitmentTarget: string
    position: string
    applicationProgress: string
    lastUpdatedTime: string
    updateTime: string
    deadline: string
    relatedLinks: string
    jobAnnouncement: string
    internalReferralCode: string
    state: number,
    remarks: string
}
const PAGE_SIZE = 10
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
    const [isDialogOpen, setIsDialogOpen] = useState(false)
    const [ocToDelete, setOcToDelete] = useState<JobOffer | null>(null)

    const { toast } = useToast();
    const [companyTypes, setCompanyTypes] = useState<GlobalConfigItemValue[]>([]);
    const [recruitmentTypes, setRecruitmentTypes] = useState<GlobalConfigItemValue[]>([]);
    const [recruitmentTarget, setRecruitmentTarget] = useState<GlobalConfigItemValue[]>([]);

    const handleDelete = async () => {
        if (!ocToDelete) return;
        updateOcState({ id: ocToDelete.id as number, state: -1 }).then((res) => {
            toast({
                title: "成功",
                description: "已删除",
            })
            setJobOffers(jobOffers.filter((offer) => offer.id != ocToDelete.id))
            setIsDialogOpen(false)
        }).catch(err => {
            setIsDialogOpen(false)
            toast({
                title: "失败",
                description: err.message,
                variant: "destructive",
            })
        })
    }

    const handleSave = (offer: JobOffer) => {
        submitOcEntry({ ...offer, id: offer.id }).then((res) => {
            toast({ title: '保存成功', description: '职位信息已保存' })
            setJobOffers(jobOffers.map((o) => (o.id === offer.id ? offer : o)))
            setEditingOffer(null)
        }).catch((e) => {
            toast({ title: '保存失败', description: e.message, variant: "destructive" })
        })
    }

    const handleOcStateChange = async (id: number, state: number) => {
        updateOcState({ id: id, state: state }).then((res) => {
            toast({ title: '状态更新成功', description: '职位状态已更新' })
            setJobOffers(jobOffers.map((o) => (o.id === id ? { ...o, state: state } : o)))
        }).catch((e) => {
            toast({ title: '状态更新失败', description: e.message })
        })
    }


    const fetchData = async (params: JobListQuery = {}) => {
        setLoading(true)
        setError(null)
        try {
            const res: JobListResponse = await fetchAdminJobList({ page, size: pageSize, ...filters, ...params })
            console.log('返回的列表信息：', res);
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
        getConfigValue('oc', 'CompanyTypeEnum').then(setCompanyTypes);
        getConfigValue('oc', 'RecruitmentTypeEnum').then(setRecruitmentTypes);
        getConfigValue('oc', 'RecruitmentTargetEnum').then(setRecruitmentTarget);
        // eslint-disable-next-line
    }, [page, filters])

    const handleFilterChange = (key: keyof JobListQuery, value: string) => {
        if (value == '-1') {
            value = ''
        }
        setFilters((prev) => ({ ...prev, [key]: value }))
        setPage(1)
    }

    return (
        <div className="min-h-screen bg-gray-50">
            <header className="bg-white border-b">
                <div className="full-w mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex justify-between items-center h-16">
                        <h1 className="text-2xl font-bold text-gray-900">职位管理</h1>
                    </div>
                </div>
            </header>

            <div className="full-w mx-auto px-4 sm:px-6 lg:px-8 py-6">
                <div className="mb-6 flex flex-wrap gap-2 items-center">
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

                    {recruitmentTarget && (
                        <Select value={filters.recruitmentTarget || ''} onValueChange={value => handleFilterChange('recruitmentTarget', value)}>
                            <SelectTrigger className="w-32"><SelectValue placeholder="招聘对象" /></SelectTrigger>
                            <SelectContent>
                                <SelectItem value="-1">全部</SelectItem>
                                {recruitmentTarget.map(type => (
                                    <SelectItem key={type.intro as string} value={type.intro as string}>
                                        {type.intro}
                                    </SelectItem>
                                ))}
                            </SelectContent>
                        </Select>
                    )}

                    <Input placeholder="工作地点" className="w-32" value={filters.jobLocation || ''} onChange={e => handleFilterChange('jobLocation', e.target.value)} />
                    <Input placeholder="岗位" className="w-36" value={filters.position || ''} onChange={e => handleFilterChange('position', e.target.value)} />
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
                                <TableHead>状态</TableHead>
                                <TableHead>编辑时间</TableHead>
                                <TableHead>操作</TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {loading ? (
                                <TableRow><TableCell colSpan={8}>加载中...</TableCell></TableRow>
                            ) : error ? (
                                <TableRow><TableCell colSpan={8} className="text-red-600">{error}</TableCell></TableRow>
                            ) : jobOffers?.length === 0 ? (
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
                                        <TableCell className="whitespace-nowrap text-center">
                                            <Switch
                                                checked={offer.state === 1}
                                                onCheckedChange={(newState) => handleOcStateChange(offer.id, newState ? 1 : 0)}
                                            />
                                        </TableCell>
                                        <TableCell>{formatDateTime(offer.updateTime || offer.lastUpdatedTime)}</TableCell>
                                        <TableCell>
                                            <div className="flex space-x-2">
                                                <Button size="sm" variant="outline" onClick={() => setEditingOffer(offer)}>
                                                    编辑
                                                </Button>
                                                <Button size="sm" variant="destructive" onClick={() => {
                                                    setOcToDelete(offer)
                                                    setIsDialogOpen(true);
                                                }}>
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

                <AlertDialog open={!!isDialogOpen} onOpenChange={(isOpen) => !isOpen && setOcToDelete(null)}>
                    <AlertDialogContent>
                        <AlertDialogHeader>
                            <AlertDialogTitle>确定要删除吗？</AlertDialogTitle>
                            <AlertDialogDescription>
                                此操作无法撤销。这将永久删除您的字典配置。
                            </AlertDialogDescription>
                        </AlertDialogHeader>
                        <AlertDialogFooter>
                            <AlertDialogCancel>取消</AlertDialogCancel>
                            <AlertDialogAction onClick={handleDelete}>确定</AlertDialogAction>
                        </AlertDialogFooter>
                    </AlertDialogContent>
                </AlertDialog>

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

                {editingOffer && (
                    <Dialog
                        open={true}
                        onOpenChange={() => {
                            setEditingOffer(null)
                            setIsAddingNew(false)
                        }}
                    >
                        <DialogContent className="max-w-4xl max-h-[85vh] overflow-y-auto">
                            <DialogHeader>
                                <DialogTitle>{isAddingNew ? "添加新职位" : "编辑职位"}</DialogTitle>
                            </DialogHeader>
                            <div className="grid grid-cols-3 gap-4 py-2">
                                <div>
                                    <label className="text-sm font-medium">公司名称</label>
                                    <Input
                                        value={editingOffer.companyName}
                                        onChange={(e) => setEditingOffer({ ...editingOffer, companyName: e.target.value })}
                                    />
                                </div>
                                <div className={companyTypes.some(item => item.intro === editingOffer.companyType) ? "" : "text-red-500"}>
                                    <label className="text-sm font-medium">公司类型</label>
                                    <Select
                                        value={editingOffer.companyType}
                                        onValueChange={(value) => setEditingOffer({ ...editingOffer, companyType: value })}
                                    >
                                        <SelectTrigger>
                                            <SelectValue placeholder="选择公司类型" />
                                        </SelectTrigger>
                                        <SelectContent>
                                            {companyTypes.map(option => (
                                                <SelectItem key={option.intro as string} value={option.intro as string}>{option.intro}</SelectItem>
                                            ))}
                                        </SelectContent>
                                    </Select>
                                </div>
                                <div className={recruitmentTypes.some(item => item.intro === editingOffer.recruitmentType) ? "" : "text-red-500"}>
                                    <label className="text-sm font-medium">招聘类型</label>
                                    <Select
                                        value={editingOffer.recruitmentType}
                                        onValueChange={(value) => setEditingOffer({ ...editingOffer, recruitmentType: value })}
                                    >
                                        <SelectTrigger>
                                            <SelectValue placeholder="选择招聘类型" />
                                        </SelectTrigger>
                                        <SelectContent>
                                            {recruitmentTypes.map(type => (
                                                <SelectItem key={type.intro as string} value={type.intro as string}>
                                                    {type.intro}
                                                </SelectItem>
                                            ))}
                                        </SelectContent>
                                    </Select>
                                </div>
                                <div className={recruitmentTarget.some(item => item.intro === editingOffer.recruitmentTarget) ? "" : "text-red-500"}>
                                    <label className="text-sm font-medium">招聘对象</label>
                                    <Select
                                        value={editingOffer.recruitmentTarget}
                                        onValueChange={(value) => setEditingOffer({ ...editingOffer, recruitmentTarget: value })}
                                    >
                                        <SelectTrigger>
                                            <SelectValue placeholder="选择招聘对象" />
                                        </SelectTrigger>
                                        <SelectContent>
                                            {recruitmentTarget.map(type => (
                                                <SelectItem key={type.intro as string} value={type.intro as string}>
                                                    {type.intro}
                                                </SelectItem>
                                            ))}
                                        </SelectContent>
                                    </Select>
                                </div>
                                <div className={editingOffer.deadline ? '' : 'text-red-500'}>
                                    <label className="text-sm font-medium">投递截止</label>
                                    <Input
                                        value={editingOffer.deadline}
                                        onChange={(e) => setEditingOffer({ ...editingOffer, deadline: e.target.value })}
                                    />
                                </div>
                                <div className="col-span-1">
                                    <label className="text-sm font-medium">发布时间</label>
                                    <Input
                                        className="w-full p-2 border rounded-md"
                                        value={editingOffer.lastUpdatedTime}
                                        onChange={(e) => setEditingOffer({ ...editingOffer, lastUpdatedTime: e.target.value })}
                                    />
                                </div>
                                <div className={editingOffer.position ? 'col-span-2' : 'text-red-500 col-span-2'}>
                                    <label className="text-sm font-medium">岗位描述</label>
                                    <textarea
                                        rows={2}
                                        className="w-full p-2 border rounded-md"
                                        value={editingOffer.position}
                                        onChange={(e) => setEditingOffer({ ...editingOffer, position: e.target.value })}
                                    />
                                </div>
                                <div className={editingOffer.location ? 'col-span-1' : 'text-red-500 col-span-1'}>
                                    <label className="text-sm font-medium">工作地点</label>
                                    <textarea
                                        className="w-full p-2 border rounded-md"
                                        rows={2}
                                        value={editingOffer.location}
                                        onChange={(e) => setEditingOffer({ ...editingOffer, location: e.target.value })}
                                    />
                                </div>

                                <div className={editingOffer.relatedLinks ? 'col-span-1' : 'text-red-500 col-span-1'}>
                                    <label className="text-sm font-medium">相关链接</label>
                                    <input
                                        className="w-full p-2 border rounded-md"
                                        value={editingOffer.relatedLinks}
                                        onChange={(e) => setEditingOffer({ ...editingOffer, relatedLinks: e.target.value })}
                                    />
                                </div>
                                <div className={editingOffer.jobAnnouncement ? 'col-span-1' : 'text-red-500 col-span-1'}>
                                    <label className="text-sm font-medium">公告链接</label>
                                    <input
                                        className="w-full p-2 border rounded-md"
                                        value={editingOffer.jobAnnouncement}
                                        onChange={(e) => setEditingOffer({ ...editingOffer, jobAnnouncement: e.target.value })}
                                    />
                                </div>
                                <div className="col-span-1">
                                    <label className="text-sm font-medium">内推码</label>
                                    <input
                                        className="w-full p-2 border rounded-md"
                                        value={editingOffer.internalReferralCode}
                                        onChange={(e) => setEditingOffer({ ...editingOffer, internalReferralCode: e.target.value })}
                                    />
                                </div>
                                <div className="col-span-3">
                                    <label className="text-sm font-medium">备注说明</label>
                                    <textarea
                                        rows={3}
                                        className="w-full p-2 border rounded-md"
                                        value={editingOffer.remarks}
                                        onChange={(e) => setEditingOffer({ ...editingOffer, remarks: e.target.value })}
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