package org.chunsik.pq.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@Table(name = "User")
@Entity
@Getter
public class User implements UserDetails {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false,unique = true)
    private String email;

    @Column
    private String password;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private OauthProvider oauthProvider;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return nickname;
    }

    @Override
    public String getPassword(){
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    private User(Long id,String nickname,String email,String password,LocalDateTime createdAt,OauthProvider oauthProvider){
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
        this.oauthProvider = oauthProvider;
    }

    public static User create(String nickname,String email,String password, OauthProvider oauthProvider){
        return new User(null,nickname,email,password,LocalDateTime.now(),oauthProvider);
    }
}
