package org.chunsik.pq.login.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chunsik.pq.login.dto.*;
import org.chunsik.pq.login.exception.DuplicateEmailException;
import org.chunsik.pq.login.manager.UserManager;
import org.chunsik.pq.login.repository.UserRepository;
import org.chunsik.pq.login.security.CustomUserDetails;
import org.chunsik.pq.login.security.JwtTokenProvider;
import org.chunsik.pq.login.model.OauthProvider;
import org.chunsik.pq.login.model.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserManager userManager;

    public TokenDto login(String email, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        String accessToken = jwtTokenProvider.createToken(email);
        String refreshToken = jwtTokenProvider.createRefreshToken(email);

        return new TokenDto(accessToken, refreshToken);
    }

    @Transactional
    public User join(JoinDto joinDto, OauthProvider oauthProvider) {

        User user = User.create(
                joinDto.getNickname().trim(),
                joinDto.getEmail(),
                passwordEncoder.encode(joinDto.getPassword()),
                oauthProvider
        );

        if (userRepository.existsByEmail(joinDto.getEmail())) {
            throw new DuplicateEmailException("email exists.");
        }

        return userRepository.save(user);
    }

    public boolean checkIfUserExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public TokenDto signUpOrLogin(SignUpOrLoginDto dto, OauthProvider oauthProvider) {
        Optional<User> optionalUser = userRepository.findByEmail(dto.getEmail());

        if (optionalUser.isPresent()) {
            if (optionalUser.get().getOauthProvider() != oauthProvider)
                throw new DuplicateEmailException("email exists");
        }

        User user = optionalUser.orElseGet(
                () -> User.create(
                        dto.getNickname(),
                        dto.getEmail(),
                        "",
                        oauthProvider
                ));

        userRepository.save(user);
        UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken.authenticated(user.getEmail(), user.getPassword(), new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(token);

        String accessToken = jwtTokenProvider.createToken(user.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        return new TokenDto(accessToken, refreshToken);
    }

    public MeResponseDto me() {
        Optional<CustomUserDetails> currentUser = userManager.currentUser();
        CustomUserDetails customUserDetails = currentUser.orElseThrow(() -> new AuthenticationException("No current user") {
        });
        String email = customUserDetails.getEmail();
        String nickname = customUserDetails.getNickname();
        Long id = customUserDetails.getId();
        return new MeResponseDto(id, email, nickname);
    }

    public LogoutSuccessDTO logout() {
        Optional<CustomUserDetails> currentUser = userManager.currentUser();
        CustomUserDetails customUserDetails = currentUser.orElseThrow(() -> new AuthenticationException("No current user") {
        });

        return new LogoutSuccessDTO("logout success");
    }

    @Transactional
    public ModifyResponseDTO modify(ModifyRequestDTO modifyRequestDTO) {
        Optional<CustomUserDetails> currentUser = userManager.currentUser();
        CustomUserDetails customUserDetails = currentUser.orElseThrow(() -> new AuthenticationException("No current user") {
        });
        Optional<User> findUser = userRepository.findByEmail(customUserDetails.getEmail());
        User user = findUser.orElseThrow(() -> new NoSuchElementException("User Not Exist"));

        user.modifyNickname(modifyRequestDTO.getNickname());

        User modifiedUser = userRepository.save(user);

        return new ModifyResponseDTO(
                modifiedUser.getId(),
                modifiedUser.getEmail(),
                modifyRequestDTO.getNickname()
        );
    }

    @Transactional
    public ResetResponseDTO resetPassword(ResetPasswordDTO resetPasswordDTO) {
        Optional<User> findUserByEmail = userRepository.findByEmail(resetPasswordDTO.getEmail());
        User user = findUserByEmail.orElseThrow(() -> new NoSuchElementException("User Not Exist"));

        user.resetPassword(passwordEncoder.encode(resetPasswordDTO.getPassword()));

        Long id = userRepository.save(user).getId();
        return new ResetResponseDTO(id);
    }
}
