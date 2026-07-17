package com.example.aisocket.project;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create",
        "spring.ai.openai.api-key=test-api-key"
})
public abstract class SpringBootIntegrationTestSupport extends ProjectIntegrationTestSupport {
}
