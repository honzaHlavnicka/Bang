import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import tailwindcss from '@tailwindcss/vite';
import { fileURLToPath } from 'node:url';

const coreSrc = fileURLToPath(new URL('../core/src', import.meta.url));
const reactModule = fileURLToPath(new URL('node_modules/react', import.meta.url));
const reactDomModule = fileURLToPath(new URL('node_modules/react-dom', import.meta.url));

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    react(),
    tailwindcss(),
  ],
  resolve: {
    alias: {
      '@bang/core': coreSrc,
      'react': reactModule,
      'react-dom': reactDomModule,
    }
  },
  server: {
    port: 5174
  }
});
