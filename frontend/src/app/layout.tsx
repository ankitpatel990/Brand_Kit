import type { Metadata } from 'next'
import { DM_Sans, Sora } from 'next/font/google'
import './globals.css'
import { Toaster } from 'react-hot-toast'

const dmSans = DM_Sans({ 
  subsets: ['latin'],
  variable: '--font-sans',
  display: 'swap',
  weight: ['400', '500', '600', '700'],
})

const sora = Sora({ 
  subsets: ['latin'],
  variable: '--font-display',
  display: 'swap',
  weight: ['400', '500', '600', '700', '800'],
})

export const metadata: Metadata = {
  title: 'BrandKit - Custom Print-on-Demand Merchandise | T-Shirts, Mugs, Bottles & More',
  description: 'Design and order custom branded merchandise. From T-shirts to mugs, bottles to corporate gifts - create personalized products with your logo. Fast delivery across India.',
  keywords: ['print on demand', 'custom merchandise', 'promotional products', 'corporate gifts', 'custom t-shirts', 'branded mugs', 'welcome kits'],
  openGraph: {
    title: 'BrandKit - Custom Print-on-Demand Merchandise',
    description: 'Your design, your way, printed beautifully. Create personalized products for your business.',
    type: 'website',
  },
}

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="en" className={`${dmSans.variable} ${sora.variable}`}>
      <body className="font-sans antialiased bg-white text-secondary-950">
        <Toaster 
          position="top-center"
          toastOptions={{
            duration: 4000,
            style: {
              background: '#0f141f',
              color: '#ffffff',
              borderRadius: '12px',
              padding: '16px 24px',
              fontSize: '14px',
              fontWeight: 500,
            },
            success: {
              iconTheme: {
                primary: '#10b981',
                secondary: '#ffffff',
              },
            },
            error: {
              iconTheme: {
                primary: '#ef4444',
                secondary: '#ffffff',
              },
            },
          }}
        />
        {children}
      </body>
    </html>
  )
}
