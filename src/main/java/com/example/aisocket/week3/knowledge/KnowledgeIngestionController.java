package com.example.aisocket.week3.knowledge;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/knowledge")
public class KnowledgeIngestionController {

    private final KnowledgeIngestionService ingestionService;

    public KnowledgeIngestionController(KnowledgeIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @PostMapping("/inject")
    public ResponseEntity<Map<String, String>> inject(@RequestBody KnowledgePayload payload) {

        ingestionService.injectKnowledge(payload);

        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "category", payload.category(),
                "message", "성공적으로 임베딩되어 벡터 DB에 저장되었습니다."
        ));
    }
}