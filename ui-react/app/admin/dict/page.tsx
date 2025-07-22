"use client"

import { useEffect, useState } from "react"
import {
    Card,
    CardContent,
    CardHeader,
    CardTitle,
} from "@/components/ui/card"
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from "@/components/ui/table"
import { fetchDictList, DictListItem, saveDict, DictSaveReq, updateDictState, deleteDict } from "@/lib/api"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Input } from "@/components/ui/input"
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
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
} from "@/components/ui/dialog"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select"
import { useToast } from "@/components/ui/use-toast"
import { Switch } from "@/components/ui/switch"
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group"
import {
    Pagination,
    PaginationContent,
    PaginationEllipsis,
    PaginationItem,
    PaginationLink,
    PaginationNext,
    PaginationPrevious,
} from "@/components/ui/pagination"

import { getConfigValue } from "@/lib/config"
import { GlobalConfigItemValue } from "@/lib/api"

const newDictInitValue: DictSaveReq = {
    app: "",
    key: "",
    value: "",
    intro: "",
    remark: "",
    scope: 0,
    state: 1,
}

export default function DictPage() {
    const [dicts, setDicts] = useState<DictListItem[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const [app, setApp] = useState("")
    const [key, setKey] = useState("")
    const [pagination, setPagination] = useState({ page: 1, size: 10, total: 0 })
    const [isSaving, setIsSaving] = useState(false)
    const { toast } = useToast()

    const [isDialogOpen, setIsDialogOpen] = useState(false)
    const [activeDict, setActiveDict] = useState<DictSaveReq>(newDictInitValue)
    const [isEditing, setIsEditing] = useState(false)
    const [dictToDelete, setDictToDelete] = useState<DictListItem | null>(null)
    const [scopeOptions, setScopeOptions] = useState<GlobalConfigItemValue[]>([]);
    const [appOptions, setAppOptions] = useState<GlobalConfigItemValue[]>([]);

    useEffect(() => {
        getConfigValue('dicts', 'DictScopeEnum').then(options => {
            setScopeOptions(options);
        });
        getConfigValue('dicts', 'DictAppEnum').then(options => {
            setAppOptions(options);
        });
    }, []);

    const loadDicts = async (search: { app: string, key: string, page: number, size: number }) => {
        try {
            setLoading(true)
            const response = await fetchDictList(search)
            setDicts(response.list)
            setPagination({ page: response.page, size: response.size, total: response.total })
        } catch (err: any) {
            setError(err.message)
        } finally {
            setLoading(false)
        }
    }

    useEffect(() => {
        loadDicts({ app, key, page: pagination.page, size: pagination.size })
    }, [pagination.page, pagination.size])

    const handleSearch = () => {
        if (pagination.page === 1) {
            loadDicts({ app, key, page: 1, size: pagination.size })
        } else {
            setPagination({ ...pagination, page: 1 })
        }
    }

    const handleSave = async () => {
        try {
            setIsSaving(true)
            await saveDict(activeDict)
            toast({
                title: "成功",
                description: "字典项已保存",
            })
            setIsDialogOpen(false)
            loadDicts({ app, key, page: pagination.page, size: pagination.size }) // Refresh the list
        } catch (err: any) {
            toast({
                title: "失败",
                description: err.message,
                variant: "destructive",
            })
        } finally {
            setIsSaving(false)
        }
    }

    const handleAddNew = () => {
        setIsEditing(false)
        setActiveDict(newDictInitValue)
        setIsDialogOpen(true)
    }

    const handleEdit = (dict: DictListItem) => {
        setIsEditing(true)
        setActiveDict({ ...dict })
        setIsDialogOpen(true)
    }

    const handleDialogChange = (isOpen: boolean) => {
        setIsDialogOpen(isOpen)
        if (!isOpen) {
            setActiveDict(newDictInitValue)
            setIsEditing(false)
        }
    }

    const handleStateChange = async (id: number, newState: boolean) => {
        const state = newState ? 1 : 0
        try {
            await updateDictState(id, state)
            toast({
                title: "成功",
                description: "状态已更新",
            })
            // Optimistically update the UI
            setDicts((prevDicts) =>
                prevDicts.map((d) => (d.id === id ? { ...d, state } : d))
            )
        } catch (err: any) {
            toast({
                title: "失败",
                description: err.message,
                variant: "destructive",
            })
        }
    }

    const handleDeleteConfirm = async () => {
        if (!dictToDelete) return
        try {
            await deleteDict(dictToDelete.id)
            toast({
                title: "成功",
                description: "字典项已删除",
            })
            setDictToDelete(null)
            loadDicts({ app, key, page: pagination.page, size: pagination.size }) // Refresh the list
        } catch (err: any) {
            toast({
                title: "失败",
                description: err.message,
                variant: "destructive",
            })
        }
    }


    const renderState = (state: number) => {
        if (state === 1) {
            return <Badge variant="default">有效</Badge>
        }
        return <Badge variant="secondary">未启用</Badge>
    }

    const renderScope = (scope: number) => {
        const option = scopeOptions.find(o => Number(o.value) === scope);
        if (option) {
            return <Badge variant="outline">{option.intro}</Badge>
        }
        return <Badge variant="outline">未知配置</Badge>
    }

    return (
        <div className="min-h-screen bg-gray-50">
            <header className="bg-white border-b">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex justify-between items-center h-16">
                        <h1 className="text-2xl font-bold text-gray-900">字典管理</h1>
                        <Button onClick={handleAddNew}>添加配置</Button>
                    </div>
                </div>
            </header>
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
                <div className="flex flex-wrap gap-2 mb-4 items-center">
                    <div className="flex items-center justify-between mb-4">
                        <div className="flex items-center space-x-4">
                            <div className="flex items-center">
                                <span className="mr-2 font-medium">App：</span>
                                <Select value={app} onValueChange={setApp}>
                                    <SelectTrigger className="w-40">
                                        <SelectValue placeholder="请选择App" />
                                    </SelectTrigger>
                                    <SelectContent>
                                        {appOptions.map(option => (
                                            <SelectItem value={option.value as string} key={option.value as string}>
                                                {option.intro}
                                            </SelectItem>
                                        ))}
                                    </SelectContent>
                                </Select>
                            </div>
                            <Input
                                placeholder="Code"
                                value={key}
                                onChange={(e) => setKey(e.target.value)}
                                className="max-w-xs"
                            />
                            <Button onClick={handleSearch}>查询</Button>
                        </div>
                    </div>
                </div>
                <Card >
                    <CardContent className="pt-6">
                        {loading && <p>加载中...</p>}
                        {error && <p className="text-red-500">错误: {error}</p>}
                        {!loading && !error && (
                            <Table>
                                <TableHeader>
                                    <TableRow>
                                        <TableHead>ID</TableHead>
                                        <TableHead>App</TableHead>
                                        <TableHead>作用域</TableHead>
                                        <TableHead>Code</TableHead>
                                        <TableHead>Value</TableHead>
                                        <TableHead>说明</TableHead>
                                        <TableHead>状态</TableHead>
                                        <TableHead>备注</TableHead>
                                        <TableHead>更新时间</TableHead>
                                        <TableHead>操作</TableHead>
                                    </TableRow>
                                </TableHeader>
                                <TableBody>
                                    {dicts.map((dict) => (
                                        <TableRow key={dict.id}>
                                            <TableCell>{dict.id}</TableCell>
                                            <TableCell>{dict.app}</TableCell>
                                            <TableCell>{renderScope(dict.scope)}</TableCell>
                                            <TableCell>{dict.key}</TableCell>
                                            <TableCell className="max-w-xs truncate">{dict.value}</TableCell>
                                            <TableCell className="max-w-xs truncate">{dict.intro}</TableCell>
                                            <TableCell>
                                                <Switch
                                                    checked={dict.state === 1}
                                                    onCheckedChange={(newState) => handleStateChange(dict.id, newState)}
                                                />
                                            </TableCell>
                                            <TableCell>{dict.remark}</TableCell>
                                            <TableCell>{new Date(dict.updateTime).toLocaleString()}</TableCell>
                                            <TableCell className="space-x-2">
                                                <Button variant="link" size="sm" onClick={() => handleEdit(dict)}>编辑</Button>
                                                <Button variant="link" size="sm" className="text-red-600" onClick={() => setDictToDelete(dict)}>删除</Button>
                                            </TableCell>
                                        </TableRow>
                                    ))}
                                </TableBody>
                            </Table>
                        )}
                        <div className="mt-4 flex justify-end">
                            <Pagination>
                                <PaginationContent>
                                    <PaginationItem>
                                        <PaginationPrevious
                                            href="#"
                                            onClick={(e) => {
                                                e.preventDefault()
                                                if (pagination.page > 1) {
                                                    setPagination({ ...pagination, page: pagination.page - 1 })
                                                }
                                            }}
                                            className={pagination.page <= 1 ? "pointer-events-none opacity-50" : ""}
                                        />
                                    </PaginationItem>
                                    <PaginationItem>
                                        <span className="text-sm text-muted-foreground">
                                            第 {pagination.page} 页 / 共 {Math.ceil(pagination.total / pagination.size)} 页
                                        </span>
                                    </PaginationItem>
                                    <PaginationItem>
                                        <PaginationNext
                                            href="#"
                                            onClick={(e) => {
                                                e.preventDefault()
                                                if (pagination.page * pagination.size < pagination.total) {
                                                    setPagination({ ...pagination, page: pagination.page + 1 })
                                                }
                                            }}
                                            className={pagination.page * pagination.size >= pagination.total ? "pointer-events-none opacity-50" : ""}
                                        />
                                    </PaginationItem>
                                </PaginationContent>
                            </Pagination>
                        </div>
                    </CardContent>
                </Card>
            </div>

            <Dialog open={isDialogOpen} onOpenChange={handleDialogChange}>
                <DialogContent className="sm:max-w-[625px]">
                    <DialogHeader>
                        <DialogTitle>{isEditing ? '编辑配置' : '添加新配置'}</DialogTitle>
                        <DialogDescription>
                            {isEditing ? '在这里修改您的字典配置项。' : '在这里添加一个新的字典配置项。'}
                        </DialogDescription>
                    </DialogHeader>
                    <div className="grid gap-4 py-4">
                        <div className="grid grid-cols-4 items-center gap-4">
                            <Label htmlFor="app" className="text-right">App</Label>
                            <Select
                                value={activeDict.app}
                                onValueChange={(value) => setActiveDict({ ...activeDict, app: value })}
                            >
                                <SelectTrigger className="w-40">
                                    <SelectValue placeholder="请选择App" />
                                </SelectTrigger>
                                <SelectContent>
                                    {appOptions.map(option => (
                                        <SelectItem value={option.value as string} key={option.value as string}>
                                            {option.intro}
                                        </SelectItem>
                                    ))}
                                </SelectContent>
                            </Select>
                        </div>
                        <div className="grid grid-cols-4 items-center gap-4">
                            <Label htmlFor="key" className="text-right">Code</Label>
                            <Input id="key" value={activeDict.key} onChange={(e) => setActiveDict({ ...activeDict, key: e.target.value })} className="col-span-3" />
                        </div>
                        <div className="grid grid-cols-4 items-center gap-4">
                            <Label htmlFor="value" className="text-right">Value</Label>
                            <Textarea id="value" value={activeDict.value} onChange={(e) => setActiveDict({ ...activeDict, value: e.target.value })} className="col-span-3" />
                        </div>
                        <div className="grid grid-cols-4 items-center gap-4">
                            <Label htmlFor="intro" className="text-right">说明</Label>
                            <Textarea id="intro" value={activeDict.intro} onChange={(e) => setActiveDict({ ...activeDict, intro: e.target.value })} className="col-span-3" />
                        </div>
                        <div className="grid grid-cols-4 items-center gap-4">
                            <Label htmlFor="scope" className="text-right">作用域</Label>
                            <RadioGroup
                                value={String(activeDict.scope)}
                                onValueChange={(value) => setActiveDict({ ...activeDict, scope: Number(value) })}
                                className="col-span-3 flex items-center space-x-4"
                            >
                                {scopeOptions.map(option => (
                                    <div className="flex items-center space-x-2" key={option.value as string}>
                                        <RadioGroupItem value={option.value as string} id={`scope-${option.value}`} />
                                        <Label htmlFor={`scope-${option.value}`}>{option.intro}</Label>
                                    </div>
                                ))}
                            </RadioGroup>
                        </div>
                        <div className="grid grid-cols-4 items-center gap-4">
                            <Label htmlFor="intro" className="text-right">备注</Label>
                            <Textarea id="remark" value={activeDict.remark} onChange={(e) => setActiveDict({ ...activeDict, remark: e.target.value })} className="col-span-3" />
                        </div>
                        <div className="grid grid-cols-4 items-center gap-4">
                            <Label htmlFor="state" className="text-right">状态</Label>
                            <RadioGroup
                                value={String(activeDict.state)}
                                onValueChange={(value) => setActiveDict({ ...activeDict, state: Number(value) })}
                                className="col-span-3 flex items-center space-x-4"
                            >
                                <div className="flex items-center space-x-2">
                                    <RadioGroupItem value="1" id="state-1" />
                                    <Label htmlFor="state-1">有效</Label>
                                </div>
                                <div className="flex items-center space-x-2">
                                    <RadioGroupItem value="0" id="state-0" />
                                    <Label htmlFor="state-0">未启用</Label>
                                </div>
                            </RadioGroup>
                        </div>
                    </div>
                    <DialogFooter>
                        <Button type="submit" onClick={handleSave} disabled={isSaving}>
                            {isSaving ? '保存中...' : '保存'}
                        </Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>
            <AlertDialog open={!!dictToDelete} onOpenChange={(isOpen) => !isOpen && setDictToDelete(null)}>
                <AlertDialogContent>
                    <AlertDialogHeader>
                        <AlertDialogTitle>确定要删除吗？</AlertDialogTitle>
                        <AlertDialogDescription>
                            此操作无法撤销。这将永久删除您的字典配置。
                        </AlertDialogDescription>
                    </AlertDialogHeader>
                    <AlertDialogFooter>
                        <AlertDialogCancel>取消</AlertDialogCancel>
                        <AlertDialogAction onClick={handleDeleteConfirm}>确定</AlertDialogAction>
                    </AlertDialogFooter>
                </AlertDialogContent>
            </AlertDialog>
        </div>
    )
} 