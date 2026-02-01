import api from './api';

// Types
export interface CartItem {
  cartItemId: string;
  productId: string;
  productName: string;
  productSlug: string;
  productImageUrl: string | null;
  customizationId: string | null;
  previewUrl: string | null;
  hasCustomization: boolean;
  quantity: number;
  originalUnitPrice: number;
  unitPrice: number;
  discountPercentage: number;
  customizationFee: number;
  effectiveUnitPrice: number;
  subtotal: number;
}

export interface CartPricing {
  originalSubtotal: number;
  subtotal: number;
  totalDiscount: number;
  gst: number;
  deliveryCharges: number;
  total: number;
  freeDeliveryEligible: boolean;
  freeDeliveryThreshold: number;
}

export interface Cart {
  cartId: string;
  userId: string | null;
  items: CartItem[];
  itemCount: number;
  totalQuantity: number;
  pricing: CartPricing;
}

export interface AddToCartRequest {
  productId: string;
  customizationId?: string;
  quantity: number;
}

export interface UpdateCartItemRequest {
  quantity: number;
}

export interface ValidationError {
  itemId: string | null;
  errorCode: string;
  message: string;
}

export interface CartValidationResponse {
  isValid: boolean;
  errors: ValidationError[];
  invalidItemIds: string[];
  pricesUpdated: boolean;
  updatedCart: Cart;
}

export interface Address {
  id: string;
  fullName: string;
  phone: string;
  addressLine1: string;
  addressLine2: string | null;
  city: string;
  state: string;
  pinCode: string;
  addressType: 'HOME' | 'OFFICE' | 'OTHER';
  isDefault: boolean;
  isServiceable: boolean;
  formattedAddress: string;
}

export interface AddressRequest {
  fullName: string;
  phone: string;
  addressLine1: string;
  addressLine2?: string;
  city: string;
  state: string;
  pinCode: string;
  addressType: 'HOME' | 'OFFICE' | 'OTHER';
  isDefault?: boolean;
}

export interface DeliveryOptionResponse {
  option: 'STANDARD' | 'EXPRESS';
  displayName: string;
  charge: number;
  isFree: boolean;
  deliveryTimeRange: string;
  estimatedDeliveryStart: string;
  estimatedDeliveryEnd: string;
  isAvailable: boolean;
  unavailableReason: string | null;
}

export interface CheckoutRequest {
  deliveryAddressId: string;
  deliveryOption: 'STANDARD' | 'EXPRESS';
  termsAccepted: boolean;
  notes?: string;
}

export interface OrderPricing {
  originalSubtotal: number;
  subtotal: number;
  totalDiscount: number;
  discountPercentage: number;
  gstAmount: number;
  cgstAmount: number;
  sgstAmount: number;
  igstAmount: number;
  deliveryCharges: number;
  totalAmount: number;
  totalSavings: number;
}

export interface OrderItemResponse {
  id: string;
  productId: string;
  productName: string;
  productSlug: string;
  productImageUrl: string | null;
  previewImageUrl: string | null;
  customizationId: string | null;
  hasCustomization: boolean;
  hsnCode: string;
  quantity: number;
  originalUnitPrice: number;
  discountPercentage: number;
  unitPrice: number;
  customizationFee: number;
  effectiveUnitPrice: number;
  subtotal: number;
  discountAmount: number;
}

export interface TrackingInfo {
  courierName: string;
  trackingId: string;
  trackingUrl: string;
  estimatedDelivery: string;
}

export interface OrderStatusHistory {
  id: string;
  status: string;
  statusDisplayName: string;
  description: string;
  timestamp: string;
}

