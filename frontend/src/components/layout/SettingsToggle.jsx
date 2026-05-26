import React from 'react'
import { Sun, Moon, Globe } from 'lucide-react'
import { useUIStore } from '../../store/uiStore'

export default function SettingsToggle() {
  const { theme, language, toggleTheme, toggleLanguage } = useUIStore()

  return (
    <div className="flex items-center gap-2 bg-slate-900/40 dark:bg-slate-950/40 p-1.5 rounded-full border border-slate-800/40 dark:border-slate-700/35 backdrop-blur-md">
      {/* Theme Toggle */}
      <button
        onClick={toggleTheme}
        title={theme === 'dark' ? 'Chuyển sang chế độ sáng / Light Mode' : 'Chuyển sang chế độ tối / Dark Mode'}
        className="p-1.5 rounded-full text-slate-400 hover:text-brand hover:bg-slate-800/60 dark:hover:bg-slate-900/60 transition-all duration-300 relative group cursor-pointer"
      >
        {theme === 'dark' ? (
          <Moon size={16} className="transition-transform group-hover:rotate-12" />
        ) : (
          <Sun size={16} className="transition-transform group-hover:rotate-45" />
        )}
      </button>

      <span className="w-px h-4 bg-slate-800 dark:bg-slate-700/50" />

      {/* Language Toggle */}
      <button
        onClick={toggleLanguage}
        title={language === 'vi' ? 'Switch to English' : 'Chuyển sang Tiếng Việt'}
        className="flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-black text-slate-400 hover:text-brand hover:bg-slate-800/60 dark:hover:bg-slate-900/60 transition-all duration-300 cursor-pointer"
      >
        <Globe size={13} />
        <span>{language.toUpperCase()}</span>
      </button>
    </div>
  )
}
