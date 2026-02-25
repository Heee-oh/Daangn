const STORAGE_KEY = "daangn_front_state_v1";

const state = {
    baseUrl: "http://localhost:8080",
    token: "",
    memberId: "",
    expiresIn: "",
    listingIds: [],
    regions: []
};

const els = {
    installBtn: document.getElementById("installBtn"),
    logoutBtn: document.getElementById("logoutBtn"),
    baseUrlInput: document.getElementById("baseUrlInput"),
    saveConfigBtn: document.getElementById("saveConfigBtn"),
    signupForm: document.getElementById("signupForm"),
    loginForm: document.getElementById("loginForm"),
    signupPhone: document.getElementById("signupPhone"),
    signupNickname: document.getElementById("signupNickname"),
    loginPhone: document.getElementById("loginPhone"),
    sessionMemberId: document.getElementById("sessionMemberId"),
    sessionToken: document.getElementById("sessionToken"),
    loadRegionsBtn: document.getElementById("loadRegionsBtn"),
    useLocationBtn: document.getElementById("useLocationBtn"),
    verifyRegionBtn: document.getElementById("verifyRegionBtn"),
    regionSelect: document.getElementById("regionSelect"),
    regionLat: document.getElementById("regionLat"),
    regionLng: document.getElementById("regionLng"),
    regionsList: document.getElementById("regionsList"),
    createDraftBtn: document.getElementById("createDraftBtn"),
    refreshListingsBtn: document.getElementById("refreshListingsBtn"),
    listingIdInput: document.getElementById("listingIdInput"),
    updateListingForm: document.getElementById("updateListingForm"),
    titleInput: document.getElementById("titleInput"),
    descriptionInput: document.getElementById("descriptionInput"),
    categoryInput: document.getElementById("categoryInput"),
    priceInput: document.getElementById("priceInput"),
    isFreeInput: document.getElementById("isFreeInput"),
    hopeRegionInput: document.getElementById("hopeRegionInput"),
    hopeLatInput: document.getElementById("hopeLatInput"),
    hopeLngInput: document.getElementById("hopeLngInput"),
    imageUrlsInput: document.getElementById("imageUrlsInput"),
    publishBtn: document.getElementById("publishBtn"),
    hideBtn: document.getElementById("hideBtn"),
    unhideBtn: document.getElementById("unhideBtn"),
    interestListingIdInput: document.getElementById("interestListingIdInput"),
    addInterestBtn: document.getElementById("addInterestBtn"),
    loadInterestsBtn: document.getElementById("loadInterestsBtn"),
    interestsList: document.getElementById("interestsList"),
    buyerIdInput: document.getElementById("buyerIdInput"),
    reserveBtn: document.getElementById("reserveBtn"),
    cancelReserveBtn: document.getElementById("cancelReserveBtn"),
    soldOutBtn: document.getElementById("soldOutBtn"),
    deleteBtn: document.getElementById("deleteBtn"),
    listingFeed: document.getElementById("listingFeed"),
    logPanel: document.getElementById("logPanel")
};

let deferredPrompt = null;

boot();

function boot() {
    hydrateState();
    bindEvents();
    fillDefaultFormValues();
    renderSession();
    renderRegions([]);
    renderInterests([]);
    refreshListingFeed();
    registerPwaHandlers();

    if (state.token) {
        loadRegions();
        loadInterests();
    }
}

