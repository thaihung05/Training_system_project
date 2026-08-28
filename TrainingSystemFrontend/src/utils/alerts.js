import Swal from 'sweetalert2'

const swal = Swal.mixin({
  customClass: {
    popup: 'tlh-swal-popup',
  },
})

export function confirmDialog(text, title = 'Xác nhận') {
  return swal
    .fire({
      title,
      text,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Xác nhận',
      cancelButtonText: 'Huỷ',
      confirmButtonColor: '#B54747',
      cancelButtonColor: '#6B7280',
      reverseButtons: true,
    })
    .then((result) => result.isConfirmed)
}

export function showError(text) {
  return swal.fire({
    title: 'Lỗi',
    text,
    icon: 'error',
    confirmButtonText: 'Đóng',
    confirmButtonColor: '#2F3E82',
  })
}
