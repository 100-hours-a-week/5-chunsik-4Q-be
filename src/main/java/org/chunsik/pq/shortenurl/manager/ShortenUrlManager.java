package org.chunsik.pq.shortenurl.manager;

import org.springframework.stereotype.Component;

import java.util.Random;

import static org.chunsik.pq.shortenurl.util.constant.ShortenUrlConstant.*;

@Component
public class ShortenUrlManager {

    public String convertUrl() {
        String destinationUrl;

        String base62Characters = BASE62_CHARACTER_SET;

        Random random = new Random();
        StringBuilder convertKey = new StringBuilder();

        for (int count = INIT_NUMBER; count < END_OF_GENERATE_NUMBER; count++) {
            int base62Index = random.nextInt(INIT_NUMBER, base62Characters.length());
            char base62SelectCharacter = base62Characters.charAt(base62Index);
            convertKey.append(base62SelectCharacter);
        }

        destinationUrl = convertKey + INIT_STRING_SET;

        return destinationUrl;
    }
}
