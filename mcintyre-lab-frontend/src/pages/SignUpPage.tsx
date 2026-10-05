import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthLayout from '../components/auth/AuthLayout';
import FormInput from '../components/auth/FormInput';
import PasswordInput from '../components/auth/PasswordInput';
import { register } from '../api/authApi';

const USERNAME_REGEX = /^[a-z]{3}\d{6}$/;
const EMAIL_REGEX    = /^([a-z]{3}[0-9]{6}|[a-z]+\.[a-z]+)@utdallas\.edu$/;
const PASSWORD_REGEX = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;

export default function SignUpPage() {
    const navigate = useNavigate();
    const [firstname, setFirstname] = useState('');
    const [lastname, setLastname]   = useState('');
    const [email, setEmail]         = useState('');
    const [username, setUsername]   = useState('');
    const [password, setPassword]   = useState('');
    const [agreed, setAgreed]       = useState(false);
    const [error, setError]         = useState('');
    const [loading, setLoading]     = useState(false);

    const emailValid    = EMAIL_REGEX.test(email.toLowerCase());
    const usernameValid = USERNAME_REGEX.test(username);
    const passwordValid = PASSWORD_REGEX.test(password);

    async function handleSignUp() {
        setError('');
        if (!firstname.trim())  { setError('First name is required'); return; }
        if (!lastname.trim())   { setError('Last name is required'); return; }
        if (!emailValid)        { setError('Email must be a valid @utdallas.edu address'); return; }
        if (!usernameValid)     { setError('Username must be 3 lowercase letters + 6 digits (e.g. abc123456)'); return; }
        if (!passwordValid)     { setError('Password needs 8+ chars with uppercase, lowercase, number and special character'); return; }
        if (!agreed)            { setError('You must agree to the Terms & Conditions'); return; }

        setLoading(true);
        try {
            await register({ firstname, lastname, email, username, password });
            // Store email so VerifyEmailPage can pre-fill it
            localStorage.setItem('pendingVerifyEmail', email.toLowerCase());
            navigate('/verify-email');
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Registration failed');
        } finally {
            setLoading(false);
        }
    }

    return (
        <AuthLayout>
            <h1 style={titleStyle}>Sign Up</h1>
            <div style={{ marginTop: '1.5rem' }}>
                <div style={nameRowStyle}>
                    <FormInput label="FIRST NAME" placeholder="Jane"
                               value={firstname} onChange={e => setFirstname(e.target.value)}
                               isValid={firstname.trim().length >= 1} />
                    <FormInput label="LAST NAME" placeholder="Doe"
                               value={lastname} onChange={e => setLastname(e.target.value)}
                               isValid={lastname.trim().length >= 1} />
                </div>
                <FormInput label="UTD EMAIL" type="email" placeholder="abc123456@utdallas.edu"
                           value={email} onChange={e => setEmail(e.target.value)} isValid={emailValid} />
                <FormInput label="USERNAME" placeholder="abc123456"
                           value={username} onChange={e => setUsername(e.target.value)} isValid={usernameValid} />
                <PasswordInput value={password} onChange={e => setPassword(e.target.value)} />
            </div>
            <label style={checkboxLabelStyle}>
                <input type="checkbox" checked={agreed}
                       onChange={e => setAgreed(e.target.checked)} style={checkboxStyle} />
                I agree to the Terms &amp; Conditions
            </label>
            {error && <p style={errorStyle}>{error}</p>}
            <div style={buttonRowStyle}>
                <button onClick={handleSignUp} disabled={loading} style={primaryBtn}>
                    {loading ? 'Creating account…' : 'Sign Up'}
                </button>
                <button onClick={() => navigate('/login')} style={outlineBtn}>Login</button>
            </div>
        </AuthLayout>
    );
}

const titleStyle: React.CSSProperties = { fontSize: '2rem', fontWeight: 700, color: '#1a1a2e', margin: 0 };
const nameRowStyle: React.CSSProperties = { display: 'flex', gap: '1rem' };
const checkboxLabelStyle: React.CSSProperties = { display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.9rem', color: '#444', cursor: 'pointer', marginTop: '0.5rem' };
const checkboxStyle: React.CSSProperties = { accentColor: '#22C5D8', width: '16px', height: '16px' };
const errorStyle: React.CSSProperties = { color: '#e53935', fontSize: '0.85rem', marginTop: '0.75rem' };
const buttonRowStyle: React.CSSProperties = { display: 'flex', gap: '1rem', marginTop: '1.5rem' };
const primaryBtn: React.CSSProperties = { padding: '0.7rem 2rem', borderRadius: '999px', border: 'none', background: '#22C5D8', color: '#fff', fontWeight: 600, fontSize: '1rem', cursor: 'pointer' };
const outlineBtn: React.CSSProperties = { padding: '0.7rem 2rem', borderRadius: '999px', border: '2px solid #22C5D8', background: 'transparent', color: '#1a1a2e', fontWeight: 600, fontSize: '1rem', cursor: 'pointer' };