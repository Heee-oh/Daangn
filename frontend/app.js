const STORAGE_KEY = "daangn_controller_console_v2";
const MAX_HISTORY = 40;

const state = {
    baseUrl: "http://localhost:8080",
    token: "",
    memberId: "",
    history: []
};

const el = {
    baseUrl: document.getElementById("baseUrl"),
    tokenInput: document.getElementById("tokenInput"),
    currentMemberId: document.getElementById("currentMemberId"),
    saveConfigBtn: document.getElementById("saveConfigBtn"),
    applyTokenBtn: document.getElementById("applyTokenBtn"),
    clearTokenBtn: document.getElementById("clearTokenBtn"),

    authPhone: document.getElementById("authPhone"),
    authNickname: document.getElementById("authNickname"),
    signupBtn: document.getElementById("signupBtn"),
    loginBtn: document.getElementById("loginBtn"),

    regionIdInput: document.getElementById("regionIdInput"),
    listingIdInput: document.getElementById("listingIdInput"),
    interestListingIdInput: document.getElementById("interestListingIdInput"),
    buyerIdInput: document.getElementById("buyerIdInput"),
    latInput: document.getElementById("latInput"),
    lngInput: document.getElementById("lngInput"),

    verifyRegionBtn: document.getElementById("verifyRegionBtn"),

    memberMeBtn: document.getElementById("memberMeBtn"),
    memberRegionsBtn: document.getElementById("memberRegionsBtn"),
    updateMemberBtn: document.getElementById("updateMemberBtn"),
    updateProfileBtn: document.getElementById("updateProfileBtn"),
    updateNicknameBtn: document.getElementById("updateNicknameBtn"),
    addInterestBtn: document.getElementById("addInterestBtn"),
    deleteInterestBtn: document.getElementById("deleteInterestBtn"),
    listInterestsBtn: document.getElementById("listInterestsBtn"),
    withdrawBtn: document.getElementById("withdrawBtn"),
    updateMemberBody: document.getElementById("updateMemberBody"),
    profileImageInput: document.getElementById("profileImageInput"),
    nicknameInput: document.getElementById("nicknameInput"),
    lastInterestIdInput: document.getElementById("lastInterestIdInput"),
    interestSizeInput: document.getElementById("interestSizeInput"),

    getListingsBtn: document.getElementById("getListingsBtn"),
    createDraftBtn: document.getElementById("createDraftBtn"),
    getListingBtn: document.getElementById("getListingBtn"),
    updateListingBtn: document.getElementById("updateListingBtn"),
    publishBtn: document.getElementById("publishBtn"),
    hideBtn: document.getElementById("hideBtn"),
    unhideBtn: document.getElementById("unhideBtn"),
    reserveBtn: document.getElementById("reserveBtn"),
    cancelReserveBtn: document.getElementById("cancelReserveBtn"),
    soldOutBtn: document.getElementById("soldOutBtn"),
    deleteListingBtn: document.getElementById("deleteListingBtn"),
    lastListingIdInput: document.getElementById("lastListingIdInput"),
    listingUpdateBody: document.getElementById("listingUpdateBody"),

    requestLine: document.getElementById("requestLine"),
    statusBadge: document.getElementById("statusBadge"),
    durationText: document.getElementById("durationText"),
    responseBody: document.getElementById("responseBody"),
    historyList: document.getElementById("historyList")
};

boot();

async function boot() {
    await cleanupLegacyServiceWorker();
    hydrateState();
    bindEvents();
    renderSession();
    renderHistory();
}

