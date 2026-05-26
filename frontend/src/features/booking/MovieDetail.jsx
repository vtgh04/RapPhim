import React, { useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { motion, AnimatePresence } from 'framer-motion'
import api from '../../services/api'
import Spinner from '../../components/ui/Spinner'
import { Calendar, Clock, Film, Landmark, ChevronLeft, Play, Star, MessageSquare, Send } from 'lucide-react'
import { useAuthStore } from '../../store/authStore'
import { useNotificationStore } from '../../store/notificationStore'

// Star Rating Component
function StarRating({ value, onChange, readOnly = false }) {
  const [hovered, setHovered] = useState(0)

  return (
    <div className="flex gap-0.5">
      {[1, 2, 3, 4, 5].map((star) => (
        <button
          key={star}
          type={readOnly ? 'button' : 'button'}
          disabled={readOnly}
          onClick={() => !readOnly && onChange && onChange(star)}
          onMouseEnter={() => !readOnly && setHovered(star)}
          onMouseLeave={() => !readOnly && setHovered(0)}
          className={`transition-all ${readOnly ? 'cursor-default' : 'cursor-pointer hover:scale-110'}`}
        >
          <Star
            size={readOnly ? 13 : 20}
            className={`transition-colors ${
              star <= (hovered || value)
                ? 'text-amber-400 fill-amber-400'
                : 'text-slate-600'
            }`}
          />
        </button>
      ))}
    </div>
  )
}

// Single review card
function ReviewCard({ review }) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 8 }}
      animate={{ opacity: 1, y: 0 }}
      className="glass-panel p-4 rounded-xl space-y-2"
    >
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2">
          <div className="w-7 h-7 rounded-full bg-brand/20 border border-brand/30 flex items-center justify-center text-brand font-black text-xs uppercase">
            {review.userId.charAt(0)}
          </div>
          <span className="text-xs font-bold text-slate-300">{review.userId}</span>
        </div>
        <div className="flex items-center gap-2">
          <StarRating value={review.rating} readOnly />
          <span className="text-[10px] text-slate-500">
            {new Date(review.createdAt).toLocaleDateString('vi-VN')}
          </span>
        </div>
      </div>
      {review.comment && (
        <p className="text-xs text-slate-400 leading-relaxed pl-9">{review.comment}</p>
      )}
    </motion.div>
  )
}

