import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { GameProvider, DialogProvider, setNotificationHandler, posthog } from '@bang/core';
import { PostHogProvider } from '@posthog/react';
import { toast, Toaster } from 'sonner';
import App from './App';
import './i18n';
import './index.css';

// Propojení Sonner toastů s headless herním jádrem
setNotificationHandler({
  success: (msg: string) => toast.success(msg),
  error: (msg: string) => toast.error(msg),
  loading: (msg: string, opts?: any) => toast.loading(msg, { id: opts?.id }),
  dismiss: (id?: string) => toast.dismiss(id)
});

// Inicializace PostHog telemetrie
const isDebug: boolean = String(import.meta.env.VITE_DEBUG).trim().toLowerCase() === 'true';
const posthogToken: string = String(import.meta.env.VITE_PUBLIC_POSTHOG_TOKEN || '');
const posthogHost: string = String(import.meta.env.VITE_PUBLIC_POSTHOG_HOST || '');
const isHonza = typeof localStorage !== 'undefined' && localStorage.getItem('jsemhonzaa') === 'ano';
const consent = typeof localStorage !== 'undefined' && localStorage.getItem('souhlas') === 'true';

if (!isDebug && posthogToken && !isHonza) {
  posthog.init(posthogToken, {
    api_host: posthogHost,
    capture_pageview: false,
    persistence: consent ? 'localStorage+cookie' : 'memory',
    disable_persistence: !consent,
    save_referrer: true,
    disable_session_recording: !consent,
    autocapture: consent,
    capture_performance: consent,
  });

  posthog.opt_in_capturing();
  if (!consent) {
    posthog.stopSessionRecording();
  }

  const captureUnifiedPageview = () => {
    const currentPath: string = window.location.pathname;
    let language: string = 'default';
    let unifiedPath: string = currentPath;

    if (currentPath.startsWith('/cs')) {
      language = 'cs';
      unifiedPath = currentPath.replace(/^\/cs\/?/, '/') || '/';
    } else if (currentPath.startsWith('/en')) {
      language = 'en';
      unifiedPath = currentPath.replace(/^\/en\/?/, '/') || '/';
    }

    posthog.capture('$pageview', {
      $pathname: unifiedPath,
      $current_url: window.location.origin + unifiedPath,
      app_language: language,
    });
  };

  captureUnifiedPageview();
  window.addEventListener('popstate', captureUnifiedPageview);
} else {
  console.log('🛠️ Debug mód: PostHog analytika je vypnutá.');
}

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <PostHogProvider client={posthog}>
      <DialogProvider>
        <GameProvider>
          <Toaster richColors position="top-right" theme="dark" />
          <App />
        </GameProvider>
      </DialogProvider>
    </PostHogProvider>
  </StrictMode>
);
