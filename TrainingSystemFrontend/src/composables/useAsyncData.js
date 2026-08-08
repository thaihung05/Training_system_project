import { ref } from "vue";

export function useAsyncData(fetcher) {
    const data = ref(null);
    const loading = ref(true);
    const error = ref(null);

    async function refresh() {
        loading.value = true;
        error.value = null;
        try{
            const res = await fetcher();
            data.value = res.data;
        } catch (err) {
            error.value = err;
        } finally {
            loading.value = false;
        }
    }

    refresh();
    
    return {
        data, loading, error, refresh
    }
}