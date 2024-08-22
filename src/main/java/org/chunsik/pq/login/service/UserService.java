package org.chunsik.pq.login.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chunsik.pq.login.dto.JoinDto;
import org.chunsik.pq.login.dto.SignUpOrLoginDto;
import org.chunsik.pq.login.dto.TokenDto;
import org.chunsik.pq.login.exception.DuplicateEmailException;
import org.chunsik.pq.login.repository.UserRepository;
import org.chunsik.pq.login.security.JwtTokenProvider;
import org.chunsik.pq.model.OauthProvider;
import org.chunsik.pq.model.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

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
                joinDto.getNickname(),
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
}
