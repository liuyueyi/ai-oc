"use client"
import { useEffect, useRef } from "react"

export function useSSE(
    url: string,
    onMessage: (type: string, payload: string) => void,
    onError?: (err: any) => void
) {
    const eventSourceRef = useRef<EventSource | null>(null)

    useEffect(() => {
        if (!url || typeof window === "undefined") return
        const es = new window.EventSource(url)
        eventSourceRef.current = es

        es.onmessage = (event) => {
            // 解析后端格式: type#payload
            const [type, ...rest] = event.data.split("#")
            onMessage(type, rest.join("#"))
        }
        es.onerror = (err) => {
            if (onError) onError(err)
            es.close()
        }
        return () => {
            es.close()
        }
    }, [url, onMessage, onError])
}