## 2주차 정리

### 2주차 - 대규모 데이터 파이프라인 & 병렬 처리 최적화

- **목표:** AI 컨텍스트 유지를 위한 대규모 대화 이력(Memory) 파이프라인을 구축하고, 멀티스레드 환경에서 메시지 브로드캐스트와 큐(Queue)를 이용한 동시성 병목을 제어합니다
- **선행학습**
    - TCP/IP 3-Way Handshake
    - 프로세스(Process) vs 스레드(Thread)
    - 임계 영역(Critical Section)
- **주요 학습 개념**
    - 자바 멀티스레딩, `ExecutorService` 스레드 풀 튜닝, `ConcurrentHashMap`과 `CAS 연산 기반 Lock-Free 동시성 제어`.
    - 비동기 데이터 흐름 생성을 위한 Java Stream 및 `CompletableFuture`, `프로듀서-컨슈머 패턴`(Producer-Consumer).
    - AI 대화 이력(Context) 저장을 위한 대용량 `파일 I/O 및 버퍼링 시스템` & `시스템 콜` 최적화
- **실습**
    - **[미션: 코딩테스트] 고동시성 환경에서의 AI 요청 비동기 스케줄링 큐**
        - **조건:** 외부 AI API의 Rate Limit(분당 요청 제한)이 걸려있는 상황. 멀티스레드 환경에서 쏟아지는 유저의 AI 질의 요청을 순서와 가중치에 따라 데드락 없이 안전하게 큐잉하고 소모시키는 지수 백오프(Exponential Backoff) 기반 스케줄러 알고리즘 작성.
    - **[미션 1: 로레벨 검증] Thread-Per-Connection 소켓 폭발 및 락 병목 스레드 덤프 분석**
        - **조건:** 멀티스레드 소켓 채팅 서버에 **k6**를 이용해 동시 연결 **3,000**개를 인입시켜 스레드 생성 폭발로 인한 OOM 상황을 재현하고, AI 응답 대기 시간 동안 동기화(`synchronized`) 락에 묶여 병목이 발생한 스레드 상태를 `jstack` 덤프로 추출해 원인 격리하기.
    - **[미션 2: 실무 구현] 메시지 큐(Queue) 기반 대규모 AI 채팅 이력 파이프라인 완성**
        - **조건:** 유저별 채팅 세션을 스레드 안전하게 관리하고, AI 응답 데이터를 유저에게 브로드캐스트하는 동시에, 시스템 Call을 최소화하는 고성능 `BufferedOutputStream` 청크 단위를 활용하여 무손실로 로컬 디스크 로그 파일에 실시간 플러시(Flush)하는 파이프라인 구축

---

