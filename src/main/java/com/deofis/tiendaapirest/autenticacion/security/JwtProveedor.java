package com.deofis.tiendaapirest.autenticacion.security;

import com.deofis.tiendaapirest.autenticacion.exceptions.TokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProveedor {

    private KeyStore keyStore;

    private Long expirationInMillis;

    private String secretKey;

    @PostConstruct
    public void init() {
        try {
            keyStore = KeyStore.getInstance("JKS");
            InputStream resourceAsStream = getClass().getResourceAsStream("/deofis.jks");
            keyStore.load(resourceAsStream, this.secretKey.toCharArray());
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new TokenException("Excepción al cargar el keystore : " + e.getMessage());
        }
    }

    /**
     * Genera un JWT para el usuario que se está autenticando en el sistema (iniciando
     * sesión), seteando distintos atributos para el mismo: subject, authorities,
     * key de firmado y fecha de expiración.
     * @param authentication Authentication: objeto que contiene el usuario que se está
     *                       autenticando en el sistema.
     * @return String con el token generado.
     */
    public String generateToken(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(principal.getUsername())
                .claim("Authorities", principal.getAuthorities())
                .signWith(getPrivateKey())
                .setExpiration(new Date(new Date().getTime() + this.expirationInMillis))
                .compact();
    }

    /**
     * Genera el token (JWT) sólo con el nombre de usuario (email) en el caso de que,
     * en lugar de iniciar sesión, se necesite extender la sesión mediante el
     * refresh token.
     * @param userEmail String email del usuario a generar token.
     * @return String del JWT generado.
     */
    public String generateTokenWithUsername(String userEmail) {
        return Jwts.builder()
                .setSubject(userEmail)
                .signWith(getPrivateKey())
                .setExpiration(new Date(new Date().getTime() + expirationInMillis))
                .compact();
    }

    /**
     * Valida que el token sea válido, a través de la PUBLIC KEY del certificado.
     * @param jwt String con el token a validar.
     * @return boolean si fue o no validado.
     */
    public boolean validateToken(String jwt) {
        try {
            parser().setSigningKey(getPublicKey()).parseClaimsJws(jwt);
        } catch (ExpiredJwtException e) {
            return false;
        }
        return true;
    }

    /**
     * Obtiene el nombre de usuario (email) a partir de un token requerido.
     * @param jwt String JWT a obtener su usuario.
     * @return String email del usuario.
     */
    public String getUsernameFromJwt(String jwt) {
        Claims claims = parser()
                .setSigningKey(getPublicKey())
                .parseClaimsJws(jwt)
                .getBody();

        return claims.getSubject();
    }

    /**
     * Obtiene la PRIVATE KEY a partir del archivo certificado guardado en key store.
     * @return PrivateKey.
     */
    private PrivateKey getPrivateKey() {

        try {
            return (PrivateKey) keyStore.getKey("deofis", this.secretKey.toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new TokenException("Error al obtener private key del keystore : " + e.getMessage());
        }
    }

    /**
     * Obtiene la PUBLIC KEY a partir del archivo certificado guardado en key store.
     * @return PublicKey.
     */
    private PublicKey getPublicKey() {

        try {
            return keyStore.getCertificate("deofis").getPublicKey();
        } catch (KeyStoreException e) {
            throw new TokenException("Excepción al obtener public key del keystore");
        }
    }

    /**
     * Obtiene el tiempo de expiración del JWT en milisegundos. Este valor se establece
     * en las propiedades de la aplicación.
     * @return Long tiempo de expiración en milisegundos.
     */
    public Long getExpirationInMillis() {
        return this.expirationInMillis;
    }

    /**
     * Obtiene la SECRET KEY para el certificado al firmar JWT.
     * @return String secret key.
     */
    public String getSecretKey() {
        return this.secretKey;
    }
}