function bindEvents() {
    els.saveConfigBtn.addEventListener("click", () => {
        state.baseUrl = els.baseUrlInput.value.trim().replace(/\/+$/, "");
        persistState();
        log("success", `API 주소 저장: ${state.baseUrl}`);
    });

    els.logoutBtn.addEventListener("click", () => {
        state.token = "";
        state.memberId = "";
        state.expiresIn = "";
        persistState();
        renderSession();
        log("success", "로그아웃 완료");
    });

    els.signupForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        try {
            const phone = normalizePhone(els.signupPhone.value);
            const payload = {
                phone_number: phone,
                nickname: els.signupNickname.value.trim() || null
            };
            const data = await api("/api/auth/signup", { method: "POST", auth: false, body: payload });
            applyAuthResponse(data);
            await loadRegions();
            log("success", "회원가입 및 로그인 성공");
        } catch (err) {
            log("error", err.message);
        }
    });

    els.loginForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        try {
            const phone = normalizePhone(els.loginPhone.value);
            const data = await api("/api/auth/login", {
                method: "POST",
                auth: false,
                body: { phone_number: phone }
            });
            applyAuthResponse(data);
            await loadRegions();
            log("success", "로그인 성공");
        } catch (err) {
            log("error", err.message);
        }
    });

    els.loadRegionsBtn.addEventListener("click", () => loadRegions());

    els.useLocationBtn.addEventListener("click", () => {
        if (!navigator.geolocation) {
            log("error", "이 브라우저는 위치 정보를 지원하지 않습니다.");
            return;
        }

        navigator.geolocation.getCurrentPosition(
            (position) => {
                const lat = position.coords.latitude.toFixed(6);
                const lng = position.coords.longitude.toFixed(6);
                els.regionLat.value = lat;
                els.regionLng.value = lng;
                els.hopeLatInput.value = lat;
                els.hopeLngInput.value = lng;
                log("success", `현재 위치 채움: lat=${lat}, lng=${lng}`);
            },
            (error) => {
                log("error", `위치 가져오기 실패: ${error.message}`);
            }
        );
    });

    els.verifyRegionBtn.addEventListener("click", async () => {
        try {
            ensureAuth();
            const regionId = toRequiredNumber(els.regionSelect.value, "region_id");
            const lat = toRequiredNumber(els.regionLat.value, "lat");
            const lng = toRequiredNumber(els.regionLng.value, "lng");

            await api(`/api/members/me/regions/${regionId}/verify`, {
                method: "POST",
                body: { lat, lng }
            });

            await loadRegions();
            log("success", `동네 인증 성공(region_id=${regionId})`);
        } catch (err) {
            log("error", err.message);
        }
    });

    els.createDraftBtn.addEventListener("click", async () => {
        try {
            ensureAuth();
            const data = await api("/api/listings/drafts", { method: "POST" });
            const listingId = Number(data?.listing_id ?? data?.listingId);

            if (!listingId) {
                throw new Error("draft 생성 응답에 listing_id가 없습니다.");
            }

            setCurrentListingId(listingId);
            pushListingId(listingId);
            await refreshListingFeed();
            log("success", `Draft 생성 완료(listing_id=${listingId})`);
        } catch (err) {
            log("error", err.message);
        }
    });

    els.updateListingForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        try {
            ensureAuth();
            const listingId = currentListingId();
            const isFree = els.isFreeInput.checked;
            const imageUrls = splitImageUrls(els.imageUrlsInput.value);

            const body = {
                title: els.titleInput.value.trim(),
                description: els.descriptionInput.value.trim(),
                category_id: toRequiredNumber(els.categoryInput.value, "category_id"),
                price_amount: isFree ? 0 : toRequiredNumber(els.priceInput.value, "price_amount"),
                is_free: isFree,
                hope_location: {
                    region_id: toRequiredNumber(els.hopeRegionInput.value, "hope_location.region_id"),
                    lat: toRequiredNumber(els.hopeLatInput.value, "hope_location.lat"),
                    lng: toRequiredNumber(els.hopeLngInput.value, "hope_location.lng")
                },
                image_urls: imageUrls
            };

            await api(`/api/listings/${listingId}`, { method: "PUT", body });
            pushListingId(listingId);
            await refreshListingFeed();
            log("success", `게시글 저장 성공(listing_id=${listingId})`);
        } catch (err) {
            log("error", err.message);
        }
    });

    els.refreshListingsBtn.addEventListener("click", () => refreshListingFeed());

    els.publishBtn.addEventListener("click", () => runListingAction("게시", "publish"));
    els.hideBtn.addEventListener("click", () => runListingAction("숨김", "hide"));
    els.unhideBtn.addEventListener("click", () => runListingAction("숨김 해제", "unhide"));

    els.reserveBtn.addEventListener("click", async () => {
        try {
            ensureAuth();
            const listingId = currentListingId();
            const buyerId = toRequiredNumber(els.buyerIdInput.value, "buyer_id");

            await api(`/api/listings/${listingId}/reserve`, {
                method: "POST",
                body: { buyer_id: buyerId }
            });
            await refreshListingFeed();
            log("success", `예약중으로 변경 완료(listing_id=${listingId}, buyer_id=${buyerId})`);
        } catch (err) {
            log("error", err.message);
        }
    });

    els.cancelReserveBtn.addEventListener("click", () => runListingAction("예약 취소", "reserve/cancel"));

    els.soldOutBtn.addEventListener("click", async () => {
        try {
            ensureAuth();
            const listingId = currentListingId();
            const buyerId = toRequiredNumber(els.buyerIdInput.value, "buyer_id");

            await api(`/api/listings/${listingId}/sold-out`, {
                method: "POST",
                body: { buyer_id: buyerId }
            });
            await refreshListingFeed();
            log("success", `판매완료 처리 완료(listing_id=${listingId}, buyer_id=${buyerId})`);
        } catch (err) {
            log("error", err.message);
        }
    });

    els.deleteBtn.addEventListener("click", async () => {
        try {
            ensureAuth();
            const listingId = currentListingId();
            await api(`/api/listings/${listingId}`, { method: "DELETE" });
            removeListingId(listingId);
            await refreshListingFeed();
            log("success", `게시글 삭제 완료(listing_id=${listingId})`);
        } catch (err) {
            log("error", err.message);
        }
    });

    els.addInterestBtn.addEventListener("click", async () => {
        try {
            ensureAuth();
            const listingId = toRequiredNumber(els.interestListingIdInput.value, "listing_id");
            await api(`/api/members/me/interests/${listingId}`, { method: "PUT" });
            await loadInterests();
            log("success", `관심 등록 성공(listing_id=${listingId})`);
        } catch (err) {
            log("error", err.message);
        }
    });

    els.loadInterestsBtn.addEventListener("click", () => loadInterests());

    els.isFreeInput.addEventListener("change", () => {
        els.priceInput.disabled = els.isFreeInput.checked;
        if (els.isFreeInput.checked) {
            els.priceInput.value = "0";
        }
    });
}

