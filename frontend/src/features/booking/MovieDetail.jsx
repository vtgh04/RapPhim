import React from 'react'
import { useParams, Link } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { motion } from 'framer-motion'
import api from '../../services/api'
import Spinner from '../../shared/Spinner'
import { Calendar, Clock, Film, Landmark, ChevronLeft, Play } from 'lucide-react'

export default function MovieDetail() {
  const { id } = useParams()

  // 1. Fetch movie details
  const { data: movie, isLoading: isMovieLoading, error: movieError } = useQuery({
    queryKey: ['movie', id],
    queryFn: async () => {
      const response = await api.get(`/movies/${id}`)
      return response.data
    }
  })

  // 2. Fetch all showtimes and filter by movieId
  const { data: showtimes = [], isLoading: isShowtimesLoading } = useQuery({
    queryKey: ['showtimes'],
    queryFn: async () => {
      const response = await api.get('/showtimes')
      return response.data
    }
  })

  if (isMovieLoading || isShowtimesLoading) return <Spinner className="py-24" size="lg" />

  if (movieError || !movie) {
    return (
      <div className="text-center py-24 text-red-500 space-y-4">
        <p className="font-bold text-lg">Không tìm thấy thông tin bộ phim.</p>
        <Link to="/" className="inline-flex items-center gap-1 text-sm text-brand hover:underline">
          <ChevronLeft size={16} /> Quay lại trang chủ
        </Link>
      </div>
    )
  }

  // Filter showtimes for this specific movie & sort by start time
  const movieShowtimes = showtimes
    .filter((st) => st.movieId === id && st.status === 'SCHEDULED')
    .sort((a, b) => new Date(a.startTime) - new Date(b.startTime))

  // Group showtimes by Date: YYYY-MM-DD
  const groupedShowtimes = movieShowtimes.reduce((acc, st) => {
    const dateStr = st.startTime.split('T')[0]
    if (!acc[dateStr]) acc[dateStr] = []
    acc[dateStr].push(st)
    return acc
  }, {})

  // Helper to format date display (Vietnam style: Thứ... Ngày DD/MM)
  const formatDateLabel = (dateString) => {
    const d = new Date(dateString)
    const options = { weekday: 'long', day: '2-digit', month: '2-digit' }
    return d.toLocaleDateString('vi-VN', options)
  }

  const getImageUrl = (url) => {
    if (!url) return '';
    if (url.startsWith('http') || url.startsWith('/')) return url;
    return '/' + url;
  }

  return (
    <div className="space-y-8">
      {/* Back link */}
      <Link to="/" className="inline-flex items-center gap-1.5 text-sm font-semibold text-slate-400 hover:text-brand transition-all">
        <ChevronLeft size={18} />
        Quay lại Lịch Chiếu
      </Link>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        
        {/* Left Side: Movie Poster & Metadata */}
        <motion.div
          initial={{ opacity: 0, x: -20 }}
          animate={{ opacity: 1, x: 0 }}
          className="lg:col-span-1 space-y-6"
        >
          <div className="aspect-[2/3] rounded-2xl overflow-hidden shadow-2xl border border-slate-900 bg-slate-950">
            <img
              src={getImageUrl(movie.posterUrl) || 'https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=600&q=80'}
              alt={movie.title}
              className="w-full h-full object-cover"
            />
          </div>

          <div className="glass-panel p-5 rounded-2xl space-y-4 text-sm text-slate-300">
            <h3 className="font-extrabold text-white text-base">Thông Tin Chi Tiết</h3>
            <div className="space-y-2.5">
              <div className="flex justify-between">
                <span className="text-slate-500">Thời lượng</span>
                <span className="font-medium text-slate-200">{movie.durationMins} phút</span>
              </div>
              <div className="flex justify-between">
                <span className="text-slate-500">Định dạng</span>
                <span className="font-medium text-slate-200">{movie.formatMovie || '2D'}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-slate-500">Giới hạn tuổi</span>
                <span className="px-1.5 py-0.5 bg-slate-800 rounded font-bold text-xs text-brand">
                  {movie.rating || 'T13'}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-slate-500">Thể loại</span>
                <span className="font-medium text-slate-200 capitalize">{movie.genre}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-slate-500">Ngôn ngữ</span>
                <span className="font-medium text-slate-200">{movie.language || 'Phụ đề Tiếng Việt'}</span>
              </div>
            </div>
          </div>
        </motion.div>

        {/* Right Side: Description & Grouped Showtimes */}
        <motion.div
          initial={{ opacity: 0, x: 20 }}
          animate={{ opacity: 1, x: 0 }}
          className="lg:col-span-2 space-y-8"
        >
          {/* Header */}
          <div className="space-y-4">
            <h1 className="text-3xl sm:text-4xl font-black text-white leading-tight">
              {movie.title}
            </h1>
            <p className="text-sm text-slate-400 leading-relaxed font-medium">
              {movie.description || 'Chưa có thông tin mô tả chi tiết của bộ phim.'}
            </p>
          </div>

          {/* Showtimes Selector */}
          <div className="space-y-6">
            <h2 className="text-xl font-extrabold tracking-wide flex items-center gap-2 text-white pb-3 border-b border-slate-900">
              <Calendar size={20} className="text-brand" />
              Lịch Chiếu Suất Chiếu
            </h2>

            {Object.keys(groupedShowtimes).length === 0 ? (
              <div className="glass-panel p-8 text-center text-slate-500 rounded-2xl">
                Hiện tại không có suất chiếu nào được lên lịch cho phim này.
              </div>
            ) : (
              <div className="space-y-6">
                {Object.entries(groupedShowtimes).map(([date, list]) => (
                  <div key={date} className="glass-panel p-5 rounded-2xl space-y-4">
                    <h3 className="text-sm font-extrabold text-slate-200 capitalize flex items-center gap-2">
                      <Clock size={15} className="text-brand" />
                      {formatDateLabel(date)}
                    </h3>
                    
                    <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
                      {list.map((st) => {
                        const time = new Date(st.startTime).toLocaleTimeString('vi-VN', {
                          hour: '2-digit',
                          minute: '2-digit',
                          hour12: false
                        })
                        return (
                          <Link
                            key={st.showtimeId}
                            to={`/booking/${st.showtimeId}`}
                            className="flex flex-col items-center justify-center p-3.5 bg-slate-900 border border-slate-800/80 hover:border-brand rounded-xl group transition-all"
                          >
                            <span className="text-lg font-black text-white group-hover:text-brand transition-all">
                              {time}
                            </span>
                            <span className="text-[10px] text-slate-500 flex items-center gap-0.5 mt-1 capitalize font-medium">
                              <Landmark size={10} />
                              {st.hallId}
                            </span>
                          </Link>
                        )
                      })}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </motion.div>

      </div>
    </div>
  )
}
