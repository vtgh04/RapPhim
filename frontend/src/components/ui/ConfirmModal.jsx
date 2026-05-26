import React from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { AlertTriangle, X } from 'lucide-react'
import { useUIStore } from '../../store/uiStore'

export default function ConfirmModal({
  isOpen,
  onClose,
  onConfirm,
  title,
  message,
  confirmText,
  cancelText,
  type = 'danger' // 'danger' | 'info' | 'warning'
}) {
  const { t } = useUIStore()

  // Default localized texts if not provided
  const displayConfirmText = confirmText || (type === 'danger' ? t('delete') : t('saveChanges'))
  const displayCancelText = cancelText || t('cancel')

  return (
    <AnimatePresence>
      {isOpen && (
        <div className="fixed inset-0 z-55 flex items-center justify-center p-4">
          {/* Backdrop Overlay */}
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            onClick={onClose}
            className="absolute inset-0 bg-slate-950/80 backdrop-blur-sm"
          />

          {/* Modal Card */}
          <motion.div
            initial={{ opacity: 0, scale: 0.95, y: 15 }}
            animate={{ opacity: 1, scale: 1, y: 0 }}
            exit={{ opacity: 0, scale: 0.95, y: 15 }}
            transition={{ type: 'spring', duration: 0.4 }}
            className="relative z-10 w-full max-w-md p-6 bg-slate-900/90 border border-slate-800 backdrop-blur-md rounded-3xl shadow-2xl flex flex-col gap-4 text-left"
          >
            {/* Close button */}
            <button
              onClick={onClose}
              className="absolute top-4 right-4 p-1 rounded-full text-slate-400 hover:text-white hover:bg-slate-800 transition-all cursor-pointer"
            >
              <X size={16} />
            </button>

            {/* Header / Icon */}
            <div className="flex items-center gap-3">
              <div className={`p-2.5 rounded-xl ${
                type === 'danger'
                  ? 'bg-rose-500/10 text-rose-500'
                  : type === 'warning'
                  ? 'bg-amber-500/10 text-amber-500'
                  : 'bg-sky-500/10 text-sky-500'
              }`}>
                <AlertTriangle size={20} />
              </div>
              <h3 className="font-extrabold text-white text-base leading-snug">{title}</h3>
            </div>

            {/* Body message */}
            <div className="text-xs text-slate-400 leading-relaxed font-semibold">
              {message}
            </div>

            {/* Actions */}
            <div className="flex gap-3 mt-2 justify-end">
              <button
                type="button"
                onClick={onClose}
                className="px-4 py-2.5 bg-slate-800 hover:bg-slate-700 text-slate-300 font-bold text-xs rounded-xl transition-all uppercase tracking-wider cursor-pointer"
              >
                {displayCancelText}
              </button>
              <button
                type="button"
                onClick={() => {
                  onConfirm()
                  onClose()
                }}
                className={`px-5 py-2.5 font-bold text-xs rounded-xl shadow-md transition-all uppercase tracking-wider cursor-pointer ${
                  type === 'danger'
                    ? 'bg-rose-600 hover:bg-rose-500 text-white shadow-rose-950/20'
                    : type === 'warning'
                    ? 'bg-amber-600 hover:bg-amber-500 text-white shadow-amber-950/20'
                    : 'bg-brand hover:bg-brand-hover text-white'
                }`}
              >
                {displayConfirmText}
              </button>
            </div>
          </motion.div>
        </div>
      )}
    </AnimatePresence>
  )
}
