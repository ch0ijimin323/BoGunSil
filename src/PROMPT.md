# 보건실 관리 시스템 백엔드 풀스택 구현 및 GitHub(`BoGunSil`) 자동 생성·배포 마스터 명세서

당신은 시니어 Spring Boot 백엔드 아키텍트입니다. 아래 요구사항을 바탕으로 **GitHub 원격 저장소(`BoGunSil`) 생성부터 초기 프로젝트 세팅, 전체 52개 API(어드민 48개 + 유저 키오스크 4개) 구현, 초기 시드 데이터, 빌드 검증, 그리고 단계별 커밋 및 푸시까지** 하나도 빠짐없이 완성하십시오.

---

## 0. Git 및 GitHub(`BoGunSil`) 자동 생성·배포 규칙 (최우선 준수)

코드 작성 전부터 완료 시점까지 아래 Git 및 GitHub CLI(`gh`) 규칙을 반드시 따르십시오:

1. **로컬 Git 초기화 및 `.gitignore` 세팅**:
   - 현재 디렉토리에 `.git`이 없다면 `git init`을 실행하고 기본 브랜치를 `main`으로 설정(`git branch -M main`)한다.
   - Java, Gradle, IntelliJ(`.idea/`, `*.iml`), OS(`.DS_Store`), 환경변수/시크릿 파일(`application-secret.yml`, `.env`)을 제외하는 `.gitignore` 파일을 가장 먼저 생성한다.
2. **새 GitHub 저장소(`BoGunSil`) 생성 및 원격 연결**:
   - 원격 저장소(`origin`)가 연결되어 있지 않은 경우, 터미널에서 GitHub CLI를 실행하여 **`BoGunSil`**이라는 이름으로 새 저장소를 생성하고 원격(`origin`)에 즉시 연결한다:
     ```bash
     gh repo create BoGunSil --private --source=. --remote=origin
     ```
     *(※ 만약 공개 저장소를 원하거나 `gh` CLI가 설치/로그인되어 있지 않아 명령이 실패하면, 로컬 커밋은 모두 정상적으로 진행한 뒤 마지막에 사용자가 수동으로 연결할 수 있도록 안내한다.)*
3. **단계별 단위 커밋 (Conventional Commits 규칙)**:
   - 모든 코드를 한 번에 커밋하지 말고, **아래 6개 마일스톤이 끝날 때마다 `./gradlew compileJava`로 컴파일 확인 후 의미 있는 단위로 `git add` 및 `git commit`을 수행**한다.
   - 커밋 메시지 형식:
      - `chore: 초기 프로젝트 및 Gradle/Security/MySQL 환경 설정`
      - `feat(auth): 관리자 로그인, 토큰 재발급, 공통코드, 환경설정 및 대시보드 API 구현`
      - `feat(admin): 학년반(엑셀 업로드), 일정, 요양호자, 병원치료 관리 API 구현`
      - `feat(healthlog): 보건일지 CRUD, 일자 일괄 변경, 일괄 출력 및 학생 키오스크 접수 API 구현`
      - `feat(medicine): 약품구입, 약품수불부 입출고 연동 및 4종 통계 API 구현`
      - `chore: 초기 시드 데이터 로더 추가 및 전체 빌드 검증 완료`
4. **GitHub 원격 저장소 Push**:
   - 각 마일스톤 커밋 직후 `git push -u origin main`을 실행하여 `BoGunSil` 저장소에 실시간으로 반영한다.

---

## 1. 기술 스택 및 환경 설정

- **Language / Framework**: Java 17, Spring Boot 3.3.5, Gradle (Groovy)
- **Database**: MySQL 8.x (`spring-boot-starter-data-jpa`, `mysql-connector-j`)
- **Security**: Spring Security 6 + JWT (`io.jsonwebtoken:jjwt-api:0.12.6`)
- **Validation & Utils**: `spring-boot-starter-validation`, Lombok, Apache POI (`org.apache.poi:poi-ooxml:5.3.0`)
- **Package Root**: `com.school.healthroom`

