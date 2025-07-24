"use client"

import { useState, useEffect } from "react"
import { useRouter, useSearchParams } from "next/navigation"
import { ArrowLeft, MapPin, Calendar, Users, Building, Briefcase, Clock, ExternalLink } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Separator } from "@/components/ui/separator"
import { jobDetail } from "@/lib/api"

interface JobDetail {
    id: number
    draftId: number
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
    createTime: number
    updateTime: number
}

export default function JobDetailPage() {
    const searchParams = useSearchParams()
    const router = useRouter()
    const [job, setJob] = useState<JobDetail | null>(null)
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)

    useEffect(() => {
        const id = searchParams.get("id")
        if (!id) {
            setJob(null)
            setError("未指定职位ID")
            return
        }
        setLoading(true)
        setError(null)
        jobDetail(Number(id))
            .then((data) => setJob(data))
            .catch((err) => {
                setJob(null)
                setError(err.message || "获取职位信息失败")
            })
            .finally(() => setLoading(false))
    }, [searchParams])

    if (loading) {
        return (
            <div className="min-h-screen flex items-center justify-center">
                <div className="text-gray-500 text-lg">加载中...</div>
            </div>
        )
    }
    if (error) {
        return (
            <div className="min-h-screen flex items-center justify-center">
                <div className="text-center">
                    <h2 className="text-2xl font-bold text-red-600 mb-4">{error}</h2>
                    <Button onClick={() => router.back()}>返回列表</Button>
                </div>
            </div>
        )
    }
    if (!job) {
        return (
            <div className="min-h-screen bg-gray-50 flex items-center justify-center">
                <div className="text-center">
                    <h2 className="text-2xl font-bold text-gray-900 mb-4">职位不存在</h2>
                    <Button onClick={() => router.back()}>返回列表</Button>
                </div>
            </div>
        )
    }

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Header */}
            <header className="bg-white border-b">
                <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex items-center h-16">
                        <Button variant="ghost" onClick={() => router.back()} className="mr-4">
                            <ArrowLeft className="h-4 w-4 mr-2" />
                            返回列表
                        </Button>
                        <h1 className="text-xl font-semibold text-gray-900">职位详情</h1>
                    </div>
                </div>
            </header>

            <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                {/* Job Header */}
                <Card className="mb-6">
                    <CardHeader>
                        <div className="flex justify-between items-start">
                            <div>
                                <CardTitle className="text-2xl mb-2">{job.companyName}</CardTitle>
                                <CardDescription className="text-lg text-gray-700 mb-4">{job.position}</CardDescription>
                                <div className="flex flex-wrap gap-2 mb-4">
                                    <Badge variant={job.companyType === "民企" ? "default" : job.companyType === "央国企" ? "secondary" : "outline"}>
                                        <Building className="h-3 w-3 mr-1" />
                                        {job.companyType}
                                    </Badge>
                                    <Badge variant="outline" className="text-pink-600 border-pink-600">
                                        <Briefcase className="h-3 w-3 mr-1" />
                                        {job.recruitmentType}
                                    </Badge>
                                    <Badge variant="outline" className="text-blue-600 border-blue-600">
                                        <Users className="h-3 w-3 mr-1" />
                                        {job.recruitmentTarget}
                                    </Badge>
                                </div>
                                <div className="flex items-center text-sm text-gray-600 space-x-4">
                                    <div className="flex items-center">
                                        <MapPin className="h-4 w-4 mr-1" />
                                        {job.jobLocation}
                                    </div>
                                    <div className="flex items-center">
                                        <Calendar className="h-4 w-4 mr-1" />
                                        更新时间: {job.lastUpdatedTime}
                                    </div>
                                    <div className="flex items-center">
                                        <Clock className="h-4 w-4 mr-1" />
                                        截止时间: {job.deadline}
                                    </div>
                                </div>
                            </div>
                            <div className="flex flex-col space-y-2">
                                <Button className="bg-blue-500 hover:bg-blue-600">立即投递</Button>
                                {job.jobAnnouncement && (
                                    <Button variant="outline" className="text-green-600 border-green-600 hover:bg-green-50 bg-transparent" asChild>
                                        <a href={job.jobAnnouncement} target="_blank" rel="noopener noreferrer">
                                            查看公告
                                        </a>
                                    </Button>
                                )}
                            </div>
                        </div>
                    </CardHeader>
                </Card>

                <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                    {/* Main Content */}
                    <div className="lg:col-span-2 space-y-6">
                        {/* Job Description */}
                        <Card>
                            <CardHeader>
                                <CardTitle>备注</CardTitle>
                            </CardHeader>
                            <CardContent>
                                <p className="text-gray-700 leading-relaxed">{job.remarks || "-"}</p>
                            </CardContent>
                        </Card>
                    </div>

                    {/* Sidebar */}
                    <div className="space-y-6">
                        {/* Company Info */}
                        <Card>
                            <CardHeader>
                                <CardTitle>公司信息</CardTitle>
                            </CardHeader>
                            <CardContent className="space-y-4">
                                <div>
                                    <h4 className="font-medium text-gray-900 mb-1">公司名称</h4>
                                    <p className="text-gray-600">{job.companyName}</p>
                                </div>
                                <Separator />
                                <div>
                                    <h4 className="font-medium text-gray-900 mb-1">公司类型</h4>
                                    <p className="text-gray-600">{job.companyType}</p>
                                </div>
                                <Separator />
                                <div>
                                    <h4 className="font-medium text-gray-900 mb-1">工作地点</h4>
                                    <p className="text-gray-600">{job.jobLocation }</p>
                                </div>
                            </CardContent>
                        </Card>

                        {/* Application Status */}
                        <Card>
                            <CardHeader>
                                <CardTitle>投递状态</CardTitle>
                            </CardHeader>
                            <CardContent>
                                <div className="text-center py-4">
                                    <div className="inline-flex items-center px-3 py-1 rounded-full text-sm bg-gray-100 text-gray-600 mb-4">
                                        {job.deliveryProgress}
                                    </div>
                                    <p className="text-sm text-gray-500 mb-4">您还未投递此职位</p>
                                    <Button className="w-full bg-blue-500 hover:bg-blue-600">立即投递简历</Button>
                                </div>
                            </CardContent>
                        </Card>

                        {/* Related Links */}
                        {job.relatedLink && (
                            <Card>
                                <CardHeader>
                                    <CardTitle>相关链接</CardTitle>
                                </CardHeader>
                                <CardContent>
                                    <Button variant="outline" className="w-full bg-transparent" asChild>
                                        <a href={job.relatedLink} target="_blank" rel="noopener noreferrer">
                                            <ExternalLink className="h-4 w-4 mr-2" />
                                            查看更多信息
                                        </a>
                                    </Button>
                                </CardContent>
                            </Card>
                        )}
                    </div>
                </div>
            </div>
        </div>
    )
} 