import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { motion } from 'framer-motion'
import { useAuthStore } from './AuthStore'
import api from '../../services/api'
import { Film, User, Lock, AlertCircle, ArrowRight } from 'lucide-react'

export default function Login() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const setAuth = useAuthStore((state) => state.setAuth)
  const navigate = useNavigate()

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!username.trim() || !password.trim()) {
      setError('Vui lòng điền tên đăng nhập và mật khẩu.')
      return
    }

    setError('')
    setLoading(true)

    try {
      const response = await api.post('/auth/login', { username, password })
      const { accessToken, refreshToken, employeeId, username: uName, fullName, role } = response.data
      
      setAuth(accessToken, refreshToken, { employeeId, username: uName, fullName, role })
      
      // Redirect based on role
      if (role === 'MANAGER' || role === 'ADMIN') {
        navigate('/dashboard')
      } else {
        navigate('/')
      }
    } catch (err) {
      setError(err.response?.data?.error || 'Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen w-full bg-[#020617] text-slate-100 font-sans relative flex items-center justify-center overflow-hidden">
      {/* Red Radial Glow Background */}
      <div
        className="absolute inset-0 z-0 pointer-events-none"
        style={{
          backgroundImage: `radial-gradient(circle 500px at 50% 100px, rgba(239,68,68,0.4), transparent)`,
        }}
      />

      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="w-full max-w-md p-8 rounded-2xl glass-panel relative overflow-hidden z-10"
      >
        {/* Glow effect */}
        <div className="absolute -top-10 -right-10 w-32 h-32 bg-brand/20 rounded-full blur-2xl pointer-events-none" />
        <div className="absolute -bottom-10 -left-10 w-32 h-32 bg-rose-500/10 rounded-full blur-2xl pointer-events-none" />

        <div className="flex flex-col items-center mb-8">
          <div className="flex items-center justify-center w-14 h-14 bg-brand rounded-full mb-3 shadow-lg shadow-brand/35 text-white">
            <Film size={28} />
          </div>
          <h2 className="text-2xl font-extrabold tracking-wider text-slate-100 uppercase">ĐĂNG NHẬP</h2>
          <p className="text-sm text-slate-400 mt-1">Hệ thống đặt vé RapPhim Engine</p>
        </div>

        {error && (
          <motion.div
            initial={{ opacity: 0, x: -10 }}
            animate={{ opacity: 1, x: 0 }}
            className="flex items-center gap-2 p-3 bg-brand/10 border border-brand/25 rounded-lg text-sm text-brand mb-6"
          >
            <AlertCircle size={16} className="flex-shrink-0" />
            <span>{error}</span>
          </motion.div>
        )}

        <form onSubmit={handleSubmit} className="space-y-5">
          <div>
            <label className="block text-sm font-semibold text-slate-300 mb-1.5">Tên đăng nhập</label>
            <div className="relative">
              <span className="absolute inset-y-0 left-0 pl-3.5 flex items-center text-slate-400">
                <User size={18} />
              </span>
              <input
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="Nhập tên đăng nhập..."
                className="w-full pl-10 pr-4 py-3 bg-slate-900 border border-slate-800 rounded-xl text-slate-200 focus:border-brand transition-all text-sm"
              />
            </div>
          </div>

          <div>
            <label className="block text-sm font-semibold text-slate-300 mb-1.5">Mật khẩu</label>
            <div className="relative">
              <span className="absolute inset-y-0 left-0 pl-3.5 flex items-center text-slate-400">
                <Lock size={18} />
              </span>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Nhập mật khẩu..."
                className="w-full pl-10 pr-4 py-3 bg-slate-900 border border-slate-800 rounded-xl text-slate-200 focus:border-brand transition-all text-sm"
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full flex items-center justify-center gap-2 py-3.5 bg-brand hover:bg-brand-hover text-white rounded-xl font-extrabold shadow-lg shadow-brand/15 hover:shadow-brand/25 transition-all text-sm"
          >
            {loading ? (
              <span className="w-5 h-5 border-2 border-t-transparent border-white rounded-full animate-spin" />
            ) : (
              <>
                Đăng Nhập
                <ArrowRight size={16} />
              </>
            )}
          </button>
        </form>
        
        <div className="mt-6 text-center">
          <p className="text-xs text-slate-500">Dành riêng cho Nhân viên và Quản lý Rạp chiếu phim</p>
        </div>
      </motion.div>
    </div>
  )
}
