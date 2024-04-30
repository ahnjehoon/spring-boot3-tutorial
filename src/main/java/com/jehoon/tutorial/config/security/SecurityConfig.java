package com.jehoon.tutorial.config.security;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  private final RSAKey rsaKey;

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http

        // CORS CSRF 미사용처리
        .cors(AbstractHttpConfigurer::disable)
        .csrf(AbstractHttpConfigurer::disable)

        // Session 상태 없이 변경
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        // Jwt 인증 설정
        .oauth2ResourceServer(resourceServerConfig -> resourceServerConfig
            .jwt(Customizer.withDefaults()))

        // 동일도메인 iframe 접근 활성화
        .headers(header -> header
            .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))

        // Endpoint 권한 설정
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.GET, "/api/v3/api-docs/*").permitAll()
            .requestMatchers(HttpMethod.GET, "/swagger-ui/*").permitAll()
            .requestMatchers(HttpMethod.GET, "/h2/**").permitAll()
            .requestMatchers("/api/auth/**").permitAll()
            .anyRequest().authenticated()
        )

        .build();
  }

  @Bean
  JWKSource<SecurityContext> jwkSource() {
    return new ImmutableJWKSet<>(
        new JWKSet(this.rsaKey)
    );
  }

  @Bean
  JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
    var jwtProcessor = new DefaultJWTProcessor<>();
    var jwsKeySelector = new JWSVerificationKeySelector<>(JWSAlgorithm.Family.RSA, jwkSource);
    jwtProcessor.setJWSKeySelector(jwsKeySelector);
    return new NimbusJwtDecoder(jwtProcessor);
  }

  @Bean
  JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
    return new NimbusJwtEncoder(jwkSource);
  }
}
