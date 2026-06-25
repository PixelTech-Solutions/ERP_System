import { useEffect, useState } from "react";
import { api } from "../api.js";

const STATUSES = ["PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED"];

export default function Orders() {
  const [orders, setOrders] = useState([]);
  const [customers, setCustomers] = useState([]);
  const [products, setProducts] = useState([]);
  const [customerId, setCustomerId] = useState("");
  const [lines, setLines] = useState([{ productId: "", quantity: 1 }]);
  const [error, setError] = useState(null);

  const load = () => {
    api.listOrders().then(setOrders).catch((e) => setError(e.message));
    api.listCustomers().then(setCustomers);
    api.listProducts().then(setProducts);
  };
  useEffect(() => { load(); }, []);

  const addLine = () => setLines([...lines, { productId: "", quantity: 1 }]);
  const updateLine = (i, field, value) => {
    const next = [...lines];
    next[i][field] = value;
    setLines(next);
  };
  const removeLine = (i) => setLines(lines.filter((_, idx) => idx !== i));

  const submit = async (e) => {
    e.preventDefault();
    setError(null);
    try {
      await api.createOrder({
        customerId: parseInt(customerId, 10),
        items: lines.map((l) => ({
          productId: parseInt(l.productId, 10),
          quantity: parseInt(l.quantity, 10),
        })),
      });
      setCustomerId("");
      setLines([{ productId: "", quantity: 1 }]);
      load();
    } catch (e) {
      setError(e.message);
    }
  };

  const changeStatus = async (id, status) => {
    try { await api.updateOrderStatus(id, status); load(); } catch (e) { setError(e.message); }
  };

  return (
    <div>
      <h1>Orders</h1>
      {error && <div className="error">{error}</div>}

      <form className="order-form" onSubmit={submit}>
        <div className="form-row">
          <select value={customerId} onChange={(e) => setCustomerId(e.target.value)} required>
            <option value="">Select customer…</option>
            {customers.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
          </select>
        </div>

        {lines.map((line, i) => (
          <div className="form-row" key={i}>
            <select value={line.productId}
              onChange={(e) => updateLine(i, "productId", e.target.value)} required>
              <option value="">Select product…</option>
              {products.map((p) => (
                <option key={p.id} value={p.id}>{p.name} (${Number(p.price).toFixed(2)}, {p.stockQuantity} in stock)</option>
              ))}
            </select>
            <input type="number" min="1" value={line.quantity}
              onChange={(e) => updateLine(i, "quantity", e.target.value)} required />
            {lines.length > 1 && (
              <button type="button" className="link-danger" onClick={() => removeLine(i)}>Remove</button>
            )}
          </div>
        ))}

        <div className="form-row">
          <button type="button" onClick={addLine}>+ Add line</button>
          <button type="submit">Place order</button>
        </div>
      </form>

      <table className="data-table">
        <thead>
          <tr><th>ID</th><th>Customer</th><th>Items</th><th>Total</th><th>Status</th><th>Date</th></tr>
        </thead>
        <tbody>
          {orders.map((o) => (
            <tr key={o.id}>
              <td>{o.id}</td>
              <td>{o.customerName}</td>
              <td>{o.items?.map((it) => `${it.productName} ×${it.quantity}`).join(", ")}</td>
              <td>${Number(o.totalAmount).toFixed(2)}</td>
              <td>
                <select value={o.status} onChange={(e) => changeStatus(o.id, e.target.value)}>
                  {STATUSES.map((s) => <option key={s} value={s}>{s}</option>)}
                </select>
              </td>
              <td>{new Date(o.orderDate).toLocaleString()}</td>
            </tr>
          ))}
          {orders.length === 0 && <tr><td colSpan="6" className="muted">No orders yet.</td></tr>}
        </tbody>
      </table>
    </div>
  );
}
