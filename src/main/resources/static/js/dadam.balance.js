/* =====================================================
   dadam.balance.js
   - 오늘의 밸런스 게임
   - 백엔드: /api/v1/balance/today, /api/v1/balance/today/vote
===================================================== */

/* 오늘 날짜 키 (yyyy-mm-dd) – 퀴즈에서도 같이 사용 */
function getTodayKey() {
    const now = new Date();
    const y = now.getFullYear();
    const m = String(now.getMonth() + 1).padStart(2, "0");
    const d = String(now.getDate()).padStart(2, "0");
    return `${y}-${m}-${d}`;
}

/* -----------------------------------------------------
   ⚙ 아바타용 이름 라벨 헬퍼
   - "윤수진"  → "수진"
   - "엄마"    → "엄마"
   - "아빠"    → "아빠"
   - "홍 길동" → "길동"
----------------------------------------------------- */
function getBalanceAvatarLabel(rawName) {
    if (!rawName) return "가족";

    const name = String(rawName).trim();
    if (name.length === 0) return "가족";

    // 공백이 포함된 경우 → 마지막 토큰 사용 (예: "홍 길동" → "길동")
    const parts = name.split(/\s+/);
    const lastPart = parts[parts.length - 1];

    // 한글 이름인 경우
    // - 1~2글자: 그대로 사용 ("엄마", "아빠", "수진")
    // - 3글자: 뒤 2글자만 사용 ("윤수진" → "수진")
    if (/^[가-힣]+$/.test(lastPart)) {
        if (lastPart.length <= 2) {
            return lastPart;
        }
        if (lastPart.length === 3) {
            return lastPart.slice(1); // 2~3번째 글자
        }
        // 4글자 이상이면 그냥 마지막 파트 전체
        return lastPart;
    }

    // 그 외(알파벳 등)는 그대로 사용
    return lastPart;
}

/* -----------------------------------------------------
   ⚖ 밸런스 게임 로직
----------------------------------------------------- */

/* 예비용(백엔드 장애 시) 기본 밸런스 게임 풀 */
const BALANCE_POOL = [
    {
        id: "food-ramen-chicken",
        question: "평생 한 가지 음식만 먹어야 한다면?",
        A: "라면 🍜",
        B: "치킨 🍗",
    },
    {
        id: "trip-mountain-sea",
        question: "가족 여행지로 한 곳만 고른다면?",
        A: "바다 여행 🏖️",
        B: "산속 캠핑 ⛺",
    },
    {
        id: "time-morning-night",
        question: "가족이 같이 보내기 좋은 시간대는?",
        A: "아침 브런치 타임 ☕",
        B: "늦은 밤 수다 타임 🌙",
    },
    {
        id: "home-movie-outside",
        question: "주말에 뭐가 더 좋아?",
        A: "집에서 영화 몰아보기 🎬",
        B: "밖에 나가 액티비티 🏃",
    },
];

/* ✅ 백엔드 엔드포인트 */
const BALANCE_TODAY_API_URL = "/api/v1/balance/today";
const BALANCE_VOTE_API_URL = "/api/v1/balance/today/vote";

const balanceContainer = document.getElementById("balance-game");
const balanceQuestionEl = document.getElementById("balance-question");
const balanceOptions = document.querySelectorAll(".balance-option");
const regenBalanceBtn = document.getElementById("regen-balance");

/* 로그인 JWT 토큰 가져오기 (localStorage 기준) */
function getAuthToken() {
    try {
        return localStorage.getItem("dadam_auth_token");
    } catch (e) {
        return null;
    }
}

