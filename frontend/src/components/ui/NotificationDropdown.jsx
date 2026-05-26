import React, { useRef, useEffect } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { Bell, Check, CheckCheck, Trash2, X, Info, CheckCircle, AlertTriangle } from 'lucide-react'
import { useNotificationStore } from '../../store/notificationStore'

const TYPE_ICON = {
  success: <CheckCircle size={14} className="text-emerald-400 shrink-0" />,
  warning: <AlertTriangle size={14} className="text-amber-400 shrink-0" />,
  info: <Info size={14} className="text-sky-400 shrink-0" />,
  error: <X size={14} className="text-rose-400 shrink-0" />,
}

function formatTimeAgo(date) {
  const seconds = Math.floor((Date.now() - new Date(date)) / 1000)
  if (seconds < 60) return 'Vừa xong'
  const minutes = Math.floor(seconds / 60)
  if (minutes < 60) return `${minutes} phút trước`
  const hours = Math.floor(minutes / 60)
  if (hours < 24) return `${hours} giờ trước`
  return `${Math.floor(hours / 24)} ngày trước`
}

export default function NotificationDropdown({ isOpen, onClose }) {
  const { notifications, markRead, markAllRead, remove, clearAll } = useNotificationStore()
  const unreadCount = notifications.filter((n) => !n.read).length
  const dropdownRef = useRef(null)

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        onClose()
      }
    }
    if (isOpen) document.addEventListener('mousedown', handleClickOutside)
    return () => document.removeEventListener('mousedown', handleClickOutside)
  }, [isOpen, onClose])

  return (
    <AnimatePresence>
      {isOpen && (
        <motion.div
          ref={dropdownRef}
          initial={{ opacity: 0, y: -8, scale: 0.96 }}
          animate={{ opacity: 1, y: 0, scale: 1 }}
          exit={{ opacity: 0, y: -8, scale: 0.96 }}
          transition={{ duration: 0.18 }}
          className="absolute right-0 top-full mt-2 w-96 z-50 rounded-2xl shadow-2xl border overflow-hidden"
          style={{
            backgroundColor: 'var(--color-panel-solid)',
            borderColor: 'var(--color-border)',
          }}
        >
          {/* Header */}
          <div className="flex items-center justify-between px-4 py-3 border-b" style={{ borderColor: 'var(--color-border)' }}>
            <div className="flex items-center gap-2">
              <Bell size={15} className="text-brand" />
              <span className="font-extrabold text-sm" style={{ color: 'var(--color-text)' }}>
                Thông báo
              </span>
              {unreadCount > 0 && (
                <span className="px-1.5 py-0.5 bg-brand text-white text-[10px] font-bold rounded-full">
                  {unreadCount}
                </span>
              )}
            </div>
            <div className="flex items-center gap-1">
              {unreadCount > 0 && (
                <button
                  onClick={markAllRead}
                  title="Đánh dấu tất cả đã đọc"
                  className="p-1.5 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 transition-all cursor-pointer"
                >
                  <CheckCheck size={14} style={{ color: 'var(--color-text-muted)' }} />
                </button>
              )}
              {notifications.length > 0 && (
                <button
                  onClick={clearAll}
                  title="Xóa tất cả"
                  className="p-1.5 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 transition-all cursor-pointer"
                >
                  <Trash2 size={14} style={{ color: 'var(--color-text-muted)' }} />
                </button>
              )}
              <button
                onClick={onClose}
                className="p-1.5 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 transition-all cursor-pointer"
              >
                <X size={14} style={{ color: 'var(--color-text-muted)' }} />
              </button>
            </div>
          </div>

          {/* List */}
          <div className="max-h-80 overflow-y-auto">
            {notifications.length === 0 ? (
              <div className="py-10 text-center text-sm" style={{ color: 'var(--color-text-muted)' }}>
                <Bell size={28} className="mx-auto mb-2 opacity-30" />
                Chưa có thông báo nào
              </div>
            ) : (
              <AnimatePresence initial={false}>
                {notifications.map((n) => (
                  <motion.div
                    key={n.id}
                    initial={{ opacity: 0, x: 10 }}
                    animate={{ opacity: 1, x: 0 }}
                    exit={{ opacity: 0, x: -10 }}
                    transition={{ duration: 0.15 }}
                    className={`flex items-start gap-3 px-4 py-3 border-b cursor-pointer group transition-all ${
                      !n.read ? 'bg-brand/5' : 'hover:bg-slate-50 dark:hover:bg-slate-800/30'
                    }`}
                    style={{ borderColor: 'var(--color-border)' }}
                    onClick={() => markRead(n.id)}
                  >
                    <div className="mt-0.5">
                      {TYPE_ICON[n.type] || TYPE_ICON.info}
                    </div>
                    <div className="flex-1 min-w-0">
                      <p className="text-xs font-bold leading-snug line-clamp-2" style={{ color: 'var(--color-text)' }}>
                        {n.title}
                      </p>
                      {n.message && (
                        <p className="text-[11px] mt-0.5 line-clamp-2" style={{ color: 'var(--color-text-muted)' }}>
                          {n.message}
                        </p>
                      )}
                      <p className="text-[10px] mt-1" style={{ color: 'var(--color-text-muted)' }}>
                        {formatTimeAgo(n.createdAt)}
                      </p>
                    </div>
                    <div className="flex items-center gap-1 opacity-0 group-hover:opacity-100 transition-all shrink-0">
                      {!n.read && (
                        <button
                          onClick={(e) => { e.stopPropagation(); markRead(n.id) }}
                          className="p-1 rounded hover:bg-emerald-100 dark:hover:bg-emerald-900/20"
                        >
                          <Check size={11} className="text-emerald-500" />
                        </button>
                      )}
                      <button
                        onClick={(e) => { e.stopPropagation(); remove(n.id) }}
                        className="p-1 rounded hover:bg-rose-100 dark:hover:bg-rose-900/20"
                      >
                        <X size={11} className="text-rose-500" />
                      </button>
                    </div>
                  </motion.div>
                ))}
              </AnimatePresence>
            )}
          </div>
        </motion.div>
      )}
    </AnimatePresence>
  )
}
