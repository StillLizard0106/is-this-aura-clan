import { createContext, useContext, useEffect, useMemo, useState } from 'react';
import { AuthResponse, UserProfile } from '../types';
import { getProfile, login as loginApi, register as registerApi } from '../api/auth';

interface AuthContextValue {
  token: string | null;
  user: UserProfile | null;
  login: (email: string, password: string) => Promise<void>;
  register: (email: string, username: string, password: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [token, setToken] = useState<string | null>(() => localStorage.getItem('gogrowglow_token'));
  const [user, setUser] = useState<UserProfile | null>(null);

  useEffect(() => {
    if (token) {
      localStorage.setItem('gogrowglow_token', token);
      getProfile().then(setUser).catch(() => setUser(null));
    } else {
      localStorage.removeItem('gogrowglow_token');
      setUser(null);
    }
  }, [token]);

  const login = async (email: string, password: string) => {
    const response = await loginApi(email, password);
    setToken(response.token);
    setUser({ email: response.email, username: response.username });
  };

  const register = async (email: string, username: string, password: string) => {
    const response = await registerApi(email, username, password);
    setToken(response.token);
    setUser({ email: response.email, username: response.username });
  };

  const logout = () => {
    setToken(null);
    setUser(null);
  };

  const value = useMemo(
    () => ({ token, user, login, register, logout }),
    [token, user]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used inside AuthProvider');
  }
  return context;
}
