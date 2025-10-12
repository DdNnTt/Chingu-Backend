# 🙆‍♀️ 칭구칭구 Backend

![Version](https://img.shields.io/badge/version-1.3.7-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-6DB33F?logo=springboot&logoColor=white)
![Java](https://img.shields.io/badge/Java%2017-007396?logo=openjdk&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A?logo=gradle&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/CI%2FCD-GitHub%20Actions-2088FF?logo=githubactions&logoColor=white)

---

## 🌐 Links

| 항목 | 링크 |
|------|------|
| 🖥️ **실서버 (Production)** | [https://chinguchingu.vercel.app](https://chinguchingu.vercel.app) |
| 🎨 **프론트엔드 레포지토리** | [chinguchingu-frontend (Vercel)](https://github.com/DdNnTt/Chingu-Frontend) |
| 📘 **API 문서 (Swagger)** | [https://chinguchingu.kro.kr/swagger-ui/index.html](https://chinguchingu.kro.kr/swagger-ui/index.html) |

---

## 💡 프로젝트 소개

> **우리만의 공간, 진짜 친구들과의 기록과 추억을 나누다**

이 프로젝트는 로그인한 사용자만 이용 가능한 모바일 기반의 **소셜 플랫폼**으로  
친구들과의 **진짜 소통과 관계 형성**을 중심에 두고 있습니다.  
**마이홈**을 중심으로 친구의 홈을 방문하고, 친구를 맺어  
1:1 채팅은 물론, 그룹을 만들어 추억을 남길 수 있습니다.

---


## ⚙️ 사용 스택 (Tech Stack)

> **Frontend**  
> Next.js, TypeScript, Zustand, Storybook
>
> **Backend**  
> Java 17, Spring Boot, Spring Security, Spring Data JPA, SSE (Server-Sent Events),  
> Redis (Email 인증 / 캐싱), Jakarta Bean Validation, JWT 기반 인증,  
> AWS SDK for Java (S3), Lombok
>
> **Database**  
> MySQL (AWS RDS)
>
> **Infrastructure / DevOps**  
> AWS EC2, S3, RDS, Nginx, GitHub Actions (CI/CD), Git, SSL

---

## 🧩 주요 기능 (Key Features)

| 기능 | 설명                                                                         |
|------|----------------------------------------------------------------------------|
| 👤 **회원 기능** | 회원가입, 로그인(JWT), 이메일 인증(메일+Redis), 프로필 수정, 탈퇴, 소셜 로그인 지원(KakaoTalk, Google) |
| 👥 **친구 / 그룹 기능** | 친구 목록, 친구 요청, 그룹 생성 및 초대, 일정, 추억 앨범                                        |
| 💬 **쪽지 기능** | 사용자 간 쪽지 송수신, 쪽지함 관리                                                       |
| 🧠 **나를 맞춰봐 퀴즈** | 사용자 맞춤형 퀴즈 기능 (친구와의 우정점수 측정)                                               |
| 🛠️ **관리자(Admin)** | 전체 회원/그룹 관리                                                                |
| 🔐 **보안** | Spring Security + JWT 기반 인증 구조                                             |
| ☁️ **운영 및 배포** | AWS EC2, RDS, S3 기반 서버 운영 및 GitHub Actions 자동 배포                           |

---

## 📁 프로젝트 구조

```bash
src
├── main
│   ├── java/com/chingubackend
│   │   ├── config/          # 설정 (Security, Swagger, AWS, Redis, WebSocket 등)
│   │   ├── controller/      # API Controller (admin, user, group, message 등)
│   │   ├── dto/             # Request / Response DTO
│   │   ├── entity/          # JPA Entity (User, Group, Message, Quiz 등)
│   │   ├── exception/       # 예외 처리 (GlobalExceptionHandler, Custom Exception)
│   │   ├── jwt/             # JWT 생성 및 인증 관련 로직
│   │   ├── model/           # Enum 및 상수 정의 (Role, SocialType, Status 등)
│   │   ├── repository/      # JPA Repository 인터페이스
│   │   ├── security/        # Spring Security, OAuth2 설정 및 사용자 인증
│   │   └── service/         # 비즈니스 로직 계층 (admin, group, user 등 세분화)
│   └── resources/
│       ├── application.properties
│       ├── static/
│       └── templates/
└── test/java/com/chingubackend
    └── ChinguBackendApplicationTests.java
```

## 🚀 실행 방법 (Local)

### 1️⃣ 환경 변수 설정

프로젝트 루트에 `application.properties` 파일을 생성하고,  
DB·JWT·Redis·메일·AWS 관련 환경 변수를 등록하세요.  
(예: DB_URL, DB_USERNAME, JWT_SECRET_KEY 등)

### 2️⃣ 빌드 및 실행

```bash
# Gradle Wrapper로 빌드
./gradlew clean build

# 빌드된 JAR 실행
java -jar build/libs/chingu-backend-0.0.1-SNAPSHOT.jar
```

> 💡 기본 포트: 8080


### 3️⃣ 테스트 실행
```
./gradlew test
```

### 4️⃣ 서버 로그 확인 (EC2 기준)
```
# 백그라운드 실행
nohup java -jar build/libs/chingu-backend-0.0.1-SNAPSHOT.jar > logs/application.log 2>&1 &

# 로그 실시간 확인
tail -f logs/application.log
```

---

## 👥 Team Members

| 이름      | 역할 | 담당 기능 | GitHub |
|---------|------|------------|--------|
| **모리**  | Frontend Developer | 회원 전체, 친구 그룹 기능, 소셜 로그인, 와이어프레임 설계 | [@sooozi](https://github.com/sooozi) |
| **해니니** | Frontend Developer | 프로젝트 기본 세팅, 마이 홈, 쪽지, 나를 맞춰봐, 관리자, 와이어프레임 설계 | [@henny1105](https://github.com/henny1105) |
| **골드송** | Backend Developer | 마이 홈, 쪽지, 나를 맞춰봐, API 명세, 와이어프레임 설계 | [@goldsonge](https://github.com/goldsonge) |
| **드정**  | Backend Developer / Server Ops | 프로젝트 기본 세팅, 회원 전체, 친구 그룹 기능, 관리자, 서버 배포 및 CI/CD 구축, ERD 설계, 와이어프레임 설계 | [@jeongggggg](https://github.com/jeongggggg) |

---
### 🧠 추가 정보

- 프로젝트 아키텍처: RESTful API 기반 3-Tier 구조 
- 인증 방식: JWT + OAuth2 (Google, Kakao)
- 배포 방식: GitHub Actions → EC2 (Gradle build & jar deploy)
- 파일 업로드: AWS S3 Presigned URL 방식 
- 데이터 캐싱 및 인증: Redis 활용 
- 문서화: Springdoc OpenAPI (Swagger), Notion