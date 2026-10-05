import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthLayout from '../components/auth/AuthLayout';
import FormInput from '../components/auth/FormInput';
import PasswordInput from '../components/auth/PasswordInput';
import { login } from '../api/authApi';

// abc123456 format
const USERNAME_REGEX = /^[a-z]{3}\d{6}$/;

export default function LoginPage() {
    const navigate = useNavigate();
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError]       = useState('');
    const [loading, setLoading]   = useState(false);

    const usernameValid = USERNAME_REGEX.test(username);

    async function handleLogin() {
        setError('');
        if (!usernameValid) {
            setError('Username must be 3 lowercase letters followed by 6 digits (e.g. abc123456)');
            return;
        }
        if (!password) {
            setError('Password is required');
            return;
        }
        setLoading(true);
        try {
            const response = await login({ username, password });
            localStorage.setItem('jwtToken', response.jwtToken);
            navigate('/dashboard');
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Login failed');
        } finally {
            setLoading(false);
        }
    }

    return (
        <AuthLayout>
            <h1 style={titleStyle}>Login</h1>
            <div style={{ marginTop: '2rem' }}>
                <FormInput
                    label="USERNAME"
                    placeholder="abc123456"
                    value={username}
                    onChange={e => setUsername(e.target.value)}
                    isValid={usernameValid}
                />
                <PasswordInput value={password} onChange={e => setPassword(e.target.value)} />
            </div>
            <div style={rowStyle}>
                <a href="/forgot-password" style={linkStyle}>Forgot Password</a>
            </div>
            {error && <p style={errorStyle}>{error}</p>}
            <div style={buttonRowStyle}>
                <button onClick={handleLogin} disabled={loading} style={primaryBtn}>
                    {loading ? 'Logging in…' : 'Login'}
                </button>
                <button onClick={() => navigate('/signup')} style={outlineBtn}>Sign Up</button>
            </div>
        </AuthLayout>
    );
}

const titleStyle: React.CSSProperties = { fontSize: '2rem', fontWeight: 700, color: '#1a1a2e', margin: 0 };
const rowStyle: React.CSSProperties = { display: 'flex', justifyContent: 'flex-end', marginTop: '0.75rem' };
const linkStyle: React.CSSProperties = { fontSize: '0.85rem', color: '#444', textDecoration: 'underline' };
const errorStyle: React.CSSProperties = { color: '#e53935', fontSize: '0.85rem', marginTop: '0.75rem' };
const buttonRowStyle: React.CSSProperties = { display: 'flex', gap: '1rem', marginTop: '2rem' };
const primaryBtn: React.CSSProperties = { padding: '0.7rem 2rem', borderRadius: '999px', border: 'none', background: '#22C5D8', color: '#fff', fontWeight: 600, fontSize: '1rem', cursor: 'pointer' };
const outlineBtn: React.CSSProperties = { padding: '0.7rem 2rem', borderRadius: '999px', border: '2px solid #22C5D8', background: 'transparent', color: '#1a1a2e', fontWeight: 600, fontSize: '1rem', cursor: 'pointer' };