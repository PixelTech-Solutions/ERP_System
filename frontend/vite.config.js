import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

// In dev, proxy /api calls to the Spring Boot backend on :8080 so there are
// no CORS issues. In production, nginx handles the same proxying.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      "/api": "http://localhost:8080",
    },
  },
});