/* 백엔드 응답 DTO (BalanceGameTodayResponse)
   {
     "id": 1,
     "question": "string",
     "optionA": "string",
     "optionB": "string",
     "category": "string",
     "votesA": [ { "userId": 1, "userName": "엄마" }, ... ],
     "votesB": [ { "userId": 2, "userName": "아빠" }, ... ]
   }
*/
function normalizeBalanceSummary(raw) {
    if (!raw) return null;

    const votesA = Array.isArray(raw.votesA) ? raw.votesA : [];
    const votesB = Array.isArray(raw.votesB) ? raw.votesB : [];

    console.log("[BALANCE] normalize", {
        id: raw.id,
        question: raw.question,
        optionA: raw.optionA,
        optionB: raw.optionB,
        category: raw.category,
        votesA,
        votesB,
    });

    return {
        id: raw.id,
        question: raw.question || "가족 밸런스 게임",
        A: raw.optionA || "A 선택지",
        B: raw.optionB || "B 선택지",
        category: raw.category || "ETC",
        votesA,
        votesB,
    };
}

/* 퍼센트 & 아바타 렌더링 */
function renderBalanceFromSummary(summary) {
    const votesA = summary.votesA || [];
    const votesB = summary.votesB || [];
    const total = votesA.length + votesB.length;

    const percentA =
        total === 0 ? 0 : Math.round((votesA.length / total) * 100);
    const percentB = total === 0 ? 0 : 100 - percentA;

    const barA = document.querySelector('[data-bar="A"]');
    const barB = document.querySelector('[data-bar="B"]');
    const labelA = document.querySelector('[data-percent="A"]');
    const labelB = document.querySelector('[data-percent="B"]');

    if (barA) barA.style.width = percentA + "%";
    if (barB) barB.style.width = percentB + "%";
    if (labelA) labelA.textContent = percentA + "%";
    if (labelB) labelB.textContent = percentB + "%";

    const avatarA = document.querySelector('[data-avatars="A"]');
    const avatarB = document.querySelector('[data-avatars="B"]');

    if (avatarA) {
        avatarA.innerHTML = votesA
            .map((voter) => {
                const rawName =
                    typeof voter === "string"
                        ? voter
                        : (voter.userName || "가족");

                const label = getBalanceAvatarLabel(rawName); // "윤수진" → "수진"

                return `
          <span class="avatar avatar-sm">
            <span class="avatar-initial">${label}</span>
          </span>
        `;
            })
            .join("");
    }

    if (avatarB) {
        avatarB.innerHTML = votesB
            .map((voter) => {
                const rawName =
                    typeof voter === "string"
                        ? voter
                        : (voter.userName || "가족");

                const label = getBalanceAvatarLabel(rawName);

                return `
          <span class="avatar avatar-sm">
            <span class="avatar-initial">${label}</span>
          </span>
        `;
            })
            .join("");
    }
}

/* 밸런스 게임 화면에 설정 */
function setBalanceGameFromSummary(summary) {
    if (!balanceContainer || !summary) return;

    console.log("[BALANCE] set game", summary);

    balanceContainer.dataset.gameId = summary.id;

    if (balanceQuestionEl) balanceQuestionEl.textContent = summary.question;

    balanceOptions.forEach((btn) => {
        const choice = btn.dataset.choice;
        const textEl = btn.querySelector(".balance-text");
        if (!textEl) return;
        if (choice === "A") textEl.textContent = summary.A;
        if (choice === "B") textEl.textContent = summary.B;
    });

    renderBalanceFromSummary(summary);
}

/* 서버에서 오늘의 밸런스 게임 + 투표 현황 가져오기 */
async function fetchBalanceGameFromServer() {
    try {
        const res = await fetch(BALANCE_TODAY_API_URL, {
            method: "GET",
            headers: { "Content-Type": "application/json" },
        });
        if (!res.ok) throw new Error("Failed to fetch balance game");

        const raw = await res.json();
        console.log("[BALANCE] /today response:", raw);
        const summary = normalizeBalanceSummary(raw);
        if (!summary) throw new Error("Invalid balance summary data");

        setBalanceGameFromSummary(summary);

        // addNotification({
        //     type: "info",
        //     message: "오늘의 밸런스 게임이 준비되었어요.",
        // });
    } catch (err) {
        console.error("[BALANCE] error:", err);

        // 서버 장애 시 fallback 문제 사용 (이 경우는 공유 X, 내 브라우저 한정)
        const fallback =
            BALANCE_POOL[Math.floor(Math.random() * BALANCE_POOL.length)];

        const summary = {
            id: fallback.id,
            question: fallback.question,
            A: fallback.A,
            B: fallback.B,
            category: "ETC",
            votesA: [],
            votesB: [],
        };
        setBalanceGameFromSummary(summary);

        addNotification({
            type: "error",
            message:
                "서버에서 밸런스 게임을 불러오지 못해, 기본 문제를 보여드릴게요.",
        });
    }
}

