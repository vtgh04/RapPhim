import React, { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { motion, AnimatePresence } from 'framer-motion'
import api from '../../services/api'
import Spinner from '../../shared/Spinner'
import { FileText, Ticket, Calendar, DollarSign, Receipt, ChevronDown, ChevronUp } from 'lucide-react'

export default function InvoicesHistory() {
  const [expandedInvoiceId, setExpandedInvoiceId] = useState(null)

  // 1. Fetch invoices
  const { data: invoices = [], isLoading } = useQuery({
    queryKey: ['invoices'],
    queryFn: async () => {
      const response = await api.get('/bookings/invoices')
      return response.data
    }
  })

  // 2. Fetch tickets for expanded invoice
  const { data: tickets = [], isLoading: isTicketsLoading } = useQuery({
    queryKey: ['invoice-tickets', expandedInvoiceId],
    queryFn: async () => {
      const response = await api.get(`/bookings/invoices/${expandedInvoiceId}/tickets`)
      return response.data
    },
    enabled: !!expandedInvoiceId
  })

  const toggleExpand = (invoiceId) => {
    if (expandedInvoiceId === invoiceId) {
      setExpandedInvoiceId(null)
    } else {
      setExpandedInvoiceId(invoiceId)
    }
  }

  if (isLoading) return <Spinner className="py-24" size="lg" />

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-black text-white flex items-center gap-2">
          <Receipt size={28} className="text-brand" />
          Lịch Sử Giao Dịch
        </h1>
        <p className="text-xs text-slate-400 font-semibold mt-0.5">
          Danh sách các hóa đơn đặt vé đã được thanh toán thành công
        </p>
      </div>

      {invoices.length === 0 ? (
        <div className="glass-panel p-12 text-center text-slate-500 rounded-3xl">
          Chưa thực hiện giao dịch mua vé nào trên tài khoản này.
        </div>
      ) : (
        <div className="space-y-4">
          {invoices.map((inv) => {
            const isExpanded = expandedInvoiceId === inv.invoiceId
            const formattedDate = new Date(inv.createdAt).toLocaleString('vi-VN', {
              day: '2-digit',
              month: '2-digit',
              year: 'numeric',
              hour: '2-digit',
              minute: '2-digit'
            })

            return (
              <div
                key={inv.invoiceId}
                className="glass-panel rounded-2xl overflow-hidden border border-slate-900 hover:border-slate-800/80 transition-all"
              >
                {/* Header row */}
                <div
                  onClick={() => toggleExpand(inv.invoiceId)}
                  className="flex flex-col sm:flex-row justify-between items-start sm:items-center p-5 gap-4 cursor-pointer hover:bg-slate-900/30 transition-all"
                >
                  <div className="space-y-1">
                    <div className="flex items-center gap-2">
                      <span className="font-black text-slate-100 uppercase tracking-wide">
                        {inv.invoiceId}
                      </span>
                      <span className="px-2 py-0.5 bg-emerald-500/10 border border-emerald-500/25 rounded text-[10px] font-bold text-emerald-400 uppercase tracking-wide">
                        {inv.status}
                      </span>
                    </div>
                    
                    <div className="flex items-center gap-4 text-xs text-slate-400 font-medium">
                      <span className="flex items-center gap-1">
                        <Calendar size={12} />
                        {formattedDate}
                      </span>
                      <span>•</span>
                      <span>Phương thức: <span className="font-bold text-slate-300">{inv.paymentMethod}</span></span>
                    </div>
                  </div>

                  <div className="flex items-center gap-6 self-stretch sm:self-auto justify-between border-t border-slate-900 sm:border-0 pt-3 sm:pt-0">
                    <div className="flex items-center gap-6">
                      <div className="text-right">
                        <span className="block text-[10px] text-slate-500 uppercase font-bold">Số lượng vé</span>
                        <span className="font-extrabold text-slate-200">{inv.totalTickets} vé</span>
                      </div>
                      
                      <div className="text-right">
                        <span className="block text-[10px] text-slate-500 uppercase font-bold">Tổng thanh toán</span>
                        <span className="font-black text-green-400">{inv.totalAmount.toLocaleString('vi-VN')}đ</span>
                      </div>
                    </div>

                    <button className="text-slate-400 hover:text-brand transition-all">
                      {isExpanded ? <ChevronUp size={20} /> : <ChevronDown size={20} />}
                    </button>
                  </div>
                </div>

                {/* Expanded ticket listing details */}
                <AnimatePresence>
                  {isExpanded && (
                    <motion.div
                      initial={{ opacity: 0, height: 0 }}
                      animate={{ opacity: 1, height: 'auto' }}
                      exit={{ opacity: 0, height: 0 }}
                      className="border-t border-slate-900 bg-slate-950/40 p-5 space-y-4"
                    >
                      <h4 className="text-xs font-bold text-slate-400 uppercase tracking-wider flex items-center gap-1.5">
                        <Ticket size={14} className="text-brand" />
                        Danh sách vé thuộc hóa đơn
                      </h4>

                      {isTicketsLoading ? (
                        <Spinner className="py-6" size="sm" />
                      ) : (
                        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                          {tickets.map((t) => (
                            <div
                              key={t.ticketId}
                              className="p-4 bg-slate-900/60 border border-slate-800/80 rounded-xl space-y-2 relative overflow-hidden"
                            >
                              <div className="flex justify-between items-center text-xs">
                                <span className="font-bold text-brand uppercase">{t.ticketId}</span>
                                <span className="px-2 py-0.5 bg-slate-800 rounded font-semibold text-slate-400 uppercase tracking-wide">
                                  {t.status}
                                </span>
                              </div>
                              
                              <div className="flex justify-between text-sm">
                                <span className="font-bold text-slate-200">Giá vé</span>
                                <span className="font-bold text-slate-300">
                                  {t.price.toLocaleString('vi-VN')}đ
                                </span>
                              </div>

                              <div className="pt-2 border-t border-slate-900/60 flex items-center justify-between text-[11px] text-slate-500 font-medium">
                                <span>Barcode: <span className="font-mono text-slate-400">{t.barcode}</span></span>
                              </div>
                            </div>
                          ))}
                        </div>
                      )}
                      
                      {inv.note && (
                        <div className="mt-4 p-3 bg-slate-900/40 border border-slate-900 rounded-lg text-xs text-slate-400 leading-relaxed">
                          <span className="font-bold text-slate-500">Ghi chú:</span> {inv.note}
                        </div>
                      )}
                    </motion.div>
                  )}
                </AnimatePresence>
              </div>
            )
          })}
        </div>
      )}
    </div>
  )
}
