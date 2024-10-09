package org.chunsik.pq.common.aspect;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Aspect
@Component
public class ExecutionTimeAspect {

    private static final Logger logger = LoggerFactory.getLogger(ExecutionTimeAspect.class);
    private final MeterRegistry meterRegistry;

    // 서비스 패키지에 적용 (포인트컷)
    @Around("execution(* org.chunsik.pq..service..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();

        // 실행 시간 측정 시작 (수동 측정)
        long startTime = System.currentTimeMillis();

        // Micrometer 타이머로 실행 시간을 측정
        Timer.Sample sample = Timer.start(meterRegistry);

        // 실제 메서드 실행
        Object proceed = joinPoint.proceed();

        // 실행 시간 측정 종료 (Micrometer 타이머로 기록)
        sample.stop(Timer.builder("method.execution.time")
                .description("Execution time of service methods")
                .tags("method", methodName)
                .register(meterRegistry));

        // 수동으로 계산한 실행 시간
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        // 메서드 이름과 실행 시간을 로그로 출력
        logger.info("{} executed in {} ms", joinPoint.getSignature(), executionTime);

        return proceed;
    }
}
