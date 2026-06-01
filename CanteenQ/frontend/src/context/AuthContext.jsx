import React, { createContext, useState, useEffect } from 'react';
import {
  signInWithEmailAndPassword,
  createUserWithEmailAndPassword,
  signOut,
  onAuthStateChanged,
} from 'firebase/auth';
import { authService, isUsingMock } from '../api/firebaseConfig';
import { verifyToken } from '../api/endpoints';

export const AuthContext = createContext();

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
            setError(err.message);
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
              localStorage.setItem('firebaseToken', token);

              const response = await verifyToken(token);
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
            setError(err.message);
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
      localStorage.setItem('firebaseToken', token);

      let role = 'STUDENT';
      try {
        const response = await verifyToken(token);
        role = response.data.role || 'STUDENT';
        setUserRole(role);
      } catch (err) {
        setUserRole(role);
      }

      return { user, role };
    } catch (err) {
      setError(err.message);
      throw err;
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
          await user.updateProfile({ displayName });
        }
      }

      const token = isUsingMock
        ? await authService.getIdToken()
        : await user.getIdToken();
      localStorage.setItem('firebaseToken', token);

      try {
        const response = await verifyToken(token);
        setUserRole(response.data.role || 'STUDENT');
      } catch (err) {
        setUserRole('STUDENT');
      }

      return user;
    } catch (err) {
      setError(err.message);
      throw err;
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
      setError(err.message);
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


