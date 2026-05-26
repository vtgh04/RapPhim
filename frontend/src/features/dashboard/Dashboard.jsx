import React, { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { motion } from 'framer-motion'
import api from '../../services/api'
import Spinner from '../../shared/Spinner'
import { LayoutDashboard, Film, Calendar, Users, DollarSign, Plus, Trash2, ArrowUpRight, TrendingUp, RefreshCw, BarChart3, PieChart, LineChart, AreaChart } from 'lucide-react'

export default function Dashboard() {
  const [activeTab, setActiveTab] = useState('overview')
  const queryClient = useQueryClient()
  const [revenueChartType, setRevenueChartType] = useState('bar')
  const [hoveredPoint, setHoveredPoint] = useState(null)
  const [hoveredMovieIdx, setHoveredMovieIdx] = useState(null)
  const [selectedMovieId, setSelectedMovieId] = useState(null)
  const [newShowtimeHall, setNewShowtimeHall] = useState('')
  const [newShowtimeStart, setNewShowtimeStart] = useState('')
  const [newShowtimePrice, setNewShowtimePrice] = useState(80000)
  const [showtimeActionTab, setShowtimeActionTab] = useState('list')

  // 1. Fetch Overview Data
  const { data: invoices = [], isLoading: isInvoicesLoading, error: invoicesError } = useQuery({
    queryKey: ['invoices'],
    queryFn: async () => {
      const response = await api.get('/bookings/invoices')
      return response.data
    }
  })

  const { data: movies = [], isLoading: isMoviesLoading, error: moviesError } = useQuery({
    queryKey: ['movies'],
    queryFn: async () => {
      const response = await api.get('/movies')
      return response.data
    }
  })

  const { data: showtimes = [], isLoading: isShowtimesLoading, error: showtimesError } = useQuery({
    queryKey: ['showtimes'],
    queryFn: async () => {
      const response = await api.get('/showtimes')
      return response.data
    }
  })

  const { data: employees = [], isLoading: isEmployeesLoading, error: employeesError } = useQuery({
    queryKey: ['employees'],
    queryFn: async () => {
      const response = await api.get('/employees')
      return response.data
    }
  })

  const { data: cinemaHalls = [], isLoading: isCinemaHallsLoading, error: cinemaHallsError } = useQuery({
    queryKey: ['cinema-halls'],
    queryFn: async () => {
      const response = await api.get('/cinema-halls')
      return response.data
    }
  })

  // 2. Fetch Dashboard analytical API data
  const { data: topMovies = [], error: topMoviesError } = useQuery({
    queryKey: ['dashboard-top-movies'],
    queryFn: async () => {
      const response = await api.get('/dashboard/top-movies')
      return response.data
    }
  })

  const { data: revenueByDay = [], error: revenueByDayError } = useQuery({
    queryKey: ['dashboard-revenue-by-day'],
    queryFn: async () => {
      const response = await api.get('/dashboard/revenue-by-day')
      return response.data
    }
  })

  // 3. Add movie mutation
  const [newMovieTitle, setNewMovieTitle] = useState('')
  const [newMovieGenre, setNewMovieGenre] = useState('')
  const [newMovieDuration, setNewMovieDuration] = useState(120)
  
  const addMovieMutation = useMutation({
    mutationFn: async (payload) => {
      const response = await api.post('/movies', payload)
      return response.data
    },
    onSuccess: () => {
      queryClient.invalidateQueries(['movies'])
      setNewMovieTitle('')
      setNewMovieGenre('')
    }
  })

  const addShowtimeMutation = useMutation({
    mutationFn: async (payload) => {
      const response = await api.post('/showtimes', payload)
      return response.data
    },
    onSuccess: () => {
      queryClient.invalidateQueries(['showtimes'])
      setNewShowtimeStart('')
    },
    onError: (err) => {
      const msg = err.response?.data?.message || err.message || 'Lỗi khi tạo suất chiếu'
      alert(msg)
    }
  })

  const deleteShowtimeMutation = useMutation({
    mutationFn: async (showtimeId) => {
      const response = await api.delete(`/showtimes/${showtimeId}`)
      return response.data
    },
    onSuccess: () => {
      queryClient.invalidateQueries(['showtimes'])
    },
    onError: (err) => {
      const msg = err.response?.data?.message || err.message || 'Lỗi khi xóa suất chiếu'
      alert(msg)
    }
  })

  const handleAddMovie = (e) => {
    e.preventDefault()
    if (!newMovieTitle.trim() || !newMovieGenre.trim()) return
    
    // Add default mock properties
    addMovieMutation.mutate({
      title: newMovieTitle,
      genre: newMovieGenre,
      durationMins: newMovieDuration,
      status: 'ACTIVE',
      formatMovie: '2D',
      rating: 'T13',
      language: 'Phụ đề Tiếng Việt',
      description: 'Mô tả tóm tắt bộ phim mới cập nhật.'
    })
  }

  // Calculate Metrics
  const totalRevenue = Array.isArray(invoices) ? invoices.reduce((sum, inv) => sum + inv.totalAmount, 0) : 0
  const totalTicketsSold = Array.isArray(invoices) ? invoices.reduce((sum, inv) => sum + inv.totalTickets, 0) : 0
  const activeMoviesCount = Array.isArray(movies) ? movies.filter(m => m.status === 'ACTIVE').length : 0
  const employeeCount = Array.isArray(employees) ? employees.length : 0

  // Process Chart Data
  const hasRevenueData = Array.isArray(revenueByDay) && revenueByDay.length > 0
  const sortedRevenueData = hasRevenueData 
    ? [...revenueByDay].sort((a, b) => a[0] - b[0])
    : []

  const maxRevenue = hasRevenueData
    ? Math.max(...sortedRevenueData.map(item => item[1]), 100000)
    : 100000

  const getNiceMax = (max) => {
    if (max <= 100000) return 100000;
    const digits = Math.floor(Math.log10(max));
    const base = Math.pow(10, digits);
    return Math.ceil(max / (base / 2)) * (base / 2);
  }
  const niceMaxRevenue = getNiceMax(maxRevenue)

  const svgWidth = 600
  const svgHeight = 220
  const paddingLeft = 60
  const paddingRight = 20
  const paddingTop = 20
  const paddingBottom = 30

  const chartWidth = svgWidth - paddingLeft - paddingRight
  const chartHeight = svgHeight - paddingTop - paddingBottom

  const getX = (index) => {
    if (sortedRevenueData.length <= 1) return paddingLeft + chartWidth / 2
    return paddingLeft + (index / (sortedRevenueData.length - 1)) * chartWidth
  }

  const getY = (val) => {
    return paddingTop + chartHeight - (val / niceMaxRevenue) * chartHeight
  }

  let lineD = ''
  let areaD = ''

  if (sortedRevenueData.length > 0) {
    const points = sortedRevenueData.map((item, idx) => ({
      x: getX(idx),
      y: getY(item[1])
    }))
    
    lineD = `M ${points[0].x} ${points[0].y} ` + points.slice(1).map(p => `L ${p.x} ${p.y}`).join(' ')
    areaD = `${lineD} L ${points[points.length - 1].x} ${paddingTop + chartHeight} L ${points[0].x} ${paddingTop + chartHeight} Z`
  }

  const barWidth = sortedRevenueData.length > 0
    ? Math.min(25, (chartWidth / sortedRevenueData.length) * 0.6)
    : 15

  const yTicks = [0, 0.25, 0.5, 0.75, 1]
  const formatYLabel = (val) => {
    if (val >= 1000000) return `${(val / 1000000).toFixed(1)}M`
    if (val >= 1000) return `${(val / 1000).toFixed(0)}k`
    return val.toString()
  }

  const xLabelStep = Math.max(1, Math.ceil(sortedRevenueData.length / 8))

  // Donut Chart Calculations
  const totalTopTickets = Array.isArray(topMovies) ? topMovies.reduce((sum, item) => sum + item[2], 0) : 0
  const donutColors = ['#f43f5e', '#3b82f6', '#10b981', '#a855f7', '#f59e0b']
  const donutRadius = 50
  const donutCircumference = 2 * Math.PI * donutRadius

  let accumulatedShare = 0
  const donutSegments = Array.isArray(topMovies) ? topMovies.map((item, idx) => {
    const share = totalTopTickets > 0 ? item[2] / totalTopTickets : 0
    const strokeLength = share * donutCircumference
    const strokeOffset = donutCircumference - (accumulatedShare * donutCircumference)
    accumulatedShare += share
    return {
      title: item[0],
      tickets: item[2],
      share,
      color: donutColors[idx % donutColors.length],
      strokeLength,
      strokeOffset
    }
  }) : []

  // Accumulate errors
  const apiErrors = []
  if (invoicesError) apiErrors.push({ endpoint: '/bookings/invoices', message: invoicesError.message, response: invoicesError.response?.data })
  if (moviesError) apiErrors.push({ endpoint: '/movies', message: moviesError.message, response: moviesError.response?.data })
  if (showtimesError) apiErrors.push({ endpoint: '/showtimes', message: showtimesError.message, response: showtimesError.response?.data })
  if (employeesError) apiErrors.push({ endpoint: '/employees', message: employeesError.message, response: employeesError.response?.data })
  if (topMoviesError) apiErrors.push({ endpoint: '/dashboard/top-movies', message: topMoviesError.message, response: topMoviesError.response?.data })
  if (revenueByDayError) apiErrors.push({ endpoint: '/dashboard/revenue-by-day', message: revenueByDayError.message, response: revenueByDayError.response?.data })
  if (cinemaHallsError) apiErrors.push({ endpoint: '/cinema-halls', message: cinemaHallsError.message, response: cinemaHallsError.response?.data })

  if (isInvoicesLoading || isMoviesLoading || isShowtimesLoading || isEmployeesLoading || isCinemaHallsLoading) {
    return <Spinner className="py-24" size="lg" />
  }

  return (
    <div className="space-y-8">
      {/* Title */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 pb-4 border-b border-slate-900">
        <div>
          <h1 className="text-3xl font-black text-white flex items-center gap-2">
            <LayoutDashboard size={28} className="text-brand" />
            Bảng Quản Trị Hệ Thống
          </h1>
          <p className="text-xs text-slate-400 font-semibold mt-0.5">
            Quản lý doanh thu, phim chiếu, suất chiếu và đội ngũ nhân viên
          </p>
        </div>
      </div>

      {/* API Errors Alert Box */}
      {apiErrors.length > 0 && (
        <div className="glass-panel border-rose-500/20 bg-rose-500/5 p-5 rounded-2xl space-y-3">
          <div className="flex items-center gap-2.5 text-rose-400 font-bold text-sm">
            <span className="w-2 h-2 rounded-full bg-rose-500 animate-pulse" />
            Cảnh báo đồng bộ hóa dữ liệu ({apiErrors.length} API thất bại)
          </div>
          <p className="text-xs text-slate-400 font-medium">
            Có lỗi xảy ra khi tải dữ liệu từ Spring Boot backend. Vui lòng kiểm tra lại quyền đăng nhập hoặc log của server.
          </p>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
            {apiErrors.map((err, idx) => (
              <div key={idx} className="bg-slate-950/60 p-3 rounded-xl border border-slate-900 text-xs">
                <div className="font-extrabold text-slate-300 tracking-wide mb-1">{err.endpoint}</div>
                <div className="text-rose-400 font-semibold mb-1">{err.message}</div>
                {err.response && (
                  <pre className="text-[10px] text-slate-500 bg-slate-900 p-1.5 rounded overflow-x-auto max-h-24">
                    {JSON.stringify(err.response, null, 2)}
                  </pre>
                )}
              </div>
            ))}
          </div>
        </div>
      )}

      {/* KPI Cards Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        <div className="glass-panel p-5 rounded-2xl flex items-center gap-4">
          <div className="p-3.5 bg-emerald-500/10 text-emerald-400 rounded-xl">
            <DollarSign size={24} />
          </div>
          <div>
            <span className="text-[10px] uppercase font-bold text-slate-500 tracking-wider">Doanh Thu Hệ Thống</span>
            <h3 className="text-xl font-black text-white mt-0.5">{totalRevenue.toLocaleString('vi-VN')}đ</h3>
          </div>
        </div>

        <div className="glass-panel p-5 rounded-2xl flex items-center gap-4">
          <div className="p-3.5 bg-brand/10 text-brand rounded-xl">
            <Plus size={24} />
          </div>
          <div>
            <span className="text-[10px] uppercase font-bold text-slate-500 tracking-wider">Tổng Vé Đã Bán</span>
            <h3 className="text-xl font-black text-white mt-0.5">{totalTicketsSold} vé</h3>
          </div>
        </div>

        <div className="glass-panel p-5 rounded-2xl flex items-center gap-4">
          <div className="p-3.5 bg-sky-500/10 text-sky-400 rounded-xl">
            <Film size={24} />
          </div>
          <div>
            <span className="text-[10px] uppercase font-bold text-slate-500 tracking-wider">Phim Đang Chiếu</span>
            <h3 className="text-xl font-black text-white mt-0.5">{activeMoviesCount} phim</h3>
          </div>
        </div>

        <div className="glass-panel p-5 rounded-2xl flex items-center gap-4">
          <div className="p-3.5 bg-amber-500/10 text-amber-400 rounded-xl">
            <Users size={24} />
          </div>
          <div>
            <span className="text-[10px] uppercase font-bold text-slate-500 tracking-wider">Tổng Nhân Sự</span>
            <h3 className="text-xl font-black text-white mt-0.5">{employeeCount} thành viên</h3>
          </div>
        </div>
      </div>

      {/* Tabs Menu */}
      <div className="flex border-b border-slate-900 pb-px">
        {[
          { id: 'overview', label: 'Tổng Quan' },
          { id: 'movies', label: 'Quản Lý Phim' },
          { id: 'showtimes', label: 'Suất Chiếu' },
          { id: 'employees', label: 'Nhân Sự' },
        ].map((tab) => (
          <button
            key={tab.id}
            onClick={() => setActiveTab(tab.id)}
            className={`px-6 py-3 font-extrabold text-sm border-b-2 transition-all ${
              activeTab === tab.id
                ? 'border-brand text-brand'
                : 'border-transparent text-slate-400 hover:text-slate-200'
            }`}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {/* Tab Panels */}
      <div>
        {activeTab === 'overview' && (
          <div className="space-y-8">
            {/* Top row: Interactive SVG Charts */}
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
              {/* Daily Revenue Chart Card (Line/Bar/Area Toggle) */}
              <div className="lg:col-span-2 glass-panel p-6 rounded-3xl space-y-4">
                <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3">
                  <div>
                    <h3 className="font-extrabold text-white text-base flex items-center gap-2">
                      <BarChart3 size={18} className="text-brand" />
                      Thống Kê Doanh Thu 30 Ngày Gần Nhất
                    </h3>
                    <p className="text-[10px] text-slate-500 font-bold uppercase mt-0.5">
                      Biểu đồ doanh thu hàng ngày
                    </p>
                  </div>
                  {/* Chart Type Toggle */}
                  <div className="flex bg-slate-950/60 p-1 rounded-xl border border-slate-900 text-xs font-bold shrink-0">
                    {[
                      { id: 'bar', label: 'Cột', icon: BarChart3 },
                      { id: 'line', label: 'Đường', icon: LineChart },
                      { id: 'area', label: 'Miền', icon: AreaChart }
                    ].map(t => (
                      <button
                        key={t.id}
                        type="button"
                        onClick={() => setRevenueChartType(t.id)}
                        className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg transition-all cursor-pointer ${
                          revenueChartType === t.id
                            ? 'bg-brand text-white shadow-sm'
                            : 'text-slate-400 hover:text-slate-200'
                        }`}
                      >
                        <t.icon size={13} />
                        {t.label}
                      </button>
                    ))}
                  </div>
                </div>

                {/* SVG Chart Container */}
                <div className="w-full bg-slate-950/20 rounded-2xl border border-slate-900/40 p-2 relative">
                  {!hasRevenueData ? (
                    <div className="flex flex-col items-center justify-center h-[220px] text-slate-500 text-xs font-semibold">
                      Chưa có dữ liệu doanh thu
                    </div>
                  ) : (
                    <svg viewBox={`0 0 ${svgWidth} ${svgHeight}`} className="w-full h-auto overflow-visible">
                      <defs>
                        {/* Gradient for Area Chart */}
                        <linearGradient id="areaGrad" x1="0" y1="0" x2="0" y2="1">
                          <stop offset="0%" stopColor="#f43f5e" stopOpacity="0.4" />
                          <stop offset="100%" stopColor="#f43f5e" stopOpacity="0.0" />
                        </linearGradient>
                        {/* Gradient for Column Chart */}
                        <linearGradient id="barGrad" x1="0" y1="0" x2="0" y2="1">
                          <stop offset="0%" stopColor="#f43f5e" stopOpacity="1" />
                          <stop offset="100%" stopColor="#f43f5e" stopOpacity="0.6" />
                        </linearGradient>
                      </defs>

                      {/* Y-Axis Gridlines & Labels */}
                      {yTicks.map((tick, idx) => {
                        const val = niceMaxRevenue * tick;
                        const y = getY(val);
                        return (
                          <g key={idx} className="opacity-40">
                            <line
                              x1={paddingLeft}
                              y1={y}
                              x2={svgWidth - paddingRight}
                              y2={y}
                              stroke="#1e293b"
                              strokeWidth={1}
                              strokeDasharray={tick === 0 ? '0' : '4 4'}
                            />
                            <text
                              x={paddingLeft - 10}
                              y={y + 3}
                              textAnchor="end"
                              className="fill-slate-400 font-extrabold text-[9px]"
                            >
                              {formatYLabel(val)}đ
                            </text>
                          </g>
                        );
                      })}

                      {/* Chart Elements based on selected type */}
                      {revenueChartType === 'area' && (
                        <path d={areaD} fill="url(#areaGrad)" className="transition-all duration-300" />
                      )}

                      {(revenueChartType === 'line' || revenueChartType === 'area') && (
                        <path
                          d={lineD}
                          fill="none"
                          stroke="#f43f5e"
                          strokeWidth={2.5}
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          className="transition-all duration-300"
                        />
                      )}

                      {revenueChartType === 'bar' && (
                        <g>
                          {sortedRevenueData.map((item, idx) => {
                            const x = getX(idx) - barWidth / 2;
                            const y = getY(item[1]);
                            const height = Math.max(2, paddingTop + chartHeight - y);
                            return (
                              <rect
                                key={idx}
                                x={x}
                                y={y}
                                width={barWidth}
                                height={height}
                                rx={3}
                                fill="url(#barGrad)"
                                className="transition-all duration-300 hover:fill-rose-400 cursor-pointer"
                                onMouseEnter={() => setHoveredPoint({ index: idx, x: x + barWidth / 2, y, day: item[0], revenue: item[1] })}
                                onMouseLeave={() => setHoveredPoint(null)}
                              />
                            );
                          })}
                        </g>
                      )}

                      {/* Interactive Circles on Line/Area */}
                      {(revenueChartType === 'line' || revenueChartType === 'area') && (
                        <g>
                          {sortedRevenueData.map((item, idx) => {
                            const x = getX(idx);
                            const y = getY(item[1]);
                            return (
                              <circle
                                key={idx}
                                cx={x}
                                cy={y}
                                r={hoveredPoint?.index === idx ? 5.5 : 3.5}
                                className={`fill-brand stroke-slate-950 stroke-2 cursor-pointer transition-all ${
                                  hoveredPoint?.index === idx ? 'opacity-100' : 'opacity-0 hover:opacity-100'
                                }`}
                                onMouseEnter={() => setHoveredPoint({ index: idx, x, y, day: item[0], revenue: item[1] })}
                                onMouseLeave={() => setHoveredPoint(null)}
                              />
                            );
                          })}
                        </g>
                      )}

                      {/* X-Axis Labels */}
                      {sortedRevenueData.map((item, idx) => {
                        if (idx % xLabelStep !== 0 && idx !== sortedRevenueData.length - 1) return null;
                        const x = getX(idx);
                        const y = paddingTop + chartHeight + 15;
                        return (
                          <text
                            key={idx}
                            x={x}
                            y={y}
                            textAnchor="middle"
                            className="fill-slate-500 font-bold text-[9px] opacity-75"
                          >
                            N.{item[0]}
                          </text>
                        );
                      })}

                      {/* Tooltip Overlay */}
                      {hoveredPoint && (
                        <g>
                          {/* Vertical line indicator */}
                          <line
                            x1={hoveredPoint.x}
                            y1={paddingTop}
                            x2={hoveredPoint.x}
                            y2={paddingTop + chartHeight}
                            stroke="#f43f5e"
                            strokeWidth={1}
                            strokeDasharray="2 2"
                            className="opacity-60"
                          />
                          {/* Tooltip Card */}
                          <rect
                            x={Math.max(paddingLeft, Math.min(svgWidth - paddingRight - 110, hoveredPoint.x - 55))}
                            y={Math.max(5, hoveredPoint.y - 45)}
                            width={110}
                            height={36}
                            rx={8}
                            className="fill-slate-950/95 stroke-brand/35 stroke-[1px] shadow-2xl"
                          />
                          <text
                            x={Math.max(paddingLeft + 55, Math.min(svgWidth - paddingRight - 55, hoveredPoint.x))}
                            y={Math.max(17, hoveredPoint.y - 33)}
                            textAnchor="middle"
                            className="fill-slate-400 font-extrabold text-[8px] uppercase tracking-wider"
                          >
                            Ngày {hoveredPoint.day}
                          </text>
                          <text
                            x={Math.max(paddingLeft + 55, Math.min(svgWidth - paddingRight - 55, hoveredPoint.x))}
                            y={Math.max(30, hoveredPoint.y - 20)}
                            textAnchor="middle"
                            className="fill-brand font-black text-[10px]"
                          >
                            {hoveredPoint.revenue.toLocaleString('vi-VN')}đ
                          </text>
                        </g>
                      )}
                    </svg>
                  )}
                </div>
              </div>

              {/* Donut Chart (Top Movies ticket share) */}
              <div className="lg:col-span-1 glass-panel p-6 rounded-3xl flex flex-col justify-between space-y-4">
                <div>
                  <h3 className="font-extrabold text-white text-base flex items-center gap-2">
                    <PieChart size={18} className="text-brand" />
                    Tỉ Lệ Vé Phim Đã Bán
                  </h3>
                  <p className="text-[10px] text-slate-500 font-bold uppercase mt-0.5">
                    Phần trăm theo top 5 phim bán chạy
                  </p>
                </div>

                {/* Donut Visualization */}
                <div className="flex flex-col sm:flex-row lg:flex-col items-center justify-center gap-6 py-2">
                  {totalTopTickets === 0 ? (
                    <div className="text-slate-500 text-xs font-semibold py-8">Chưa có số liệu</div>
                  ) : (
                    <>
                      {/* SVG Donut */}
                      <div className="relative w-[130px] h-[130px] shrink-0">
                        <svg width="130" height="130" viewBox="0 0 180 180" className="overflow-visible">
                          {/* Background Circle */}
                          <circle
                            cx="90"
                            cy="90"
                            r="60"
                            fill="transparent"
                            stroke="#111827"
                            strokeWidth="14"
                            className="opacity-40"
                          />
                          {/* Data Segments */}
                          {donutSegments.map((seg, idx) => (
                            <circle
                              key={idx}
                              cx="90"
                              cy="90"
                              r="60"
                              fill="transparent"
                              stroke={seg.color}
                              strokeWidth={hoveredMovieIdx === idx ? 18 : 14}
                              strokeDasharray={`${seg.strokeLength} 376.99`}
                              strokeDashoffset={seg.strokeOffset}
                              transform="rotate(-90 90 90)"
                              className="cursor-pointer transition-all duration-300 origin-center"
                              onMouseEnter={() => setHoveredMovieIdx(idx)}
                              onMouseLeave={() => setHoveredMovieIdx(null)}
                            />
                          ))}
                        </svg>
                        {/* Center Content */}
                        <div className="absolute inset-0 flex flex-col items-center justify-center pointer-events-none">
                          <span className="text-2xl font-black text-white">
                            {hoveredMovieIdx !== null ? donutSegments[hoveredMovieIdx].tickets : totalTopTickets}
                          </span>
                          <span className="text-[9px] text-slate-500 font-bold uppercase tracking-wider">
                            {hoveredMovieIdx !== null ? 'vé bán ra' : 'tổng vé top'}
                          </span>
                        </div>
                      </div>

                      {/* Donut Legend */}
                      <div className="flex-1 w-full space-y-2 text-xs">
                        {donutSegments.map((seg, idx) => (
                          <div
                            key={idx}
                            className={`flex items-center justify-between p-1.5 rounded-lg transition-all cursor-pointer ${
                              hoveredMovieIdx === idx ? 'bg-slate-900/40 text-white' : 'text-slate-400 hover:text-slate-300'
                            }`}
                            onMouseEnter={() => setHoveredMovieIdx(idx)}
                            onMouseLeave={() => setHoveredMovieIdx(null)}
                          >
                            <div className="flex items-center gap-2 min-w-0">
                              <span className="w-2.5 h-2.5 rounded-full shrink-0" style={{ backgroundColor: seg.color }} />
                              <span className="font-semibold truncate pr-1">{seg.title}</span>
                            </div>
                            <span className="font-bold text-[10px] text-slate-300 shrink-0">
                              {(seg.share * 100).toFixed(0)}%
                            </span>
                          </div>
                        ))}
                      </div>
                    </>
                  )}
                </div>
              </div>
            </div>

            {/* Bottom row: Lists/Tables */}
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
              {/* Top movies by ticket counts */}
              <div className="lg:col-span-1 glass-panel p-6 rounded-3xl space-y-4">
                <h3 className="font-extrabold text-white text-base flex items-center gap-2">
                  <TrendingUp size={18} className="text-brand" />
                  Phim bán chạy nhất
                </h3>
                
                <div className="space-y-4">
                  {topMovies.length === 0 ? (
                    <p className="text-xs text-slate-500 text-center py-4">Chưa có số liệu thống kê.</p>
                  ) : (
                    topMovies.map((item, idx) => (
                      <div key={idx} className="flex justify-between items-center text-sm border-b border-slate-900/60 pb-3 last:border-0 last:pb-0">
                        <span className="font-bold text-slate-300">{item[0]}</span>
                        <span className="font-bold text-brand bg-brand/10 border border-brand/20 px-2 py-0.5 rounded text-xs shrink-0 ml-3">
                          {item[2]} vé
                        </span>
                      </div>
                    ))
                  )}
                </div>
              </div>

              {/* Recent Billing Log */}
              <div className="lg:col-span-2 glass-panel p-6 rounded-3xl space-y-4">
                <h3 className="font-extrabold text-white text-base flex items-center gap-2">
                  <ArrowUpRight size={18} className="text-brand" />
                  Các hóa đơn gần đây
                </h3>

                <div className="overflow-x-auto">
                  <table className="w-full text-left text-sm">
                    <thead>
                      <tr className="text-slate-500 border-b border-slate-900 text-xs uppercase tracking-wider font-bold">
                        <th className="pb-3">Mã HĐ</th>
                        <th className="pb-3">Thời gian</th>
                        <th className="pb-3">Số vé</th>
                        <th className="pb-3">Tổng tiền</th>
                        <th className="pb-3 text-right">Trạng thái</th>
                      </tr>
                    </thead>
                    <tbody>
                      {invoices.slice(0, 5).map((inv) => (
                        <tr key={inv.invoiceId} className="border-b border-slate-900/40 hover:bg-slate-900/30 transition-all text-slate-300">
                          <td className="py-3 font-bold uppercase">{inv.invoiceId}</td>
                          <td className="py-3 text-xs text-slate-400">
                            {new Date(inv.createdAt).toLocaleString('vi-VN')}
                          </td>
                          <td className="py-3 font-semibold">{inv.totalTickets}</td>
                          <td className="py-3 font-bold text-green-400">{inv.totalAmount.toLocaleString('vi-VN')}đ</td>
                          <td className="py-3 text-right">
                            <span className="px-2 py-0.5 bg-emerald-500/10 border border-emerald-500/25 rounded text-[10px] font-bold text-emerald-400 uppercase tracking-wide">
                              {inv.status}
                            </span>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>
        )}

        {activeTab === 'movies' && (
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            {/* Movies List Table */}
            <div className="lg:col-span-2 glass-panel p-6 rounded-3xl space-y-4">
              <h3 className="font-extrabold text-white text-base">Danh sách tất cả phim</h3>
              
              <div className="overflow-x-auto">
                <table className="w-full text-left text-sm">
                  <thead>
                    <tr className="text-slate-500 border-b border-slate-900 text-xs uppercase tracking-wider font-bold">
                      <th className="pb-3">Tên phim</th>
                      <th className="pb-3">Thể loại</th>
                      <th className="pb-3">Thời lượng</th>
                      <th className="pb-3">Định dạng</th>
                      <th className="pb-3 text-right">Trạng thái</th>
                    </tr>
                  </thead>
                  <tbody>
                    {movies.map((movie) => (
                      <tr key={movie.movieId} className="border-b border-slate-900/40 text-slate-300">
                        <td className="py-3 font-bold">{movie.title}</td>
                        <td className="py-3 text-xs capitalize">{movie.genre}</td>
                        <td className="py-3">{movie.durationMins} phút</td>
                        <td className="py-3 font-semibold">{movie.formatMovie || '2D'}</td>
                        <td className="py-3 text-right">
                          <span className={`px-2 py-0.5 rounded text-[10px] font-bold uppercase tracking-wide ${
                            movie.status === 'ACTIVE' 
                              ? 'bg-emerald-500/10 border border-emerald-500/20 text-emerald-400'
                              : 'bg-slate-800 border border-slate-700 text-slate-500'
                          }`}>
                            {movie.status === 'ACTIVE' ? 'Đang chiếu' : 'Đóng'}
                          </span>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>

            {/* Quick add Movie form */}
            <div className="lg:col-span-1 glass-panel p-6 rounded-3xl space-y-5">
              <h3 className="font-extrabold text-white text-base flex items-center gap-2">
                <Film size={18} className="text-brand" />
                Thêm phim mới nhanh
              </h3>
              
              <form onSubmit={handleAddMovie} className="space-y-4">
                <div>
                  <label className="block text-xs font-bold text-slate-400 uppercase mb-1.5">Tên phim</label>
                  <input
                    type="text"
                    required
                    value={newMovieTitle}
                    onChange={(e) => setNewMovieTitle(e.target.value)}
                    placeholder="Nhập tên phim..."
                    className="w-full px-3.5 py-2.5 bg-slate-900 border border-slate-800 rounded-xl text-slate-200 text-sm"
                  />
                </div>
                <div>
                  <label className="block text-xs font-bold text-slate-400 uppercase mb-1.5">Thể loại</label>
                  <input
                    type="text"
                    required
                    value={newMovieGenre}
                    onChange={(e) => setNewMovieGenre(e.target.value)}
                    placeholder="ví dụ: Hành động, Tâm lý..."
                    className="w-full px-3.5 py-2.5 bg-slate-900 border border-slate-800 rounded-xl text-slate-200 text-sm"
                  />
                </div>
                <div>
                  <label className="block text-xs font-bold text-slate-400 uppercase mb-1.5">Thời lượng (Phút)</label>
                  <input
                    type="number"
                    value={newMovieDuration}
                    onChange={(e) => setNewMovieDuration(parseInt(e.target.value) || 120)}
                    className="w-full px-3.5 py-2.5 bg-slate-900 border border-slate-800 rounded-xl text-slate-200 text-sm"
                  />
                </div>
                
                <button
                  type="submit"
                  disabled={addMovieMutation.isPending}
                  className="w-full py-3 bg-brand hover:bg-brand-hover text-white text-xs font-bold rounded-xl shadow-md transition-all uppercase tracking-wider"
                >
                  {addMovieMutation.isPending ? 'Đang tạo...' : 'Tạo Phim Mới'}
                </button>
              </form>
            </div>
          </div>
        )}

        {activeTab === 'showtimes' && (
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            {/* Left Column: List of Available Movies as Cards */}
            <div className="lg:col-span-1 space-y-4">
              <div className="glass-panel p-5 rounded-3xl space-y-3">
                <h3 className="font-extrabold text-white text-base flex items-center gap-2">
                  <Film size={18} className="text-brand" />
                  Phim Đang Đóng/Mở Chiếu
                </h3>
                <p className="text-[10px] text-slate-500 font-bold uppercase mt-0.5">
                  Chọn phim để quản lý suất chiếu
                </p>
              </div>

              <div className="grid grid-cols-1 gap-4 max-h-[600px] overflow-y-auto pr-1">
                {movies.filter(m => m.status === 'ACTIVE').length === 0 ? (
                  <p className="text-xs text-slate-500 text-center py-8">Không có phim nào đang hoạt động.</p>
                ) : (
                  movies
                    .filter(m => m.status === 'ACTIVE')
                    .map((movie) => {
                      const movieShowtimesCount = showtimes.filter((s) => s.movieId === movie.movieId).length;
                      const isSelected = selectedMovieId === movie.movieId;
                      return (
                        <div
                          key={movie.movieId}
                          onClick={() => {
                            setSelectedMovieId(movie.movieId);
                            // Auto select first hall if hall dropdown is empty
                            if (cinemaHalls.length > 0 && !newShowtimeHall) {
                              setNewShowtimeHall(cinemaHalls[0].hallId);
                            }
                          }}
                          className={`glass-panel p-4 rounded-2xl cursor-pointer transition-all duration-300 flex gap-4 border ${
                            isSelected
                              ? 'border-brand/40 bg-brand/5 shadow-[0_0_15px_rgba(244,63,94,0.1)]'
                              : 'border-slate-900/60 hover:border-slate-800'
                          }`}
                        >
                          {/* Mini Poster Mockup */}
                          <div className="w-14 h-20 rounded-xl bg-slate-900 border border-slate-800 flex items-center justify-center shrink-0 overflow-hidden relative">
                            {movie.posterUrl ? (
                              <img src={movie.posterUrl} alt={movie.title} className="w-full h-full object-cover" />
                            ) : (
                              <Film size={20} className="text-slate-600" />
                            )}
                            <div className="absolute top-1 left-1 px-1 py-0.5 bg-brand/90 rounded text-[8px] font-black text-white">
                              {movie.formatMovie || '2D'}
                            </div>
                          </div>

                          {/* Movie Text Info */}
                          <div className="flex-1 min-w-0 flex flex-col justify-between py-0.5">
                            <div>
                              <h4 className={`font-extrabold text-sm truncate ${isSelected ? 'text-brand' : 'text-slate-200'}`}>
                                {movie.title}
                              </h4>
                              <p className="text-[10px] text-slate-500 font-semibold mt-0.5 truncate capitalize">
                                {movie.genre} • {movie.durationMins} Phút
                              </p>
                            </div>
                            <div className="flex items-center justify-between">
                              <span className="text-[9px] font-black uppercase px-1.5 py-0.5 bg-slate-950/60 text-slate-400 border border-slate-900 rounded">
                                {movie.rating || 'P'}
                              </span>
                              <span className={`text-[10px] font-bold px-2 py-0.5 rounded ${
                                movieShowtimesCount > 0
                                  ? 'bg-sky-500/10 text-sky-400 border border-sky-500/25'
                                  : 'bg-slate-950/60 text-slate-500 border border-slate-900'
                              }`}>
                                {movieShowtimesCount} suất chiếu
                              </span>
                            </div>
                          </div>
                        </div>
                      );
                    })
                )}
              </div>
            </div>

            {/* Right Column: Showtime Management for Selected Movie */}
            <div className="lg:col-span-2 space-y-6">
              {!selectedMovieId ? (
                <div className="glass-panel p-8 rounded-3xl h-[450px] flex flex-col items-center justify-center text-center space-y-4">
                  <div className="p-4 bg-slate-900 border border-slate-800 rounded-full text-slate-600">
                    <Calendar size={32} />
                  </div>
                  <div>
                    <h4 className="font-extrabold text-white text-base">Chưa Chọn Bộ Phim</h4>
                    <p className="text-xs text-slate-500 max-w-sm mt-1">
                      Vui lòng nhấp vào một trong các thẻ phim đang hoạt động bên cạnh để bắt đầu lên lịch hoặc quản lý suất chiếu hiện tại của phim đó.
                    </p>
                  </div>
                </div>
              ) : (
                (() => {
                  const selectedMovie = movies.find(m => m.movieId === selectedMovieId);
                  const selectedMovieShowtimes = showtimes
                    .filter(s => s.movieId === selectedMovieId)
                    .sort((a, b) => new Date(a.startTime) - new Date(b.startTime));
                  
                  return (
                    <div className="glass-panel p-6 rounded-3xl space-y-6">
                      {/* Selected Movie Info Header */}
                      <div className="flex flex-col sm:flex-row gap-5 pb-5 border-b border-slate-900/60">
                        <div className="w-20 h-28 rounded-2xl bg-slate-900 border border-slate-800 overflow-hidden shrink-0">
                          {selectedMovie.posterUrl ? (
                            <img src={selectedMovie.posterUrl} alt={selectedMovie.title} className="w-full h-full object-cover" />
                          ) : (
                            <Film size={24} className="text-slate-600" />
                          )}
                        </div>
                        <div className="flex-1 min-w-0 flex flex-col justify-between py-1">
                          <div>
                            <div className="flex items-center gap-2.5">
                              <h3 className="font-black text-white text-lg truncate">{selectedMovie.title}</h3>
                              <span className="px-1.5 py-0.5 bg-brand/10 border border-brand/20 rounded text-[9px] font-black text-brand uppercase shrink-0">
                                {selectedMovie.formatMovie || '2D'}
                              </span>
                            </div>
                            <p className="text-xs text-slate-400 font-semibold mt-1">
                              Thể loại: <span className="text-slate-300 capitalize">{selectedMovie.genre}</span> | Thời lượng: <span className="text-slate-300">{selectedMovie.durationMins} phút</span>
                            </p>
                            <p className="text-xs text-slate-500 font-medium mt-1 truncate">
                              Ngôn ngữ: {selectedMovie.language || 'Tiếng Việt'} | Độ tuổi: {selectedMovie.rating || 'P'}
                            </p>
                          </div>
                          <p className="text-xs text-slate-500 line-clamp-1 italic mt-1">
                            "{selectedMovie.description || 'Không có mô tả chi tiết.'}"
                          </p>
                        </div>
                      </div>

                      {/* Header Tab Switcher (Showtimes List vs Add Showtime) */}
                      <div className="flex border-b border-slate-900/60 pb-px">
                        <button
                          type="button"
                          onClick={() => setShowtimeActionTab('list')}
                          className={`px-5 py-2.5 font-extrabold text-xs border-b-2 transition-all cursor-pointer ${
                            showtimeActionTab === 'list'
                              ? 'border-brand text-brand'
                              : 'border-transparent text-slate-400 hover:text-slate-300'
                          }`}
                        >
                          Danh sách suất chiếu ({selectedMovieShowtimes.length})
                        </button>
                        <button
                          type="button"
                          onClick={() => setShowtimeActionTab('add')}
                          className={`px-5 py-2.5 font-extrabold text-xs border-b-2 transition-all cursor-pointer ${
                            showtimeActionTab === 'add'
                              ? 'border-brand text-brand'
                              : 'border-transparent text-slate-400 hover:text-slate-300'
                          }`}
                        >
                          Thêm suất chiếu mới
                        </button>
                      </div>

                      {/* Action Panels */}
                      <div>
                        {/* List Tab */}
                        {showtimeActionTab === 'list' && (
                          <div className="space-y-4">
                            {selectedMovieShowtimes.length === 0 ? (
                              <div className="flex flex-col items-center justify-center py-12 text-center text-slate-500 text-xs font-semibold">
                                Phim này chưa có suất chiếu nào được lên lịch.
                                <button
                                  type="button"
                                  onClick={() => setShowtimeActionTab('add')}
                                  className="mt-2.5 px-4 py-2 bg-brand/10 border border-brand/20 text-brand rounded-xl font-bold hover:bg-brand/20 transition-all text-[10px]"
                                >
                                  Lên lịch ngay
                                </button>
                              </div>
                            ) : (
                              <div className="overflow-x-auto">
                                <table className="w-full text-left text-sm">
                                  <thead>
                                    <tr className="text-slate-500 border-b border-slate-900 text-xs uppercase tracking-wider font-bold">
                                      <th className="pb-3">Phòng chiếu</th>
                                      <th className="pb-3">Bắt đầu</th>
                                      <th className="pb-3">Kết thúc</th>
                                      <th className="pb-3">Giá vé</th>
                                      <th className="pb-3">Trạng thái</th>
                                      <th className="pb-3 text-right">Hành động</th>
                                    </tr>
                                  </thead>
                                  <tbody>
                                    {selectedMovieShowtimes.map((st) => {
                                      const hallName = cinemaHalls.find(h => h.hallId === st.hallId)?.name || st.hallId;
                                      return (
                                        <tr key={st.showtimeId} className="border-b border-slate-900/40 hover:bg-slate-950/20 text-slate-300 transition-all">
                                          <td className="py-3 font-bold uppercase">{hallName}</td>
                                          <td className="py-3 text-xs text-slate-400">
                                            {new Date(st.startTime).toLocaleString('vi-VN')}
                                          </td>
                                          <td className="py-3 text-xs text-slate-500">
                                            {new Date(st.endTime).toLocaleString('vi-VN')}
                                          </td>
                                          <td className="py-3 font-semibold text-brand">{st.basePrice.toLocaleString('vi-VN')}đ</td>
                                          <td className="py-3">
                                            <span className={`px-2 py-0.5 rounded text-[9px] font-bold uppercase tracking-wide ${
                                              st.status === 'SCHEDULED' 
                                                ? 'bg-sky-500/10 border border-sky-500/20 text-sky-400'
                                                : st.status === 'ONGOING'
                                                ? 'bg-amber-500/10 border border-amber-500/20 text-amber-400'
                                                : st.status === 'COMPLETED'
                                                ? 'bg-emerald-500/10 border border-emerald-500/20 text-emerald-400'
                                                : 'bg-slate-800 border border-slate-700 text-slate-500'
                                            }`}>
                                              {st.status}
                                            </span>
                                          </td>
                                          <td className="py-3 text-right">
                                            <button
                                              type="button"
                                              disabled={deleteShowtimeMutation.isPending}
                                              onClick={() => {
                                                if (window.confirm(`Bạn có chắc chắn muốn xóa suất chiếu ${st.showtimeId} của phim này không?`)) {
                                                  deleteShowtimeMutation.mutate(st.showtimeId);
                                                }
                                              }}
                                              className="p-1.5 bg-rose-500/10 border border-rose-500/20 hover:bg-rose-500/20 text-rose-400 rounded-lg transition-all cursor-pointer disabled:opacity-50"
                                            >
                                              <Trash2 size={14} />
                                            </button>
                                          </td>
                                        </tr>
                                      );
                                    })}
                                  </tbody>
                                </table>
                              </div>
                            )}
                          </div>
                        )}

                        {/* Add Tab */}
                        {showtimeActionTab === 'add' && (
                          <form
                            onSubmit={(e) => {
                              e.preventDefault();
                              if (!newShowtimeHall || !newShowtimeStart || !newShowtimePrice) {
                                alert('Vui lòng điền đầy đủ thông tin');
                                return;
                              }
                              
                              const startDate = new Date(newShowtimeStart);
                              const endDate = new Date(startDate.getTime() + (selectedMovie.durationMins || 120) * 60 * 1000);
                              
                              const pad = (n) => n.toString().padStart(2, '0');
                              const startTimeISO = newShowtimeStart + ":00";
                              const endTimeISO = `${endDate.getFullYear()}-${pad(endDate.getMonth() + 1)}-${pad(endDate.getDate())}T${pad(endDate.getHours())}:${pad(endDate.getMinutes())}:00`;
                              
                              addShowtimeMutation.mutate({
                                movieId: selectedMovieId,
                                hallId: newShowtimeHall,
                                startTime: startTimeISO,
                                endTime: endTimeISO,
                                basePrice: parseFloat(newShowtimePrice),
                                status: 'SCHEDULED'
                              });
                            }}
                            className="space-y-4 max-w-md"
                          >
                            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                              <div>
                                <label className="block text-xs font-bold text-slate-400 uppercase mb-1.5">Phòng chiếu</label>
                                <select
                                  required
                                  value={newShowtimeHall}
                                  onChange={(e) => setNewShowtimeHall(e.target.value)}
                                  className="w-full px-3.5 py-2.5 bg-slate-900 border border-slate-800 rounded-xl text-slate-200 text-sm focus:border-brand/40"
                                >
                                  {cinemaHalls.map((hall) => (
                                    <option key={hall.hallId} value={hall.hallId}>
                                      {hall.name} ({hall.hallType} - {hall.totalRows * hall.totalCols} Ghế)
                                    </option>
                                  ))}
                                </select>
                              </div>
                              <div>
                                <label className="block text-xs font-bold text-slate-400 uppercase mb-1.5">Giá vé cơ bản (VND)</label>
                                <input
                                  type="number"
                                  required
                                  min="0"
                                  value={newShowtimePrice}
                                  onChange={(e) => setNewShowtimePrice(e.target.value)}
                                  className="w-full px-3.5 py-2.5 bg-slate-900 border border-slate-800 rounded-xl text-slate-200 text-sm focus:border-brand/40"
                                />
                              </div>
                            </div>

                            <div>
                              <label className="block text-xs font-bold text-slate-400 uppercase mb-1.5">Thời gian bắt đầu</label>
                              <input
                                type="datetime-local"
                                required
                                value={newShowtimeStart}
                                onChange={(e) => setNewShowtimeStart(e.target.value)}
                                className="w-full px-3.5 py-2.5 bg-slate-900 border border-slate-800 rounded-xl text-slate-200 text-sm focus:border-brand/40"
                              />
                              <p className="text-[10px] text-slate-500 font-semibold mt-1">
                                * Thời gian kết thúc sẽ được tính toán tự động dựa trên thời lượng phim ({selectedMovie.durationMins} phút).
                              </p>
                            </div>

                            <button
                              type="submit"
                              disabled={addShowtimeMutation.isPending}
                              className="w-full sm:w-auto px-6 py-3 bg-brand hover:bg-brand-hover text-white text-xs font-bold rounded-xl shadow-md transition-all uppercase tracking-wider disabled:opacity-50"
                            >
                              {addShowtimeMutation.isPending ? 'Đang thêm...' : 'Lên Lịch Suất Chiếu'}
                            </button>
                          </form>
                        )}
                      </div>
                    </div>
                  );
                })()
              )}
            </div>
          </div>
        )}

        {activeTab === 'employees' && (
          <div className="glass-panel p-6 rounded-3xl space-y-4">
            <h3 className="font-extrabold text-white text-base flex items-center gap-2">
              <Users size={18} className="text-brand" />
              Đội ngũ nhân sự hệ thống
            </h3>
            <div className="overflow-x-auto">
              <table className="w-full text-left text-sm">
                <thead>
                  <tr className="text-slate-500 border-b border-slate-900 text-xs uppercase tracking-wider font-bold">
                    <th className="pb-3">Mã NV</th>
                    <th className="pb-3">Họ và tên</th>
                    <th className="pb-3">Tên đăng nhập</th>
                    <th className="pb-3">Vai trò</th>
                    <th className="pb-3">Email</th>
                    <th className="pb-3 text-right">Trạng thái</th>
                  </tr>
                </thead>
                <tbody>
                  {employees.map((emp) => (
                    <tr key={emp.employeeId} className="border-b border-slate-900/40 text-slate-300">
                      <td className="py-3 font-bold uppercase">{emp.employeeId}</td>
                      <td className="py-3 font-semibold">{emp.fullName}</td>
                      <td className="py-3 text-slate-400">{emp.username}</td>
                      <td className="py-3 font-bold text-xs uppercase text-slate-400">{emp.role}</td>
                      <td className="py-3 text-xs text-slate-500">{emp.email || 'Chưa cung cấp'}</td>
                      <td className="py-3 text-right">
                        <span className="px-2 py-0.5 bg-emerald-500/10 border border-emerald-500/25 rounded text-[10px] font-bold text-emerald-400 uppercase tracking-wide">
                          {emp.status}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}
