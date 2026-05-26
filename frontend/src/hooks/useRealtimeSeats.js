import { useState, useEffect, useRef } from 'react'

/**
 * useRealtimeSeats — Subscribes to STOMP WebSocket for realtime seat status updates.
 * Falls back gracefully if @stomp/stompjs is not installed.
 *
 * @param {string} showtimeId - The showtime to subscribe to
 * @param {Object} initialStatusMap - Initial seat status map from REST API
 * @returns {{ statusMap, connected }} - Live-updated seat status map and connection state
 */
export function useRealtimeSeats(showtimeId, initialStatusMap = {}) {
  const [statusMap, setStatusMap] = useState(initialStatusMap)
  const [connected, setConnected] = useState(false)
  const clientRef = useRef(null)

  // Sync when REST data loads
  useEffect(() => {
    setStatusMap(initialStatusMap)
  }, [initialStatusMap])

  useEffect(() => {
    if (!showtimeId) return

    let client = null
    let isActive = true

    const connectWebSocket = async () => {
      try {
        // Dynamically import to handle missing package gracefully
        const [{ Client }, SockJS] = await Promise.all([
          import('@stomp/stompjs'),
          import('sockjs-client'),
        ])

        client = new Client({
          webSocketFactory: () => new SockJS.default('/ws'),
          reconnectDelay: 5000,
          onConnect: () => {
            if (!isActive) return
            setConnected(true)

            // Subscribe to this showtime's seat updates
            client.subscribe(`/topic/showtime/${showtimeId}/seats`, (message) => {
              try {
                const payload = JSON.parse(message.body)
                // payload: { seatId: "A1", status: "LOCKED" | "AVAILABLE" | "BOOKED" }
                if (payload.seatId && payload.status) {
                  setStatusMap((prev) => ({
                    ...prev,
                    [payload.seatId]: payload.status,
                  }))
                }
              } catch {
                // Ignore malformed messages
              }
            })
          },
          onDisconnect: () => {
            if (isActive) setConnected(false)
          },
          onStompError: () => {
            if (isActive) setConnected(false)
          },
        })

        client.activate()
        clientRef.current = client
      } catch {
        // @stomp/stompjs not installed — polling fallback already handled by useShowtimeSeats
        console.debug('[useRealtimeSeats] STOMP not available, using polling fallback.')
      }
    }

    connectWebSocket()

    return () => {
      isActive = false
      if (clientRef.current?.active) {
        clientRef.current.deactivate()
      }
      clientRef.current = null
    }
  }, [showtimeId])

  /**
   * Broadcasts a local seat selection to all other users via WebSocket.
   * @param {string} seatId
   * @param {'LOCKED'|'AVAILABLE'} status
   */
  const broadcastSeatUpdate = (seatId, status) => {
    if (clientRef.current?.connected) {
      clientRef.current.publish({
        destination: `/app/showtime/${showtimeId}/seat-update`,
        body: JSON.stringify({ seatId, status }),
      })
    }
  }

  return { statusMap, connected, broadcastSeatUpdate }
}
