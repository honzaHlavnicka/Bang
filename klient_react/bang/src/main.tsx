import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'
import { GameProvider } from './modules/GameProvider.tsx'
import { Toaster } from 'react-hot-toast'
import { DialogProvider } from './modules/DialogProvider.tsx'
import Dialog from './components/Dialog.tsx'
import { ZoomProvider } from './modules/ZoomContext.tsx'
import ZoomDialog from './components/zoomDialog.tsx'
import posthog from 'posthog-js'
import { PostHogProvider } from '@posthog/react'

import '../i18n.ts';

const isDebug: boolean = String(import.meta.env.VITE_DEBUG).trim().toLowerCase() === 'true';
const posthogToken: string = String(import.meta.env.VITE_PUBLIC_POSTHOG_TOKEN);
const posthogHost: string = String(import.meta.env.VITE_PUBLIC_POSTHOG_HOST);

const consent = localStorage.getItem("souhlas") === "true";

if (!isDebug && posthogToken) {
  posthog.init(posthogToken, {
    api_host: posthogHost,
    capture_pageview: false, // Budeme zachytávat manuálně pro sjednocení cest
    persistence: consent ? 'localStorage+cookie' : 'memory',
    disable_cookies: !consent,
    save_referrer: true,
    store_google_advertiser_ids: true,
  });

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
      // PostHog automaticky sbírá UTM a referrer, pokud jsou v URL/document, 
      // ale pro jistotu je můžeme explicitně zdůraznit, pokud by byl problém se SPA navigací
    });
  };

  captureUnifiedPageview();

  // Pro zachycení změn v SPA (pokud by se měnila URL bez reloadu)
  window.addEventListener('popstate', captureUnifiedPageview);
} else {
  console.log('🛠️ Debug mód: PostHog analytika je vypnutá.');
}

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <PostHogProvider client={posthog}>
      <DialogProvider>
        <GameProvider>
          <ZoomProvider >
            <ZoomDialog />
            <Dialog />
            <Toaster position={'top-right'} containerStyle={{fontSize:"1.4em"}} toastOptions={{className:"toastsForCSS"}}/>
            <App />
          </ZoomProvider>
        </GameProvider>
      </DialogProvider>
    </PostHogProvider>
  </StrictMode>,
)