export interface OrderResponse {
  id: string;
  orderNumber: string;
  orderDate: string;
  status: string;
  statusDisplayName: string;
  items: OrderItemResponse[];
  itemCount: number;
  totalQuantity: number;
  deliveryAddress: Address;
  deliveryOption: 'STANDARD' | 'EXPRESS';
  deliveryOptionDisplayName: string;
  estimatedDeliveryStart: string | null;
  estimatedDeliveryEnd: string | null;
  estimatedDeliveryRange: string | null;
  actualDeliveryDate: string | null;
  pricing: OrderPricing;
  trackingInfo: TrackingInfo | null;
  invoiceNumber: string | null;
  invoiceUrl: string | null;
  cancelledAt: string | null;
  cancellationReason: string | null;
  refundAmount: number | null;
  refundStatus: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface OrderListResponse {
  id: string;
  orderNumber: string;
  orderDate: string;
  status: string;
  statusDisplayName: string;
  itemCount: number;
  totalQuantity: number;
  totalAmount: number;
  firstProductName: string | null;
  firstProductImageUrl: string | null;
  canReorder: boolean;
  hasInvoice: boolean;
}

export interface ReorderResponse {
  success: boolean;
  itemsAdded: number;
  itemsUnavailable: number;
  unavailableItems: Array<{
    productId: string;
    productName: string;
    reason: string;
  }>;
  pricesUpdated: boolean;
  cart: Cart;
  message: string;
}

// Cart API
export const cartApi = {
  getCart: async (): Promise<Cart> => {
    const response = await api.get('/api/cart');
    return response.data;
  },

  addToCart: async (request: AddToCartRequest): Promise<Cart> => {
    const response = await api.post('/api/cart/add', request);
    return response.data;
  },

  updateCartItem: async (itemId: string, request: UpdateCartItemRequest): Promise<Cart> => {
    const response = await api.put(`/api/cart/item/${itemId}`, request);
    return response.data;
  },

  removeCartItem: async (itemId: string): Promise<Cart> => {
    const response = await api.delete(`/api/cart/item/${itemId}`);
    return response.data;
  },

  clearCart: async (): Promise<void> => {
    await api.delete('/api/cart');
  },

  getCartCount: async (): Promise<number> => {
    const response = await api.get('/api/cart/count');
    return response.data.count;
  },

  validateCart: async (): Promise<CartValidationResponse> => {
    const response = await api.post('/api/cart/validate');
    return response.data;
  },

  removeInvalidItems: async (itemIds: string[]): Promise<Cart> => {
    const response = await api.post('/api/cart/remove-invalid', itemIds);
    return response.data;
  },
};

// Address API
export const addressApi = {
  getAddresses: async (): Promise<Address[]> => {
    const response = await api.get('/api/addresses');
    return response.data;
  },

  getAddress: async (id: string): Promise<Address> => {
    const response = await api.get(`/api/addresses/${id}`);
    return response.data;
  },

  getDefaultAddress: async (): Promise<Address | null> => {
    try {
      const response = await api.get('/api/addresses/default');
      return response.data;
    } catch {
      return null;
    }
  },

  createAddress: async (request: AddressRequest): Promise<Address> => {
    const response = await api.post('/api/addresses', request);
    return response.data;
  },

  updateAddress: async (id: string, request: AddressRequest): Promise<Address> => {
    const response = await api.put(`/api/addresses/${id}`, request);
    return response.data;
  },

  deleteAddress: async (id: string): Promise<void> => {
    await api.delete(`/api/addresses/${id}`);
  },

  setDefaultAddress: async (id: string): Promise<Address> => {
    const response = await api.put(`/api/addresses/${id}/default`);
    return response.data;
  },

  checkPinCode: async (pinCode: string): Promise<{ serviceable: boolean; message: string }> => {
    const response = await api.get(`/api/addresses/check-pincode/${pinCode}`);
    return response.data;
  },
};

// Checkout API
export const checkoutApi = {
  validateCart: async (): Promise<CartValidationResponse> => {
    const response = await api.post('/api/checkout/validate');
    return response.data;
  },

  getSummary: async (): Promise<Cart> => {
    const response = await api.get('/api/checkout/summary');
    return response.data;
  },

  getDeliveryOptions: async (pinCode: string): Promise<DeliveryOptionResponse[]> => {
    const response = await api.get(`/api/checkout/delivery-options/${pinCode}`);
    return response.data;
  },

  getAddresses: async (): Promise<Address[]> => {
    const response = await api.get('/api/checkout/addresses');
    return response.data;
  },
};

// Order API
export const orderApi = {
  createOrder: async (request: CheckoutRequest): Promise<OrderResponse> => {
    const response = await api.post('/api/orders/create', request);
    return response.data;
  },

  getOrders: async (
    page = 0,
    size = 10,
    status?: string,
    search?: string
  ): Promise<{ content: OrderListResponse[]; totalPages: number; totalElements: number }> => {
    const params = new URLSearchParams();
    params.append('page', page.toString());
    params.append('size', size.toString());
    if (status) params.append('status', status);
    if (search) params.append('search', search);
    
    const response = await api.get(`/api/orders?${params.toString()}`);
    return response.data;
  },

  getOrder: async (orderId: string): Promise<OrderResponse> => {
    const response = await api.get(`/api/orders/${orderId}`);
    return response.data;
  },

  getOrderByNumber: async (orderNumber: string): Promise<OrderResponse> => {
    const response = await api.get(`/api/orders/number/${orderNumber}`);
    return response.data;
  },

  getOrderStatusHistory: async (orderId: string): Promise<OrderStatusHistory[]> => {
    const response = await api.get(`/api/orders/${orderId}/status-history`);
    return response.data;
  },

  reorder: async (orderId: string): Promise<ReorderResponse> => {
    const response = await api.post(`/api/orders/${orderId}/reorder`);
    return response.data;
  },

  getInvoice: async (orderId: string): Promise<{ invoiceNumber: string; invoiceUrl: string }> => {
    const response = await api.get(`/api/orders/${orderId}/invoice`);
    return response.data;
  },
};

// Helper functions
export const formatPrice = (price: number): string => {
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    minimumFractionDigits: 0,
    maximumFractionDigits: 2,
  }).format(price);
};

export const formatDate = (dateString: string): string => {
  return new Date(dateString).toLocaleDateString('en-IN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  });
};

export const getStatusColor = (status: string): string => {
  const colors: Record<string, string> = {
    PENDING_PAYMENT: 'text-yellow-600 bg-yellow-50',
    PAYMENT_FAILED: 'text-red-600 bg-red-50',
    CONFIRMED: 'text-blue-600 bg-blue-50',
    ACCEPTED: 'text-blue-600 bg-blue-50',
    IN_PRODUCTION: 'text-purple-600 bg-purple-50',
    READY_TO_SHIP: 'text-indigo-600 bg-indigo-50',
    SHIPPED: 'text-cyan-600 bg-cyan-50',
    OUT_FOR_DELIVERY: 'text-teal-600 bg-teal-50',
    DELIVERED: 'text-green-600 bg-green-50',
    CANCELLED: 'text-gray-600 bg-gray-50',
    REFUND_INITIATED: 'text-orange-600 bg-orange-50',
    REFUNDED: 'text-gray-600 bg-gray-50',
  };
  return colors[status] || 'text-gray-600 bg-gray-50';
};
