import type {
    LoginRequest,
    LoginResponse,
    RegisterRequest,
    RegisterResponse,
    VerifyEmailRequest,
} from '../types/auth';

const BASE_URL = 'http://localhost:8081/mcintyre-lab/v1/auth';

async function request<T>(endpoint: string, body: unknown): Promise<T> {
    const response = await fetch(`${BASE_URL}${endpoint}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body),
    });

    if (!response.ok) {
        const error = await response.json().catch(() => ({ message: 'Request failed' }));
        throw new Error(error.message ?? 'Something went wrong');
    }

    return response.json() as Promise<T>;
}

export const login = (data: LoginRequest): Promise<LoginResponse> =>
    request<LoginResponse>('/login', data);

export const register = (data: RegisterRequest): Promise<RegisterResponse> =>
    request<RegisterResponse>('/register', data);

export const verifyEmail = (data: VerifyEmailRequest): Promise<string> =>
    request<string>('/verify-email', data);

export const resendCode = (email: string): Promise<string> =>
    fetch(`${BASE_URL}/resend-code?email=${encodeURIComponent(email)}`, {
        method: 'POST',
    }).then(res => res.text());