### `application.yml` 구성
- DB URL: `jdbc:mysql://localhost:3306/health_room?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Seoul&characterEncoding=UTF-8`
- `spring.jpa.hibernate.ddl-auto: update`
- `spring.jpa.open-in-view: false`
- `spring.servlet.multipart.max-file-size: 10MB`
- JWT 설정: `secret` (Base64 256bit 이상), `access-expiration: 3600000` (1시간), `refresh-expiration: 1209600000` (14일)

---

## 2. 공통 아키텍처 및 보안 정책

1. **공통 응답 포맷 (`ApiResponse<T>`)**:
   - 모든 컨트롤러는 `ResponseEntity<ApiResponse<T>>`를 반환한다.
   - JSON 필드: `{ "success": boolean, "code": String, "message": String, "data": T }`
2. **예외 처리 (`GlobalExceptionHandler`)**:
   - `CustomException` 및 `ErrorCode` Enum 정의 (`INVALID_INPUT`, `UNAUTHORIZED`, `INVALID_CREDENTIALS`, `INVALID_TOKEN`, `NOT_FOUND`, `DUPLICATE_RESOURCE`, `INSUFFICIENT_STOCK`, `EXCEL_PARSE_ERROR`, `INTERNAL_ERROR`).
   - `@Valid` 검증 실패 시 첫 번째 필드 에러 메시지를 담아 `400 Bad Request` 반환.
3. **보안 설정 (`SecurityConfig`)**:
   - Stateless 세션 정책, CSRF 비활성화, CORS 전체 허용(`*`).
   - 인증 없이 허용할 경로(`permitAll`):
      - `/api/admin/auth/login`, `/api/admin/auth/reissue`
      - `/api/user/**` (학생 방문 접수 키오스크 전용 API)
   - 그 외 `/api/admin/**` 모든 경로는 `Authorization: Bearer <token>` JWT 인증 필수.

---

## 3. 데이터베이스 엔티티 설계 (총 11개 테이블)

모든 엔티티는 `createdAt`, `updatedAt`을 가지는 `BaseTimeEntity`를 상속한다 (`@EnableJpaAuditing` 활성화 필수).

1. **`Admin`** (`admin`): `id`, `loginId`(unique), `password`(BCrypt), `name`, `refreshToken`
2. **`CommonCode`** (`common_code`): `id`, `groupCode`, `code`, `codeName`, `parentCode`(증상별 처치 매핑용 상위 코드), `sortOrder`, `useYn`, `description` (`groupCode` + `code` 유니크 제약)
3. **`SystemSetting`** (`system_setting`): `id`(고정 `1L`), `schoolName`, `teacherName`, `retentionYears`(기본 5), `kioskEnabled`(기본 true)
4. **`GradeClass`** (`grade_class`): `id`, `schoolYear`, `grade`, `classNo`, `teacherName`, `studentCount` (`schoolYear` + `grade` + `classNo` 유니크 제약)
5. **`Schedule`** (`schedule`): `id`, `title`, `startDate`, `endDate`, `startTime`, `endTime`, `category`, `content`
6. **`CareStudent`** (`care_student`): `id`, `schoolYear`, `grade`, `classNo`, `studentNo`, `studentName`, `diseaseName`, `emergencyContact`, `careGuide`
7. **`HospitalTreatment`** (`hospital_treatment`): `id`, `visitDate`, `grade`, `classNo`, `studentNo`, `studentName`, `symptom`, `hospitalName`, `companion`, `treatmentResult`
8. **`HealthLog`** (`health_log`): `id`, `visitDate`, `visitTime`, `grade`, `classNo`, `studentNo`, `studentName`, `gender`, `symptomCode`, `symptomDetail`, `treatmentCode`, `treatmentDetail`, `sourceType`(`ADMIN` 또는 `KIOSK`), `memo`, `medicines`(`OneToMany` -> `HealthLogMedicine`, `cascade = ALL`, `orphanRemoval = true`)
9. **`HealthLogMedicine`** (`health_log_medicine`): `id`, `healthLog`(FK), `medicineCode`, `medicineName`, `quantity`
10. **`MedicinePurchase`** (`medicine_purchase`): `id`, `purchaseDate`, `medicineCode`, `medicineName`, `unit`, `quantity`, `unitPrice`, `totalPrice`, `supplier`, `expiryDate`
11. **`MedicineLedger`** (`medicine_ledger`): `id`, `ledgerDate`, `medicineCode`, `medicineName`, `ioType`(`IN` 또는 `OUT`), `inQuantity`, `outQuantity`, `stockQuantity`(해당 시점 누적 잔여 재고), `refId`(구입ID 또는 보건일지ID), `remark`

