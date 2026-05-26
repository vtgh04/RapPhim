import React from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'

import { useAuthStore } from './features/auth/AuthStore'
import Layout from './shared/Layout'
import Login from './features/auth/Login'
import MovieList from './features/booking/MovieList'
import MovieDetail from './features/booking/MovieDetail'
import SeatGrid from './features/booking/SeatGrid'
import Checkout from './features/booking/Checkout'
import InvoicesHistory from './features/booking/InvoicesHistory'
import Dashboard from './features/dashboard/Dashboard'
import './index.css'

// Initialize TanStack Query Client
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      retry: 1,
    },
  },
})

// Route Protection Guard
function ProtectedRoute({ children, allowedRoles }) {
  const { user } = useAuthStore()

  if (!user) {
    return <Navigate to="/login" replace />
  }

  if (allowedRoles && !allowedRoles.includes(user.role)) {
    return <Navigate to="/" replace />
  }

  return children
}

createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <Routes>
          {/* Main Layout routes */}
          <Route path="/" element={<Layout />}>
            <Route index element={<MovieList />} />
            <Route path="movie/:id" element={<MovieDetail />} />
            <Route path="booking/:showtimeId" element={<SeatGrid />} />
            
            {/* User authenticated checkout & billing history */}
            <Route
              path="checkout"
              element={
                <ProtectedRoute>
                  <Checkout />
                </ProtectedRoute>
              }
            />
            <Route
              path="invoices"
              element={
                <ProtectedRoute>
                  <InvoicesHistory />
                </ProtectedRoute>
              }
            />

            {/* Admin/Manager Dashboard */}
            <Route
              path="dashboard"
              element={
                <ProtectedRoute allowedRoles={['MANAGER', 'ADMIN']}>
                  <Dashboard />
                </ProtectedRoute>
              }
            />
          </Route>

          {/* Login outside main layout */}
          <Route path="/login" element={<Login />} />
          
          {/* Fallback routing */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </QueryClientProvider>
  </React.StrictMode>
)
