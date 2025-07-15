"use client"
import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"

interface User {
  id: string | number
  username: string
  email: string
  role: string
}

export default function UsersPage() {
  const [users, setUsers] = useState<User[]>([])
  const [editingUser, setEditingUser] = useState<User | null>(null)
  const [isAddingNew, setIsAddingNew] = useState(false)

  const handleDelete = (id: string) => {
    setUsers(users.filter((user) => user.id !== id))
  }

  const handleSave = (user: User) => {
    if (isAddingNew) {
      setUsers([...users, { ...user, id: Date.now().toString() }])
      setIsAddingNew(false)
    } else {
      setUsers(users.map((u) => (u.id === user.id ? user : u)))
    }
    setEditingUser(null)
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <h1 className="text-2xl font-bold text-gray-900">用户管理</h1>
          </div>
        </div>
      </header>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
        <div className="mb-6">
          <Button
            onClick={() => {
              setIsAddingNew(true)
              setEditingUser({ id: "", username: "", email: "", role: "" })
            }}
          >
            添加新用户
          </Button>
        </div>

        <div className="bg-white rounded-lg shadow overflow-hidden">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>用户名</TableHead>
                <TableHead>邮箱</TableHead>
                <TableHead>角色</TableHead>
                <TableHead>操作</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {users.map((user) => (
                <TableRow key={String(user.id)}>
                  <TableCell className="font-medium">{user.username}</TableCell>
                  <TableCell>{user.email}</TableCell>
                  <TableCell>{user.role}</TableCell>
                  <TableCell>
                    <div className="flex space-x-2">
                      <Button size="sm" variant="outline" onClick={() => setEditingUser(user)}>
                        编辑
                      </Button>
                      <Button size="sm" variant="destructive" onClick={() => handleDelete(String(user.id))}>
                        删除
                      </Button>
                    </div>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </div>

        {editingUser && (
          <Dialog
            open={true}
            onOpenChange={() => {
              setEditingUser(null)
              setIsAddingNew(false)
            }}
          >
            <DialogContent className="max-w-md max-h-[80vh] overflow-y-auto">
              <DialogHeader>
                <DialogTitle>{isAddingNew ? "添加新用户" : "编辑用户"}</DialogTitle>
              </DialogHeader>
              <div className="grid grid-cols-1 gap-4 py-4">
                <div>
                  <label className="text-sm font-medium">用户名</label>
                  <Input
                    value={editingUser.username}
                    onChange={(e) => setEditingUser({ ...editingUser, username: e.target.value })}
                  />
                </div>
                <div>
                  <label className="text-sm font-medium">邮箱</label>
                  <Input
                    value={editingUser.email}
                    onChange={(e) => setEditingUser({ ...editingUser, email: e.target.value })}
                  />
                </div>
                <div>
                  <label className="text-sm font-medium">角色</label>
                  <Input
                    value={editingUser.role}
                    onChange={(e) => setEditingUser({ ...editingUser, role: e.target.value })}
                  />
                </div>
              </div>
              <div className="flex justify-end space-x-2">
                <Button
                  variant="outline"
                  onClick={() => {
                    setEditingUser(null)
                    setIsAddingNew(false)
                  }}
                >
                  取消
                </Button>
                <Button onClick={() => handleSave(editingUser)}>保存</Button>
              </div>
            </DialogContent>
          </Dialog>
        )}
      </div>
    </div>
  )
} 