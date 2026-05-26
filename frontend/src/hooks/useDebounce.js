import { useState, useEffect } from 'react'

/**
 * useDebounce — Delays updating a value until after `delay` ms of inactivity.
 * @param {*} value — The value to debounce
 * @param {number} delay — Delay in milliseconds (default 300ms)
 * @returns Debounced value
 */
export function useDebounce(value, delay = 300) {
  const [debouncedValue, setDebouncedValue] = useState(value)

  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedValue(value)
    }, delay)

    return () => clearTimeout(handler)
  }, [value, delay])

  return debouncedValue
}
