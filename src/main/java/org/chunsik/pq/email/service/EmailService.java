package org.chunsik.pq.email.service;

import org.springframework.beans.factory.annotation.Value;
import org.chunsik.pq.email.model.EmailConfirm;
import org.chunsik.pq.email.repository.EmailConfirmRepository;
import org.chunsik.pq.email.util.AESUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

@Service
public class EmailService {

    @Value("${auth-code-expiration-millis}")
    private long authCodeExpirationMillis;

    private final RandomGenerator randomGenerator = RandomGeneratorFactory.of("Random").create();

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailConfirmRepository emailConfirmRepository;

    public void sendSimpleEmail(String toEmail) {
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
        message.setFrom("4Q <no-reply@4q.com>");

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
}
