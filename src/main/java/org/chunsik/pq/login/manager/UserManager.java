package org.chunsik.pq.login.manager;

import lombok.RequiredArgsConstructor;
import org.chunsik.pq.login.repository.UserRepository;
import org.chunsik.pq.login.security.CustomUserDetails;
import org.chunsik.pq.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserManager {
    private final UserRepository userRepository;

    public Optional<User> getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) principal;
                return Optional.of(userRepository.getReferenceById(userDetails.getId()));
            }
        }
        return Optional.empty();
    }
}
