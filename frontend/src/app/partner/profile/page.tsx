'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import { 
  User, 
  LogOut,
  Bell,
  Building2,
  Phone,
  Mail,
  MapPin,
  CreditCard,
  CheckCircle,
  AlertCircle,
  Save,
  ArrowLeft,
  Settings,
  Shield
} from 'lucide-react';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';

/**
 * Partner Profile Page - FRD-005 FR-64
 * Partner profile management
 */

const mockProfile = {
  partnerId: 'uuid-partner-123',
  email: 'partner@printmaster.in',
  businessName: 'PrintMaster Gujarat',
  ownerName: 'Vijay Patel',
  phone: '+919876543210',
  businessAddress: '123, Industrial Estate, GIDC',
  city: 'Ahmedabad',
  state: 'Gujarat',
  gstin: '24AAACP1234A1Z5',
  profilePictureUrl: null,
  profileCompleted: true,
  bankDetails: {
    accountHolder: 'Vijay Patel',
    bankName: 'HDFC Bank',
    accountNumber: 'XXXX1234',
    ifscCode: 'HDFC0001234',
    verified: true
  },
  capacitySettings: {
    maxConcurrentOrders: 25,
    isAcceptingOrders: true,
    currentActiveOrders: 8
  },
  categories: ['T-Shirts', 'Bags', 'Drinkware'],
  commissionRate: 12,
  status: 'ACTIVE',
  createdAt: '2025-06-15'
};

