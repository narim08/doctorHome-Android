# DoctorHome

> AI 기반 증상 진단 및 약 복용 관리 서비스 

<br>

## 프로젝트 개요

| | |
|---|---|
| **개발 인원** | 1인 개인 프로젝트 |
| **개발 기간** | 2025.11.09 – 2025.11.26 |
| **플랫폼** | Android App + Web |

사용자가 간편하게 건강을 관리할 수 있도록 돕는 서비스입니다.<br>
(현재 레포지토리는 앱 버전으로, 웹 버전은 별도 레포지토리에 있습니다.)

<br>

## 실행 화면

- 실행 영상 (youtube)
[![DoctorHome Demo](https://img.youtube.com/vi/cyBPEnx-yZQ/0.jpg)](https://www.youtube.com/shorts/cyBPEnx-yZQ)

- 메인 화면(보유 약 CRUD) / 긴급 메시지
![1](https://github.com/user-attachments/assets/973d6921-0ab4-4350-8d7b-9f85b1af56fd)

- AI 진단 / 근처 병원 찾기
![2](https://github.com/user-attachments/assets/2cbc2873-5775-4242-bc3b-9bc150cb5f67)

- 약 복용 캘린더(체크리스트 형태) / 알림 설정
![3](https://github.com/user-attachments/assets/d52acef7-acb7-4fa6-a9a6-0ff9da5e75f9)


<br>

## 기술 스택

### Android App
![Java](https://img.shields.io/badge/Java-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Android Studio](https://img.shields.io/badge/Android_Studio-3DDC84?style=flat-square&logo=android-studio&logoColor=white)

### Web
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![MariaDB](https://img.shields.io/badge/MariaDB-003545?style=flat-square&logo=mariadb&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=mysql&logoColor=white)
![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=flat-square&logo=html5&logoColor=white)
![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=flat-square&logo=css3&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=flat-square&logo=javascript&logoColor=black)

<br>

## 주요 기능

| 기능 | 웹 | 앱 |
|------|:---:|:---:|
| 💊 약 관리 CRUD | ✅ | ✅ |
| 🤖 AI 기반 증상 진단 | ✅ | ✅ |
| 🗺️ 지도 API — 근처 병원/약국 찾기 | ✅ | ✅ |
| 📅 약 복용 기록 캘린더 | ❌ | ✅ |
| 🔔 약 복용 알림 설정 | ❌ | ✅ |
| 🚨 응급 전화/메시지 보내기 | ❌ | ✅ |

<br>

## 개발 목적

1. 집에 보관 중인 약의 종류와 소비기한을 직접 찾아봐야 하는 불편함
   → 약 정보를 메인 화면에서 한눈에 관리할 수 있는 CRUD 기능 구현

2. 증상 발생 시 집에 있는 약 중 복용 가능한 약, 또는 추가로 필요한 약·영양제, 방문할 병원 등을
   일일이 검색해야 하는 번거로움
   → 보유 약 DB를 기반으로 AI 모델과 연동, 증상에 맞는 약 자동 추천 및 진단

3. 현재 위치 주변의 병원·약국을 쉽게 파악하기 어려운 문제
   → 지도 API를 활용해 근처 병원·약국을 마커로 표시하고 이름·전화번호 제공

4. 응급 상황에서 신속한 연락 수단이 필요한 경우
   → 통화 가능 시 119 자동 연결, 통화 불가 시 현재 위치와 구조 요청 메시지를
      긴급 연락처로 자동 발송

5. 약 복용 여부를 기억하거나 관리하기 어려운 점
   → 체크리스트 형식의 복용 관리 + 캘린더 기록 + 알림 설정 기능 구현

<br>

## 트러블슈팅

### 1. 웹 → 앱 확장 과정에서의 구조적 차이 적응

기존 웹 프로젝트를 안드로이드 애플리케이션으로 확장하면서 웹과 모바일 환경의 구조적 차이를 학습하였습니다.

- 메인 화면에 **RecyclerView**를 적용하여 약 정보를 그리드 형태로 구성 → 확장성과 재사용성 향상
- AI 진단 화면에 **ProgressBar**를 추가하여 API 응답 대기 중 사용자 경험(UX) 개선

---

### 2. 지도 API — AVD 에뮬레이터 위치 문제

**문제**

AVD 에뮬레이터의 기본 위치가 미국으로 설정되어 있어, 카카오 맵 API가 정상 동작하지 않았습니다.

**해결**

- 현재 위치가 국내(한국)인지 검증하는 로직 추가
- 해외 위치로 감지될 경우, **서울 시청 좌표로 fallback** 처리하는 예외 로직 구현

---

### 3. Gemini API 연속 호출 시 503 오류

**문제**

AI 증상 진단 중, Gemini API를 연속 호출할 때 503 오류가 반복 발생했습니다.<br>
> 503(Service Unavailable) 오류는 서버가 일시적으로 요청을 처리할 수 없는 상태일 때 발생하는 HTTP 상태 코드입니다. 주로 서버 과부화, 유지보수, 또는 외부 서비스 장애로 인해 발생합니다.

**원인**

API 호출마다 `OkHttpClient` 인스턴스를 새로 생성해서 커넥션 풀이 재사용되지 않고 있었습니다. 이로 인해 짧은 시간 내 다수 요청이 발생할 경우, 서버 자원이 급격히 소모되어 오류가 발생했던 것이었습니다. <br>
커넥션 풀은 HTTP 요청을 보낼 때 매번 새로운 TCP 연결을 생성하는 대신 기존 연결을 재사용하는 방식입니다. TCP는 신뢰성을 높이기 위해 연결 과정에 많은 시간이 필요하므로, 커넥션을 재사용해야 네트워크 비용을 줄여서 API 호출 성능까지 개선할 수 있습니다.
>OkHttpClient는 HTTP 요청을 보내기 위한 Java 기반 HTTP 클라이언트 라이브러리입니다. 커넥션 풀, 요청 재시도 기능 등을 제공하며 안정적인 API 통신을 할 수 있습니다.

**해결**

- `OkHttpClient`를 **싱글톤(static)** 으로 전환하여 커넥션을 재사용하도록 수정했습니다. <br> 해당 인스턴스는 내부적으로 커넥션 풀을 관리하기 때문에 싱글톤으로 관리해야 커넥션을 재사용할 수 있어 성능을 개선할 수 있습니다. 
- **지수 백오프(Exponential Backoff)** 기반 재시도 로직을 추가했습니다. <br>모든 클라이언트가 동시에 같은 간격으로 재시도 할 경우, 트래픽이 몰려 서버 과부화가 일어날 수 있기 때문에 지수적으로 나눠서 시간에 따라 요청이 분산되도록 했습니다. 

**결과** 

API 응답 시간이 **40초 이상 또는 503 실패 → 평균 15~20초**로 모든 요청 성공 및 시간이 단축되었습니다.

<br>

## 실행 방법

### Requirements
- Android Studio (latest recommended)
- Android SDK 34+
- Kakao Map API Key
- Gemini API Key

### Run
```bash
#1. Clone the repository
git clone https://github.com/narim08/doctorHome-Android.git

#2. Open in Android Studio

#3. Add your API keys to local.properties or the appropriate config file

#4. Build and run on emulator or physical device
```

> ⚠️ **AVD 에뮬레이터 사용 시:** 기본 위치가 미국으로 설정되어 있을 수 있습니다. 지도 기능은 자동으로 서울 시청 좌표로 대체됩니다. 