---

## 4. 전체 API 명세 (총 52개 엔드포인트)

### A. 인증 · 환경설정 · 공통코드 · 대시보드 (12개)
| 기능 | Method | Endpoint | 핵심 로직 |
| :--- | :--- | :--- | :--- |
| 로그인 | `POST` | `/api/admin/auth/login` | `loginId`, `password` 검증 후 Access/Refresh Token 발급 및 DB 저장 |
| 토큰 재발급 | `POST` | `/api/admin/auth/reissue` | `refreshToken` 검증 및 일치 여부 확인 후 새 토큰 쌍 발급 |
| 대시보드 조회 | `GET` | `/api/admin/dashboard` | 오늘 방문자 수, 이번 달 방문자 수, 요양호자 수, 금일 일정 목록, 최근 보건일지 5건 반환 |
| 환경설정 조회 | `GET` | `/api/admin/settings` | 설정 조회 (없으면 기본값 자동 생성 후 반환) |
| 환경설정 저장 | `PUT` | `/api/admin/settings` | 학교명, 보건교사명, 보관기간(년), 키오스크 활성화 여부 수정 |
| 환경설정 기본값복원 | `POST` | `/api/admin/settings/reset` | 기본값(`OO고등학교`, `보건교사`, `5년`, `true`)으로 초기화 |
| 보관기간 경과 데이터 삭제 | `DELETE` | `/api/admin/settings/expired-data` | 현재 날짜 기준 `retentionYears`년 이전의 보건일지, 병원치료, 약품구입/수불부 데이터 일괄 삭제 및 삭제 건수 반환 |
| 공통코드 목록조회 | `GET` | `/api/admin/common-codes` | `groupCode`, `useYn` 필터링 및 `sortOrder` 오름차순 조회 |
| 공통코드 등록 | `POST` | `/api/admin/common-codes` | 중복 코드 검사 후 등록 |
| 공통코드 수정 | `PUT` | `/api/admin/common-codes/{id}` | 코드명, 상위코드, 정렬순서, 사용여부, 설명 수정 |
| 공통코드 삭제 | `DELETE` | `/api/admin/common-codes/{id}` | 단건 삭제 |

### B. 학년반 · 보건실 일정 (10개)
| 기능 | Method | Endpoint | 핵심 로직 |
| :--- | :--- | :--- | :--- |
| 학년반 목록조회 | `GET` | `/api/admin/grade-classes` | `schoolYear`, `grade` 필터링 조회 |
| 학년반 등록 | `POST` | `/api/admin/grade-classes` | 학년도+학년+반 중복 검사 후 등록 |
| 학년반 수정 | `PUT` | `/api/admin/grade-classes/{id}` | 학년, 반, 담임교사, 학생 수 수정 |
| 학년반 삭제 | `DELETE` | `/api/admin/grade-classes/{id}` | 단건 삭제 |
| 학년반 엑셀 일괄업로드 | `POST` | `/api/admin/grade-classes/excel-upload` | `MultipartFile`(`.xlsx`) 파싱(0행 헤더 제외: 학년도, 학년, 반, 담임교사, 학생수) 후 기존 데이터면 업데이트, 신규면 삽입 |
| 일정 목록조회 | `GET` | `/api/admin/schedules` | `startDate`, `endDate`, `keyword` 조건 검색 |
| 일정 달력조회 | `GET` | `/api/admin/schedules/calendar` | `year`, `month` 파라미터로 해당 월과 겹치는 모든 일정 조회 |
| 일정 등록 | `POST` | `/api/admin/schedules` | 보건실 일정 등록 |
| 일정 수정 | `PUT` | `/api/admin/schedules/{id}` | 일정 수정 |
| 일정 삭제 | `DELETE` | `/api/admin/schedules/{id}` | 일정 삭제 |

