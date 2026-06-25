import { useEffect, useState } from "react";
import { api } from "../api.js";

const EMPTY = { name: "", email: "", phone: "", address: "" };

export default function Customers() {
  const [customers, setCustomers] = useState([]);
  const [form, setForm] = useState(EMPTY);
  const [error, setError] = useState(null);

  const load = () => api.listCustomers().then(setCustomers).catch((e) => setError(e.message));
  useEffect(() => { load(); }, []);

  const submit = async (e) => {
    e.preventDefault();
    setError(null);
    try {
      await api.createCustomer(form);
      setForm(EMPTY);
      load();
    } catch (e) {
      setError(e.message);
    }
  };

  const remove = async (id) => {
    if (!confirm("Delete this customer?")) return;
    try { await api.deleteCustomer(id); load(); } catch (e) { setError(e.message); }
  };

  return (
    <div>
      <h1>Customers</h1>
      {error && <div className="error">{error}</div>}

      <form className="form-row" onSubmit={submit}>
        <input placeholder="Name" value={form.name}
          onChange={(e) => setForm({ ...form, name: e.target.value })} required />
        <input placeholder="Email" type="email" value={form.email}
          onChange={(e) => setForm({ ...form, email: e.target.value })} required />
        <input placeholder="Phone" value={form.phone}
          onChange={(e) => setForm({ ...form, phone: e.target.value })} />
        <input placeholder="Address" value={form.address}
          onChange={(e) => setForm({ ...form, address: e.target.value })} />
        <button type="submit">Add</button>
      </form>

      <table className="data-table">
        <thead>
          <tr><th>ID</th><th>Name</th><th>Email</th><th>Phone</th><th>Address</th><th></th></tr>
        </thead>
        <tbody>
          {customers.map((c) => (
            <tr key={c.id}>
              <td>{c.id}</td><td>{c.name}</td><td>{c.email}</td>
              <td>{c.phone}</td><td>{c.address}</td>
              <td><button className="link-danger" onClick={() => remove(c.id)}>Delete</button></td>
            </tr>
          ))}
          {customers.length === 0 && <tr><td colSpan="6" className="muted">No customers yet.</td></tr>}
        </tbody>
      </table>
    </div>
  );
}
