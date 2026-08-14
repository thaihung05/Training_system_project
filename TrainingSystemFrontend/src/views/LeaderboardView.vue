<script setup>
import { computed } from 'vue'
import { Award, GraduationCap } from '@lucide/vue'
import { useAuthStore } from '@/stores/auth'
import { useAsyncData } from '@/composables/useAsyncData'
import pointService from '@/api/pointService'
import badgeService from '@/api/badgeService'
import certificateService from '@/api/certificateService'
import { formatDate } from '@/utils/formatDate'

const auth = useAuthStore()

const { data: pointsData } = useAsyncData(() => pointService.getMy())
const { data: leaderboard, loading: leaderboardLoading } = useAsyncData(() => pointService.getLeaderboard(9999))
const leaderboardTop15 = computed(() => (leaderboard.value || []).slice(0, 15))
const { data: allBadges } = useAsyncData(() => badgeService.getAll())
const { data: myBadges } = useAsyncData(() => badgeService.getMy())
const { data: certificates, error: certificatesError } = useAsyncData(() => certificateService.getMy())

const myRank = computed(() => {
  const list = leaderboard.value || []
  const idx = list.findIndex((row) => row.userId === auth.user?.id)
  return idx === -1 ? '—' : `#${idx + 1}`
})

function isEarned(badgeId) {
  return (myBadges.value || []).some((mb) => mb.badgeId.id === badgeId)
}

function initials(name) {
  return (name || '').split(' ').slice(-2).map((w) => w[0]).join('').toUpperCase()
}
</script>

<template>
  <div class="page">
    <div class="page-header">
      <h1>Thành tích của tôi</h1>
    </div>

    <div class="achv-stats">
      <div class="stat-card">
        <div class="stat-body">
          <div class="stat-subtitle">Tổng điểm tích luỹ</div>
          <div class="stat-mono stat-mono--gold">{{ pointsData?.totalPoints ?? 0 }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-body">
          <div class="stat-subtitle">Hạng hiện tại</div>
          <div class="stat-mono">{{ myRank }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-body">
          <div class="stat-subtitle">Huy hiệu đạt được</div>
          <div class="stat-mono">{{ myBadges?.length ?? 0 }}/{{ allBadges?.length ?? 0 }}</div>
        </div>
      </div>
    </div>

    <div class="achv-body">
      <div class="card achv-leaderboard">
        <h2>Bảng xếp hạng</h2>
        <p v-if="leaderboardLoading" class="state-text">Đang tải...</p>
        <div v-else class="widget-list">
          <div
            v-for="(row, index) in leaderboardTop15"
            :key="row.userId"
            class="list-item"
            :class="{ 'list-item--self': row.userId === auth.user?.id }"
          >
            <span class="list-item-rank">{{ index + 1 }}</span>
            <span class="avatar avatar-sm">{{ initials(row.name) }}</span>
            <span class="list-item-body">
              <span class="list-item-title">{{ row.name }}</span>
            </span>
            <span class="list-item-points">{{ row.totalPoints }}</span>
          </div>
        </div>
      </div>

      <div class="achv-side">
        <div class="card">
          <h2>Huy hiệu</h2>
          <div class="badge-grid">
            <div v-for="b in allBadges" :key="b.id" class="badge-item" :class="{ 'badge-item--locked': !isEarned(b.id) }">
              <div class="badge-icon"><Award :size="20" /></div>
              <div class="badge-name">{{ b.name }}</div>
            </div>
          </div>
        </div>

        <div class="card">
          <h2>Chứng chỉ</h2>
          <p v-if="certificatesError" class="alert alert-error">{{ certificatesError.response?.data || 'Có lỗi xảy ra.' }}</p>
          <div v-else-if="!certificates || certificates.length === 0" class="state-text">Chưa có chứng chỉ nào.</div>
          <div v-else class="cert-list">
            <div v-for="c in certificates" :key="c.id" class="cert-row">
              <div class="cert-icon"><GraduationCap :size="18" /></div>
              <div class="cert-info">
                <div class="cert-title">{{ c.courseId.title }}</div>
                <div class="cert-date">Cấp ngày {{ formatDate(c.issuedAt) }}</div>
              </div>
              <a v-if="c.pdfUrl" :href="c.pdfUrl" target="_blank" rel="noopener" class="cert-download">Tải về</a>
              <span v-else class="cert-pending">Đang chờ file</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped src="./LeaderboardView.css"></style>
