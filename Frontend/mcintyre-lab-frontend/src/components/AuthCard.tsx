import { useState } from "react";

const BASE = "http://localhost:8081/mcintyre-lab/v1/auth";

type View = "login" | "register" | "verify";
type AlertType = "error" | "success" | "info";

interface Alert {
    message: string;
    type: AlertType;
}

function AlertBox({ alert }: { alert: Alert | null }) {
    if (!alert) return null;
    const styles: Record<AlertType, string> = {
        error: "bg-red-50 border border-red-200 text-red-800",
        success: "bg-green-50 border border-green-200 text-green-800",
        info: "bg-blue-50 border border-blue-200 text-blue-800",
    };
    return (
        <div className={`rounded-lg px-3 py-2 text-sm mb-4 ${styles[alert.type]}`}>
            {alert.message}
        </div>
    );
}

function Brand() {
    return (
        <div className="flex items-center gap-3 mb-6">
            <div className="w-9 h-9 bg-blue-100 text-blue-700 rounded-lg flex items-center justify-center text-lg font-bold">
                M
            </div>
            <div>
                <div className="text-sm font-medium text-gray-900">McIntyre Lab</div>
                <div className="text-xs text-gray-500">Research Portal</div>
            </div>
        </div>
    );
}

function StepDots({ active }: { active: number }) {
    return (
        <div className="flex gap-1.5 mb-5">
            {[0, 1, 2].map((i) => (
                <div
                    key={i}
                    className={`h-1.5 rounded-full transition-all ${
                        i === active ? "w-5 bg-blue-600" : "w-1.5 bg-gray-200"
                    }`}
                />
            ))}
        </div>
    );
}

function InputField({
                        label,
                        type = "text",
                        value,
                        onChange,
                        placeholder,
                        autoComplete,
                    }: {
    label: string;
    type?: string;
    value: string;
    onChange: (v: string) => void;
    placeholder?: string;
    autoComplete?: string;
}) {
    return (
        <div className="mb-4">
            <label className="block text-xs text-gray-500 mb-1">{label}</label>
            <input
                type={type}
                value={value}
                onChange={(e) => onChange(e.target.value)}
                placeholder={placeholder}
                autoComplete={autoComplete}
                className="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg bg-white text-gray-900 outline-none focus:border-blue-400 focus:ring-2 focus:ring-blue-100 transition"
            />
        </div>
    );
}

function PrimaryButton({
                           onClick,
                           loading,
                           children,
                       }: {
    onClick: () => void;
    loading: boolean;
    children: React.ReactNode;
}) {
    return (
        <button
            onClick={onClick}
            disabled={loading}
            className="w-full py-2.5 bg-blue-600 hover:bg-blue-500 disabled:bg-blue-200 text-white text-sm font-medium rounded-lg transition mt-1 cursor-pointer disabled:cursor-not-allowed"
        >
            {loading ? "Please wait..." : children}
        </button>
    );
}

function LoginView({
                       onSwitch,
                       onVerify,
                   }: {
    onSwitch: () => void;
    onVerify: (email: string) => void;
}) {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [loading, setLoading] = useState(false);
    const [alert, setAlert] = useState<Alert | null>(null);
    const [token, setToken] = useState<string | null>(null);

    async function doLogin() {
        if (!username || !password) {
            setAlert({ message: "Please fill in all fields.", type: "error" });
            return;
        }
        setLoading(true);
        setAlert(null);
        setToken(null);
        try {
            const res = await fetch(`${BASE}/login`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ username, password }),
            });
            const data = await res.json();
            if (res.ok && data.token) {
                setAlert({ message: "Signed in successfully!", type: "success" });
                setToken(data.token);
            } else {
                const msg: string = data.message || data.error || JSON.stringify(data);
                if (msg === "Please verify your email address before logging in.") {
                    onVerify(username);
                } else {
                    setAlert({ message: msg || "Invalid credentials.", type: "error" });
                }
            }
        } catch {
            setAlert({
                message: "Could not reach the server. Is Spring Boot running on port 8081?",
                type: "error",
            });
        } finally {
            setLoading(false);
        }
    }

    return (
        <>
            <StepDots active={0} />
            <h2 className="text-lg font-medium text-gray-900 mb-1">Sign in</h2>
            <p className="text-sm text-gray-500 mb-5">
                Access your lab dashboard and research data.
            </p>
            <AlertBox alert={alert} />
            <InputField
                label="Username"
                value={username}
                onChange={setUsername}
                placeholder="your_username"
                autoComplete="username"
            />
            <InputField
                label="Password"
                type="password"
                value={password}
                onChange={setPassword}
                placeholder="••••••••"
                autoComplete="current-password"
            />
            <PrimaryButton onClick={doLogin} loading={loading}>
                Sign in
            </PrimaryButton>
            {token && (
                <div className="mt-4 bg-gray-50 border border-gray-200 rounded-lg p-3 text-xs text-gray-500">
                    <span className="font-medium text-gray-700">JWT token received:</span>
                    <br />
                    <code className="break-all text-gray-900 font-mono">{token}</code>
                </div>
            )}
            <hr className="my-5 border-gray-100" />
            <p className="text-center text-sm text-gray-500">
                No account?{" "}
                <button onClick={onSwitch} className="text-blue-600 hover:underline">
                    Register here
                </button>
            </p>
        </>
    );
}

