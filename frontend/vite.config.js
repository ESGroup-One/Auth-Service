import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
  plugins: [
    react(),
    tailwindcss(),
  ],
  server: {
    host: true, // Crucial for Docker/Cloud environments
    port: 5173,
    strictPort: true,
    watch: {
      usePolling: true, // Helps Vite detect file changes in workspaces
    }
  }
})