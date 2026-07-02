package com.example.aisocket.week3.arg;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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
    public ResponseEntity<Map<String, Object>> dbRetrieve(@RequestBody QueryPayload payload) {

        List<String> top5Documents = ragSearchService.retrieveTop5FromDb(payload);

        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "count", top5Documents.size(),
                "documents", top5Documents
        ));
    }

    /**
     * memory를 통해 Top5 가져오는 방식.
     */
    @PostMapping("/memory/retrieve")
    public ResponseEntity<Map<String, Object>> memoryRetrieve(@RequestBody QueryPayload payload) {

        List<String> top5Documents = ragSearchService.retrieveTop5FromMemory(payload);

        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "count", top5Documents.size(),
                "documents", top5Documents
        ));
    }



}