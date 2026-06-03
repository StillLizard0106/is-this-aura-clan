# CanteenQ Frontend

React + Vite frontend for the CanteenQ digital pre-ordering system.

## Setup

### 1. Install Dependencies

```bash
npm install
```

### 2. Configure Environment

Copy `.env.example` to `.env` and update with your Firebase and backend API details:

```bash
cp .env.example .env
```

Edit `.env`:
```env
VITE_FIREBASE_API_KEY=your_firebase_api_key
VITE_FIREBASE_AUTH_DOMAIN=your_firebase_project.firebaseapp.com
VITE_FIREBASE_PROJECT_ID=your_project_id
VITE_FIREBASE_STORAGE_BUCKET=your_project.appspot.com
VITE_FIREBASE_MESSAGING_SENDER_ID=your_sender_id
VITE_FIREBASE_APP_ID=your_app_id

VITE_API_BASE_URL=http://localhost:8080/api
```

### 3. Run Development Server

```bash
npm run dev
```

The app will be available at `http://localhost:5173/`

## Project Structure

```
src/
├── api/                    # API endpoints and Firebase config
├── components/             # Reusable components
│   ├── auth/              # Auth form components
│   ├── common/            # Common utilities (PrivateRoute, LoadingSpinner)
│   ├── shared/            # Shared components (Navbar, StatusBadge)
│   ├── student/           # Student-specific components
│   └── staff/             # Staff-specific components
├── context/               # React Context (Auth, Catalog, etc.)
├── hooks/                 # Custom hooks (useAuth, useSSE, etc.)
├── pages/                 # Page components
│   ├── auth/              # Login, Register
│   ├── student/           # Student pages
│   └── staff/             # Staff pages
├── utils/                 # Utilities (formatters, constants)
├── App.jsx                # Main app with routing
└── main.jsx               # Entry point
```

## Features

### Student Features
- ✅ Firebase authentication with friendly error messages
- ✅ Browse stalls and menus
- ✅ Place orders with pickup slot selection
- ✅ Real-time order status tracking (SSE)
- ✅ View order history
- ✅ Cancel orders (PENDING only, with 15-minute window)

### Staff Features
- ✅ Staff dashboard with queue overview
- ✅ Real-time order queue view with status cards
- ✅ Update order status via dropdown (PENDING → PREPARING → READY → COMPLETED)
- ✅ Reject pending orders (mark as CANCELLED)
- ✅ View daily reporting summary
- ✅ Per-stall order and revenue breakdown
- ✅ Order count summary (Pending/Preparing/Ready/Completed)

## Build

```bash
npm run build
```

Output will be in `dist/` directory.

## Environment Notes

- **VITE_API_BASE_URL**: Backend API URL (default: http://localhost:8080/api)
- **Firebase Config**: Get from Firebase Console → Project Settings

## Dependencies

- **react**: UI framework
- **react-router-dom**: Client-side routing
- **firebase**: Authentication
- **axios**: HTTP client
- **tailwindcss**: Styling
- **react-toastify**: Notifications
- **date-fns**: Date formatting
- **lucide-react**: Icons

## Deployment

### Build for Production

```bash
npm run build
```

### Deploy to Vercel (Recommended)

```bash
npm run build
vercel
```

### Deploy to Netlify

```bash
npm run build
# Upload dist/ to Netlify
```

## Troubleshooting

### Firebase Authentication Issues
- Ensure Firebase credentials in `.env` are correct
- Check Firebase project has Email/Password auth enabled
- Verify backend is running at VITE_API_BASE_URL

### API Connection Errors
- Verify backend is running on port 8080
- Check VITE_API_BASE_URL is correct
- Ensure CORS is configured on backend

### SSE Notifications Not Working
- Verify Firefox/Chrome dev tools Network tab shows EventSource connection
- Check backend is running and accessible
- Ensure token is being sent correctly

## Support

For issues or questions, refer to the main project README in the root directory.
