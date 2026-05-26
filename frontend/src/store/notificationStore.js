import { create } from 'zustand'

/**
 * notificationStore — Manages in-app notifications.
 * Notifications can be pushed from any component or event (e.g., booking confirmed, system alert).
 */
export const useNotificationStore = create((set, get) => ({
  notifications: [],

  /** Add a new notification */
  push: (notification) => {
    const id = Date.now().toString()
    set((state) => ({
      notifications: [
        { id, read: false, createdAt: new Date(), ...notification },
        ...state.notifications,
      ].slice(0, 50), // Max 50 notifications
    }))
    return id
  },

  /** Mark a single notification as read */
  markRead: (id) => {
    set((state) => ({
      notifications: state.notifications.map((n) =>
        n.id === id ? { ...n, read: true } : n
      ),
    }))
  },

  /** Mark all notifications as read */
  markAllRead: () => {
    set((state) => ({
      notifications: state.notifications.map((n) => ({ ...n, read: true })),
    }))
  },

  /** Remove a single notification */
  remove: (id) => {
    set((state) => ({
      notifications: state.notifications.filter((n) => n.id !== id),
    }))
  },

  /** Clear all notifications */
  clearAll: () => set({ notifications: [] }),

  /** Unread count */
  get unreadCount() {
    return get().notifications.filter((n) => !n.read).length
  },
}))
