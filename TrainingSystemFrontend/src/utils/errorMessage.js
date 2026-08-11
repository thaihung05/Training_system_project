export function getErrorText(err, fallback = 'Có lỗi xảy ra, thử lại nhé.') {
  return err?.response?.data || fallback
}
