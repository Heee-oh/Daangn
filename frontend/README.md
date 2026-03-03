# Frontend Controller Test Console

`frontend/`는 백엔드 컨트롤러를 수동 테스트하기 위한 정적 웹 콘솔입니다.

## 실행 방법

1. 백엔드 실행
```bash
./gradlew bootRun
```

2. 프론트 정적 서버 실행 (프로젝트 루트에서)
```bash
python -m http.server 5173 --directory frontend
```

3. 브라우저 접속
```text
http://localhost:5173
```

## 지원 범위

- `AuthController`
  - `POST /api/auth/signup`
  - `POST /api/auth/login`
- `MemberRegionController`
  - `POST /api/members/me/regions/{region_id}/verify`
- `MemberController`
  - `GET /api/members/me/regions`
  - `GET /api/members/me`
  - `PATCH /api/members/me`
  - `PUT /api/members/me/profile-image`
  - `PATCH /api/members/me/nickname`
  - `PUT /api/members/me/interests/{listing_id}`
  - `DELETE /api/members/me/interests/{listing_id}`
  - `GET /api/members/me/interests`
  - `DELETE /api/members/me`
- `ListingController`
  - `GET /api/listings`
  - `POST /api/listings/drafts`
  - `GET /api/listings/{listing_id}`
  - `PUT /api/listings/{listing_id}`
  - `POST /api/listings/{listing_id}/publish`
  - `POST /api/listings/{listing_id}/hide`
  - `POST /api/listings/{listing_id}/unhide`
  - `POST /api/listings/{listing_id}/reserve`
  - `POST /api/listings/{listing_id}/reserve/cancel`
  - `POST /api/listings/{listing_id}/sold-out`
  - `DELETE /api/listings/{listing_id}`

## 특징

- 버튼 클릭 즉시 API 호출
- 마지막 응답을 `status + body`로 즉시 표시
- 요청 히스토리(최신순) 제공
- 로그인/회원가입 응답에서 토큰 자동 반영
- 요청 JSON/쿼리 키는 `snake_case` 기준으로 전송
