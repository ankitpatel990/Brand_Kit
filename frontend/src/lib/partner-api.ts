/**
 * Partner Dashboard API Client - FRD-005
 * Internal Partner Portal API Functions
 */

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

// Types
export interface PartnerDashboard {
  summary: {
    pendingOrders: number;
    activeOrders: number;
    readyToShip: number;
    revenueThisMonth: number;
    activeDiscounts: number;
  };
  recentOrders: RecentOrder[];
  alerts: Alert[];
  discountStatus: {
    active: number;
    pending: number;
    disabled: number;
  };
  unreadNotifications: number;
}

export interface RecentOrder {
  orderId: string;
  orderNumber: string;
  productName: string;
  quantity: number;
  status: string;
  orderDate: string;
  expectedShipDate: string;
  partnerEarnings: number;
}

export interface Alert {
  type: string;
  title: string;
  message: string;
  actionUrl: string;
  orderId?: string;
}

export interface PartnerOrder {
  orderId: string;
  orderNumber: string;
  clientName: string;
  productName: string;
  quantity: number;
  orderDate: string;
  expectedShipDate: string;
  status: string;
  partnerStatus: string;
  partnerEarnings: number;
  actions: string[];
}

export interface OrderDetails {
  orderId: string;
  orderNumber: string;
  orderDate: string;
  status: string;
  partnerStatus: string;
  expectedShipDate: string;
  product: {
    productId: string;
    name: string;
    category: string;
    quantity: number;
    customizationType: string;
    printReadyImageUrl: string;
    previewImageUrl: string;
    specifications: string;
  };
  delivery: {
    city: string;
    state: string;
    pinCode: string;
    fullAddress: string | null;
    deliveryOption: string;
    addressRevealed: boolean;
  };
  commission: {
    productAmount: number;
    discountAmount: number;
    finalAmount: number;
    commissionPercentage: number;
    platformCommission: number;
    partnerEarnings: number;
  };
  timeline: { status: string; description: string; timestamp: string }[];
  proofs: { id: string; imageUrl: string; caption: string; uploadedAt: string }[];
  actions: string[];
  notes: string;
}

export interface Settlement {
  settlementId: string;
  settlementNumber: string;
  period: string;
  orderCount: number;
  totalAmount: number;
  status: string;
  date: string;
  statementUrl: string;
}

export interface SettlementSummary {
  totalEarningsAllTime: number;
  pendingSettlement: number;
  lastSettlement: { amount: number; date: string } | null;
  nextSettlementDate: string;
}

export interface PerformanceMetrics {
  period: string;
  metrics: {
    fulfillmentRate: number;
    averageLeadTime: number;
    deliverySuccessRate: number;
    averageRating: number;
    totalOrdersFulfilled: number;
    totalRevenue: number;
    totalOrdersAssigned: number;
    totalOrdersAccepted: number;
    totalOrdersRejected: number;
  };
  benchmark: {
    platformAverageFulfillment: number;
    platformAverageLeadTime: number;
  };
  alerts: { type: string; message: string }[];
}

export interface Notification {
  id: string;
  type: string;
  title: string;
  message: string;
  orderId?: string;
  orderNumber?: string;
  isRead: boolean;
  createdAt: string;
}

// Helper function for authenticated requests
async function fetchWithAuth(url: string, options: RequestInit = {}) {
  const token = typeof window !== 'undefined' ? localStorage.getItem('accessToken') : null;
  
  const headers: HeadersInit = {
    'Content-Type': 'application/json',
    ...(options.headers || {}),
  };
  
  if (token) {
    (headers as Record<string, string>)['Authorization'] = `Bearer ${token}`;
  }
  
  const response = await fetch(`${API_BASE_URL}${url}`, {
    ...options,
    headers,
  });
  
  if (!response.ok) {
    const error = await response.json().catch(() => ({ message: 'Request failed' }));
    throw new Error(error.message || 'Request failed');
  }
  
  return response.json();
}

// Dashboard API
export async function getDashboard(): Promise<PartnerDashboard> {
  return fetchWithAuth('/partner/dashboard');
}

// Orders API
export async function getOrders(status?: string, page = 0, size = 20) {
  const params = new URLSearchParams({ page: String(page), size: String(size) });
  if (status) params.append('status', status);
  return fetchWithAuth(`/partner/orders?${params}`);
}

export async function getOrderDetails(orderId: string): Promise<OrderDetails> {
  return fetchWithAuth(`/partner/orders/${orderId}`);
}

export async function acceptOrder(orderId: string): Promise<OrderDetails> {
  return fetchWithAuth(`/partner/orders/${orderId}/accept`, { method: 'POST' });
}

export async function rejectOrder(orderId: string, reason: string): Promise<void> {
  return fetchWithAuth(`/partner/orders/${orderId}/reject`, {
    method: 'POST',
    body: JSON.stringify({ reason }),
  });
}

export async function updateOrderStatus(orderId: string, status: string): Promise<OrderDetails> {
  return fetchWithAuth(`/partner/orders/${orderId}/status`, {
    method: 'PUT',
    body: JSON.stringify({ status }),
  });
}

export async function markAsShipped(orderId: string, shipmentData: {
  courierName: string;
  trackingId: string;
  shipDate: string;
  weightKg?: number;
  numPackages?: number;
  notes?: string;
}): Promise<void> {
  return fetchWithAuth(`/partner/orders/${orderId}/ship`, {
    method: 'POST',
    body: JSON.stringify(shipmentData),
  });
}

// Settlements API
export async function getSettlements(page = 0, size = 10) {
  return fetchWithAuth(`/partner/settlements?page=${page}&size=${size}`);
}

export async function getSettlementDetails(settlementId: string) {
  return fetchWithAuth(`/partner/settlements/${settlementId}`);
}

// Performance API
export async function getPerformanceMetrics(period = 'all_time'): Promise<PerformanceMetrics> {
  return fetchWithAuth(`/partner/performance?period=${period}`);
}

// Notifications API
export async function getNotifications(page = 0, size = 20) {
  return fetchWithAuth(`/partner/notifications?page=${page}&size=${size}`);
}

export async function getUnreadCount(): Promise<{ count: number }> {
  return fetchWithAuth('/partner/notifications/unread-count');
}

export async function markNotificationRead(notificationId: string): Promise<void> {
  return fetchWithAuth(`/partner/notifications/${notificationId}/read`, { method: 'PUT' });
}

export async function markAllNotificationsRead(): Promise<{ marked: number }> {
  return fetchWithAuth('/partner/notifications/read-all', { method: 'PUT' });
}

// Discounts API
export async function getDiscounts(page = 0, size = 20) {
  return fetchWithAuth(`/partner/discounts?page=${page}&size=${size}`);
}

export async function createDiscount(productId: string, discountPercentage: number) {
  return fetchWithAuth('/partner/discounts', {
    method: 'POST',
    body: JSON.stringify({ productId, discountPercentage }),
  });
}

export async function deleteDiscount(discountId: string): Promise<void> {
  return fetchWithAuth(`/partner/discounts/${discountId}`, { method: 'DELETE' });
}

// Profile API
export async function getProfile() {
  return fetchWithAuth('/partner/profile');
}

export async function updateProfile(profileData: Record<string, unknown>) {
  return fetchWithAuth('/partner/profile', {
    method: 'PUT',
    body: JSON.stringify(profileData),
  });
}

export async function completeProfile(profileData: Record<string, unknown>) {
  return fetchWithAuth('/partner/profile/complete', {
    method: 'POST',
    body: JSON.stringify(profileData),
  });
}