function bindEvents() {
    el.saveConfigBtn.addEventListener("click", () => {
        state.baseUrl = normalizeBaseUrl(el.baseUrl.value);
        persistState();
        showLocalResult("Config", `Base URL saved: ${state.baseUrl}`);
    });

    el.applyTokenBtn.addEventListener("click", () => {
        state.token = el.tokenInput.value.trim();
        persistState();
        renderSession();
        showLocalResult("Session", "Access token updated");
    });

    el.clearTokenBtn.addEventListener("click", () => {
        state.token = "";
        state.memberId = "";
        persistState();
        renderSession();
        showLocalResult("Session", "Token cleared");
    });

    el.signupBtn.addEventListener("click", () => runAction("Auth Signup", async () => {
        const phoneNumber = requireText(el.authPhone.value, "phone_number");
        const nickname = el.authNickname.value.trim();
        const payload = await callApi("Auth Signup", {
            method: "POST",
            path: "/api/auth/signup",
            auth: false,
            body: {
                phone_number: phoneNumber,
                nickname: nickname || null
            }
        });
        applyAuthPayload(payload);
    }));

    el.loginBtn.addEventListener("click", () => runAction("Auth Login", async () => {
        const phoneNumber = requireText(el.authPhone.value, "phone_number");
        const payload = await callApi("Auth Login", {
            method: "POST",
            path: "/api/auth/login",
            auth: false,
            body: { phone_number: phoneNumber }
        });
        applyAuthPayload(payload);
    }));

    el.verifyRegionBtn.addEventListener("click", () => runAction("MemberRegion Verify", async () => {
        const regionId = requireNumber(el.regionIdInput.value, "region_id");
        const lat = requireNumber(el.latInput.value, "lat");
        const lng = requireNumber(el.lngInput.value, "lng");
        await callApi("MemberRegion Verify", {
            method: "POST",
            path: `/api/members/me/regions/${regionId}/verify`,
            body: { lat, lng }
        });
    }));

    el.memberMeBtn.addEventListener("click", () => runAction("Member Me", () => callApi("Member Me", {
        method: "GET",
        path: "/api/members/me"
    })));

    el.memberRegionsBtn.addEventListener("click", () => runAction("Member Regions", () => callApi("Member Regions", {
        method: "GET",
        path: "/api/members/me/regions"
    })));

    el.updateMemberBtn.addEventListener("click", () => runAction("Member Update", () => callApi("Member Update", {
        method: "PATCH",
        path: "/api/members/me",
        body: parseJsonEditor(el.updateMemberBody.value, "update member body")
    })));

    el.updateProfileBtn.addEventListener("click", () => runAction("Member ProfileImage", () => callApi("Member ProfileImage", {
        method: "PUT",
        path: "/api/members/me/profile-image",
        body: { profile_image: requireText(el.profileImageInput.value, "profile_image") }
    })));

    el.updateNicknameBtn.addEventListener("click", () => runAction("Member Nickname", () => callApi("Member Nickname", {
        method: "PATCH",
        path: "/api/members/me/nickname",
        body: { nickname: requireText(el.nicknameInput.value, "nickname") }
    })));

    el.addInterestBtn.addEventListener("click", () => runAction("Member AddInterest", () => callApi("Member AddInterest", {
        method: "PUT",
        path: `/api/members/me/interests/${requireNumber(el.interestListingIdInput.value, "listing_id")}`
    })));

    el.deleteInterestBtn.addEventListener("click", () => runAction("Member DeleteInterest", () => callApi("Member DeleteInterest", {
        method: "DELETE",
        path: `/api/members/me/interests/${requireNumber(el.interestListingIdInput.value, "listing_id")}`
    })));

    el.listInterestsBtn.addEventListener("click", () => runAction("Member Interests", () => callApi("Member Interests", {
        method: "GET",
        path: "/api/members/me/interests",
        query: {
            last_interest_id: optionalNumber(el.lastInterestIdInput.value),
            size: requireNumber(el.interestSizeInput.value || "20", "size")
        }
    })));

    el.withdrawBtn.addEventListener("click", () => runAction("Member Withdraw", () => callApi("Member Withdraw", {
        method: "DELETE",
        path: "/api/members/me"
    })));

    el.getListingsBtn.addEventListener("click", () => runAction("Listing List", () => callApi("Listing List", {
        method: "GET",
        path: "/api/listings",
        query: buildListingsQuery(
            requireNumber(el.regionIdInput.value, "region_id"),
            optionalNumber(el.lastListingIdInput.value)
        )
    })));

    el.createDraftBtn.addEventListener("click", () => runAction("Listing CreateDraft", async () => {
        const payload = await callApi("Listing CreateDraft", {
            method: "POST",
            path: "/api/listings/drafts",
            query: {
                region_id: requireNumber(el.regionIdInput.value, "region_id")
            }
        });

        const listingId = payload?.listingId ?? payload?.listing_id;
        if (listingId !== undefined && listingId !== null) {
            el.listingIdInput.value = String(listingId);
        }
    }));

    el.getListingBtn.addEventListener("click", () => runAction("Listing Detail", () => callApi("Listing Detail", {
        method: "GET",
        path: `/api/listings/${requireNumber(el.listingIdInput.value, "listing_id")}`
    })));

    el.updateListingBtn.addEventListener("click", () => runAction("Listing Update", () => callApi("Listing Update", {
        method: "PUT",
        path: `/api/listings/${requireNumber(el.listingIdInput.value, "listing_id")}`,
        body: parseJsonEditor(el.listingUpdateBody.value, "listing update body")
    })));

    el.publishBtn.addEventListener("click", () => runAction("Listing Publish", () => callApi("Listing Publish", {
        method: "POST",
        path: `/api/listings/${requireNumber(el.listingIdInput.value, "listing_id")}/publish`
    })));

    el.hideBtn.addEventListener("click", () => runAction("Listing Hide", () => callApi("Listing Hide", {
        method: "POST",
        path: `/api/listings/${requireNumber(el.listingIdInput.value, "listing_id")}/hide`
    })));

    el.unhideBtn.addEventListener("click", () => runAction("Listing Unhide", () => callApi("Listing Unhide", {
        method: "POST",
        path: `/api/listings/${requireNumber(el.listingIdInput.value, "listing_id")}/unhide`
    })));

    el.reserveBtn.addEventListener("click", () => runAction("Listing Reserve", () => callApi("Listing Reserve", {
        method: "POST",
        path: `/api/listings/${requireNumber(el.listingIdInput.value, "listing_id")}/reserve`,
        body: { buyer_id: requireNumber(el.buyerIdInput.value, "buyer_id") }
    })));

    el.cancelReserveBtn.addEventListener("click", () => runAction("Listing CancelReserve", () => callApi("Listing CancelReserve", {
        method: "POST",
        path: `/api/listings/${requireNumber(el.listingIdInput.value, "listing_id")}/reserve/cancel`
    })));

    el.soldOutBtn.addEventListener("click", () => runAction("Listing SoldOut", () => callApi("Listing SoldOut", {
        method: "POST",
        path: `/api/listings/${requireNumber(el.listingIdInput.value, "listing_id")}/sold-out`,
        body: { buyer_id: requireNumber(el.buyerIdInput.value, "buyer_id") }
    })));

    el.deleteListingBtn.addEventListener("click", () => runAction("Listing Delete", () => callApi("Listing Delete", {
        method: "DELETE",
        path: `/api/listings/${requireNumber(el.listingIdInput.value, "listing_id")}`
    })));
}

