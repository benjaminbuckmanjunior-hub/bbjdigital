
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { login } from '../services/api';

export default function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [userType, setUserType] = useState('member');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      const response = await login(email, password, userType);
      if (response.data?.success) {
        const data = response.data;
        localStorage.setItem('userId', data.userId);
        localStorage.setItem('userType', data.userType);
        localStorage.setItem('userName', data.name);
        if (data.userType === 'member') {
          navigate('/home');
        } else {
          navigate('/admin');
        }
      } else {
        setError(response.data?.message || 'Login failed');
      }
    } catch (err) {
      setError(err.message || 'Network error');
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-primary">
      <div className="bg-white text-gray-800 p-10 rounded-2xl shadow-2xl w-full max-w-md">
        <h2 className="text-3xl font-bold text-primary mb-6 text-center">
          {userType === 'member' ? 'Member Login' : 'Admin Login'}
        </h2>

        <div className="mb-4 flex justify-center space-x-4">
          <label>
            <input
              type="radio"
              name="userType"
              value="member"
              checked={userType === 'member'}
              onChange={() => setUserType('member')}
            />{' '}
            Member
          </label>
          <label>
            <input
              type="radio"
              name="userType"
              value="admin"
              checked={userType === 'admin'}
              onChange={() => setUserType('admin')}
            />{' '}
            Admin
          </label>
        </div>

        <form onSubmit={handleSubmit}>
          <input
            type="email"
            placeholder="Email Address"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="w-full mb-4 px-4 py-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-accent"
            required
          />

          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="w-full mb-6 px-4 py-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-accent"
            required
          />

          {error && <p className="text-red-500 mb-4">{error}</p>}

          <button
            type="submit"
            className="w-full bg-accent hover:bg-accent-hover text-primary font-semibold py-3 rounded-lg transition"
          >
            Login
          </button>
        </form>
      </div>
    </div>
  );
}
