import type { Metadata } from 'next';
import './globals.css';

export const metadata: Metadata = {
  title: 'KONSTRIVO PRO — Plateforme Intelligente BTP & Devis',
  description: 'Calculateur intelligent, chiffrage et devis pour artisans du bâtiment',
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="fr">
      <body className="antialiased">{children}</body>
    </html>
  );
}
