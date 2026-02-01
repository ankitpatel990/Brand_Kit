'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { motion, AnimatePresence } from 'framer-motion';
import { useAuth } from '@/lib/auth-context';
import {
  cartApi,
  addressApi,
  checkoutApi,
  orderApi,
  Cart,
  Address,
  DeliveryOptionResponse,
  formatPrice,
} from '@/lib/order-api';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Select } from '@/components/ui/Select';

type CheckoutStep = 'address' | 'delivery' | 'review';

export default function CheckoutPage() {
  const router = useRouter();
  const { user, isLoading: authLoading } = useAuth();
  
  const [currentStep, setCurrentStep] = useState<CheckoutStep>('address');
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  
  // Data
  const [cart, setCart] = useState<Cart | null>(null);
  const [addresses, setAddresses] = useState<Address[]>([]);
  const [deliveryOptions, setDeliveryOptions] = useState<DeliveryOptionResponse[]>([]);
  
  // Selections
  const [selectedAddressId, setSelectedAddressId] = useState<string | null>(null);
  const [selectedDeliveryOption, setSelectedDeliveryOption] = useState<'STANDARD' | 'EXPRESS'>('STANDARD');
  const [termsAccepted, setTermsAccepted] = useState(false);
  const [notes, setNotes] = useState('');
  
  // New address form
  const [showNewAddressForm, setShowNewAddressForm] = useState(false);
  const [newAddress, setNewAddress] = useState({
    fullName: '',
    phone: '',
    addressLine1: '',
    addressLine2: '',
    city: '',
    state: '',
    pinCode: '',
    addressType: 'OFFICE' as const,
    isDefault: false,
  });

  useEffect(() => {
    if (!authLoading && !user) {
      router.push('/auth/login?redirect=/checkout');
      return;
    }

    if (user) {
      fetchInitialData();
    }
  }, [user, authLoading, router]);

  const fetchInitialData = async () => {
    try {
      setIsLoading(true);
      const [cartData, addressData] = await Promise.all([
        cartApi.getCart(),
        addressApi.getAddresses(),
      ]);
      
      if (!cartData || cartData.items.length === 0) {
        router.push('/cart');
        return;
      }
      
      setCart(cartData);
      setAddresses(addressData);
      
      // Select default address
      const defaultAddr = addressData.find(a => a.isDefault);
      if (defaultAddr) {
        setSelectedAddressId(defaultAddr.id);
      }
    } catch (err) {
      console.error(err);
      setError('Failed to load checkout data');
    } finally {
      setIsLoading(false);
    }
  };

  const fetchDeliveryOptions = async (pinCode: string) => {
    try {
      const options = await checkoutApi.getDeliveryOptions(pinCode);
      setDeliveryOptions(options);
    } catch (err) {
      console.error(err);
    }
  };

  const handleAddressSelect = async (addressId: string) => {
    setSelectedAddressId(addressId);
    const address = addresses.find(a => a.id === addressId);
    if (address) {
      await fetchDeliveryOptions(address.pinCode);
    }
  };

  const handleNewAddressSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setIsSubmitting(true);
      const created = await addressApi.createAddress(newAddress);
      setAddresses([created, ...addresses]);
      setSelectedAddressId(created.id);
      setShowNewAddressForm(false);
      await fetchDeliveryOptions(created.pinCode);
    } catch (err) {
      console.error(err);
      setError('Failed to create address');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handlePinCodeChange = async (pinCode: string) => {
    setNewAddress({ ...newAddress, pinCode });
    if (pinCode.length === 6) {
      try {
        const result = await addressApi.checkPinCode(pinCode);
        if (!result.serviceable) {
          setError('Delivery not available to this PIN code');
        } else {
          setError(null);
        }
      } catch {
        // Ignore
      }
    }
  };

  const goToStep = (step: CheckoutStep) => {
    if (step === 'delivery' && !selectedAddressId) {
      setError('Please select a delivery address');
      return;
    }
    if (step === 'review' && !selectedDeliveryOption) {
      setError('Please select a delivery option');
      return;
    }
    setError(null);
    setCurrentStep(step);
  };

  const handlePlaceOrder = async () => {
    if (!termsAccepted) {
      setError('Please accept Terms & Conditions');
      return;
    }

    if (!selectedAddressId) {
      setError('Please select a delivery address');
      return;
    }

    try {
      setIsSubmitting(true);
      setError(null);

      const order = await orderApi.createOrder({
        deliveryAddressId: selectedAddressId,
        deliveryOption: selectedDeliveryOption,
        termsAccepted: true,
        notes: notes || undefined,
      });

      // Redirect to payment or order confirmation
      router.push(`/orders/${order.id}?new=true`);
    } catch (err: any) {
      console.error(err);
      setError(err.response?.data?.message || 'Failed to place order');
    } finally {
      setIsSubmitting(false);
    }
  };

  if (authLoading || isLoading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-slate-50 via-white to-slate-100 flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-4 border-indigo-600 border-t-transparent"></div>
      </div>
    );
  }

  const selectedAddress = addresses.find(a => a.id === selectedAddressId);
  const selectedDelivery = deliveryOptions.find(o => o.option === selectedDeliveryOption);

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-white to-slate-100 py-8">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Stepper */}
        <div className="mb-8">
          <div className="flex items-center justify-center">
            {(['address', 'delivery', 'review'] as const).map((step, index) => (
              <div key={step} className="flex items-center">
                <button
                  onClick={() => goToStep(step)}
                  className={`flex items-center justify-center w-10 h-10 rounded-full font-semibold transition-colors ${
                    currentStep === step
                      ? 'bg-indigo-600 text-white'
                      : index < ['address', 'delivery', 'review'].indexOf(currentStep)
                      ? 'bg-green-500 text-white'
                      : 'bg-slate-200 text-slate-600'
                  }`}
                >
                  {index < ['address', 'delivery', 'review'].indexOf(currentStep) ? (
                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                    </svg>
                  ) : (
                    index + 1
                  )}
                </button>
                <span className={`ml-2 mr-8 text-sm font-medium ${
                  currentStep === step ? 'text-indigo-600' : 'text-slate-600'
                }`}>
                  {step === 'address' && 'Address'}
                  {step === 'delivery' && 'Delivery'}
                  {step === 'review' && 'Review'}
                </span>
                {index < 2 && (
                  <div className={`w-16 h-1 mr-4 rounded ${
                    index < ['address', 'delivery', 'review'].indexOf(currentStep)
                      ? 'bg-green-500'
                      : 'bg-slate-200'
                  }`} />
                )}
              </div>
            ))}
          </div>
        </div>

        {error && (
          <motion.div
            initial={{ opacity: 0, y: -10 }}
            animate={{ opacity: 1, y: 0 }}
            className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg text-red-700"
          >
            {error}
          </motion.div>
        )}

        <AnimatePresence mode="wait">
          {/* Step 1: Address */}
          {currentStep === 'address' && (
            <motion.div
              key="address"
              initial={{ opacity: 0, x: 20 }}
              animate={{ opacity: 1, x: 0 }}
              exit={{ opacity: 0, x: -20 }}
              className="bg-white rounded-2xl shadow-lg border border-slate-100 p-6"
            >
              <h2 className="text-xl font-bold text-slate-900 mb-6">Delivery Address</h2>
              
              {addresses.length > 0 && !showNewAddressForm && (
                <div className="space-y-4 mb-6">
                  {addresses.map((address) => (
                    <label
                      key={address.id}
                      className={`block p-4 border-2 rounded-xl cursor-pointer transition-colors ${
                        selectedAddressId === address.id
                          ? 'border-indigo-600 bg-indigo-50'
                          : 'border-slate-200 hover:border-slate-300'
                      }`}
                    >
                      <div className="flex items-start">
                        <input
                          type="radio"
                          name="address"
                          value={address.id}
                          checked={selectedAddressId === address.id}
                          onChange={() => handleAddressSelect(address.id)}
                          className="mt-1 mr-3"
                        />
                        <div className="flex-1">
                          <div className="flex items-center gap-2">
                            <span className="font-semibold text-slate-900">{address.fullName}</span>
                            <span className="text-xs px-2 py-0.5 bg-slate-100 text-slate-600 rounded">
                              {address.addressType}
                            </span>
                            {address.isDefault && (
                              <span className="text-xs px-2 py-0.5 bg-indigo-100 text-indigo-600 rounded">
                                Default
                              </span>
                            )}
                          </div>
                          <p className="text-slate-600 text-sm mt-1">{address.phone}</p>
                          <p className="text-slate-600 text-sm">{address.formattedAddress}</p>
                        </div>
                      </div>
                    </label>
                  ))}
                </div>
              )}

              {!showNewAddressForm ? (
                <button
                  onClick={() => setShowNewAddressForm(true)}
                  className="text-indigo-600 hover:text-indigo-700 font-medium text-sm"
                >
                  + Add New Address
                </button>
              ) : (
                <form onSubmit={handleNewAddressSubmit} className="space-y-4">
                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                    <Input
                      label="Full Name"
                      value={newAddress.fullName}
                      onChange={(e) => setNewAddress({ ...newAddress, fullName: e.target.value })}
                      required
                    />
                    <Input
                      label="Phone Number"
                      value={newAddress.phone}
                      onChange={(e) => setNewAddress({ ...newAddress, phone: e.target.value })}
                      placeholder="+91XXXXXXXXXX"
                      required
                    />
                  </div>
                  <Input
                    label="Address Line 1"
                    value={newAddress.addressLine1}
                    onChange={(e) => setNewAddress({ ...newAddress, addressLine1: e.target.value })}
                    required
                  />
                  <Input
                    label="Address Line 2 (Optional)"
                    value={newAddress.addressLine2}
                    onChange={(e) => setNewAddress({ ...newAddress, addressLine2: e.target.value })}
                  />
                  <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                    <Input
                      label="PIN Code"
                      value={newAddress.pinCode}
                      onChange={(e) => handlePinCodeChange(e.target.value)}
                      maxLength={6}
                      required
                    />
                    <Input
                      label="City"
                      value={newAddress.city}
                      onChange={(e) => setNewAddress({ ...newAddress, city: e.target.value })}
                      required
                    />
                    <Input
                      label="State"
                      value={newAddress.state}
                      onChange={(e) => setNewAddress({ ...newAddress, state: e.target.value })}
                      required
                    />
                  </div>
                  <Select
                    label="Address Type"
                    value={newAddress.addressType}
                    onChange={(e) => setNewAddress({ ...newAddress, addressType: e.target.value as any })}
                    options={[
                      { value: 'HOME', label: 'Home' },
                      { value: 'OFFICE', label: 'Office' },
                      { value: 'OTHER', label: 'Other' },
                    ]}
                  />
                  <div className="flex gap-4">
                    <Button type="submit" disabled={isSubmitting}>
                      {isSubmitting ? 'Saving...' : 'Save Address'}
                    </Button>
                    <Button
                      type="button"
                      variant="secondary"
                      onClick={() => setShowNewAddressForm(false)}
                    >
                      Cancel
                    </Button>
                  </div>
                </form>
              )}

              <div className="mt-8 flex justify-end">
                <Button
                  onClick={() => goToStep('delivery')}
                  disabled={!selectedAddressId}
                >
                  Continue to Delivery
                </Button>
              </div>
            </motion.div>
          )}

          {/* Step 2: Delivery Options */}
          {currentStep === 'delivery' && (
            <motion.div
              key="delivery"
              initial={{ opacity: 0, x: 20 }}
              animate={{ opacity: 1, x: 0 }}
              exit={{ opacity: 0, x: -20 }}
              className="bg-white rounded-2xl shadow-lg border border-slate-100 p-6"
            >
              <h2 className="text-xl font-bold text-slate-900 mb-6">Delivery Options</h2>
              
              <div className="space-y-4">
                {deliveryOptions.map((option) => (
                  <label
                    key={option.option}
                    className={`block p-4 border-2 rounded-xl cursor-pointer transition-colors ${
                      selectedDeliveryOption === option.option
                        ? 'border-indigo-600 bg-indigo-50'
                        : option.isAvailable
                        ? 'border-slate-200 hover:border-slate-300'
                        : 'border-slate-200 bg-slate-50 opacity-60 cursor-not-allowed'
                    }`}
                  >
                    <div className="flex items-start">
                      <input
                        type="radio"
                        name="delivery"
                        value={option.option}
                        checked={selectedDeliveryOption === option.option}
                        onChange={() => setSelectedDeliveryOption(option.option)}
                        disabled={!option.isAvailable}
                        className="mt-1 mr-3"
                      />
                      <div className="flex-1">
                        <div className="flex items-center justify-between">
                          <span className="font-semibold text-slate-900">{option.displayName}</span>
                          <span className="font-bold text-slate-900">
                            {option.isFree ? (
                              <span className="text-green-600">Free</span>
                            ) : (
                              formatPrice(option.charge)
                            )}
                          </span>
                        </div>
                        <p className="text-slate-600 text-sm mt-1">
                          Estimated delivery: {option.deliveryTimeRange}
                        </p>
                        <p className="text-slate-500 text-sm">
                          Expected by: {new Date(option.estimatedDeliveryStart).toLocaleDateString()} - {new Date(option.estimatedDeliveryEnd).toLocaleDateString()}
                        </p>
                        {!option.isAvailable && option.unavailableReason && (
                          <p className="text-red-500 text-sm mt-1">{option.unavailableReason}</p>
                        )}
                      </div>
                    </div>
                  </label>
                ))}
              </div>

              <div className="mt-8 flex justify-between">
                <Button variant="secondary" onClick={() => goToStep('address')}>
                  Back
                </Button>
                <Button onClick={() => goToStep('review')}>
                  Continue to Review
                </Button>
              </div>
            </motion.div>
          )}

          {/* Step 3: Review */}
          {currentStep === 'review' && cart && (
            <motion.div
              key="review"
              initial={{ opacity: 0, x: 20 }}
              animate={{ opacity: 1, x: 0 }}
              exit={{ opacity: 0, x: -20 }}
              className="space-y-6"
            >
              {/* Order Items */}
              <div className="bg-white rounded-2xl shadow-lg border border-slate-100 p-6">
                <h2 className="text-xl font-bold text-slate-900 mb-4">Order Items</h2>
                <div className="space-y-4">
                  {cart.items.map((item) => (
                    <div key={item.cartItemId} className="flex items-center gap-4 py-3 border-b border-slate-100 last:border-0">
                      <div className="w-16 h-16 bg-slate-100 rounded-lg flex-shrink-0"></div>
                      <div className="flex-1">
                        <p className="font-medium text-slate-900">{item.productName}</p>
                        <p className="text-sm text-slate-600">Qty: {item.quantity}</p>
                      </div>
                      <p className="font-semibold text-slate-900">{formatPrice(item.subtotal)}</p>
                    </div>
                  ))}
                </div>
              </div>

              {/* Delivery Address */}
              <div className="bg-white rounded-2xl shadow-lg border border-slate-100 p-6">
                <div className="flex justify-between items-start">
                  <h2 className="text-xl font-bold text-slate-900">Delivery Address</h2>
                  <button
                    onClick={() => goToStep('address')}
                    className="text-indigo-600 hover:text-indigo-700 text-sm font-medium"
                  >
                    Change
                  </button>
                </div>
                {selectedAddress && (
                  <div className="mt-4">
                    <p className="font-medium text-slate-900">{selectedAddress.fullName}</p>
                    <p className="text-slate-600">{selectedAddress.phone}</p>
                    <p className="text-slate-600">{selectedAddress.formattedAddress}</p>
                  </div>
                )}
              </div>

              {/* Delivery Option */}
              <div className="bg-white rounded-2xl shadow-lg border border-slate-100 p-6">
                <div className="flex justify-between items-start">
                  <h2 className="text-xl font-bold text-slate-900">Delivery Option</h2>
                  <button
                    onClick={() => goToStep('delivery')}
                    className="text-indigo-600 hover:text-indigo-700 text-sm font-medium"
                  >
                    Change
                  </button>
                </div>
                {selectedDelivery && (
                  <div className="mt-4">
                    <p className="font-medium text-slate-900">{selectedDelivery.displayName}</p>
                    <p className="text-slate-600">
                      Expected: {selectedDelivery.estimatedDeliveryStart} - {selectedDelivery.estimatedDeliveryEnd}
                    </p>
                  </div>
                )}
              </div>

              {/* Price Breakdown */}
              <div className="bg-white rounded-2xl shadow-lg border border-slate-100 p-6">
                <h2 className="text-xl font-bold text-slate-900 mb-4">Price Details</h2>
                <div className="space-y-3">
                  {cart.pricing.totalDiscount > 0 && (
                    <>
                      <div className="flex justify-between text-slate-600">
                        <span>Original Price</span>
                        <span className="line-through">{formatPrice(cart.pricing.originalSubtotal)}</span>
                      </div>
                      <div className="flex justify-between text-green-600">
                        <span>Discount</span>
                        <span>-{formatPrice(cart.pricing.totalDiscount)}</span>
                      </div>
                    </>
                  )}
                  <div className="flex justify-between text-slate-600">
                    <span>Subtotal</span>
                    <span>{formatPrice(cart.pricing.subtotal)}</span>
                  </div>
                  <div className="flex justify-between text-slate-600">
                    <span>GST (18%)</span>
                    <span>{formatPrice(cart.pricing.gst)}</span>
                  </div>
                  <div className="flex justify-between text-slate-600">
                    <span>Delivery</span>
                    <span>
                      {selectedDelivery?.isFree ? (
                        <span className="text-green-600">Free</span>
                      ) : (
                        formatPrice(selectedDelivery?.charge || 0)
                      )}
                    </span>
                  </div>
                  <div className="border-t border-slate-200 pt-3 flex justify-between text-lg font-bold text-slate-900">
                    <span>Total</span>
                    <span>{formatPrice(cart.pricing.total)}</span>
                  </div>
                </div>
              </div>

              {/* Notes */}
              <div className="bg-white rounded-2xl shadow-lg border border-slate-100 p-6">
                <h2 className="text-xl font-bold text-slate-900 mb-4">Order Notes (Optional)</h2>
                <textarea
                  value={notes}
                  onChange={(e) => setNotes(e.target.value)}
                  className="w-full p-3 border border-slate-200 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
                  rows={3}
                  placeholder="Any special instructions for your order..."
                />
              </div>

              {/* Terms & Place Order */}
              <div className="bg-white rounded-2xl shadow-lg border border-slate-100 p-6">
                <label className="flex items-start gap-3 cursor-pointer">
                  <input
                    type="checkbox"
                    checked={termsAccepted}
                    onChange={(e) => setTermsAccepted(e.target.checked)}
                    className="mt-1"
                  />
                  <span className="text-slate-600 text-sm">
                    I agree to the <a href="/terms" className="text-indigo-600 hover:underline">Terms & Conditions</a> and <a href="/privacy" className="text-indigo-600 hover:underline">Privacy Policy</a>
                  </span>
                </label>

                <div className="mt-6 flex justify-between">
                  <Button variant="secondary" onClick={() => goToStep('delivery')}>
                    Back
                  </Button>
                  <Button
                    onClick={handlePlaceOrder}
                    disabled={!termsAccepted || isSubmitting}
                    size="lg"
                  >
                    {isSubmitting ? 'Processing...' : 'Proceed to Payment'}
                  </Button>
                </div>
              </div>
            </motion.div>
          )}
        </AnimatePresence>
      </div>
    </div>
  );
}
