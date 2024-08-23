package org.chunsik.pq.generate.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.chunsik.pq.generate.dto.GenerateApiRequestDTO;
import org.chunsik.pq.generate.dto.GenerateImageDTO;
import org.chunsik.pq.generate.dto.GenerateResponseDTO;
import org.chunsik.pq.generate.manager.OpenAIManager;
import org.chunsik.pq.generate.model.BackgroundImage;
import org.chunsik.pq.generate.model.Category;
import org.chunsik.pq.generate.model.Tag;
import org.chunsik.pq.generate.model.TagBackgroundImage;
import org.chunsik.pq.generate.repository.BackgroundImageRepository;
import org.chunsik.pq.generate.repository.CategoryRepository;
import org.chunsik.pq.generate.repository.TagBackgroundImageRepository;
import org.chunsik.pq.generate.repository.TagRepository;
import org.chunsik.pq.login.repository.UserRepository;
import org.chunsik.pq.model.User;
import org.chunsik.pq.s3.dto.S3UploadResponseDTO;
import org.chunsik.pq.s3.manager.S3Manager;
import org.chunsik.pq.s3.model.Ticket;
import org.chunsik.pq.s3.repository.TicketRepository;
import org.chunsik.pq.shortenurl.model.ShortenURL;
import org.chunsik.pq.shortenurl.repository.ShortenUrlRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final TagBackgroundImageRepository tagBackgroundImageRepository;

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
                dto.getTags(), dto.getCategory()
        );
    }

    @Transactional
    public void createImage(
            MultipartFile ticketImage, String backgroundImageUrl,
            Integer shortenUrlId, String title,
            List<String> tags, String category
    ) throws IOException, NoSuchElementException {
        // 로그인된 사용자의 userId를 찾기
        Integer userId = findAuthenticatedUserId();

        // 카테고리로 카테고리ID 찾기
        Integer categoryId = findCategoryIdByName(category);

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

        BackgroundImage backgroundImage = builder.build();
        backgroundImageRepository.save(backgroundImage);

        // 태그와 BackgroundImage 간의 관계 저장
        saveTagBackgroundImages(tags, backgroundImage.getId());

        // 티켓 이미지 S3 업로드
        File file = new File("/tmp/" + UUID.randomUUID() + ".jpg");
        ticketImage.transferTo(file);

        s3UploadResponseDTO = s3Manager.uploadFile(file, ticket);

        // 단축 URL
        Optional<ShortenURL> shortenURL = shortenURLRepository.findById(shortenUrlId);
        if (shortenURL.isEmpty()) {
            throw new NoSuchElementException("No shorten URL found for shortenUrlId: " + shortenUrlId);
        }

        Optional<User> user = Optional.empty();
        if (userId != null) {
            user = userRepository.findById(userId);
            if (user.isEmpty()) {
                throw new NoSuchElementException("No user found for userId: " + userId);
            }
        }

        Ticket ticket = new Ticket(userId, shortenURL.get(), backgroundImage, title, s3UploadResponseDTO.getS3Url());
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

    private Integer findAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            UserDetails details = (UserDetails) authentication.getPrincipal();
            String email = details.getUsername();

            // 이메일을 통해 userId 찾기
            Optional<User> userOptional = userRepository.findByEmail(email);
            return userOptional.map(User::getId)
                    .orElse(null);
        }
        // 비로그인 사용자는 null 반환
        return null;
    }

    // 카테고리 이름으로 카테고리 ID 찾기
    private Integer findCategoryIdByName(String categoryName) {
        Optional<Category> category = categoryRepository.findByName(categoryName);
        return category.map(Category::getId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + categoryName));
    }

    // Tags 처리 및 TagBackgroundImage에 저장
    private void saveTagBackgroundImages(List<String> tags, Long backgroundImageId) {
        for (String tagName : tags) {
            Optional<Tag> tagOptional = tagRepository.findByName(tagName);
            if (tagOptional.isPresent()) {
                Tag tag = tagOptional.get();

                // TagBackgroundImage 객체 생성 후 저장
                TagBackgroundImage tagBackgroundImage = new TagBackgroundImage(tag.getId(), backgroundImageId);
                tagBackgroundImageRepository.save(tagBackgroundImage);
            } else {
                // Tag가 없는 경우, 여기서 새 Tag를 생성하거나 무시할 수 있습니다.
            }
        }
    }
}
