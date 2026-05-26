import React from 'react'
import Spinner from './Spinner'

export default function DataTable({
  headers = [],
  data = [],
  isLoading = false,
  emptyMessage = 'Không có dữ liệu hiển thị.',
  renderRow,
  className = ''
}) {
  return (
    <div className={`overflow-x-auto ${className}`}>
      <table className="w-full text-left text-sm">
        <thead>
          <tr className="text-slate-500 border-b border-slate-900 text-xs uppercase tracking-wider font-bold">
            {headers.map((header, idx) => (
              <th key={idx} className="pb-3 px-2 first:pl-0 last:pr-0">
                {header}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {isLoading ? (
            <tr>
              <td colSpan={headers.length} className="py-8">
                <Spinner size="md" />
              </td>
            </tr>
          ) : data.length === 0 ? (
            <tr>
              <td colSpan={headers.length} className="py-8 text-center text-xs text-slate-500 font-semibold">
                {emptyMessage}
              </td>
            </tr>
          ) : (
            data.map((item, idx) => renderRow(item, idx))
          )}
        </tbody>
      </table>
    </div>
  )
}