function RegisterView({
                          onSwitch,
                          onSuccess,
                      }: {
    onSwitch: () => void;
    onSuccess: (email: string) => void;
}) {
    const [firstname, setFirstname] = useState("");
    const [lastname, setLastname] = useState("");
    const [email, setEmail] = useState("");
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [loading, setLoading] = useState(false);
    const [alert, setAlert] = useState<Alert | null>(null);

    async function doRegister() {
        if (!firstname || !lastname || !email || !username || !password) {
            setAlert({ message: "Please fill in all fields.", type: "error" });
            return;
        }
        setLoading(true);
        setAlert(null);
        try {
            const res = await fetch(`${BASE}/register`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ firstname, lastname, email, username, password }),
            });
            const data = await res.json();
            if (res.ok) {
                onSuccess(email);
            } else {
                setAlert({
                    message: data.message || data.error || "Registration failed.",
                    type: "error",
                });
            }
        } catch {
            setAlert({
                message: "Could not reach the server. Is Spring Boot running on port 8081?",
                type: "error",
            });
        } finally {
            setLoading(false);
        }
    }

    return (
        <>
            <StepDots active={1} />
            <h2 className="text-lg font-medium text-gray-900 mb-1">Create account</h2>
            <p className="text-sm text-gray-500 mb-5">
                Register as authorized lab personnel to request access.
            </p>
            <AlertBox alert={alert} />
            <div className="grid grid-cols-2 gap-3">
                <InputField
                    label="First name"
                    value={firstname}
                    onChange={setFirstname}
                    placeholder="Jane"
                />
                <InputField
                    label="Last name"
                    value={lastname}
                    onChange={setLastname}
                    placeholder="Smith"
                />
            </div>
            <InputField
                label="Email"
                type="email"
                value={email}
                onChange={setEmail}
                placeholder="jsmith@university.edu"
            />
            <InputField
                label="Username"
                value={username}
                onChange={setUsername}
                placeholder="jsmith"
                autoComplete="username"
            />
            <InputField
                label="Password"
                type="password"
                value={password}
                onChange={setPassword}
                placeholder="••••••••"
                autoComplete="new-password"
            />
            <PrimaryButton onClick={doRegister} loading={loading}>
                Register account
            </PrimaryButton>
            <hr className="my-5 border-gray-100" />
            <p className="text-center text-sm text-gray-500">
                Already have an account?{" "}
                <button onClick={onSwitch} className="text-blue-600 hover:underline">
                    Sign in
                </button>
            </p>
        </>
    );
}

