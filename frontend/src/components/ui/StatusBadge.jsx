import React from 'react'

export default function StatusBadge({ status, className = '' }) {
  const normalizedStatus = (status || '').toUpperCase()

  let themeClass = 'bg-slate-800 border-slate-700 text-slate-500'
  let label = status

  switch (normalizedStatus) {
    case 'ACTIVE':
    case 'CONFIRMED':
    case 'COMPLETED':
    case 'PAID':
    case 'VALID':
      themeClass = 'bg-emerald-500/10 border-emerald-500/20 text-emerald-400 dark:border-emerald-500/15'
      label = status === 'ACTIVE' ? 'Đang chiếu / Hoạt động' : status
      break
    case 'SCHEDULED':
    case 'ONGOING':
      themeClass = 'bg-sky-500/10 border-sky-500/20 text-sky-400 dark:border-sky-500/15'
      label = status === 'SCHEDULED' ? 'Đã lên lịch' : 'Đang chiếu'
      break
    case 'INACTIVE':
    case 'RETIRED':
    case 'CANCELLED':
    case 'CLOSED':
      themeClass = 'bg-rose-500/10 border-rose-500/20 text-rose-400 dark:border-rose-500/15'
      label = status === 'INACTIVE' ? 'Dừng chiếu' : status === 'RETIRED' ? 'Đã nghỉ việc' : 'Đã hủy'
      break
    case 'PENDING':
    case 'LOCKED':
    case 'HELD':
      themeClass = 'bg-amber-500/10 border-amber-500/20 text-amber-400 dark:border-amber-500/15'
      label = status === 'PENDING' ? 'Chờ duyệt' : status
      break
    default:
      break
  }

  return (
    <span
      className={`inline-flex items-center px-2 py-0.5 border rounded text-[10px] font-black uppercase tracking-wide transition-all ${themeClass} ${className}`}
    >
      {label}
    </span>
  )
}
