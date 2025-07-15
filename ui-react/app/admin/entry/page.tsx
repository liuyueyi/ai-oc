"use client"
import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Tabs, TabsList, TabsTrigger, TabsContent } from "@/components/ui/tabs"
import { Textarea } from "@/components/ui/textarea"
import { submitAIEntry } from "@/lib/api"


const companyTypes = ["民企", "央国企", "事业单位", "外企"]
const recruitmentTypes = ["春招", "秋招", "秋招提前批", "日常招聘"]
const recruitmentTargets = ["2025年毕业生", "2026年毕业生", "社会招聘"]
const aiTypes = [{
  label: '文本',
  value: 'TEXT'
}, { label: 'html文本', value: 'HTML_TEXT' },
{ label: 'http链接', value: 'HTTP_URL' },
{ label: 'excel文件', value: 'EXCEL_FILE' },
{ label: 'csv文件', value: 'CSV_FILE' },
{ label: '图片', value: 'IMAGE' },
]
const aiModels = [
  { label: '清华智谱', model: 'zhipu' }, { label: 'GPT-4', model: "GPT4" }
]

export default function EntryPage() {
  const [tab, setTab] = useState("ai")
  // 表单录入 state
  const [form, setForm] = useState({
    companyName: "",
    companyType: "",
    location: "",
    recruitmentType: "",
    recruitmentTarget: "",
    position: "",
    applicationProgress: "",
    updateTime: "",
    deadline: "",
    relatedLinks: "",
    recruitmentNotice: "",
    referralCode: "",
    notes: "",
  })
  // AI录入 state
  const [aiType, setAiType] = useState(aiTypes[0].value)
  const [aiModel, setAiModel] = useState(aiModels[0].model)
  const [aiInput, setAiInput] = useState("")
  const [aiLoading, setAiLoading] = useState(false)
  const [aiMsg, setAiMsg] = useState<string | null>(null)

  const handleFormChange = (key: string, value: string) => {
    setForm((prev) => ({ ...prev, [key]: value }))
  }

  const handleFormSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    alert("表单录入成功！\n" + JSON.stringify(form, null, 2))
    setForm({
      companyName: "",
      companyType: "",
      location: "",
      recruitmentType: "",
      recruitmentTarget: "",
      position: "",
      applicationProgress: "",
      updateTime: "",
      deadline: "",
      relatedLinks: "",
      recruitmentNotice: "",
      referralCode: "",
      notes: "",
    })
  }

  const handleAISubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setAiLoading(true)
    setAiMsg(null)
    try {
      submitAIEntry({
        content: aiInput,
        model: aiModel,
        type: aiType,
      }).then(res => {
        console.log('ai录入返回结果', res);
        setAiMsg("提交成功！" + (res.data?.msg ? `\n${res.data.msg}` : ""))
        setAiInput("")
      })
    } catch (err: any) {
      setAiMsg("提交失败: " + (err?.response?.data?.msg || err?.message || "未知错误"))
    } finally {
      setAiLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <h1 className="text-2xl font-bold text-gray-900">职位录入</h1>
          </div>
        </div>
      </header>
      <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
        <Tabs value={tab} onValueChange={setTab} className="w-full">
          <TabsList className="mb-6">
            <TabsTrigger value="form">表单录入</TabsTrigger>
            <TabsTrigger value="ai">AI录入</TabsTrigger>
          </TabsList>
          <TabsContent value="form">
            <form className="bg-white rounded-lg shadow p-6 space-y-4" onSubmit={handleFormSubmit}>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="text-sm font-medium">公司名称</label>
                  <Input value={form.companyName} onChange={e => handleFormChange("companyName", e.target.value)} />
                </div>
                <div>
                  <label className="text-sm font-medium">公司类型</label>
                  <Select value={form.companyType} onValueChange={v => handleFormChange("companyType", v)}>
                    <SelectTrigger><SelectValue placeholder="请选择" /></SelectTrigger>
                    <SelectContent>
                      {companyTypes.map(type => <SelectItem key={type} value={type}>{type}</SelectItem>)}
                    </SelectContent>
                  </Select>
                </div>
                <div>
                  <label className="text-sm font-medium">工作地点</label>
                  <Input value={form.location} onChange={e => handleFormChange("location", e.target.value)} />
                </div>
                <div>
                  <label className="text-sm font-medium">招聘类型</label>
                  <Select value={form.recruitmentType} onValueChange={v => handleFormChange("recruitmentType", v)}>
                    <SelectTrigger><SelectValue placeholder="请选择" /></SelectTrigger>
                    <SelectContent>
                      {recruitmentTypes.map(type => <SelectItem key={type} value={type}>{type}</SelectItem>)}
                    </SelectContent>
                  </Select>
                </div>
                <div>
                  <label className="text-sm font-medium">招聘对象</label>
                  <Select value={form.recruitmentTarget} onValueChange={v => handleFormChange("recruitmentTarget", v)}>
                    <SelectTrigger><SelectValue placeholder="请选择" /></SelectTrigger>
                    <SelectContent>
                      {recruitmentTargets.map(type => <SelectItem key={type} value={type}>{type}</SelectItem>)}
                    </SelectContent>
                  </Select>
                </div>
                <div>
                  <label className="text-sm font-medium">投递截止</label>
                  <Input value={form.deadline} onChange={e => handleFormChange("deadline", e.target.value)} />
                </div>
                <div className="col-span-2">
                  <label className="text-sm font-medium">岗位描述</label>
                  <Textarea rows={3} value={form.position} onChange={e => handleFormChange("position", e.target.value)} />
                </div>
                <div className="col-span-2">
                  <label className="text-sm font-medium">投递进度</label>
                  <Input value={form.applicationProgress} onChange={e => handleFormChange("applicationProgress", e.target.value)} />
                </div>
                <div className="col-span-2">
                  <label className="text-sm font-medium">相关链接</label>
                  <Input value={form.relatedLinks} onChange={e => handleFormChange("relatedLinks", e.target.value)} />
                </div>
                <div className="col-span-2">
                  <label className="text-sm font-medium">招聘公告</label>
                  <Input value={form.recruitmentNotice} onChange={e => handleFormChange("recruitmentNotice", e.target.value)} />
                </div>
                <div>
                  <label className="text-sm font-medium">内推码</label>
                  <Input value={form.referralCode} onChange={e => handleFormChange("referralCode", e.target.value)} />
                </div>
                <div>
                  <label className="text-sm font-medium">备注</label>
                  <Input value={form.notes} onChange={e => handleFormChange("notes", e.target.value)} />
                </div>
              </div>
              <div className="flex justify-end">
                <Button type="submit">提交</Button>
              </div>
            </form>
          </TabsContent>
          <TabsContent value="ai">
            <form className="bg-white rounded-lg shadow p-6 space-y-6" onSubmit={handleAISubmit}>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="text-sm font-medium">AI识别类型</label>
                  <Select value={aiType} onValueChange={setAiType}>
                    <SelectTrigger><SelectValue placeholder="请选择" /></SelectTrigger>
                    <SelectContent>
                      {aiTypes.map(type => (
                        <SelectItem key={type.value} value={type.value}>{type.label}</SelectItem>
                      ))}
                    </SelectContent>
                  </Select>

                </div>
                <div>
                  <label className="text-sm font-medium">大模型选择</label>
                  <Select value={aiModel} onValueChange={setAiModel}>
                    <SelectTrigger><SelectValue placeholder="请选择" /></SelectTrigger>
                    <SelectContent>
                      {aiModels.map(model => (
                        <SelectItem key={model.model} value={model.model}>{model.label}</SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>
              </div>
              <div>
                <label className="text-sm font-medium">AI输入内容</label>
                <Textarea rows={8} value={aiInput} onChange={e => setAiInput(e.target.value)} placeholder="请粘贴职位JD、简历或其他AI任务内容..." />
              </div>
              <div className="flex justify-end">
                <Button type="submit" disabled={aiLoading}>
                  {aiLoading ? "提交中..." : "提交"}
                </Button>
                {aiMsg && (
                  <p className={`ml-4 text-sm ${aiMsg.includes("成功") ? "text-green-600" : "text-red-600"}`}>
                    {aiMsg}
                  </p>
                )}
              </div>
            </form>
          </TabsContent>
        </Tabs>
      </div>
    </div>
  )
} 