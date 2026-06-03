import { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import nuShield from '../../assets/nulogo.png';
import { ChevronDown, LogOut, UserCircle2 } from 'lucide-react';
import { useAuth } from '../../hooks/useAuth';

const Navbar = () => {
  const { pathname } = useLocation();
  const navigate = useNavigate();
  const { isAuthenticated, logout, userRole, user } = useAuth();
  const [menuOpen, setMenuOpen] = useState(false);
  const isAuthPage = pathname === '/login' || pathname === '/register';
  const dashboardPath = userRole === 'STUDENT'
    ? '/dashboard'
    : userRole === 'ADMIN'
      ? '/staff/admin'
      : '/staff/dashboard';
  const roleLabel = userRole || 'GUEST';
  const displayName = user?.displayName || user?.email || 'Account';
  const avatarText = displayName
    .split(' ')
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part[0])
    .join('')
    .toUpperCase()
    .slice(0, 2);

  const shellStyle = isAuthPage
    ? {
        backgroundColor: 'transparent',
        padding: '12px 24px',
        position: 'absolute',
      }
    : {
        backgroundColor: 'rgba(255, 255, 255, 0.88)',
        backdropFilter: 'blur(16px)',
        borderBottom: '1px solid rgba(15, 23, 42, 0.08)',
        padding: '14px 24px',
        position: 'sticky',
      };

  const textStyle = {
    color: isAuthPage ? '#ffffff' : '#0f172a',
    letterSpacing: '1px',
  };

  const handleLogout = async () => {
    try {
      await logout();
      navigate('/login');
    } catch (error) {
      console.error('Logout failed:', error);
    }
  };

  const handleAccountClick = () => {
    setMenuOpen((current) => !current);
  };

  return (
      <nav
        style={{
          ...shellStyle,
          top: 0,
          left: 0,
          right: 0,
          zIndex: 10,
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: '16px' }}>
          <Link
            to={isAuthPage ? '/login' : dashboardPath}
            style={{
              display: 'inline-flex',
              alignItems: 'center',
              gap: '10px',
              textDecoration: 'none',
              padding: '0',
            }}
          >
            <img
              src={nuShield}
              alt="National University Logo"
              style={{
                height: '42px',
                width: 'auto',
                filter: isAuthPage ? 'none' : 'saturate(0.9) contrast(1.05)',
              }}
            />
            <div style={{ lineHeight: '1.02' }}>
              <div style={{ fontSize: '10px', fontWeight: 700, ...textStyle }}>
                NATIONAL
              </div>
              <div style={{ fontSize: '13px', fontWeight: 800, ...textStyle }}>
                UNIVERSITY
              </div>
            </div>
          </Link>

          {!isAuthPage && isAuthenticated && (
            <div style={{ position: 'relative' }}>
              <button
                type="button"
                onClick={handleAccountClick}
                aria-expanded={menuOpen}
                style={{
                  display: 'inline-flex',
                  alignItems: 'center',
                  gap: '10px',
                  border: '1px solid rgba(15, 23, 42, 0.08)',
                  background: 'rgba(255, 255, 255, 0.92)',
                  color: '#0f172a',
                  borderRadius: '999px',
                  padding: '8px 10px 8px 8px',
                  fontSize: '13px',
                  fontWeight: 700,
                  cursor: 'pointer',
                  boxShadow: '0 8px 20px rgba(15, 23, 42, 0.06)',
                }}
              >
                <span
                  style={{
                    width: '34px',
                    height: '34px',
                    borderRadius: '999px',
                    display: 'inline-flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    background: 'linear-gradient(135deg, #0a2155, #1d4ed8)',
                    color: '#fff',
                    fontSize: '11px',
                    letterSpacing: '0.06em',
                    flexShrink: 0,
                  }}
                >
                  {avatarText || <UserCircle2 size={16} />}
                </span>
                <span style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-start', lineHeight: '1.05' }}>
                  <span style={{ fontSize: '13px', fontWeight: 800, color: '#0f172a' }}>{displayName}</span>
                  <span style={{ fontSize: '11px', fontWeight: 700, color: '#64748b', letterSpacing: '0.08em' }}>
                    {roleLabel}
                  </span>
                </span>
                <ChevronDown size={16} style={{ color: '#64748b', flexShrink: 0 }} />
              </button>

              {menuOpen && (
                <div
                  style={{
                    position: 'absolute',
                    right: 0,
                    top: 'calc(100% + 10px)',
                    minWidth: '220px',
                    borderRadius: '18px',
                    border: '1px solid rgba(15, 23, 42, 0.08)',
                    background: 'rgba(255,255,255,0.98)',
                    boxShadow: '0 18px 50px rgba(15, 23, 42, 0.16)',
                    overflow: 'hidden',
                    zIndex: 30,
                  }}
                >
                  <div style={{ padding: '14px 16px', borderBottom: '1px solid rgba(15, 23, 42, 0.06)' }}>
                    <p style={{ margin: 0, fontSize: '13px', fontWeight: 800, color: '#0f172a' }}>{displayName}</p>
                    <p style={{ margin: '4px 0 0', fontSize: '11px', fontWeight: 700, color: '#64748b', letterSpacing: '0.08em' }}>
                      {roleLabel}
                    </p>
                  </div>
                  {userRole === 'ADMIN' && (
                    <Link
                      to="/staff/admin"
                      style={{
                        width: '100%',
                        display: 'inline-flex',
                        alignItems: 'center',
                        gap: '10px',
                        padding: '14px 16px',
                        border: 'none',
                        background: 'transparent',
                        color: '#0f172a',
                        fontSize: '13px',
                        fontWeight: 700,
                        cursor: 'pointer',
                        textAlign: 'left',
                        textDecoration: 'none',
                      }}
                    >
                      Admin Stall Management
                    </Link>
                  )}
                  <button
                    type="button"
                    onClick={handleLogout}
                    style={{
                      width: '100%',
                      display: 'flex',
                      alignItems: 'center',
                      gap: '10px',
                      padding: '14px 16px',
                      border: 'none',
                      background: 'transparent',
                      color: '#0f172a',
                      fontSize: '13px',
                      fontWeight: 700,
                      cursor: 'pointer',
                      textAlign: 'left',
                    }}
                  >
                    <LogOut size={16} />
                    Logout
                  </button>
                </div>
              )}
            </div>
          )}
        </div>
      </nav>
  );
};

export default Navbar;
