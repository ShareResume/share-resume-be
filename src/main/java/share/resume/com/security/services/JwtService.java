package share.resume.com.security.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import share.resume.com.exceptions.JwtException;
import share.resume.com.exceptions.UserException;
import share.resume.com.security.dto.UserDetailsDto;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    @Value("${jwt.private.key}")
    private String privateKey;

    @Value("${jwt.public.key}")
    private String publicKey;

    @Value("${jwt.expiration.minutes}")
    private int jwtExpirationMinutes;

    @Value("${jwt.refresh.token.expiration.days}")
    private int jwtRefreshTokenExpirationDays;

    public String generateAccessToken(UserDetailsDto userDetailsDto) {
        try {
            if (userDetailsDto.getId() == null) {
                throw new UserException("User id is null during JWT generation");
            }
            log.info("Generating JWT");
            byte[] decodedKey = Base64.getDecoder().decode(privateKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
            PrivateKey privateKeyObject = keyFactory.generatePrivate(keySpec);

            return Jwts.builder()
                    .signWith(SignatureAlgorithm.RS256, privateKeyObject)
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(jwtExpirationMinutes)))
                    .subject(userDetailsDto.getId().toString())
                    .claim("email", userDetailsDto.getEmail())
                    .claim("nick", userDetailsDto.getNick())
                    .claim("role", userDetailsDto.getRole())
                    .compact();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new JwtException("Error during generating JWT");
        }
    }

    public String generateRefreshToken(UUID userId, String email) {
        try {
            if (userId == null) {
                throw new UserException("User id is null during JWT refresh token generation");
            }
            byte[] decodedKey = Base64.getDecoder().decode(privateKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
            PrivateKey privateKeyObject = keyFactory.generatePrivate(keySpec);

            return Jwts.builder()
                    .signWith(SignatureAlgorithm.RS256, privateKeyObject)
                    .claim("email", email)
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(jwtRefreshTokenExpirationDays)))
                    .subject(userId.toString())
                    .compact();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new JwtException("Error during generating JWT");
        }
    }

    public Claims validateToken(String token) {
        try {
            log.info("Validating JWT");
            byte[] decoded = Base64.getDecoder().decode(publicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKeyObject = keyFactory.generatePublic(keySpec);

            Claims claims = Jwts.parser()
                    .setSigningKey(publicKeyObject)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            log.info("JWT is validated. Subject: {}", claims.getSubject());
            return claims;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new JwtException("Error during validating JWT");
        }
    }
}
