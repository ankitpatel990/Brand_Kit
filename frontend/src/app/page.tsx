'use client';

import React, { useState, useEffect } from 'react';
import Link from 'next/link';
import Image from 'next/image';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  ArrowRight, 
  Search, 
  ShoppingBag, 
  User, 
  Menu, 
  X, 
  Star,
  Truck,
  Shield,
  Palette,
  Package,
  Sparkles,
  Heart,
  Phone,
  Mail,
  MapPin,
  ChevronRight,
  Zap,
  Clock,
  Award,
  Users
} from 'lucide-react';
import { productApi, ProductSummary, Category } from '@/lib/product-api';

/**
 * Landing Page - Printo.in Inspired Design
 * Modern e-commerce landing with products from database
 */

// Animation variants
const fadeInUp = {
  initial: { opacity: 0, y: 30 },
  animate: { opacity: 1, y: 0 },
  transition: { duration: 0.6, ease: [0.22, 1, 0.36, 1] }
};

const staggerContainer = {
  animate: {
    transition: {
      staggerChildren: 0.1
    }
  }
};

// Static category data with images (fallback)
const FEATURED_CATEGORIES = [
  { name: 'T-Shirts', slug: 't-shirts', image: '/images/categories/tshirts.jpg', icon: '👕', color: 'from-rose-500 to-pink-600' },
  { name: 'Mugs', slug: 'mugs', image: '/images/categories/mugs.jpg', icon: '☕', color: 'from-amber-500 to-orange-600' },
  { name: 'Bottles', slug: 'water-bottles', image: '/images/categories/bottles.jpg', icon: '🍶', color: 'from-cyan-500 to-blue-600' },
  { name: 'Tote Bags', slug: 'bags', image: '/images/categories/bags.jpg', icon: '👜', color: 'from-emerald-500 to-teal-600' },
  { name: 'Diaries', slug: 'diaries', image: '/images/categories/diaries.jpg', icon: '📓', color: 'from-violet-500 to-purple-600' },
  { name: 'Pens', slug: 'pens', image: '/images/categories/pens.jpg', icon: '🖊️', color: 'from-slate-600 to-slate-800' },
];

const TRUST_BADGES = [
  { icon: Truck, label: '4 Hrs Express Delivery', sublabel: 'In Select Cities' },
  { icon: Shield, label: 'Quality Assured', sublabel: '100% Satisfaction' },
  { icon: Award, label: 'Since 2006', sublabel: 'Trusted Brand' },
  { icon: Users, label: '50K+ Clients', sublabel: 'Pan India' },
];

const BUSINESS_SOLUTIONS = [
  { title: 'Startup Branding', description: 'Launch your brand with custom merchandise', icon: '🚀' },
  { title: 'Event & Promotions', description: 'Stand out at trade shows & events', icon: '🎉' },
  { title: 'Employee Gifting', description: 'Reward your team with personalized gifts', icon: '🎁' },
  { title: 'Corporate Kits', description: 'Welcome kits & onboarding packages', icon: '📦' },
];

