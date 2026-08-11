<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useAsyncData } from '@/composables/useAsyncData'
import enrollmentService from '@/api/enrollmentService'
import pointService from '@/api/pointService'
import notificationService from '@/api/notificationService'
import { thumbFor } from '@/utils/courseThumb'

const auth = useAuthStore()
const router = useRouter()

const { data: enrollments } = useAsyncData(() => enrollmentService.getMyEnrollments())
const { data: pointsData } = useAsyncData(() => pointService.getMy())
const { data: leaderboard } = useAsyncData(() => pointService.getLeaderboard(5))
const { data: notifications } = useAsyncData(() => notificationService.getMy())

const inProgressCourses = computed(() =>
  (enrollments.value || []).filter((e) => e.progressPercent < 100).slice(0, 3),
)
const completedCount = computed(
  () => (enrollments.value || []).filter((e) => e.progressPercent >= 100).length,
)

const stats = computed(() => [
  { label: 'Khoá học của tôi', value: enrollments.value?.length ?? '—' },
  { label: 'Điểm tích luỹ', value: pointsData.value?.totalPoints ?? 0 },
  { label: 'Hoàn thành', value: completedCount.value },
  { label: 'Thông báo mới', value: (notifications.value || []).filter((n) => !n.isRead).length },
])

function initials(name) {
  return (name || '').split(' ').slice(-2).map((w) => w[0]).join('').toUpperCase()
}

async function openNotification(n) {
  if (!n.isRead) {
    await notificationService.markRead(n.id)
    n.isRead = true
  }
  if (n.link) {
    router.push(n.link)
  }
}
</script>

<template>
  <div class="home-page">
    <div class="home-greeting">
      <h1>Chào {{ auth.user?.name}}</h1>
      <div class="home-date">{{ new Date().toLocaleDateString('vi-VN', { timeZone: 'Asia/Ho_Chi_Minh', weekday: 'long', year: 'numeric', month: '2-digit', day: '2-digit' }) }}</div>
    </div>

    <div class="stat-rail home-stats">
      <div v-for="s in stats" :key="s.label" class="stat-rail-item">
        <div class="stat-rail-value">{{ s.value }}</div>
        <div class="stat-rail-label">{{ s.label }}</div>
      </div>
    </div>

    <div class="home-layout">
      <div class="home-main">
        <div class="card">
          <h2>Khoá học đang học</h2>
          <div v-if="inProgressCourses.length === 0" class="empty-state">
            Bạn chưa có khoá học nào đang học.
          </div>
          <div v-else class="course-progress-list">
            <div v-for="e in inProgressCourses" :key="e.id" class="course-progress-row">
              <div class="course-thumb" :style="e.courseId.imageUrl ? {} : { background: thumbFor(e.courseId).bg }">
                <img v-if="e.courseId.imageUrl" :src="e.courseId.imageUrl" class="course-thumb-img" />
                <span v-else class="course-thumb-initials" :style="{ color: thumbFor(e.courseId).fg }">{{ thumbFor(e.courseId).initials }}</span>
              </div>
              <div class="course-progress-info">
                <div class="course-progress-title">{{ e.courseId.title }}</div>
                <div class="course-progress-meta">
                  {{ e.courseId.departmentId ? e.courseId.departmentId.name : 'Toàn công ty' }}
                </div>
                <div class="progress-track">
                  <div class="progress-fill" :style="{ width: e.progressPercent + '%' }"></div>
                </div>
              </div>
              <div class="course-progress-percent">{{ e.progressPercent }}%</div>
              <RouterLink :to="{ name: 'course-detail', params: { id: e.courseId.id } }" class="btn btn-primary btn-sm">
                Tiếp tục
              </RouterLink>
            </div>
          </div>
        </div>
      </div>

      <aside class="home-sidebar">
        <div class="card">
          <h2>Bảng xếp hạng</h2>
          <div v-if="!leaderboard || leaderboard.length === 0" class="state-text">Chưa có dữ liệu.</div>
          <div v-else class="widget-list">
            <div v-for="(row, index) in leaderboard" :key="row.userId" class="list-item">
              <span class="list-item-rank">{{ index + 1 }}</span>
              <span class="avatar avatar-sm">{{ initials(row.name) }}</span>
              <span class="list-item-body">
                <span class="list-item-title">{{ row.name }}</span>
              </span>
              <span class="list-item-points">{{ row.totalPoints }}</span>
            </div>
          </div>
        </div>

        <div class="card">
          <h2>Thông báo</h2>
          <div v-if="!notifications || notifications.length === 0" class="state-text">Không có thông báo.</div>
          <div v-else class="widget-list">
            <div
              v-for="n in notifications.slice(0, 4)"
              :key="n.id"
              class="list-item home-notif-item"
              :class="{ 'home-notif-item--unread': !n.isRead }"
              @click="openNotification(n)"
            >
              <span class="list-item-body">
                <span class="list-item-title">{{ n.title }}</span>
              </span>
            </div>
          </div>
        </div>
      </aside>
    </div>
  </div>
</template>

<style scoped src="./HomeView.css"></style>