'use client';

import React, { useState, useRef } from 'react';
import Link from 'next/link';
import { useParams } from 'next/navigation';
import { 
  Package, 
  LogOut,
  Bell,
  User,
  ArrowLeft,
  CheckCircle,
  XCircle,
  Clock,
  Truck,
  Camera,
  Upload,
  MapPin,
  IndianRupee,
  FileText,
  AlertTriangle,
  ChevronRight
} from 'lucide-react';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';

/**
 * Partner Order Details Page - FRD-005 FR-54, FR-55, FR-56, FR-57, FR-58
 * Order details, acceptance, status updates, proof upload, shipment creation
 */

const mockOrder = {
  orderId: 'order-789',
  orderNumber: 'BK-2026-0789',
  status: 'PENDING_ACCEPTANCE',
  assignedAt: '2026-01-31T10:30:00Z',
  acceptanceDeadline: '2026-01-31T11:00:00Z',
  productName: 'Branded Premium T-Shirt',
  productCategory: 'Apparel',
  quantity: 25,
  size: 'L',
  color: 'Navy Blue',
  customizationType: 'Screen Print',
  printArea: 'Front Center',
  designUrl: 'https://example.com/design.png',
  productionNotes: 'Client prefers matte finish. Double check logo alignment.',
  totalOrderValue: 6500,
  partnerEarnings: 5720,
  platformCommission: 780,
  deliveryInfo: {
    name: 'Acme Corp Office',
    phone: '+91 98765 43210',
    address: '123, Business Park, Sector 5',
    city: 'Ahmedabad',
    state: 'Gujarat',
    pincode: '380015',
    expectedDelivery: '2026-02-05'
  },
  timeline: [
    { status: 'ORDER_PLACED', timestamp: '2026-01-31T09:00:00Z', note: 'Order placed by client' },
    { status: 'ASSIGNED', timestamp: '2026-01-31T10:30:00Z', note: 'Assigned to partner' },
  ],
  proofImages: [],
  shipment: null
};

const statusConfig: Record<string, { bg: string; text: string; icon: React.ReactNode; label: string }> = {
  'PENDING_ACCEPTANCE': { bg: 'bg-amber-100', text: 'text-amber-700', icon: <Clock className="w-5 h-5" />, label: 'Pending Acceptance' },
  'ACCEPTED': { bg: 'bg-blue-100', text: 'text-blue-700', icon: <CheckCircle className="w-5 h-5" />, label: 'Accepted' },
  'IN_PRODUCTION': { bg: 'bg-purple-100', text: 'text-purple-700', icon: <Package className="w-5 h-5" />, label: 'In Production' },
  'QUALITY_CHECK': { bg: 'bg-indigo-100', text: 'text-indigo-700', icon: <CheckCircle className="w-5 h-5" />, label: 'Quality Check' },
  'READY_TO_SHIP': { bg: 'bg-teal-100', text: 'text-teal-700', icon: <Package className="w-5 h-5" />, label: 'Ready to Ship' },
  'SHIPPED': { bg: 'bg-green-100', text: 'text-green-700', icon: <Truck className="w-5 h-5" />, label: 'Shipped' },
  'DELIVERED': { bg: 'bg-green-100', text: 'text-green-700', icon: <CheckCircle className="w-5 h-5" />, label: 'Delivered' },
  'REJECTED': { bg: 'bg-red-100', text: 'text-red-700', icon: <XCircle className="w-5 h-5" />, label: 'Rejected' },
};

const productionStatuses = [
  'ACCEPTED',
  'IN_PRODUCTION',
  'QUALITY_CHECK',
  'READY_TO_SHIP'
];

const formatCurrency = (amount: number) => {
  return new Intl.NumberFormat('en-IN', { 
    style: 'currency', 
    currency: 'INR',
    minimumFractionDigits: 0
  }).format(amount);
};