### C. 요양호자 · 병원치료 (10개)
| 기능 | Method | Endpoint | 핵심 로직 |
| :--- | :--- | :--- | :--- |
| 요양호자 목록조회 | `GET` | `/api/admin/care-students` | `schoolYear`, `grade`, `classNo`, `keyword`(이름/질환) 검색 |
| 요양호자 상세조회 | `GET` | `/api/admin/care-students/{id}` | 단건 상세 조회 |
| 요양호자 등록 | `POST` | `/api/admin/care-students` | 요양호자 등록 |
| 요양호자 수정 | `PUT` | `/api/admin/care-students/{id}` | 요양호자 정보 수정 |
| 요양호자 삭제 | `DELETE` | `/api/admin/care-students/{id}` | 요양호자 삭제 |
| 병원치료 목록조회 | `GET` | `/api/admin/hospital-treatments` | `startDate`, `endDate`, `grade`, `keyword` 검색 |
| 병원치료 상세조회 | `GET` | `/api/admin/hospital-treatments/{id}` | 단건 상세 조회 |
| 병원치료 등록 | `POST` | `/api/admin/hospital-treatments` | 외부 병원 이송 및 치료 내역 등록 |
| 병원치료 수정 | `PUT` | `/api/admin/hospital-treatments/{id}` | 내역 수정 |
| 병원치료 삭제 | `DELETE` | `/api/admin/hospital-treatments/{id}` | 내역 삭제 |

### D. 보건일지 · 유저(학생 키오스크) (11개)
| 기능 | Method | Endpoint | 핵심 로직 |
| :--- | :--- | :--- | :--- |
| 보건일지 목록조회 | `GET` | `/api/admin/health-logs` | `startDate`, `endDate`, `grade`, `classNo`, `symptomCode`, `keyword` 다중 조건 페이징 조회 |
| 보건일지 상세조회 | `GET` | `/api/admin/health-logs/{id}` | 투약 약품 목록(`medicines`) 포함 상세 조회 |
| 보건일지 등록 | `POST` | `/api/admin/health-logs` | 일지 저장(`sourceType="ADMIN"`). 투약 약품이 포함된 경우 약품별 재고 확인 후 `MedicineLedger`에 `OUT`(출고) 자동 기록 |
| 보건일지 수정 | `PUT` | `/api/admin/health-logs/{id}` | 기존 투약 약품 출고량을 `IN`(수정원복) 처리 후 변경된 약품 목록 기준으로 재출고 기록 |
| 보건일지 삭제 | `DELETE` | `/api/admin/health-logs/{id}` | 투약 약품이 있었다면 재고 원복(`IN`) 처리 후 일지 삭제 |
| 보건일지 일자변경 | `PATCH` | `/api/admin/health-logs/change-date` | `ids`(List<Long>)와 `targetDate`를 받아 선택된 일지들의 방문일자를 일괄 변경 |
| 보건일지 일괄출력조회 | `GET` | `/api/admin/health-logs/print` | `startDate`, `endDate`, `grade` 조건으로 출력용 전체 리스트(페이징 없음) 반환 |
| [유저] 학년반 선택 목록 조회 | `GET` | `/api/user/grade-classes` | 현재 학년도 기준 학년·반 선택 목록 반환 |
| [유저] 증상 목록 조회 | `GET` | `/api/user/symptoms` | `CommonCode` 중 `groupCode="SYMPTOM"`이고 `useYn=true`인 목록 반환 |
| [유저] 증상별 처치 옵션 조회 | `GET` | `/api/user/treatments` | `symptomCode` 쿼리 파라미터를 받아 `groupCode="TREATMENT"` 중 `parentCode`가 일치하는 처치 옵션 반환 |
| [유저] 보건실 방문 접수 작성 | `POST` | `/api/user/visit` | 키오스크 활성화 여부(`kioskEnabled`) 확인 후 학생 방문 내역을 `HealthLog`(`sourceType="KIOSK"`)로 저장 |

