# 📱 Plop Messenger
> 스마일게이트 개발 캠프 2022 - 윈터 개발 캠프 2기 - Plop
<img src="https://user-images.githubusercontent.com/58140426/222033085-2fa98aff-7fd5-4932-92fd-d5c955acc013.png" width="200">

SmileGate 윈터데브캠프에서 IOS 개발자 1명, AOS 개발자 1명, 벡엔드 개발자 1명과 함께 협업 프로젝트를 진행했습니다.

<br>

## 1️⃣ 프로젝트 개요

Plop 메신저 애플리케이션

> 페이스북 메신저를 클론한 앱으로 여러 사람들과 친구를 하여 채팅을 하고 접속한 친구도 알 수 있는 앱을 만들었습니다.

<br>

## 2️⃣ 주요 화면
### 1. 1:1 채팅
<img src="https://user-images.githubusercontent.com/58140426/222035975-93710f9d-e6ea-4840-8c24-7ecaa4280d1c.png" width="720">

### 2. 그룹 채팅
<img src="https://user-images.githubusercontent.com/58140426/222036233-040ab562-0aec-4c3c-b77f-e9f62ec86225.png" width="720">

### 3. 멤버 초대 
<img src="https://user-images.githubusercontent.com/58140426/222036513-18fb2eb9-1632-4057-84d9-801ac9107cd1.png" width="720">

### 4. 접속 상태 표시
<img src="https://user-images.githubusercontent.com/58140426/222036414-7fa22d60-b511-4568-afe9-43574056b144.png" width="720">

### 5. 푸시 알림
<img src="https://user-images.githubusercontent.com/58140426/222036574-674207f1-f641-4456-8442-eba6b9838117.png" width="720">

<br>

## 3️⃣ 프로젝트 구조 
> MSA 구조로 API 게이트웨이 서버와 디스커버리 서버를 두고 유저, 채팅 등 여러 마이크로서비스들로 구성하였습니다.

> 저는 채팅 서비스, 접속상태 서비스를 담당하였습니다. 설계와 구현 과정을 [기술 블로그](https://javapp.tistory.com/category/Dev/2022%20Winter%20Dev%20Camp)에 정리했습니다.

> 사용한 기술 스택은 다음과 같습니다.<br>
Java11<br>
Spring Boot 2.7.8<br>
kafka<br>
MongoDB(NoSQL)<br>
Gradle<br>
Git<br>
JUnit5<br>
AWS<br>
Docker<br>


### 주요 작업 
- [채팅, 채팅방, 유저 접속상태 API 로직 구현](https://github.com/sgdevcamp2022/plop/wiki/%EC%B1%84%ED%8C%85-%EA%B8%B0%EB%8A%A5-%EA%B5%AC%ED%98%84-%EB%A1%9C%EC%A7%81)
- [웹소켓 통신에서 pub/sub 구조를 통해 채팅 전송을 하는 로직 설계 및 구현](https://github.com/sgdevcamp2022/plop/wiki/%EC%9B%B9%EC%86%8C%EC%BC%93-%ED%86%B5%EC%8B%A0%EC%97%90%EC%84%9C-pub-sub-%EA%B5%AC%EC%A1%B0%EB%A5%BC-%ED%86%B5%ED%95%B4-%EC%B1%84%ED%8C%85-%EC%A0%84%EC%86%A1%EC%9D%84-%ED%95%98%EB%8A%94-%EB%A1%9C%EC%A7%81-%EC%84%A4%EA%B3%84-%EB%B0%8F-%EA%B5%AC%ED%98%84)
- [외부 메시지 브로커로 카프카를 이용하여 전송](https://github.com/sgdevcamp2022/plop/wiki/Kafka-%EC%82%AC%EC%9A%A9)
- [MongoDB를 이용하여 채팅, 채팅방, 유저접속상태 데이터 관리](https://github.com/sgdevcamp2022/plop/wiki/MongoDB-%EC%82%AC%EC%9A%A9)
- ec2 서버에 카프카 연동
- springdoc으로 REST API 문서화
- [성능 테스트와 그라파나로 모니터링](https://github.com/sgdevcamp2022/plop/wiki/%EC%84%B1%EB%8A%A5-%EA%B0%9C%EC%84%A0)
<br>

## 4️⃣ 아키텍처
<img src="https://user-images.githubusercontent.com/58140426/222035675-11bb537f-8feb-4ee3-a4a6-164ebdecce9e.png" width="720">

- AWS EC2 배포하였습니다.
- EC2에 카프카 설치와 도커를 설치하여 인증 토큰을 저장하는 Redis와 모니터링 툴인 그라파나를 
- AWS RDS 를 사용하여 MySQL을 배포하였습니다.
- MongoDB Atlas를 사용하여 클라우드에 MongoDB 배포하였습니다.
