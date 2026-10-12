"use client";

import { SUPPORTED_TRADES } from '@/config/trades';
import { SUPPORTED_MARKETS } from '@/config/markets';
import { SUPPORTED_LANGUAGES } from '@/config/languages';
import { useEffect, useMemo, useState } from 'react';
import { verifyAdminCredential } from '@/lib/adminAuth';

// R-001: local authority gate config. Static export embeds these in the client
// bundle — they are NOT secrets (salted PBKDF2 hash material only).
const ADMIN_EMAIL_CONFIG = process.env.NEXT_PUBLIC_ADMIN_EMAIL ?? '';
const ADMIN_CREDENTIAL_CONFIG = process.env.NEXT_PUBLIC_ADMIN_CREDENTIAL ?? '';

export default function HomePage() {
  const trades = Object.values(SUPPORTED_TRADES);
  const markets = Object.values(SUPPORTED_MARKETS);
  const languages = Object.values(SUPPORTED_LANGUAGES);

  useEffect(() => {
    // Helper to read a numeric input value
    const num = (id: string) => {
      const el = document.getElementById(id) as HTMLInputElement | null;
      if (!el) return 0;
      const v = parseFloat(el.value as any);
      return isFinite(v) ? v : 0;
    };

    const setText = (id: string, txt: string) => {
      const el = document.getElementById(id);
      if (el) el.textContent = txt;
    };

    const verifyBtn = document.getElementById('verifyBtn');
    const downloadBtn = document.getElementById('downloadBtn');
    const downloadHtml = document.getElementById('downloadHtml');
    const badgeContainer = document.getElementById('badgeContainer');
    const auditAlert = document.getElementById('auditAlert');
    const breakdown = document.getElementById('breakdown');
    const breakdownContent = document.getElementById('breakdownContent');

    const computeTotals = (materials: number, labor: number, misc: number, vatRate: number, stampMode: string, stampValue: number) => {
      const subtotal = +(materials + labor + misc).toFixed(2);
      const vat = +((subtotal * vatRate) / 100).toFixed(2);
      const stamp = stampMode === 'percent' ? +((subtotal * stampValue) / 100).toFixed(2) : +(+stampValue).toFixed(2);
      const total = +(subtotal + vat + stamp).toFixed(2);
      return { subtotal, vat, stamp, total };
    };

    const renderBadge = (ok: boolean) => {
      if (!badgeContainer) return;
      badgeContainer.innerHTML = ok
        ? '<span class="text-xs px-3 py-1 rounded-full bg-emerald-500 text-white font-semibold">Calcul Vérifié & Conforme</span>'
        : '<span class="text-xs px-3 py-1 rounded-full bg-amber-500 text-white font-semibold">Calcul Vérifié — Attention</span>';
    };

    const runAudit = () => {
      // read inputs
      const surface = num('surface');
      const panels = num('panels');
      const profiles = num('profiles');
      const screws = num('screws');
      const jointKg = num('jointKg');
      const materialsCost = num('materialsCost');
      const laborCost = num('laborCost');
      const miscCost = num('miscCost');
      const vatRate = num('vatRate');
      const stampModeEl = document.getElementById('stampMode') as HTMLSelectElement | null;
      const stampMode = stampModeEl ? stampModeEl.value : 'fixed';
      const stampValue = num('stampValue');

      const issues: string[] = [];

      // Basic validations
      if (!(surface > 0)) issues.push('Surface doit être supérieure à 0 m²');
      if (panels < 0) issues.push('Nombre de panneaux invalide');
      if (profiles < 0) issues.push('Profilés métalliques invalides');
      if (screws < 0) issues.push('Quantité de vis invalide');
      if (jointKg < 0) issues.push('Quantité d\'enduit invalide');
      if (materialsCost < 0 || laborCost < 0 || miscCost < 0) issues.push('Les coûts doivent être positifs');

      // Ratio checks
      const panelsPerM2 = surface > 0 ? panels / surface : 0;
      if (panelsPerM2 > 5 || panelsPerM2 < 0.01) issues.push(`Ratio panneaux/m² inattendu: ${panelsPerM2.toFixed(3)}`);

      const screwsPerPanel = panels > 0 ? screws / panels : 0;
      if (screwsPerPanel < 2 || screwsPerPanel > 50) issues.push(`Vis/panneau hors plage réaliste: ${screwsPerPanel.toFixed(2)}`);

      const profilesPerM2 = surface > 0 ? profiles / surface : 0;
      if (profilesPerM2 < 0.05 || profilesPerM2 > 10) issues.push(`Profilés (m)/m² hors plage réaliste: ${profilesPerM2.toFixed(3)}`);

      const jointPerM2 = surface > 0 ? jointKg / surface : 0;
      if (jointPerM2 < 0.01 || jointPerM2 > 2) issues.push(`Enduit (kg)/m² hors plage réaliste: ${jointPerM2.toFixed(3)}`);

      const laborPerM2 = surface > 0 ? laborCost / surface : 0;
      if (laborPerM2 < 0 || laborPerM2 > 1000) issues.push(`Coût main d'œuvre/m² suspect: ${laborPerM2.toFixed(2)}`);

      const totals = computeTotals(materialsCost, laborCost, miscCost, vatRate, stampMode, stampValue);

      // Update UI
      if (auditAlert) {
        auditAlert.className = issues.length > 0 ? 'mt-4 p-3 rounded bg-amber-100 text-amber-900' : 'mt-4 p-3 rounded bg-emerald-100 text-emerald-900';
        auditAlert.innerHTML = issues.length > 0 ? `<strong>Avertissement:</strong><ul>${issues.map(i => `<li>${i}</li>`).join('')}</ul>` : '<strong>OK</strong> — Aucun problème détecté.';
      }

      renderBadge(issues.length === 0);

      if (breakdown) breakdown.classList.remove('hidden');
      if (breakdownContent) {
        breakdownContent.innerHTML = `
          <div>Surface: ${surface} m²</div>
          <div>Panneaux: ${panels} (p/m²: ${panelsPerM2.toFixed(3)})</div>
          <div>Profilés: ${profiles} m (m/m²: ${profilesPerM2.toFixed(3)})</div>
          <div>Vis: ${screws} (vis/panneau: ${screwsPerPanel.toFixed(2)})</div>
          <div>Enduit: ${jointKg} kg (kg/m²: ${jointPerM2.toFixed(3)})</div>
        `;
      }

      setText('subTotalDisplay', totals.subtotal.toFixed(2));
      setText('vatDisplay', totals.vat.toFixed(2));
      setText('stampDisplay', totals.stamp.toFixed(2));
      setText('totalDisplay', totals.total.toFixed(2));

      return { issues, totals };
    };

    verifyBtn?.addEventListener('click', (e) => {
      e.preventDefault();
      runAudit();
    });

    downloadBtn?.addEventListener('click', (e) => {
      e.preventDefault();
      const { totals } = runAudit();
      const content = `KONSTRIVO — Verified Audit Summary\n\nSubtotal (HT): ${totals.subtotal}\nTVA: ${totals.vat}\nTimbre fiscal: ${totals.stamp}\nTotal (TTC): ${totals.total}\n\nGenerated: ${new Date().toISOString()}`;
      // Open printable window
      const w = window.open('', '_blank');
      if (w) {
        w.document.write('<pre style="font-family:system-ui,Segoe UI,Roboto,Helvetica,Arial; padding:18px;">' + content + '</pre>');
        w.document.title = 'KONSTRIVO — Verified Audit';
        w.print();
      }
    });

    downloadHtml?.addEventListener('click', (e) => {
      e.preventDefault();
      const { totals, issues } = runAudit();
      const html = `<!doctype html><html><head><meta charset="utf-8"><title>KONSTRIVO Audit</title></head><body><h2>KONSTRIVO — Verified Audit Summary</h2><pre>Subtotal (HT): ${totals.subtotal}\nTVA: ${totals.vat}\nTimbre fiscal: ${totals.stamp}\nTotal (TTC): ${totals.total}\n</pre><h3>Issues</h3><pre>${issues.length ? issues.join('\n') : 'None'}</pre></body></html>`;
      const blob = new Blob([html], { type: 'text/html' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = 'konstrivo-verified-audit.html';
      document.body.appendChild(a);
      a.click();
      a.remove();
      URL.revokeObjectURL(url);
    });

    return () => {
      verifyBtn?.removeEventListener('click', () => {});
      downloadBtn?.removeEventListener('click', () => {});
      downloadHtml?.removeEventListener('click', () => {});
    };
  }, []);

  // --- Admin state (local, sessionStorage) ---
  const [isAdmin, setIsAdmin] = useState<boolean>(() => {
    try { return (typeof window !== 'undefined') && sessionStorage.getItem('konstrivo_is_admin') === '1'; } catch { return false; }
  });
  const [adminEmail, setAdminEmail] = useState('');
  const [adminPass, setAdminPass] = useState('');

  const GLOBAL_PRICES_KEY = 'konstrivo_global_prices';
  const ARTISANS_KEY = 'konstrivo_artisans';
  const USERS_KEY = 'konstrivo_users';
  const ESTIMATES_KEY = 'konstrivo_estimates';

  const defaultGlobalPrices = { ba13: 18.0, hydrofuge: 24.0, labor: 12.0 };

  const loadGlobalPrices = () => {
    try {
      if (typeof window === 'undefined') return defaultGlobalPrices;
      const raw = localStorage.getItem(GLOBAL_PRICES_KEY);
      return raw ? JSON.parse(raw) : defaultGlobalPrices;
    } catch { return defaultGlobalPrices; }
  };

  const [globalPrices, setGlobalPrices] = useState(() => loadGlobalPrices());

  useEffect(() => {
    try { localStorage.setItem(GLOBAL_PRICES_KEY, JSON.stringify(globalPrices)); } catch {}
  }, [globalPrices]);

  const loadArray = (key: string, seed: any[] = []): any[] => {
    try { if (typeof window === 'undefined') return seed; const s = localStorage.getItem(key); return s ? JSON.parse(s) : seed; } catch { return seed; }
  };

  const [artisans, setArtisans] = useState(() => loadArray(ARTISANS_KEY, [
    { id: 'artisan_1', name: 'Ali Ben Salah', city: 'Tunis', phone: '+21690000000', plan: 'Free', isPro: false },
    { id: 'artisan_2', name: 'Sana Haddad', city: 'Sfax', phone: '+21691000001', plan: 'Pro', isPro: true }
  ]));

  const [users, setUsers] = useState(() => loadArray(USERS_KEY, [
    { id: 'u1', email: 'client1@example.com' }
  ]));

  const [estimates, setEstimates] = useState(() => loadArray(ESTIMATES_KEY, [
    { id: 'e1' }, { id: 'e2' }
  ]));

  useEffect(() => { try { localStorage.setItem(ARTISANS_KEY, JSON.stringify(artisans)); } catch {} }, [artisans]);
  useEffect(() => { try { localStorage.setItem(USERS_KEY, JSON.stringify(users)); } catch {} }, [users]);
  useEffect(() => { try { localStorage.setItem(ESTIMATES_KEY, JSON.stringify(estimates)); } catch {} }, [estimates]);

  const metrics = useMemo(() => ({
    usersCount: users.length,
    proArtisans: artisans.filter((a: any) => a.isPro).length,
    estimatesCount: estimates.length
  }), [users, artisans, estimates]);

  const loginAdmin = (e?: Event) => {
    if (e) e.preventDefault();
    // R-001: local authority gate only — static export ships the credential in the
    // bundle (it is NOT a secret); verify PBKDF2 hash, never store plaintext.
    void (async () => {
      const ok = await verifyAdminCredential(
        adminEmail,
        adminPass,
        ADMIN_EMAIL_CONFIG,
        ADMIN_CREDENTIAL_CONFIG,
      );
      if (ok) {
        try { sessionStorage.setItem('konstrivo_is_admin', '1'); } catch {}
        setIsAdmin(true);
      } else {
        alert('Invalid admin credentials');
      }
    })();
  };

  const logoutAdmin = () => {
    try { sessionStorage.removeItem('konstrivo_is_admin'); } catch {}
    setIsAdmin(false);
    setAdminEmail(''); setAdminPass('');
  };

  const togglePro = (id: string) => {
    setArtisans((prev: any[]) => prev.map(a => a.id === id ? { ...a, isPro: !a.isPro, plan: a.isPro ? 'Free' : 'Pro' } : a));
  };

  const deleteArtisan = (id: string) => {
    setArtisans((prev: any[]) => prev.filter(a => a.id !== id));
  };

  const updateGlobalPrice = (field: string, value: number) => {
    setGlobalPrices((p: any) => ({ ...p, [field]: value }));
  };

  // Pro Features (saved to localStorage)
  const PRO_FEATURES_KEY = 'konstrivo_pro_features';
  const loadProFeatures = () => {
    try { if (typeof window === 'undefined') return ['Support WhatsApp', 'Priority Listing']; const s = localStorage.getItem(PRO_FEATURES_KEY); return s ? JSON.parse(s) : ['Support WhatsApp', 'Priority Listing']; } catch { return ['Support WhatsApp', 'Priority Listing']; }
  };
  const [proFeatures, setProFeatures] = useState<string[]>(() => loadProFeatures());
  const [newFeatureText, setNewFeatureText] = useState('');
  useEffect(() => { try { localStorage.setItem(PRO_FEATURES_KEY, JSON.stringify(proFeatures)); } catch {} }, [proFeatures]);

  // Materials list (CSV import target)
  const MATERIALS_KEY = 'konstrivo_materials';
  const loadMaterials = (): any[] => { try { if (typeof window === 'undefined') return []; const s = localStorage.getItem(MATERIALS_KEY); return s ? JSON.parse(s) : []; } catch { return []; } };
  const [materials, setMaterials] = useState(() => loadMaterials());
  useEffect(() => { try { localStorage.setItem(MATERIALS_KEY, JSON.stringify(materials)); } catch {} }, [materials]);

  const addProFeature = () => { if (!newFeatureText.trim()) return; setProFeatures(p => [...p, newFeatureText.trim()]); setNewFeatureText(''); };

  // CSV parsing (simple RFC4180-ish parser)
  const parseCSV = (text: string) => {
    const rows: string[][] = [];
    const lines = text.split(/\r?\n/);
    for (let i = 0; i < lines.length; i++) {
      const line = lines[i].trim();
      if (!line) continue;
      const cols: string[] = [];
      let cur = '';
      let inQuotes = false;
      for (let j = 0; j < line.length; j++) {
        const ch = line[j];
        if (ch === '"') {
          if (inQuotes && line[j+1] === '"') { cur += '"'; j++; }
          else inQuotes = !inQuotes;
        } else if (ch === ',' && !inQuotes) { cols.push(cur); cur = ''; }
        else cur += ch;
      }
      cols.push(cur);
      rows.push(cols);
    }
    return rows;
  };

  const handleImportFile = (file: File|null) => {
    if (!file) return alert('No file selected');
    const reader = new FileReader();
    reader.onload = (ev) => {
      const text = String(ev.target?.result || '');
      try {
        const rows = parseCSV(text);
        if (rows.length === 0) return alert('CSV empty');
        const header = rows[0].map(h => h.trim().toLowerCase());
        const items: any[] = [];
        for (let i = 1; i < rows.length; i++) {
          const r = rows[i];
          if (r.length === 0) continue;
          const obj: any = {};
          header.forEach((h, idx) => { obj[h] = r[idx] !== undefined ? r[idx].trim() : ''; });
          // map expected fields
          const id = obj['id'] || obj['materialid'] || (obj['name'] ? obj['name'].toLowerCase().replace(/\s+/g,'_') : `m_${i}`);
          const category = obj['category'] || 'unknown';
          const nameFr = obj['namefr'] || obj['name'] || obj['name_fr'] || '';
          const defaultPrice = parseFloat(obj['defaultprice'] || obj['default_price'] || obj['price'] || '0') || 0;
          const currentPrice = parseFloat(obj['currentprice'] || obj['current_price'] || obj['price'] || String(defaultPrice)) || defaultPrice;
          const currency = obj['currency'] || 'TND';
          items.push({ id, category, nameFr, defaultPrice, currentPrice, currency });
          // if known material ids update global prices
          if (/ba13/i.test(id) && !/hydrofuge/i.test(id)) updateGlobalPrice('ba13', currentPrice);
          if (/hydrofuge/i.test(id)) updateGlobalPrice('hydrofuge', currentPrice);
        }
        // merge into materials state (by id)
        setMaterials(prev => {
          const map = new Map(prev.map((m: any) => [m.id, m]));
          items.forEach(it => map.set(it.id, it));
          return Array.from(map.values());
        });
        alert(`Imported ${items.length} rows`);
      } catch (e) { alert('Failed to parse file: ' + e); }
    };
    reader.readAsText(file, 'utf-8');
  };


  return (
    <div className="min-h-screen flex flex-col bg-slate-50 text-slate-900">
      <header className="sticky top-0 z-50 bg-white/90 backdrop-blur border-b border-slate-200 shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
          <div className="flex items-center space-x-3">
            <span className="text-2xl">🏗️</span>
            <div>
              <span className="font-bold text-lg tracking-tight text-slate-900">KONSTRIVO</span>
              <span className="ml-1 text-xs font-semibold px-2 py-0.5 rounded-full bg-amber-100 text-amber-800">
                PRO
              </span>
            </div>
          </div>
          <nav className="flex items-center space-x-6 text-sm font-medium text-slate-600">
            <span className="text-emerald-700 bg-emerald-50 px-3 py-1 rounded-full text-xs font-semibold">
              API Ready: v1
            </span>
          </nav>
        </div>
      </header>

      {/* Admin full-screen dashboard overlay */}
      {isAdmin && (
        <div style={{ backgroundColor: '#0b0f17' }} className="fixed inset-0 z-50 text-slate-50 overflow-auto p-8">
          <div className="max-w-6xl mx-auto space-y-6">
            <div className="flex items-center justify-between">
              <h2 className="text-2xl font-extrabold">Espace Administration Général — Owner Admin Dashboard</h2>
              <div className="flex items-center gap-3">
                <button onClick={logoutAdmin} className="px-3 py-2 bg-amber-500 hover:bg-amber-400 rounded text-slate-900 font-semibold">Logout</button>
              </div>
            </div>

            {/* Metrics Bar */}
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
              <div className="p-4 rounded shadow" style={{ backgroundColor: '#08101a' }}>
                <div className="text-sm text-slate-400">Total Registered Users</div>
                <div className="text-3xl font-bold">{metrics.usersCount}</div>
              </div>
              <div className="p-4 rounded shadow" style={{ backgroundColor: '#08101a' }}>
                <div className="text-sm text-slate-400">Active PRO Artisans</div>
                <div className="text-3xl font-bold">{metrics.proArtisans}</div>
              </div>
              <div className="p-4 rounded shadow" style={{ backgroundColor: '#08101a' }}>
                <div className="text-sm text-slate-400">Total Devis Calculés</div>
                <div className="text-3xl font-bold">{metrics.estimatesCount}</div>
              </div>
            </div>

            {/* Artisan Management Table */}
            <section className="bg-slate-900 p-4 rounded">
              <h3 className="font-bold mb-3">Artisan Management</h3>
              <div className="overflow-auto">
                <table className="w-full text-sm">
                  <thead>
                    <tr className="text-slate-400 text-left">
                      <th className="py-2">Name</th>
                      <th>City</th>
                      <th>Phone</th>
                      <th>Plan</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {artisans.map((a: any) => (
                      <tr key={a.id} className="border-t border-slate-800">
                        <td className="py-2">{a.name}</td>
                        <td>{a.city}</td>
                        <td>{a.phone}</td>
                        <td>{a.plan}</td>
                        <td>
                          <button onClick={() => togglePro(a.id)} className="mr-2 px-2 py-1 bg-emerald-600 rounded text-xs">{a.isPro ? 'Revoke PRO' : 'Activate PRO'}</button>
                          <button onClick={() => deleteArtisan(a.id)} className="px-2 py-1 bg-rose-600 rounded text-xs">Delete</button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </section>

            {/* Global Price Manager */}
            <section className="bg-slate-900 p-4 rounded space-y-3">
              <h3 className="font-bold">Global Price Manager</h3>
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
                <div>
                  <label className="text-xs text-slate-300">BA13 rate / m²</label>
                  <input type="number" step="0.1" value={globalPrices.ba13} onChange={(e) => updateGlobalPrice('ba13', parseFloat(e.target.value || '0'))} className="w-full px-2 py-2 rounded bg-slate-800 text-slate-50" />
                </div>
                <div>
                  <label className="text-xs text-slate-300">Hydrofuge rate / m²</label>
                  <input type="number" step="0.1" value={globalPrices.hydrofuge} onChange={(e) => updateGlobalPrice('hydrofuge', parseFloat(e.target.value || '0'))} className="w-full px-2 py-2 rounded bg-slate-800 text-slate-50" />
                </div>
                <div>
                  <label className="text-xs text-slate-300">Labor rate / m²</label>
                  <input type="number" step="0.1" value={globalPrices.labor} onChange={(e) => updateGlobalPrice('labor', parseFloat(e.target.value || '0'))} className="w-full px-2 py-2 rounded bg-slate-800 text-slate-50" />
                </div>
              </div>
            </section>

            {/* Pro Features Editor */}
            <section className="bg-slate-900 p-4 rounded space-y-3">
              <h3 className="font-bold">Gestion des Offres Pro</h3>
              <div className="grid sm:grid-cols-2 gap-4">
                <div>
                  <div className="mb-2 text-slate-300">Features shown on the Plan Pro card</div>
                  <ul className="list-disc pl-5 text-sm text-slate-200">
                    {proFeatures.map((f, idx) => (
                      <li key={idx} className="flex items-center gap-2">
                        <input type="checkbox" checked readOnly className="mr-2" />
                        <span>{f}</span>
                      </li>
                    ))}
                  </ul>
                </div>
                <div>
                  <label className="text-xs text-slate-400">Ajouter une fonctionnalité</label>
                  <div className="flex gap-2 mt-2">
                    <input value={newFeatureText} onChange={(e) => setNewFeatureText(e.target.value)} className="flex-1 px-2 py-2 rounded bg-slate-800 text-slate-50" placeholder="Ex: Assistance prioritaire 24/7" />
                    <button onClick={addProFeature} className="px-3 py-2 bg-emerald-600 rounded">Ajouter</button>
                  </div>
                </div>
              </div>

              {/* Preview Plan Pro Card */}
              <div className="mt-3 p-3 rounded bg-slate-800">
                <div className="text-sm text-amber-400 font-semibold">Plan Pro — Aperçu</div>
                <h4 className="text-lg font-bold mt-1">Plan Pro</h4>
                <ul className="mt-2 list-disc pl-5 text-sm text-slate-200">
                  {proFeatures.map((f, i) => <li key={i}>{f}</li>)}
                </ul>
              </div>
            </section>

            {/* Material Import (CSV/Excel) */}
            <section className="bg-slate-900 p-4 rounded space-y-3">
              <h3 className="font-bold">Importer Catalogue BTP (CSV/Excel)</h3>
              <div className="flex items-center gap-3">
                <input id="materialFile" type="file" accept=".csv,.xls,.xlsx" className="text-sm" />
                <button onClick={() => {
                  const el = document.getElementById('materialFile') as HTMLInputElement | null;
                  if (!el || !el.files || el.files.length === 0) return alert('Sélectionnez un fichier CSV');
                  handleImportFile(el.files[0]);
                }} className="px-3 py-2 bg-amber-500 rounded">Importer Catalogue (CSV)</button>
                <div className="text-sm text-slate-400">Tip: Exporter Excel en CSV si votre fichier .xlsx n'est pas pris en charge.</div>
              </div>
              <div className="mt-2 text-sm text-slate-300">Matériaux importés: {materials.length}</div>
            </section>

            <div className="text-sm text-slate-400">Changes are saved locally (localStorage) and do not affect remote APIs.</div>
          </div>
        </div>
      )}

      <main className="flex-1 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 space-y-12 w-full">
        {/* Hero Section */}
        {!isAdmin && (
          <section className="max-w-3xl mx-auto mb-6">
            <div className="bg-white p-4 rounded-xl border border-slate-200 shadow-sm flex items-center justify-between">
              <div>
                <div className="text-sm text-slate-600">Owner Admin</div>
                <div className="text-xs text-slate-500">Login to access Owner Admin Dashboard</div>
              </div>
              <form onSubmit={(e) => { e.preventDefault(); loginAdmin(); }} className="flex items-center gap-2">
                <input placeholder="admin@konstrivo.com" value={adminEmail} onChange={(e) => setAdminEmail(e.target.value)} className="px-2 py-2 rounded bg-slate-50 border" />
                <input placeholder="password" type="password" value={adminPass} onChange={(e) => setAdminPass(e.target.value)} className="px-2 py-2 rounded bg-slate-50 border" />
                <button onClick={() => loginAdmin()} className="px-3 py-2 bg-amber-500 rounded font-semibold">Owner Login</button>
              </form>
            </div>
          </section>
        )}
        <section className="text-center max-w-3xl mx-auto space-y-4">
          <h1 className="text-4xl font-extrabold tracking-tight text-slate-900 sm:text-5xl">
            Plateforme Intelligente de Chiffrage BTP
          </h1>
          <p className="text-lg text-slate-600 leading-relaxed">
            Base logicielle unifiée pour artisans, entrepreneurs et quincailleries. Calculs métrés précis, barèmes régionaux et synchronisation devis.
          </p>
        </section>

        {/* Trades Grid */}
        <section className="space-y-6">
          <div className="flex items-center justify-between border-b pb-3">
            <h2 className="text-xl font-bold text-slate-900 flex items-center gap-2">
              <span>🧰</span> Corps d’État Disponibles (Trades)
            </h2>
            <span className="text-xs font-semibold px-2.5 py-1 rounded bg-slate-100 text-slate-700">
              {trades.length} Métiers Intégrés
            </span>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {trades.map((trade) => (
              <div
                key={trade.id}
                className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm hover:shadow transition-all space-y-3"
              >
                <div className="flex items-center justify-between">
                  <span className="text-3xl">{trade.iconEmoji}</span>
                  <span className="text-xs font-mono bg-slate-100 text-slate-600 px-2 py-0.5 rounded">
                    ID: {trade.id}
                  </span>
                </div>
                <h3 className="font-semibold text-lg text-slate-900 capitalize">
                  {trade.id}
                </h3>
                <p className="text-xs text-slate-500">
                  {trade.workTypeCount} type(s) d'ouvrages paramétrés
                </p>
              </div>
            ))}
          </div>
        </section>

        {/* Global Markets & Localization */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          {/* Markets Card */}
          <section className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm space-y-4">
            <h2 className="text-lg font-bold text-slate-900 flex items-center gap-2">
              <span>🌍</span> Marchés & Devises ({markets.length})
            </h2>
            <div className="divide-y divide-slate-100 text-sm">
              {markets.map((market) => (
                <div key={market.countryCode} className="py-2.5 flex items-center justify-between">
                  <span className="font-medium text-slate-700">{market.countryName}</span>
                  <div className="flex items-center gap-2">
                    <span className="text-xs font-mono bg-slate-100 text-slate-600 px-1.5 py-0.5 rounded">
                      {market.countryCode}
                    </span>
                    <span className="font-semibold text-amber-700 text-xs">
                      {market.currencySymbol} ({market.currencyCode})
                    </span>
                  </div>
                </div>
              ))}
            </div>
          </section>

          {/* Languages & RTL Card */}
          <section className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm space-y-4">
            <h2 className="text-lg font-bold text-slate-900 flex items-center gap-2">
              <span>🌐</span> Langues & Support RTL ({languages.length})
            </h2>
            <div className="divide-y divide-slate-100 text-sm">
              {languages.map((lang) => (
                <div key={lang.code} className="py-2.5 flex items-center justify-between">
                  <div>
                    <span className="font-medium text-slate-700">{lang.name}</span>
                    <span className="text-xs text-slate-400 ml-2">({lang.nativeName})</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${
                      lang.isRtl ? 'bg-purple-100 text-purple-700' : 'bg-blue-100 text-blue-700'
                    }`}>
                      {lang.direction.toUpperCase()}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          </section>
        </div>

        {/* Calculation Verification Card */}
        <section className="bg-slate-900 text-slate-50 p-6 rounded-xl border border-slate-800 shadow-lg">
          <div className="flex items-start justify-between">
            <h2 className="text-xl font-bold">🔍 Automated Calculation Audit — وحدة التحقق التلقائي</h2>
            <span className="text-xs px-3 py-1 rounded-full bg-emerald-600 text-emerald-50 font-semibold">Calcul Vérifié</span>
          </div>

          <p className="text-sm text-slate-300 mt-2">Entrez les métrés et coûts. Le moteur vérifiera la cohérence et calculera HT, TVA, timbre fiscal et TTC avec précision.</p>

          <div className="mt-4 grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="space-y-3">
              <label className="text-xs text-slate-300">Surface (m²)</label>
              <input id="surface" type="number" step="0.01" min="0" defaultValue={10} className="w-full px-3 py-2 rounded bg-slate-800 text-slate-50" />

              <label className="text-xs text-slate-300">Nombre de panneaux</label>
              <input id="panels" type="number" step="1" min="0" defaultValue={8} className="w-full px-3 py-2 rounded bg-slate-800 text-slate-50" />

              <label className="text-xs text-slate-300">Profilés métalliques (m total)</label>
              <input id="profiles" type="number" step="0.1" min="0" defaultValue={12} className="w-full px-3 py-2 rounded bg-slate-800 text-slate-50" />

              <label className="text-xs text-slate-300">Vis (qty)</label>
              <input id="screws" type="number" step="1" min="0" defaultValue={80} className="w-full px-3 py-2 rounded bg-slate-800 text-slate-50" />
            </div>

            <div className="space-y-3">
              <label className="text-xs text-slate-300">Enduit / Joint (kg)</label>
              <input id="jointKg" type="number" step="0.1" min="0" defaultValue={5} className="w-full px-3 py-2 rounded bg-slate-800 text-slate-50" />

              <label className="text-xs text-slate-300">Coût Matériaux (HT)</label>
              <input id="materialsCost" type="number" step="0.01" min="0" defaultValue={2500} className="w-full px-3 py-2 rounded bg-slate-800 text-slate-50" />

              <label className="text-xs text-slate-300">Coût Main d'œuvre (HT)</label>
              <input id="laborCost" type="number" step="0.01" min="0" defaultValue={1200} className="w-full px-3 py-2 rounded bg-slate-800 text-slate-50" />

              <label className="text-xs text-slate-300">Coût Divers (HT)</label>
              <input id="miscCost" type="number" step="0.01" min="0" defaultValue={50} className="w-full px-3 py-2 rounded bg-slate-800 text-slate-50" />
            </div>
          </div>

          <div className="mt-4 grid grid-cols-1 md:grid-cols-3 gap-3">
            <div>
              <label className="text-xs text-slate-300">TVA (%)</label>
              <input id="vatRate" type="number" step="0.1" min="0" defaultValue={19} className="w-full px-3 py-2 rounded bg-slate-800 text-slate-50" />
            </div>
            <div>
              <label className="text-xs text-slate-300">Timbre fiscal (mode)</label>
              <select id="stampMode" className="w-full px-3 py-2 rounded bg-slate-800 text-slate-50">
                <option value="fixed">Montant fixe</option>
                <option value="percent">Pourcentage</option>
              </select>
            </div>
            <div>
              <label className="text-xs text-slate-300">Timbre valeur</label>
              <input id="stampValue" type="number" step="0.01" min="0" defaultValue={0.6} className="w-full px-3 py-2 rounded bg-slate-800 text-slate-50" />
            </div>
          </div>

          <div className="mt-4 flex items-center gap-3">
            <button id="verifyBtn" className="px-4 py-2 bg-emerald-600 hover:bg-emerald-500 rounded font-semibold">Vérifier & Calculer</button>
            <button id="downloadBtn" className="px-3 py-2 bg-slate-700 hover:bg-slate-600 rounded text-slate-200">Download Verified Audit (Print/PDF)</button>
            <button id="downloadHtml" className="px-3 py-2 bg-slate-700 hover:bg-slate-600 rounded text-slate-200">Download Summary (HTML)</button>
            <div id="badgeContainer" className="ml-auto"></div>
          </div>

          <div id="auditAlert" className="mt-4 hidden"></div>

          <details id="breakdown" className="mt-4 bg-slate-800 p-3 rounded text-sm text-slate-300 hidden">
            <summary className="cursor-pointer font-medium">Détails de l'audit</summary>
            <div id="breakdownContent" className="mt-2"></div>
            <div className="mt-3 grid grid-cols-2 gap-2 text-xs text-slate-400">
              <div>SubTotal (HT): <span id="subTotalDisplay">-</span></div>
              <div>TVA: <span id="vatDisplay">-</span></div>
              <div>Timbre fiscal: <span id="stampDisplay">-</span></div>
              <div>Total (TTC): <span id="totalDisplay">-</span></div>
            </div>
          </details>
        </section>
      </main>

      <footer className="bg-white border-t border-slate-200 py-8 text-center text-xs text-slate-500">
        <p>© 2026 KONSTRIVO PRO. Architecture multi-plateforme Android / Web / API Cloud.</p>
      </footer>
    </div>
  );
}
