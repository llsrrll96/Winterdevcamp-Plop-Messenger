# 📱 Plop Messenger
> 스마일게이트 윈터 개발 캠프 2기 2022
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

## 3️⃣ 프로젝트 결과 
> 저는 벡엔드 담당으로 채팅 서비스, 접속상태 서비스를 담당하였습니다. 설계와 구현 과정을 [기술 블로그](https://javapp.tistory.com/category/Dev/2022%20Winter%20Dev%20Camp)에 정리했습니다.

> MSA 구조로 API 게이트웨이 서버와 디스커버리 서버를 두고 유저, 채팅 등 여러 마이크로서비스들로 구성하였습니다.

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
Prometheus&Grafana<br>

> [최종 발표](https://drive.google.com/file/d/1a4kDE4Aer8W8LRsf4IpQH-Hb_nG3kCki/view?usp=sharing)

### 주요 작업 
- [채팅, 채팅방, 유저 접속상태 API 로직 구현](https://github.com/sgdevcamp2022/plop/wiki/%EC%B1%84%ED%8C%85-%EA%B8%B0%EB%8A%A5-%EA%B5%AC%ED%98%84-%EB%A1%9C%EC%A7%81)
- [웹소켓 통신을 위해 STOM의의 pub/sub 구조를 통해 채팅 전송을 하는 로직 설계 및 구현](https://github.com/sgdevcamp2022/plop/wiki/%EC%9B%B9%EC%86%8C%EC%BC%93-%ED%86%B5%EC%8B%A0%EC%97%90%EC%84%9C-pub-sub-%EA%B5%AC%EC%A1%B0%EB%A5%BC-%ED%86%B5%ED%95%B4-%EC%B1%84%ED%8C%85-%EC%A0%84%EC%86%A1%EC%9D%84-%ED%95%98%EB%8A%94-%EB%A1%9C%EC%A7%81-%EC%84%A4%EA%B3%84-%EB%B0%8F-%EA%B5%AC%ED%98%84)
- [외부 메시지 브로커로 카프카를 이용하여 전송](https://github.com/sgdevcamp2022/plop/wiki/Kafka-%EC%82%AC%EC%9A%A9)
- [MongoDB를 이용하여 채팅, 채팅방, 유저접속상태 데이터 관리](https://github.com/sgdevcamp2022/plop/wiki/MongoDB-%EC%82%AC%EC%9A%A9)
- ec2 서버에 카프카 연동
- [SpringDoc으로 REST API 문서화](https://github.com/llsrrll96/Winterdevcamp-Plop-Messenger/wiki/API-%EB%AC%B8%EC%84%9C%ED%99%94%EB%A5%BC-%EC%9C%84%ED%95%B4-Spring-REST-Docs-%EB%8F%84%EC%9E%85)
- [성능 테스트와 그라파나로 모니터링](https://github.com/sgdevcamp2022/plop/wiki/%EC%84%B1%EB%8A%A5-%EA%B0%9C%EC%84%A0)
<br>

## 4️⃣ 아키텍처
<img src="https://user-images.githubusercontent.com/58140426/222035675-11bb537f-8feb-4ee3-a4a6-164ebdecce9e.png" width="720">

- AWS EC2에 배포하였습니다.
- EC2에 카프카 설치와 도커를 설치하여 인증 토큰을 저장하는 Redis와 모니터링 툴인 그라파나를 실행시켰습니다.
- AWS RDS 를 사용하여 MySQL을 배포하였습니다.
- MongoDB Atlas를 사용하여 클라우드에 MongoDB 배포하였습니다.

<br>

## 5️⃣ 웹소켓 통신
> 웹소켓과 STOMP 프로토콜을 서브 프로토콜로 사용, pub/sub 구조를 통해 채팅기능을 구현하였습니다.
<img src="https://user-images.githubusercontent.com/58140426/223014008-7e1c820f-e406-47e8-bc43-6c05337749ad.png" width="720">

STOMP 의 구독(sub) 엔드포인트
- 채팅 메시지 : "/chatting/topic/room/{roomId}"
- 방생성 메시지 : "/chatting/topic/new-room/{userId}"
- 사용자 접속상태 메시지 : "/presence/user-sub/{userId}"

<img src="https://user-images.githubusercontent.com/58140426/223027012-0efd6798-82a2-4bae-843a-3ccc48e6a410.png" >

<br>

## 6️⃣ kafka (Broker)

> 다수 서버일 경우 다른 서버사용자와 채팅불가하기 때문에 외부 메시지 브로커 사용 <br/>

<b>kafka의 producer/consumer을 통해 메시지 이벤트 브로커로 사용</b> <br/>
토픽을 통해 채팅, 방생성, 사용자 접속상태 메시지들을 구분 <br/>

장점: <br/>
- 느슨한 결합( Publisher는 메시지를 발신할 때 다른 서비스들에 대해 알 필요가 전혀 없음, scale-out용이)
- 메시지 버퍼링에 대해 Subscriber에서 원하는 시점에 메시지 처리 가능 <br/>

단점: <br/>
- 부하 발생, 비해서 속도가 느릴 수 있다. <br/><br/>


카프카 활용 <br/>
<img src="https://user-images.githubusercontent.com/58140426/223024472-84b1236e-bb21-44a4-9e51-9150ca87084e.png" >

Consumers 클래스로 받아 소켓으로 클라이언트에 메시지는 보낸다.
- 토픽(topics)을 통해 채팅, 방생성 메시지를 구분하여 전달

<br>

## 7️⃣ 성능 개선

### 측정환경

> nGrinder를 사용하여 부하 테스트를 진행했고 도커에 Prometheus와 Grafana를 통해 서버 상태를 모니터링하였습니다.

<img src="https://user-images.githubusercontent.com/58140426/223004735-7972ec45-6263-401c-b43c-8c94a29c9bdc.png" width="720">

<br>
> 리팩토링, 톰캣의 스레드, 히카리CP Connection pool 설정으로 TPS 을 개선하였습니다. 
<div>
<img src="https://user-images.githubusercontent.com/58140426/223026288-997d7cd0-2aac-4678-a975-8869614aa69e.png" width="480">
<img src="https://user-images.githubusercontent.com/58140426/223026044-11520eb9-8fc4-490f-918d-9521d9be8a8a.png" width="480">
</div>
TPS : 141.2 -> 399.4 

<br>
<br>

## 8️⃣ Learned
- 웹소켓과 STOMP, Kafka를 사용하여 1:1, 단체 채팅, 사용자 접속상태 구현하였습니다.
    - 웹소켓 기반의 STOMP 을 사용하여 pub/sub 구조를 통해 웹소켓 통신으로 채팅기능을 구현
    - 메시지 브로커로 Kafka를 사용하여 Producer/consumer 구조로 토픽을 분류하여 전달
        - 채팅 메시지 토픽, 방생성 정보 토픽으로 나누어 전달 
- nGrinder를 사용해 부하테스트를 해보고 Grafana로 서버 상태를 모니터링, 톰캣 설정과 리팩토링을 통해 성능 개선해 보았습니다. 
  - CPU 사용량, 힙, GC과 같은 객관적인 수치를 보고 성능 개선을 할 수 있는 개발자가 되기 위해 필요 지식들을 공부해야겠다는 생각이 들었습니다.
  - 개발 환경과 배포 환경이 다를 경우가 많기 때문에 설정값이 다르게 적용됨.
- MongoDB를 사용하면서 db.collection.find() 와 같은 쿼리 사용법을 익혔고 이를 MongoTemplate을 사용하여 MongoDB에 손쉽게 질의할 수 있습니다.
- 팀장으로서 협업 프로젝트를 주도하면서 기능 정의부터 설계, 구현, 배포, 성능 테스트까지 경험해볼 수 있었습니다.
  - 기간 내 프로젝트 완성 : 일주일에 한 번씩 메인 회의를 진행하여 팀원들의 진행도를 파악하고, 전체일정 계획하여 프로젝트 진행하였습니다.
    - 팀원들과 개발일지를 써보면서 팀과 자신의 개발상황을 체크하고 이슈를 공유
    - 슬랙을 통해 팀원들과 지속적으로 소통하여 즉각적으로 피드백하고, 필요할 경우 코어시간 중 회의를 통해 개발도중 이슈를 해결
  - 맡은 부분에 최선을 다하기 : 이번 프로젝트에 MongoDB와 웹소켓 통신을 처음 사용해보는 기술이지만 빠르게 익히고 내가 맡은 부분에 책임감을 가지고 구현하였습니다.
  - 프론트엔드와 연동 : 대부분의 기능을 구현한 뒤에 배포를 하여 프론트엔드와 연동하였습니다만, 멘토링을 통해 다음과 같이 진행해야겠다고 생각했습니다.
    - 프로젝트 개발 과정 : 개발 초기에 서버에 가데이터를 넣어 배포, 프론트엔드에서 요청에 대한 결과를 통해 서버와 미리 연동해보며 에러 대처와 화면 구성의 편의성을 제공, 이후 DB와 연동하여 정식으로 배포를 하는 방법
