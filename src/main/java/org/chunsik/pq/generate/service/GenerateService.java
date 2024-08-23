package org.chunsik.pq.generate.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.chunsik.pq.generate.dto.*;
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
    private String ticketFolder;

    @Value("${chunsik.server.url}")
    private String serverDomain;

    @Transactional
    public GenerateResponseDTO generateImage(GenerateImageDTO generateImageDTO) {
        // 이미지 생성
        return openAIManager.generateImage(generateImageDTO.getTags());
    }

    @Transactional
    public CreateImageResponseDto createImage(GenerateApiRequestDTO dto) throws IOException {
        return this.createImage(dto.getTicketImage(), dto.getBackgroundImageUrl(),
                dto.getShortenUrlId(), dto.getTitle(),
                dto.getTags(), dto.getUserId(), dto.getCategoryId()
        );
    }

    @Transactional
    public CreateImageResponseDto createImage(
            MultipartFile ticketImage, String backgroundImageUrl,
            Long shortenUrlId, String title,
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

        s3UploadResponseDTO = s3Manager.uploadFile(file, ticketFolder);

        // 단축 URL
        ShortenURL shortenURL = shortenURLRepository.findById(shortenUrlId).orElseThrow(() -> new NoSuchElementException("No shorten URL found for shortenUrlId: " + shortenUrlId));

        // 로그인 사용자
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("No user found for userId: " + userId));

        Ticket ticket = new Ticket(user, shortenURL, backgroundImage, title, s3UploadResponseDTO.getS3Url());
        Long id = ticketRepository.save(ticket).getId();

        return new CreateImageResponseDto("Success", id);
    }

    public TicketResponseDTO findTicketById(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new NoSuchElementException("No ticket found for ticketId: " + ticketId));

        String image_path = ticket.getImagePath();
        String title = ticket.getTitle();
        String shortenUrl = serverDomain + "/s/" + ticket.getUrl().getDestURL();

        return new TicketResponseDTO(image_path, title, shortenUrl);
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