async function runListingAction(actionLabel, subPath) {
    try {
        ensureAuth();
        const listingId = currentListingId();
        await api(`/api/listings/${listingId}/${subPath}`, { method: "POST" });
        await refreshListingFeed();
        log("success", `${actionLabel} 처리 완료(listing_id=${listingId})`);
    } catch (err) {
        log("error", err.message);
    }
}

async function loadRegions() {
    try {
        ensureAuth();
        const data = await api("/api/members/me/regions");
        const list = Array.isArray(data) ? data : [];
        state.regions = list;
        renderRegions(list);
        persistState();
        log("success", `동네 목록 조회 완료(${list.length}건)`);
        return list;
    } catch (err) {
        log("error", err.message);
        throw err;
    }
}

async function loadInterests() {
    try {
        ensureAuth();
        const data = await api("/api/members/me/interests?size=20");
        const items = Array.isArray(data) ? data : (data?.content ?? []);
        renderInterests(items);
        log("success", `관심목록 조회 완료(${items.length}건)`);
    } catch (err) {
        log("error", err.message);
    }
}

async function refreshListingFeed() {
    if (!state.token) {
        els.listingFeed.innerHTML = "<p class=\"hint\">로그인 후 게시글 목록을 조회할 수 있습니다.</p>";
        return;
    }

    const ids = [...new Set((state.listingIds || []).map(Number).filter(Boolean))];
    if (ids.length === 0) {
        els.listingFeed.innerHTML = "<p class=\"hint\">저장된 listing_id가 없습니다.</p>";
        return;
    }

    const rows = await Promise.all(ids.map(async (id) => {
        try {
            const detail = await api(`/api/listings/${id}`, { method: "GET" });
            return { id, ok: true, detail };
        } catch (err) {
            return { id, ok: false, error: err.message };
        }
    }));

    rows.sort((a, b) => b.id - a.id);
    renderListingFeed(rows);
}

