import { scopeLabel } from '@/utils/courseScope'

const THUMB_PALETTE = [
  { bg: '#E8EAF6', fg: '#2F3E82' },
  { bg: '#E3EDE8', fg: '#1F7A5C' },
  { bg: '#F5E9E9', fg: '#9C4A4A' },
  { bg: '#FBF2DC', fg: '#8A6A1E' },
  { bg: '#E7E9F2', fg: '#4A5578' },
  { bg: '#EFE7F2', fg: '#6B4A80' },
]

export function thumbFor(course) {
  const label = scopeLabel(course)
  let hash = 0
  for (let i = 0; i < label.length; i++) hash = (hash * 31 + label.charCodeAt(i)) | 0
  const palette = THUMB_PALETTE[Math.abs(hash) % THUMB_PALETTE.length]
  const initials = label
    .split(' ')
    .filter((w) => w.length > 0)
    .slice(0, 2)
    .map((w) => w[0])
    .join('')
    .toUpperCase()
  return { ...palette, initials }
}
