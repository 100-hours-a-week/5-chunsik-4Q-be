package org.chunsik.pq.shortenurl.service;

import lombok.RequiredArgsConstructor;
import org.chunsik.pq.shortenurl.dto.RequestConvertUrlDTO;
import org.chunsik.pq.shortenurl.dto.ResponseConvertUrlDTO;
import org.chunsik.pq.shortenurl.manager.ShortenUrlManager;
import org.chunsik.pq.shortenurl.model.ShortenURL;
import org.chunsik.pq.shortenurl.repository.ShortenUrlRepository;
import org.springframework.stereotype.Service;

import static org.chunsik.pq.shortenurl.util.constant.ShortenUrlConstant.*;

@Service
@RequiredArgsConstructor
public class ShortenUrlService {

    private final ShortenUrlRepository shortenUrlRepository;
    private final ShortenUrlManager shortenUrlManager;

    public ResponseConvertUrlDTO convertToShortUrl(RequestConvertUrlDTO requestConvertUrlDTO) {
        String srcUrl = requestConvertUrlDTO.getSrcUrl();
        String destUrl = INIT_STRING_SET;

        for (int count = INIT_NUMBER; count < END_OF_TRY_NUMBER; count++) {
            destUrl = shortenUrlManager.convertUrl();
            if (shortenUrlRepository.findByDestURL(destUrl) == null) break;
            if (count == END_OF_DUPLICATION_TRY) throw new RuntimeException();    // custom 예외 만들어서 처리
        }

        ShortenURL shortenURL = new ShortenURL(srcUrl, destUrl);
        shortenUrlRepository.save(shortenURL);

        return new ResponseConvertUrlDTO(destUrl);
    }

    public String responseShortUrl(String destUrl) {
        ShortenURL shortenURL = shortenUrlRepository.findByDestURL(destUrl);
        System.out.println(shortenURL.getSrcURL());
        System.out.println(shortenURL.getDestURL());
        return shortenURL.getSrcURL();
    }

}
