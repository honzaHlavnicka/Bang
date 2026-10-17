import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { GameProvider, DialogProvider, setNotificationHandler } from '@bang/core';
import { toast, Toaster } from 'sonner';
import App from './App';
import './index.css';

// Propojení Sonner toastů s headless herním jádrem
setNotificationHandler({
  success: (msg: string) => toast.success(msg),
  error: (msg: string) => toast.error(msg),
  loading: (msg: string, opts?: any) => toast.loading(msg, { id: opts?.id }),
  dismiss: (id?: string) => toast.dismiss(id)
});

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <DialogProvider>
      <GameProvider>
        <Toaster richColors position="top-right" theme="dark" />
        <App />
      </GameProvider>
    </DialogProvider>
  </StrictMode>
);
