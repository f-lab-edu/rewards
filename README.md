# rewards
## 🎁 쿠폰/리워드 운영 서비스
사용자에게 쿠폰을 선착순으로 발급하고, 관리자는 운영 및 성과를 분석할 수 있는 쿠폰 운영 백엔드 서비스입니다.

### 📌 기술 스택
- **Language**: Java 21  
- **Framework**: Spring Boot, Spring Security  
- **Authentication**: JWT  
- **Database**: MySQL  
- **Build Tool**: Gradle  

### 🎯 주요 기능
- JWT 기반 사용자 인증 및 권한 처리
- 쿠폰 생성 / 조회 / 삭제 (CRUD)
- 쿠폰 유효성 검사 및 사용자 조건 검증
- 사용자 선착순 쿠폰 발급 및 중복 발급 방지
- 관리자, 사용자 Role 기반 기능 구분
- Redis를 활용한 중복요청 방지 (5초 내 재요청 거부 등)
🔎 [기능 도출 문제정의 시나리오 상세] https://aaaajin.notion.site/1d2a4c074939803fa1cbc4cf1d87bb38?pvs=4

### 📂 패키지 구조
```
└── src
    └── main
        ├── java
        │   └── com.example.coupon
        │       ├── config         # Security, Swagger 설정
        │       ├── constant       # 상수 정의
        │       ├── controller     # API 엔드포인트
        │       ├── entity         # 엔티티 클래스
        │       ├── exception      # ExceptionHandler 및 커스텀Exception 정의
        │       ├── repository     # JPA Repository
        │       ├── security       # JWT 인증/인가 관련
        │       └── service        # 비즈니스 로직
        └── resources
            └── application.properties
```

### 🔐 인증 방식
- 사용자 로그인 시 JWT 토큰 발급
- 이후 모든 요청에 대해 토큰 기반 인증 수행
- 사용자 권한(Role)에 따라 API 접근 제어

### 쿠폰 선착순 발급 시 고려한점
- 서버간 일관성 유지
- 사용자 편의성을 위해 짧은 응답대기시간
- 중복발급 등의 상황 고려
