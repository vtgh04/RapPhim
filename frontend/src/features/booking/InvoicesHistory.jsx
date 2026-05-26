import React, { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { motion, AnimatePresence } from 'framer-motion'
import api from '../../services/api'
import Spinner from '../../components/ui/Spinner'
import { FileText, Ticket, Calendar, DollarSign, Receipt, ChevronDown, ChevronUp, Search, Download, HelpCircle } from 'lucide-react'
import { useUIStore } from '../../store/uiStore'

export default function InvoicesHistory() {
  const { t, language } = useUIStore()
  const [expandedInvoiceId, setExpandedInvoiceId] = useState(null)
  
  // Filter & Search states
  const [searchId, setSearchId] = useState('')
  const [dateFilter, setDateFilter] = useState('ALL') // 'ALL' | '7days' | '30days' | 'custom'
  const [startDate, setStartDate] = useState('')
  const [endDate, setEndDate] = useState('')
  
  // Pagination state
  const [currentPage, setCurrentPage] = useState(1)
  const pageSize = 5

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

  // File Download Handlers
  const handleDownloadInvoice = async (invoiceId) => {
    try {
      const response = await api.get(`/bookings/invoices/${invoiceId}/pdf`, {
        responseType: 'blob'
      })
      const url = window.URL.createObjectURL(new Blob([response.data]))
      const link = document.createElement('a')
      link.href = url
      link.setAttribute('download', `invoice_${invoiceId}.pdf`)
      document.body.appendChild(link)
      link.click()
      link.remove()
    } catch (err) {
      // Caught by api interceptor
    }
  }

  const handleDownloadTickets = async (invoiceId) => {
    try {
      const response = await api.get(`/bookings/invoices/${invoiceId}/tickets/pdf`, {
        responseType: 'blob'
      })
      const url = window.URL.createObjectURL(new Blob([response.data]))
      const link = document.createElement('a')
      link.href = url
      link.setAttribute('download', `tickets_${invoiceId}.pdf`)
      document.body.appendChild(link)
      link.click()
      link.remove()
    } catch (err) {
      // Caught by api interceptor
    }
  }

  // Filter and Search logic
  const filteredInvoices = invoices.filter((inv) => {
    // 1. Search by ID (case-insensitive substring)
    const matchSearch = inv.invoiceId.toLowerCase().includes(searchId.toLowerCase())
    
    // 2. Filter by date range
    let matchDate = true
    const invDate = new Date(inv.createdAt)
    const now = new Date()

    if (dateFilter === '7days') {
      const sevenDaysAgo = new Date(now.setDate(now.getDate() - 7))
      matchDate = invDate >= sevenDaysAgo
    } else if (dateFilter === '30days') {
      const thirtyDaysAgo = new Date(now.setDate(now.getDate() - 30))
      matchDate = invDate >= thirtyDaysAgo
    } else if (dateFilter === 'custom' && startDate && endDate) {
      const start = new Date(startDate)
      start.setHours(0, 0, 0, 0)
      const end = new Date(endDate)
      end.setHours(23, 59, 59, 999)
      matchDate = invDate >= start && invDate <= end
    }

    return matchSearch && matchDate
  })

  // Pagination calculations
  const totalPages = Math.ceil(filteredInvoices.length / pageSize)
  const paginatedInvoices = filteredInvoices.slice((currentPage - 1) * pageSize, currentPage * pageSize)

  const handlePageChange = (page) => {
    if (page >= 1 && page <= totalPages) {
      setCurrentPage(page)
      setExpandedInvoiceId(null) // Collapse on page change
    }
  }

  if (isLoading) return <Spinner className="py-24" size="lg" />

  return (
    <div className="space-y-6">
      {/* Title */}
      <div>
        <h1 className="text-3xl font-black text-white flex items-center gap-2">
          <Receipt size={28} className="text-brand" />
          {t('billingHistoryTitle')}
        </h1>
        <p className="text-xs text-slate-400 font-semibold mt-0.5">
          {language === 'vi' ? 'Quản lý lịch sử hóa đơn, chi tiết số ghế và tải lại vé dạng file PDF' : 'Manage your billing logs, seat labels, and redownload ticket PDFs'}
        </p>
      </div>

      {/* Filter and Search Bar */}
      <div className="glass-panel p-5 rounded-2xl grid grid-cols-1 md:grid-cols-4 gap-4 items-end">
        {/* Search Input */}
        <div className="space-y-1.5 md:col-span-1">
          <label className="block text-xs font-bold text-slate-400 uppercase tracking-wider">
            {language === 'vi' ? 'Tìm theo mã hóa đơn' : 'Search by Invoice ID'}
          </label>
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-500" size={14} />
            <input
              type="text"
              value={searchId}
              onChange={(e) => {
                setSearchId(e.target.value)
                setCurrentPage(1)
              }}
              placeholder="INV001..."
              className="w-full pl-9 pr-3 py-2 bg-slate-950 border border-slate-900 rounded-xl text-slate-200 text-xs font-semibold focus:border-brand/40"
            />
          </div>
        </div>

        {/* Date presets */}
        <div className="space-y-1.5 md:col-span-2">
          <label className="block text-xs font-bold text-slate-400 uppercase tracking-wider">
            {language === 'vi' ? 'Bộ lọc thời gian' : 'Time Filter'}
          </label>
          <div className="flex gap-2 flex-wrap">
            {[
              { id: 'ALL', label: language === 'vi' ? 'Tất cả' : 'All' },
              { id: '7days', label: language === 'vi' ? '7 ngày gần đây' : 'Last 7 Days' },
              { id: '30days', label: language === 'vi' ? '30 ngày gần đây' : 'Last 30 Days' },
              { id: 'custom', label: language === 'vi' ? 'Tùy chỉnh' : 'Custom Range' },
            ].map((preset) => (
              <button
                key={preset.id}
                onClick={() => {
                  setDateFilter(preset.id)
                  setCurrentPage(1)
                }}
                className={`px-3 py-2 rounded-xl text-xs font-bold transition-all cursor-pointer ${
                  dateFilter === preset.id
                    ? 'bg-brand/10 border border-brand text-brand'
                    : 'bg-slate-950 border border-slate-900 text-slate-400 hover:text-slate-200'
                }`}
              >
                {preset.label}
              </button>
            ))}
          </div>
        </div>

        {/* Custom date range inputs */}
        {dateFilter === 'custom' && (
          <div className="flex gap-2 items-center md:col-span-4 mt-2">
            <div className="space-y-1">
              <span className="text-[10px] text-slate-500 font-bold uppercase">{language === 'vi' ? 'Từ ngày' : 'Start'}</span>
              <input
                type="date"
                value={startDate}
                onChange={(e) => {
                  setStartDate(e.target.value)
                  setCurrentPage(1)
                }}
                className="px-3 py-1.5 bg-slate-950 border border-slate-900 rounded-xl text-slate-200 text-xs font-semibold"
              />
            </div>
            <span className="text-slate-500 text-xs self-end mb-2">—</span>
            <div className="space-y-1">
              <span className="text-[10px] text-slate-500 font-bold uppercase">{language === 'vi' ? 'Đến ngày' : 'End'}</span>
              <input
                type="date"
                value={endDate}
                onChange={(e) => {
                  setEndDate(e.target.value)
                  setCurrentPage(1)
                }}
                className="px-3 py-1.5 bg-slate-950 border border-slate-900 rounded-xl text-slate-200 text-xs font-semibold"
              />
            </div>
          </div>
        )}
      </div>

      {/* Invoices List */}
      {paginatedInvoices.length === 0 ? (
        <div className="glass-panel p-12 text-center text-slate-500 rounded-3xl">
          {t('noInvoices')}
        </div>
      ) : (
        <div className="space-y-4">
          {paginatedInvoices.map((inv) => {
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
                className="glass-panel rounded-2xl overflow-hidden border border-slate-900 hover:border-slate-800/85 transition-all"
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
                      <span>{language === 'vi' ? 'Thanh toán:' : 'Payment:'} <span className="font-bold text-slate-300">{inv.paymentMethod}</span></span>
                    </div>
                  </div>

                  <div className="flex items-center gap-6 self-stretch sm:self-auto justify-between border-t border-slate-900 sm:border-0 pt-3 sm:pt-0">
                    <div className="flex items-center gap-6">
                      <div className="text-right">
                        <span className="block text-[10px] text-slate-500 uppercase font-bold">{language === 'vi' ? 'Số vé' : 'Tickets'}</span>
                        <span className="font-extrabold text-slate-200">{inv.totalTickets} {language === 'vi' ? 'vé' : 'tkts'}</span>
                      </div>
                      
                      <div className="text-right">
                        <span className="block text-[10px] text-slate-500 uppercase font-bold">{t('totalAmount')}</span>
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
                      {/* Sub-Header: Movie Details & PDF download triggers */}
                      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 pb-4 border-b border-slate-900/60">
                        {isTicketsLoading ? (
                          <div className="h-10 bg-slate-900 animate-pulse rounded-lg w-1/2" />
                        ) : (
                          <div>
                            <h4 className="font-black text-white text-base leading-snug">
                              {tickets[0]?.movieTitle || (language === 'vi' ? 'Không rõ tên phim' : 'Unknown Movie')}
                            </h4>
                            <p className="text-[11px] text-slate-400 font-semibold mt-1 capitalize">
                              {tickets[0]?.startTime ? new Date(tickets[0].startTime).toLocaleString('vi-VN', { weekday: 'long', day: '2-digit', month: '2-digit', hour: '2-digit', minute: '2-digit' }) : ''}
                              {tickets[0]?.hallId ? ` — Phòng: ${tickets[0].hallId.toUpperCase()}` : ''}
                            </p>
                          </div>
                        )}

                        <div className="flex gap-2">
                          <button
                            onClick={() => handleDownloadInvoice(inv.invoiceId)}
                            className="flex items-center gap-1.5 px-3 py-2 bg-slate-900 border border-slate-800 text-[11px] text-slate-300 font-extrabold rounded-xl hover:text-white hover:border-slate-700 transition-all cursor-pointer"
                          >
                            <Download size={13} />
                            {language === 'vi' ? 'Tải hóa đơn' : 'Invoice PDF'}
                          </button>
                          <button
                            onClick={() => handleDownloadTickets(inv.invoiceId)}
                            className="flex items-center gap-1.5 px-3 py-2 bg-brand/10 border border-brand/20 text-[11px] text-brand font-extrabold rounded-xl hover:bg-brand/20 transition-all cursor-pointer"
                          >
                            <Download size={13} />
                            {language === 'vi' ? 'Tải vé xem phim' : 'Tickets PDF'}
                          </button>
                        </div>
                      </div>

                      {/* Seat labels list */}
                      {!isTicketsLoading && tickets.length > 0 && (
                        <div className="p-3 bg-slate-900/50 border border-slate-800/60 rounded-xl flex items-center justify-between text-xs">
                          <span className="font-bold text-slate-400">{language === 'vi' ? 'Vị trí ghế đặt:' : 'Booked Seats:'}</span>
                          <span className="font-extrabold text-brand uppercase tracking-wider text-sm">
                            {tickets.map(t => t.seatLabel).filter(Boolean).join(', ')}
                          </span>
                        </div>
                      )}

                      <h4 className="text-xs font-bold text-slate-500 uppercase tracking-wider flex items-center gap-1.5 mt-2">
                        <Ticket size={13} className="text-brand" />
                        {language === 'vi' ? 'Danh sách mã vé chi tiết' : 'Ticket barcodes details'}
                      </h4>

                      {isTicketsLoading ? (
                        <Spinner className="py-6" size="sm" />
                      ) : (
                        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
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
                                <span className="font-bold text-slate-300">{language === 'vi' ? 'Giá vé' : 'Ticket Price'}</span>
                                <span className="font-extrabold text-slate-100">
                                  {t.finalPrice.toLocaleString('vi-VN')}đ
                                </span>
                              </div>

                              <div className="pt-2 border-t border-slate-900/60 flex items-center justify-between text-[10px] text-slate-500 font-medium">
                                <span>Barcode: <span className="font-mono text-slate-400">{t.barcode}</span></span>
                                {t.seatLabel && <span className="font-bold uppercase text-slate-400">{language === 'vi' ? 'Ghế' : 'Seat'} {t.seatLabel}</span>}
                              </div>
                            </div>
                          ))}
                        </div>
                      )}
                      
                      {inv.note && (
                        <div className="mt-4 p-3 bg-slate-900/40 border border-slate-900/60 rounded-xl text-xs text-slate-400 leading-relaxed font-medium">
                          <span className="font-bold text-slate-500">{language === 'vi' ? 'Ghi chú:' : 'Note:'}</span> {inv.note}
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

      {/* Pagination controls */}
      {totalPages > 1 && (
        <div className="flex items-center justify-center gap-2 pt-4">
          <button
            onClick={() => handlePageChange(currentPage - 1)}
            disabled={currentPage === 1}
            className="px-3 py-1.5 bg-slate-900 border border-slate-800 text-xs font-bold rounded-xl text-slate-300 hover:text-white disabled:opacity-50 transition-all cursor-pointer"
          >
            {language === 'vi' ? 'Trước' : 'Prev'}
          </button>
          
          {Array.from({ length: totalPages }).map((_, idx) => {
            const pageNum = idx + 1
            return (
              <button
                key={pageNum}
                onClick={() => handlePageChange(pageNum)}
                className={`w-8 h-8 rounded-xl text-xs font-bold transition-all cursor-pointer ${
                  currentPage === pageNum
                    ? 'bg-brand text-white'
                    : 'bg-slate-900 border border-slate-800 text-slate-400 hover:text-white'
                }`}
              >
                {pageNum}
              </button>
            )
          })}

          <button
            onClick={() => handlePageChange(currentPage + 1)}
            disabled={currentPage === totalPages}
            className="px-3 py-1.5 bg-slate-900 border border-slate-800 text-xs font-bold rounded-xl text-slate-300 hover:text-white disabled:opacity-50 transition-all cursor-pointer"
          >
            {language === 'vi' ? 'Sau' : 'Next'}
          </button>
        </div>
      )}
    </div>
  )
}
