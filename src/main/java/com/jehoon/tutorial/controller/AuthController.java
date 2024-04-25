package com.jehoon.tutorial.controller;

import com.jehoon.tutorial.dto.LoginRequest;
import com.jehoon.tutorial.entity.User;
import com.jehoon.tutorial.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final UserService userService;

    @PostMapping("login")
    public String login(@RequestBody LoginRequest request) {
        final var user = userService.select(request);
        return createToken(user);
    }

    @GetMapping("token-info")
    public String decode(@RequestParam("token") String token) {
        final var jwt = jwtDecoder.decode(token);
        final var subject = jwt.getSubject();
        final var name = jwt.getClaim("name");
        final var email = jwt.getClaim("email");
        return String.format("subject: %s, name: %s, email: %s", subject, name, email);
    }

    @GetMapping("token-info2")
    public String decode() {
        final var authentication = SecurityContextHolder.getContext().getAuthentication();
        final var token = ((JwtAuthenticationToken) authentication).getToken();
        return String.format("subject: %s, name: %s, email: %s",
                token.getSubject(),
                token.getClaim("name"),
                token.getClaim("email")
        );
    }

    private String createToken(User user) {
        final var jwtClaimsSet = JwtClaimsSet.builder()
                .issuer("middle-developer.tistory.com")
                .issuedAt(Instant.now())
                .subject(user.getId())
                .expiresAt(Instant.now().plusSeconds(60 * 60 * 24))
                .claim("name", StringUtils.isEmpty(user.getName()) ? "" : user.getName())
                .claim("email", StringUtils.isEmpty(user.getEmail()) ? "" : user.getEmail())
                .build();

        return jwtEncoder.
                encode(JwtEncoderParameters.from(jwtClaimsSet))
                .getTokenValue();
    }
}
