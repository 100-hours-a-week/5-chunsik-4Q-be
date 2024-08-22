package org.chunsik.pq.generate.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.chunsik.pq.generate.dto.GenerateApiRequestDTO;
import org.chunsik.pq.generate.dto.GenerateImageDTO;
import org.chunsik.pq.generate.dto.GenerateResponseDTO;
import org.chunsik.pq.generate.manager.OpenAIManager;
import org.chunsik.pq.generate.model.BackgroundImage;
import org.chunsik.pq.generate.repository.BackgroundImageRepository;
import org.chunsik.pq.login.repository.UserRepository;
import org.chunsik.pq.model.User;
import org.chunsik.pq.s3.dto.S3UploadResponseDTO;
import org.chunsik.pq.s3.manager.S3Manager;
import org.chunsik.pq.s3.model.Ticket;
import org.chunsik.pq.s3.repository.TicketRepository;
import org.chunsik.pq.shortenurl.model.ShortenURL;
import org.chunsik.pq.shortenurl.repository.ShortenUrlRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GenerateService {
    private final S3Manager s3Manager;
    private final OpenAIManager openAIManager;
    private final BackgroundImageRepository backgroundImageRepository;
    private final ShortenUrlRepository shortenURLRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;

    @Value("${cloud.aws.s3.generate}")
    private String generate;

    @Value("${cloud.aws.s3.ticket}")
    private String ticket;

    @Transactional
    public GenerateResponseDTO generateImage(GenerateImageDTO generateImageDTO) {
        // 이미지 생성
        return openAIManager.generateImage(generateImageDTO.getTags());
    }

    @Transactional
    public void createImage(GenerateApiRequestDTO dto) throws IOException {
        this.createImage(dto.getTicketImage(), dto.getBackgroundImageUrl(),
                dto.getShortenUrlId(), dto.getTitle(),
                dto.getTags(), dto.getUserId(), dto.getCategoryId()
        );
    }

    @Transactional
    public void createImage(
            MultipartFile ticketImage, String backgroundImageUrl,
            String shortenUrlId, String title,
            List<String> tags, Long userId, String categoryId
    ) throws IOException, NoSuchElementException {
        // 배경이미지 다운로드
        File jpgFile = downloadJpg(backgroundImageUrl);

        // 배경이미지 S3 업로드
        S3UploadResponseDTO s3UploadResponseDTO = s3Manager.uploadFile(jpgFile, generate);

        // 배경이미지 Insert
        BackgroundImage.BackgroundImageBuilder builder = BackgroundImage.builder()
                .size(jpgFile.length())
                .url(s3UploadResponseDTO.getS3Url())
                .userId(userId)
                .categoryId(categoryId);

        switch (tags.size()) {
            case 3:
                builder.thirdTag(tags.get(2));
            case 2:
                builder.secondTag(tags.get(1));
            case 1:
                builder.firstTag(tags.get(0));
        }

        BackgroundImage backgroundImage = builder.build();
        backgroundImageRepository.save(backgroundImage);

        // 티켓 이미지 S3 업로드
        File file = new File("/tmp/" + UUID.randomUUID() + ".jpg");
        ticketImage.transferTo(file);

        s3UploadResponseDTO = s3Manager.uploadFile(file, ticket);

        // 단축 URL
        Optional<ShortenURL> shortenURL = shortenURLRepository.findById(Long.valueOf(shortenUrlId));
        if (shortenURL.isEmpty()) {
            throw new NoSuchElementException("No shorten URL found for shortenUrlId: " + shortenUrlId);
        }

        // 로그인 사용자
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NoSuchElementException("No user found for userId: " + userId);
        }
        Ticket ticket = new Ticket(user.get(), shortenURL.get(), backgroundImage, title, s3UploadResponseDTO.getS3Url());
        ticketRepository.save(ticket);
    }

    // url 이미지를 File로 매핑해 리턴하는 메소드
    private File downloadJpg(String imageUrl) throws IOException {
        BufferedImage image = ImageIO.read(new URL(imageUrl));
        String fileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);    // 파일명만 추출
        File jpgFile = File.createTempFile(fileName, "jpg");    // 파일명.jpg
        ImageIO.write(image, "jpg", jpgFile);
        return jpgFile;
    }
}