async function runAction(name, action) {
    try {
        await action();
    } catch (error) {
        if (error && error.logged) {
            return;
        }
        showLocalError(name, error instanceof Error ? error.message : String(error));
    }
}

async function callApi(name, request) {
    const method = request.method || "GET";
    const query = request.query || {};
    const url = buildUrl(request.path, query);
    const headers = {};

    if (request.body !== undefined) {
        headers["Content-Type"] = "application/json";
    }
    if (request.auth !== false && state.token) {
        headers.Authorization = `Bearer ${state.token}`;
    }

    const startAt = performance.now();

    try {
        const response = await fetch(url, {
            method,
            headers,
            body: request.body !== undefined ? JSON.stringify(request.body) : undefined
        });

        const raw = await response.text();
        const payload = tryParseJson(raw);

        const record = {
            time: nowTime(),
            name,
            method,
            url,
            status: response.status,
            ok: response.ok,
            durationMs: Math.round(performance.now() - startAt),
            payload
        };

        applyRecord(record);

        if (!response.ok) {
            const message = extractErrorMessage(payload, response.status, response.statusText);
            const error = new Error(message);
            error.logged = true;
            throw error;
        }

        return payload;
    } catch (error) {
        if (error && error.logged) {
            throw error;
        }

        const record = {
            time: nowTime(),
            name,
            method,
            url,
            status: "NETWORK",
            ok: false,
            durationMs: Math.round(performance.now() - startAt),
            payload: {
                message: error instanceof Error ? error.message : String(error)
            }
        };

        applyRecord(record);
        const wrapped = new Error(record.payload.message);
        wrapped.logged = true;
        throw wrapped;
    }
}

function applyAuthPayload(payload) {
    if (!payload || typeof payload !== "object") {
        return;
    }

    const token = payload.accessToken ?? payload.access_token;
    const memberId = payload.memberId ?? payload.member_id;

    if (token) {
        state.token = String(token);
    }
    if (memberId !== undefined && memberId !== null) {
        state.memberId = String(memberId);
    }

    persistState();
    renderSession();
}

function applyRecord(record) {
    state.history = [record, ...state.history].slice(0, MAX_HISTORY);
    renderRecord(record);
    renderHistory();
}

function renderRecord(record) {
    el.requestLine.textContent = `${record.method} ${record.url}`;
    el.durationText.textContent = `${record.name} | ${record.durationMs}ms | ${record.time}`;
    el.statusBadge.textContent = String(record.status);
    el.statusBadge.className = `badge ${record.ok ? "ok" : "error"}`;
    el.responseBody.textContent = formatPayload(record.payload);
}

