"use client"

import { useState } from "react"
import { Search, Bell, User, QrCode, Settings } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog"
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from "@/components/ui/pagination"
import Link from "next/link"
import { fetchJobList, JobListResponse } from "@/lib/api"
import { useEffect } from "react"
import { useRouter } from "next/navigation"

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

export default function HomePage() {
  const [user, setUser] = useState<{ name: string; isAdmin: boolean } | null>(null)
  const [currentView, setCurrentView] = useState<"frontend" | "admin">("frontend")
  const [jobOffers, setJobOffers] = useState<JobOffer[]>([])
  const [filteredOffers, setFilteredOffers] = useState<JobOffer[]>([])
  const [searchFilters, setSearchFilters] = useState({
    companyName: "",
    companyType: "",
    location: "",
    recruitmentType: "",
    recruitmentTarget: "",
    position: "",
  })
  const [currentPage, setCurrentPage] = useState(1)
  const itemsPerPage = 10
  const [total, setTotal] = useState(0)
  const [queryParams, setQueryParams] = useState<any>({})
  const router = useRouter()

  // 请求岗位数据（带分页）
  const loadJobList = (params: any = {}, page = currentPage) => {
    fetchJobList({
      page,
      size: itemsPerPage,
      ...params,
    })
      .then((data: JobListResponse) => {
        const mapped = data.list.map((item: any) => ({
          id: item.id,
          companyName: item.companyName,
          companyType: item.companyType,
          location: item.jobLocation,
          recruitmentType: item.recruitmentType,
          recruitmentTarget: item.recruitmentTarget,
          position: item.position,
          applicationProgress: item.deliveryProgress,
          updateTime: item.lastUpdatedTime ? item.lastUpdatedTime.split("T")[0] : "",
          deadline: item.deadline,
          relatedLinks: item.relatedLink,
          recruitmentNotice: item.jobAnnouncement,
          referralCode: item.internalReferralCode,
          notes: item.remarks,
        }))
        setJobOffers(mapped)
        setFilteredOffers(mapped)
        setTotal(data.total)
      })
      .catch((err: any) => {
        console.error("获取岗位数据失败", err)
      })
  }

  useEffect(() => {
    loadJobList(queryParams, currentPage)
    // eslint-disable-next-line
  }, [currentPage])

  const handleSearch = () => {
    const params = {
      companyName: searchFilters.companyName || undefined,
      companyType: (searchFilters.companyType == 'all' ? undefined : searchFilters.companyType) || undefined,
      jobLocation: searchFilters.location || undefined,
      recruitmentType: (searchFilters.recruitmentType == 'all' ? undefined : searchFilters.recruitmentType) || undefined,
      recruitmentTarget: (searchFilters.recruitmentTarget == 'all' ? undefined : searchFilters.recruitmentTarget) || undefined,
      position: searchFilters.position || undefined,
    }
    setQueryParams(params)
    setCurrentPage(1)
    loadJobList(params, 1)
  }

  const handleReset = () => {
    setSearchFilters({
      companyName: "",
      companyType: "",
      location: "",
      recruitmentType: "",
      recruitmentTarget: "",
      position: "",
    })
    setQueryParams({})
    setCurrentPage(1)
    loadJobList({}, 1)
  }

  const handleLogin = () => {
    // Simulate login - in real app this would be QR code authentication
    setUser({ name: "管理员", isAdmin: true })
  }

  const totalPages = Math.ceil(total / itemsPerPage)
  const paginatedOffers = filteredOffers // 直接用接口返回的分页数据

  if (currentView === "admin" && user?.isAdmin) {
    router.push("/admin")
    return null
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center space-x-8">
              <div className="flex items-center">
                <span className="text-2xl font-bold text-blue-600">🏢 来个 OC</span>
                <span className="ml-2 text-yellow-500">🏠</span>
              </div>
              <nav className="flex space-x-6">
                <a href="#" className="text-gray-700 hover:text-blue-600">
                  招聘
                </a>
                <a href="#" className="text-gray-700 hover:text-blue-600">
                  实习
                </a>
              </nav>
            </div>
            <div className="flex items-center space-x-4">
              <Bell className="h-5 w-5 text-gray-500" />
              {user ? (
                <div className="flex items-center space-x-2">
                  <span className="text-sm text-gray-700">欢迎, {user.name}</span>
                  {user.isAdmin && (
                    <Button variant="outline" size="sm" onClick={() => setCurrentView("admin")} className="ml-2">
                      <Settings className="h-4 w-4 mr-1" />
                      管理后台
                    </Button>
                  )}
                </div>
              ) : (
                <Dialog>
                  <DialogTrigger asChild>
                    <Button variant="outline" size="sm">
                      <User className="h-4 w-4 mr-1" />
                      登录
                    </Button>
                  </DialogTrigger>
                  <DialogContent className="sm:max-w-md">
                    <DialogHeader>
                      <DialogTitle>扫码登录</DialogTitle>
                    </DialogHeader>
                    <div className="flex flex-col items-center space-y-4 py-4">
                      <div className="w-48 h-48 bg-gray-100 border-2 border-dashed border-gray-300 flex items-center justify-center">
                        <QrCode className="h-16 w-16 text-gray-400" />
                      </div>
                      <p className="text-sm text-gray-600 text-center">请使用手机扫描二维码登录</p>
                      <Button onClick={handleLogin} className="w-full">
                        模拟登录 (演示用)
                      </Button>
                    </div>
                  </DialogContent>
                </Dialog>
              )}
            </div>
          </div>
        </div>
      </header>

      {/* Search Filters */}
      <div className="mx-auto px-4 sm:px-6 lg:px-8 py-6">
        <div className="bg-white rounded-lg shadow p-6 mb-6">
          <div className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-6 gap-4 mb-4">
            <Input
              placeholder="公司名称"
              value={searchFilters.companyName}
              onChange={(e) => setSearchFilters((prev) => ({ ...prev, companyName: e.target.value }))}
            />
            <Select
              value={searchFilters.companyType}
              onValueChange={(value) => setSearchFilters((prev) => ({ ...prev, companyType: value }))}
            >
              <SelectTrigger>
                <SelectValue placeholder="公司类型" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">全部</SelectItem>
                <SelectItem value="民企">民企</SelectItem>
                <SelectItem value="央国企">央国企</SelectItem>
                <SelectItem value="事业单位">事业单位</SelectItem>
                <SelectItem value="外企">外企</SelectItem>
              </SelectContent>
            </Select>
            <Input
              placeholder="工作地点"
              value={searchFilters.location}
              onChange={(e) => setSearchFilters((prev) => ({ ...prev, location: e.target.value }))}
            />
            <Select
              value={searchFilters.recruitmentType}
              onValueChange={(value) => setSearchFilters((prev) => ({ ...prev, recruitmentType: value }))}
            >
              <SelectTrigger>
                <SelectValue placeholder="招聘类型" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">全部</SelectItem>
                <SelectItem value="春招">春招</SelectItem>
                <SelectItem value="秋招">秋招</SelectItem>
                <SelectItem value="秋招提前批">秋招提前批</SelectItem>
                <SelectItem value="日常招聘">日常招聘</SelectItem>
              </SelectContent>
            </Select>
            <Select
              value={searchFilters.recruitmentTarget}
              onValueChange={(value) => setSearchFilters((prev) => ({ ...prev, recruitmentTarget: value }))}
            >
              <SelectTrigger>
                <SelectValue placeholder="招聘对象" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">全部</SelectItem>
                <SelectItem value="2025年毕业生">2025年毕业生</SelectItem>
                <SelectItem value="2026年毕业生">2026年毕业生</SelectItem>
                <SelectItem value="社会招聘">社会招聘</SelectItem>
              </SelectContent>
            </Select>
            <Input
              placeholder="岗位名称"
              value={searchFilters.position}
              onChange={(e) => setSearchFilters((prev) => ({ ...prev, position: e.target.value }))}
            />
          </div>
          <div className="flex space-x-4">
            <Button onClick={handleSearch} className="bg-blue-500 hover:bg-blue-600">
              <Search className="h-4 w-4 mr-2" />
              查询
            </Button>
            <Button variant="outline" onClick={handleReset}>
              重置
            </Button>
          </div>
        </div>

        {/* Job Listings Table */}
        <div className="bg-white rounded-lg shadow overflow-x-auto">
          <Table className="min-w-[1200px] table-fixed">
            <TableHeader>
              <TableRow>
                <TableHead className="w-32 break-words">公司名称</TableHead>
                <TableHead className="w-32 break-words">公司类型</TableHead>
                <TableHead className="w-32 break-words">工作地点</TableHead>
                <TableHead className="w-24 break-words">招聘类型</TableHead>
                <TableHead className="w-28 break-words">招聘对象</TableHead>
                <TableHead className="w-56 break-words">岗位(大都不限专业)</TableHead>
                <TableHead className="w-20 break-words">投递进度</TableHead>
                <TableHead className="w-24 break-words">更新时间</TableHead>
                <TableHead className="w-24 break-words">投递截止</TableHead>
                <TableHead className="w-32 break-words">相关链接</TableHead>
                <TableHead className="w-32 break-words">招聘公告</TableHead>
                <TableHead className="w-24 break-words">内推码</TableHead>
                <TableHead className="w-32 break-words">备注</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {paginatedOffers.map((offer) => (
                <TableRow key={String(offer.id)} className="hover:bg-gray-50 cursor-pointer">
                  <TableCell className="font-medium break-words w-32">
                    <Link href={`/job/${offer.id}`} className="text-blue-600 hover:underline">
                      {offer.companyName}
                    </Link>
                  </TableCell>
                  <TableCell className="break-words w-32">
                    <Badge className="rounded-md"
                      variant={
                        offer.companyType === "民企"
                          ? "default"
                          : offer.companyType === "央国企"
                            ? "secondary"
                            : "outline"
                      }
                    >
                      {offer.companyType}
                    </Badge>
                  </TableCell>
                  <TableCell className="break-words w-32">{offer.location}</TableCell>
                  <TableCell className="break-words w-24">
                    <Badge variant="outline" className="text-pink-600 border-pink-600 rounded-md">
                      {offer.recruitmentType}
                    </Badge>
                  </TableCell>
                  <TableCell className="break-words w-28">
                    <Badge variant="outline" className="text-blue-600 border-blue-600 rounded-md">
                      {offer.recruitmentTarget}
                    </Badge>
                  </TableCell>
                  <TableCell className="max-w-xs break-words w-56">
                    <div className="whitespace-pre-line break-words" title={offer.position}>
                      {offer.position}
                    </div>
                  </TableCell>
                  <TableCell className="break-words w-20">{offer.applicationProgress}</TableCell>
                  <TableCell className="break-words w-24">{offer.updateTime}</TableCell>
                  <TableCell className="break-words w-24">{offer.deadline}</TableCell>
                  <TableCell className="break-words w-32">
                    <a
                      href={offer.relatedLinks}
                      className="inline-flex items-center justify-center rounded-md text-sm font-medium transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:opacity-50 disabled:pointer-events-none bg-blue-500 hover:bg-blue-600 text-white h-8 px-3"
                      target="_blank"
                      rel="noopener noreferrer"
                    >
                      投递
                    </a>
                  </TableCell>
                  <TableCell className="break-words w-32">
                    <div className="flex space-x-2">
                      <Button
                        size="sm"
                        variant="outline"
                        className="text-green-600 border-green-600 hover:bg-green-50 bg-transparent"
                      >
                        公告
                      </Button>
                    </div>
                  </TableCell>
                  <TableCell className="break-words w-24">{offer.referralCode}</TableCell>
                  <TableCell className="break-words w-32">{offer.notes}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </div>

        {/* Pagination */}
        <div className="mt-6 flex flex-col items-center">
          <div className="text-sm text-gray-700 mb-2">共 {total} 条记录 当前在线人数: 221</div>
          <div>
            <Pagination>
              <PaginationContent>
                <PaginationItem>
                  <PaginationPrevious
                    href="#"
                    onClick={(e) => {
                      e.preventDefault()
                      if (currentPage > 1) setCurrentPage(currentPage - 1)
                    }}
                  />
                </PaginationItem>
                {Array.from({ length: Math.min(5, totalPages) }, (_, i) => (
                  <PaginationItem key={i + 1}>
                    <PaginationLink
                      href="#"
                      isActive={currentPage === i + 1}
                      onClick={(e) => {
                        e.preventDefault()
                        setCurrentPage(i + 1)
                      }}
                    >
                      {i + 1}
                    </PaginationLink>
                  </PaginationItem>
                ))}
                <PaginationItem>
                  <PaginationNext
                    href="#"
                    onClick={(e) => {
                      e.preventDefault()
                      if (currentPage < totalPages) setCurrentPage(currentPage + 1)
                    }}
                  />
                </PaginationItem>
              </PaginationContent>
            </Pagination>
          </div>
        </div>
      </div>
    </div>
  )
}
