'use client';

import React, { createContext, useContext, useEffect, useState, ReactNode } from 'react';
import { useRouter } from 'next/navigation';
import { authApi, UserProfile, getAccessToken, removeAccessToken } from './api';

/**
 * Authentication Context
 * FRD-001: User session management on frontend
 */

interface AuthContextType {
  user: UserProfile | null;
  isLoading: boolean;
  isAuthenticated: boolean;
  login: (email: string, password: string, rememberMe?: boolean, captchaToken?: string) => Promise<void>;
  logout: () => Promise<void>;
  refreshUser: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

interface AuthProviderProps {
  children: ReactNode;
}

export function AuthProvider({ children }: AuthProviderProps) {
  const [user, setUser] = useState<UserProfile | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const router = useRouter();

  // Check authentication status on mount
  useEffect(() => {
    checkAuth();
  }, []);

  const checkAuth = async () => {
    const token = getAccessToken();
    if (!token) {
      setIsLoading(false);
      return;
    }

    try {
      const profile = await authApi.getProfile();
      setUser(profile);
    } catch (error) {
      // Token invalid, try refresh
      try {
        await authApi.refreshToken();
        const profile = await authApi.getProfile();
        setUser(profile);
      } catch {
        // Refresh failed, clear token
        removeAccessToken();
      }
    } finally {
      setIsLoading(false);
    }
  };

  const login = async (
    email: string,
    password: string,
    rememberMe?: boolean,
    captchaToken?: string
  ) => {
    const response = await authApi.login({
      email,
      password,
      rememberMe,
      captchaToken,
    });

    // Fetch user profile
    const profile = await authApi.getProfile();
    setUser(profile);

    // Redirect based on role
    const role = profile.userType;
    if (role === 'ADMIN') {
      router.push('/admin/dashboard');
    } else if (role === 'PARTNER') {
      router.push('/partner/dashboard');
    } else {
      router.push('/dashboard');
    }
  };

  const logout = async () => {
    await authApi.logout();
    setUser(null);
    router.push('/auth/login');
  };

  const refreshUser = async () => {
    try {
      const profile = await authApi.getProfile();
      setUser(profile);
    } catch (error) {
      setUser(null);
    }
  };

  const value: AuthContextType = {
    user,
    isLoading,
    isAuthenticated: !!user,
    login,
    logout,
    refreshUser,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
