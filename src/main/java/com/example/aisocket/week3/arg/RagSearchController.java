package com.example.aisocket.week3.arg;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/rag")
public class RagSearchController {

    private final RagSearchService ragSearchService;

    public RagSearchController(RagSearchService ragSearchService) {
        this.ragSearchService = ragSearchService;
    }

    @PostMapping("/retrieve")
    public ResponseEntity<Map<String, Object>> retrieve(@RequestBody QueryPayload payload) {

        List<String> top5Documents = ragSearchService.retrieveTop5FromDb(payload);

        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "count", top5Documents.size(),
                "documents", top5Documents
        ));
    }
}