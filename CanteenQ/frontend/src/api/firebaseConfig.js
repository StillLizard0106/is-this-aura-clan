import { initializeApp } from 'firebase/app';
import { getAuth, connectAuthEmulator } from 'firebase/auth';
import mockAuth from './mockAuth';

let auth = null;
let isUsingMockAuth = false;

// Check if we should use Firebase or mock auth
const hasValidFirebaseConfig = import.meta.env.VITE_FIREBASE_API_KEY &&
  !import.meta.env.VITE_FIREBASE_API_KEY?.includes('Demo');

if (!isUsingMockAuth && hasValidFirebaseConfig) {
  // Firebase configuration (from your Firebase Console)
  const firebaseConfig = {
    apiKey: import.meta.env.VITE_FIREBASE_API_KEY,
    authDomain: import.meta.env.VITE_FIREBASE_AUTH_DOMAIN,
    projectId: import.meta.env.VITE_FIREBASE_PROJECT_ID,
    storageBucket: import.meta.env.VITE_FIREBASE_STORAGE_BUCKET,
    messagingSenderId: import.meta.env.VITE_FIREBASE_MESSAGING_SENDER_ID,
    appId: import.meta.env.VITE_FIREBASE_APP_ID,
  };

  try {
    const app = initializeApp(firebaseConfig);
    auth = getAuth(app);

    if (import.meta.env.DEV) {
      connectAuthEmulator(auth, 'http://localhost:9099', { disableWarnings: true });
    }
  } catch (error) {
    console.warn('Firebase initialization failed, falling back to mock auth:', error.message);
    auth = mockAuth;
    isUsingMockAuth = true;
  }
} else {
  // Use mock auth in development
  console.warn('🔐 Using MOCK Authentication for Development');
  console.warn('⚠️ Real Firebase credentials not configured. Install valid credentials to use production auth.');
  auth = mockAuth;
  isUsingMockAuth = true;
  mockAuth.restoreSession();
}

export const authService = auth;
export const isUsingMock = isUsingMockAuth;
export default authService;



