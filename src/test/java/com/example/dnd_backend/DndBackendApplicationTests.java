package com.example.dnd_backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(classes = DndBackendApplication.class)
class DndBackendApplicationTests {

    @Test
    void contextLoads() {
        // test most basic Spring functionality
    }

}
