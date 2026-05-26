import React, { useState } from 'react'
import { useSearchParams, useNavigate, Link } from 'react-router-dom'
import { useQuery, useMutation } from '@tanstack/react-query'
import { motion, AnimatePresence } from 'framer-motion'
import api from '../../services/api'
import Spinner from '../../shared/Spinner'
import { ChevronLeft, Landmark, CreditCard, Ticket, CheckCircle, FileText, Gift, Info } from 'lucide-react'

export default function Checkout() {
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()
  const showtimeId = searchParams.get('showtimeId')
  const seatIdsString = searchParams.get('seats') || ''
  const seatIds = seatIdsString ? seatIdsString.split(',') : []

  const [paymentMethod, setPaymentMethod] = useState('VNPAY')
  const [note, setNote] = useState('')
  const [discountCode, setDiscountCode] = useState('')
  const [discountApplied, setDiscountApplied] = useState(null)
  const [discountError, setDiscountError] = useState('')
  const [checkoutSuccess, setCheckoutSuccess] = useState(null)

  // 1. Fetch Showtime info
  const { data: showtime, isLoading: isShowtimeLoading } = useQuery({
    queryKey: ['showtime', showtimeId],
    queryFn: async () => {
      const response = await api.get(`/showtimes/${showtimeId}`)
      return response.data
    },
    enabled: !!showtimeId
  })

  // 2. Fetch Movie info
  const { data: movie, isLoading: isMovieLoading } = useQuery({
    queryKey: ['movie', showtime?.movieId],
    queryFn: async () => {
      const response = await api.get(`/movies/${showtime?.movieId}`)
      return response.data
    },
    enabled: !!showtime?.movieId
  })

  // 3. Fetch Hall seats layout to get factors
  const { data: seats = [], isLoading: isSeatsLoading } = useQuery({
    queryKey: ['hall-seats', showtime?.hallId],
    queryFn: async () => {
      const response = await api.get(`/cinema-halls/${showtime?.hallId}/seats`)
      return response.data
    },
    enabled: !!showtime?.hallId
  })

  // 4. Fetch all active discounts
  const { data: discounts = [] } = useQuery({
    queryKey: ['discounts'],
    queryFn: async () => {
      const response = await api.get('/discounts')
      return response.data
    }
  })

  // 5. Checkout mutation
  const checkoutMutation = useMutation({
    mutationFn: async (payload) => {
      const response = await api.post('/bookings/checkout', payload)
      return response.data
    },
    onSuccess: (data) => {
      setCheckoutSuccess(data)
    }
  })

  if (isShowtimeLoading || isMovieLoading || isSeatsLoading) {
    return <Spinner className="py-24" size="lg" />
  }

  if (!showtime || !movie || seatIds.length === 0) {
    return (
      <div className="text-center py-24 text-red-500 space-y-4">
        <p className="font-bold text-lg">Thông tin đặt vé không hợp lệ.</p>
        <Link to="/" className="inline-flex items-center gap-1 text-sm text-brand hover:underline">
          <ChevronLeft size={16} /> Quay lại trang chủ
        </Link>
      </div>
    )
  }

  // Calculate ticket pricing
  const basePrice = showtime.basePrice
  const selectedSeatsData = seatIds.map(id => {
    const seatObj = seats.find(s => s.seatId === id)
    const factor = seatObj ? seatObj.seatFactor : 1
    const price = basePrice * factor
    return {
      seatId: id,
      type: seatObj ? seatObj.seatType : 'REGULAR',
      price
    }
  })

  const subtotal = selectedSeatsData.reduce((sum, item) => sum + item.price, 0)
  
  // Calculate discount amount
  const discountVal = discountApplied 
    ? (discountApplied.discountPercentage ? (subtotal * discountApplied.discountPercentage) / 100 : 0)
    : 0
  const finalTotal = Math.max(0, subtotal - discountVal)

  const handleApplyDiscount = () => {
    setDiscountError('')
    const code = discountCode.trim().toUpperCase()
    if (!code) return

    const match = discounts.find(d => d.code === code)
    if (!match) {
      setDiscountError('Mã giảm giá không tồn tại.')
      return
    }

    setDiscountApplied(match)
    setDiscountCode('')
  }

  const handleRemoveDiscount = () => {
    setDiscountApplied(null)
  }

  const handleConfirmOrder = () => {
    const noteText = discountApplied 
      ? `${note} | Mã giảm giá: ${discountApplied.code} (Giảm ${discountApplied.discountPercentage}%)`
      : note

    checkoutMutation.mutate({
      showtimeId,
      seatIds,
      paymentMethod,
      note: noteText
    })
  }

  if (checkoutSuccess) {
    return (
      <div className="flex items-center justify-center min-h-[70vh]">
        <motion.div
          initial={{ opacity: 0, scale: 0.95 }}
          animate={{ opacity: 1, scale: 1 }}
          className="w-full max-w-xl p-8 glass-panel rounded-3xl text-center space-y-6 relative overflow-hidden"
        >
          {/* Accent decoration */}
          <div className="absolute top-0 inset-x-0 h-2 bg-gradient-to-r from-green-500 via-emerald-400 to-green-500" />
          
          <div className="flex items-center justify-center w-20 h-20 bg-emerald-500/10 text-emerald-400 rounded-full mx-auto shadow-2xl">
            <CheckCircle size={48} />
          </div>

          <div className="space-y-2">
            <h2 className="text-3xl font-black text-white">THANH TOÁN THÀNH CÔNG!</h2>
            <p className="text-sm text-slate-400">Giao dịch đã được hệ thống ghi nhận thành công.</p>
          </div>

          {/* Bill summary card */}
          <div className="bg-slate-900/60 border border-slate-800/80 rounded-2xl p-5 text-left text-sm space-y-4 max-w-md mx-auto">
            <div className="flex justify-between pb-2.5 border-b border-slate-800">
              <span className="text-slate-500">Mã hóa đơn</span>
              <span className="font-bold text-white uppercase">{checkoutSuccess.invoiceId}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-slate-500">Bộ phim</span>
              <span className="font-bold text-slate-200">{movie.title}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-slate-500">Số lượng vé</span>
              <span className="font-semibold text-slate-200">{checkoutSuccess.totalTickets} vé</span>
            </div>
            <div className="flex justify-between">
              <span className="text-slate-500">Phương thức</span>
              <span className="font-semibold text-brand tracking-wider">{checkoutSuccess.paymentMethod}</span>
            </div>
            <div className="flex justify-between pt-2.5 border-t border-slate-800 text-base">
              <span className="font-bold text-slate-300">Tổng thanh toán</span>
              <span className="font-black text-green-400">{checkoutSuccess.totalAmount.toLocaleString('vi-VN')}đ</span>
            </div>
          </div>

          <div className="flex items-center justify-center gap-2 p-3.5 bg-brand/10 border border-brand/15 rounded-xl text-xs text-brand font-bold max-w-md mx-auto">
            <FileText size={14} className="flex-shrink-0" />
            <span>Hóa đơn PDF và vé xem phim (PDF) đã được xuất thành công vào thư mục `invoice/` của server!</span>
          </div>

          <div className="pt-4">
            <Link
              to="/"
              className="inline-flex items-center gap-2 px-6 py-3.5 bg-brand hover:bg-brand-hover text-white font-bold rounded-xl shadow-lg transition-all text-sm uppercase tracking-wider"
            >
              Quay Lại Lịch Chiếu
            </Link>
          </div>
        </motion.div>
      </div>
    )
  }

  return (
    <div className="space-y-8">
      {/* Back button */}
      <Link to={`/booking/${showtimeId}`} className="inline-flex items-center gap-1.5 text-sm font-semibold text-slate-400 hover:text-brand transition-all">
        <ChevronLeft size={18} />
        Quay lại Sơ Đồ Ghế
      </Link>

      <h1 className="text-3xl font-black text-white pb-3 border-b border-slate-900">Xác Nhận Đặt Vé & Thanh Toán</h1>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        
        {/* Left Side: Forms (Payment & Notes) */}
        <div className="lg:col-span-2 space-y-6">
          {/* Payment Method Cards */}
          <div className="glass-panel p-6 rounded-3xl space-y-4">
            <h3 className="font-extrabold text-white text-base flex items-center gap-2">
              <CreditCard size={18} className="text-brand" />
              Phương thức thanh toán
            </h3>
            
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
              {[
                { id: 'VNPAY', label: 'Cổng VNPAY', desc: 'Thanh toán QR hoặc ATM' },
                { id: 'MOMO', label: 'Ví MoMo', desc: 'Thanh toán nhanh qua App' },
                { id: 'CASH', label: 'Tiền mặt', desc: 'Thanh toán tại quầy vé' },
              ].map((pm) => (
                <div
                  key={pm.id}
                  onClick={() => setPaymentMethod(pm.id)}
                  className={`p-4 border rounded-xl cursor-pointer transition-all ${
                    paymentMethod === pm.id
                      ? 'bg-brand/10 border-brand text-brand'
                      : 'bg-slate-900 border-slate-800/80 hover:border-slate-700 text-slate-300'
                  }`}
                >
                  <span className="block font-bold text-sm text-slate-100">{pm.label}</span>
                  <span className="block text-[11px] text-slate-500 mt-1">{pm.desc}</span>
                </div>
              ))}
            </div>
          </div>

          {/* Notes & Discounts */}
          <div className="glass-panel p-6 rounded-3xl space-y-5">
            <h3 className="font-extrabold text-white text-base flex items-center gap-2">
              <Gift size={18} className="text-brand" />
              Ưu đãi & Ghi chú
            </h3>

            {/* Discount Form */}
            <div className="space-y-2">
              <label className="block text-xs font-bold text-slate-400 uppercase tracking-wider">Mã giảm giá</label>
              <div className="flex gap-2">
                <input
                  type="text"
                  value={discountCode}
                  onChange={(e) => setDiscountCode(e.target.value)}
                  placeholder="Nhập mã ưu đãi (ví dụ: UUDAI10)..."
                  className="flex-grow px-4 py-3 bg-slate-900 border border-slate-800 rounded-xl text-slate-200 text-sm"
                />
                <button
                  type="button"
                  onClick={handleApplyDiscount}
                  className="px-5 py-3 bg-slate-800 hover:bg-slate-700 text-slate-200 hover:text-white rounded-xl text-sm font-bold transition-all"
                >
                  Áp Dụng
                </button>
              </div>
              {discountError && <p className="text-xs text-brand font-medium mt-1">{discountError}</p>}

              <AnimatePresence>
                {discountApplied && (
                  <motion.div
                    initial={{ opacity: 0, height: 0 }}
                    animate={{ opacity: 1, height: 'auto' }}
                    exit={{ opacity: 0, height: 0 }}
                    className="flex justify-between items-center p-3 bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 rounded-xl text-xs font-semibold mt-3"
                  >
                    <span>Mã `{discountApplied.code}` đã áp dụng: Giảm {discountApplied.discountPercentage}%</span>
                    <button
                      type="button"
                      onClick={handleRemoveDiscount}
                      className="text-brand hover:underline text-xs"
                    >
                      Hủy bỏ
                    </button>
                  </motion.div>
                )}
              </AnimatePresence>
            </div>

            {/* Note Area */}
            <div className="space-y-2 pt-2">
              <label className="block text-xs font-bold text-slate-400 uppercase tracking-wider">Ghi chú giao dịch</label>
              <textarea
                rows={3}
                value={note}
                onChange={(e) => setNote(e.target.value)}
                placeholder="Nhập ghi chú hoặc thông tin khách hàng nếu cần..."
                className="w-full px-4 py-3 bg-slate-900 border border-slate-800 rounded-xl text-slate-200 text-sm resize-none"
              />
            </div>
          </div>
        </div>

        {/* Right Side: Order Summary */}
        <div className="lg:col-span-1 space-y-6">
          <div className="glass-panel p-6 rounded-3xl space-y-5 flex flex-col justify-between h-full">
            <div className="space-y-4">
              <h3 className="font-extrabold text-white text-base pb-3 border-b border-slate-900">Chi Tiết Thanh Toán</h3>

              {/* Movie info */}
              <div className="space-y-1.5">
                <span className="text-2xl font-black text-white">{movie.title}</span>
                <p className="text-xs text-slate-400 font-semibold uppercase flex items-center gap-1">
                  <Landmark size={12} />
                  {showtime.hallId} — Suất {new Date(showtime.startTime).toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' })}
                </p>
              </div>

              {/* Tickets list */}
              <div className="space-y-2.5 pt-3 border-t border-slate-900 max-h-48 overflow-y-auto pr-1">
                {selectedSeatsData.map((item) => (
                  <div key={item.seatId} className="flex justify-between items-center text-sm">
                    <span className="font-bold text-slate-300 flex items-center gap-1.5 uppercase">
                      <Ticket size={13} className="text-brand" />
                      Ghế {item.seatId}
                    </span>
                    <span className="font-semibold text-slate-400">
                      {item.price.toLocaleString('vi-VN')}đ
                    </span>
                  </div>
                ))}
              </div>
            </div>

            {/* Calculations breakdown */}
            <div className="space-y-4 pt-4 border-t border-slate-900 text-sm">
              <div className="flex justify-between text-slate-400">
                <span>Tạm tính</span>
                <span>{subtotal.toLocaleString('vi-VN')}đ</span>
              </div>
              
              {discountApplied && (
                <div className="flex justify-between text-emerald-400">
                  <span>Giảm giá ({discountApplied.discountPercentage}%)</span>
                  <span>-{discountVal.toLocaleString('vi-VN')}đ</span>
                </div>
              )}

              <div className="flex justify-between items-end pt-2 border-t border-slate-900/60">
                <span className="text-base text-white font-bold mb-1">Tổng tiền thanh toán</span>
                <span className="text-2xl font-black text-brand tracking-tight">
                  {finalTotal.toLocaleString('vi-VN')}đ
                </span>
              </div>

              <button
                onClick={handleConfirmOrder}
                disabled={checkoutMutation.isPending}
                className="w-full flex items-center justify-center gap-2 py-4 bg-brand hover:bg-brand-hover text-white font-extrabold rounded-xl shadow-lg shadow-brand/10 hover:shadow-brand/20 transition-all text-sm uppercase tracking-wider"
              >
                {checkoutMutation.isPending ? (
                  <span className="w-5 h-5 border-2 border-t-transparent border-white rounded-full animate-spin" />
                ) : (
                  'Xác Nhận Thanh Toán'
                )}
              </button>
            </div>
          </div>
        </div>

      </div>
    </div>
  )
}
