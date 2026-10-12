export default function NotFoundPage() {
  return (
    <div className="min-h-screen flex flex-col items-center justify-center bg-slate-50 text-slate-900 px-4">
      <h1 className="text-4xl font-extrabold mb-2">404</h1>
      <p className="text-lg text-slate-600 mb-6">Page non trouvée / Page Not Found</p>
      <a
        href="/"
        className="px-4 py-2 bg-amber-600 text-white rounded-lg hover:bg-amber-700 transition-colors font-medium text-sm"
      >
        Retour à l'accueil
      </a>
    </div>
  );
}
