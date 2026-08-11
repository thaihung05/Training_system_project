/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.utils;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

/**
 *
 * @author LENOVO
 */
public class JwtUtils {
    private static final String SECRET = loadSecret();
    private static final long EXPIRATION_MS = 86400000;

    private static String loadSecret() {
        try (InputStream is = JwtUtils.class.getClassLoader().getResourceAsStream("databases.properties")) {
            if (is == null) {
                throw new IllegalStateException("Không tìm thấy databases.properties trong classpath");
            }
            Properties props = new Properties();
            props.load(is);
            String secret = props.getProperty("jwt.secret");
            if (secret == null || secret.trim().isEmpty()) {
                throw new IllegalStateException("Thiếu cấu hình jwt.secret trong databases.properties");
            }
            return secret.trim();
        } catch (IOException e) {
            throw new IllegalStateException("Không đọc được jwt.secret từ databases.properties", e);
        }
    }

    public static String generateToken(String username, String role) throws Exception {
        JWSSigner signer = new MACSigner(SECRET);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(username)
                .claim("role", role)
                .expirationTime(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .issueTime(new Date())
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
        signedJWT.sign(signer);

        return signedJWT.serialize();
    }

    public static String validateTokenAndGetUsername(String token) throws Exception {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier verifier = new MACVerifier(SECRET);

        if (signedJWT.verify(verifier)) {
            Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (expiration != null && expiration.after(new Date())) {
                return signedJWT.getJWTClaimsSet().getSubject();
            }
        }
        return null;
    }
}
