package org.chunsik.pq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PqApplication {

    public static void main(String[] args) {
        SpringApplication.run(PqApplication.class, args);
    }

}
