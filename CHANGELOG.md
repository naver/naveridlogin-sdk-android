# CHANGELOG

## Ver x.y.z
이후 변경사항은 [릴리즈 노트](https://github.com/naver/naveridlogin-sdk-android/wiki/%EB%A6%B4%EB%A6%AC%EC%A6%88-%EB%85%B8%ED%8A%B8)를 참고해 주세요.

---

## Ver 4.2.3

### 기능 개선

* [Chrome Custom Tabs](https://developer.chrome.com/multidevice/android/customtabs)을 이용해 로그인 간소화
* 개발환경이 Eclipse 기반에서 Android Studio 기반으로 변경
  * 기존 jar 배포에서 aar 배포로 변경

### 기능 변경사항

* 리소스 파일 분리
  * OAuthLoginString
  * 내부 이미지 이미지 파일로 변경

### 버그 수정

* 웹뷰를 이용한 로그인 중 언어 선택 후 화면 회전 시 연동이 원활하지 않던 문제 해결

### 기타 변경사항

* 샘플 소스코드 [Github](https://github.com/naver/naveridlogin-sdk-android) 공개

---

## ver 4.1.4

### 기능 개선

* 네이버 앱 설치 유도하는 Dialog 대신 다운로드 배너 보여주도록 변경
* SDK 초기화 시 Callback Intent 값 넣지 않고 앱의 package name 을 넘기도록 변경

---

## ver 4.1.3

### 기능 개선

* 데이터를 사용하는지 Wifi인지 서버로 전달
* 네아로 SDK 버전 정보를 네이버 앱으로 전달 및 버전 정보로 화면 회전 여부 결정

### 버그 수정

* 개발 도구에서 레이아웃을 미리 볼 때 OAuthLoginButton 클래스가 오류를 발생하는 현상 수정

---

## ver 4.1.2

### 기능 추가

* 가로 세로 모드 모두 지원하도록 변경