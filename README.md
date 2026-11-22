# BoGunSil (보건실 관리 시스템 백엔드)

Spring Boot 3.3.5 + Java 17 + MySQL 8.x + Spring Security 6 (JWT) 기반의 학교 보건실 통합 관리 및 학생 방문 접수 키오스크 백엔드 시스템입니다.

## 기술 스택
- **Java 17**, **Spring Boot 3.3.5**, **Gradle**
- **Spring Data JPA**, **MySQL 8.x**
- **Spring Security 6 + JWT (`jjwt 0.12.6`)**
- **Apache POI (`poi-ooxml 5.3.0`)** - 학년반 엑셀(`.xlsx`) 일괄 업로드
- **Spring Validation**, **Lombok**

## 초기 시드 데이터 (`DataInitializer`)
애플리케이션 최초 기동 시 자동 생성됩니다.
- **기본 관리자 계정**: `admin` / `admin1234` (`보건교사`)
- **기본 환경설정**: `OO고등학교`, `보건교사`, 보관기간 `5년`, 키오스크 `true`
- **기본 공통코드**:
  - `SYMPTOM`: `SYM_01` ~ `SYM_05`
  - `TREATMENT`: `TRT_01` ~ `TRT_05` (`parentCode`로 증상과 매핑)
  - `MEDICINE`: `MED_01` ~ `MED_04`
- **기본 학년반**: 현재 연도 기준 1~3학년 1~3반 샘플 데이터 (총 9개 학급)
