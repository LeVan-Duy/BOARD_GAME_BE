package org.example.board_game.core.auth.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.auth.service.JwtService;
import org.example.board_game.entity.auth.RefreshToken;
import org.example.board_game.repository.auth.BlacklistTokenRepository;
import org.example.board_game.repository.auth.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.signer-key}")
    String SECRET_KEY;

    @Value("${jwt.expiration}")
    Long ACCESS_TOKEN_EXPIRATION;

    @Value("${jwt.refresh-expiration}")
    Long REFRESH_TOKEN_EXPIRATION;

    final RefreshTokenRepository refreshTokenRepository;
    final BlacklistTokenRepository blacklistTokenRepository;

    @Override
    public String generateToken(UserDetails userDetails) {
        return Jwts
                .builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(getSignKey())
                .compact();
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public boolean isValidToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return (username != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    @Override
    public String createRefreshToken(UserDetails userDetails) {
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .username(userDetails.getUsername())
                .expiryDate(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION)
                .build();
        deleteByUsername(userDetails.getUsername());
        refreshTokenRepository.save(refreshToken);
        return token;
    }

    @Override
    public boolean validRefreshToken(String token) {

        if (blacklistTokenRepository.existsByToken(token)) {
            return false;
        }
        return refreshTokenRepository.findByToken(token)
                .filter(rt -> Instant.ofEpochMilli(rt.getExpiryDate()).isAfter(Instant.now()))
                .isPresent();
    }

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public void deleteByUsername(String username) {
        refreshTokenRepository.deleteByUsername(username);
    }
}
