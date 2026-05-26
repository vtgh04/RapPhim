import React, { useState } from 'react'
import { Link } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { motion } from 'framer-motion'
import api from '../../services/api'
import Spinner from '../../shared/Spinner'
import { Play, Calendar, Clock, Film, Star } from 'lucide-react'

export default function MovieList() {
  const [filter, setFilter] = useState('ACTIVE')

  const getImageUrl = (url) => {
    if (!url) return '';
    if (url.startsWith('http') || url.startsWith('/')) return url;
    return '/' + url;
  }

  const { data: movies = [], isLoading, error } = useQuery({
    queryKey: ['movies'],
    queryFn: async () => {
      const response = await api.get('/movies')
      return response.data
    }
  })

  const filteredMovies = movies.filter(movie => movie.status === filter)
  const featuredMovie = movies.find(movie => movie.status === 'ACTIVE')

  if (isLoading) return <Spinner className="py-24" size="lg" />

  if (error) {
    return (
      <div className="text-center py-24 text-red-500">
        <p className="font-bold">Đã xảy ra lỗi khi tải danh sách phim.</p>
        <p className="text-sm mt-1">{error.message}</p>
      </div>
    )
  }

  return (
    <div className="space-y-12">
      {/* Featured Banner Section */}
      {featuredMovie && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ duration: 0.8 }}
          className="relative h-[65vh] rounded-3xl overflow-hidden shadow-2xl border border-slate-800/40"
        >
          {/* Background image & overlay */}
          <div className="absolute inset-0 bg-cover bg-center" style={{ backgroundImage: `url(${getImageUrl(featuredMovie.posterUrl) || 'https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=1200&q=80'})` }}>
            <div className="absolute inset-0 bg-gradient-to-t from-slate-950 via-slate-950/60 to-transparent" />
            <div className="absolute inset-0 bg-gradient-to-r from-slate-950 via-slate-950/40 to-transparent" />
          </div>

          {/* Banner content */}
          <div className="absolute bottom-0 left-0 max-w-2xl p-8 sm:p-12 space-y-4">
            <div className="inline-flex items-center gap-1.5 px-3 py-1 bg-brand text-white font-extrabold text-xs tracking-wider rounded-md uppercase">
              <Star size={12} fill="white" />
              Nổi Bật Nhất
            </div>
            
            <h1 className="text-4xl sm:text-5xl font-black tracking-tight text-white line-clamp-2">
              {featuredMovie.title}
            </h1>

            <div className="flex flex-wrap items-center gap-4 text-sm text-slate-300">
              <span className="px-2 py-0.5 bg-slate-800 rounded font-semibold text-slate-200">
                {featuredMovie.rating || 'T13'}
              </span>
              <span className="flex items-center gap-1">
                <Clock size={15} />
                {featuredMovie.durationMins} phút
              </span>
              <span className="capitalize">{featuredMovie.genre}</span>
            </div>

            <p className="text-slate-400 text-sm sm:text-base line-clamp-3 leading-relaxed">
              {featuredMovie.description || 'Không có mô tả chi tiết cho bộ phim này.'}
            </p>

            <div className="pt-2">
              <Link
                to={`/movie/${featuredMovie.movieId}`}
                className="inline-flex items-center gap-2.5 px-6 py-3 bg-brand hover:bg-brand-hover text-white font-bold rounded-xl shadow-lg shadow-brand/20 transition-all text-sm uppercase tracking-wider"
              >
                <Play size={16} fill="white" />
                Đặt Vé Ngay
              </Link>
            </div>
          </div>
        </motion.div>
      )}

      {/* Filter Tabs */}
      <div className="flex items-center justify-between border-b border-slate-900 pb-4">
        <h2 className="text-2xl font-extrabold tracking-wide flex items-center gap-2 text-white">
          <Film size={22} className="text-brand" />
          Danh Sách Phim
        </h2>
        <div className="flex bg-slate-900/80 p-1 rounded-xl border border-slate-800/40">
          <button
            onClick={() => setFilter('ACTIVE')}
            className={`px-4 py-2 rounded-lg text-xs font-bold transition-all ${
              filter === 'ACTIVE'
                ? 'bg-brand text-white shadow-md'
                : 'text-slate-400 hover:text-slate-200'
            }`}
          >
            Đang Chiếu
          </button>
          <button
            onClick={() => setFilter('INACTIVE')}
            className={`px-4 py-2 rounded-lg text-xs font-bold transition-all ${
              filter === 'INACTIVE'
                ? 'bg-brand text-white shadow-md'
                : 'text-slate-400 hover:text-slate-200'
            }`}
          >
            Sắp Chiếu / Dừng
          </button>
        </div>
      </div>

      {/* Movies Grid */}
      {filteredMovies.length === 0 ? (
        <div className="text-center py-24 text-slate-500">
          Không tìm thấy phim nào trong chuyên mục này.
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
          {filteredMovies.map((movie, idx) => (
            <motion.div
              key={movie.movieId}
              initial={{ opacity: 0, y: 15 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: idx * 0.05, duration: 0.4 }}
              className="glass-card rounded-2xl overflow-hidden flex flex-col h-full group"
            >
              {/* Poster frame */}
              <div className="relative aspect-[2/3] bg-slate-950 overflow-hidden">
                <img
                  src={getImageUrl(movie.posterUrl) || 'https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=600&q=80'}
                  alt={movie.title}
                  className="w-full h-full object-cover group-hover:scale-105 transition-all duration-500"
                />
                <div className="absolute inset-0 bg-gradient-to-t from-slate-950 via-slate-950/20 to-transparent opacity-0 group-hover:opacity-100 transition-all duration-300 flex items-end justify-center p-4">
                  <Link
                    to={`/movie/${movie.movieId}`}
                    className="w-full text-center py-2.5 bg-brand text-white font-bold rounded-xl text-xs uppercase tracking-wider shadow-lg transition-all"
                  >
                    Xem Chi Tiết
                  </Link>
                </div>
                <span className="absolute top-3 left-3 px-2 py-0.5 bg-slate-950/80 backdrop-blur-md rounded text-xs font-bold text-slate-300">
                  {movie.rating || 'T13'}
                </span>
              </div>

              {/* Detail frame */}
              <div className="p-4 flex flex-col flex-grow justify-between space-y-3">
                <div className="space-y-1">
                  <h3 className="font-extrabold text-slate-100 group-hover:text-brand transition-all line-clamp-1">
                    <Link to={`/movie/${movie.movieId}`}>{movie.title}</Link>
                  </h3>
                  <p className="text-xs text-slate-400 capitalize">{movie.genre}</p>
                </div>

                <div className="flex items-center justify-between text-xs text-slate-400 pt-2 border-t border-slate-900">
                  <span className="flex items-center gap-1">
                    <Clock size={12} />
                    {movie.durationMins} phút
                  </span>
                  <span className="text-slate-300 font-semibold">{movie.formatMovie || '2D'}</span>
                </div>
              </div>
            </motion.div>
          ))}
        </div>
      )}
    </div>
  )
}
