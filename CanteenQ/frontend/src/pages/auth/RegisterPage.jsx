import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Mail, Lock, User, AlertCircle } from 'lucide-react';
import { useAuth } from '../../hooks/useAuth';
import { toast } from 'react-toastify';

const RegisterPage = () => {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const { register } = useAuth();

  const validateEmail = (value) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value);
  const isAllowedStudentEmail = (value) => value.trim().toLowerCase().endsWith('@students.nu-laguna.edu.ph');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      if (!name || !email || !password || !confirmPassword) {
        setError('Please fill in all fields');
        return;
      }
      if (!validateEmail(email)) {
        setError('Please enter a valid email address');
        return;
      }
      if (!isAllowedStudentEmail(email)) {
        setError('Registration is restricted to @students.nu-laguna.edu.ph email addresses.');
        return;
      }
      if (password.length < 6) {
        setError('Password must be at least 6 characters');
        return;
      }
      if (password !== confirmPassword) {
        setError('Passwords do not match');
        return;
      }

      await register(email, password, name);
      toast.success('Registration successful!');
      // Navigate to root — App LandingPage will render the correct dashboard for the role
      navigate('/');
    } catch (err) {
      const errorMessage = err.message || 'Registration failed. Please try again.';
      setError(errorMessage);
      toast.error(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <style>{`
        @import url('https://fonts.googleapis.com/css2?family=Manrope:wght@400;500;600;700;800&display=swap');
        @font-face {
          font-family: 'Magics Flavor';
          src: url('/fonts/Magics-Flavor.ttf') format('truetype');
          font-display: swap;
        }
        .reg-wrap * { font-family: 'Manrope', sans-serif; }
        .reg-title {
          font-family: 'Magics Flavor', cursive;
          letter-spacing: 0.02em;
          line-height: 0.94;
          font-weight: 400;
          color: #081535;
          text-rendering: geometricPrecision;
          font-synthesis: none;
        }
        .reg-subtitle {
          color: #5a6a8a;
          font-size: 11px;
          margin: 0;
          letter-spacing: 0.08em;
          text-transform: none;
          font-weight: 600;
          line-height: 1.35;
        }
        .reg-input {
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
        .reg-input::placeholder { color: #9aa5c0; }
        .reg-input:focus { border-color: #f5a800; background: #fff; }
        .reg-input:disabled { opacity: 0.5; }
        .reg-label {
          color: #0a1f4e;
          font-size: 13px;
          font-weight: 600;
          margin-bottom: 6px;
          display: flex;
          align-items: center;
          gap: 6px;
        }
        .reg-btn {
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
        .reg-btn:hover:not(:disabled) { filter: brightness(1.03); }
        .reg-btn:disabled { opacity: 0.6; cursor: not-allowed; }
        .reg-card {
          background: linear-gradient(160deg, rgba(255, 255, 255, 0.98) 0%, rgba(231, 239, 255, 0.95) 55%, rgba(200, 220, 255, 0.9) 100%);
          border-radius: 20px;
          padding: 40px 36px;
          width: 100%;
          max-width: 420px;
          box-shadow: 0 24px 60px rgba(2, 12, 27, 0.32), inset 0 1px 0 rgba(255,255,255,0.9);
          border: 1px solid rgba(255,255,255,0.65);
          backdrop-filter: blur(20px);
        }
      `}</style>

      <div
        className="reg-wrap min-h-screen flex items-center justify-center px-4 py-12"
        style={{ background: 'linear-gradient(135deg, #051535 0%, #0a2155 50%, #0d2b6b 100%)' }}
      >
        <div className="reg-card">
          <div style={{ height: '3px', background: 'linear-gradient(90deg, #f5a800, #ffc93c)', borderRadius: '2px', marginBottom: '28px' }} />

          <div style={{ textAlign: 'center', marginBottom: '28px' }}>
            <h1 className="reg-title" style={{ fontSize: 58, margin: '0 0 6px' }}>CanteenQ</h1>
            <p className="reg-subtitle">
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

          <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
            <div>
              <label className="reg-label">
                <User size={14} /> Full Name
              </label>
              <input
                type="text"
                className="reg-input"
                placeholder="John Doe"
                value={name}
                onChange={(e) => setName(e.target.value)}
                disabled={loading}
              />
            </div>

            <div>
              <label className="reg-label">
                <Mail size={14} /> Email
              </label>
              <input
                type="email"
                className="reg-input"
                placeholder="your@nu.edu.ph"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                disabled={loading}
              />
            </div>

            <div>
              <label className="reg-label">
                <Lock size={14} /> Password
              </label>
              <input
                type="password"
                className="reg-input"
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                disabled={loading}
              />
              <p style={{ fontSize: 11, color: '#9aa5c0', margin: '4px 0 0' }}>Minimum 6 characters</p>
            </div>

            <div>
              <label className="reg-label">
                <Lock size={14} /> Confirm Password
              </label>
              <input
                type="password"
                className="reg-input"
                placeholder="••••••••"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                disabled={loading}
              />
            </div>

            <button type="submit" className="reg-btn" disabled={loading} style={{ marginTop: 4 }}>
              {loading ? 'Creating account...' : 'Register'}
            </button>
          </form>

          <div style={{ marginTop: 20, paddingTop: 16, borderTop: '1px solid rgba(10,31,78,0.1)', textAlign: 'center' }}>
            <p style={{ color: '#5a6a8a', fontSize: 13, margin: 0 }}>
              Already have an account?{' '}
              <Link to="/login" style={{ color: '#f5a800', fontWeight: 600, textDecoration: 'none' }}>
                Login here
              </Link>
            </p>
          </div>
        </div>
      </div>
    </>
  );
};

export default RegisterPage;
