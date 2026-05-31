// Mock authentication service for development/testing without Firebase
export class MockAuthService {
  constructor() {
    this.currentUser = null;
    this.listeners = [];
  }

  // Simulate user registration
  async register(email, password) {
    const user = {
      uid: 'mock-' + Math.random().toString(36).substr(2, 9),
      email,
      emailVerified: false,
      metadata: {
        creationTime: new Date().toISOString(),
      },
    };
    this.currentUser = user;
    this.notifyListeners();
    return user;
  }

  // Simulate user login
  async signInWithEmailAndPassword(email, password) {
    if (password.length < 6) {
      throw new Error('auth/weak-password');
    }
    const user = {
      uid: 'mock-' + Math.random().toString(36).substr(2, 9),
      email,
      emailVerified: false,
      metadata: {
        lastSignInTime: new Date().toISOString(),
      },
    };
    this.currentUser = user;
    this.notifyListeners();
    localStorage.setItem('mockAuthUser', JSON.stringify(user));
    return user;
  }

  // Simulate sign out
  async signOut() {
    this.currentUser = null;
    this.notifyListeners();
    localStorage.removeItem('mockAuthUser');
  }

  // Get ID token (mock JWT)
  async getIdToken() {
    if (!this.currentUser) return null;
    // Return a parseable mock token for the dev backend verifier.
    return `mock-jwt-token:${this.currentUser.uid}:${this.currentUser.email}`;
  }

  // Listen to auth state changes
  onAuthStateChanged(callback) {
    this.listeners.push(callback);
    // Immediately call with current state
    callback(this.currentUser);
    return () => {
      this.listeners = this.listeners.filter(l => l !== callback);
    };
  }

  // Notify all listeners
  notifyListeners() {
    this.listeners.forEach(callback => callback(this.currentUser));
  }

  // Restore session from localStorage
  restoreSession() {
    const savedUser = localStorage.getItem('mockAuthUser');
    if (savedUser) {
      this.currentUser = JSON.parse(savedUser);
      this.notifyListeners();
    }
  }
}

export default new MockAuthService();
