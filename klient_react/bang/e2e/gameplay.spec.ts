import { test, expect } from '@playwright/test';

test.describe('Bang Multiplayer E2E', () => {
  test('Dva hráči se připojí do stejné hry přes lobby', async ({ browser }) => {
    // 1. Kontext pro Hráče 1 (Host / Admin)
    const hostContext = await browser.newContext();
    const hostPage = await hostContext.newPage();

    // 2. Kontext pro Hráče 2 (Hostující hráč)
    const guestContext = await browser.newContext();
    const guestPage = await guestContext.newPage();

    // Host otevře aplikaci
    await hostPage.goto('/?mock=false');
    await expect(hostPage.getByRole('button', { name: /vytvořit novou hru/i })).toBeVisible();

    // Host klikne na Vytvořit novou hru
    await hostPage.getByRole('button', { name: /vytvořit novou hru/i }).click();

    // Vyplní jméno a založí hru
    const hostNameInput = hostPage.locator('input').last();
    await hostNameInput.fill('HostHrac');
    await hostPage.getByRole('button', { name: /^vytvořit hru$/i }).click();

    // Počkáme na přechod do lobby a načtení kódu hry
    await expect(hostPage.getByText(/čekání na další hráče/i)).toBeVisible({ timeout: 10000 });
    const codeElement = hostPage.locator('a[class*="gameCode"]');
    await expect(codeElement).toBeVisible();
    const gameCode = (await codeElement.textContent())?.trim();
    expect(gameCode).toMatch(/^[0-9]+$/);

    // Hostující hráč (Guest) otevře aplikaci
    await guestPage.goto('/?mock=false');
    await expect(guestPage.getByRole('button', { name: /připojit se ke hře/i })).toBeVisible();

    // Guest klikne na Připojit se ke hře
    await guestPage.getByRole('button', { name: /připojit se ke hře/i }).click();

    // Vyplní kód a jméno
    const guestInputs = guestPage.locator('input');
    await guestInputs.nth(0).fill(gameCode!);
    await guestInputs.nth(1).fill('GuestHrac');
    await guestPage.getByRole('button', { name: /^připojit se ke hře$/i }).click();

    // Počkáme, až se Guest ocitne v lobby
    await expect(guestPage.getByText(/čekání na další hráče/i)).toBeVisible({ timeout: 10000 });

    // Host vidí Guest hráče v seznamu hráčů
    await expect(hostPage.getByText('GuestHrac')).toBeVisible({ timeout: 10000 });

    // Host má viditelné tlačítko pro spuštění hry (jsou již 2 hráči)
    const startButton = hostPage.getByRole('button', { name: /spustit hru/i });
    await expect(startButton).toBeVisible();

    // Spuštění hry
    await startButton.click();

    // Úklid
    await hostContext.close();
    await guestContext.close();
  });
});
