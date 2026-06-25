import { useEffect, useState } from "react";
import { api } from "../api.js";

export default function Dashboard() {
  const [stats, setStats] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    api.stats().then(setStats).catch((e) => setError(e.message));
  }, []);

  if (error) return <div className="error">Could not load dashboard: {error}</div>;
  if (!stats) return <div className="muted">Loading…</div>;

  const cards = [
    { label: "Customers", value: stats.customers },
    { label: "Products", value: stats.products },
    { label: "Orders", value: stats.orders },
    { label: "Revenue", value: `$${Number(stats.revenue).toLocaleString()}` },
    { label: "Low-stock items", value: stats.lowStockProducts, warn: stats.lowStockProducts > 0 },
  ];

  return (
    <div>
      <h1>Dashboard</h1>
      <p className="muted">Live overview of the PixelTech ERP system.</p>
      <div className="card-grid">
        {cards.map((c) => (
          <div className={`stat-card ${c.warn ? "warn" : ""}`} key={c.label}>
            <div className="stat-value">{c.value}</div>
            <div className="stat-label">{c.label}</div>
          </div>
        ))}
      </div>
    </div>
  );
}
