# rewards
## 🎁 쿠폰/리워드 운영 서비스
관리자가 쿠폰을 생성하고 사용자가 선착순으로 쿠폰을 발급받는 백엔드 서비스입니다.

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

### 📂 패키지 구조
```
└── src
    └── main
        ├── java
        │   └── com.example.coupon
        │       ├── config         # 설정 파일 (CORS, Swagger 등)
        │       ├── constant       # 공통 상수 정의 (Enum, Status 코드 등)
        │       ├── controller     # API 엔드포인트
        │       ├── domain         # 핵심 비즈니스 로직 담당 도메인 객체 (DDD 적용 영역, Entity 등을 포함)
        │       ├── exception      # 예외 클래스 및 글로벌 예외 핸들러
        │       ├── infra          # 외부 시스템과의 통신 (Redis, Kafka 등) 관련 구현체
        │       ├── repository     # JPA Repository 인터페이스
        │       ├── security       # Spring Security 설정 및 인증 관련 컴포넌트(JWT 인증/인가)
        │       └── service        # 도메인 객체를 활용한 비즈니스 로직 처리 계층
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
  <img width="1069" alt="image" src="https://github.com/user-attachments/assets/df2f6d47-cbbe-4b5b-a92b-e25287229d47" />
