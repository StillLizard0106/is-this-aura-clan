import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Mail, Lock, AlertCircle, Eye, EyeOff } from 'lucide-react';
import { useAuth } from '../../hooks/useAuth';
import { toast } from 'react-toastify';

const LoginPage = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const navigate = useNavigate();
  const { login, isAuthenticated, userRole } = useAuth();
  const [pendingRedirect, setPendingRedirect] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      if (!email || !password) {
        setError('Please fill in all fields');
        return;
      }

      const result = await login(email, password);
      toast.success('Login successful!');
      // Wait for auth state to propagate (onAuthStateChanged) before navigating to protected routes
      setPendingRedirect(true);
    } catch (err) {
      const errorMessage = err.message || 'Login failed. Please try again.';
      setError(errorMessage);
      toast.error(errorMessage);
      setPendingRedirect(false);
    } finally {
      setLoading(false);
    }
  };

  // Redirect when auth state becomes available after login
  React.useEffect(() => {
    if (pendingRedirect && isAuthenticated) {
      setPendingRedirect(false);
      if (userRole === 'ADMIN') {
        navigate('/staff/admin');
      } else if (userRole === 'STAFF') {
        navigate('/staff/dashboard');
      } else {
        navigate('/dashboard');
      }
    }
  }, [pendingRedirect, isAuthenticated, userRole, navigate]);

  return (
    <>
      <style>{`
        @import url('https://fonts.googleapis.com/css2?family=Manrope:wght@400;500;600;700;800&display=swap');
        @font-face {
          font-family: 'Magics Flavor';
          src: url('/fonts/Magics-Flavor.ttf') format('truetype');
          font-display: swap;
        }
        .login-wrap * { font-family: 'Manrope', sans-serif; }
        .login-title {
          font-family: 'Magics Flavor', cursive;
          letter-spacing: 0.02em;
          line-height: 0.94;
          font-weight: 400;
          color: #081535;
          text-rendering: geometricPrecision;
          font-synthesis: none;
        }
        .login-subtitle {
          color: #5a6a8a;
          font-size: 11px;
          margin: 0;
          letter-spacing: 0.08em;
          text-transform: none;
          font-weight: 600;
          line-height: 1.35;
        }
        .login-input {
          width: 100%;
          padding: 10px 14px;
          border-radius: 8px;
          border: 1.5px solid rgba(208, 216, 240, 0.85);
          background: rgba(255, 255, 255, 0.82);
          color: #0a1f4e;
          font-size: 14px;
          outline: none;
          transition: border 0.2s, background 0.2s;
          box-sizing: border-box;
        }
        .login-input::placeholder { color: #9aa5c0; }
        .login-input:focus { border-color: #f5a800; background: #fff; }
        .login-input:disabled { opacity: 0.5; }
        .login-label {
          color: #0a1f4e;
          font-size: 13px;
          font-weight: 600;
          margin-bottom: 6px;
          display: flex;
          align-items: center;
          gap: 6px;
        }
        .login-btn {
          width: 100%;
          padding: 12px;
          border-radius: 8px;
          border: none;
          background: linear-gradient(135deg, #f5a800, #ffc93c);
          color: #0a1f4e;
          font-weight: 700;
          font-size: 14px;
          letter-spacing: 0.5px;
          cursor: pointer;
          transition: filter 0.2s, opacity 0.2s;
          box-shadow: 0 12px 24px rgba(245, 168, 0, 0.25);
        }
        .login-btn:hover:not(:disabled) { filter: brightness(1.03); }
        .login-btn:disabled { opacity: 0.6; cursor: not-allowed; }
        .login-card {
          background: linear-gradient(160deg, rgba(255, 255, 255, 0.98) 0%, rgba(231, 239, 255, 0.95) 55%, rgba(200, 220, 255, 0.9) 100%);
          border-radius: 20px;
          padding: 40px 36px;
          width: 100%;
          max-width: 420px;
          box-shadow: 0 24px 60px rgba(2, 12, 27, 0.32), inset 0 1px 0 rgba(255,255,255,0.9);
          border: 1px solid rgba(255,255,255,0.65);
          backdrop-filter: blur(20px);
        }
        .loading-overlay {
          position: fixed;
          inset: 0;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          background: rgba(5, 21, 53, 0.6);
          z-index: 60;
        }
        .spinner {
          width: 64px;
          height: 64px;
          border-radius: 50%;
          border: 8px solid rgba(255,255,255,0.18);
          border-top-color: #ffffff;
          animation: spin 0.9s linear infinite;
          box-shadow: 0 8px 20px rgba(0,0,0,0.24);
        }
        .loading-text {
          color: #fff;
          margin-top: 12px;
          font-weight: 700;
          letter-spacing: 0.02em;
        }
        @keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }
      `}</style>

      <div
        className="login-wrap min-h-screen flex items-center justify-center px-4"
        style={{ background: 'linear-gradient(135deg, #051535 0%, #0a2155 50%, #0d2b6b 100%)' }}
      >
        {loading && (
          <div className="loading-overlay" role="status" aria-live="polite">
            <div className="spinner" />
            <div className="loading-text">Logging in…</div>
          </div>
        )}
        <div className="login-card">
          <div style={{ height: '3px', background: 'linear-gradient(90deg, #f5a800, #ffc93c)', borderRadius: '2px', marginBottom: '28px' }} />

          <div style={{ textAlign: 'center', marginBottom: '28px' }}>
            <h1 className="login-title" style={{ fontSize: 58, margin: '0 0 6px' }}>CanteenQ</h1>
            <p className="login-subtitle">
              &quot;Masakit na pila? MagCanteenQ&quot;
            </p>
          </div>

          {error && (
            <div
              style={{
                marginBottom: 16,
                padding: '10px 14px',
                background: 'rgba(220,38,38,0.08)',
                border: '1px solid rgba(220,38,38,0.3)',
                borderRadius: 8,
                display: 'flex',
                alignItems: 'flex-start',
                gap: 10,
              }}
            >
              <AlertCircle size={16} color="#dc2626" style={{ marginTop: 1, flexShrink: 0 }} />
              <p style={{ color: '#dc2626', fontSize: 13, margin: 0 }}>{error}</p>
            </div>
          )}

          <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
            <div>
              <label className="login-label">
                <Mail size={14} /> Email
              </label>
              <input
                type="email"
                className="login-input"
                placeholder="your@nu.edu.ph"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                disabled={loading}
              />
            </div>

            <div>
              <label className="login-label">
                <Lock size={14} /> Password
              </label>
              <div style={{ position: 'relative' }}>
                <input
                  type={showPassword ? 'text' : 'password'}
                  className="login-input"
                  placeholder="••••••••"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  disabled={loading}
                  style={{ paddingRight: 40 }}
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  style={{
                    position: 'absolute',
                    right: 12,
                    top: '50%',
                    transform: 'translateY(-50%)',
                    background: 'none',
                    border: 'none',
                    cursor: 'pointer',
                    color: '#9aa5c0',
                    display: 'flex',
                    padding: 0,
                  }}
                >
                  {showPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                </button>
              </div>
            </div>

            <button type="submit" className="login-btn" disabled={loading} style={{ marginTop: 4 }}>
              {loading ? 'Logging in...' : 'Login'}
            </button>
          </form>

          <div style={{ marginTop: 20, paddingTop: 16, borderTop: '1px solid rgba(10,31,78,0.1)', textAlign: 'center' }}>
            <p style={{ color: '#5a6a8a', fontSize: 13, margin: 0 }}>
              Don't have an account?{' '}
              <Link to="/register" style={{ color: '#f5a800', fontWeight: 600, textDecoration: 'none' }}>
                Register here
              </Link>
            </p>
          </div>
        </div>
      </div>
    </>
  );
};

export default LoginPage;
