"use client"

import React, { createContext, useContext, useEffect, useState } from "react";

function parseJwt(token: string) {
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
}
function isJwtValid(token: string): boolean {
    try {
        const payload = parseJwt(token);
        if (!payload) return false;
        if (payload.exp && typeof payload.exp === 'number') {
            return payload.exp > Math.floor(Date.now() / 1000);
        }
        return true;
    } catch {
        return false;
    }
}
function getCookie(name: string): string | null {
    if (typeof document === 'undefined') return null;
    const match = document.cookie.match(new RegExp('(^| )' + name + '=([^;]+)'));
    return match ? decodeURIComponent(match[2]) : null;
}

const LoginUserContext = createContext<{
    userInfo: { userId: number; role: number; nickname?: string; avatar?: string } | null,
    setUserInfo: (u: any) => void,
    logout: () => void
}>({ userInfo: null, setUserInfo: () => { }, logout: () => { } });

export function useLoginUser() {
    return useContext(LoginUserContext);
}

export function LoginUserProvider({ children }: { children: React.ReactNode }) {
    const [userInfo, setUserInfo] = useState<{ userId: number; role: number; nickname?: string; avatar?: string } | null>(null);

    useEffect(() => {
        if (typeof window !== 'undefined') {
            const localUser = localStorage.getItem('oc-user');
            const token = localStorage.getItem('oc-token');
            if (localUser && token && isJwtValid(token)) {
                try {
                    setUserInfo(JSON.parse(localUser));
                    return;
                } catch { }
            } else {
                localStorage.removeItem('oc-user');
                localStorage.removeItem('oc-token');
            }
        }
        const cookieToken = getCookie('oc-session');
        if (cookieToken && isJwtValid(cookieToken)) {
            const jwt = parseJwt(cookieToken);
            if (jwt) {
                const info = {
                    userId: jwt.uid,
                    role: jwt.r,
                    nickname: jwt.un,
                    avatar: jwt.av,
                };
                setUserInfo(info);
                if (typeof window !== 'undefined') {
                    localStorage.setItem('oc-user', JSON.stringify(info));
                    localStorage.setItem('oc-token', cookieToken);
                }
            }
        }
    }, []);

    const logout = () => {
        document.cookie = 'oc-session=;path=/;expires=Thu, 01 Jan 1970 00:00:00 GMT';
        setUserInfo(null);
        if (typeof window !== 'undefined') {
            localStorage.removeItem('oc-user');
            localStorage.removeItem('oc-token');
        }
    };

    return (
        <LoginUserContext.Provider value={{ userInfo, setUserInfo, logout }}>
            {children}
        </LoginUserContext.Provider>
    );
} 