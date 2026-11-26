// @ts-check
import { test, expect } from '@playwright/test';

/**
 * Smoke tests that need only the frontend (no backend). API calls are allowed
 * to fail; the UI must still render.
 */
test.describe('smoke @cross-browser', () => {
  test('home page renders the hero section', async ({ page }) => {
    await page.goto('/');
    await expect(page.locator('h1').first()).toBeVisible();
  });

  test('login page renders the login form', async ({ page }) => {
    await page.goto('/login');
    await expect(page.getByRole('heading', { name: 'Welcome Back!' })).toBeVisible();
    await expect(page.locator('input[type="email"]')).toBeVisible();
    await expect(page.locator('input[type="password"]')).toBeVisible();
  });
});
