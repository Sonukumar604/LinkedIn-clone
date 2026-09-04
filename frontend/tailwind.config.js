/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        ink: '#17313b',
        coral: '#e46f51',
        cream: '#f6f4ef',
        mist: '#e6eeec',
      },
      fontFamily: {
        display: ['Georgia', 'serif'],
        sans: ['Trebuchet MS', 'sans-serif'],
      },
      boxShadow: {
        paper: '0 12px 40px rgba(23, 49, 59, 0.08)',
      },
    },
  },
  plugins: [],
}