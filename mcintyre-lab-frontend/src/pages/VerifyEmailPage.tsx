import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthLayout from '../components/auth/AuthLayout';
import FormInput from '../components/auth/FormInput';
import { verifyEmail, resendCode } from '../api/authApi';

export default function VerifyEmailPage() {
    const navigate  = useNavigate();
    const savedEmail = localStorage.getItem('pendingVerifyEmail') ?? '';

    const [email, setEmail]   = useState(savedEmail);
    const [code, setCode]     = useState('');
    const [error, setError]   = useState('');
    const [success, setSuccess] = useState('');
    const [loading, setLoading] = useState(false);
    const [resending, setResending] = useState(false);

    const codeValid = /^\d{6}$/.test(code);

    async function handleVerify() {
        setError('');
        setSuccess('');
        if (!codeValid) { setError('Enter the 6-digit code from your email'); return; }
        setLoading(true);
        try {
            await verifyEmail({ email, verificationToken: parseInt(code, 10) });
            localStorage.removeItem('pendingVerifyEmail');
            navigate('/login');
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Verification failed');
        } finally {
            setLoading(false);
        }
    }

    async function handleResend() {
        setError('');
        setSuccess('');
        setResending(true);
        try {
            await resendCode(email);
            setSuccess('A new code has been sent to your email');
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Could not resend code');
        } finally {
            setResending(false);
        }
    }

    return (
        <AuthLayout>
            <h1 style={titleStyle}>Verify Email</h1>
            <p style={subtitleStyle}>Enter the 6-digit code sent to <strong>{email}</strong></p>
            <div style={{ marginTop: '2rem' }}>
                <FormInput
                    label="VERIFICATION CODE"
                    placeholder="123456"
                    value={code}
                    onChange={e => setCode(e.target.value.replace(/\D/g, '').slice(0, 6))}
                    isValid={codeValid}
                />
            </div>
            {error   && <p style={errorStyle}>{error}</p>}
            {success && <p style={successStyle}>{success}</p>}
            <div style={buttonRowStyle}>
                <button onClick={handleVerify} disabled={loading} style={primaryBtn}>
                    {loading ? 'Verifying…' : 'Verify'}
                </button>
                <button onClick={handleResend} disabled={resending} style={outlineBtn}>
                    {resending ? 'Sending…' : 'Resend Code'}
                </button>
            </div>
        </AuthLayout>
    );
}

const titleStyle: React.CSSProperties = { fontSize: '2rem', fontWeight: 700, color: '#1a1a2e', margin: 0 };
const subtitleStyle: React.CSSProperties = { fontSize: '0.9rem', color: '#666', marginTop: '0.5rem' };
const errorStyle: React.CSSProperties = { color: '#e53935', fontSize: '0.85rem', marginTop: '0.75rem' };
const successStyle: React.CSSProperties = { color: '#2e7d32', fontSize: '0.85rem', marginTop: '0.75rem' };
const buttonRowStyle: React.CSSProperties = { display: 'flex', gap: '1rem', marginTop: '2rem' };
const primaryBtn: React.CSSProperties = { padding: '0.7rem 2rem', borderRadius: '999px', border: 'none', background: '#22C5D8', color: '#fff', fontWeight: 600, fontSize: '1rem', cursor: 'pointer' };
const outlineBtn: React.CSSProperties = { padding: '0.7rem 2rem', borderRadius: '999px', border: '2px solid #22C5D8', background: 'transparent', color: '#1a1a2e', fontWeight: 600, fontSize: '1rem', cursor: 'pointer' };