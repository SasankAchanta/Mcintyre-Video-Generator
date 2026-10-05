import React from 'react';

interface FormInputProps {
    label: string;
    type?: string;
    placeholder?: string;
    value: string;
    onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
    isValid?: boolean;
    rightSlot?: React.ReactNode;
}

export default function FormInput({
                                      label, type = 'text', placeholder, value, onChange, isValid = false, rightSlot,
                                  }: FormInputProps) {
    return (
        <div style={{ marginBottom: '1.5rem' }}>
            <label style={labelStyle}>{label}</label>
            <div style={{ position: 'relative' }}>
                <input
                    type={type}
                    placeholder={placeholder}
                    value={value}
                    onChange={onChange}
                    style={inputStyle}
                />
                <span style={iconSlotStyle}>
          {rightSlot ?? (isValid && <Checkmark />)}
        </span>
            </div>
        </div>
    );
}

function Checkmark() {
    return (
        <svg width="18" height="18" viewBox="0 0 18 18" fill="none">
            <path d="M3 9l4 4 8-8" stroke="#22C5D8" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round" />
        </svg>
    );
}

const labelStyle: React.CSSProperties = {
    display: 'block',
    fontSize: '0.72rem',
    fontWeight: 700,
    letterSpacing: '0.1em',
    color: '#1a1a2e',
    marginBottom: '0.5rem',
    textTransform: 'uppercase',
};

const inputStyle: React.CSSProperties = {
    width: '100%',
    border: 'none',
    borderBottom: '1.5px solid #1a1a2e',
    outline: 'none',
    fontSize: '0.95rem',
    color: '#555',
    padding: '0.35rem 2rem 0.35rem 0',
    background: 'transparent',
    boxSizing: 'border-box',
};

const iconSlotStyle: React.CSSProperties = {
    position: 'absolute',
    right: 0,
    top: '50%',
    transform: 'translateY(-50%)',
    display: 'flex',
    alignItems: 'center',
};