function VerifyView({
                        email,
                        onSuccess,
                        onBack,
                    }: {
    email: string;
    onSuccess: () => void;
    onBack: () => void;
}) {
    const [digits, setDigits] = useState(["", "", "", "", "", ""]);
    const [loading, setLoading] = useState(false);
    const [resendLoading, setResendLoading] = useState(false);
    const [resendCooldown, setResendCooldown] = useState(0);
    const [alert, setAlert] = useState<Alert | null>(null);

    function handleDigit(i: number, val: string) {
        const clean = val.replace(/\D/g, "").slice(-1);
        const next = [...digits];
        next[i] = clean;
        setDigits(next);
        if (clean && i < 5) {
            document.getElementById(`cd-${i + 1}`)?.focus();
        }
    }

    function handleBack(e: React.KeyboardEvent, i: number) {
        if (e.key === "Backspace" && !digits[i] && i > 0) {
            document.getElementById(`cd-${i - 1}`)?.focus();
        }
    }

    async function doVerify() {
        const code = digits.join("");
        if (code.length < 6) {
            setAlert({ message: "Please enter the full 6-digit code.", type: "error" });
            return;
        }
        setLoading(true);
        setAlert(null);
        try {
            const res = await fetch(`${BASE}/verify-email`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email, verificationToken: parseInt(code, 10) }),
            });
            const text = await res.text();
            if (res.ok) {
                onSuccess();
            } else {
                setAlert({ message: text || "Invalid or expired code.", type: "error" });
            }
        } catch {
            setAlert({
                message: "Could not reach the server. Is Spring Boot running on port 8081?",
                type: "error",
            });
        } finally {
            setLoading(false);
        }
    }

    async function doResend() {
        setResendLoading(true);
        setAlert(null);
        try {
            const res = await fetch(
                `${BASE}/resend-code?email=${encodeURIComponent(email)}`,
                { method: "POST" }
            );
            const text = await res.text();
            setAlert({ message: text || "A new code has been sent.", type: "info" });
            let countdown = 30;
            setResendCooldown(countdown);
            const timer = setInterval(() => {
                countdown--;
                setResendCooldown(countdown);
                if (countdown <= 0) clearInterval(timer);
            }, 1000);
        } catch {
            setAlert({ message: "Could not reach the server.", type: "error" });
        } finally {
            setResendLoading(false);
        }
    }

    return (
        <>
            <StepDots active={2} />
            <h2 className="text-lg font-medium text-gray-900 mb-1">Verify your email</h2>
            <p className="text-sm text-gray-500 mb-5">
                Enter the 6-digit code sent to{" "}
                <span className="font-medium text-gray-700">{email || "your email"}</span>.
            </p>
            <AlertBox alert={alert} />
            <div className="flex gap-2 justify-center my-5">
                {digits.map((d, i) => (
                    <input
                        key={i}
                        id={`cd-${i}`}
                        type="text"
                        inputMode="numeric"
                        maxLength={1}
                        value={d}
                        onChange={(e) => handleDigit(i, e.target.value)}
                        onKeyDown={(e) => handleBack(e, i)}
                        className="w-11 h-13 text-center text-xl font-medium border border-gray-200 rounded-lg bg-white text-gray-900 outline-none focus:border-blue-400 focus:ring-2 focus:ring-blue-100 transition"
                        style={{ height: "52px" }}
                    />
                ))}
            </div>
            <PrimaryButton onClick={doVerify} loading={loading}>
                Verify account
            </PrimaryButton>
            <p className="text-center text-sm text-gray-500 mt-4">
                Didn't get a code?{" "}
                <button
                    onClick={doResend}
                    disabled={resendLoading || resendCooldown > 0}
                    className="text-blue-600 hover:underline disabled:text-gray-400 disabled:no-underline"
                >
                    {resendCooldown > 0 ? `Resend code (${resendCooldown}s)` : "Resend code"}
                </button>
            </p>
            <hr className="my-5 border-gray-100" />
            <p className="text-center text-sm text-gray-500">
                <button onClick={onBack} className="text-blue-600 hover:underline">
                    Back to sign in
                </button>
            </p>
        </>
    );
}

export default function AuthPortal() {
    const [view, setView] = useState<View>("login");
    const [verifyEmail, setVerifyEmail] = useState("");
    const [loginAlert, setLoginAlert] = useState<Alert | null>(null);

    function goToVerify(email: string) {
        setVerifyEmail(email);
        setView("verify");
    }

    function onVerifySuccess() {
        setLoginAlert({
            message: "Account verified! You can now sign in.",
            type: "success",
        });
        setView("login");
    }

    return (
        <div className="min-h-screen bg-slate-100 flex items-center justify-center p-6">
            <div className="bg-white border border-gray-200 rounded-xl w-full max-w-md p-8 shadow-sm">
                <Brand />
                {view === "login" && (
                    <LoginView
                        onSwitch={() => setView("register")}
                        onVerify={goToVerify}
                    />
                )}
                {view === "register" && (
                    <RegisterView
                        onSwitch={() => setView("login")}
                        onSuccess={goToVerify}
                    />
                )}
                {view === "verify" && (
                    <VerifyView
                        email={verifyEmail}
                        onSuccess={onVerifySuccess}
                        onBack={() => setView("login")}
                    />
                )}
                {view === "login" && loginAlert && <AlertBox alert={loginAlert} />}
            </div>
        </div>
    );
}