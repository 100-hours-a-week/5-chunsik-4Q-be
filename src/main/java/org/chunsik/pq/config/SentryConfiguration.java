package org.chunsik.pq.config;

import io.sentry.spring.jakarta.EnableSentry;
import org.springframework.context.annotation.Configuration;

@EnableSentry(dsn = "https://1686fe03e1a3d5cac128f8a60f52bb83@o4507841787265024.ingest.us.sentry.io/4507841811906560")
@Configuration
public class SentryConfiguration {
}
