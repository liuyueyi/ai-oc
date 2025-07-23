import type React from "react"
import type { Metadata } from "next"
import { Inter } from "next/font/google"
import "./globals.css"
import { LoginUserProvider } from "@/hooks/useLoginUser"
import { Toaster } from "@/components/ui/toaster"

const inter = Inter({ subsets: ["latin"] })

export const metadata: Metadata = {
  title: "校招派 - 职位招聘平台",
  description: "专业的职位招聘和求职平台",
  generator: '一灰灰'
}

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="zh-CN">
      <body className={inter.className}>
        <LoginUserProvider>
          {children}
        </LoginUserProvider>
        <Toaster />
      </body>
    </html>
  )
}
