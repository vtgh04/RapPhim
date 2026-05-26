import { useQuery } from '@tanstack/react-query'
import api from '../services/api'

/**
 * useShowtimes — Fetches all showtimes from the API.
 */
export function useShowtimes() {
  return useQuery({
    queryKey: ['showtimes'],
    queryFn: async () => {
      const response = await api.get('/showtimes')
      return response.data
    },
    staleTime: 1000 * 60 * 2, // Cache for 2 minutes
  })
}

/**
 * useShowtime — Fetches a single showtime by ID.
 * @param {string} showtimeId
 */
export function useShowtime(showtimeId) {
  return useQuery({
    queryKey: ['showtime', showtimeId],
    queryFn: async () => {
      const response = await api.get(`/showtimes/${showtimeId}`)
      return response.data
    },
    enabled: !!showtimeId,
  })
}

/**
 * useShowtimeSeats — Fetches the seat status map for a showtime.
 * @param {string} showtimeId
 */
export function useShowtimeSeats(showtimeId) {
  return useQuery({
    queryKey: ['showseat-statuses', showtimeId],
    queryFn: async () => {
      const response = await api.get(`/showtimes/${showtimeId}/seats`)
      return response.data
    },
    enabled: !!showtimeId,
    refetchInterval: 15000, // Polling fallback every 15s if WS not connected
  })
}
