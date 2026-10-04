import { lazy, type ComponentType } from 'react';

/**
 * Bezpečně líně načte komponentu a v případě selhání (např. zastaralé CSS/JS chunky po novém nasazení) automaticky obnoví stránku.
 */
export function lazyWithRetry<T extends ComponentType<any>>(
  componentImport: () => Promise<{ default: T }>
) {
  return lazy(async () => {
    try {
      return await componentImport();
    } catch (error: any) {
      console.error('Chyba při stahování komponenty / CSS chunků:', error);

      const reloadKey = 'chunk_load_reload_timestamp';
      const lastReload = sessionStorage.getItem(reloadKey);
      const now = Date.now();

      // Prevence nekonečné smyčky reloadů (max 1 reload za 15 sekund)
      if (!lastReload || now - parseInt(lastReload, 10) > 15000) {
        sessionStorage.setItem(reloadKey, String(now));
        window.location.reload();
      }

      throw error;
    }
  });
}
