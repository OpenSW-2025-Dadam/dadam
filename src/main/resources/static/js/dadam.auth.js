/* =====================================================
   dadam.auth.js
   - 로그인 / 회원가입 모달 동작
   - 로그인/회원가입 성공 시:
       1) 토큰 저장
       2) currentUser 갱신
       3) 화면 블러 해제
       4) 로그인 모달 닫기
===================================================== */

// ----- 탭 버튼 / 패널 -----
const authTabs = document.querySelectorAll(".auth-tab");
const authPanels = document.querySelectorAll(".auth-panel");

// 로그인 / 회원가입 폼 + 인풋
const loginForm = document.getElementById("login-form");
const loginEmailInput = document.getElementById("login-email");
const loginPasswordInput = document.getElementById("login-password");

const signupForm = document.getElementById("signup-form");
const signupNameInput = document.getElementById("signup-name");
const signupEmailInput = document.getElementById("signup-email");
const signupPasswordInput = document.getElementById("signup-password");

const introLoginBtn = document.getElementById("intro-login-btn");
const introSignupBtn = document.getElementById("intro-signup-btn");
const loginCancelBtn = document.getElementById("login-cancel-btn");
const signupCancelBtn = document.getElementById("signup-cancel-btn");

// 로그인/회원가입 패널 이동 링크
const goSignupLink = document.getElementById("go-signup-link");
const goLoginLink = document.getElementById("go-login-link");

const loginFeedbackEl = document.getElementById("login-feedback");
const signupFeedbackEl = document.getElementById("signup-feedback");

// 현재 모드 상태 (login | signup)
let authMode = "login";

/* -----------------------------------------------------
   탭 전환 (로그인 <-> 회원가입)
----------------------------------------------------- */
function setAuthMode(mode) {
    authMode = mode;

    // 탭 버튼 상태
    authTabs.forEach((tab) => {
        const tabMode = tab.dataset.authTab; // "login" or "signup"
        if (tabMode === mode) {
            tab.classList.add("is-active");
        } else {
            tab.classList.remove("is-active");
        }
    });

    // 패널 상태
    authPanels.forEach((panel) => {
        const panelMode = panel.dataset.authPanel;
        if (panelMode === mode) {
            panel.classList.add("is-active");
        } else {
            panel.classList.remove("is-active");
        }
    });
}

function clearAuthFeedback(target) {
    const el = target === "signup" ? signupFeedbackEl : loginFeedbackEl;
    if (!el) return;
    el.textContent = "";
    el.hidden = true;
    el.classList.remove("is-error");
}

function showAuthFeedback(target, message, tone = "info") {
    const el = target === "signup" ? signupFeedbackEl : loginFeedbackEl;
    if (!el || !message) return;
    el.textContent = message;
    el.hidden = false;
    if (tone === "error") {
        el.classList.add("is-error");
    } else {
        el.classList.remove("is-error");
    }
}

function openSignupModal(prefillEmail, message) {
    setAuthMode("signup");
    clearAuthFeedback("login");
    if (typeof closeModal === "function") {
        closeModal("modal-login");
    }
    if (typeof openModal === "function") {
        openModal("modal-signup");
    }
    if (prefillEmail && signupEmailInput) {
        signupEmailInput.value = prefillEmail;
    }
    if (message) {
        showAuthFeedback("signup", message);
    } else {
        clearAuthFeedback("signup");
    }
    signupNameInput?.focus();
}

function openLoginModal(prefillEmail, message) {
    setAuthMode("login");
    clearAuthFeedback("signup");
    if (typeof closeModal === "function") {
        closeModal("modal-signup");
    }
    if (typeof openModal === "function") {
        openModal("modal-login");
    }
    if (prefillEmail && loginEmailInput) {
        loginEmailInput.value = prefillEmail;
    }
    if (message) {
        showAuthFeedback("login", message);
    } else {
        clearAuthFeedback("login");
    }
    loginEmailInput?.focus();
}

// 탭 버튼 클릭 이벤트
authTabs.forEach((tab) => {
    tab.addEventListener("click", () => {
        const mode = tab.dataset.authTab;
        if (!mode) return;
        setAuthMode(mode);
    });
});

// "회원가입" 링크 → 회원가입 패널로
goSignupLink?.addEventListener("click", () => {
    openSignupModal(loginEmailInput?.value);
});

// "로그인" 링크 → 로그인 패널로
goLoginLink?.addEventListener("click", () => {
    openLoginModal(signupEmailInput?.value);
});

introLoginBtn?.addEventListener("click", () => {
    if (typeof closeModal === "function") {
        closeModal("modal-intro");
    }
    openLoginModal();
});

introSignupBtn?.addEventListener("click", () => {
    if (typeof closeModal === "function") {
        closeModal("modal-intro");
    }
    openSignupModal();
});

loginCancelBtn?.addEventListener("click", (e) => {
    e.preventDefault();
    if (typeof showIntroModal === "function") {
        showIntroModal();
    }
});

signupCancelBtn?.addEventListener("click", (e) => {
    e.preventDefault();
    if (typeof showIntroModal === "function") {
        showIntroModal();
    }
});

