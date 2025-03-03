package org.lib.jwtdemo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Service
public class JwtService {

    private static final String SECRET = "qBTmv4oXFFR2GwjexDJ4t6fsIUIUhhXqlktXjXdkcyygs8nPVEwMfo29VDRRepYDVV5IkIxBMzr7OEHXEHd37w==";

    //генерация аутентификационого токена(складываем токены в dto, чтобы потом отдать)
    public JwtAuthenticationDTO generateAuthToken(String email) {
        JwtAuthenticationDTO jwtAuthenticationDTO = new JwtAuthenticationDTO();
        jwtAuthenticationDTO.setToken(generateJwtToken(email));
        jwtAuthenticationDTO.setRefreshToken(generateRefreshToken(email));
        return jwtAuthenticationDTO;
    }

    //обновление jwt токена с помощью refresh токена
    public JwtAuthenticationDTO refreshBaseToken(String email, String refreshToken) {
        JwtAuthenticationDTO jwtAuthenticationDTO = new JwtAuthenticationDTO();
        jwtAuthenticationDTO.setToken(generateJwtToken(email));
        jwtAuthenticationDTO.setRefreshToken(refreshToken);
        return jwtAuthenticationDTO;
    }

    //получения почты из токена
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    //проверка на валидность(правильный ли токен)
    public boolean validateJwtToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET)
                    .build()
                    .parseClaimsJws(token);
            return true;
        }catch (ExpiredJwtException expEx) {
            log.error("Token expired", expEx);
        } catch (UnsupportedJwtException unsEx) {
            log.error("Unsupported jwt", unsEx);
        } catch (MalformedJwtException mjEx) {
            log.error("Malformed jwt", mjEx);
        } catch (SignatureException sEx) {
            log.error("Invalid signature", sEx);
        } catch (Exception e) {
            log.error("invalid token", e);
        }
        return false;
    }


    //генерация jwt токена
    public String generateJwtToken(String email) {
        Date date = Date.from(LocalDateTime.now().plusMinutes(10).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .setSubject(email)
                .setExpiration(date)
                .signWith(getSignInKey())
                .compact();
    }

    //генерация refresh токена
    public String generateRefreshToken(String email) {
        Date date = Date.from(LocalDateTime.now().plusDays(10).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .setSubject(email)
                .setExpiration(date)
                .signWith(getSignInKey())
                .compact();
    }

    //расшифровка секрета
    public SecretKey getSignInKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
    }
}
