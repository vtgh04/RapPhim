import React, { createContext, useContext, useState, useCallback } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { X, CheckCircle2, AlertTriangle, Info, AlertCircle } from 'lucide-react'

const ToastContext = createContext(null)

let toastRef = null

export const toast = {
  success: (msg) => toastRef?.success(msg),
  error: (msg) => toastRef?.error(msg),
  info: (msg) => toastRef?.info(msg),
  warn: (msg) => toastRef?.warn(msg)
}

export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([])

  const addToast = useCallback((message, type = 'info') => {
    const id = Date.now() + Math.random().toString(36).substr(2, 9)
    setToasts((prev) => [...prev, { id, message, type }])
    setTimeout(() => {
      removeToast(id)
    }, 4000)
  }, [])

  const removeToast = useCallback((id) => {
    setToasts((prev) => prev.filter((t) => t.id !== id))
  }, [])

  const success = useCallback((msg) => addToast(msg, 'success'), [addToast])
  const error = useCallback((msg) => addToast(msg, 'error'), [addToast])
  const info = useCallback((msg) => addToast(msg, 'info'), [addToast])
  const warn = useCallback((msg) => addToast(msg, 'warning'), [addToast])

  React.useEffect(() => {
    toastRef = { success, error, info, warn }
    return () => {
      toastRef = null
    }
  }, [success, error, info, warn])

  return (
    <ToastContext.Provider value={{ success, error, info, warn }}>
      {children}
      
      {/* Toast Portal Container */}
      <div className="fixed bottom-5 right-5 z-55 flex flex-col gap-3 max-w-sm w-full pointer-events-none">
        <AnimatePresence>
          {toasts.map((toast) => {
            let Icon = Info
            let bgClass = 'bg-slate-900/95 border-slate-800 text-slate-100'
            let iconColor = 'text-brand'

            if (toast.type === 'success') {
              Icon = CheckCircle2
              bgClass = 'bg-emerald-950/95 border-emerald-500/25 text-emerald-100'
              iconColor = 'text-emerald-400'
            } else if (toast.type === 'error') {
              Icon = AlertCircle
              bgClass = 'bg-rose-950/95 border-rose-500/25 text-rose-100'
              iconColor = 'text-rose-400'
            } else if (toast.type === 'warning') {
              Icon = AlertTriangle
              bgClass = 'bg-amber-950/95 border-amber-500/25 text-amber-100'
              iconColor = 'text-amber-400'
            }

            return (
              <motion.div
                key={toast.id}
                layout
                initial={{ opacity: 0, y: 30, scale: 0.9 }}
                animate={{ opacity: 1, y: 0, scale: 1 }}
                exit={{ opacity: 0, scale: 0.85, transition: { duration: 0.2 } }}
                className={`flex items-start gap-3 p-4 rounded-2xl border backdrop-blur-md shadow-2xl pointer-events-auto ${bgClass}`}
              >
                <Icon size={18} className={`shrink-0 mt-0.5 ${iconColor}`} />
                <div className="flex-grow text-xs font-bold leading-relaxed">{toast.message}</div>
                <button
                  onClick={() => removeToast(toast.id)}
                  className="shrink-0 p-0.5 rounded-full hover:bg-white/10 text-white/50 hover:text-white transition-all cursor-pointer"
                >
                  <X size={14} />
                </button>
              </motion.div>
            )
          })}
        </AnimatePresence>
      </div>
    </ToastContext.Provider>
  )
}

export function useToast() {
  const context = useContext(ToastContext)
  if (!context) {
    throw new Error('useToast must be used within a ToastProvider')
  }
  return context
}