function renderListingFeed(rows) {
    if (!rows.length) {
        els.listingFeed.innerHTML = "<p class=\"hint\">조회된 게시글이 없습니다.</p>";
        return;
    }

    els.listingFeed.innerHTML = rows.map((row) => {
        if (!row.ok) {
            return `
                <article class="feed-item">
                    <h3>#${row.id}</h3>
                    <p class="hint">조회 실패: ${escapeHtml(row.error)}</p>
                </article>
            `;
        }

        const d = row.detail;
        const status = d.status || "-";
        const priceText = d.is_free ? "무료나눔" : `${numberWithComma(d.price_amount ?? 0)}원`;
        const imageCount = Array.isArray(d.images) ? d.images.length : 0;

        return `
            <article class="feed-item">
                <h3>${escapeHtml(d.title || "(제목 없음)")}</h3>
                <div class="feed-meta">
                    <span class="badge">listing_id ${d.listing_id}</span>
                    <span class="badge">상태 ${escapeHtml(status)}</span>
                    <span>${escapeHtml(priceText)}</span>
                    <span>이미지 ${imageCount}장</span>
                    <span>seller ${d.seller_id ?? "-"}</span>
                </div>
            </article>
        `;
    }).join("");
}

function renderRegions(list) {
    const options = list.map((r) => {
        const regionId = r.region_id ?? r.regionId;
        const dong = r.dongnm ?? "-";
        return `<option value="${regionId}">${regionId} (${escapeHtml(dong)})</option>`;
    });

    els.regionSelect.innerHTML = options.length ? options.join("") : "<option value=\"\">동네 없음</option>";

    els.regionsList.innerHTML = list.map((r) => {
        const regionId = r.region_id ?? r.regionId;
        const verifiedAt = r.verified_at ?? r.verifiedAt ?? "-";
        const primary = r.primary ?? r.is_primary ?? r.isPrimary;
        const dong = r.dongnm ?? "-";
        return `<li>region_id=${regionId} | 동네=${escapeHtml(dong)} | 인증시각=${escapeHtml(String(verifiedAt))} | 대표=${primary ? "Y" : "N"}</li>`;
    }).join("");

    if (list.length > 0) {
        els.hopeRegionInput.value = String(list[0].region_id ?? list[0].regionId);
    } else {
        els.regionsList.innerHTML = "<li>조회된 동네가 없습니다.</li>";
    }
}

function renderInterests(items) {
    if (!items.length) {
        els.interestsList.innerHTML = "<li>관심목록이 비어 있습니다.</li>";
        return;
    }

    els.interestsList.innerHTML = items.map((i) => {
        const id = i.id ?? "-";
        const listingId = i.listing_id ?? i.listingId ?? "-";
        return `<li>interest_id=${id} | listing_id=${listingId}</li>`;
    }).join("");
}

function applyAuthResponse(data) {
    const memberId = data?.member_id ?? data?.memberId;
    const accessToken = data?.access_token ?? data?.accessToken;
    const expiresIn = data?.expires_in ?? data?.expiresIn;

    if (!accessToken) {
        throw new Error("토큰 응답 파싱 실패");
    }

    state.memberId = String(memberId ?? "");
    state.token = accessToken;
    state.expiresIn = String(expiresIn ?? "");
    persistState();
    renderSession();
}

function setCurrentListingId(listingId) {
    els.listingIdInput.value = String(listingId);
}

function currentListingId() {
    const value = els.listingIdInput.value.trim();
    return toRequiredNumber(value, "listing_id");
}

function pushListingId(listingId) {
    const numericId = Number(listingId);
    if (!numericId) return;
    state.listingIds = [numericId, ...state.listingIds.filter((id) => id !== numericId)].slice(0, 80);
    persistState();
}

function removeListingId(listingId) {
    const numericId = Number(listingId);
    state.listingIds = state.listingIds.filter((id) => id !== numericId);
    persistState();
}

function renderSession() {
    els.baseUrlInput.value = state.baseUrl;
    els.sessionMemberId.textContent = state.memberId || "-";
    els.sessionToken.textContent = state.token ? `${state.token.slice(0, 20)}...` : "-";
}

function persistState() {
    localStorage.setItem(STORAGE_KEY, JSON.stringify({
        baseUrl: state.baseUrl,
        token: state.token,
        memberId: state.memberId,
        expiresIn: state.expiresIn,
        listingIds: state.listingIds
    }));
}

function hydrateState() {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) return;

    try {
        const parsed = JSON.parse(raw);
        state.baseUrl = parsed.baseUrl || state.baseUrl;
        state.token = parsed.token || "";
        state.memberId = parsed.memberId || "";
        state.expiresIn = parsed.expiresIn || "";
        state.listingIds = Array.isArray(parsed.listingIds) ? parsed.listingIds : [];
    } catch (err) {
        log("error", `로컬 상태 복원 실패: ${err.message}`);
    }
}

