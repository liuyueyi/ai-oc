"use client"

import { useState, useCallback, useEffect } from "react"
import { Search, Bell, User, QrCode, Settings, ChevronDown } from "lucide-react"
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
import {
  DropdownMenu,
  DropdownMenuTrigger,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
} from "@/components/ui/dropdown-menu"
import Link from "next/link"
import { fetchJobList, JobListResponse, getWxSseUrl, postWxCallback } from "@/lib/api"
import { useRouter } from "next/navigation"
import { useSSE } from "@/hooks/useSSE"
import { QRCodeCanvas } from "qrcode.react";
import { useLoginUser } from "@/hooks/useLoginUser";

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
  const [loginOpen, setLoginOpen] = useState(false)
  const [qr, setQr] = useState("")
  const [code, setCode] = useState("")
  const [session, setSession] = useState("")
  const [mounted, setMounted] = useState(false)
  const [loginLoading, setLoginLoading] = useState(false)
  const [adminLoading, setAdminLoading] = useState(false)
  const { userInfo, setUserInfo, logout } = useLoginUser();
  const [sseUrl, setSseUrl] = useState("");

  useEffect(() => {
    setMounted(true)
  }, [])

  useEffect(() => {
    if (loginOpen) {
      setSseUrl(getWxSseUrl());
    } else {
      setSseUrl("");
    }
  }, [loginOpen]);

  const handleSSE = useCallback((type: string, payload: string) => {
    if (type === "qr") setQr(payload)
    if (type === "init") setCode(payload)
    if (type === "login") {
      setSession(payload)
      setLoginOpen(false)
      if (payload) {
        document.cookie = payload;
        const token = payload.substring(payload.indexOf('=') + 1, payload.indexOf(';'))
        // 解析 jwt 并设置全局用户信息
        const jwt = (() => {
          try {
            const base64Url = token.split('.')[1];
            const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
            const jsonPayload = decodeURIComponent(atob(base64).split('').map(function (c) {
              return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
            }).join(''));
            return JSON.parse(jsonPayload);
          } catch {
            return null;
          }
        })();
        if (jwt) {
          const info = {
            userId: jwt.uid,
            role: jwt.r,
            nickname: jwt.un,
            avatar: jwt.av
          };
          setUserInfo(info);
          if (typeof window !== 'undefined') {
            localStorage.setItem('oc-user', JSON.stringify(info));
            localStorage.setItem('oc-token', token);
          }
        }
      }
    }
  }, [setUserInfo])

  useSSE(sseUrl, handleSSE)

  const handleWxLogin = async (type: "user" | "admin", code: String) => {
    const content = type === "user" ? "login" : "admin"
    const xml = `<xml><URL><![CDATA[https://hhui.top]]></URL><ToUserName><![CDATA[一灰灰blog]]></ToUserName><FromUserName><![CDATA[demoUser-${content}]]></FromUserName><CreateTime>${Math.floor(Date.now() / 1000)}</CreateTime><MsgType><![CDATA[text]]></MsgType><Content><![CDATA[${code}]]></Content><MsgId>${Date.now()}</MsgId></xml>`
    try {
      if (type === "user") setLoginLoading(true)
      else setAdminLoading(true)
      await postWxCallback(xml)
    } catch (err) {
      // 可加 toast 错误提示
    } finally {
      setLoginLoading(false)
      setAdminLoading(false)
    }
  }

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

  if (currentView === "admin" && userInfo?.role === 3) { // 使用 userInfo 判断
    router.push("/admin")
    return null
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white border-b">
        <div className="px-10">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center space-x-8">
              <div className="flex items-center">
                <span className="text-2xl font-bold text-blue-600">🏢来个OC</span>
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
              {userInfo ? (
                <DropdownMenu>
                  <DropdownMenuTrigger asChild>
                    <span className="flex items-center cursor-pointer">
                      <img
                        src={userInfo.avatar}
                        alt="avatar"
                        className="w-8 h-8 rounded-full cursor-pointer"
                        title={userInfo.nickname || `用户${userInfo.userId}`}
                      />
                      <ChevronDown className="w-4 h-4 ml-1 text-gray-500" />
                    </span>
                  </DropdownMenuTrigger>
                  <DropdownMenuContent align="end">
                    <div className="px-3 py-2">
                      <div className="font-medium">{userInfo.nickname || `用户${userInfo.userId}`}</div>
                      <div className="text-xs text-gray-500">
                        {userInfo.role === 1 ? "普通用户" : userInfo.role === 2 ? "VIP用户" : userInfo.role === 3 ? "管理员" : "未知"}
                      </div>
                    </div>
                    <DropdownMenuSeparator />
                    <DropdownMenuItem onClick={() => { /* 跳转到个人信息页 */ }}>
                      个人信息
                    </DropdownMenuItem>
                    {userInfo.role === 3 && (
                      <DropdownMenuItem onClick={() => router.push('/admin')}>
                        管理后台
                      </DropdownMenuItem>
                    )}
                    <DropdownMenuSeparator />
                    <DropdownMenuItem onClick={logout}>
                      退出
                    </DropdownMenuItem>
                  </DropdownMenuContent>
                </DropdownMenu>
              ) : (
                mounted && (
                  <Dialog open={loginOpen} onOpenChange={setLoginOpen}>
                    <DialogTrigger asChild>
                      <Button variant="outline" size="sm">
                        <User className="h-4 w-4 mr-1" />
                        登录
                      </Button>
                    </DialogTrigger>
                    <DialogContent>
                      <DialogHeader>
                        <DialogTitle>扫码登录</DialogTitle>
                      </DialogHeader>
                      {qr ? (
                        <div className="flex flex-col items-center">
                          <QRCodeCanvas value={qr} size={180} />
                          <div className="mt-2 text-lg font-bold">验证码：{code}</div>
                          <div className="flex gap-4 mt-6">
                            <Button onClick={() => handleWxLogin("user", code)} disabled={loginLoading}>
                              {loginLoading ? "登录中..." : "普通用户登录"}
                            </Button>
                            <Button onClick={() => handleWxLogin("admin", code)} disabled={adminLoading} variant="secondary">
                              {adminLoading ? "登录中..." : "管理员登录"}
                            </Button>
                          </div>
                        </div>
                      ) : (
                        <div>等待二维码...</div>
                      )}
                    </DialogContent>
                  </Dialog>
                )
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
                    <Link href={`/job?id=${offer.id}`} className="text-blue-600 hover:underline">
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
          <div className="text-sm text-gray-700 mb-2">共 {total} 条记录 当前在线人数: -</div>
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