export default function PartnerOrderDetailsPage() {
  const params = useParams();
  const [order, setOrder] = useState(mockOrder);
  const [showRejectModal, setShowRejectModal] = useState(false);
  const [rejectReason, setRejectReason] = useState('');
  const [showShipmentModal, setShowShipmentModal] = useState(false);
  const [shipmentData, setShipmentData] = useState({
    courierPartner: '',
    trackingId: '',
    weight: '',
    numberOfPackages: '1'
  });
  const [uploadingProof, setUploadingProof] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleAccept = () => {
    setOrder({ ...order, status: 'ACCEPTED' });
    // API call to accept
  };

  const handleReject = () => {
    if (rejectReason.trim()) {
      setOrder({ ...order, status: 'REJECTED' });
      setShowRejectModal(false);
      // API call to reject
    }
  };

  const handleStatusUpdate = (newStatus: string) => {
    setOrder({ ...order, status: newStatus });
    // API call to update status
  };

  const handleProofUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = e.target.files;
    if (files && files.length > 0) {
      setUploadingProof(true);
      // Simulate upload
      setTimeout(() => {
        setUploadingProof(false);
        // In real implementation, upload to S3 and get URL
      }, 2000);
    }
  };

  const handleCreateShipment = () => {
    if (shipmentData.courierPartner && shipmentData.trackingId) {
      setOrder({ ...order, status: 'SHIPPED' });
      setShowShipmentModal(false);
      // API call to create shipment
    }
  };

  const currentStatusIndex = productionStatuses.indexOf(order.status);

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-slate-50 to-blue-50">
      {/* Header */}
      <header className="bg-white/80 backdrop-blur-md border-b border-slate-200 sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between h-16">
            <div className="flex items-center gap-4">
              <Link href="/partner/orders" className="p-2 hover:bg-slate-100 rounded-lg transition-colors">
                <ArrowLeft size={20} className="text-slate-600" />
              </Link>
              <div className="flex items-center gap-2">
                <div className="w-9 h-9 bg-gradient-to-br from-indigo-500 to-purple-600 rounded-xl flex items-center justify-center shadow-lg shadow-indigo-200">
                  <span className="text-white font-bold text-lg font-display">B</span>
                </div>
                <span className="text-xl font-bold font-display text-slate-900">
                  Brand<span className="text-indigo-600">Kit</span>
                </span>
              </div>
              <span className="px-3 py-1 bg-gradient-to-r from-indigo-500 to-purple-500 text-white text-xs font-semibold rounded-full shadow-sm">
                Partner Portal
              </span>
            </div>

            <div className="flex items-center gap-3">
              <button className="relative p-2 text-slate-500 hover:text-indigo-600 hover:bg-indigo-50 rounded-lg transition-colors">
                <Bell size={20} />
              </button>
              <Link href="/partner/profile">
                <Button variant="ghost" size="sm" leftIcon={<User size={18} />}>Profile</Button>
              </Link>
              <Link href="/api/auth/logout">
                <Button variant="ghost" size="sm" leftIcon={<LogOut size={18} />}>Logout</Button>
              </Link>
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Order Header */}
        <div className="bg-white rounded-2xl shadow-sm border border-slate-100 p-6 mb-6">
          <div className="flex items-start justify-between">
            <div>
              <div className="flex items-center gap-3 mb-2">
                <h1 className="text-2xl font-bold text-slate-900">{order.orderNumber}</h1>
                <span className={`inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-sm font-medium ${
                  statusConfig[order.status]?.bg} ${statusConfig[order.status]?.text
                }`}>
                  {statusConfig[order.status]?.icon}
                  {statusConfig[order.status]?.label}
                </span>
              </div>
              <p className="text-slate-600">{order.productName} × {order.quantity}</p>
            </div>

            {/* Action Buttons */}
            {order.status === 'PENDING_ACCEPTANCE' && (
              <div className="flex gap-3">
                <Button variant="outline" onClick={() => setShowRejectModal(true)}>
                  <XCircle size={18} className="mr-2" /> Reject
                </Button>
                <Button onClick={handleAccept}>
                  <CheckCircle size={18} className="mr-2" /> Accept Order
                </Button>
              </div>
            )}

            {order.status === 'READY_TO_SHIP' && (
              <Button onClick={() => setShowShipmentModal(true)}>
                <Truck size={18} className="mr-2" /> Create Shipment
              </Button>
            )}
          </div>

          {/* Acceptance Timer */}
          {order.status === 'PENDING_ACCEPTANCE' && (
            <div className="mt-4 p-4 bg-amber-50 border border-amber-100 rounded-xl flex items-center gap-3">
              <AlertTriangle className="w-5 h-5 text-amber-600" />
              <div>
                <p className="text-sm font-medium text-amber-900">Action Required</p>
                <p className="text-sm text-amber-700">Accept or reject within 30 minutes of assignment.</p>
              </div>
            </div>
          )}
        </div>

        <div className="grid lg:grid-cols-3 gap-6">
          {/* Main Content */}
          <div className="lg:col-span-2 space-y-6">
            {/* Product Details */}
            <div className="bg-white rounded-2xl shadow-sm border border-slate-100 p-6">
              <h2 className="text-lg font-bold text-slate-900 mb-4 flex items-center gap-2">
                <Package className="w-5 h-5 text-indigo-600" />
                Product Details
              </h2>

              <div className="grid md:grid-cols-2 gap-4">
                <div className="p-4 bg-slate-50 rounded-xl">
                  <p className="text-xs text-slate-500 mb-1">Product</p>
                  <p className="font-medium text-slate-900">{order.productName}</p>
                </div>
                <div className="p-4 bg-slate-50 rounded-xl">
                  <p className="text-xs text-slate-500 mb-1">Category</p>
                  <p className="font-medium text-slate-900">{order.productCategory}</p>
                </div>
                <div className="p-4 bg-slate-50 rounded-xl">
                  <p className="text-xs text-slate-500 mb-1">Quantity</p>
                  <p className="font-medium text-slate-900">{order.quantity} units</p>
                </div>
                <div className="p-4 bg-slate-50 rounded-xl">
                  <p className="text-xs text-slate-500 mb-1">Size / Color</p>
                  <p className="font-medium text-slate-900">{order.size} / {order.color}</p>
                </div>
                <div className="p-4 bg-slate-50 rounded-xl">
                  <p className="text-xs text-slate-500 mb-1">Customization</p>
                  <p className="font-medium text-slate-900">{order.customizationType}</p>
                </div>
                <div className="p-4 bg-slate-50 rounded-xl">
                  <p className="text-xs text-slate-500 mb-1">Print Area</p>
                  <p className="font-medium text-slate-900">{order.printArea}</p>
                </div>
              </div>

              {order.productionNotes && (
                <div className="mt-4 p-4 bg-indigo-50 rounded-xl">
                  <p className="text-xs text-indigo-600 font-medium mb-1">Production Notes</p>
                  <p className="text-sm text-indigo-900">{order.productionNotes}</p>
                </div>
              )}

              {/* Design Preview */}
              <div className="mt-4">
                <p className="text-sm font-medium text-slate-700 mb-2">Design File</p>
                <a 
                  href={order.designUrl} 
                  target="_blank" 
                  rel="noopener noreferrer"
                  className="inline-flex items-center gap-2 px-4 py-2 bg-slate-100 hover:bg-slate-200 rounded-xl text-sm text-slate-700 transition-colors"
                >
                  <FileText size={16} />
                  Download Design
                </a>
              </div>
            </div>

            {/* Production Status Update */}
            {['ACCEPTED', 'IN_PRODUCTION', 'QUALITY_CHECK', 'READY_TO_SHIP'].includes(order.status) && (
              <div className="bg-white rounded-2xl shadow-sm border border-slate-100 p-6">
                <h2 className="text-lg font-bold text-slate-900 mb-4">Update Production Status</h2>
                
                <div className="flex items-center gap-2 overflow-x-auto pb-2">
                  {productionStatuses.map((status, index) => {
                    const isCompleted = index < currentStatusIndex;
                    const isCurrent = status === order.status;
                    const isNext = index === currentStatusIndex + 1;

                    return (
                      <React.Fragment key={status}>
                        <button
                          onClick={() => isNext && handleStatusUpdate(status)}
                          disabled={!isNext}
                          className={`flex-shrink-0 px-4 py-2 rounded-xl text-sm font-medium transition-all ${
                            isCompleted
                              ? 'bg-green-100 text-green-700'
                              : isCurrent
                              ? 'bg-indigo-600 text-white'
                              : isNext
                              ? 'bg-indigo-100 text-indigo-700 hover:bg-indigo-200 cursor-pointer'
                              : 'bg-slate-100 text-slate-400 cursor-not-allowed'
                          }`}
                        >
                          {statusConfig[status]?.label}
                        </button>
                        {index < productionStatuses.length - 1 && (
                          <ChevronRight className="w-4 h-4 text-slate-300 flex-shrink-0" />
                        )}
                      </React.Fragment>
                    );
                  })}
                </div>
              </div>
            )}

            {/* Proof Upload */}
            {['IN_PRODUCTION', 'QUALITY_CHECK', 'READY_TO_SHIP'].includes(order.status) && (
              <div className="bg-white rounded-2xl shadow-sm border border-slate-100 p-6">
                <h2 className="text-lg font-bold text-slate-900 mb-4 flex items-center gap-2">
                  <Camera className="w-5 h-5 text-indigo-600" />
                  Production Proof Images
                </h2>

                <input
                  type="file"
                  ref={fileInputRef}
                  onChange={handleProofUpload}
                  accept="image/*"
                  multiple
                  className="hidden"
                />

                <div className="grid grid-cols-3 gap-4">
                  {/* Upload Button */}
                  <button
                    onClick={() => fileInputRef.current?.click()}
                    disabled={uploadingProof}
                    className="aspect-square border-2 border-dashed border-slate-200 rounded-xl flex flex-col items-center justify-center gap-2 text-slate-400 hover:border-indigo-300 hover:text-indigo-500 hover:bg-indigo-50 transition-colors"
                  >
                    {uploadingProof ? (
                      <div className="w-6 h-6 border-2 border-indigo-500 border-t-transparent rounded-full animate-spin" />
                    ) : (
                      <>
                        <Upload size={24} />
                        <span className="text-xs font-medium">Upload</span>
                      </>
                    )}
                  </button>

                  {/* Placeholder for uploaded images */}
                  {order.proofImages.map((image: any, idx: number) => (
                    <div key={idx} className="aspect-square bg-slate-100 rounded-xl" />
                  ))}
                </div>

                <p className="text-xs text-slate-500 mt-3">
                  Upload clear images of the finished product for quality verification. Maximum 5 images.
                </p>
              </div>
            )}
          </div>

          {/* Sidebar */}
          <div className="space-y-6">
            {/* Earnings Card */}
            <div className="bg-gradient-to-br from-green-500 to-emerald-600 rounded-2xl p-6 text-white shadow-lg">
              <h3 className="text-green-100 text-sm font-medium mb-1">Your Earnings</h3>
              <p className="text-3xl font-bold">{formatCurrency(order.partnerEarnings)}</p>
              <div className="mt-4 pt-4 border-t border-white/20 text-sm">
                <div className="flex justify-between mb-1">
                  <span className="text-green-100">Order Value</span>
                  <span>{formatCurrency(order.totalOrderValue)}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-green-100">Platform Fee</span>
                  <span>-{formatCurrency(order.platformCommission)}</span>
                </div>
              </div>
            </div>

            {/* Delivery Info */}
            <div className="bg-white rounded-2xl shadow-sm border border-slate-100 p-6">
              <h3 className="font-bold text-slate-900 mb-4 flex items-center gap-2">
                <MapPin className="w-5 h-5 text-indigo-600" />
                Delivery Address
              </h3>

              <div className="space-y-2 text-sm">
                <p className="font-medium text-slate-900">{order.deliveryInfo.name}</p>
                <p className="text-slate-600">{order.deliveryInfo.address}</p>
                <p className="text-slate-600">
                  {order.deliveryInfo.city}, {order.deliveryInfo.state} - {order.deliveryInfo.pincode}
                </p>
                <p className="text-slate-600">{order.deliveryInfo.phone}</p>
              </div>

              <div className="mt-4 pt-4 border-t border-slate-100">
                <p className="text-xs text-slate-500">Expected Delivery</p>
                <p className="font-medium text-slate-900">
                  {new Date(order.deliveryInfo.expectedDelivery).toLocaleDateString('en-IN', {
                    weekday: 'short',
                    year: 'numeric',
                    month: 'short',
                    day: 'numeric'
                  })}
                </p>
              </div>
            </div>

            {/* Order Timeline */}
            <div className="bg-white rounded-2xl shadow-sm border border-slate-100 p-6">
              <h3 className="font-bold text-slate-900 mb-4">Order Timeline</h3>

              <div className="space-y-4">
                {order.timeline.map((event, idx) => (
                  <div key={idx} className="flex gap-3">
                    <div className="flex flex-col items-center">
                      <div className="w-2 h-2 bg-indigo-500 rounded-full" />
                      {idx < order.timeline.length - 1 && (
                        <div className="w-0.5 h-full bg-slate-200 mt-1" />
                      )}
                    </div>
                    <div className="pb-4">
                      <p className="text-sm font-medium text-slate-900">{event.note}</p>
                      <p className="text-xs text-slate-500">
                        {new Date(event.timestamp).toLocaleString('en-IN')}
                      </p>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      </main>

      {/* Reject Modal */}
      {showRejectModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <div className="bg-white rounded-2xl p-6 max-w-md w-full mx-4 shadow-2xl">
            <h3 className="text-xl font-bold text-slate-900 mb-2">Reject Order</h3>
            <p className="text-sm text-slate-600 mb-4">
              Please provide a reason for rejecting this order. This will be recorded for future reference.
            </p>
            
            <textarea
              value={rejectReason}
              onChange={(e) => setRejectReason(e.target.value)}
              className="w-full p-3 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-red-500 mb-4"
              rows={4}
              placeholder="Enter rejection reason..."
            />

            <div className="flex gap-3">
              <button
                onClick={() => setShowRejectModal(false)}
                className="flex-1 py-2.5 px-4 border border-slate-200 rounded-xl text-slate-600 font-medium hover:bg-slate-50 transition-colors"
              >
                Cancel
              </button>
              <button
                onClick={handleReject}
                disabled={!rejectReason.trim()}
                className="flex-1 py-2.5 px-4 bg-red-600 text-white rounded-xl font-medium hover:bg-red-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              >
                Confirm Reject
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Shipment Modal */}
      {showShipmentModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <div className="bg-white rounded-2xl p-6 max-w-md w-full mx-4 shadow-2xl">
            <h3 className="text-xl font-bold text-slate-900 mb-4">Create Shipment</h3>
            
            <div className="space-y-4 mb-6">
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-2">Courier Partner</label>
                <select
                  value={shipmentData.courierPartner}
                  onChange={(e) => setShipmentData({ ...shipmentData, courierPartner: e.target.value })}
                  className="w-full p-3 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-500"
                >
                  <option value="">Select courier...</option>
                  <option value="delhivery">Delhivery</option>
                  <option value="bluedart">Blue Dart</option>
                  <option value="dtdc">DTDC</option>
                  <option value="ecom">Ecom Express</option>
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-slate-700 mb-2">Tracking ID / AWB</label>
                <Input
                  value={shipmentData.trackingId}
                  onChange={(e) => setShipmentData({ ...shipmentData, trackingId: e.target.value })}
                  placeholder="Enter tracking number"
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-2">Weight (kg)</label>
                  <Input
                    type="number"
                    value={shipmentData.weight}
                    onChange={(e) => setShipmentData({ ...shipmentData, weight: e.target.value })}
                    placeholder="0.5"
                    step="0.1"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-2">Packages</label>
                  <Input
                    type="number"
                    value={shipmentData.numberOfPackages}
                    onChange={(e) => setShipmentData({ ...shipmentData, numberOfPackages: e.target.value })}
                    min="1"
                  />
                </div>
              </div>
            </div>

            <div className="flex gap-3">
              <button
                onClick={() => setShowShipmentModal(false)}
                className="flex-1 py-2.5 px-4 border border-slate-200 rounded-xl text-slate-600 font-medium hover:bg-slate-50 transition-colors"
              >
                Cancel
              </button>
              <button
                onClick={handleCreateShipment}
                disabled={!shipmentData.courierPartner || !shipmentData.trackingId}
                className="flex-1 py-2.5 px-4 bg-indigo-600 text-white rounded-xl font-medium hover:bg-indigo-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              >
                Create Shipment
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