/* -----------------------------------------------------
   공통: 로그인 성공/회원가입 성공 후 처리
----------------------------------------------------- */
function handleAuthSuccess(data, message) {
    try {
        clearAuthFeedback("login");
        clearAuthFeedback("signup");
        // data 예시: { token: "...", user: { ... } }

        // 1) 토큰 저장 (메모리 기반)
        if (data.token && typeof setAuthToken === "function") {
            setAuthToken(data.token);
        }

        // 2) currentUser 갱신 (헤더 아바타/이름 갱신까지 포함)
        if (data.user && typeof setCurrentUser === "function") {
            setCurrentUser(data.user);
        }

        // 3) 화면 블러 해제 (로그인 상태 UI)
        if (typeof setAuthUiState === "function") {
            setAuthUiState(true);
        }

        // 4) 로그인 모달 닫기
        if (typeof closeModal === "function") {
            closeModal("modal-login");
            closeModal("modal-signup");
        }

        // 4-1) ✅ 로그인/회원가입 성공 후 퀴즈 상태 리셋
        if (typeof window.resetQuizForCurrentUser === "function") {
            window.resetQuizForCurrentUser();
        }

        // 5) 알림 추가 (선택)
        if (typeof addNotification === "function") {
            addNotification({
                type: "info",
                message: message || "로그인에 성공했어요.",
            });
        }

        if (typeof fetchAndRenderFamilyMembers === "function") {
            fetchAndRenderFamilyMembers();
        }

        if (typeof updateAvatarVisuals === "function") {
            updateAvatarVisuals();
        }

        if (typeof fetchProfile === "function") {
            fetchProfile();
        }
    } catch (err) {
        console.error("[AUTH] handleAuthSuccess error:", err);
    }
}

/* -----------------------------------------------------
   로그인 폼 제출
   POST /api/v1/auth/login  (API_BASE 사용)
----------------------------------------------------- */
loginForm?.addEventListener("submit", async (e) => {
    e.preventDefault();

    const email = loginEmailInput?.value.trim();
    const password = loginPasswordInput?.value.trim();

    clearAuthFeedback("login");

    if (!email || !password) {
        showAuthFeedback("login", "이메일과 비밀번호를 모두 입력해 주세요.", "error");
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/auth/login`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ email, password }),
        });

        if (!res.ok) {
            const text = await res.text().catch(() => "");
            console.error("[AUTH] login failed:", res.status, text);

            if (res.status === 404) {
                const message = "아직 가입되지 않은 이메일입니다. 30초 만에 회원가입을 완료해 보세요.";
                openSignupModal(email, message);
                return;
            }

            // 💡 401 뿐만 아니라 400일 때도 자격 증명 오류 메시지를 출력하도록 수정
            if (res.status === 401 || res.status === 400) {
                showAuthFeedback("login", "이메일 또는 비밀번호가 올바르지 않습니다.", "error");
            } else {
                showAuthFeedback(
                    "login",
                    "로그인 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.",
                    "error"
                );
            }
            return;
        }

        const data = await res.json();
        handleAuthSuccess(data, "로그인에 성공했어요.");
    } catch (err) {
        console.error("[AUTH] login exception:", err);
        showAuthFeedback(
            "login",
            "로그인 중 문제가 발생했습니다. 잠시 후 다시 시도해 주세요.",
            "error"
        );
    }
});

/* -----------------------------------------------------
   회원가입 폼 제출
   POST /api/v1/auth/signup  (API_BASE 사용)
----------------------------------------------------- */
signupForm?.addEventListener("submit", async (e) => {
    e.preventDefault();

    const name = signupNameInput?.value.trim();
    const email = signupEmailInput?.value.trim();
    const password = signupPasswordInput?.value.trim();
    const familyCode = document.getElementById("signup-family-code")?.value.trim();

    clearAuthFeedback("signup");

    if (!name || !email || !password) {
        showAuthFeedback("signup", "이름, 이메일, 비밀번호를 모두 입력해 주세요.", "error");
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/auth/signup`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                name,
                email,
                password,
                familyCode,
            }),
        });

        if (!res.ok) {
            const text = await res.text().catch(() => "");
            console.error("[AUTH] signup failed:", res.status, text);

            const messageFromServer = (() => {
                try {
                    const parsed = JSON.parse(text);
                    return parsed.message || parsed.errorCode || "";
                } catch (err) {
                    return "";
                }
            })();

            if (res.status === 409 || messageFromServer.includes("이미")) {
                showAuthFeedback("signup", "이미 가입된 이메일입니다.", "error");
            } else if (res.status >= 500) {
                showAuthFeedback(
                    "signup",
                    "서버 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.",
                    "error"
                );
            } else {
                showAuthFeedback(
                    "signup",
                    messageFromServer ||
                        "회원가입 중 오류가 발생했습니다. 입력 정보를 다시 확인해 주세요.",
                    "error"
                );
            }
            return;
        }

        const data = await res.json();

        // 회원가입 후 바로 로그인 상태로 만들고 싶다면 그대로 재사용
        handleAuthSuccess(
            data,
            "회원가입이 완료되었습니다. 로그인 상태로 전환합니다."
        );
    } catch (err) {
        console.error("[AUTH] signup exception:", err);
        showAuthFeedback(
            "signup",
            "회원가입 중 문제가 발생했습니다. 잠시 후 다시 시도해 주세요.",
            "error"
        );
    }
});

/* -----------------------------------------------------
   초기 모드 설정 (기본: 로그인)
----------------------------------------------------- */
document.addEventListener("DOMContentLoaded", () => {
    setAuthMode("login");
});
