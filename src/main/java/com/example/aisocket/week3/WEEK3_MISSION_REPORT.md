## 3주차 정리

### **3주차 - 차세대 고성능 AI 서빙 (Virtual Thread, SSE 스트리밍 & k6 부하 테스트)**

**목표:** Java 21 가상 스레드와 Spring AI를 결합하여 외부 AI API 호출 시 발생하는 대규모 I/O 블로킹을 무력화하고, 토큰 스트리밍과 벡터 DB 연동을 통해 시스템 최종 임계점까지 최적화합니다.

- 선행 학습
    - 스레드 풀(Thread Pool / ExecutorService)
    - HTTP/1.1 vs HTTP/2 차이
    - 자바 11 이상 버전에 내장된 `java.net.http.HttpClient`
- **주요 학습 개념**
    - Java 21 **가상 스레드(Virtual Thread)** 아키텍처와 **캐리어 스레드 고갈(Pinning)** 주의점.
    - AI 응답의 사용자 경험을 극대화하는 **SSE(Server-Sent Events) 및 소켓 기반 토큰 스트리밍(Streaming)** 기법.
    - 벡터 데이터베이스(RAG 구축을 위한 Pgvector 등) 연동 시 **DBCP(HikariCP) 커넥션** **최적화** 및 모던 GC(G1/ZGC) 튜닝.
- **실습**
    - **[미션: 코딩테스트] RAG(검색증강생성)를 위한 코사인 유사도(Cosine Similarity) 고속 연산**
        - **조건:** 수만 개의 AI 지식 벡터 데이터가 메모리에 로드되어 있을 때, 유저의 질문 벡터와 가장 유사한 데이터 Top 5를 추출하는 고속 벡터 연산 알고리즘 구현 (반복문 언롤링 및 프리미티브 배열 최적화를 통해 연산 속도 극대화).
    - **[미션 1: 로레벨 검증] Platform Thread vs Virtual Thread의 AI I/O 블로킹 효율 비교**
        - **조건:** 외부 AI API의 지연 시간(평균 1~2초 응답 대기)을 모킹한 상태에서 기존 스레드 풀 방식과 가상 스레드 방식을 비교할 것. **k6 부하 테스트** 툴을 사용하여 가상 사용자(VU)를 1만 개까지 올렸을 때, 컨텍스트 스위칭 횟수와 메모리 소모량이 어떻게 급감하는지 지표 검증 보고서 작성.
    - **[미션 2: 실무 구현] Pure Java 기반 가상 스레드 & 비동기 스트리밍 AI 게이트웨이 서버**
        - **조건:** Spring AI 유틸리티를 사용하지 않고, 순수 자바의 **`HttpClient` (Java 11+)**, **가상 스레드(Virtual Thread)**, 그리고 소켓(NIO/Stream)만을 조합하여 수천 명의 동시 AI 질의를 처리하는 프록시 게이트웨이 서버를 구축할 것. 외부 AI API가 글자 단위로 쪼개어 보내주는 Server-Sent Events(SSE) 청크를 자바 단에서 논블로킹 스트림으로 파싱하여 유저 소켓에 실시간 백프레셔(Backpressure)를 고려해 밀어주는 아키텍처 완성.

---

### 정리 문서
1. [RAG(검색증강생성)를 위한 코사인 유사도(Cosine Similarity) 고속 연산](https://taetae-o.tistory.com/15)
