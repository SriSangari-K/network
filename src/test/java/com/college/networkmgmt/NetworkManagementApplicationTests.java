package com.college.networkmgmt;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("h2")
class NetworkManagementApplicationTests {

    @Test
    void contextLoads() {
        // Verifies complete Spring Boot context loading, JPA entity mapping, and repository bean creation
    }
}
