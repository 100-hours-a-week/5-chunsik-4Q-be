package org.chunsik.pq.login.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Table(name = "User")
@Entity
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false, unique = true)
    private String email;

    @Setter
    @Column(nullable = false)
    private String password;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private OauthProvider oauthProvider;

    private User(Long id, String nickname, String email, String password, LocalDateTime createdAt, OauthProvider oauthProvider) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
        this.oauthProvider = oauthProvider;
    }

    public static User create(String nickname, String email, String password, OauthProvider oauthProvider) {
        return new User(null, nickname, email, password, LocalDateTime.now(), oauthProvider);
    }
}
