"use client"

import { useState, useEffect } from "react"
import { useParams, useRouter } from "next/navigation"
import { ArrowLeft, MapPin, Calendar, Users, Building, Briefcase, Clock, ExternalLink } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Separator } from "@/components/ui/separator"

interface JobOffer {
  id: string
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
  description?: string
  requirements?: string[]
  benefits?: string[]
  contactInfo?: string
}

const mockJobDetails: Record<string, JobOffer> = {
  "1": {
    id: "1",
    companyName: "牧原-暑期夏令营",
    companyType: "民企",
    location: "河南南阳",
    recruitmentType: "秋招提前批",
    recruitmentTarget: "2026年毕业生",
    position: "畜牧兽医/机械电气/计算机/经济/食品/法学等相关专业，特别优秀者可放宽专业限制",
    applicationProgress: "未投递",
    updateTime: "2025-07-10",
    deadline: "招满为止",
    relatedLinks: "",
    recruitmentNotice: "投递",
    referralCode: "",
    notes: "-",
    description:
      "牧原股份是中国领先的生猪养殖企业，致力于为社会提供安全、优质的猪肉产品。我们正在寻找优秀的应届毕业生加入我们的暑期夏令营项目，为未来的职业发展奠定基础。",
    requirements: [
      "2026年应届毕业生",
      "畜牧兽医、机械电气、计算机、经济、食品、法学等相关专业",
      "学习成绩优秀，综合素质较高",
      "具有良好的沟通能力和团队合作精神",
      "对农业和畜牧业有浓厚兴趣",
    ],
    benefits: [
      "具有竞争力的薪酬待遇",
      "完善的培训体系",
      "广阔的职业发展空间",
      "五险一金",
      "带薪年假",
      "员工宿舍",
      "餐补",
    ],
    contactInfo: "联系邮箱：hr@muyuan.com",
  },
  "2": {
    id: "2",
    companyName: "住房城乡建设部直属事业单位",
    companyType: "事业单位",
    location: "北京",
    recruitmentType: "春招",
    recruitmentTarget: "2025年毕业生",
    position: "各种",
    applicationProgress: "未投递",
    updateTime: "2025-07-10",
    deadline: "招满为止",
    relatedLinks: "",
    recruitmentNotice: "投递",
    referralCode: "",
    notes: "-",
    description: "住房城乡建设部直属事业单位面向全国高校招聘优秀毕业生，为国家住房和城乡建设事业发展贡献力量。",
    requirements: [
      "2025年应届毕业生",
      "建筑学、城乡规划、土木工程等相关专业",
      "本科及以上学历",
      "品学兼优，身体健康",
      "具有较强的责任心和事业心",
    ],
    benefits: ["事业单位编制", "稳定的工作环境", "完善的社会保障", "良好的职业发展前景", "北京户口指标", "住房补贴"],
    contactInfo: "联系电话：010-12345678",
  },
}

