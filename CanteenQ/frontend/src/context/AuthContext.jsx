import React, { createContext, useState, useEffect } from 'react';
import {
  signInWithEmailAndPassword,
  createUserWithEmailAndPassword,
  signOut,
  onAuthStateChanged,
  updateProfile,
} from 'firebase/auth';
import { authService, isUsingMock } from '../api/firebaseConfig';
import { verifyToken } from '../api/endpoints';

export const AuthContext = createContext();

const getFriendlyAuthError = (error) => {
    // Axios network error (no response)
    if (error?.isAxiosError && !error.response) {
      return 'Network error: Unable to reach authentication server. Please try again later.';
    }
  if (!error) {
    return 'An unexpected authentication error occurred.';
  }

  const code = error.code || error?.response?.data?.code || '';
  const messageText = (error.message || error?.response?.data?.message || '').toString();
  const combined = `${code} ${messageText}`.toLowerCase();

  if (combined.includes('invalid_authorization') || combined.includes('invalid-authorization')) {
    return error.response?.data?.message || 'Your login is not authorized. Please use a valid student email domain.';
  }

  // Check common Firebase JS SDK codes and also textual forms like
  // "Firebase: Error (auth/invalid-credential)." which appear in message.
  if (
    combined.includes('auth/invalid-credential') ||
    combined.includes('invalid-credential') ||
    combined.includes('auth/wrong-password') ||
    combined.includes('wrong-password') ||
    combined.includes('auth/user-not-found') ||
    combined.includes('user-not-found')
  ) {
    return 'The Email or Password is incorrect! Try Again!';
  }

  if (combined.includes('auth/invalid-email') || combined.includes('invalid-email')) {
    return 'Please enter a valid email address.';
  }

  return error.response?.data?.message || error.message || String(error);
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [userRole, setUserRole] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    let unsubscribe;

    const setupAuthListener = async () => {
      if (isUsingMock) {
        // Mock auth
        unsubscribe = authService.onAuthStateChanged(async (mockUser) => {
          try {
            if (mockUser) {
              const token = await authService.getIdToken();
              localStorage.setItem('firebaseToken', token);

              try {
                const response = await verifyToken(token);
                setUserRole(response.data.role || 'STUDENT');
              } catch (verifyErr) {
                console.warn('Mock auth verification failed, falling back to STUDENT:', verifyErr);
                setUserRole('STUDENT');
              }

              setUser({
                uid: mockUser.uid,
                email: mockUser.email,
                displayName: mockUser.displayName || 'Test User',
              });
            } else {
              setUser(null);
              setUserRole(null);
              localStorage.removeItem('firebaseToken');
            }
          } catch (err) {
            console.error('Auth state change error:', err);
            setError(getFriendlyAuthError(err));
          } finally {
            setLoading(false);
          }
        });
      } else {
        // Real Firebase auth
        unsubscribe = onAuthStateChanged(authService, async (firebaseUser) => {
          try {
            if (firebaseUser) {
              const token = await firebaseUser.getIdToken();
              const response = await verifyToken(token);

              localStorage.setItem('firebaseToken', token);
              setUser({
                uid: firebaseUser.uid,
                email: firebaseUser.email,
                displayName: firebaseUser.displayName,
              });
              setUserRole(response.data.role || 'STUDENT');
            } else {
              setUser(null);
              setUserRole(null);
              localStorage.removeItem('firebaseToken');
            }
          } catch (err) {
            console.error('Auth state change error:', err);
            setError(getFriendlyAuthError(err));
            if (firebaseUser) {
              try {
                await signOut(authService);
              } catch (signOutError) {
                console.warn('Failed to sign out invalid user:', signOutError);
              }
              setUser(null);
              setUserRole(null);
              localStorage.removeItem('firebaseToken');
            }
          } finally {
            setLoading(false);
          }
        });
      }
    };

    setupAuthListener();
    return () => {
      if (unsubscribe) unsubscribe();
    };
  }, []);

  const login = async (email, password) => {
    setError(null);
    try {
      let user;
      if (isUsingMock) {
        user = await authService.signInWithEmailAndPassword(email, password);
      } else {
        const result = await signInWithEmailAndPassword(authService, email, password);
        user = result.user;
      }

      const token = isUsingMock
        ? await authService.getIdToken()
        : await user.getIdToken();

      try {
        const response = await verifyToken(token);
        const role = response.data.role || 'STUDENT';
        setUserRole(role);
        localStorage.setItem('firebaseToken', token);
        return { user, role };
      } catch (err) {
        if (!isUsingMock) {
          try {
            await signOut(authService);
          } catch (signOutError) {
            console.warn('Failed to sign out after auth verification failure:', signOutError);
          }
        }
        throw err;
      }
    } catch (err) {
      const message = getFriendlyAuthError(err);
      setError(message);
      throw new Error(message);
    }
  };

  const register = async (email, password, displayName) => {
    setError(null);
    try {
      let user;
      if (isUsingMock) {
        user = await authService.register(email, password);
      } else {
        const result = await createUserWithEmailAndPassword(authService, email, password);
        user = result.user;
        if (displayName) {
          await updateProfile(user, { displayName });
        }
      }

      const token = isUsingMock
        ? await authService.getIdToken()
        : await user.getIdToken();

      try {
        const response = await verifyToken(token);
        const role = response.data.role || 'STUDENT';
        setUserRole(role);
        localStorage.setItem('firebaseToken', token);
        return user;
      } catch (err) {
        if (!isUsingMock) {
          try {
            await signOut(authService);
          } catch (signOutError) {
            console.warn('Failed to sign out after auth verification failure:', signOutError);
          }
        }
        throw err;
      }
    } catch (err) {
      const message = getFriendlyAuthError(err);
      setError(message);
      throw new Error(message);
    }
  };

  const logout = async () => {
    setError(null);
    try {
      if (isUsingMock) {
        await authService.signOut();
      } else {
        await signOut(authService);
      }
      setUser(null);
      setUserRole(null);
      localStorage.removeItem('firebaseToken');
    } catch (err) {
      const message = getFriendlyAuthError(err);
      setError(message);
      throw err;
    }
  };

  const value = {
    user,
    userRole,
    loading,
    error,
    login,
    register,
    logout,
    isAuthenticated: !!user,
    isStaff: userRole === 'STAFF',
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};


