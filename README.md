# rewards
## ✨ 쿠폰 서비스 프로젝트
쿠폰 지급 기능 구현

### 📌 기술 스택
- **Language**: Java 21  
- **Framework**: Spring Boot, Spring Security  
- **Authentication**: JWT  
- **Database**: MySQL  
- **Build Tool**: Gradle  

### 🎯 주요 기능
- 쿠폰 생성, 조회, 삭제 (CRUD)
- 쿠폰 유효성 검사
- JWT 인증 기반 사용자 인증 및 권한 처리
- 쿠폰 선착순 발급 및 중복 방지 로직

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
- JWT 토큰을 이용한 로그인 및 사용자 인증
- 로그인 시 토큰 발급, 요청 시 토큰 검증

### 📈 개발 진행 상황
- [x] 회원가입, 로그인
- [x] 스프링시큐리티
- [x] 쿠폰 CRUD 기능 개발
- [ ] 쿠폰 페이징처리
- [ ] 사용자 선착순 쿠폰 발행 기능 추가
- [ ] 사용자 권한에 따라 기능구분을 위한 role 추가
- [ ] 로그인 기능 시큐리티 필터대신 controller에서 처리하도록하기(내용정리하기)  
- [ ] application.yml 구성  
- [ ] 실행 테스트 (`./gradlew bootRun`)  
- [ ] 배포 설정 (application.properties 로컬과 올릴거 구분시키기/브랜치랑 설정은 -dev -qa로 운영하고 분리시켜줘야함)