### 정리 문서
1. [고동시성 환경에서의 AI 요청 비동기 스케줄링 큐](https://taetae-o.tistory.com/13)
2. [Thread-Per-Connection 소켓 폭발 및 락 병목 스레드 덤프 분석](https://taetae-o.tistory.com/14)
3. ```svg
   메시지 큐(Queue) 기반 대규모 AI 채팅 이력 파이프라인 완성
<svg xmlns="http://www.w3.org/2000/svg" width="720" height="590" viewBox="0 0 1440 1180" role="img" aria-labelledby="title desc" style="display: block; margin: 0 auto;">
  <title id="title">Week2 HTTP 및 TCP 소켓 워크플로</title>
  <desc id="desc">HTTP와 TCP 소켓의 공통 AI 처리 과정을 별도 박스로 보여주고, TCP에만 존재하는 세션 및 로그 저장 흐름을 표시한 의존 관계도</desc>
  <defs>
    <style>
      .bg { fill: #f8fafc; }
      .http-panel { fill: #f0fdf4; stroke: #16a34a; stroke-width: 3; }
      .tcp-panel { fill: #fff7ed; stroke: #ea580c; stroke-width: 3; }
      .http-box { fill: #ffffff; stroke: #22c55e; stroke-width: 2; }
      .tcp-box { fill: #ffffff; stroke: #f97316; stroke-width: 2; }
      .common-box { fill: #eff6ff; stroke: #2563eb; stroke-width: 2; }
      .domain-box { fill: #faf5ff; stroke: #9333ea; stroke-width: 2; }
      .storage-box { fill: #fefce8; stroke: #ca8a04; stroke-width: 2; }
      .client-box { fill: #0f172a; stroke: #0f172a; stroke-width: 2; }
      .ai-box { fill: #eef2ff; stroke: #4f46e5; stroke-width: 3; }
      .note-box { fill: #ffffff; stroke: #94a3b8; stroke-width: 1.5; stroke-dasharray: 6 4; }
      .title { font: 700 30px -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif; fill: #0f172a; }
      .subtitle { font: 500 15px -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif; fill: #475569; }
      .panel-title { font: 700 21px -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif; fill: #0f172a; }
      .label { font: 700 16px -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif; fill: #0f172a; }
      .small { font: 500 13px -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif; fill: #334155; }
      .tiny { font: 500 12px -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif; fill: #475569; }
      .white-label { font: 700 17px -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif; fill: #ffffff; }
      .white-small { font: 500 13px -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif; fill: #cbd5e1; }
      .step { font: 700 12px -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif; fill: #ffffff; }
      .arrow { fill: none; stroke: #334155; stroke-width: 2.2; marker-end: url(#arrow); }
      .http-arrow { fill: none; stroke: #16a34a; stroke-width: 2.5; marker-end: url(#arrow-green); }
      .tcp-arrow { fill: none; stroke: #ea580c; stroke-width: 2.5; marker-end: url(#arrow-orange); }
      .branch-arrow { fill: none; stroke: #ca8a04; stroke-width: 2.2; marker-end: url(#arrow-yellow); }
      .dash-arrow { fill: none; stroke: #64748b; stroke-width: 2; stroke-dasharray: 7 5; marker-end: url(#arrow-gray); }
    </style>
    <marker id="arrow" markerWidth="10" markerHeight="10" refX="9" refY="3" orient="auto">
      <path d="M0,0 L0,6 L9,3 z" fill="#334155"/>
    </marker>
    <marker id="arrow-green" markerWidth="10" markerHeight="10" refX="9" refY="3" orient="auto">
      <path d="M0,0 L0,6 L9,3 z" fill="#16a34a"/>
    </marker>
    <marker id="arrow-orange" markerWidth="10" markerHeight="10" refX="9" refY="3" orient="auto">
      <path d="M0,0 L0,6 L9,3 z" fill="#ea580c"/>
    </marker>
    <marker id="arrow-yellow" markerWidth="10" markerHeight="10" refX="9" refY="3" orient="auto">
      <path d="M0,0 L0,6 L9,3 z" fill="#ca8a04"/>
    </marker>
    <marker id="arrow-gray" markerWidth="10" markerHeight="10" refX="9" refY="3" orient="auto">
      <path d="M0,0 L0,6 L9,3 z" fill="#64748b"/>
    </marker>
  </defs>

  <rect class="bg" width="1440" height="1180"/>
  <text x="720" y="45" text-anchor="middle" class="title">week2 — HTTP / TCP 소켓 워크플로 비교</text>
  <text x="720" y="72" text-anchor="middle" class="subtitle">공통 AI 처리 흐름은 각 경로 안에서 독립적으로 표시하고, 최종 AI 클라이언트 구현만 통합</text>

  <!-- Calling clients -->
  <rect x="100" y="100" width="550" height="82" rx="15" class="client-box"/>
  <text x="375" y="132" text-anchor="middle" class="white-label">① HTTP / REST 클라이언트</text>
  <text x="375" y="157" text-anchor="middle" class="white-small">POST /api/v2/ai/ask · Spring MVC / Tomcat</text>

  <rect x="790" y="100" width="550" height="82" rx="15" class="client-box"/>
  <text x="1065" y="132" text-anchor="middle" class="white-label">② TCP 소켓 클라이언트</text>
  <text x="1065" y="157" text-anchor="middle" class="white-small">Socket 연결 · 한 줄 메시지 · port 8080</text>

  <!-- HTTP workflow panel -->
  <rect x="45" y="215" width="655" height="730" rx="20" class="http-panel"/>
  <text x="372" y="251" text-anchor="middle" class="panel-title">HTTP 워크플로 — Spring 관리</text>
  <text x="372" y="274" text-anchor="middle" class="tiny">AisocketApplication의 컴포넌트 스캔과 생성자 주입</text>

  <circle cx="90" cy="327" r="17" fill="#16a34a"/>
  <text x="90" y="332" text-anchor="middle" class="step">1</text>
  <rect x="120" y="295" width="520" height="72" rx="12" class="http-box"/>
  <text x="380" y="323" text-anchor="middle" class="label">AIController</text>
  <text x="380" y="347" text-anchor="middle" class="small">@RestController · DeferredResult&lt;String&gt;(30초)</text>

  <circle cx="90" cy="442" r="17" fill="#16a34a"/>
  <text x="90" y="447" text-anchor="middle" class="step">2</text>
  <rect x="120" y="405" width="520" height="82" rx="12" class="domain-box"/>
  <text x="380" y="434" text-anchor="middle" class="label">AIRequestTask 생성</text>
  <text x="380" y="458" text-anchor="middle" class="small">prompt · userGrade · UUID roomId · CompletableFuture</text>
  <text x="380" y="478" text-anchor="middle" class="tiny">HTTP 응답과 백그라운드 처리를 연결하는 작업 객체</text>

  <circle cx="90" cy="566" r="17" fill="#16a34a"/>
  <text x="90" y="571" text-anchor="middle" class="step">3</text>
  <rect x="120" y="525" width="520" height="96" rx="12" class="common-box"/>
  <text x="380" y="555" text-anchor="middle" class="label">AIScheduler.enqueueTask()</text>
  <text x="380" y="579" text-anchor="middle" class="small">PriorityBlockingQueue · 소비 스레드 3개</text>
  <text x="380" y="601" text-anchor="middle" class="tiny">등급 우선순위 + FIFO · 최대 5회 지수 백오프 + 지터</text>

  <circle cx="90" cy="689" r="17" fill="#16a34a"/>
  <text x="90" y="694" text-anchor="middle" class="step">4</text>
  <rect x="120" y="655" width="520" height="76" rx="12" class="common-box"/>
  <text x="380" y="684" text-anchor="middle" class="label">AiClient.generateResponse(prompt)</text>
  <text x="380" y="708" text-anchor="middle" class="small">공통 아웃바운드 포트 호출</text>

  <circle cx="90" cy="812" r="17" fill="#16a34a"/>
  <text x="90" y="817" text-anchor="middle" class="step">5</text>
  <rect x="120" y="770" width="520" height="90" rx="12" class="http-box"/>
  <text x="380" y="800" text-anchor="middle" class="label">HTTP 비동기 응답 반환</text>
  <text x="380" y="824" text-anchor="middle" class="small">CompletableFuture → DeferredResult.setResult()</text>
  <text x="380" y="846" text-anchor="middle" class="tiny">세션 저장 없음 · 파일 로그 저장 없음</text>

  <path d="M375 182 L375 295" class="http-arrow"/>
  <path d="M380 367 L380 405" class="http-arrow"/>
  <path d="M380 487 L380 525" class="http-arrow"/>
  <path d="M380 621 L380 655" class="http-arrow"/>
  <path d="M330 731 C250 750 250 770 315 770" class="dash-arrow"/>

  <rect x="120" y="882" width="520" height="42" rx="10" class="note-box"/>
  <text x="380" y="908" text-anchor="middle" class="small">핵심: 요청 → 우선순위 큐 → AI 응답만 처리</text>

  <!-- TCP workflow panel -->
  <rect x="740" y="215" width="655" height="730" rx="20" class="tcp-panel"/>
  <text x="1067" y="251" text-anchor="middle" class="panel-title">TCP 소켓 워크플로 — Java-only 직접 조립</text>
  <text x="1067" y="274" text-anchor="middle" class="tiny">Week2Application.main()이 모든 구현체와 자원 수명주기를 직접 구성</text>

  <circle cx="785" cy="327" r="17" fill="#ea580c"/>
  <text x="785" y="332" text-anchor="middle" class="step">1</text>
  <rect x="815" y="295" width="520" height="72" rx="12" class="tcp-box"/>
  <text x="1075" y="323" text-anchor="middle" class="label">ChatServerSocket</text>
  <text x="1075" y="347" text-anchor="middle" class="small">accept() → Virtual Thread → UserSession 생성</text>

  <circle cx="785" cy="442" r="17" fill="#ea580c"/>
  <text x="785" y="447" text-anchor="middle" class="step">2</text>
  <rect x="815" y="405" width="300" height="82" rx="12" class="domain-box"/>
  <text x="965" y="434" text-anchor="middle" class="label">AIRequestTask 생성</text>
  <text x="965" y="458" text-anchor="middle" class="small">message · HIGH · roomId</text>
  <text x="965" y="478" text-anchor="middle" class="tiny">CompletableFuture 포함</text>

  <rect x="1140" y="397" width="225" height="98" rx="12" class="storage-box"/>
  <text x="1252" y="426" text-anchor="middle" class="label">세션 저장</text>
  <text x="1252" y="450" text-anchor="middle" class="small">SessionRepository</text>
  <text x="1252" y="473" text-anchor="middle" class="tiny">MemorySessionRepository</text>

  <circle cx="785" cy="566" r="17" fill="#ea580c"/>
  <text x="785" y="571" text-anchor="middle" class="step">3</text>
  <rect x="815" y="525" width="520" height="96" rx="12" class="common-box"/>
  <text x="1075" y="555" text-anchor="middle" class="label">AIScheduler.enqueueTask()</text>
  <text x="1075" y="579" text-anchor="middle" class="small">PriorityBlockingQueue · 소비 스레드 3개</text>
  <text x="1075" y="601" text-anchor="middle" class="tiny">등급 우선순위 + FIFO · 최대 5회 지수 백오프 + 지터</text>

  <circle cx="785" cy="689" r="17" fill="#ea580c"/>
  <text x="785" y="694" text-anchor="middle" class="step">4</text>
  <rect x="815" y="655" width="520" height="76" rx="12" class="common-box"/>
  <text x="1075" y="684" text-anchor="middle" class="label">AiClient.generateResponse(prompt)</text>
  <text x="1075" y="708" text-anchor="middle" class="small">공통 아웃바운드 포트 호출</text>

  <circle cx="785" cy="812" r="17" fill="#ea580c"/>
  <text x="785" y="817" text-anchor="middle" class="step">5</text>
  <rect x="815" y="770" width="300" height="90" rx="12" class="tcp-box"/>
  <text x="965" y="800" text-anchor="middle" class="label">소켓 응답 전송</text>
  <text x="965" y="824" text-anchor="middle" class="small">roomId 세션 조회</text>
  <text x="965" y="846" text-anchor="middle" class="tiny">AI 응답 브로드캐스트</text>

  <rect x="1140" y="762" width="225" height="106" rx="12" class="storage-box"/>
  <text x="1252" y="791" text-anchor="middle" class="label">비동기 로그 저장</text>
  <text x="1252" y="815" text-anchor="middle" class="small">FileLogPipeline</text>
  <text x="1252" y="838" text-anchor="middle" class="tiny">DiskLogRepository</text>
  <text x="1252" y="857" text-anchor="middle" class="tiny">chat_async_history.log</text>

  <path d="M1065 182 L1065 295" class="tcp-arrow"/>
  <path d="M1075 367 L1000 405" class="tcp-arrow"/>
  <path d="M1115 331 C1240 335 1252 360 1252 397" class="branch-arrow"/>
  <path d="M965 487 L965 525" class="tcp-arrow"/>
  <path d="M1075 621 L1075 655" class="tcp-arrow"/>
  <path d="M1020 731 C930 750 930 770 950 770" class="dash-arrow"/>
  <path d="M1115 815 L1140 815" class="branch-arrow"/>

  <rect x="815" y="882" width="520" height="42" rx="10" class="note-box"/>
  <text x="1075" y="908" text-anchor="middle" class="small">핵심: 공통 AI 처리 + 세션 관리 + 입장/메시지/AI/퇴장 로그</text>

  <!-- Unified AI client -->
  <path d="M380 731 C380 970 610 965 665 1000" class="http-arrow"/>
  <path d="M1075 731 C1075 970 830 965 775 1000" class="tcp-arrow"/>

  <rect x="530" y="990" width="380" height="72" rx="14" class="ai-box"/>
  <text x="720" y="1019" text-anchor="middle" class="label">AiClient 〈통합 아웃바운드 포트〉</text>
  <text x="720" y="1043" text-anchor="middle" class="small">두 워크플로가 동일한 인터페이스에 의존</text>

  <path d="M720 1062 L720 1090" class="arrow"/>
  <rect x="530" y="1090" width="380" height="70" rx="14" class="ai-box"/>
  <text x="720" y="1119" text-anchor="middle" class="label">OpenAIClient 〈공통 구현체〉</text>
  <text x="720" y="1143" text-anchor="middle" class="small">@Component · 현재 Thread.sleep(10초) 모의 응답</text>

  <!-- Difference callout -->
  <rect x="945" y="990" width="420" height="132" rx="14" class="storage-box"/>
  <text x="1155" y="1021" text-anchor="middle" class="label">두 경로의 실질적 차이</text>
  <text x="975" y="1050" class="small">HTTP</text>
  <text x="1030" y="1050" class="tiny">요청/AI 응답만 처리</text>
  <text x="975" y="1078" class="small">TCP</text>
  <text x="1030" y="1078" class="tiny">연결 세션 저장 + 파일 로그 파이프라인</text>
  <text x="975" y="1106" class="small">조립</text>
  <text x="1030" y="1106" class="tiny">Spring DI vs Week2Application 직접 new</text>
</svg>

```