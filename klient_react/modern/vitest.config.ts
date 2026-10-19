import { defineConfig } from 'vitest/config';
import react from '@vitejs/plugin-react';
import path from 'path';

export default defineConfig({
  plugins: [react()],
  resolve: {
    dedupe: ['react', 'react-dom'],
    alias: {
      '@bang/core': path.resolve(__dirname, '../core/src'),
      'react': path.resolve(__dirname, '../bang/node_modules/react'),
      'react-dom': path.resolve(__dirname, '../bang/node_modules/react-dom'),
    }
  },
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: ['./src/test/setup.ts'],
    include: ['src/**/*.{test,spec}.{ts,tsx}'],
  },
});