### E. 약품구입 · 약품수불부 · 통계 (9개)
| 기능 | Method | Endpoint | 핵심 로직 |
| :--- | :--- | :--- | :--- |
| 약품구입 목록조회 | `GET` | `/api/admin/medicine-purchases` | `startDate`, `endDate`, `keyword`(약품명/코드) 검색 |
| 약품구입 상세조회 | `GET` | `/api/admin/medicine-purchases/{id}` | 단건 상세 조회 |
| 약품구입 등록 | `POST` | `/api/admin/medicine-purchases` | 구입 내역 저장 및 `MedicineLedger`에 `IN`(입고) 자동 기록, 누적 재고 갱신 |
| 약품구입 수정 | `PUT` | `/api/admin/medicine-purchases/{id}` | 수량 변경 시 차액만큼 `MedicineLedger` 재고 보정 |
| 약품구입 삭제 | `DELETE` | `/api/admin/medicine-purchases/{id}` | 해당 입고량만큼 차감 보정 후 구입 내역 삭제 |
| 약품수불부 조회 | `GET` | `/api/admin/medicine-ledgers` | `startDate`, `endDate`, `medicineCode` 조건으로 입·출고 및 잔여 재고 이력 조회 |
| 보건일지 약품사용통계 | `GET` | `/api/admin/statistics/medicine-usage` | 기간(`startDate`, `endDate`) 내 약품별 총 사용 수량 및 사용 건수 집계 |
| 보건일지 증상분류통계 | `GET` | `/api/admin/statistics/symptoms` | 기간(`startDate`, `endDate`) 내 증상코드별 방문 건수 및 비율(%) 집계 |
| 학년반별 월간통계 조회 | `GET` | `/api/admin/statistics/monthly/grade-class` | `year` 파라미터 기준 학년·반별 1월~12월 월간 방문 건수 매트릭스 반환 |
| 증상분류별 월간통계 조회 | `GET` | `/api/admin/statistics/monthly/symptoms` | `year` 파라미터 기준 증상코드별 1월~12월 월간 방문 건수 매트릭스 반환 |

---

## 5. 초기 시드 데이터 (`DataInitializer`)

애플리케이션 실행 시(`ApplicationRunner`) DB가 비어있다면 아래 데이터를 자동 삽입하십시오:
1. 기본 관리자 계정: `loginId = "admin"`, `password = "admin1234"` (BCrypt 암호화 저장), `name = "보건교사"`
2. 기본 환경설정: `SystemSetting.createDefault()`
3. 기본 공통코드:
   - `SYMPTOM`: `SYM_01`(두통), `SYM_02`(복통/소화불량), `SYM_03`(외상/찰과상), `SYM_04`(감기/발열), `SYM_05`(안정/휴식)
   - `TREATMENT`: `TRT_01`(진통제 투여, `parentCode=SYM_01`), `TRT_02`(소화제 투여, `parentCode=SYM_02`), `TRT_03`(소독 및 밴드 처치, `parentCode=SYM_03`), `TRT_04`(체온 측정 및 해열제 투여, `parentCode=SYM_04`), `TRT_05`(침상 안정, `parentCode=SYM_05`)
   - `MEDICINE`: `MED_01`(타이레놀정 500mg), `MED_02`(베아제정), `MED_03`(포비돈 스틱), `MED_04`(부루펜정)
4. 기본 학년반: 현재 연도 기준 1~3학년 각 1~3반 샘플 데이터

---

## 6. 실행 및 완료 검증 절차

1. 로컬 Git 초기화 후 `gh repo create BoGunSil --private --source=. --remote=origin`으로 GitHub 저장소를 생성·연결한다. (Public 저장소가 필요하면 `--public`으로 변경 가능)
2. 위 명세대로 모든 디렉토리와 Java 소스 파일을 생성한다.
3. 마일스톤별로 코드를 작성할 때마다 `./gradlew compileJava`로 컴파일 에러 여부를 검증하고 `git commit` & `git push -u origin main`을 수행한다.
4. 모든 구현이 끝난 후 최종적으로 `./gradlew build -x test`를 실행하여 빌드 성공(`BUILD SUCCESSFUL`)을 확인하고, 전체 API 엔드포인트 요약 및 `BoGunSil` 저장소 Push 결과를 보고한다.
