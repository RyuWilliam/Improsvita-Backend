package co.improsvita.persistence.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "tokens")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String tokenString;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TokenType tokenType = TokenType.BEARER;

    @Column(nullable = false)
    private Boolean isRevoked = false;

    @Column(nullable = false)
    private Boolean isExpired = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    public enum TokenType {
        BEARER
    }

    public Token() {
    }

    public Token(String tokenString, TokenType tokenType, Boolean isRevoked, Boolean isExpired, UserEntity user) {
        this.tokenString = tokenString;
        this.tokenType = tokenType;
        this.isRevoked = isRevoked;
        this.isExpired = isExpired;
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTokenString() {
        return tokenString;
    }

    public void setTokenString(String tokenString) {
        this.tokenString = tokenString;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    public Boolean getRevoked() {
        return isRevoked;
    }

    public void setRevoked(Boolean revoked) {
        isRevoked = revoked;
    }

    public Boolean getExpired() {
        return isExpired;
    }

    public void setExpired(Boolean expired) {
        isExpired = expired;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}