export default function MovieDetail() {
  const { id } = useParams()
  const { user } = useAuthStore()
  const { push: pushNotification } = useNotificationStore()
  const queryClient = useQueryClient()

  const [reviewRating, setReviewRating] = useState(0)
  const [reviewComment, setReviewComment] = useState('')
  const [reviewSubmitted, setReviewSubmitted] = useState(false)

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

  // 3. Fetch Reviews
  const { data: reviewData, isLoading: isReviewsLoading } = useQuery({
    queryKey: ['reviews', id],
    queryFn: async () => {
      const response = await api.get(`/reviews/${id}`)
      return response.data
    }
  })

  // Submit Review Mutation
  const submitReviewMutation = useMutation({
    mutationFn: async (payload) => {
      const response = await api.post('/reviews', payload)
      return response.data
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['reviews', id] })
      setReviewRating(0)
      setReviewComment('')
      setReviewSubmitted(true)
      pushNotification({
        type: 'success',
        title: 'Đánh giá thành công!',
        message: `Cảm ơn bạn đã đánh giá "${movie?.title}".`,
      })
    },
    onError: (err) => {
      pushNotification({
        type: 'error',
        title: 'Gửi đánh giá thất bại',
        message: err?.response?.data?.error || 'Vui lòng thử lại.',
      })
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

  // Helper to format date display (Vietnam style)
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

  const avgRating = reviewData?.averageRating
  const totalReviews = reviewData?.totalReviews || 0
  const reviews = reviewData?.reviews || []

  const handleSubmitReview = (e) => {
    e.preventDefault()
    if (reviewRating === 0) return
    submitReviewMutation.mutate({
      movieId: id,
      rating: reviewRating,
      comment: reviewComment.trim() || null,
    })
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

          {/* Rating Summary */}
          <div className="glass-panel p-4 rounded-2xl space-y-2 text-center">
            {isReviewsLoading ? (
              <div className="h-8 bg-slate-900 rounded animate-pulse" />
            ) : avgRating ? (
              <>
                <div className="text-4xl font-black text-amber-400">{avgRating.toFixed(1)}</div>
                <div className="flex justify-center">
                  <StarRating value={Math.round(avgRating)} readOnly />
                </div>
                <p className="text-xs text-slate-500">{totalReviews} đánh giá</p>
              </>
            ) : (
              <p className="text-xs text-slate-500 py-2">Chưa có đánh giá</p>
            )}
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

        {/* Right Side: Description, Showtimes & Reviews */}
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

          {/* ===== Reviews Section ===== */}
          <div className="space-y-6">
            <h2 className="text-xl font-extrabold tracking-wide flex items-center gap-2 text-white pb-3 border-b border-slate-900">
              <MessageSquare size={20} className="text-brand" />
              Đánh Giá & Bình Luận
              {totalReviews > 0 && (
                <span className="text-xs text-slate-500 font-normal">({totalReviews})</span>
              )}
            </h2>

            {/* Submit Review Form */}
            {user ? (
              reviewSubmitted ? (
                <div className="glass-panel p-4 rounded-xl text-center text-sm text-emerald-400 font-semibold">
                  ✅ Cảm ơn bạn đã gửi đánh giá!
                </div>
              ) : (
                <form onSubmit={handleSubmitReview} className="glass-panel p-5 rounded-2xl space-y-4">
                  <p className="text-sm font-bold text-slate-200">Viết đánh giá của bạn</p>
                  <div className="space-y-1">
                    <p className="text-xs text-slate-500">Xếp hạng <span className="text-rose-400">*</span></p>
                    <StarRating value={reviewRating} onChange={setReviewRating} />
                  </div>
                  <textarea
                    value={reviewComment}
                    onChange={(e) => setReviewComment(e.target.value)}
                    placeholder="Chia sẻ cảm nhận về bộ phim... (không bắt buộc)"
                    rows={3}
                    maxLength={1000}
                    className="w-full px-3.5 py-3 bg-slate-900 border border-slate-800 rounded-xl text-slate-200 text-sm focus:border-brand/40 transition-all resize-none font-medium placeholder-slate-600"
                  />
                  <div className="flex justify-end">
                    <button
                      type="submit"
                      disabled={reviewRating === 0 || submitReviewMutation.isPending}
                      className="flex items-center gap-2 px-5 py-2.5 bg-brand hover:bg-brand-hover disabled:bg-slate-800 disabled:text-slate-500 text-white font-bold rounded-xl text-sm transition-all cursor-pointer disabled:cursor-not-allowed"
                    >
                      <Send size={14} />
                      {submitReviewMutation.isPending ? 'Đang gửi...' : 'Gửi đánh giá'}
                    </button>
                  </div>
                </form>
              )
            ) : (
              <div className="glass-panel p-4 rounded-xl text-sm text-slate-400 text-center">
                <Link to="/login" className="text-brand hover:underline font-semibold">Đăng nhập</Link> để viết đánh giá.
              </div>
            )}

            {/* Reviews List */}
            {isReviewsLoading ? (
              <div className="space-y-3">
                {[1, 2].map((i) => (
                  <div key={i} className="glass-panel p-4 rounded-xl animate-pulse space-y-2">
                    <div className="h-4 bg-slate-900 rounded w-1/3" />
                    <div className="h-3 bg-slate-900 rounded w-full" />
                  </div>
                ))}
              </div>
            ) : reviews.length === 0 ? (
              <div className="text-center py-8 text-slate-500 text-sm">
                Chưa có đánh giá nào. Hãy là người đầu tiên!
              </div>
            ) : (
              <div className="space-y-3">
                <AnimatePresence>
                  {reviews.map((review) => (
                    <ReviewCard key={review.reviewId} review={review} />
                  ))}
                </AnimatePresence>
              </div>
            )}
          </div>

        </motion.div>
      </div>
    </div>
  )
}
