package org.chunsik.pq.config;

import io.sentry.spring.jakarta.EnableSentry;
import org.springframework.context.annotation.Configuration;

@EnableSentry(dsn = "https://e1e519784884ce37a377d9f22f5b0c42@o4507807934316544.ingest.us.sentry.io/4507807936872448")
@Configuration
public class SentryConfiguration {
}
