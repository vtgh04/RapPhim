import React, { useState, useEffect, useRef } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { motion, AnimatePresence } from 'framer-motion'
import api from '../../services/api'
import { Play, Calendar, Clock, Film, Star, Search, SlidersHorizontal, EyeOff } from 'lucide-react'
import { useUIStore } from '../../store/uiStore'
import MovieCard from './MovieCard'

function MovieCardSkeleton() {
  return (
    <div className="glass-card rounded-2xl overflow-hidden flex flex-col h-full animate-pulse border border-slate-900/60">
      <div className="aspect-[2/3] bg-slate-900" />
      <div className="p-4 space-y-4 flex-grow flex flex-col justify-between">
        <div className="space-y-2">
          <div className="h-4 bg-slate-900 rounded w-3/4" />
          <div className="h-3 bg-slate-900 rounded w-1/2" />
        </div>
        <div className="flex justify-between pt-3 border-t border-slate-900/40">
          <div className="h-3 bg-slate-900 rounded w-1/4" />
          <div className="h-3 bg-slate-900 rounded w-1/6" />
        </div>
      </div>
    </div>
  )
}

export default function MovieList() {
  const { t, language } = useUIStore()
  const [searchParams, setSearchParams] = useSearchParams()

  const searchQuery = searchParams.get('q') || ''
  const activeGenre = searchParams.get('genre') || 'ALL'
  const activeFormat = searchParams.get('format') || 'ALL'
  const sortBy = searchParams.get('sort') || 'newest'
  const activeStatus = searchParams.get('status') || 'ACTIVE'

  const activeGenres = activeGenre === 'ALL' || !activeGenre ? [] : activeGenre.split(',')
  const [isGenreOpen, setIsGenreOpen] = useState(false)
  const genreRef = useRef(null)
  const listRef = useRef(null)
  const isFirstLoad = useRef(true)

  const [searchText, setSearchText] = useState(searchQuery)

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (genreRef.current && !genreRef.current.contains(e.target)) {
        setIsGenreOpen(false)
      }
    }
    document.addEventListener('mousedown', handleClickOutside)
    return () => document.removeEventListener('mousedown', handleClickOutside)
  }, [])

  useEffect(() => {
    if (isFirstLoad.current) {
      isFirstLoad.current = false
      return
    }
    if (listRef.current) {
      listRef.current.scrollIntoView({ behavior: 'smooth' })
    }
  }, [searchQuery, activeGenre, activeFormat, sortBy, activeStatus])

  // Sync search text input with searchParams when page loads
  useEffect(() => {
    setSearchText(searchQuery)
  }, [searchQuery])

  // Debounced search logic (300ms)
  useEffect(() => {
    const handler = setTimeout(() => {
      updateParam('q', searchText)
    }, 300)
    return () => clearTimeout(handler)
  }, [searchText])

  const updateParam = (key, value) => {
    const params = new URLSearchParams(searchParams)
    if (value && value !== 'ALL') {
      params.set(key, value)
    } else {
      params.delete(key)
    }
    setSearchParams(params)
  }

  const getImageUrl = (url) => {
    if (!url) return '';
    if (url.startsWith('http')) return url;
    const baseUrl = import.meta.env.VITE_API_URL || '';
    const cleanedUrl = url.startsWith('/') ? url : '/' + url;
    return baseUrl + cleanedUrl;
  }

  const { data: movies = [], isLoading, error } = useQuery({
    queryKey: ['movies'],
    queryFn: async () => {
      const response = await api.get('/movies')
      return response.data
    }
  })

  // Extract unique genres dynamically from fetched movies list
  const allGenres = movies.flatMap((m) => m.genre ? m.genre.split(',').map((s) => s.trim()) : [])
  const genres = ['ALL', ...new Set(allGenres)].filter(Boolean)
  const formats = ['ALL', '2D', '3D', 'IMAX']

  // Filters logic
  const filteredMovies = movies.filter((movie) => {
    const matchStatus = movie.status === activeStatus
    const matchSearch = movie.title.toLowerCase().includes(searchQuery.toLowerCase())
    const matchGenre = activeGenres.length === 0 || activeGenres.some((g) =>
      movie.genre && movie.genre.toLowerCase().includes(g.toLowerCase())
    )
    const matchFormat = activeFormat === 'ALL' || (movie.formatMovie || '2D') === activeFormat
    return matchStatus && matchSearch && matchGenre && matchFormat
  })

  // Sort logic
  const sortedMovies = [...filteredMovies].sort((a, b) => {
    if (sortBy === 'newest') {
      // Sort by movie ID descending (higher ID assumed newer)
      return b.movieId.localeCompare(a.movieId)
    }
    if (sortBy === 'duration_asc') {
      return a.durationMins - b.durationMins
    }
    if (sortBy === 'duration_desc') {
      return b.durationMins - a.durationMins
    }
    return 0
  })

  const featuredMovie = movies.find((movie) => movie.status === 'ACTIVE')

  if (error) {
    return (
      <div className="text-center py-24 text-rose-500 space-y-4">
        <p className="font-extrabold text-lg">Đã xảy ra lỗi khi tải danh sách phim.</p>
        <p className="text-sm font-medium">{error.message}</p>
      </div>
    )
  }

  return (
    <div className="space-y-12">
      {/* Featured Banner Section */}
      {featuredMovie && !isLoading && !searchQuery && activeGenre === 'ALL' && activeFormat === 'ALL' && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ duration: 0.8 }}
          className="relative h-[65vh] rounded-3xl overflow-hidden shadow-2xl border border-slate-800/40"
        >
          <div className="absolute inset-0 bg-cover bg-center" style={{ backgroundImage: `url(${getImageUrl(featuredMovie.posterUrl) || 'https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=1200&q=80'})` }}>
            <div className="absolute inset-0 bg-gradient-to-t from-slate-950 via-slate-950/60 to-transparent" />
            <div className="absolute inset-0 bg-gradient-to-r from-slate-950 via-slate-950/40 to-transparent" />
          </div>

          <div className="absolute bottom-0 left-0 max-w-2xl p-8 sm:p-12 space-y-4">
            <div className="inline-flex items-center gap-1.5 px-3 py-1 bg-brand text-white font-extrabold text-xs tracking-wider rounded-md uppercase">
              <Star size={12} fill="white" />
              {t('featuredTitle')}
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
                {featuredMovie.durationMins} {t('mins')}
              </span>
              <span className="capitalize">{featuredMovie.genre}</span>
            </div>

            <p className="text-slate-400 text-sm sm:text-base line-clamp-3 leading-relaxed font-medium">
              {featuredMovie.description || (language === 'vi' ? 'Không có mô tả chi tiết cho bộ phim này.' : 'No detailed description available for this movie.')}
            </p>

            <div className="pt-2">
              <Link
                to={`/movie/${featuredMovie.movieId}`}
                className="inline-flex items-center gap-2.5 px-6 py-3 bg-brand hover:bg-brand-hover text-white font-bold rounded-xl shadow-lg shadow-brand/20 transition-all text-sm uppercase tracking-wider"
              >
                <Play size={16} fill="white" />
                {t('bookNow')}
              </Link>
            </div>
          </div>
        </motion.div>
      )}

      {/* Filter / Search Tool Bar */}
      <div ref={listRef} className="space-y-6 scroll-mt-24">
        <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 pb-4 border-b border-dark-border/50">
          <h2 className="text-2xl font-black tracking-wide flex items-center gap-2 text-white">
            <Film size={24} className="text-brand" />
            {t('moviesListTitle')}
          </h2>
          
          <div className="flex bg-dark-card/85 p-1 rounded-xl border border-dark-border/50">
            <button
              onClick={() => updateParam('status', 'ACTIVE')}
              className={`px-4 py-2 rounded-lg text-xs font-bold transition-all cursor-pointer ${
                activeStatus === 'ACTIVE'
                  ? 'bg-brand text-white shadow-md'
                  : 'text-dark-text/60 hover:text-brand'
              }`}
            >
              {t('nowShowing')}
            </button>
            <button
              onClick={() => updateParam('status', 'INACTIVE')}
              className={`px-4 py-2 rounded-lg text-xs font-bold transition-all cursor-pointer ${
                activeStatus === 'INACTIVE'
                  ? 'bg-brand text-white shadow-md'
                  : 'text-dark-text/60 hover:text-brand'
              }`}
            >
              {t('comingSoon')}
            </button>
          </div>
        </div>

        {/* Toolbar: Search input, Genre filters, Formats, Sorting */}
        <div className="grid grid-cols-1 lg:grid-cols-4 gap-4 items-center">
          {/* Search Input */}
          <div className="relative lg:col-span-1">
            <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-500" size={16} />
            <input
              type="text"
              value={searchText}
              onChange={(e) => setSearchText(e.target.value)}
              placeholder={language === 'vi' ? 'Tìm kiếm tên phim...' : 'Search movies...'}
              className="w-full pl-10 pr-4 py-3 bg-slate-900 border border-slate-800/80 rounded-xl text-slate-200 text-sm focus:border-brand/40 focus:bg-slate-950 transition-all font-semibold"
            />
          </div>

          {/* Genre Selection Dropdown */}
          <div className="relative" ref={genreRef}>
            <button
              type="button"
              onClick={() => setIsGenreOpen(!isGenreOpen)}
              style={{
                backgroundColor: 'var(--color-input)',
                borderColor: 'var(--color-input-border)',
                color: 'var(--color-text)'
              }}
              className="w-full flex items-center justify-between px-3.5 py-3 border rounded-xl text-xs font-bold focus:border-brand/40 cursor-pointer shadow-sm"
            >
              <span className="truncate pr-2">
                {activeGenres.length === 0 
                  ? (language === 'vi' ? 'Thể loại: Tất cả' : 'Genre: All')
                  : `${language === 'vi' ? 'Thể loại' : 'Genre'}: ${activeGenres.join(', ')}`}
              </span>
              <span className="text-slate-500 text-[9px] shrink-0">▼</span>
            </button>

            {isGenreOpen && (
              <div 
                style={{
                  backgroundColor: 'var(--color-panel-solid)',
                  borderColor: 'var(--color-border)',
                  color: 'var(--color-text)'
                }}
                className="absolute left-0 right-0 mt-2 p-3 border rounded-2xl shadow-2xl z-30 space-y-2 max-h-60 overflow-y-auto"
              >
                {/* Option All */}
                <label className="flex items-center gap-2.5 p-1.5 hover:bg-slate-100 dark:hover:bg-slate-800/60 rounded-lg cursor-pointer text-xs font-bold select-none">
                  <input
                    type="checkbox"
                    checked={activeGenres.length === 0}
                    onChange={() => {
                      updateParam('genre', 'ALL')
                    }}
                    className="w-4 h-4 rounded border-slate-300 dark:border-slate-700 text-brand focus:ring-brand/20 accent-brand cursor-pointer"
                  />
                  <span>{language === 'vi' ? 'Tất cả' : 'All'}</span>
                </label>

                <hr className="border-slate-100 dark:border-slate-800" />

                {genres.filter(g => g !== 'ALL').map((genre) => {
                  const isChecked = activeGenres.includes(genre)
                  return (
                    <label 
                      key={genre} 
                      className="flex items-center gap-2.5 p-1.5 hover:bg-slate-100 dark:hover:bg-slate-800/60 rounded-lg cursor-pointer text-xs font-bold select-none"
                    >
                      <input
                        type="checkbox"
                        checked={isChecked}
                        onChange={() => {
                          let nextGenres
                          if (isChecked) {
                            nextGenres = activeGenres.filter(g => g !== genre)
                          } else {
                            nextGenres = [...activeGenres, genre]
                          }
                          const val = nextGenres.length === 0 ? 'ALL' : nextGenres.join(',')
                          updateParam('genre', val)
                        }}
                        className="w-4 h-4 rounded border-slate-300 dark:border-slate-700 text-brand focus:ring-brand/20 accent-brand cursor-pointer"
                      />
                      <span className="capitalize">{genre}</span>
                    </label>
                  )
                })}
              </div>
            )}
          </div>

          {/* Format Selection Dropdown */}
          <div>
            <select
              value={activeFormat}
              onChange={(e) => updateParam('format', e.target.value)}
              className="w-full px-3.5 py-3 bg-slate-900 border border-slate-800 rounded-xl text-slate-200 text-xs font-bold focus:border-brand/40 cursor-pointer"
            >
              <option value="ALL">{language === 'vi' ? 'Định dạng: Tất cả' : 'Format: All'}</option>
              {formats.filter(f => f !== 'ALL').map(f => (
                <option key={f} value={f}>{f}</option>
              ))}
            </select>
          </div>

          {/* Sort Selection Dropdown */}
          <div>
            <select
              value={sortBy}
              onChange={(e) => updateParam('sort', e.target.value)}
              className="w-full px-3.5 py-3 bg-slate-900 border border-slate-800 rounded-xl text-slate-200 text-xs font-bold focus:border-brand/40 cursor-pointer"
            >
              <option value="newest">{language === 'vi' ? 'Sắp xếp: Mới nhất' : 'Sort: Newest'}</option>
              <option value="duration_asc">{language === 'vi' ? 'Sắp xếp: Thời lượng tăng' : 'Sort: Duration (shortest)'}</option>
              <option value="duration_desc">{language === 'vi' ? 'Sắp xếp: Thời lượng giảm' : 'Sort: Duration (longest)'}</option>
            </select>
          </div>
        </div>
      </div>

      {/* Movies Grid / Shimmers / Empty States */}
      {isLoading ? (
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
          {Array.from({ length: 8 }).map((_, idx) => (
            <MovieCardSkeleton key={idx} />
          ))}
        </div>
      ) : sortedMovies.length === 0 ? (
        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          className="glass-panel p-16 rounded-3xl text-center flex flex-col items-center justify-center space-y-4 max-w-lg mx-auto"
        >
          <div className="p-4 bg-slate-900 border border-slate-800 rounded-full text-slate-500 shadow-inner">
            <EyeOff size={36} />
          </div>
          <div>
            <h4 className="font-extrabold text-white text-base">Không Tìm Thấy Kết Quả</h4>
            <p className="text-xs text-slate-500 max-w-xs mx-auto mt-1">
              Không có bộ phim nào phù hợp với bộ lọc và từ khóa tìm kiếm hiện tại của bạn.
            </p>
          </div>
          <button
            onClick={() => setSearchParams({ status: activeStatus })}
            className="px-5 py-2.5 bg-slate-900 border border-slate-800 text-slate-300 hover:text-white rounded-xl text-xs font-bold transition-all hover:border-slate-700 cursor-pointer"
          >
            Đặt lại bộ lọc
          </button>
        </motion.div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
          <AnimatePresence mode="popLayout">
            {sortedMovies.map((movie, idx) => (
              <MovieCard key={movie.movieId} movie={movie} idx={idx} />
            ))}
          </AnimatePresence>
        </div>
      )}
    </div>
  )
}
