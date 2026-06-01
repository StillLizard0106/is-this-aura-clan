/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        canteen: {
          50: '#f8fafc',
          100: '#f1f5f9',
          500: '#0ea5e9',
          600: '#0284c7',
          700: '#0369a1',
          900: '#082f49',
        },
      },
    },
  },
  plugins: [],
}
