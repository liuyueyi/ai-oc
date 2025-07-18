"use client"
import { useState, useEffect } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Tabs, TabsList, TabsTrigger, TabsContent } from "@/components/ui/tabs"
import { Textarea } from "@/components/ui/textarea"
import { submitAIEntry } from "@/lib/api"
import { useRef } from "react"
import { fetchTaskList, reRunTask } from "@/lib/api";
import { Badge } from "@/components/ui/badge";


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

// 任务类型和状态映射
const TASK_TYPE_MAP: Record<string, string> = {
  1: "html文本",
  2: "纯文本",
  3: "http链接",
  4: "excel文件",
  5: "csv文件",
  6: "图片"
};
const TASK_STATE_MAP: Record<string, string> = {
  0: "未处理",
  1: "处理中",
  2: "成功",
  3: "失败"
};

function formatDateTimeStr(str?: string) {
  if (!str) return "-";
  const d = new Date(str);
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}:${String(d.getSeconds()).padStart(2, '0')}`;
}

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
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);
  const [previewImg, setPreviewImg] = useState<string | null>(null);

  // 任务列表 state
  const [taskQuery, setTaskQuery] = useState({
    page: 1,
    size: 10,
    taskId: '',
    model: '',
    type: '',
    state: ''
  });
  const [taskList, setTaskList] = useState<any[]>([]);
  const [taskTotal, setTaskTotal] = useState(0);
  const [taskLoading, setTaskLoading] = useState(false);
  const [reRunLoadingId, setReRunLoadingId] = useState<number | null>(null);


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
    e.preventDefault();
    setAiLoading(true);
    setAiMsg(null);
    try {
      if (isFileType) {
        if (!selectedFile) {
          setAiMsg("请先选择文件");
          setAiLoading(false);
          return;
        }

        submitAIEntry({
          content: aiInput,
          model: aiModel,
          type: aiType,
          file: selectedFile
        }).then(res => {
          setAiMsg("提交成功！" + (res.data?.msg ? `\n${res.data.msg}` : ""));
          setSelectedFile(null);
        });
      } else {
        // 原有逻辑
        submitAIEntry({
          content: aiInput,
          model: aiModel,
          type: aiType,
          file: null,
        }).then(res => {
          setAiMsg("提交成功！" + (res.data?.msg ? `\n${res.data.msg}` : ""));
          setAiInput("");
        });
      }
    } catch (err: any) {
      setAiMsg("提交失败: " + (err?.response?.data?.msg || err?.message || "未知错误"));
    } finally {
      setAiLoading(false);
    }
  };

  // 判断类型
  const isFileType = ["IMAGE", "CSV_FILE", "EXCEL_FILE"].includes(aiType);

  useEffect(() => {
    function handlePaste(e: ClipboardEvent) {
      if (!isFileType) return;
      const items = e.clipboardData?.items;
      let found = false;
      if (items) {
        for (let i = 0; i < items.length; i++) {
          if (items[i].kind === "file") {
            setSelectedFile(items[i].getAsFile());
            found = true;
            break;
          }
        }
      }
      if (!found && e.clipboardData?.files && e.clipboardData.files.length > 0) {
        setSelectedFile(e.clipboardData.files[0]);
      }
    }
    window.addEventListener("paste", handlePaste);
    return () => window.removeEventListener("paste", handlePaste);
  }, [isFileType]);

  useEffect(() => {
    if (tab !== 'task') return;
    setTaskLoading(true);
    fetchTaskList({
      page: taskQuery.page,
      size: taskQuery.size,
      taskId: taskQuery.taskId ? Number(taskQuery.taskId) : undefined,
      model: taskQuery.model || undefined,
      type: taskQuery.type && taskQuery.type !== '-1' ? Number(taskQuery.type) : undefined,
      state: taskQuery.state && taskQuery.state !== '-1' ? Number(taskQuery.state) : undefined
    })
      .then(res => {
        setTaskList(res.list || []);
        setTaskTotal(res.total || 0);
      })
      .finally(() => setTaskLoading(false));
  }, [tab, taskQuery]);

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <h1 className="text-2xl font-bold text-gray-900">职位录入</h1>
          </div>
        </div>
      </header>
      <div className="full-w mx-auto px-4 sm:px-6 lg:px-8 py-6">
        <Tabs value={tab} onValueChange={setTab} className="w-full">
          <TabsList className="mb-6">
            <TabsTrigger value="form">表单录入</TabsTrigger>
            <TabsTrigger value="ai">AI录入</TabsTrigger>
            <TabsTrigger value="task">任务列表</TabsTrigger>
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
                {isFileType ? (
                  <div
                    className="border border-dashed border-gray-300 rounded-md flex flex-col items-center justify-center cursor-pointer bg-gray-50 hover:bg-gray-100 transition"
                    style={{ minHeight: 260, height: 320 }}
                    onClick={() => fileInputRef.current?.click()}
                    onDrop={e => {
                      e.preventDefault();
                      if (e.dataTransfer.files && e.dataTransfer.files[0]) {
                        setSelectedFile(e.dataTransfer.files[0]);
                      }
                    }}
                    onDragOver={e => e.preventDefault()}
                    onPaste={e => {
                      const items = e.clipboardData.items;
                      for (let i = 0; i < items.length; i++) {
                        if (items[i].kind === "file") {
                          setSelectedFile(items[i].getAsFile());
                          break;
                        }
                      }
                    }}
                  >
                    <input
                      type="file"
                      ref={fileInputRef}
                      className="hidden"
                      accept={aiType === "IMAGE" ? "image/*" : aiType === "CSV_FILE" ? ".csv" : aiType === "EXCEL_FILE" ? ".xls,.xlsx" : ""}
                      onChange={e => {
                        if (e.target.files && e.target.files[0]) {
                          setSelectedFile(e.target.files[0]);
                        }
                      }}
                    />
                    {selectedFile ? (
                      <div className="flex flex-col items-center gap-2 px-5">
                        {aiType === "IMAGE" && selectedFile.type.startsWith("image/") && (
                          <img
                            src={URL.createObjectURL(selectedFile)}
                            alt="预览"
                            className="full-w gap-1 max-h-60 rounded border cursor-zoom-in"
                            style={{ objectFit: "contain" }}
                            onClick={e => {
                              e.stopPropagation();
                              setPreviewImg(URL.createObjectURL(selectedFile));
                            }}
                          />
                        )}
                        <div className="flex items-center gap-2">
                          <div className="text-blue-600 font-medium">已选择文件：{selectedFile.name}</div>
                          <button
                            type="button"
                            className="ml-2 px-2 py-0.5 rounded bg-gray-200 hover:bg-gray-300 text-gray-600 text-xs"
                            onClick={e => {
                              e.stopPropagation();
                              setSelectedFile(null);
                            }}
                            title="清除附件"
                          >
                            ×
                          </button>
                        </div>
                      </div>
                    ) : (
                      aiType === "IMAGE" ? (
                        <div className="flex flex-col items-center justify-center w-full h-full">
                          <svg className="w-16 h-16 text-sky-400 mb-2" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M12 16V4m0 0l-4 4m4-4l4 4M20 16.58A5 5 0 0017 7h-1.26A8 8 0 104 16.25" /></svg>
                          <div className="text-2xl font-bold text-gray-700 mb-2">上传图片提取数据报表</div>
                          <div className="flex items-center gap-2 mb-4 text-gray-500">
                            <span>支持三种方式：</span>
                            <span className="bg-sky-100 text-sky-600 px-2 py-1 rounded flex items-center text-sm"><svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M12 4v16m8-8H4" /></svg>点击上传</span>
                            <span className="bg-sky-100 text-sky-600 px-2 py-1 rounded flex items-center text-sm"><svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M4 7v13a1 1 0 001 1h14a1 1 0 001-1V7M4 7l8-5 8 5" /></svg>拖拽图片</span>
                            <span className="bg-sky-100 text-sky-600 px-2 py-1 rounded flex items-center text-sm"><svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M16 17l-4 4m0 0l-4-4m4 4V3" /></svg>Ctrl+V 粘贴</span>
                          </div>
                          <button
                            type="button"
                            className="bg-sky-400 hover:bg-sky-500 text-white font-semibold px-8 py-2 rounded-full flex items-center text-lg shadow mb-2"
                            onClick={e => {
                              e.stopPropagation();
                              fileInputRef.current?.click();
                            }}
                          >
                            <svg className="w-6 h-6 mr-2" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V7M3 7l9-5 9 5" /></svg>
                            选择图片
                          </button>
                        </div>
                      ) : (
                        <div className="flex flex-col items-center justify-center w-full h-full">
                          <svg className="w-16 h-16 text-sky-400 mb-2" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M12 16V4m0 0l-4 4m4-4l4 4M20 16.58A5 5 0 0017 7h-1.26A8 8 0 104 16.25" /></svg>
                          <div className="text-2xl font-bold text-gray-700 mb-2">上传文件提取数据报表</div>
                          <div className="flex items-center gap-2 mb-4 text-gray-500">
                            <span>支持两种方式：</span>
                            <span className="bg-sky-100 text-sky-600 px-2 py-1 rounded flex items-center text-sm"><svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M12 4v16m8-8H4" /></svg>点击上传</span>
                            <span className="bg-sky-100 text-sky-600 px-2 py-1 rounded flex items-center text-sm"><svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M4 7v13a1 1 0 001 1h14a1 1 0 001-1V7M4 7l8-5 8 5" /></svg>拖拽文件</span>
                          </div>
                        </div>
                      )
                    )}
                  </div>
                ) : (
                  <Textarea rows={8} value={aiInput} onChange={e => setAiInput(e.target.value)} placeholder="请粘贴职位JD、简历或其他AI任务内容..." />
                )}
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
          <TabsContent value="task">
            <div className="bg-white rounded-lg shadow p-6">
              {/* 筛选条件 */}
              <div className="flex flex-wrap gap-2 mb-4 items-center">
                <Input placeholder="任务ID" className="w-32" value={taskQuery.taskId} onChange={e => setTaskQuery(q => ({ ...q, taskId: e.target.value, page: 1 }))} />
                <Input placeholder="模型" className="w-32" value={taskQuery.model} onChange={e => setTaskQuery(q => ({ ...q, model: e.target.value, page: 1 }))} />
                <Select value={taskQuery.type} onValueChange={v => setTaskQuery(q => ({ ...q, type: v, page: 1 }))}>
                  <SelectTrigger className="w-32"><SelectValue placeholder="抓取类型" /></SelectTrigger>
                  <SelectContent>
                    <SelectItem value="-1">全部类型</SelectItem>
                    <SelectItem value="1">html文本</SelectItem>
                    <SelectItem value="2">纯文本</SelectItem>
                    <SelectItem value="3">http链接</SelectItem>
                    <SelectItem value="4">excel文件</SelectItem>
                    <SelectItem value="5">csv文件</SelectItem>
                    <SelectItem value="6">图片</SelectItem>
                  </SelectContent>
                </Select>
                <Select value={taskQuery.state} onValueChange={v => setTaskQuery(q => ({ ...q, state: v, page: 1 }))}>
                  <SelectTrigger className="w-32"><SelectValue placeholder="任务状态" /></SelectTrigger>
                  <SelectContent>
                    <SelectItem value="-1">全部状态</SelectItem>
                    <SelectItem value="0">未处理</SelectItem>
                    <SelectItem value="1">处理中</SelectItem>
                    <SelectItem value="2">已处理</SelectItem>
                    <SelectItem value="3">处理失败</SelectItem>
                  </SelectContent>
                </Select>
                <Button className="h-10 px-6" onClick={() => setTaskQuery(q => ({ ...q, page: 1 }))}>查询</Button>
              </div>
              {/* 任务表格 */}
              <div className="overflow-x-auto">
                <table className="min-w-full text-sm border">
                  <thead className="bg-gray-100">
                    <tr>
                      <th className="px-2 py-1 border whitespace-nowrap text-center">ID</th>
                      <th className="px-2 py-1 border whitespace-nowrap text-center">类型</th>
                      <th className="px-2 py-1 border whitespace-nowrap text-center">模型</th>
                      <th className="px-2 py-1 border whitespace-nowrap text-center">状态</th>
                      <th className="px-2 py-1 border whitespace-nowrap text-center">输入</th>
                      <th className="px-2 py-1 border whitespace-nowrap text-center">结果</th>
                      <th className="px-2 py-1 border whitespace-nowrap text-center">处理时间</th>
                      <th className="px-2 py-1 border whitespace-nowrap text-center">创建时间</th>
                      <th className="px-2 py-1 border whitespace-nowrap text-center">更新时间</th>
                      <th className="px-2 py-1 border whitespace-nowrap text-center">操作</th>
                    </tr>
                  </thead>
                  <tbody>
                    {taskLoading ? (
                      <tr><td colSpan={10} className="text-center text-gray-400 py-4">加载中...</td></tr>
                    ) : taskList.length === 0 ? (
                      <tr><td colSpan={10} className="text-center text-gray-400 py-4">暂无数据</td></tr>
                    ) : taskList.map(task => (
                      <tr key={task.id} className="hover:bg-gray-50">
                        <td className="border px-2 py-1 whitespace-nowrap text-center">{task.taskId}</td>
                        <td className="border px-2 py-1 whitespace-nowrap text-center">
                          <Badge
                            variant="outline"
                            className={
                              task.type === 1 ? "border-blue-500 text-blue-600" :
                                task.type === 2 ? "border-gray-400 text-gray-600" :
                                  task.type === 3 ? "border-green-500 text-green-600" :
                                    task.type === 4 ? "border-orange-500 text-orange-600" :
                                      task.type === 5 ? "border-purple-500 text-purple-600" :
                                        task.type === 6 ? "border-pink-500 text-pink-600" :
                                          "border-gray-300 text-gray-500"
                            }
                          >
                            {TASK_TYPE_MAP[task.type as keyof typeof TASK_TYPE_MAP] || task.type}
                          </Badge>
                        </td>
                        <td className="border px-2 py-1 whitespace-nowrap text-center">{task.model}</td>
                        <td className="border px-2 py-1 whitespace-nowrap text-center">
                          <Badge
                            variant="outline"
                            className={
                              task.state === 0 ? "border-gray-400 text-gray-600" :
                                task.state === 1 ? "border-blue-500 text-blue-600" :
                                  task.state === 2 ? "border-green-500 text-green-600" :
                                    task.state === 3 ? "border-red-500 text-red-600" :
                                      "border-gray-300 text-gray-500"
                            }
                          >
                            {TASK_STATE_MAP[task.state as keyof typeof TASK_STATE_MAP] || task.state}
                          </Badge>
                        </td>
                        <td className="border px-2 py-1 whitespace-nowrap max-w-[200px] truncate text-center" title={task.content}>
                          {task.type === 6 && task.content ? (
                            <a href={task.content} target="_blank" rel="noopener noreferrer">
                              <img src={task.content} alt="图片" className="max-w-[60px] max-h-[60px] rounded border hover:shadow mx-auto" style={{ objectFit: 'cover' }} />
                            </a>
                          ) : (task.type === 4 || task.type === 5) && task.content ? (
                            <a href={task.content} target="_blank" rel="noopener noreferrer" className="text-blue-600 underline break-all">
                              {task.content.length > 32 ? task.content.slice(0, 32) + '...' : task.content}
                            </a>
                          ) : (
                            <span className="break-all">{task.content}</span>
                          )}
                        </td>
                        <td className="border px-2 py-1 whitespace-nowrap max-w-[200px] truncate text-center" title={task.result}>
                          {(() => {
                            let parsed;
                            try {
                              parsed = JSON.parse(task.result);
                            } catch {
                              return task.result;
                            }
                            const inserts = Array.isArray(parsed?.insertDraftIds) && parsed.insertDraftIds.length > 0
                              ? `插入：${parsed.insertDraftIds.join(',')}` : '';
                            const updates = Array.isArray(parsed?.updateDraftIds) && parsed.updateDraftIds.length > 0
                              ? `更新：${parsed.updateDraftIds.join(',')}` : '';
                            if (inserts && updates) return `${inserts}；${updates}`;
                            if (inserts) return inserts;
                            if (updates) return updates;
                            return parsed?.msg || task.result;
                          })()}
                        </td>
                        <td className="border px-2 py-1 whitespace-nowrap text-center">{formatDateTimeStr(task.processTime)}</td>
                        <td className="border px-2 py-1 whitespace-nowrap text-center">{formatDateTimeStr(task.createTime)}</td>
                        <td className="border px-2 py-1 whitespace-nowrap text-center">{formatDateTimeStr(task.updateTime)}</td>
                        <td className="border px-2 py-1 whitespace-nowrap text-center">
                          <Button
                            size="sm"
                            variant="outline"
                            disabled={reRunLoadingId === task.id}
                            onClick={async () => {
                              setReRunLoadingId(task.id);
                              try {
                                await reRunTask(task.id);
                                setTaskQuery(q => ({ ...q }));
                              } catch (err) {
                                alert("重跑失败: " + (err instanceof Error ? err.message : "未知错误"));
                              } finally {
                                setReRunLoadingId(null);
                              }
                            }}
                          >
                            {reRunLoadingId === task.id ? "重跑中..." : "重跑"}
                          </Button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
              {/* 分页 */}
              <div className="flex justify-end items-center gap-2 mt-4">
                <span className="text-sm text-gray-500 mr-2">第 {taskQuery.page} / {Math.ceil(taskTotal / (taskQuery.size || 10)) || 1} 页</span>
                <Button size="sm" variant="outline" disabled={taskQuery.page === 1} onClick={() => setTaskQuery(q => ({ ...q, page: (q.page || 1) - 1 }))}>上一页</Button>
                <Button size="sm" variant="outline" disabled={taskQuery.page >= Math.ceil(taskTotal / (taskQuery.size || 10))} onClick={() => setTaskQuery(q => ({ ...q, page: (q.page || 1) + 1 }))}>下一页</Button>
              </div>
            </div>
          </TabsContent>
        </Tabs>
      </div>
      {/* 全屏图片预览 */}
      {previewImg && (
        <div
          className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-80"
          onClick={() => setPreviewImg(null)}
          style={{ cursor: "zoom-out" }}
        >
          <img
            src={previewImg}
            alt="全屏预览"
            className="max-w-full max-h-full rounded shadow-lg"
            onClick={e => e.stopPropagation()}
          />
        </div>
      )}
    </div>
  )
} 