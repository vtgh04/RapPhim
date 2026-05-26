import React from 'react'
import { Link, Outlet, useNavigate, useLocation } from 'react-router-dom'
import { useAuthStore } from '../features/auth/AuthStore'
import { Film, CreditCard, LayoutDashboard, LogOut, User, LogIn } from 'lucide-react'

export default function Layout() {
  const { user, logout } = useAuthStore()
  const navigate = useNavigate()
  const location = useLocation()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  const isActive = (path) => location.pathname === path

  return (
    <div className="min-h-screen w-full bg-[#020617] text-slate-100 font-sans relative flex flex-col overflow-x-hidden">
      {/* Red Radial Glow Background */}
      <div
        className="absolute inset-0 z-0 pointer-events-none"
        style={{
          backgroundImage: `radial-gradient(circle 500px at 50% 100px, rgba(239,68,68,0.4), transparent)`,
        }}
      />
      {/* Content wrapper */}
      <div className="relative z-10 flex flex-col flex-grow min-h-screen">
      {/* Navigation Header */}
      <header className="sticky top-0 z-50 glass-panel border-b border-slate-800/50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between h-16">
            
            {/* Logo */}
            <div className="flex items-center">
              <Link to="/" className="flex items-center gap-2 text-2xl font-extrabold tracking-wider text-brand">
                <span>🎬</span>
                <span>RAPPHIM</span>
              </Link>
            </div>

            {/* Nav Menu */}
            <nav className="hidden md:flex space-x-6">
              <Link
                to="/"
                className={`flex items-center gap-2 px-3 py-2 rounded-md text-sm font-medium transition-all ${
                  isActive('/') 
                    ? 'text-brand bg-slate-900' 
                    : 'text-slate-300 hover:text-brand hover:bg-slate-900/50'
                }`}
              >
                <Film size={16} />
                Lịch Chiếu Phim
              </Link>
              
              {user && (
                <Link
                  to="/invoices"
                  className={`flex items-center gap-2 px-3 py-2 rounded-md text-sm font-medium transition-all ${
                    isActive('/invoices') 
                      ? 'text-brand bg-slate-900' 
                      : 'text-slate-300 hover:text-brand hover:bg-slate-900/50'
                  }`}
                >
                  <CreditCard size={16} />
                  Lịch Sử Hóa Đơn
                </Link>
              )}

              {user && (user.role === 'MANAGER' || user.role === 'ADMIN') && (
                <Link
                  to="/dashboard"
                  className={`flex items-center gap-2 px-3 py-2 rounded-md text-sm font-medium transition-all ${
                    isActive('/dashboard') 
                      ? 'text-brand bg-slate-900' 
                      : 'text-slate-300 hover:text-brand hover:bg-slate-900/50'
                  }`}
                >
                  <LayoutDashboard size={16} />
                  Bảng Quản Trị
                </Link>
              )}
            </nav>

            {/* Profile / Login */}
            <div className="flex items-center gap-4">
              {user ? (
                <div className="flex items-center gap-3">
                  <div className="hidden sm:flex flex-col text-right">
                    <span className="text-sm font-semibold text-slate-200">{user.fullName}</span>
                    <span className="text-xs text-slate-400 capitalize">{user.role.toLowerCase()}</span>
                  </div>
                  <div className="flex items-center justify-center w-8 h-8 rounded-full bg-brand/10 border border-brand/20 text-brand">
                    <User size={16} />
                  </div>
                  <button
                    onClick={handleLogout}
                    title="Đăng xuất"
                    className="p-2 text-slate-400 hover:text-brand rounded-full hover:bg-slate-900/50 transition-all"
                  >
                    <LogOut size={18} />
                  </button>
                </div>
              ) : (
                <Link
                  to="/login"
                  className="flex items-center gap-2 px-4 py-2 text-sm font-bold text-white bg-brand rounded-full hover:bg-brand-hover shadow-lg hover:shadow-brand/20 transition-all"
                >
                  <LogIn size={16} />
                  Đăng Nhập
                </Link>
              )}
            </div>

          </div>
        </div>
      </header>

      {/* Main Content Viewport */}
      <main className="flex-grow max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <Outlet />
      </main>

      {/* Footer */}
      <footer className="border-t border-slate-900/80 bg-slate-950/80 py-6 text-center text-slate-500 text-sm">
        <div className="max-w-7xl mx-auto px-4">
          <p>© 2026 RapPhim Cinema Platform. Mọi quyền được bảo lưu.</p>
          <p className="text-xs mt-1 text-slate-600">Hệ thống đặt vé xem phim trực tuyến chuẩn Production-Level</p>
        </div>
      </footer>
      </div>
    </div>
  )
}
