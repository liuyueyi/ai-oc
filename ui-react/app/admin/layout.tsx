"use client"
import React from "react"
import { usePathname } from "next/navigation"
import { SidebarProvider, Sidebar, SidebarMenu, SidebarMenuItem, SidebarMenuButton, SidebarContent } from "@/components/ui/sidebar"
import Link from "next/link"
import { Toaster } from "@/components/ui/toaster"

const menu = [
    { label: "录入数据", path: "/admin/entry" },
    { label: "草稿列表", path: "/admin/drafts" },
    { label: "职位列表", path: "/admin/jobs" },
    { label: "用户管理", path: "/admin/users" },
]

export default function AdminLayout({ children }: { children: React.ReactNode }) {
    const pathname = usePathname()
    return (
        <SidebarProvider>
            <div className="flex min-h-screen w-full">
                <Sidebar className="w-64 min-h-screen bg-sidebar text-sidebar-foreground border-r flex flex-col">
                    <div className="h-16 flex items-center justify-center font-bold text-xl tracking-wide border-b border-sidebar-border mb-2 select-none">
                        管理后台
                    </div>
                    <SidebarContent>
                        <SidebarMenu>
                            {menu.map((item) => (
                                <SidebarMenuItem key={item.path}>
                                    <SidebarMenuButton
                                        asChild
                                        isActive={pathname === item.path}
                                        className={`h-12 px-6 text-base rounded-none justify-start transition-all ${pathname === item.path
                                                ? "font-bold bg-sidebar-accent text-sidebar-accent-foreground border-l-4 border-blue-600"
                                                : "font-normal hover:bg-sidebar-accent hover:text-sidebar-accent-foreground"
                                            }`}
                                    >
                                        <Link href={item.path}>{item.label}</Link>
                                    </SidebarMenuButton>
                                </SidebarMenuItem>
                            ))}
                        </SidebarMenu>
                    </SidebarContent>
                </Sidebar>
                <main className="flex-grow bg-gray-50">{children}</main>
                <Toaster />
            </div>
        </SidebarProvider>
    )
}