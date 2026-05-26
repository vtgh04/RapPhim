import React, { useState } from 'react'
import { useParams, useNavigate, Link } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { motion } from 'framer-motion'
import api from '../../services/api'
import Spinner from '../../components/ui/Spinner'
import { ChevronLeft, Landmark, Wifi, WifiOff } from 'lucide-react'
import { useRealtimeSeats } from '../../hooks/useRealtimeSeats'

export default function SeatGrid() {
  const { showtimeId } = useParams()
  const navigate = useNavigate()
  const [selectedSeats, setSelectedSeats] = useState([])

  // 1. Fetch Showtime info
  const { data: showtime, isLoading: isShowtimeLoading } = useQuery({
    queryKey: ['showtime', showtimeId],
    queryFn: async () => {
      const response = await api.get(`/showtimes/${showtimeId}`)
      return response.data
    }
  })

  const hallId = showtime?.hallId
  const movieId = showtime?.movieId
  const basePrice = showtime?.basePrice || 0

  // 2. Fetch Movie info
  const { data: movie, isLoading: isMovieLoading } = useQuery({
    queryKey: ['movie', movieId],
    queryFn: async () => {
      const response = await api.get(`/movies/${movieId}`)
      return response.data
    },
    enabled: !!movieId
  })

  // 3. Fetch Hall seats layout
  const { data: seats = [], isLoading: isSeatsLoading } = useQuery({
    queryKey: ['hall-seats', hallId],
    queryFn: async () => {
      const response = await api.get(`/cinema-halls/${hallId}/seats`)
      return response.data
    },
    enabled: !!hallId
  })

  // 4. Fetch initial Showseat status map
  const { data: initialStatusMap = {}, isLoading: isStatusLoading } = useQuery({
    queryKey: ['showseat-statuses', showtimeId],
    queryFn: async () => {
      const response = await api.get(`/showtimes/${showtimeId}/seats`)
      return response.data
    },
    refetchInterval: 15000, // polling fallback
  })

  // 5. Realtime WebSocket updates (overlays on top of REST data)
  const { statusMap, connected, broadcastSeatUpdate } = useRealtimeSeats(showtimeId, initialStatusMap)

  if (isShowtimeLoading || isMovieLoading || isSeatsLoading || isStatusLoading) {
    return <Spinner className="py-24" size="lg" />
  }

  if (!showtime || !movie) {
    return (
      <div className="text-center py-24 text-red-500 space-y-4">
        <p className="font-bold text-lg">Không tìm thấy thông tin suất chiếu.</p>
        <Link to="/" className="inline-flex items-center gap-1 text-sm text-brand hover:underline">
          <ChevronLeft size={16} /> Quay lại trang chủ
        </Link>
      </div>
    )
  }

  // Format start time
  const startTimeFormatted = new Date(showtime.startTime).toLocaleString('vi-VN', {
    weekday: 'long',
    day: '2-digit',
    month: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    hour12: false
  })

  // Group seats by rowChar
  const groupedSeats = seats.reduce((acc, seat) => {
    const row = seat.rowChar
    if (!acc[row]) acc[row] = []
    acc[row].push(seat)
    return acc
  }, {})

  // Sort rows alphabetically and columns numerically
  const sortedRows = Object.keys(groupedSeats).sort()
  sortedRows.forEach(row => {
    groupedSeats[row].sort((a, b) => a.colNumber - b.colNumber)
  })

  const handleSeatClick = (seat) => {
    const seatId = seat.seatId
    const status = statusMap[seatId]

    if (status !== 'AVAILABLE' || seat.broken) return

    if (selectedSeats.some(s => s.seatId === seatId)) {
      setSelectedSeats(prev => prev.filter(s => s.seatId !== seatId))
      broadcastSeatUpdate(seatId, 'AVAILABLE') // Unlock for others
    } else {
      setSelectedSeats(prev => [...prev, seat])
      broadcastSeatUpdate(seatId, 'LOCKED') // Lock for others
    }
  }

  const computeTotalPrice = () => {
    return selectedSeats.reduce((sum, seat) => {
      const price = basePrice * seat.seatFactor
      return sum + price
    }, 0)
  }

  const handleProceedCheckout = () => {
    if (selectedSeats.length === 0) return
    const seatIdsParam = selectedSeats.map(s => s.seatId).join(',')
    navigate(`/checkout?showtimeId=${showtimeId}&seats=${encodeURIComponent(seatIdsParam)}`)
  }

  return (
    <div className="space-y-8">
      {/* Detail header bar */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 pb-4 border-b border-slate-900">
        <div>
          <Link to={`/movie/${movieId}`} className="inline-flex items-center gap-1 text-xs text-slate-400 hover:text-brand transition-all mb-1 font-semibold">
            <ChevronLeft size={14} /> Quay lại trang thông tin phim
          </Link>
          <h1 className="text-2xl font-black text-white">{movie.title}</h1>
          <p className="text-xs text-slate-400 font-semibold mt-0.5">
            {startTimeFormatted} — <span className="capitalize">{showtime.hallId}</span>
          </p>
        </div>
        {/* Realtime indicator */}
        <div className={`flex items-center gap-1.5 px-3 py-1.5 rounded-full text-[10px] font-bold border ${
          connected
            ? 'bg-emerald-500/10 border-emerald-500/20 text-emerald-400'
            : 'bg-amber-500/10 border-amber-500/20 text-amber-400'
        }`}>
          {connected ? <Wifi size={11} /> : <WifiOff size={11} />}
          {connected ? 'LIVE' : 'Polling 15s'}
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-4 gap-8">
        {/* Left Side: Seat Map */}
        <div className="lg:col-span-3 flex flex-col items-center justify-center p-6 glass-panel rounded-3xl relative overflow-x-auto min-w-0">
          
          {/* Cinema Screen shape */}
          <div className="w-[80%] max-w-lg mb-12 text-center">
            <div className="h-2 bg-gradient-to-r from-brand via-rose-500 to-brand rounded-full blur-sm shadow-xl shadow-brand/40" />
            <span className="text-[10px] uppercase font-bold text-slate-500 tracking-widest block mt-2">MÀN HÌNH CHIẾU</span>
          </div>

          {/* Seat Grid Layout */}
          <div className="flex flex-col gap-3 min-w-[500px] select-none pb-6">
            {sortedRows.map((row) => (
              <div key={row} className="flex items-center gap-4 justify-center">
                {/* Row label left */}
                <span className="w-5 text-center font-bold text-sm text-slate-600">{row}</span>
                
                {/* Seats row */}
                <div className="flex gap-2">
                  {groupedSeats[row].map((seat) => {
                    const seatId = seat.seatId
                    const status = statusMap[seatId]
                    const isSelected = selectedSeats.some(s => s.seatId === seatId)
                    const isBroken = seat.broken
                    
                    let bgClass = 'bg-slate-900 hover:bg-slate-800 border-slate-800 hover:border-slate-600 text-slate-300'
                    let cursorClass = 'cursor-pointer'
                    
                    if (isBroken) {
                      bgClass = 'bg-slate-950 border-slate-950 text-slate-700 opacity-30 cursor-not-allowed'
                    } else if (status === 'BOOKED' || status === 'LOCKED') {
                      bgClass = 'bg-brand/20 border-brand/10 text-brand cursor-not-allowed'
                    } else if (isSelected) {
                      bgClass = 'bg-brand text-white border-brand shadow-lg shadow-brand/20'
                    } else if (seat.seatType === 'VIP') {
                      bgClass = 'bg-amber-950/20 hover:bg-amber-900/30 border-amber-600/40 hover:border-amber-500 text-amber-400'
                    }

                    return (
                      <div
                        key={seatId}
                        onClick={() => handleSeatClick(seat)}
                        className={`w-9 h-9 border rounded-xl flex items-center justify-center text-xs font-bold select-none transition-all ${bgClass} ${cursorClass}`}
                        title={`${seatId} (${seat.seatType === 'VIP' ? 'VIP' : 'Thường'}) - factor x${seat.seatFactor}`}
                      >
                        {seat.colNumber}
                      </div>
                    )
                  })}
                </div>

                {/* Row label right */}
                <span className="w-5 text-center font-bold text-sm text-slate-600">{row}</span>
              </div>
            ))}
          </div>

          {/* Seat Grid Legends */}
          <div className="flex flex-wrap gap-5 justify-center mt-6 pt-6 border-t border-slate-900 w-full text-xs font-bold text-slate-400">
            <div className="flex items-center gap-2">
              <div className="w-4 h-4 border border-slate-800 bg-slate-900 rounded" />
              <span>Ghế thường</span>
            </div>
            <div className="flex items-center gap-2">
              <div className="w-4 h-4 border border-amber-600/40 bg-amber-950/20 rounded" />
              <span>Ghế VIP</span>
            </div>
            <div className="flex items-center gap-2">
              <div className="w-4 h-4 border border-brand bg-brand rounded" />
              <span>Ghế đang chọn</span>
            </div>
            <div className="flex items-center gap-2">
              <div className="w-4 h-4 border border-brand/10 bg-brand/20 rounded" />
              <span>Ghế đã bán</span>
            </div>
            <div className="flex items-center gap-2">
              <div className="w-4 h-4 border border-slate-950 bg-slate-950 opacity-30 rounded" />
              <span>Ghế hỏng</span>
            </div>
          </div>
        </div>

        {/* Right Side: Selected Summary */}
        <div className="lg:col-span-1 space-y-6">
          <div className="glass-panel p-6 rounded-3xl space-y-5 flex flex-col justify-between h-full">
            <div className="space-y-4">
              <h3 className="font-extrabold text-white text-base pb-3 border-b border-slate-900">Chi Tiết Đặt Vé</h3>
              
              <div className="space-y-3.5 text-sm text-slate-300">
                <div className="flex flex-col gap-1">
                  <span className="text-slate-500 text-xs">Suất chiếu</span>
                  <span className="font-bold text-slate-100">{movie.title}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-slate-500">Giờ chiếu</span>
                  <span className="font-semibold text-slate-200">
                    {new Date(showtime.startTime).toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' })}
                  </span>
                </div>
                <div className="flex justify-between">
                  <span className="text-slate-500">Phòng chiếu</span>
                  <span className="font-semibold text-slate-200 uppercase">{showtime.hallId}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-slate-500">Ghế chọn</span>
                  <span className="font-bold text-brand uppercase">
                    {selectedSeats.length > 0
                      ? selectedSeats.map(s => s.seatId).join(', ')
                      : 'Chưa chọn'}
                  </span>
                </div>
              </div>
            </div>

            <div className="space-y-4 pt-4 border-t border-slate-900">
              <div className="flex justify-between items-end">
                <span className="text-sm text-slate-500 font-semibold mb-1">Tạm tính</span>
                <span className="text-2xl font-black text-brand tracking-tight">
                  {computeTotalPrice().toLocaleString('vi-VN')}đ
                </span>
              </div>

              <button
                onClick={handleProceedCheckout}
                disabled={selectedSeats.length === 0}
                className="w-full py-4 bg-brand hover:bg-brand-hover disabled:bg-slate-900 text-white disabled:text-slate-600 font-extrabold rounded-xl shadow-lg shadow-brand/10 disabled:shadow-none hover:shadow-brand/20 transition-all text-sm uppercase tracking-wider disabled:cursor-not-allowed"
              >
                Tiếp Tục Thanh Toán
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
