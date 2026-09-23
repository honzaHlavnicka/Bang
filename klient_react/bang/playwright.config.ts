import { defineConfig, devices } from '@playwright/test';

/**
 * Konfigurace Playwright pro testování Bang klienta s běžícím backendem.
 */
export default defineConfig({
  testDir: './e2e',
  timeout: 30000,
  fullyParallel: false,
  workers: 1,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 2 : 0,
  reporter: 'list',
  use: {
    baseURL: 'http://localhost:5173',
    trace: 'on-first-retry',
  },
  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
  ],
  webServer: {
    command: 'java -jar server/target/server-1.0-SNAPSHOT.jar & npm --prefix klient_react/bang run dev -- --port 5173',
    url: 'http://localhost:5173',
    cwd: '../../',
    reuseExistingServer: !process.env.CI,
    timeout: 30000,
  },
});
