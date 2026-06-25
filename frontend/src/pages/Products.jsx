import { useEffect, useState } from "react";
import { api } from "../api.js";

const EMPTY = { sku: "", name: "", category: "", description: "", price: "", stockQuantity: "" };

export default function Products() {
  const [products, setProducts] = useState([]);
  const [form, setForm] = useState(EMPTY);
  const [error, setError] = useState(null);

  const load = () => api.listProducts().then(setProducts).catch((e) => setError(e.message));
  useEffect(() => { load(); }, []);

  const submit = async (e) => {
    e.preventDefault();
    setError(null);
    try {
      await api.createProduct({
        ...form,
        price: parseFloat(form.price),
        stockQuantity: parseInt(form.stockQuantity, 10),
      });
      setForm(EMPTY);
      load();
    } catch (e) {
      setError(e.message);
    }
  };

  const remove = async (id) => {
    if (!confirm("Delete this product?")) return;
    try { await api.deleteProduct(id); load(); } catch (e) { setError(e.message); }
  };

  return (
    <div>
      <h1>Products &amp; Inventory</h1>
      {error && <div className="error">{error}</div>}

      <form className="form-row" onSubmit={submit}>
        <input placeholder="SKU" value={form.sku}
          onChange={(e) => setForm({ ...form, sku: e.target.value })} required />
        <input placeholder="Name" value={form.name}
          onChange={(e) => setForm({ ...form, name: e.target.value })} required />
        <input placeholder="Category" value={form.category}
          onChange={(e) => setForm({ ...form, category: e.target.value })} />
        <input placeholder="Price" type="number" step="0.01" value={form.price}
          onChange={(e) => setForm({ ...form, price: e.target.value })} required />
        <input placeholder="Stock" type="number" value={form.stockQuantity}
          onChange={(e) => setForm({ ...form, stockQuantity: e.target.value })} required />
        <button type="submit">Add</button>
      </form>

      <table className="data-table">
        <thead>
          <tr><th>ID</th><th>SKU</th><th>Name</th><th>Category</th><th>Price</th><th>Stock</th><th></th></tr>
        </thead>
        <tbody>
          {products.map((p) => (
            <tr key={p.id} className={p.stockQuantity <= 10 ? "row-warn" : ""}>
              <td>{p.id}</td><td>{p.sku}</td><td>{p.name}</td><td>{p.category}</td>
              <td>${Number(p.price).toFixed(2)}</td>
              <td>{p.stockQuantity}{p.stockQuantity <= 10 && <span className="badge">low</span>}</td>
              <td><button className="link-danger" onClick={() => remove(p.id)}>Delete</button></td>
            </tr>
          ))}
          {products.length === 0 && <tr><td colSpan="7" className="muted">No products yet.</td></tr>}
        </tbody>
      </table>
    </div>
  );
}
