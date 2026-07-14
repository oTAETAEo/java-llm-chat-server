package com.example.aisocket.week3.arg;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/rag")
@RequiredArgsConstructor
public class RagSearchController {

    private final RagSearchService ragSearchService;

    /**
     * DB를 통해 Top5 가져오는 방식.
     */
    @PostMapping("/db/retrieve")
    public Mono<ResponseEntity<Map<String, Object>>> dbRetrieve(@RequestBody QueryPayload payload) {
        return ragSearchService.retrieveTop5FromDb(payload)
                .map(top5Documents -> ResponseEntity.ok(Map.of(
                        "status", "SUCCESS",
                        "count", top5Documents.size(),
                        "documents", top5Documents
                )));
    }

    /**
     * memory에 올라와있는 백터 데이터를 통해 Top5 가져오는 방식.
     */
    @PostMapping("/memory/retrieve")
    public Mono<ResponseEntity<Map<String, Object>>> memoryRetrieve(@RequestBody QueryPayload payload) {
        return ragSearchService.retrieveTop5FromMemory(payload)
                .map(top5Documents -> ResponseEntity.ok(Map.of(
                        "status", "SUCCESS",
                        "count", top5Documents.size(),
                        "documents", top5Documents
                )));
    }
}
