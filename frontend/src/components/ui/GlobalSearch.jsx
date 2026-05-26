import React, { useState, useEffect, useRef } from 'react'
import { useNavigate } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { Search, Film, Calendar, Receipt, X, CornerDownLeft } from 'lucide-react'
import api from '../../services/api'
import { useUIStore } from '../../store/uiStore'

export default function GlobalSearch() {
  const [isOpen, setIsOpen] = useState(false)
  const [query, setQuery] = useState('')
  const navigate = useRef(useNavigate()).current
  const { t, language } = useUIStore()

  // 1. Fetch search indices (movies, showtimes, invoices)
  const { data: movies = [] } = useQuery({
    queryKey: ['movies-search'],
    queryFn: async () => {
      const response = await api.get('/movies')
      return response.data
    },
    enabled: isOpen
  })

  const { data: showtimes = [] } = useQuery({
    queryKey: ['showtimes-search'],
    queryFn: async () => {
      const response = await api.get('/showtimes')
      return response.data
    },
    enabled: isOpen
  })

  const { data: invoices = [] } = useQuery({
    queryKey: ['invoices-search'],
    queryFn: async () => {
      const response = await api.get('/bookings/invoices')
      return response.data
    },
    enabled: isOpen
  })

  // Keyboard shortcut listener
  useEffect(() => {
    const handleKeyDown = (e) => {
      if ((e.metaKey || e.ctrlKey) && e.key === 'k') {
        e.preventDefault()
        setIsOpen((prev) => !prev)
        setQuery('')
      }
      if (e.key === 'Escape') {
        setIsOpen(false)
      }
    }
    window.addEventListener('keydown', handleKeyDown)
    return () => window.removeEventListener('keydown', handleKeyDown)
  }, [])

  // Filter search matches
  const searchResults = []
  if (query.trim().length > 0) {
    const term = query.toLowerCase()

    // Match movies
    movies
      .filter((m) => m.title.toLowerCase().includes(term) && m.status === 'ACTIVE')
      .slice(0, 3)
      .forEach((m) => {
        searchResults.push({
          type: 'movie',
          id: m.movieId,
          title: m.title,
          subtitle: `${m.genre} • ${m.durationMins} ${t('mins')}`,
          url: `/movie/${m.movieId}`
        })
      })

    // Match showtimes (by movie name or hall name)
    showtimes
      .filter((st) => {
        const movie = movies.find((m) => m.movieId === st.movieId)
        const movieMatch = movie ? movie.title.toLowerCase().includes(term) : false
        const hallMatch = st.hallId.toLowerCase().includes(term)
        return (movieMatch || hallMatch) && st.status === 'SCHEDULED'
      })
      .slice(0, 3)
      .forEach((st) => {
        const movie = movies.find((m) => m.movieId === st.movieId)
        const dateStr = new Date(st.startTime).toLocaleString(language === 'vi' ? 'vi-VN' : 'en-US', {
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit'
        })
        searchResults.push({
          type: 'showtime',
          id: st.showtimeId,
          title: movie ? movie.title : `Suất chiếu ${st.showtimeId}`,
          subtitle: `${dateStr} • ${st.hallId.toUpperCase()} • ${st.basePrice.toLocaleString('vi-VN')}đ`,
          url: `/booking/${st.showtimeId}`
        })
      })

    // Match invoices
    invoices
      .filter((inv) => inv.invoiceId.toLowerCase().includes(term))
      .slice(0, 3)
      .forEach((inv) => {
        searchResults.push({
          type: 'invoice',
          id: inv.invoiceId,
          title: `Hóa đơn ${inv.invoiceId}`,
          subtitle: `${inv.totalTickets} vé • ${inv.totalAmount.toLocaleString('vi-VN')}đ • ${inv.paymentMethod}`,
          url: `/invoices?search=${inv.invoiceId}`
        })
      })
  }

  const handleSelect = (url) => {
    setIsOpen(false)
    navigate(url)
  }

  return (
    <>
      {/* Keyboard Hint Banner in Layout Header */}
      <button
        onClick={() => setIsOpen(true)}
        className="hidden md:flex items-center gap-2 px-3 py-1.5 bg-slate-950/60 border border-slate-900 rounded-xl hover:border-slate-800 text-slate-500 hover:text-slate-400 text-xs font-semibold transition-all cursor-pointer"
      >
        <Search size={13} />
        <span>{language === 'vi' ? 'Tìm kiếm...' : 'Search...'}</span>
        <kbd className="px-1.5 py-0.5 bg-slate-905 border border-slate-800 rounded font-mono text-[9px]">Ctrl+K</kbd>
      </button>

      {isOpen && (
        <div className="fixed inset-0 z-55 flex items-start justify-center pt-[15vh] p-4">
          {/* Backdrop */}
          <div
            onClick={() => setIsOpen(false)}
            className="absolute inset-0 bg-slate-950/80 backdrop-blur-sm"
          />

          {/* Search Box */}
          <div className="relative z-10 w-full max-w-lg bg-slate-900/90 border border-slate-800 rounded-3xl shadow-2xl overflow-hidden backdrop-blur-md flex flex-col">
            {/* Input Bar */}
            <div className="flex items-center gap-3 px-4 py-4 border-b border-slate-900">
              <Search className="text-slate-500 shrink-0" size={18} />
              <input
                type="text"
                autoFocus
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                placeholder={language === 'vi' ? 'Tìm phim, suất chiếu, mã hóa đơn...' : 'Search movies, showtimes, invoices...'}
                className="flex-grow bg-transparent text-white text-sm font-semibold border-0 outline-none focus:ring-0 placeholder-slate-500"
              />
              <button
                onClick={() => setIsOpen(false)}
                className="p-1 rounded-full text-slate-500 hover:text-white hover:bg-slate-800 transition-all cursor-pointer shrink-0"
              >
                <X size={15} />
              </button>
            </div>

            {/* Results Panel */}
            <div className="max-h-[350px] overflow-y-auto p-2">
              {query.trim().length === 0 ? (
                <div className="py-8 text-center text-slate-500 text-xs font-semibold">
                  {language === 'vi' ? 'Nhập tên phim, phòng chiếu hoặc mã hóa đơn để tìm...' : 'Type to search movies, cinema halls, or invoices...'}
                </div>
              ) : searchResults.length === 0 ? (
                <div className="py-8 text-center text-slate-500 text-xs font-semibold">
                  {language === 'vi' ? 'Không tìm thấy kết quả nào' : 'No results found'}
                </div>
              ) : (
                <div className="space-y-1">
                  {searchResults.map((item, idx) => {
                    let Icon = Film
                    if (item.type === 'showtime') Icon = Calendar
                    if (item.type === 'invoice') Icon = Receipt

                    return (
                      <div
                        key={`${item.type}-${item.id}`}
                        onClick={() => handleSelect(item.url)}
                        className="flex items-center justify-between p-3 rounded-2xl hover:bg-slate-800/60 cursor-pointer group transition-all"
                      >
                        <div className="flex items-center gap-3 min-w-0">
                          <div className="p-2 bg-slate-950 border border-slate-900 text-slate-400 group-hover:text-brand group-hover:border-brand/20 rounded-xl transition-all">
                            <Icon size={16} />
                          </div>
                          <div className="min-w-0">
                            <span className="block text-sm font-bold text-white group-hover:text-brand transition-all truncate">
                              {item.title}
                            </span>
                            <span className="block text-[10px] text-slate-500 font-semibold uppercase mt-0.5 truncate">
                              {item.subtitle}
                            </span>
                          </div>
                        </div>

                        <div className="opacity-0 group-hover:opacity-100 flex items-center gap-1 text-[10px] text-slate-500 font-bold transition-all pr-2">
                          <span>{language === 'vi' ? 'Mở' : 'Open'}</span>
                          <CornerDownLeft size={10} />
                        </div>
                      </div>
                    )
                  })}
                </div>
              )}
            </div>

            {/* Footer hints */}
            <div className="px-4 py-2.5 bg-slate-950/40 border-t border-slate-900 flex justify-between items-center text-[10px] text-slate-500 font-bold uppercase tracking-wider">
              <span>esc {language === 'vi' ? 'để đóng' : 'to close'}</span>
              <span>ctrl+k {language === 'vi' ? 'để toggle' : 'to toggle'}</span>
            </div>
          </div>
        </div>
      )}
    </>
  )
}
