import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from "../stores/auth";

const router = createRouter({
    history: createWebHistory(),
    routes: [
        {
            path: '/login',
            name: 'Login',
            component: () => import('@/views/auth/LoginView.vue'),
        },
        {
            path: '/',
            name: 'home',
            component: () => import('@/views/HomeView.vue'),
            meta: { requiresAuth: true }
        },
        {
            path: '/courses',
            name: 'courses',
            component: () => import('@/views/courses/CourseListView.vue'),
            meta: { requiresAuth: true },
        },
        {
            path: '/courses/:id',
            name: 'course-detail',
            component: () => import('@/views/courses/CourseDetailView.vue'),
            meta: { requiresAuth: true },
        },
        {
            path: '/my-courses',
            name: 'my-courses',
            component: () => import('@/views/courses/MyCoursesView.vue'),
            meta: { requiresAuth: true },
        },
        {
            path: '/leaderboard',
            name: 'leaderboard',
            component: () => import('@/views/LeaderboardView.vue'),
            meta: { requiresAuth: true },
        },
        {
            path: '/notifications',
            name: 'notifications',
            component: () => import('@/views/NotificationsView.vue'),
            meta: { requiresAuth: true },
        },
        {
            path: '/manage/courses',
            name: 'manage-courses',
            component: () => import('@/views/manage/CourseManageView.vue'),
            meta: { requiresAuth: true, roles: ['TRAINER', 'ADMIN'] },
        },
        {
            path: '/manage/courses/:courseId/enrollments',
            name: 'course-enrollments',
            component: () => import('@/views/manage/EnrollmentManageView.vue'),
            meta: { requiresAuth: true, roles: ['TRAINER', 'ADMIN'] },
        },
        {
            path: '/manage/courses/:courseId/lessons',
            name: 'manage-lessons',
            component: () => import('@/views/manage/LessonManageView.vue'),
            meta: { requiresAuth: true, roles: ['TRAINER', 'ADMIN'] },
        },
        {
            path: '/manage/courses/:courseId/tests',
            name: 'manage-tests',
            component: () => import('@/views/manage/TestManageView.vue'),
            meta: { requiresAuth: true, roles: ['TRAINER', 'ADMIN'] },
        },
        {
            path: '/manage/courses/:courseId/tests/:testId/questions',
            name: 'manage-questions',
            component: () => import('@/views/manage/QuestionManageView.vue'),
            meta: { requiresAuth: true, roles: ['TRAINER', 'ADMIN'] },
        },
        {
            path: '/tests/:testId/attempts/:attemptId/take',
            name: 'attempt-take',
            component: () => import('@/views/attempt/AttemptTakeView.vue'),
            meta: { requiresAuth: true },
        },
        {
            path: '/attempts/:attemptId/result',
            name: 'attempt-result',
            component: () => import('@/views/attempt/AttemptResultView.vue'),
            meta: { requiresAuth: true },
        },
        {
            path: '/manage/courses/:courseId/certificates',
            name: 'course-certificates',
            component: () => import('@/views/manage/CourseCertificatesView.vue'),
            meta: { requiresAuth: true, roles: ['TRAINER', 'ADMIN'] },
        },
        {
            path: '/admin',
            name: 'admin-dashboard',
            component: () => import('@/views/admin/AdminDashboardView.vue'),
            meta: { requiresAuth: true, hideNavbar: true, roles: ['ADMIN'] },
        },
        {
            path: '/admin/users',
            name: 'admin-users',
            component: () => import('@/views/admin/UsersManageView.vue'),
            meta: { requiresAuth: true, hideNavbar: true, roles: ['ADMIN'] },
        },
        {
            path: '/admin/chains',
            name: 'admin-chains',
            component: () => import('@/views/admin/ChainsManageView.vue'),
            meta: { requiresAuth: true, hideNavbar: true, roles: ['ADMIN'] },
        },
        {
            path: '/admin/regions',
            name: 'admin-regions',
            component: () => import('@/views/admin/RegionsManageView.vue'),
            meta: { requiresAuth: true, hideNavbar: true, roles: ['ADMIN'] },
        },
        {
            path: '/admin/stores',
            name: 'admin-stores',
            component: () => import('@/views/admin/StoresManageView.vue'),
            meta: { requiresAuth: true, hideNavbar: true, roles: ['ADMIN'] },
        },
        {
            path: '/admin/point-rules',
            name: 'admin-point-rules',
            component: () => import('@/views/admin/PointRulesManageView.vue'),
            meta: { requiresAuth: true, hideNavbar: true, roles: ['ADMIN'] },
        },
        {
            path: '/admin/badges',
            name: 'admin-badges',
            component: () => import('@/views/admin/BadgesManageView.vue'),
            meta: { requiresAuth: true, hideNavbar: true, roles: ['ADMIN'] },
        },
        {
            path: '/chat/queue',
            name: 'chat-queue',
            component: () => import('@/views/chat/ChatQueueView.vue'),
            meta: { requiresAuth: true, roles: ['TRAINER', 'ADMIN'] },
        },
        {
            path: '/manage/courses/:courseId/tests/:testId/attempts',
            name: 'test-attempts-roster',
            component: () => import('@/views/manage/TestAttemptsRosterView.vue'),
            meta: { requiresAuth: true, roles: ['TRAINER', 'ADMIN'] },
        },
        {
            path: '/my-attempts',
            name: 'my-attempts',
            component: () => import('@/views/attempt/MyAttemptsHistoryView.vue'),
            meta: { requiresAuth: true },
        },
        {
            path: '/profile',
            name: 'profile',
            component: () => import('@/views/ProfileView.vue'),
            meta: { requiresAuth: true },
        },
        {
            path: '/my-questions',
            name: 'my-chat-history',
            component: () => import('@/views/chat/MyChatHistoryView.vue'),
            meta: { requiresAuth: true },
        },
        {
            path: '/:pathMatch(.*)*',
            name: 'not-found',
            component: () => import('@/views/NotFoundView.vue'),
        },

    ]
})

router.beforeEach((to) => {
    const auth = useAuthStore()
    if (to.meta.requiresAuth && !auth.isLoggedIn) {
        return { name: 'Login' }
    }
    if (to.meta.roles && !to.meta.roles.includes(auth.role)) {
        return { name: 'home' }
    }
})

export default router