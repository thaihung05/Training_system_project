const DATE_OPTS = { timeZone: 'Asia/Ho_Chi_Minh', day: '2-digit', month: '2-digit', year: 'numeric' }
const DATETIME_OPTS = { ...DATE_OPTS, hour: '2-digit', minute: '2-digit', hourCycle: 'h23' }

export function formatDate(ms) {
  if (!ms) return ''
  return new Date(ms).toLocaleDateString('vi-VN', DATE_OPTS)
}

export function formatDateTime(ms) {
  if (!ms) return ''
  return new Date(ms).toLocaleString('vi-VN', DATETIME_OPTS)
}
