import { useState } from 'react';
import FormInput from './FormInput';

interface PasswordInputProps {
    value: string;
    onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
    label?: string;
}

export default function PasswordInput({ value, onChange, label = 'PASSWORD' }: PasswordInputProps) {
    const [visible, setVisible] = useState(false);

    const EyeIcon = () => (
        <button
            type="button"
            onClick={() => setVisible(v => !v)}
            style={{ background: 'none', border: 'none', cursor: 'pointer', padding: 0, display: 'flex' }}
        >
            {visible ? <EyeOpen /> : <EyeSlash />}
        </button>
    );

    return (
        <FormInput
            label={label}
            type={visible ? 'text' : 'password'}
            placeholder="••••••••••"
            value={value}
            onChange={onChange}
            rightSlot={<EyeIcon />}
        />
    );
}

function EyeOpen() {
    return (
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#aaa" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
            <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
            <circle cx="12" cy="12" r="3" />
        </svg>
    );
}

function EyeSlash() {
    return (
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#aaa" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
            <path d="M17.94 17.94A10.07 10.07 0 0112 20c-7 0-11-8-11-8a18.45 18.45 0 015.06-5.94" />
            <path d="M9.9 4.24A9.12 9.12 0 0112 4c7 0 11 8 11 8a18.5 18.5 0 01-2.16 3.19" />
            <line x1="1" y1="1" x2="23" y2="23" />
        </svg>
    );
}