export default function HomePage() {
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const [isScrolled, setIsScrolled] = useState(false);
  const [products, setProducts] = useState<ProductSummary[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [activeCategory, setActiveCategory] = useState<string | null>(null);

  // Handle scroll effect
  useEffect(() => {
    const handleScroll = () => {
      setIsScrolled(window.scrollY > 20);
    };
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  // Fetch products and categories
  useEffect(() => {
    const fetchData = async () => {
      try {
        setIsLoading(true);
        const [productsRes, categoriesRes] = await Promise.all([
          productApi.getProducts({ limit: 12, sort: 'popular' }),
          productApi.getCategories()
        ]);
        setProducts(productsRes.data.products);
        setCategories(categoriesRes.data);
      } catch (error) {
        console.error('Failed to fetch data:', error);
        // Use empty arrays as fallback
        setProducts([]);
        setCategories([]);
      } finally {
        setIsLoading(false);
      }
    };
    fetchData();
  }, []);

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
    }).format(price);
  };

  return (
    <div className="min-h-screen bg-white">
      {/* ===== TOP BAR ===== */}
      <div className="bg-secondary-950 text-white py-2 text-sm">
        <div className="container-wide flex items-center justify-between">
          <div className="flex items-center gap-6">
            <span className="flex items-center gap-2">
              <Zap className="w-4 h-4 text-accent-400" />
              <span className="hidden sm:inline">4 Hrs Express Delivery in Bengaluru, Hyderabad, Chennai & Delhi</span>
              <span className="sm:hidden">Express Delivery Available</span>
            </span>
          </div>
          <div className="flex items-center gap-4">
            <Link href="/track-order" className="hover:text-accent-400 transition-colors hidden sm:block">
              Track Order
            </Link>
            <Link href="/help" className="hover:text-accent-400 transition-colors">
              Help
            </Link>
          </div>
        </div>
      </div>

      {/* ===== HEADER ===== */}
      <header
        className={`sticky top-0 z-50 transition-all duration-300 ${
          isScrolled
            ? 'bg-white/95 backdrop-blur-xl shadow-soft'
            : 'bg-white'
        }`}
      >
        <div className="container-wide">
          <div className="flex items-center justify-between h-16 lg:h-20">
            {/* Logo */}
            <Link href="/" className="flex items-center gap-3 group">
              <div className="relative w-10 h-10 lg:w-12 lg:h-12">
                <div className="absolute inset-0 bg-gradient-to-br from-primary-500 to-primary-600 rounded-xl rotate-6 group-hover:rotate-12 transition-transform" />
                <div className="absolute inset-0 bg-gradient-to-br from-primary-500 to-primary-600 rounded-xl flex items-center justify-center">
                  <span className="text-white font-bold text-xl lg:text-2xl font-display">B</span>
                </div>
              </div>
              <div>
                <span className="text-xl lg:text-2xl font-bold font-display text-secondary-900">
                  Brand<span className="text-primary-500">Kit</span>
                </span>
                <p className="text-[10px] text-secondary-500 -mt-1 hidden lg:block">Print On Demand</p>
              </div>
            </Link>

            {/* Desktop Navigation */}
            <nav className="hidden lg:flex items-center gap-1">
              <Link href="/products" className="px-4 py-2 text-secondary-700 hover:text-primary-600 font-medium transition-colors rounded-lg hover:bg-surface-100">
                All Products
              </Link>
              {FEATURED_CATEGORIES.slice(0, 5).map((cat) => (
                <Link
                  key={cat.slug}
                  href={`/products?category=${cat.slug}`}
                  className="px-4 py-2 text-secondary-700 hover:text-primary-600 font-medium transition-colors rounded-lg hover:bg-surface-100"
                >
                  {cat.name}
                </Link>
              ))}
            </nav>

            {/* Search & Actions */}
            <div className="flex items-center gap-2 lg:gap-4">
              {/* Search */}
              <button className="p-2.5 text-secondary-600 hover:text-secondary-900 hover:bg-surface-100 rounded-xl transition-colors">
                <Search className="w-5 h-5" />
              </button>

              {/* Cart */}
              <Link href="/cart" className="relative p-2.5 text-secondary-600 hover:text-secondary-900 hover:bg-surface-100 rounded-xl transition-colors">
                <ShoppingBag className="w-5 h-5" />
                <span className="absolute -top-0.5 -right-0.5 w-5 h-5 bg-primary-500 text-white text-xs font-bold rounded-full flex items-center justify-center">
                  0
                </span>
              </Link>

              {/* Auth */}
              <div className="hidden sm:flex items-center gap-2">
                <Link href="/auth/login" className="btn-ghost text-sm">
                  Sign In
                </Link>
                <Link href="/auth/register" className="btn-primary text-sm !px-4 !py-2">
                  Get Started
                </Link>
              </div>

              {/* Mobile Menu */}
              <button
                onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
                className="lg:hidden p-2.5 text-secondary-600 hover:text-secondary-900 hover:bg-surface-100 rounded-xl transition-colors"
              >
                {isMobileMenuOpen ? <X className="w-5 h-5" /> : <Menu className="w-5 h-5" />}
              </button>
            </div>
          </div>
        </div>

        {/* Mobile Menu */}
        <AnimatePresence>
          {isMobileMenuOpen && (
            <motion.div
              initial={{ height: 0, opacity: 0 }}
              animate={{ height: 'auto', opacity: 1 }}
              exit={{ height: 0, opacity: 0 }}
              className="lg:hidden border-t border-surface-200 bg-white overflow-hidden"
            >
              <nav className="container-wide py-4 space-y-2">
                <Link href="/products" className="block px-4 py-3 rounded-xl text-secondary-700 hover:bg-surface-100 font-medium">
                  All Products
                </Link>
                {FEATURED_CATEGORIES.map((cat) => (
                  <Link
                    key={cat.slug}
                    href={`/products?category=${cat.slug}`}
                    className="block px-4 py-3 rounded-xl text-secondary-700 hover:bg-surface-100"
                  >
                    <span className="mr-2">{cat.icon}</span>
                    {cat.name}
                  </Link>
                ))}
                <div className="pt-4 border-t border-surface-200 flex gap-2">
                  <Link href="/auth/login" className="flex-1 btn-secondary text-center">
                    Sign In
                  </Link>
                  <Link href="/auth/register" className="flex-1 btn-primary text-center">
                    Get Started
                  </Link>
                </div>
              </nav>
            </motion.div>
          )}
        </AnimatePresence>
      </header>

      {/* ===== HERO SECTION ===== */}
      <section className="relative overflow-hidden hero-gradient">
        {/* Decorative Elements */}
        <div className="absolute inset-0 overflow-hidden pointer-events-none">
          <div className="absolute -top-40 -right-40 w-96 h-96 bg-primary-500/10 rounded-full blur-3xl" />
          <div className="absolute top-1/2 -left-20 w-72 h-72 bg-accent-500/10 rounded-full blur-3xl" />
          <div className="absolute bottom-0 right-1/4 w-64 h-64 bg-blue-500/5 rounded-full blur-3xl" />
        </div>

        <div className="container-wide relative">
          <div className="py-12 lg:py-20">
            <div className="grid lg:grid-cols-2 gap-12 items-center">
              {/* Hero Content */}
              <motion.div
                initial={{ opacity: 0, y: 30 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.7, ease: [0.22, 1, 0.36, 1] }}
              >
                <span className="inline-flex items-center gap-2 px-4 py-2 bg-primary-50 text-primary-600 rounded-full text-sm font-semibold mb-6">
                  <Sparkles className="w-4 h-4" />
                  Your Design, Your Way
                </span>
                
                <h1 className="text-4xl sm:text-5xl lg:text-6xl font-bold font-display text-secondary-950 leading-tight mb-6">
                  Create{' '}
                  <span className="text-gradient">Beautiful</span>
                  <br />
                  Custom Merchandise
                </h1>
                
                <p className="text-lg text-secondary-600 mb-8 max-w-xl">
                  From T-shirts to mugs, bottles to corporate gifts — design, preview, and order 
                  personalized products that make your brand unforgettable.
                </p>

                <div className="flex flex-col sm:flex-row gap-4 mb-10">
                  <Link href="/products" className="btn-primary text-base !px-8 !py-4">
                    Explore Products
                    <ArrowRight className="w-5 h-5" />
                  </Link>
                  <Link href="/auth/register" className="btn-secondary text-base !px-8 !py-4">
                    Start Designing
                  </Link>
                </div>

                {/* Trust Indicators */}
                <div className="flex flex-wrap items-center gap-6">
                  <div className="flex items-center gap-2">
                    <div className="flex -space-x-2">
                      {[1, 2, 3, 4].map((i) => (
                        <div key={i} className="w-8 h-8 rounded-full bg-gradient-to-br from-secondary-200 to-secondary-300 border-2 border-white" />
                      ))}
                    </div>
                    <div className="text-sm">
                      <span className="font-bold text-secondary-900">50,000+</span>
                      <span className="text-secondary-500 ml-1">Happy Clients</span>
                    </div>
                  </div>
                  <div className="h-8 w-px bg-secondary-200 hidden sm:block" />
                  <div className="flex items-center gap-1">
                    {[1, 2, 3, 4, 5].map((i) => (
                      <Star key={i} className="w-5 h-5 text-accent-500 fill-accent-500" />
                    ))}
                    <span className="text-sm font-bold text-secondary-900 ml-2">4.9/5</span>
                  </div>
                </div>
              </motion.div>

              {/* Hero Visual */}
              <motion.div
                initial={{ opacity: 0, scale: 0.95 }}
                animate={{ opacity: 1, scale: 1 }}
                transition={{ duration: 0.7, delay: 0.2, ease: [0.22, 1, 0.36, 1] }}
                className="relative"
              >
                <div className="relative z-10">
                  {/* Main Product Showcase */}
                  <div className="relative bg-gradient-to-br from-surface-100 to-surface-200 rounded-3xl p-8 lg:p-12 shadow-hero">
                    {/* Product Grid Preview */}
                    <div className="grid grid-cols-2 gap-4">
                      {['👕 T-Shirts', '☕ Mugs', '🍶 Bottles', '👜 Bags'].map((item, i) => (
                        <div 
                          key={i}
                          className="bg-white rounded-2xl p-6 shadow-card flex flex-col items-center justify-center aspect-square hover:shadow-card-hover hover:-translate-y-1 transition-all cursor-pointer"
                        >
                          <span className="text-4xl mb-2">{item.split(' ')[0]}</span>
                          <span className="text-sm font-medium text-secondary-700">{item.split(' ')[1]}</span>
                        </div>
                      ))}
                    </div>

                    {/* Floating Badge */}
                    <div className="absolute -top-4 -right-4 bg-white rounded-2xl shadow-card p-4 animate-bounce-gentle">
                      <div className="flex items-center gap-2">
                        <div className="w-10 h-10 bg-emerald-100 rounded-xl flex items-center justify-center">
                          <Truck className="w-5 h-5 text-emerald-600" />
                        </div>
                        <div className="text-sm">
                          <p className="font-bold text-secondary-900">Free Shipping</p>
                          <p className="text-secondary-500">Orders above ₹999</p>
                        </div>
                      </div>
                    </div>

                    {/* Floating Badge 2 */}
                    <div className="absolute -bottom-4 -left-4 bg-white rounded-2xl shadow-card p-4 animate-float">
                      <div className="flex items-center gap-2">
                        <div className="w-10 h-10 bg-primary-100 rounded-xl flex items-center justify-center">
                          <Clock className="w-5 h-5 text-primary-600" />
                        </div>
                        <div className="text-sm">
                          <p className="font-bold text-secondary-900">4 Hr Delivery</p>
                          <p className="text-secondary-500">In Metro Cities</p>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </motion.div>
            </div>
          </div>
        </div>
      </section>

      {/* ===== TRUST BADGES ===== */}
      <section className="border-y border-surface-200 bg-surface-50">
        <div className="container-wide py-6">
          <div className="grid grid-cols-2 lg:grid-cols-4 gap-6">
            {TRUST_BADGES.map((badge, i) => (
              <motion.div
                key={i}
                initial={{ opacity: 0, y: 20 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true }}
                transition={{ delay: i * 0.1 }}
                className="flex items-center gap-4"
              >
                <div className="w-12 h-12 bg-white rounded-xl shadow-soft flex items-center justify-center flex-shrink-0">
                  <badge.icon className="w-6 h-6 text-primary-500" />
                </div>
                <div>
                  <p className="font-bold text-secondary-900">{badge.label}</p>
                  <p className="text-sm text-secondary-500">{badge.sublabel}</p>
                </div>
              </motion.div>
            ))}
          </div>
        </div>
      </section>

      {/* ===== FEATURED CATEGORIES ===== */}
      <section className="section bg-white">
        <div className="container-wide">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            className="text-center mb-12"
          >
            <h2 className="text-3xl lg:text-4xl font-bold font-display text-secondary-950 mb-4">
              Shop By Category
            </h2>
            <p className="text-secondary-600 max-w-2xl mx-auto">
              From everyday essentials to premium corporate gifts — find the perfect canvas for your brand
            </p>
          </motion.div>

          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4 lg:gap-6">
            {FEATURED_CATEGORIES.map((cat, i) => (
              <motion.div
                key={cat.slug}
                initial={{ opacity: 0, y: 20 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true }}
                transition={{ delay: i * 0.1 }}
              >
                <Link
                  href={`/products?category=${cat.slug}`}
                  className="block group"
                >
                  <div className={`relative aspect-square rounded-2xl bg-gradient-to-br ${cat.color} p-1 overflow-hidden`}>
                    <div className="absolute inset-1 bg-white rounded-xl flex flex-col items-center justify-center transition-all group-hover:bg-opacity-95">
                      <span className="text-5xl mb-3 group-hover:scale-110 transition-transform">{cat.icon}</span>
                      <span className="font-semibold text-secondary-800 group-hover:text-primary-600 transition-colors">{cat.name}</span>
                    </div>
                  </div>
                </Link>
              </motion.div>
            ))}
          </div>
        </div>
      </section>

      {/* ===== PRODUCTS FROM DATABASE ===== */}
      <section className="section bg-surface-50">
        <div className="container-wide">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            className="flex flex-col sm:flex-row sm:items-end sm:justify-between gap-4 mb-10"
          >
            <div>
              <h2 className="text-3xl lg:text-4xl font-bold font-display text-secondary-950 mb-2">
                Popular Products
              </h2>
              <p className="text-secondary-600">
                Best sellers loved by thousands of businesses
              </p>
            </div>
            <Link href="/products" className="btn-ghost text-primary-600 hover:text-primary-700 !px-0">
              View All Products
              <ChevronRight className="w-5 h-5" />
            </Link>
          </motion.div>

          {isLoading ? (
            // Loading Skeleton
            <div className="product-grid">
              {[...Array(8)].map((_, i) => (
                <div key={i} className="bg-white rounded-2xl overflow-hidden shadow-card">
                  <div className="aspect-square shimmer" />
                  <div className="p-4 space-y-3">
                    <div className="h-4 shimmer rounded w-1/3" />
                    <div className="h-5 shimmer rounded w-full" />
                    <div className="h-6 shimmer rounded w-1/2" />
                  </div>
                </div>
              ))}
            </div>
          ) : products.length > 0 ? (
            <div className="product-grid">
              {products.map((product, i) => (
                <motion.div
                  key={product.productId}
                  initial={{ opacity: 0, y: 20 }}
                  whileInView={{ opacity: 1, y: 0 }}
                  viewport={{ once: true }}
                  transition={{ delay: i * 0.05 }}
                >
                  <Link href={`/products/${product.slug}`} className="block group">
                    <div className="bg-white rounded-2xl overflow-hidden shadow-card card-hover">
                      {/* Image */}
                      <div className="relative aspect-square bg-surface-100 overflow-hidden">
                        {product.imageUrl ? (
                          <Image
                            src={product.imageUrl}
                            alt={product.name}
                            fill
                            className="object-cover group-hover:scale-105 transition-transform duration-500"
                          />
                        ) : (
                          <div className="absolute inset-0 flex items-center justify-center bg-gradient-to-br from-surface-100 to-surface-200">
                            <Package className="w-12 h-12 text-secondary-300" />
                          </div>
                        )}

                        {/* Badges */}
                        <div className="absolute top-3 left-3 flex flex-col gap-2">
                          {product.hasDiscount && (
                            <span className="badge bg-primary-500 text-white">
                              -{product.discountPercentage}%
                            </span>
                          )}
                          {product.ecoFriendly && (
                            <span className="badge bg-emerald-500 text-white">
                              🌿 Eco
                            </span>
                          )}
                        </div>

                        {/* Quick Actions */}
                        <div className="absolute top-3 right-3 opacity-0 group-hover:opacity-100 transition-opacity">
                          <button className="w-10 h-10 bg-white rounded-xl shadow-lg flex items-center justify-center hover:bg-primary-50 transition-colors">
                            <Heart className="w-5 h-5 text-secondary-600" />
                          </button>
                        </div>
                      </div>

                      {/* Content */}
                      <div className="p-4">
                        <span className="text-xs font-semibold text-primary-600 uppercase tracking-wide">
                          {product.category}
                        </span>
                        <h3 className="mt-1 font-semibold text-secondary-900 line-clamp-2 group-hover:text-primary-600 transition-colors">
                          {product.name}
                        </h3>
                        
                        {/* Rating */}
                        <div className="flex items-center gap-1 mt-2">
                          <Star className="w-4 h-4 text-accent-500 fill-accent-500" />
                          <span className="text-sm font-medium text-secondary-800">{product.aggregateRating.toFixed(1)}</span>
                          <span className="text-sm text-secondary-400">({product.totalReviews})</span>
                        </div>

                        {/* Price */}
                        <div className="mt-3 flex items-baseline gap-2">
                          {product.hasDiscount ? (
                            <>
                              <span className="text-lg font-bold text-secondary-900">
                                {formatPrice(product.discountedPrice)}
                              </span>
                              <span className="text-sm text-secondary-400 line-through">
                                {formatPrice(product.basePrice)}
                              </span>
                            </>
                          ) : (
                            <span className="text-lg font-bold text-secondary-900">
                              From {formatPrice(product.basePrice)}
                            </span>
                          )}
                        </div>

                        <p className="mt-2 text-xs text-secondary-500">
                          Delivery in {product.leadTimeDays} days
                        </p>
                      </div>
                    </div>
                  </Link>
                </motion.div>
              ))}
            </div>
          ) : (
            // Empty state
            <div className="text-center py-16">
              <Package className="w-16 h-16 text-secondary-300 mx-auto mb-4" />
              <h3 className="text-xl font-semibold text-secondary-700 mb-2">No products available</h3>
              <p className="text-secondary-500 mb-6">Check back soon for new arrivals!</p>
              <Link href="/products" className="btn-primary">
                Browse All Products
              </Link>
            </div>
          )}
        </div>
      </section>

      {/* ===== BUSINESS SOLUTIONS ===== */}
      <section className="section bg-white">
        <div className="container-wide">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            className="text-center mb-12"
          >
            <h2 className="text-3xl lg:text-4xl font-bold font-display text-secondary-950 mb-4">
              Solutions For Every Business
            </h2>
            <p className="text-secondary-600 max-w-2xl mx-auto">
              Whether you're a startup or enterprise, we have the perfect merchandise solutions for your needs
            </p>
          </motion.div>

          <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-6">
            {BUSINESS_SOLUTIONS.map((solution, i) => (
              <motion.div
                key={i}
                initial={{ opacity: 0, y: 20 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true }}
                transition={{ delay: i * 0.1 }}
                className="group"
              >
                <div className="bg-surface-50 rounded-2xl p-6 h-full border-2 border-transparent hover:border-primary-200 hover:bg-white transition-all cursor-pointer">
                  <span className="text-4xl block mb-4">{solution.icon}</span>
                  <h3 className="text-xl font-bold text-secondary-900 mb-2 group-hover:text-primary-600 transition-colors">
                    {solution.title}
                  </h3>
                  <p className="text-secondary-600">{solution.description}</p>
                </div>
              </motion.div>
            ))}
          </div>
        </div>
      </section>

      {/* ===== PROMO BANNER ===== */}
      <section className="section bg-surface-50">
        <div className="container-wide">
          <motion.div
            initial={{ opacity: 0, scale: 0.98 }}
            whileInView={{ opacity: 1, scale: 1 }}
            viewport={{ once: true }}
            className="promo-banner p-8 lg:p-16 text-center text-white"
          >
            <h2 className="text-3xl lg:text-5xl font-bold font-display mb-4">
              Ready to Build Your Brand?
            </h2>
            <p className="text-lg lg:text-xl text-white/90 mb-8 max-w-2xl mx-auto">
              Join thousands of businesses creating memorable branded experiences with BrandKit
            </p>
            <div className="flex flex-col sm:flex-row gap-4 justify-center">
              <Link href="/auth/register" className="btn-secondary !bg-white !text-primary-600 hover:!bg-white/90">
                Create Free Account
                <ArrowRight className="w-5 h-5" />
              </Link>
              <Link href="/products" className="btn-ghost !text-white hover:!bg-white/10">
                Browse Products
              </Link>
            </div>
          </motion.div>
        </div>
      </section>

      {/* ===== WHY CHOOSE US ===== */}
      <section className="section bg-white">
        <div className="container-wide">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            className="text-center mb-12"
          >
            <h2 className="text-3xl lg:text-4xl font-bold font-display text-secondary-950 mb-4">
              Why Choose BrandKit?
            </h2>
            <p className="text-secondary-600 max-w-2xl mx-auto">
              We combine cutting-edge technology with premium quality to deliver the best custom merchandise experience
            </p>
          </motion.div>

          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8">
            {[
              {
                icon: Palette,
                title: 'Real-time Preview',
                description: 'See your design on products instantly with our advanced 360° preview technology',
              },
              {
                icon: Package,
                title: 'Bundle Builder',
                description: 'Create custom welcome kits combining multiple products in one order',
              },
              {
                icon: Truck,
                title: 'Express Delivery',
                description: '4-hour delivery in metro cities, pan-India shipping within 5-7 days',
              },
              {
                icon: Shield,
                title: 'Quality Assured',
                description: 'Every product undergoes rigorous quality checks before shipping',
              },
              {
                icon: Zap,
                title: 'Easy Ordering',
                description: 'Simple 3-step process: Upload, Preview, Order. No design skills needed',
              },
              {
                icon: Award,
                title: 'Trusted by 50K+',
                description: 'Businesses across India trust us for their branding needs since 2006',
              },
            ].map((feature, i) => (
              <motion.div
                key={i}
                initial={{ opacity: 0, y: 20 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true }}
                transition={{ delay: i * 0.1 }}
                className="flex gap-4"
              >
                <div className="w-14 h-14 bg-primary-50 rounded-2xl flex items-center justify-center flex-shrink-0">
                  <feature.icon className="w-7 h-7 text-primary-500" />
                </div>
                <div>
                  <h3 className="text-lg font-bold text-secondary-900 mb-2">{feature.title}</h3>
                  <p className="text-secondary-600">{feature.description}</p>
                </div>
              </motion.div>
            ))}
          </div>
        </div>
      </section>

      {/* ===== FOOTER ===== */}
      <footer className="bg-secondary-950 text-white">
        {/* Main Footer */}
        <div className="container-wide py-16">
          <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-10">
            {/* Brand */}
            <div>
              <Link href="/" className="flex items-center gap-3 mb-6">
                <div className="w-10 h-10 bg-gradient-to-br from-primary-500 to-primary-600 rounded-xl flex items-center justify-center">
                  <span className="text-white font-bold text-xl font-display">B</span>
                </div>
                <span className="text-xl font-bold font-display">
                  Brand<span className="text-primary-400">Kit</span>
                </span>
              </Link>
              <p className="text-secondary-400 mb-6">
                India's leading print-on-demand platform. Creating custom branded merchandise since 2006.
              </p>
              <div className="flex gap-4">
                {['facebook', 'twitter', 'instagram', 'linkedin'].map((social) => (
                  <a key={social} href="#" className="w-10 h-10 bg-secondary-800 rounded-xl flex items-center justify-center hover:bg-secondary-700 transition-colors">
                    <span className="sr-only">{social}</span>
                    <div className="w-5 h-5 bg-secondary-400 rounded" />
                  </a>
                ))}
              </div>
            </div>

            {/* Products */}
            <div>
              <h4 className="font-bold text-lg mb-6">Products</h4>
              <ul className="space-y-3">
                {FEATURED_CATEGORIES.map((cat) => (
                  <li key={cat.slug}>
                    <Link href={`/products?category=${cat.slug}`} className="text-secondary-400 hover:text-white transition-colors">
                      {cat.name}
                    </Link>
                  </li>
                ))}
              </ul>
            </div>

            {/* Company */}
            <div>
              <h4 className="font-bold text-lg mb-6">Company</h4>
              <ul className="space-y-3">
                <li><Link href="/about" className="text-secondary-400 hover:text-white transition-colors">About Us</Link></li>
                <li><Link href="/blog" className="text-secondary-400 hover:text-white transition-colors">Blog</Link></li>
                <li><Link href="/careers" className="text-secondary-400 hover:text-white transition-colors">Careers</Link></li>
                <li><Link href="/contact" className="text-secondary-400 hover:text-white transition-colors">Contact</Link></li>
                <li><Link href="/partner" className="text-secondary-400 hover:text-white transition-colors">Become a Partner</Link></li>
              </ul>
            </div>

            {/* Contact */}
            <div>
              <h4 className="font-bold text-lg mb-6">Contact Us</h4>
              <ul className="space-y-4">
                <li className="flex items-start gap-3">
                  <Phone className="w-5 h-5 text-primary-400 mt-0.5" />
                  <div>
                    <p className="text-white">+91 80-1234-5678</p>
                    <p className="text-sm text-secondary-400">Mon-Sat, 9am-6pm</p>
                  </div>
                </li>
                <li className="flex items-start gap-3">
                  <Mail className="w-5 h-5 text-primary-400 mt-0.5" />
                  <a href="mailto:hello@brandkit.in" className="text-white hover:text-primary-400 transition-colors">
                    hello@brandkit.in
                  </a>
                </li>
                <li className="flex items-start gap-3">
                  <MapPin className="w-5 h-5 text-primary-400 mt-0.5" />
                  <p className="text-secondary-400">
                    123 Business Park, HSR Layout,<br />
                    Bengaluru, Karnataka 560102
                  </p>
                </li>
              </ul>
            </div>
          </div>
        </div>

        {/* Bottom Footer */}
        <div className="border-t border-secondary-800">
          <div className="container-wide py-6 flex flex-col md:flex-row items-center justify-between gap-4">
            <p className="text-secondary-400 text-sm">
              © 2026 BrandKit. All rights reserved.
            </p>
            <div className="flex items-center gap-6 text-sm">
              <Link href="/privacy" className="text-secondary-400 hover:text-white transition-colors">Privacy Policy</Link>
              <Link href="/terms" className="text-secondary-400 hover:text-white transition-colors">Terms of Service</Link>
              <Link href="/refund" className="text-secondary-400 hover:text-white transition-colors">Refund Policy</Link>
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
}