/* 밸런스 게임 초기화 */
function initBalanceGame() {
    if (!balanceContainer) return;
    fetchBalanceGameFromServer();
}

/* 선택 처리: 서버에 투표 요청 후, 최신 집계 반영 */
async function handleBalanceChoice(choice) {
    if (!balanceContainer) return;

    const currentGameId = balanceContainer.dataset.gameId;
    if (!currentGameId) return;

    // ✅ 로그인 토큰 확인
    const token = getAuthToken();
    if (!token) {
        addNotification({
            type: "error",
            message: "로그인 후에만 밸런스 게임에 참여할 수 있어요.",
        });
        if (typeof openModal === "function") {
            openModal("modal-login"); // 로그인 모달이 있다면 열기
        }
        return;
    }

    try {
        const res = await fetch(BALANCE_VOTE_API_URL, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                // ✅ JWT 토큰을 Authorization 헤더로 전송
                "Authorization": `Bearer ${token}`,
            },
            body: JSON.stringify({ choice }),
        });
        if (!res.ok) throw new Error("Failed to vote balance game");

        const raw = await res.json();
        console.log("[BALANCE] vote response:", raw);
        let summary = normalizeBalanceSummary(raw);
        if (!summary) throw new Error("Invalid vote response");

        // 서버에서 아직 투표 리스트가 비어 있으면 최소한 내 '나' 아바타는 넣어줌
        if (
            (!summary.votesA || summary.votesA.length === 0) &&
            (!summary.votesB || summary.votesB.length === 0)
        ) {
            const name =
                (typeof currentUser !== "undefined" && currentUser.name) ||
                "나";
            const meVoter = {
                userId: null,
                userName: name,
            };
            if (choice === "A") {
                summary.votesA = [meVoter];
                summary.votesB = [];
            } else if (choice === "B") {
                summary.votesA = [];
                summary.votesB = [meVoter];
            }
        }

        setBalanceGameFromSummary(summary);

        const text = choice === "A" ? summary.A : summary.B;
        const voterName =
            (typeof currentUser !== "undefined" &&
                currentUser &&
                currentUser.name) ||
            "나";

        addNotification({
            type: "info",
            message: `${voterName}님이 밸런스 게임에서 "${text}"를 선택했어요.`,
        });
    } catch (err) {
        console.error("[BALANCE] vote error:", err);
        addNotification({
            type: "error",
            message:
                "밸런스 게임 선택에 실패했어요. 잠시 후 다시 시도해 주세요.",
        });
    }
}

/* 옵션 클릭 이벤트 (위임) */
document.addEventListener("click", (e) => {
    const btn = e.target.closest(".balance-option");
    if (!btn || !balanceContainer) return;

    const choice = btn.dataset.choice;
    if (!choice) return;

    handleBalanceChoice(choice);
});

/* 다른 주제 버튼 */
regenBalanceBtn?.addEventListener("click", () => {
    addNotification({
        type: "info",
        message:
            "오늘의 밸런스 게임은 하루에 한 번만 제공돼요. 내일 새로운 문제가 열려요.",
    });
});

/* 초기 진입 시 밸런스 게임 실행 */
document.addEventListener("DOMContentLoaded", () => {
    initBalanceGame();
});
