import React from 'react'
import { Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import { Clock, Star } from 'lucide-react'
import { useUIStore } from '../../store/uiStore'
import { useQuery } from '@tanstack/react-query'
import api from '../../services/api'

function MiniStars({ rating }) {
  return (
    <span className="flex items-center gap-0.5">
      {[1, 2, 3, 4, 5].map((s) => (
        <Star
          key={s}
          size={9}
          className={s <= Math.round(rating) ? 'text-amber-400 fill-amber-400' : 'text-slate-700'}
        />
      ))}
      <span className="text-[10px] text-amber-400 font-bold ml-0.5">{rating.toFixed(1)}</span>
    </span>
  )
}

export default function MovieCard({ movie, idx }) {
  const { t } = useUIStore()

  const { data: reviewData } = useQuery({
    queryKey: ['reviews', movie.movieId],
    queryFn: async () => {
      const res = await api.get(`/reviews/${movie.movieId}`)
      return res.data
    },
    staleTime: 1000 * 60 * 5,
  })

  const getImageUrl = (url) => {
    if (!url) return '';
    if (url.startsWith('http') || url.startsWith('/')) return url;
    return '/' + url;
  }

  const avgRating = reviewData?.averageRating

  return (
    <motion.div
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
          loading="lazy"
          className="w-full h-full object-cover group-hover:scale-105 transition-all duration-500"
        />
        <div className="absolute inset-0 bg-gradient-to-t from-slate-950 via-slate-950/20 to-transparent opacity-0 group-hover:opacity-100 transition-all duration-300 flex items-end justify-center p-4">
          <Link
            to={`/movie/${movie.movieId}`}
            className="w-full text-center py-2.5 bg-brand hover:bg-brand-hover text-white font-bold rounded-xl text-xs uppercase tracking-wider shadow-lg transition-all"
          >
            {t('viewDetails')}
          </Link>
        </div>
        <span className="absolute top-3 left-3 px-2 py-0.5 bg-slate-950/80 backdrop-blur-md rounded text-xs font-bold text-slate-300">
          {movie.rating || 'T13'}
        </span>
      </div>

      {/* Detail frame */}
      <div className="p-4 flex flex-col flex-grow justify-between space-y-3">
        <div className="space-y-1">
          <h3 
            className="font-extrabold text-slate-100 group-hover:text-brand transition-all line-clamp-2" 
            title={movie.title}
          >
            <Link to={`/movie/${movie.movieId}`}>{movie.title}</Link>
          </h3>
          <p className="text-xs text-slate-400 capitalize">{movie.genre}</p>
        </div>

        <div className="space-y-2">
          {avgRating && (
            <div className="flex items-center">
              <MiniStars rating={avgRating} />
            </div>
          )}
          <div className="flex items-center justify-between text-xs text-slate-400 pt-2 border-t border-slate-900">
            <span className="flex items-center gap-1">
              <Clock size={12} />
              {movie.durationMins} {t('mins')}
            </span>
            <span className="text-slate-300 font-semibold">{movie.formatMovie || '2D'}</span>
          </div>
        </div>
      </div>
    </motion.div>
  )
}
