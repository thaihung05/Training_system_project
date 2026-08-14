export function scopeLabel(course) {
    const chainNames = (course.chains || []).map((c) => c.name)
    const regionNames = (course.regions || []).map((r) => r.name)
    if (chainNames.length === 0 && regionNames.length === 0) return 'Toàn tập đoàn'
    return [chainNames.join(', '), regionNames.join(', ')].filter(Boolean).join(' · ')
}

export function isStoreInCourseScope(store, course) {
    if (!store || !course || !store.chainId || !store.regionId) return false

    const chainIds = (course.chains || []).map((chain) => chain.id)
    const regionIds = (course.regions || []).map((region) => region.id)
    const chainMatch = chainIds.length === 0 || chainIds.includes(store.chainId.id)
    const regionMatch = regionIds.length === 0 || regionIds.includes(store.regionId.id)

    return chainMatch && regionMatch
}
