import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import path from 'path'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@bang/core': path.resolve(__dirname, '../core/src'),
      'react': path.resolve(__dirname, 'node_modules/react'),
      'react-dom': path.resolve(__dirname, 'node_modules/react-dom'),
      'i18next': path.resolve(__dirname, 'node_modules/i18next'),
      'posthog-js': path.resolve(__dirname, 'node_modules/posthog-js')
    }
  },
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
