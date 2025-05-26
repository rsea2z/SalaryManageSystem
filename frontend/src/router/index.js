import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '@/layouts/MainLayout.vue' // Import MainLayout
import LoginView from '../views/LoginView.vue'
import HomeView from '../views/HomeView.vue'
import ProfileView from '../views/ProfileView.vue'
import AdminView from '../views/AdminView.vue'
import { useAuthStore } from '@/stores/authStore'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: LoginView
    // No layout for login page
  },
  {
    path: '/',
    component: MainLayout, // Use MainLayout for these routes
    meta: { requiresAuth: true },
    children: [
      {
        path: '', // Default child route for '/'
        name: 'Home',
        component: HomeView,
      },
      {
        path: 'profile', // No leading slash for children
        name: 'Profile',
        component: ProfileView,
      },
      {
        path: 'admin', // No leading slash for children
        name: 'Admin',
        component: AdminView,
        meta: { requiresAdmin: true }
      }
    ]
  },
  // Redirect to login if no other route matches, or to home if preferred
  {
    path: '/:catchAll(.*)',
    redirect: '/' 
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore();

  // Attempt to fetch user details if not already loaded and token exists
  // This is useful for page reloads
  if (authStore.token && !authStore.userId) {
      await authStore.fetchUserDetails(); // Ensure user details are loaded
  }
  
  const isAuthenticated = authStore.isAuthenticated;
  const isAdmin = authStore.isAdmin;

  if (to.matched.some(record => record.meta.requiresAuth) && !isAuthenticated) {
    next({ name: 'Login', query: { redirect: to.fullPath } });
  } else if (to.matched.some(record => record.meta.requiresAdmin) && !isAdmin) {
    next({ name: 'Home' }); // Or an unauthorized page
  }
  else {
    next();
  }
});

export default router