function renderHistory() {
    if (state.history.length === 0) {
        el.historyList.innerHTML = "<li>No requests yet.</li>";
        return;
    }

    el.historyList.innerHTML = state.history.map((item) => {
        const status = String(item.status);
        return `<li>[${escapeHtml(item.time)}] ${escapeHtml(status)} | ${escapeHtml(item.method)} | ${escapeHtml(item.name)}</li>`;
    }).join("");
}

function renderSession() {
    el.baseUrl.value = state.baseUrl;
    el.tokenInput.value = state.token;
    el.currentMemberId.textContent = state.memberId || "-";
}

function hydrateState() {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) {
        return;
    }

    try {
        const parsed = JSON.parse(raw);
        if (parsed.baseUrl) {
            state.baseUrl = normalizeBaseUrl(parsed.baseUrl);
        }
        if (parsed.token) {
            state.token = String(parsed.token);
        }
        if (parsed.memberId) {
            state.memberId = String(parsed.memberId);
        }
    } catch {
        localStorage.removeItem(STORAGE_KEY);
    }
}

function persistState() {
    localStorage.setItem(STORAGE_KEY, JSON.stringify({
        baseUrl: state.baseUrl,
        token: state.token,
        memberId: state.memberId
    }));
}

function showLocalResult(name, message) {
    applyRecord({
        time: nowTime(),
        name,
        method: "LOCAL",
        url: "-",
        status: "OK",
        ok: true,
        durationMs: 0,
        payload: { message }
    });
}

function showLocalError(name, message) {
    applyRecord({
        time: nowTime(),
        name,
        method: "LOCAL",
        url: "-",
        status: "INPUT",
        ok: false,
        durationMs: 0,
        payload: { message }
    });
}

function buildUrl(path, query) {
    const base = normalizeBaseUrl(state.baseUrl);
    const url = new URL(`${base}${path}`);

    Object.entries(query || {}).forEach(([key, value]) => {
        if (value === undefined || value === null || value === "") {
            return;
        }
        url.searchParams.set(key, String(value));
    });

    return url.toString();
}

function buildListingsQuery(regionId, lastListingId) {
    const query = {
        region_id: regionId,
        regionId
    };

    if (lastListingId !== undefined) {
        query.last_listing_id = lastListingId;
        query.lastListingId = lastListingId;
    }

    return query;
}

function normalizeBaseUrl(value) {
    const text = (value || "").trim();
    if (!text) {
        return "http://localhost:8080";
    }
    return text.replace(/\/+$/, "");
}

function parseJsonEditor(raw, label) {
    const text = (raw || "").trim();
    if (!text) {
        throw new Error(`${label} is required`);
    }

    try {
        return JSON.parse(text);
    } catch {
        throw new Error(`${label} must be valid JSON`);
    }
}

function requireText(value, label) {
    const text = (value || "").trim();
    if (!text) {
        throw new Error(`${label} is required`);
    }
    return text;
}

function requireNumber(value, label) {
    const parsed = Number(value);
    if (!Number.isFinite(parsed)) {
        throw new Error(`${label} must be a number`);
    }
    return parsed;
}

function optionalNumber(value) {
    const text = (value || "").trim();
    if (!text) {
        return undefined;
    }

    const parsed = Number(text);
    if (!Number.isFinite(parsed)) {
        throw new Error("optional number input is invalid");
    }
    return parsed;
}

function tryParseJson(value) {
    if (!value) {
        return null;
    }

    try {
        return JSON.parse(value);
    } catch {
        return value;
    }
}

function extractErrorMessage(payload, status, statusText) {
    if (payload && typeof payload === "object" && payload.message) {
        return `[${status}] ${payload.message}`;
    }
    if (typeof payload === "string" && payload.trim()) {
        return `[${status}] ${payload}`;
    }
    return `[${status}] ${statusText}`;
}

function formatPayload(payload) {
    if (payload === null || payload === undefined) {
        return "null";
    }

    if (typeof payload === "string") {
        return payload;
    }

    try {
        return JSON.stringify(payload, null, 2);
    } catch {
        return String(payload);
    }
}

function nowTime() {
    return new Date().toLocaleTimeString("ko-KR", { hour12: false });
}

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

async function cleanupLegacyServiceWorker() {
    if (!("serviceWorker" in navigator)) {
        return;
    }

    try {
        const regs = await navigator.serviceWorker.getRegistrations();
        await Promise.all(regs.map((reg) => reg.unregister()));
    } catch {
        // ignore cleanup failure
    }

    if ("caches" in window) {
        try {
            const keys = await caches.keys();
            await Promise.all(keys.map((key) => caches.delete(key)));
        } catch {
            // ignore cleanup failure
        }
    }
}
