import React from 'react'
import { AlertOctagon, RefreshCw, Home } from 'lucide-react'

export default class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props)
    this.state = { hasError: false, error: null }
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error }
  }

  componentDidCatch(error, errorInfo) {
    console.error("ErrorBoundary caught an error", error, errorInfo)
  }

  handleReset = () => {
    this.setState({ hasError: false, error: null })
    window.location.href = '/'
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="min-h-screen w-full bg-[#030712] relative text-[#f8fafc] font-sans flex items-center justify-center p-6">
          {/* Circuit board styling / Radial glow */}
          <div
            className="absolute inset-0 z-0 pointer-events-none"
            style={{
              backgroundImage: 'radial-gradient(circle 500px at 50% 50%, rgba(239, 68, 68, 0.15), transparent)',
            }}
          />
          
          <div className="relative z-10 w-full max-w-md p-8 bg-slate-900/80 border border-slate-800 backdrop-blur-md rounded-3xl text-center space-y-6 shadow-2xl">
            <div className="flex items-center justify-center w-16 h-16 bg-rose-500/10 text-rose-500 rounded-full mx-auto">
              <AlertOctagon size={36} />
            </div>

            <div className="space-y-2">
              <h2 className="text-2xl font-black text-white uppercase tracking-wide">Đã Xảy Ra Lỗi Hệ Thống</h2>
              <p className="text-xs text-slate-400 font-medium">
                Component ứng dụng đã bị crash không mong muốn.
              </p>
            </div>

            {this.state.error && (
              <div className="p-3.5 bg-slate-950/60 border border-slate-900 rounded-xl text-left">
                <span className="block text-[10px] text-slate-500 font-bold uppercase tracking-wider mb-1">Chi tiết lỗi</span>
                <p className="text-xs font-mono text-rose-400 overflow-x-auto whitespace-pre-wrap max-h-32">
                  {this.state.error.toString()}
                </p>
              </div>
            )}

            <div className="flex gap-3 pt-2">
              <button
                onClick={() => window.location.reload()}
                className="flex-1 flex items-center justify-center gap-2 py-3 bg-slate-800 hover:bg-slate-700 text-slate-200 font-bold text-xs rounded-xl transition-all uppercase tracking-wider cursor-pointer"
              >
                <RefreshCw size={14} />
                Tải Lại Trang
              </button>
              <button
                onClick={this.handleReset}
                className="flex-1 flex items-center justify-center gap-2 py-3 bg-brand hover:bg-brand-hover text-white font-bold text-xs rounded-xl transition-all uppercase tracking-wider cursor-pointer"
              >
                <Home size={14} />
                Về Trang Chủ
              </button>
            </div>
          </div>
        </div>
      )
    }

    return this.props.children
  }
}
