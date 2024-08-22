package org.chunsik.pq.shortenurl.manager;

import org.springframework.stereotype.Component;

import java.util.Random;

import static org.chunsik.pq.shortenurl.util.constant.ShortenUrlConstant.*;

@Component
public class ShortenUrlManager {
    private final Random random = new Random();

    public String convertUrl() {
        String base62Characters = BASE62_CHARACTER_SET;
        StringBuilder convertKey = new StringBuilder();

        convertKey.append("/s/");
        for (int count = INIT_NUMBER; count < END_OF_GENERATE_NUMBER; count++) {
            int base62Index = random.nextInt(INIT_NUMBER, base62Characters.length());
            char base62SelectCharacter = base62Characters.charAt(base62Index);
            convertKey.append(base62SelectCharacter);
        }

        return convertKey.toString();
    }
}
