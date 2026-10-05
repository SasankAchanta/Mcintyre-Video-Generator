import type { ReactNode } from 'react';

const NATURE_IMAGE = 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600&q=80';

export default function AuthLayout({ children }: { children: ReactNode }) {
    return (
        <div style={backdropStyle}>
            <div style={cardStyle}>
                <div style={formPanelStyle}>{children}</div>
                <div style={imagePanelStyle}>
                    <img src={NATURE_IMAGE} alt="Scenic mountain lake" style={imageStyle} />
                </div>
            </div>
        </div>
    );
}

const backdropStyle: React.CSSProperties = {
    minHeight: '100vh',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    backgroundImage: `url(${NATURE_IMAGE})`,
    backgroundSize: 'cover',
    backgroundPosition: 'center',
    padding: '1rem',
};

const cardStyle: React.CSSProperties = {
    display: 'flex',
    width: '100%',
    maxWidth: '860px',
    minHeight: '480px',
    background: '#fff',
    borderRadius: '24px',
    overflow: 'hidden',
    boxShadow: '0 20px 60px rgba(0,0,0,0.18)',
};

const formPanelStyle: React.CSSProperties = {
    flex: 1,
    padding: '3rem 3rem 3rem 3.5rem',
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'center',
};

const imagePanelStyle: React.CSSProperties = {
    width: '42%',
    flexShrink: 0,
    padding: '1.25rem',
    display: 'flex',
    alignItems: 'stretch',
};

const imageStyle: React.CSSProperties = {
    width: '100%',
    height: '100%',
    objectFit: 'cover',
    borderRadius: '16px',
};