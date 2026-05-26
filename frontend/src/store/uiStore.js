import { create } from 'zustand'
import { getTranslation } from '../utils/translations'

export const useUIStore = create((set, get) => ({
  language: localStorage.getItem('language') || 'vi',
  theme: localStorage.getItem('theme') || 'dark',

  setLanguage: (language) => {
    localStorage.setItem('language', language)
    set({ language })
  },

  setTheme: (theme) => {
    localStorage.setItem('theme', theme)
    if (theme === 'light') {
      document.documentElement.classList.add('light')
    } else {
      document.documentElement.classList.remove('light')
    }
    set({ theme })
  },

  toggleTheme: () => {
    const currentTheme = get().theme
    const nextTheme = currentTheme === 'dark' ? 'light' : 'dark'
    get().setTheme(nextTheme)
  },

  toggleLanguage: () => {
    const currentLang = get().language
    const nextLang = currentLang === 'vi' ? 'en' : 'vi'
    get().setLanguage(nextLang)
  },

  // Translation helper function inside store context
  t: (key) => {
    return getTranslation(get().language, key)
  }
}))

// Apply the initial theme on load
const savedTheme = localStorage.getItem('theme') || 'dark'
if (savedTheme === 'light') {
  document.documentElement.classList.add('light')
} else {
  document.documentElement.classList.remove('light')
}
