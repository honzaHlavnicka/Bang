import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  base: '/',
  build: {
    rollupOptions: {
      output: {
        manualChunks(id) {
          if (id.includes('node_modules')) {
            if (id.includes('react-custom-roulette')) {
              return 'roulette';
            }
            if (id.includes('@dnd-kit')) {
              return 'dnd-kit';
            }
            if (id.includes('posthog')) {
              return 'posthog';
            }
            if (id.includes('i18next')) {
              return 'i18n';
            }
            if (id.includes('react') || id.includes('scheduler')) {
              return 'react-core';
            }
          }
          if (id.includes('locales/cs.json')) {
            return 'translation-cs';
          }
          if (id.includes('locales/en.json')) {
            return 'translation-en';
          }
        }
      }
    }
  }
})
