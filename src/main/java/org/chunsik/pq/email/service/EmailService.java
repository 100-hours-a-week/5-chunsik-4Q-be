package org.chunsik.pq.email.service;

import org.springframework.beans.factory.annotation.Value;
import org.chunsik.pq.email.model.EmailConfirm;
import org.chunsik.pq.email.repository.EmailConfirmRepository;
import org.chunsik.pq.email.util.AESUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Optional;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

@Service
public class EmailService {

    @Value("${auth-code-expiration-millis}")
    private long authCodeExpirationMillis;

    private static final int MAX_REQUESTS = 5;
    private static final int COOKIE_EXPIRATION_MINUTES = 30;

    private final RandomGenerator randomGenerator = RandomGeneratorFactory.of("Random").create();

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailConfirmRepository emailConfirmRepository;

    public void sendSimpleEmail(String toEmail, HttpServletRequest request, HttpServletResponse response) {
        // 쿠키 기반 재전송 제한 체크
        Optional<Cookie> requestCountCookie = getCookie(request, "requestCount");
        Optional<Cookie> lastRequestTimeCookie = getCookie(request, "lastRequestTime");

        int requestCount = 1;
        long currentTime = Instant.now().toEpochMilli();
        long lastRequestTime = currentTime;

        if (requestCountCookie.isPresent() && lastRequestTimeCookie.isPresent()) {
            requestCount = Integer.parseInt(requestCountCookie.get().getValue());
            lastRequestTime = Long.parseLong(lastRequestTimeCookie.get().getValue());

            // 시간 제한 확인
            if (Instant.now().isBefore(Instant.ofEpochMilli(lastRequestTime).plus(COOKIE_EXPIRATION_MINUTES, ChronoUnit.MINUTES))) {
                if (requestCount >= MAX_REQUESTS) {
                    throw new RuntimeException("30분 동안 최대 5번의 이메일 인증 요청만 허용됩니다. 나중에 다시 시도해주세요.");
                } else {
                    requestCount++;
                }
            } else {
                requestCount = 1;
                lastRequestTime = currentTime;
            }
        }

        // 쿠키 설정
        setCookie(response, "requestCount", String.valueOf(requestCount), COOKIE_EXPIRATION_MINUTES * 60);
        setCookie(response, "lastRequestTime", String.valueOf(lastRequestTime), COOKIE_EXPIRATION_MINUTES * 60);

        // 6자리 난수 생성
        String verificationCode = generateVerificationCode();

        // secret_code 암호화
        String encryptedCode = null;
        try {
            encryptedCode = AESUtil.encrypt(verificationCode);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error while encrypting the secret code");
        }

        // 이메일 인증 정보 데이터베이스에 저장
        EmailConfirm emailConfirm = new EmailConfirm();
        emailConfirm.setEmail(toEmail);
        emailConfirm.setSecretCode(encryptedCode);
        emailConfirm.setCreatedAt(LocalDateTime.now());
        emailConfirm.setIsSend(true);
        emailConfirm.setSendedAt(LocalDateTime.now());

        emailConfirmRepository.save(emailConfirm);

        // 이메일 제목과 내용 설정
        String subject = "이메일 인증번호입니다.";
        String body = "귀하의 이메일 인증번호는 다음과 같습니다: " + verificationCode;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("4Q <no-reply@qqqq.world>");

        // 이메일 전송
        mailSender.send(message);
    }

    private String generateVerificationCode() {
        int code = randomGenerator.nextInt(900000) + 100000; // 6자리 난수 생성
        return String.valueOf(code);
    }

    public boolean verifyCode(String email, String code) {
        // 이메일과 암호화된 코드로 데이터베이스에서 검색
        Optional<EmailConfirm> emailConfirmOpt = emailConfirmRepository.findByEmail(email);

        if (emailConfirmOpt.isPresent()) {
            EmailConfirm emailConfirm = emailConfirmOpt.get();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime createdAt = emailConfirm.getCreatedAt();

            // 유효기간 확인
            if (Duration.between(createdAt, now).toMillis() > authCodeExpirationMillis) {
                return false; // 유효기간 만료
            }

            try {
                String decryptedCode = AESUtil.decrypt(emailConfirm.getSecretCode());
                if (decryptedCode.equals(code)) {
                    emailConfirm.setConfirmation(true);
                    emailConfirm.setConfirmedAt(now);
                    emailConfirmRepository.save(emailConfirm);
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Error while decrypting the secret code");
            }
        }

        return false;
    }

    private Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(cookie -> name.equals(cookie.getName()))
                    .findFirst();
        }
        return Optional.empty();
    }

    private void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(true); // XSS 공격 방지
        response.addCookie(cookie);
    }
}
