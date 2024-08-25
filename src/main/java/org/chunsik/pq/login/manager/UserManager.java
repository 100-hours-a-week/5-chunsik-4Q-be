package org.chunsik.pq.login.manager;

import lombok.RequiredArgsConstructor;
import org.chunsik.pq.login.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserManager {


    public Optional<CustomUserDetails> currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Optional.empty();
        }
        Object principal = authentication.getPrincipal();
        if (principal == null) {
            return Optional.empty();
        }
        if (principal instanceof CustomUserDetails customUserDetails) {
            return Optional.of(customUserDetails);
        }
        return Optional.empty();
    }
}
