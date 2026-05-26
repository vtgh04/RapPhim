import { useQuery } from '@tanstack/react-query'
import api from '../services/api'

/**
 * useMovies — Fetches all movies from the API.
 * Uses TanStack Query for caching and background refetch.
 */
export function useMovies() {
  return useQuery({
    queryKey: ['movies'],
    queryFn: async () => {
      const response = await api.get('/movies')
      return response.data
    },
    staleTime: 1000 * 60 * 5, // Cache for 5 minutes
  })
}

/**
 * useMovie — Fetches a single movie by ID.
 * @param {string} movieId
 */
export function useMovie(movieId) {
  return useQuery({
    queryKey: ['movie', movieId],
    queryFn: async () => {
      const response = await api.get(`/movies/${movieId}`)
      return response.data
    },
    enabled: !!movieId,
    staleTime: 1000 * 60 * 5,
  })
}
