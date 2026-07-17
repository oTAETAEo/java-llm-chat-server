package com.example.aisocket;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:aisocket-test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.sql.init.mode=never",
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"spring.ai.openai.api-key=test-api-key",
		"app.vector-store.initialize-schema=false"
})
class AisocketApplicationTests {

	@Test
	void contextLoads() {
	}

}
