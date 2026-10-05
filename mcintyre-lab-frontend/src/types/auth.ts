// ── Requests ──────────────────────────────────────────────
export interface LoginRequest {
    username: string;
    password: string;
}

export interface RegisterRequest {
    firstname: string;
    lastname: string;
    email: string;
    username: string;
    password: string;
}

export interface VerifyEmailRequest {
    email: string;
    verificationToken: number;
}

// ── Responses ─────────────────────────────────────────────
export interface LoginResponse {
    jwtToken: string;
}

export interface RegisterResponse {
    jwtToken: string;
}