import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';
import Cookies from 'js-cookie';

/**
 * API Client for BrandKit Backend
 * FRD-001: Authentication API Integration
 */

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || '/api';

// Create axios instance
export const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true, // Enable cookies for refresh token
});

// Token storage
const ACCESS_TOKEN_KEY = 'accessToken';

export const getAccessToken = (): string | null => {
  if (typeof window === 'undefined') return null;
  return localStorage.getItem(ACCESS_TOKEN_KEY);
};

export const setAccessToken = (token: string): void => {
  if (typeof window !== 'undefined') {
    localStorage.setItem(ACCESS_TOKEN_KEY, token);
  }
};

export const removeAccessToken = (): void => {
  if (typeof window !== 'undefined') {
    localStorage.removeItem(ACCESS_TOKEN_KEY);
  }
};

// Request interceptor - add auth header
api.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = getAccessToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor - handle token refresh
let isRefreshing = false;
let failedQueue: Array<{
  resolve: (token: string) => void;
  reject: (error: Error) => void;
}> = [];

const processQueue = (error: Error | null, token: string | null = null) => {
  failedQueue.forEach((prom) => {
    if (error) {
      prom.reject(error);
    } else if (token) {
      prom.resolve(token);
    }
  });
  failedQueue = [];
};

api.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };

    // Check for token expiry header
    const tokenExpired = error.response?.headers['x-token-expired'] === 'true';

    if (error.response?.status === 401 && !originalRequest._retry && tokenExpired) {
      if (isRefreshing) {
        // Queue the request while refreshing
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        })
          .then((token) => {
            originalRequest.headers.Authorization = `Bearer ${token}`;
            return api(originalRequest);
          })
          .catch((err) => Promise.reject(err));
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        const response = await api.post('/auth/refresh');
        const { accessToken } = response.data.data;
        
        setAccessToken(accessToken);
        processQueue(null, accessToken);
        
        originalRequest.headers.Authorization = `Bearer ${accessToken}`;
        return api(originalRequest);
      } catch (refreshError) {
        processQueue(refreshError as Error, null);
        removeAccessToken();
        
        // Redirect to login
        if (typeof window !== 'undefined') {
          window.location.href = '/auth/login?error=session_expired';
        }
        
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  }
);

// API Types
export interface RegisterData {
  fullName: string;
  email: string;
  password: string;
  confirmPassword: string;
  companyName: string;
  phone?: string;
  userType: 'CLIENT' | 'PARTNER';
  termsAccepted: boolean;
}

export interface LoginData {
  email: string;
  password: string;
  rememberMe?: boolean;
  captchaToken?: string;
}

export interface AuthResponse {
  status: string;
  message: string;
  data: {
    userId: string;
    email: string;
    fullName: string;
    role: 'CLIENT' | 'PARTNER' | 'ADMIN';
    accessToken: string;
    expiresIn: number;
    verificationRequired?: boolean;
  };
}

export interface UserProfile {
  id: string;
  email: string;
  fullName: string;
  companyName: string;
  phone: string;
  userType: 'CLIENT' | 'PARTNER' | 'ADMIN';
  status: string;
  emailVerified: boolean;
  profilePictureUrl?: string;
  authProvider: 'EMAIL' | 'GOOGLE' | 'LINKEDIN';
  lastLoginAt?: string;
  createdAt: string;
}

export interface ErrorResponse {
  status: string;
  message: string;
  errorCode: string;
  fieldErrors?: Record<string, string>;
}

// Auth API Functions
export const authApi = {
  /**
   * Register new user
   * FRD-001 FR-1: Email-Based Registration
   */
  register: async (data: RegisterData): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/auth/register', data);
    return response.data;
  },

  /**
   * Login with email and password
   * FRD-001 FR-6: Login Functionality
   */
  login: async (data: LoginData): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/auth/login', data);
    if (response.data.data.accessToken) {
      setAccessToken(response.data.data.accessToken);
    }
    return response.data;
  },

  /**
   * Request password reset
   * FRD-001 FR-7: Forgot Password
   */
  forgotPassword: async (email: string): Promise<{ status: string; message: string }> => {
    const response = await api.post('/auth/forgot-password', { email });
    return response.data;
  },

  /**
   * Reset password with token
   * FRD-001 FR-7: Password Reset
   */
  resetPassword: async (data: {
    token: string;
    password: string;
    confirmPassword: string;
  }): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/auth/reset-password', data);
    if (response.data.data.accessToken) {
      setAccessToken(response.data.data.accessToken);
    }
    return response.data;
  },

  /**
   * Refresh access token
   * FRD-001 FR-9: Token Refresh
   */
  refreshToken: async (): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/auth/refresh');
    if (response.data.data.accessToken) {
      setAccessToken(response.data.data.accessToken);
    }
    return response.data;
  },

  /**
   * Logout
   * FRD-001 FR-9: Logout
   */
  logout: async (): Promise<void> => {
    try {
      await api.post('/auth/logout');
    } finally {
      removeAccessToken();
    }
  },

  /**
   * Get current user profile
   * FRD-001 FR-10: User Profile
   */
  getProfile: async (): Promise<UserProfile> => {
    const response = await api.get<UserProfile>('/auth/profile');
    return response.data;
  },

  /**
   * Update profile
   * FRD-001 FR-10: Update Profile
   */
  updateProfile: async (data: {
    fullName?: string;
    companyName?: string;
    phone?: string;
  }): Promise<UserProfile> => {
    const response = await api.put<UserProfile>('/auth/profile', data);
    return response.data;
  },

  /**
   * Change password
   * FRD-001 FR-10: Change Password
   */
  changePassword: async (data: {
    currentPassword: string;
    newPassword: string;
    confirmPassword: string;
  }): Promise<{ status: string; message: string }> => {
    const response = await api.post('/auth/change-password', data);
    return response.data;
  },

  /**
   * Complete OAuth registration with additional info
   * FRD-001 BR-10: Mandatory Fields for Social Auth
   */
  completeOAuthRegistration: async (
    token: string,
    data: {
      companyName: string;
      phone?: string;
      userType: 'CLIENT' | 'PARTNER';
      termsAccepted: boolean;
    }
  ): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>(
      '/auth/complete-registration',
      data,
      { headers: { Authorization: `Bearer ${token}` } }
    );
    if (response.data.data.accessToken) {
      setAccessToken(response.data.data.accessToken);
    }
    return response.data;
  },
};