function fillDefaultFormValues() {
    if (!els.titleInput.value) els.titleInput.value = "거의 새상품 아이폰";
    if (!els.descriptionInput.value) els.descriptionInput.value = "실사용 적고 상태 좋습니다.";
    if (!els.categoryInput.value) els.categoryInput.value = "1";
    if (!els.priceInput.value) els.priceInput.value = "700000";
    if (!els.hopeRegionInput.value) els.hopeRegionInput.value = "11000";
    if (!els.hopeLatInput.value) els.hopeLatInput.value = "37.5665";
    if (!els.hopeLngInput.value) els.hopeLngInput.value = "126.9780";
    if (!els.regionLat.value) els.regionLat.value = "37.5665";
    if (!els.regionLng.value) els.regionLng.value = "126.9780";
    if (!els.imageUrlsInput.value) els.imageUrlsInput.value = "https://img/1.png\nhttps://img/2.png";
    if (!els.buyerIdInput.value) els.buyerIdInput.value = "200";
}

function ensureAuth() {
    if (!state.token) {
        throw new Error("로그인 후 다시 시도해 주세요.");
    }
}

function splitImageUrls(raw) {
    const urls = raw
        .split(/\n|,/g)
        .map((s) => s.trim())
        .filter(Boolean);

    if (urls.length === 0) {
        throw new Error("이미지 URL을 최소 1개 입력해 주세요.");
    }
    return urls;
}

function toRequiredNumber(value, label) {
    const parsed = Number(value);
    if (!Number.isFinite(parsed)) {
        throw new Error(`${label} 값이 필요합니다.`);
    }
    return parsed;
}

function normalizePhone(value) {
    const digits = (value || "").replace(/\D/g, "");
    if (!digits) {
        throw new Error("전화번호를 입력해 주세요.");
    }
    return digits;
}

async function api(path, options = {}) {
    const method = options.method || "GET";
    const auth = options.auth ?? true;
    const body = options.body;
    const headers = {};

    if (body !== undefined) {
        headers["Content-Type"] = "application/json";
    }
    if (auth && state.token) {
        headers["Authorization"] = `Bearer ${state.token}`;
    }

    const response = await fetch(`${state.baseUrl}${path}`, {
        method,
        headers,
        body: body !== undefined ? JSON.stringify(body) : undefined
    });

    const rawText = await response.text();
    const data = tryParseJson(rawText);

    if (!response.ok) {
        const message =
            (typeof data === "object" && data && data.message) ? data.message :
                (typeof data === "string" && data.trim()) ? data :
                    `${response.status} ${response.statusText}`;
        throw new Error(`[${response.status}] ${message}`);
    }

    return data;
}

function tryParseJson(value) {
    if (!value) return null;
    try {
        return JSON.parse(value);
    } catch {
        return value;
    }
}

function numberWithComma(value) {
    return Number(value).toLocaleString("ko-KR");
}

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll("\"", "&quot;")
        .replaceAll("'", "&#039;");
}

function log(level, message) {
    const time = new Date().toLocaleTimeString("ko-KR", { hour12: false });
    const tag = level === "error" ? "오류" : "성공";
    const line = `[${time}] ${tag} | ${message}`;
    els.logPanel.textContent = `${line}\n${els.logPanel.textContent}`.trim();
}

function registerPwaHandlers() {
    window.addEventListener("beforeinstallprompt", (event) => {
        event.preventDefault();
        deferredPrompt = event;
        els.installBtn.classList.remove("hidden");
    });

    els.installBtn.addEventListener("click", async () => {
        if (!deferredPrompt) return;
        deferredPrompt.prompt();
        await deferredPrompt.userChoice;
        deferredPrompt = null;
        els.installBtn.classList.add("hidden");
    });

    if ("serviceWorker" in navigator) {
        window.addEventListener("load", () => {
            navigator.serviceWorker.register("./sw.js")
                .then(() => log("success", "서비스워커 등록 완료"))
                .catch((err) => log("error", `서비스워커 등록 실패: ${err.message}`));
        });
    }
}
