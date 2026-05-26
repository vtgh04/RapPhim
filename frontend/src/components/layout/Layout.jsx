import React, { useState } from 'react'
import { Link, Outlet, useNavigate, useLocation } from 'react-router-dom'
import { useAuthStore } from '../../store/authStore'
import { Film, CreditCard, LayoutDashboard, LogOut, User, LogIn, Bell } from 'lucide-react'
import { useUIStore } from '../../store/uiStore'
import SettingsToggle from './SettingsToggle'
import GlobalSearch from '../ui/GlobalSearch'
import NotificationDropdown from '../ui/NotificationDropdown'
import { useNotificationStore } from '../../store/notificationStore'

export default function Layout() {
  const { user, logout } = useAuthStore()
  const { t } = useUIStore()
  const navigate = useNavigate()
  const location = useLocation()
  const { notifications } = useNotificationStore()
  const unreadCount = notifications.filter((n) => !n.read).length
  const [notifOpen, setNotifOpen] = useState(false)

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  const isActive = (path) => location.pathname === path

  return (
    <div className="min-h-screen w-full bg-dark-bg text-dark-text font-sans relative flex flex-col overflow-x-hidden">
      {/* Red/Light Radial Glow Background */}
      <div
        className="absolute inset-0 z-0 pointer-events-none"
        style={{
          backgroundImage: `var(--bg-pattern)`,
          backgroundSize: `var(--bg-size, auto)`,
        }}
      />
      {/* Content wrapper */}
      <div className="relative z-10 flex flex-col flex-grow min-h-screen">
      {/* Navigation Header */}
      <header className="fixed top-0 left-0 right-0 z-50 glass-panel border-b border-dark-border/50">
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
                    ? 'text-brand bg-dark-card/85' 
                    : 'text-dark-text/75 hover:text-brand hover:bg-dark-card/50'
                }`}
              >
                <Film size={16} />
                {t('navMovieShowtimes')}
              </Link>
              
              {user && (
                <Link
                  to="/invoices"
                  className={`flex items-center gap-2 px-3 py-2 rounded-md text-sm font-medium transition-all ${
                    isActive('/invoices') 
                      ? 'text-brand bg-dark-card/85' 
                      : 'text-dark-text/75 hover:text-brand hover:bg-dark-card/50'
                  }`}
                >
                  <CreditCard size={16} />
                  {t('navBillingHistory')}
                </Link>
              )}

              {user && (user.role === 'MANAGER' || user.role === 'ADMIN') && (
                <Link
                  to="/dashboard"
                  className={`flex items-center gap-2 px-3 py-2 rounded-md text-sm font-medium transition-all ${
                    isActive('/dashboard') 
                      ? 'text-brand bg-dark-card/85' 
                      : 'text-dark-text/75 hover:text-brand hover:bg-dark-card/50'
                  }`}
                >
                  <LayoutDashboard size={16} />
                  {t('navAdminDashboard')}
                </Link>
              )}
            </nav>

            {/* Profile / Login / Settings Toggle */}
            <div className="flex items-center gap-4">
              <GlobalSearch />
              
              {/* Notification Bell */}
              <div className="relative">
                <button
                  id="notification-bell-btn"
                  onClick={() => setNotifOpen((v) => !v)}
                  className="relative p-2 rounded-full hover:bg-dark-card/50 transition-all cursor-pointer text-dark-text/60 hover:text-brand"
                  title="Thông báo"
                >
                  <Bell size={18} />
                  {unreadCount > 0 && (
                    <span className="absolute -top-0.5 -right-0.5 w-4 h-4 bg-rose-500 text-white text-[9px] font-black rounded-full flex items-center justify-center">
                      {unreadCount > 9 ? '9+' : unreadCount}
                    </span>
                  )}
                </button>
                <NotificationDropdown isOpen={notifOpen} onClose={() => setNotifOpen(false)} />
              </div>

              <SettingsToggle />
              
              {user ? (
                <div className="flex items-center gap-3">
                  <div className="hidden sm:flex flex-col text-right">
                    <span className="text-sm font-semibold text-dark-text">{user.fullName}</span>
                    <span className="text-xs text-dark-text/60 capitalize">
                      {user.role === 'MANAGER' ? t('manager') : user.role === 'ADMIN' ? t('admin') : t('staff')}
                    </span>
                  </div>
                  <div className="flex items-center justify-center w-8 h-8 rounded-full bg-brand/10 border border-brand/20 text-brand">
                    <User size={16} />
                  </div>
                  <button
                    onClick={handleLogout}
                    title={t('logout')}
                    className="p-2 text-dark-text/50 hover:text-brand rounded-full hover:bg-dark-card/50 transition-all cursor-pointer animate-pulse-hover"
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
                  {t('login')}
                </Link>
              )}
            </div>

          </div>
        </div>
      </header>

      {/* Main Content Viewport */}
      <main className="flex-grow max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 pt-24 pb-8">
        <Outlet />
      </main>

      {/* Footer */}
      <footer className="border-t border-dark-border/40 bg-dark-card/60 py-6 text-center text-dark-text/50 text-sm">
        <div className="max-w-7xl mx-auto px-4">
          <p>© 2026 RapPhim Cinema Platform. Mọi quyền được bảo lưu.</p>
          <p className="text-xs mt-1 text-dark-text/30">Hệ thống đặt vé xem phim trực tuyến chuẩn Production-Level</p>
        </div>
      </footer>
      </div>
    </div>
  )
}
