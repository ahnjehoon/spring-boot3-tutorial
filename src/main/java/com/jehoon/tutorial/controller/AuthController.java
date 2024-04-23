package com.jehoon.tutorial.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    @GetMapping
    public String getToken() {
        return createToken();
    }

    @GetMapping("extract-subject")
    public String decode(@RequestParam("token") String token) {
        final var jwt = jwtDecoder.decode(token);
        return jwt.getSubject();
    }

    private String createToken() {
        final var jwtClaimsSet = JwtClaimsSet.builder()
                .issuer("middle-developer.tistory.com")
                .issuedAt(Instant.now())
                .subject("admin")
                .expiresAt(Instant.now().plusSeconds(60 * 60 * 24))
                .build();

        return jwtEncoder.
                encode(JwtEncoderParameters.from(jwtClaimsSet))
                .getTokenValue();
    }
}
