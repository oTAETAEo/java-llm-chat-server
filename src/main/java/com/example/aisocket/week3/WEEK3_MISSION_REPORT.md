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
2. [Platform Thread vs Virtual Thread의 AI I/O 블로킹 효율 비교](https://taetae-o.tistory.com/16)
3. ```svg 
    Pure Java 기반 가상 스레드 & 비동기 스트리밍 AI 게이트웨이 서버
<svg xmlns="http://www.w3.org/2000/svg" width="720" height="590" viewBox="0 0 1440 1180" role="img" aria-labelledby="title desc" style="display: block; margin: 0 auto;">
  <title id="title">AI Gateway 가상 스레드 실행 워크플로우</title>
  <desc id="description">
  클라이언트 연결부터 AI SSE 응답을 사용자에게 전송하기까지의 흐름과
  연결 처리 가상 스레드, 소켓 쓰기 가상 스레드를 강조한 다이어그램
  </desc>
  <defs>
    <marker id="arrow" markerWidth="6" markerHeight="6" refX="5" refY="3" orient="auto">
      <path d="M0,0 L6,3 L0,6 Z" fill="#475569"/>
    </marker>
    <marker id="arrow-blue" markerWidth="6" markerHeight="6" refX="5" refY="3" orient="auto">
      <path d="M0,0 L6,3 L0,6 Z" fill="#2563eb"/>
    </marker>
    <marker id="arrow-orange" markerWidth="6" markerHeight="6" refX="5" refY="3" orient="auto">
      <path d="M0,0 L6,3 L0,6 Z" fill="#ea580c"/>
    </marker>
    <marker id="arrow-purple" markerWidth="6" markerHeight="6" refX="5" refY="3" orient="auto">
      <path d="M0,0 L6,3 L0,6 Z" fill="#7c3aed"/>
    </marker>
    <marker id="arrow-green" markerWidth="6" markerHeight="6" refX="5" refY="3" orient="auto">
      <path d="M0,0 L6,3 L0,6 Z" fill="#059669"/>
    </marker>
    <filter id="shadow" x="-20%" y="-20%" width="140%" height="140%">
      <feDropShadow dx="0" dy="4" stdDeviation="5" flood-color="#0f172a" flood-opacity="0.13"/>
    </filter>
    <style>
        .title { font: 700 30px sans-serif; fill: #0f172a; }
        .subtitle { font: 400 15px sans-serif; fill: #475569; }
        .section { font: 700 18px sans-serif; fill: #1e293b; }
        .box-title { font: 700 16px sans-serif; fill: #0f172a; }
        .body { font: 400 14px sans-serif; fill: #334155; }
        .small { font: 400 12px sans-serif; fill: #475569; }
        .code { font: 600 13px monospace; fill: #1e3a8a; }
        .arrow { fill: none; stroke: #475569; stroke-width: 2.8;
                 stroke-linecap: round; stroke-linejoin: round;
                 marker-end: url(#arrow); }
        .demand { fill: none; stroke: #2563eb; stroke-width: 2.5;
                  stroke-linecap: round; stroke-linejoin: round;
                  stroke-dasharray: 8 5; marker-end: url(#arrow-blue); }
        .request-arrow { fill: none; stroke: #ea580c; stroke-width: 3.4;
                         stroke-linecap: round; stroke-linejoin: round;
                         marker-end: url(#arrow-orange); }
        .response-arrow { fill: none; stroke: #7c3aed; stroke-width: 3.4;
                          stroke-linecap: round; stroke-linejoin: round;
                          marker-end: url(#arrow-purple); }
        .user-arrow { fill: none; stroke: #059669; stroke-width: 3.4;
                      stroke-linecap: round; stroke-linejoin: round;
                      marker-end: url(#arrow-green); }
        .flow-label { font: 700 12px sans-serif; }
        </style>
  </defs>
  <rect width="1450" height="1080" fill="#f8fafc"/>
  <text x="700" y="48" text-anchor="middle" class="title">
        Pure Java AI Gateway — 가상 스레드 실행 워크플로우
    </text>
  <text x="700" y="76" text-anchor="middle" class="subtitle">
        파란 점선 박스는 사용자 연결마다 생성되는 가상 스레드 실행 영역입니다.
    </text>
  <!-- Participants -->
  <rect x="50" y="110" width="210" height="82" rx="14" fill="#ffffff" stroke="#94a3b8" stroke-width="2" filter="url(#shadow)"/>
  <text x="155" y="142" text-anchor="middle" class="box-title">사용자 클라이언트</text>
  <text x="155" y="168" text-anchor="middle" class="body">HTTP POST + prompt</text>
  <rect x="1070" y="110" width="280" height="82" rx="14" fill="#ffffff" stroke="#94a3b8" stroke-width="2" filter="url(#shadow)"/>
  <text x="1210" y="142" text-anchor="middle" class="box-title">외부 AI API</text>
  <text x="1210" y="168" text-anchor="middle" class="body">HTTP/2 + SSE 스트리밍</text>
  <!-- Server accept lane -->
  <rect x="330" y="110" width="660" height="150" rx="18" fill="#ecfeff" stroke="#0891b2" stroke-width="2" filter="url(#shadow)"/>
  <text x="355" y="141" class="section">AiGatewayServer — 메인 accept 루프</text>
  <rect x="365" y="165" width="185" height="62" rx="10" fill="#ffffff" stroke="#06b6d4" stroke-width="1.5"/>
  <text x="457" y="190" text-anchor="middle" class="code">serverSocket.accept()</text>
  <text x="457" y="211" text-anchor="middle" class="small">사용자 Socket 생성</text>
  <rect x="590" y="165" width="175" height="62" rx="10" fill="#ffffff" stroke="#06b6d4" stroke-width="1.5"/>
  <text x="677" y="190" text-anchor="middle" class="code">capacity.tryAcquire()</text>
  <text x="677" y="211" text-anchor="middle" class="small">최대 2,000 요청 제한</text>
  <rect x="805" y="165" width="150" height="62" rx="10" fill="#dbeafe" stroke="#2563eb" stroke-width="2"/>
  <text x="880" y="190" text-anchor="middle" class="code">executor.submit()</text>
  <text x="880" y="211" text-anchor="middle" class="small">가상 스레드 생성</text>
  <path d="M260 151 H330" class="arrow"/>
  <path d="M550 196 H590" class="arrow"/>
  <path d="M765 196 H805" class="arrow"/>
  <!-- Virtual thread 1 -->
  <rect x="165" y="305" width="1070" height="310" rx="22" fill="#eff6ff" stroke="#2563eb" stroke-width="3" stroke-dasharray="10 6" filter="url(#shadow)"/>
  <rect x="188" y="286" width="410" height="40" rx="10" fill="#2563eb"/>
  <text x="393" y="312" text-anchor="middle" style="font:700 17px sans-serif; fill:#ffffff">
        가상 스레드 ① — 사용자 연결 처리
    </text>
  <text x="1198" y="330" text-anchor="end" class="small">
        AsyncAiGatewayHandler.run()
    </text>
  <rect x="205" y="355" width="180" height="82" rx="12" fill="#ffffff" stroke="#60a5fa" stroke-width="1.5"/>
  <text x="295" y="382" text-anchor="middle" class="box-title">HTTP 요청 읽기</text>
  <text x="295" y="405" text-anchor="middle" class="code">HttpRequestData.read()</text>
  <text x="295" y="424" text-anchor="middle" class="small">헤더·본문 크기 제한</text>
  <rect x="430" y="355" width="170" height="82" rx="12" fill="#ffffff" stroke="#60a5fa" stroke-width="1.5"/>
  <text x="515" y="382" text-anchor="middle" class="box-title">prompt 추출</text>
  <text x="515" y="405" text-anchor="middle" class="code">JsonSupport</text>
  <text x="515" y="424" text-anchor="middle" class="small">POST·JSON 검증</text>
  <rect x="645" y="355" width="220" height="82" rx="12" fill="#ffffff" stroke="#60a5fa" stroke-width="1.5"/>
  <text x="755" y="382" text-anchor="middle" class="box-title">AI 요청 시작</text>
  <text x="755" y="405" text-anchor="middle" class="code">sendAsync(...).join()</text>
  <text x="755" y="424" text-anchor="middle" class="small">상태·헤더·Publisher 준비</text>
  <rect x="910" y="355" width="270" height="82" rx="12" fill="#ffffff" stroke="#60a5fa" stroke-width="1.5"/>
  <text x="1045" y="382" text-anchor="middle" class="box-title">SSE 구독 시작</text>
  <text x="1045" y="405" text-anchor="middle" class="code">body().subscribe(subscriber)</text>
  <text x="1045" y="424" text-anchor="middle" class="small">Publisher ↔ Subscriber 연결</text>
  <path d="M385 396 H430" class="arrow"/>
  <path d="M600 396 H645" class="arrow"/>
  <path d="M865 396 H910" class="arrow"/>
  <path d="M865 375 C960 285, 1080 250, 1198 192" class="request-arrow"/>
  <rect x="968" y="252" width="126" height="28" rx="14" fill="#fff7ed" stroke="#ea580c"/>
  <text x="1031" y="271" text-anchor="middle" class="flow-label" fill="#c2410c">① AI 요청 전송</text>
  <rect x="300" y="485" width="800" height="92" rx="14" fill="#dbeafe" stroke="#3b82f6" stroke-width="1.5"/>
  <text x="700" y="515" text-anchor="middle" class="box-title">
        연결 유지 및 완료 대기
    </text>
  <text x="700" y="541" text-anchor="middle" class="code">
        subscriber.completion().join()
    </text>
  <text x="700" y="562" text-anchor="middle" class="small">
        비동기 SSE 스트림이 끝날 때까지 사용자 Socket을 닫지 않음
    </text>
  <path d="M1045 437 C1045 465, 985 481, 935 485" class="arrow"/>
  <!-- Publisher callback lane -->
  <rect x="95" y="660" width="650" height="286" rx="18" fill="#ffffff" stroke="#7c3aed" stroke-width="2" filter="url(#shadow)"/>
  <text x="120" y="693" class="section">HttpClient Publisher 콜백</text>
  <rect x="120" y="735" width="135" height="72" rx="11" fill="#f5f3ff" stroke="#8b5cf6" stroke-width="1.5"/>
  <text x="187" y="762" text-anchor="middle" class="code">onSubscribe()</text>
  <text x="187" y="785" text-anchor="middle" class="code">request(1)</text>
  <rect x="275" y="735" width="135" height="72" rx="11" fill="#f5f3ff" stroke="#8b5cf6" stroke-width="1.5"/>
  <text x="342" y="762" text-anchor="middle" class="code">onNext()</text>
  <text x="342" y="785" text-anchor="middle" class="small">List<ByteBuffer></text>
  <rect x="430" y="735" width="135" height="72" rx="11" fill="#f5f3ff" stroke="#8b5cf6" stroke-width="1.5"/>
  <text x="497" y="762" text-anchor="middle" class="box-title">SSE 증분 파싱</text>
  <text x="497" y="785" text-anchor="middle" class="code">parser.accept()</text>
  <rect x="585" y="735" width="135" height="72" rx="11" fill="#f5f3ff" stroke="#8b5cf6" stroke-width="1.5"/>
  <text x="652" y="762" text-anchor="middle" class="box-title">③ 제한 큐 등록</text>
  <text x="652" y="785" text-anchor="middle" class="code">enqueue()</text>
  <path d="M255 771 H275" class="arrow"/>
  <path d="M410 771 H430" class="arrow"/>
  <path d="M565 771 H585" class="arrow"/>
  <!-- Virtual thread 2 -->
  <rect x="795" y="660" width="530" height="286" rx="22" fill="#eff6ff" stroke="#2563eb" stroke-width="3" stroke-dasharray="10 6" filter="url(#shadow)"/>
  <rect x="818" y="641" width="410" height="40" rx="10" fill="#2563eb"/>
  <text x="1023" y="667" text-anchor="middle" style="font:700 17px sans-serif; fill:#ffffff">
        가상 스레드 ② — 사용자 소켓 쓰기
    </text>
  <text x="1290" y="696" text-anchor="end" class="small">
        AiStreamSubscriber.writeLoop()
    </text>
  <rect x="840" y="722" width="190" height="70" rx="11" fill="#ffffff" stroke="#60a5fa" stroke-width="1.5"/>
  <text x="935" y="748" text-anchor="middle" class="code">signals.take()</text>
  <text x="935" y="772" text-anchor="middle" class="small">DataSignal 대기</text>
  <rect x="1075" y="722" width="200" height="70" rx="11" fill="#ffffff" stroke="#60a5fa" stroke-width="1.5"/>
  <text x="1175" y="748" text-anchor="middle" class="code">userOut.write()</text>
  <text x="1175" y="772" text-anchor="middle" class="code">flush()</text>
  <rect x="920" y="832" width="280" height="72" rx="11" fill="#dbeafe" stroke="#2563eb" stroke-width="2"/>
  <text x="1060" y="858" text-anchor="middle" class="box-title">전송 완료 후 다음 수요</text>
  <text x="1060" y="882" text-anchor="middle" class="code">subscription.request(1)</text>
  <path d="M720 771 H840" class="arrow"/>
  <path d="M1030 757 H1075" class="arrow"/>
  <path d="M1175 792 V812 H1060 V832" class="arrow"/>
  <path d="M920 868 H700 700 H75 V771 H120" class="demand"/>
  <rect x="600" y="855" width="180" height="28" rx="14" fill="#eff6ff" stroke="#2563eb"/>
  <text x="690" y="874" text-anchor="middle" class="flow-label" fill="#1d4ed8">⑤ 다음 묶음 요청</text>
  <!-- Response path -->
  <path d="M1350 151 H1390 V635 H342 V735" class="response-arrow"/>
  <text x="1410" y="430" transform="rotate(-90 1410 430)" text-anchor="middle" class="flow-label" fill="#6d28d9">
        ② AI SSE 응답 ByteBuffer
    </text>
  <path d="M1175 904 V982 H50 V250 H155 V192" class="user-arrow"/>
  <rect x="424" y="966" width="420" height="32" rx="16" fill="#ecfdf5" stroke="#059669"/>
  <text x="634" y="987" text-anchor="middle" class="flow-label" fill="#047857">
        ④ 파싱된 SSE를 사용자 Socket OutputStream으로 전달
    </text>
  <!-- Legend -->
  <rect x="50" y="1031" width="18" height="18" rx="3" fill="#eff6ff" stroke="#2563eb" stroke-width="2" stroke-dasharray="4 2"/>
  <text x="78" y="1045" class="small">가상 스레드 실행 영역</text>
  <line x1="230" y1="1040" x2="278" y2="1040" class="demand"/>
  <text x="290" y="1045" class="small">백프레셔 수요(request(1))</text>
  <line x1="492" y1="1040" x2="540" y2="1040" class="request-arrow"/>
  <text x="552" y="1045" class="small">외부 AI 요청</text>
  <line x1="660" y1="1040" x2="708" y2="1040" class="response-arrow"/>
  <text x="720" y="1045" class="small">AI SSE 응답</text>
  <line x1="830" y1="1040" x2="878" y2="1040" class="user-arrow"/>
  <text x="890" y="1045" class="small">사용자 응답</text>
</svg>

```