import { ref } from 'vue'

export function useLazyList(fetcher, pageSize = 20) {
  const items = ref([])
  const loading = ref(true)
  const loadingMore = ref(false)
  const hasMore = ref(true)
  const error = ref(null)
  let page = 1

  async function loadFirstPage() {
    loading.value = true
    error.value = null
    page = 1
    try {
      const res = await fetcher(page, pageSize)
      items.value = res.data
      hasMore.value = res.data.length === pageSize
    } catch (err) {
      error.value = err
    } finally {
      loading.value = false
    }
  }

  async function loadMore() {
    if (!hasMore.value || loadingMore.value) return
    loadingMore.value = true
    error.value = null
    try {
      const nextPage = page + 1
      const res = await fetcher(nextPage, pageSize)
      items.value = [...items.value, ...res.data]
      page = nextPage
      hasMore.value = res.data.length === pageSize
    } catch (err) {
      error.value = err
    } finally {
      loadingMore.value = false
    }
  }

  loadFirstPage()

  return { items, loading, loadingMore, hasMore, error, loadMore, reload: loadFirstPage }
}
