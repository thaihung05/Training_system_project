import { createApp } from 'vue'
import './assets/base.css'
import App from './App.vue'
import { createPinia } from 'pinia'
import router from './router'
import './assets/components.css'

const app = createApp(App)

app.use(createPinia())
app.use(router)

app.mount('#app')