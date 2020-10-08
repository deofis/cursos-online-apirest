package com.deofis.tiendaapirest.autenticacion.security;

import com.deofis.tiendaapirest.autenticacion.exceptions.TokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Date;

import static io.jsonwebtoken.Jwts.parser;

@Service
public class JwtProveedor {

    private KeyStore keyStore;
    @Value("${jwt.expiration.time}")
    private Long expirationInMillis;

    @PostConstruct
    public void init() {
        try {
            keyStore = KeyStore.getInstance("JKS");
            InputStream resourceAsStream = getClass().getResourceAsStream("/springblog.jks");
            keyStore.load(resourceAsStream, "secret".toCharArray());
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new TokenException("Excepción al cargar el keystore");
        }
    }
    public String generateToken(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(principal.getUsername())
                .claim("Authorities", principal.getAuthorities())
                .signWith(getPrivateKey())
                .setExpiration(new Date(new Date().getTime() + this.expirationInMillis))
                .compact();
    }

    // Se necesita generar el token sólo con el nombre de usuario (email) en el caso de que
    // en lugar de iniciar sesión, se esté extendiendo la sesión mediante el refresh token.
    public String generateTokenWithUsername(String userEmail) {
        return Jwts.builder()
                .setSubject(userEmail)
                .signWith(getPrivateKey())
                .setExpiration(new Date(new Date().getTime() + expirationInMillis))
                .compact();
    }

    public boolean validateToken(String jwt) {
        parser().setSigningKey(getPublicKey()).parseClaimsJws(jwt);
        return true;
    }

    public String getUsernameFromJwt(String jwt) {
        Claims claims = parser()
                .setSigningKey(getPublicKey())
                .parseClaimsJws(jwt)
                .getBody();

        return claims.getSubject();
    }

    private PrivateKey getPrivateKey() {

        try {
            return (PrivateKey) keyStore.getKey("springblog", "secret".toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new TokenException("Error al obtener private key del keystore");
        }
    }

    private PublicKey getPublicKey() {

        try {
            return keyStore.getCertificate("springblog").getPublicKey();
        } catch (KeyStoreException e) {
            throw new TokenException("Excepción al obtener public key del keystore");
        }
    }

    public Long getExpirationInMillis() {
        return this.expirationInMillis;
    }
}