export default function PartnerProfilePage() {
  const [profile, setProfile] = useState(mockProfile);
  const [isEditing, setIsEditing] = useState(false);
  const [activeTab, setActiveTab] = useState('business');

  const handleSave = () => {
    // API call to save profile
    console.log('Saving profile:', profile);
    setIsEditing(false);
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-slate-50 to-indigo-50">
      {/* Header */}
      <header className="bg-white/80 backdrop-blur-md border-b border-slate-200 sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between h-16">
            <div className="flex items-center gap-4">
              <Link href="/partner/dashboard" className="p-2 hover:bg-slate-100 rounded-lg transition-colors">
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
              <Link href="/api/auth/logout">
                <Button variant="ghost" size="sm" leftIcon={<LogOut size={18} />}>Logout</Button>
              </Link>
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Profile Header */}
        <div className="bg-white rounded-2xl shadow-sm border border-slate-100 p-6 mb-6">
          <div className="flex items-start gap-6">
            <div className="w-24 h-24 bg-gradient-to-br from-indigo-400 to-purple-500 rounded-2xl flex items-center justify-center text-white text-3xl font-bold shadow-lg">
              {profile.businessName.charAt(0)}
            </div>
            <div className="flex-1">
              <div className="flex items-center gap-3 mb-2">
                <h1 className="text-2xl font-bold text-slate-900">{profile.businessName}</h1>
                <span className={`inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-medium ${
                  profile.status === 'ACTIVE' ? 'bg-green-100 text-green-700' : 'bg-slate-100 text-slate-700'
                }`}>
                  <CheckCircle className="w-3 h-3" />
                  {profile.status}
                </span>
              </div>
              <p className="text-slate-600 mb-2">{profile.ownerName}</p>
              <div className="flex items-center gap-4 text-sm text-slate-500">
                <span className="flex items-center gap-1">
                  <Mail className="w-4 h-4" />
                  {profile.email}
                </span>
                <span className="flex items-center gap-1">
                  <MapPin className="w-4 h-4" />
                  {profile.city}, {profile.state}
                </span>
              </div>
            </div>
            <div className="flex gap-2">
              {isEditing ? (
                <>
                  <Button variant="outline" onClick={() => setIsEditing(false)}>Cancel</Button>
                  <Button leftIcon={<Save size={18} />} onClick={handleSave}>Save Changes</Button>
                </>
              ) : (
                <Button variant="outline" leftIcon={<Settings size={18} />} onClick={() => setIsEditing(true)}>
                  Edit Profile
                </Button>
              )}
            </div>
          </div>
        </div>

        {/* Tabs */}
        <div className="flex gap-2 mb-6">
          {[
            { key: 'business', label: 'Business Info', icon: <Building2 size={18} /> },
            { key: 'bank', label: 'Bank Details', icon: <CreditCard size={18} /> },
            { key: 'capacity', label: 'Capacity', icon: <Settings size={18} /> },
          ].map((tab) => (
            <button
              key={tab.key}
              onClick={() => setActiveTab(tab.key)}
              className={`flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-medium transition-all ${
                activeTab === tab.key
                  ? 'bg-indigo-600 text-white shadow-lg shadow-indigo-200'
                  : 'bg-white text-slate-600 hover:bg-slate-50 border border-slate-200'
              }`}
            >
              {tab.icon}
              {tab.label}
            </button>
          ))}
        </div>

        {/* Tab Content */}
        <div className="bg-white rounded-2xl shadow-sm border border-slate-100 p-6">
          {/* Business Info Tab */}
          {activeTab === 'business' && (
            <div className="space-y-6">
              <h2 className="text-lg font-bold text-slate-900 flex items-center gap-2">
                <Building2 className="w-5 h-5 text-indigo-600" />
                Business Information
              </h2>

              <div className="grid md:grid-cols-2 gap-6">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-2">Business Name</label>
                  <Input
                    value={profile.businessName}
                    onChange={(e) => setProfile({ ...profile, businessName: e.target.value })}
                    disabled={!isEditing}
                    placeholder="Enter business name"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-2">Owner Name</label>
                  <Input
                    value={profile.ownerName}
                    onChange={(e) => setProfile({ ...profile, ownerName: e.target.value })}
                    disabled={!isEditing}
                    placeholder="Enter owner name"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-2">Phone Number</label>
                  <Input
                    value={profile.phone}
                    onChange={(e) => setProfile({ ...profile, phone: e.target.value })}
                    disabled={!isEditing}
                    placeholder="+91XXXXXXXXXX"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-2">GSTIN</label>
                  <Input
                    value={profile.gstin}
                    onChange={(e) => setProfile({ ...profile, gstin: e.target.value })}
                    disabled={!isEditing}
                    placeholder="Enter GSTIN"
                  />
                </div>

                <div className="md:col-span-2">
                  <label className="block text-sm font-medium text-slate-700 mb-2">Business Address</label>
                  <textarea
                    value={profile.businessAddress}
                    onChange={(e) => setProfile({ ...profile, businessAddress: e.target.value })}
                    disabled={!isEditing}
                    className="w-full p-3 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-500 disabled:bg-slate-50 disabled:text-slate-500"
                    rows={3}
                    placeholder="Enter full business address"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-2">City</label>
                  <Input
                    value={profile.city}
                    onChange={(e) => setProfile({ ...profile, city: e.target.value })}
                    disabled={!isEditing}
                    placeholder="City"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-2">State</label>
                  <Input
                    value={profile.state}
                    onChange={(e) => setProfile({ ...profile, state: e.target.value })}
                    disabled={!isEditing}
                    placeholder="State"
                  />
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-slate-700 mb-2">Product Categories</label>
                <div className="flex flex-wrap gap-2">
                  {profile.categories.map((cat, idx) => (
                    <span key={idx} className="px-3 py-1 bg-indigo-100 text-indigo-700 rounded-full text-sm font-medium">
                      {cat}
                    </span>
                  ))}
                </div>
              </div>
            </div>
          )}

          {/* Bank Details Tab */}
          {activeTab === 'bank' && (
            <div className="space-y-6">
              <div className="flex items-center justify-between">
                <h2 className="text-lg font-bold text-slate-900 flex items-center gap-2">
                  <CreditCard className="w-5 h-5 text-indigo-600" />
                  Bank Account Details
                </h2>
                {profile.bankDetails.verified ? (
                  <span className="inline-flex items-center gap-1 px-3 py-1 bg-green-100 text-green-700 rounded-full text-sm font-medium">
                    <Shield className="w-4 h-4" />
                    Verified
                  </span>
                ) : (
                  <span className="inline-flex items-center gap-1 px-3 py-1 bg-amber-100 text-amber-700 rounded-full text-sm font-medium">
                    <AlertCircle className="w-4 h-4" />
                    Pending Verification
                  </span>
                )}
              </div>

              <div className="bg-amber-50 border border-amber-100 rounded-xl p-4 flex items-start gap-3">
                <AlertCircle className="w-5 h-5 text-amber-600 mt-0.5" />
                <div>
                  <p className="text-sm text-amber-900 font-medium">Important</p>
                  <p className="text-sm text-amber-700">
                    Bank details are used for commission settlements. Changing these details will require re-verification by admin.
                  </p>
                </div>
              </div>

              <div className="grid md:grid-cols-2 gap-6">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-2">Account Holder Name</label>
                  <Input
                    value={profile.bankDetails.accountHolder}
                    disabled={!isEditing}
                    placeholder="Enter account holder name"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-2">Bank Name</label>
                  <Input
                    value={profile.bankDetails.bankName}
                    disabled={!isEditing}
                    placeholder="Enter bank name"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-2">Account Number</label>
                  <Input
                    value={profile.bankDetails.accountNumber}
                    disabled={!isEditing}
                    placeholder="Enter account number"
                    type={isEditing ? 'text' : 'password'}
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-2">IFSC Code</label>
                  <Input
                    value={profile.bankDetails.ifscCode}
                    disabled={!isEditing}
                    placeholder="Enter IFSC code"
                  />
                </div>
              </div>
            </div>
          )}

          {/* Capacity Tab */}
          {activeTab === 'capacity' && (
            <div className="space-y-6">
              <h2 className="text-lg font-bold text-slate-900 flex items-center gap-2">
                <Settings className="w-5 h-5 text-indigo-600" />
                Capacity Settings
              </h2>

              <div className="grid md:grid-cols-2 gap-6">
                <div className="bg-slate-50 rounded-xl p-6">
                  <h3 className="font-medium text-slate-900 mb-4">Order Capacity</h3>
                  <div className="space-y-4">
                    <div>
                      <label className="block text-sm font-medium text-slate-700 mb-2">Max Concurrent Orders</label>
                      <Input
                        type="number"
                        value={profile.capacitySettings.maxConcurrentOrders}
                        disabled={!isEditing}
                        min={1}
                        max={1000}
                      />
                      <p className="text-xs text-slate-500 mt-1">Maximum orders you can handle at once</p>
                    </div>

                    <div className="flex items-center justify-between p-4 bg-white rounded-lg border border-slate-200">
                      <div>
                        <p className="font-medium text-slate-900">Currently Active</p>
                        <p className="text-sm text-slate-500">{profile.capacitySettings.currentActiveOrders} orders in progress</p>
                      </div>
                      <span className="text-2xl font-bold text-indigo-600">
                        {profile.capacitySettings.currentActiveOrders}/{profile.capacitySettings.maxConcurrentOrders}
                      </span>
                    </div>
                  </div>
                </div>

                <div className="bg-slate-50 rounded-xl p-6">
                  <h3 className="font-medium text-slate-900 mb-4">Order Acceptance</h3>
                  <div className="p-4 bg-white rounded-lg border border-slate-200">
                    <div className="flex items-center justify-between mb-4">
                      <div>
                        <p className="font-medium text-slate-900">Accepting New Orders</p>
                        <p className="text-sm text-slate-500">Toggle to pause new order assignments</p>
                      </div>
                      <button
                        onClick={() => isEditing && setProfile({
                          ...profile,
                          capacitySettings: {
                            ...profile.capacitySettings,
                            isAcceptingOrders: !profile.capacitySettings.isAcceptingOrders
                          }
                        })}
                        disabled={!isEditing}
                        className={`relative w-14 h-8 rounded-full transition-colors ${
                          profile.capacitySettings.isAcceptingOrders ? 'bg-green-500' : 'bg-slate-300'
                        } ${!isEditing ? 'opacity-50 cursor-not-allowed' : 'cursor-pointer'}`}
                      >
                        <span className={`absolute top-1 w-6 h-6 bg-white rounded-full shadow transition-transform ${
                          profile.capacitySettings.isAcceptingOrders ? 'right-1' : 'left-1'
                        }`} />
                      </button>
                    </div>
                    {!profile.capacitySettings.isAcceptingOrders && (
                      <div className="p-3 bg-amber-50 rounded-lg text-sm text-amber-700">
                        You won't receive new order assignments while this is off.
                      </div>
                    )}
                  </div>

                  <div className="mt-4 p-4 bg-indigo-50 rounded-lg">
                    <p className="text-sm text-indigo-900 font-medium">Commission Rate</p>
                    <p className="text-3xl font-bold text-indigo-600">{profile.commissionRate}%</p>
                    <p className="text-xs text-indigo-700 mt-1">Set by platform admin</p>
                  </div>
                </div>
              </div>
            </div>
          )}
        </div>
      </main>
    </div>
  );
}
