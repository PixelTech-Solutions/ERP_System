// Tiny API helper around fetch. All calls go to /api/... which nginx (prod)
// or the Vite proxy (dev) forwards to the Spring Boot backend.

const BASE = "/api";

async function request(path, options = {}) {
  const res = await fetch(`${BASE}${path}`, {
    headers: { "Content-Type": "application/json" },
    ...options,
  });
  if (!res.ok) {
    let message = `Request failed (${res.status})`;
    try {
      const body = await res.json();
      message = body.message || message;
    } catch {
      /* ignore non-JSON errors */
    }
    throw new Error(message);
  }
  if (res.status === 204) return null;
  return res.json();
}

export const api = {
  // dashboard
  stats: () => request("/dashboard/stats"),

  // customers
  listCustomers: () => request("/customers"),
  createCustomer: (data) => request("/customers", { method: "POST", body: JSON.stringify(data) }),
  deleteCustomer: (id) => request(`/customers/${id}`, { method: "DELETE" }),

  // products
  listProducts: () => request("/products"),
  createProduct: (data) => request("/products", { method: "POST", body: JSON.stringify(data) }),
  deleteProduct: (id) => request(`/products/${id}`, { method: "DELETE" }),

  // orders
  listOrders: () => request("/orders"),
  createOrder: (data) => request("/orders", { method: "POST", body: JSON.stringify(data) }),
  updateOrderStatus: (id, status) =>
    request(`/orders/${id}/status`, { method: "PUT", body: JSON.stringify({ status }) }),
};
