import { Routes, Route, NavLink } from "react-router-dom";
import Dashboard from "./pages/Dashboard.jsx";
import Customers from "./pages/Customers.jsx";
import Products from "./pages/Products.jsx";
import Orders from "./pages/Orders.jsx";

export default function App() {
  return (
    <div className="app">
      <aside className="sidebar">
        <div className="brand">
          <span className="brand-mark">PT</span>
          <span>PixelTech ERP</span>
        </div>
        <nav>
          <NavLink to="/" end>Dashboard</NavLink>
          <NavLink to="/customers">Customers</NavLink>
          <NavLink to="/products">Products</NavLink>
          <NavLink to="/orders">Orders</NavLink>
        </nav>
        <div className="sidebar-foot">v1.0 &middot; AI-103</div>
      </aside>

      <main className="content">
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/customers" element={<Customers />} />
          <Route path="/products" element={<Products />} />
          <Route path="/orders" element={<Orders />} />
        </Routes>
      </main>
    </div>
  );
}