export default function JobDetailPage() {
  const params = useParams()
  const router = useRouter()
  const [jobOffer, setJobOffer] = useState<JobOffer | null>(null)

  useEffect(() => {
    const id = params.id as string
    const offer = mockJobDetails[id]
    if (offer) {
      setJobOffer(offer)
    }
  }, [params.id])

  if (!jobOffer) {
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
                <CardTitle className="text-2xl mb-2">{jobOffer.companyName}</CardTitle>
                <CardDescription className="text-lg text-gray-700 mb-4">{jobOffer.position}</CardDescription>
                <div className="flex flex-wrap gap-2 mb-4">
                  <Badge
                    variant={
                      jobOffer.companyType === "民企"
                        ? "default"
                        : jobOffer.companyType === "央国企"
                          ? "secondary"
                          : "outline"
                    }
                  >
                    <Building className="h-3 w-3 mr-1" />
                    {jobOffer.companyType}
                  </Badge>
                  <Badge variant="outline" className="text-pink-600 border-pink-600">
                    <Briefcase className="h-3 w-3 mr-1" />
                    {jobOffer.recruitmentType}
                  </Badge>
                  <Badge variant="outline" className="text-blue-600 border-blue-600">
                    <Users className="h-3 w-3 mr-1" />
                    {jobOffer.recruitmentTarget}
                  </Badge>
                </div>
                <div className="flex items-center text-sm text-gray-600 space-x-4">
                  <div className="flex items-center">
                    <MapPin className="h-4 w-4 mr-1" />
                    {jobOffer.location}
                  </div>
                  <div className="flex items-center">
                    <Calendar className="h-4 w-4 mr-1" />
                    更新时间: {jobOffer.updateTime}
                  </div>
                  <div className="flex items-center">
                    <Clock className="h-4 w-4 mr-1" />
                    截止时间: {jobOffer.deadline}
                  </div>
                </div>
              </div>
              <div className="flex flex-col space-y-2">
                <Button className="bg-blue-500 hover:bg-blue-600">立即投递</Button>
                <Button variant="outline" className="text-green-600 border-green-600 hover:bg-green-50 bg-transparent">
                  查看公告
                </Button>
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
                <CardTitle>职位描述</CardTitle>
              </CardHeader>
              <CardContent>
                <p className="text-gray-700 leading-relaxed">{jobOffer.description}</p>
              </CardContent>
            </Card>

            {/* Requirements */}
            {jobOffer.requirements && (
              <Card>
                <CardHeader>
                  <CardTitle>任职要求</CardTitle>
                </CardHeader>
                <CardContent>
                  <ul className="space-y-2">
                    {jobOffer.requirements.map((req, index) => (
                      <li key={index} className="flex items-start">
                        <span className="inline-block w-2 h-2 bg-blue-500 rounded-full mt-2 mr-3 flex-shrink-0"></span>
                        <span className="text-gray-700">{req}</span>
                      </li>
                    ))}
                  </ul>
                </CardContent>
              </Card>
            )}

            {/* Benefits */}
            {jobOffer.benefits && (
              <Card>
                <CardHeader>
                  <CardTitle>福利待遇</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="grid grid-cols-2 gap-2">
                    {jobOffer.benefits.map((benefit, index) => (
                      <div key={index} className="flex items-center p-2 bg-green-50 rounded-lg">
                        <span className="text-green-600 text-sm">{benefit}</span>
                      </div>
                    ))}
                  </div>
                </CardContent>
              </Card>
            )}
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
                  <p className="text-gray-600">{jobOffer.companyName}</p>
                </div>
                <Separator />
                <div>
                  <h4 className="font-medium text-gray-900 mb-1">公司类型</h4>
                  <p className="text-gray-600">{jobOffer.companyType}</p>
                </div>
                <Separator />
                <div>
                  <h4 className="font-medium text-gray-900 mb-1">工作地点</h4>
                  <p className="text-gray-600">{jobOffer.location}</p>
                </div>
                {jobOffer.contactInfo && (
                  <>
                    <Separator />
                    <div>
                      <h4 className="font-medium text-gray-900 mb-1">联系方式</h4>
                      <p className="text-gray-600">{jobOffer.contactInfo}</p>
                    </div>
                  </>
                )}
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
                    {jobOffer.applicationProgress}
                  </div>
                  <p className="text-sm text-gray-500 mb-4">您还未投递此职位</p>
                  <Button className="w-full bg-blue-500 hover:bg-blue-600">立即投递简历</Button>
                </div>
              </CardContent>
            </Card>

            {/* Related Links */}
            {jobOffer.relatedLinks && (
              <Card>
                <CardHeader>
                  <CardTitle>相关链接</CardTitle>
                </CardHeader>
                <CardContent>
                  <Button variant="outline" className="w-full bg-transparent">
                    <ExternalLink className="h-4 w-4 mr-2" />
                    查看更多信息
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
