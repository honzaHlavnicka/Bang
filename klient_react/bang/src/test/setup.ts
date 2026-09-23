import '@testing-library/jest-dom/vitest';
import i18n from '../../i18n';

// Set predictable language for tests
i18n.changeLanguage('cs');

// Mock matchMedia for window
Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: (query: string) => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: () => {},
    removeListener: () => {},
    addEventListener: () => {},
    removeEventListener: () => {},
    dispatchEvent: () => false,
  }),
});
