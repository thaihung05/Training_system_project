export function scopeLabel(course) {
    const chainNames = (course.chains || []).map((c) => c.name)
    const regionNames = (course.regions || []).map((r) => r.name)
    if (chainNames.length === 0 && regionNames.length === 0) return 'Toàn tập đoàn'
    return [chainNames.join(', '), regionNames.join(', ')].filter(Boolean).join(' · ')
}
