import { chromium, expect } from '@playwright/test';
import { mkdir, writeFile } from 'node:fs/promises';
import { dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';

const scriptDir = dirname(fileURLToPath(import.meta.url));
const repoRoot = resolve(scriptDir, '../..');
const evidenceDir = resolve(repoRoot, 'docs/evidence');
const screenshotsDir = resolve(evidenceDir, 'screenshots');
const baseUrl = process.env.TALLERC4_URL ?? 'http://127.0.0.1:5173/';
const generatedAt = new Date().toISOString();
const results = [];

await mkdir(screenshotsDir, { recursive: true });

async function check(name, action) {
  try {
    await action();
    results.push({ name, status: 'PASS' });
  } catch (error) {
    results.push({ name, status: 'FAIL', detail: error instanceof Error ? error.message : String(error) });
    throw error;
  }
}

async function capture(page, filename) {
  await page.screenshot({ path: resolve(screenshotsDir, filename), fullPage: true });
}

const browser = await chromium.launch({ channel: 'msedge', headless: true });
const page = await browser.newPage({ viewport: { width: 1365, height: 900 } });

try {
  await page.goto(baseUrl, { waitUntil: 'networkidle' });
  await expect(page.getByRole('heading', { name: /TallerC4/i }).first()).toBeVisible();
  await capture(page, 'ux-after-desktop.png');
  await page.setViewportSize({ width: 390, height: 844 });
  await page.reload({ waitUntil: 'networkidle' });
  await capture(page, 'ux-after-mobile.png');
  await page.setViewportSize({ width: 1365, height: 900 });
  await page.reload({ waitUntil: 'networkidle' });
  await page.getByRole('button', { name: /consultar contribuyente/i }).hover();
  await capture(page, 'ux-hover.png');

  await check('Tarea 1 — consulta RUC en SRI', async () => {
    await page.getByLabel(/correo electrónico/i).fill('demo@example.com');
    await page.getByLabel(/^RUC/i).fill('1790016919001');
    await page.getByRole('button', { name: /consultar contribuyente/i }).click();
    await expect(page.getByRole('article', { name: 'Resultado del contribuyente' })).toContainText('ACTIVO');
    await capture(page, 'api-sri-taxpayer.png');
  });

  await page.reload({ waitUntil: 'networkidle' });

  await check('Tarea 2 — consulta de placa en SRI', async () => {
    await page.getByLabel(/placa/i).fill('PEI7954');
    await page.getByRole('button', { name: /consultar vehículo/i }).click();
    await expect(page.getByRole('article', { name: 'Resultado del vehículo' })).toContainText('Pagos reportados');
    await capture(page, 'api-sri-vehicle.png');
  });

  await page.reload({ waitUntil: 'networkidle' });
  const antId = page.getByLabel(/identificación para ANT/i);
  const antScenario = page.getByLabel(/escenario de demostración/i);
  const antSubmit = page.locator('#ant button[type="submit"]');
  await antId.fill('0000000099');

  await check('Tarea 3 — consulta ANT simulada exitosa', async () => {
    await antScenario.selectOption('success');
    await page.route(/\/api\/v1\/ant\/points/, async (route) => {
      await new Promise((resolve) => setTimeout(resolve, 900));
      await route.continue();
    });
    await antSubmit.click();
    await expect(antSubmit).toHaveAttribute('aria-busy', 'true');
    await expect(page.getByText(/consultando datos/i)).toBeVisible();
    await capture(page, 'ux-loading.png');
    await expect(page.getByRole('article', { name: 'Resultado ANT' })).toContainText('30 puntos');
    await page.unroute(/\/api\/v1\/ant\/points/);
    await expect(page.getByText('Simulado', { exact: true })).toBeVisible();
    await expect(page.getByText('Consulta ANT lista', { exact: true })).toBeVisible();
    await capture(page, 'ux-toast.png');
    await page.getByText('Ver detalles', { exact: true }).click();
    await expect(page.getByText('Ocultar detalles', { exact: true })).toBeVisible();
    await capture(page, 'ux-results.png');
  });

  await check('ANT timeout — recuperación stale', async () => {
    await antScenario.selectOption('timeout');
    await antSubmit.click();
    await expect(page.getByText('Datos stale', { exact: true })).toBeVisible();
    await expect(page.locator('.stale-warning')).toContainText('ANT no respondió');
  });

  await check('ANT HTTP 502 — conserva último resultado válido', async () => {
    await antScenario.selectOption('bad-gateway');
    await antSubmit.click();
    await expect(page.getByText('Datos stale', { exact: true })).toBeVisible();
    await expect(page.getByRole('article', { name: 'Resultado ANT' })).toContainText('30 puntos');
    await page.getByText('Ver detalles', { exact: true }).click();
    await expect(page.getByText(/Resultado recuperado de cache ANT/i)).toBeVisible();
  });

  await check('ANT timeout sin cache — servicio no disponible', async () => {
    await antId.fill('0000000022');
    await antScenario.selectOption('timeout');
    await antSubmit.click();
    await expect(page.locator('.message-error')).toContainText('Servicio no disponible');
  });
} finally {
  const report = [
    '# Reporte de verificación automatizada de usabilidad',
    '',
    `- Generado: ${generatedAt}`,
    `- URL: ${baseUrl}`,
    '- Condición: backend local con `ANT_PROVIDER=simulated`, cache en memoria y sin credenciales Redis.',
    '- Datos de formulario: correo sintético; RUC corporativo y placa de prueba usados únicamente contra los endpoints SRI del entorno local.',
    '- Esta ejecución automatizada no equivale a una prueba con participantes humanos.',
    '',
    '## Resultados',
    '',
    ...results.map((result) => `- **${result.status}** — ${result.name}${result.detail ? ` — ${result.detail}` : ''}`),
    '',
    '## Evidencia visual',
    '',
    '- `screenshots/01-initial-responsive-flow.png` — flujo inicial conservado de la evidencia previa.',
    '- `screenshots/01b-mobile-responsive-flow.png` — flujo móvil conservado de la evidencia previa.',
    '- `screenshots/ux-before.png` — diseño original conservado antes del cambio.',
    '- `screenshots/ux-after-desktop.png` — diseño renovado en viewport desktop.',
    '- `screenshots/ux-after-mobile.png` — diseño renovado en viewport móvil.',
    '- `screenshots/ux-hover.png` — hover real sobre el CTA de contribuyente.',
    '- `screenshots/ux-toast.png` — toast real de éxito ANT.',
    '- `screenshots/ux-loading.png` — loading real con respuesta demorada.',
    '- `screenshots/ux-results.png` — resultado ANT expandido con procedencia.',
    '- `screenshots/api-sri-taxpayer.png` — resultado SRI de contribuyente en flujo E2E.',
    '- `screenshots/api-sri-vehicle.png` — resultado SRI vehicular en flujo E2E.',
    '- `screenshots/02-ant-simulated-success.png` — resultado ANT simulado conservado de la evidencia previa.',
    '- `screenshots/03-ant-stale-after-timeout.png` — timeout stale conservado de la evidencia previa.',
    '- `screenshots/04-ant-recovery-after-502.png` — recuperación 502 conservada de la evidencia previa.',
    '- `screenshots/05-ant-unavailable-without-cache.png` — error sin cache conservado de la evidencia previa.',
    '',
    '## Resultados humanos pendientes',
    '',
    'La sesión con participantes humanos sigue pendiente. Usar `docs/usability/protocol.md` y `docs/usability/recording-template.md` para registrar observaciones reales.',
    '',
  ].join('\n');
  await writeFile(resolve(evidenceDir, 'automated-browser-report.md'), report, 'utf8');
  await browser.close();
}

if (results.some((result) => result.status === 'FAIL')) process.exitCode = 1;
