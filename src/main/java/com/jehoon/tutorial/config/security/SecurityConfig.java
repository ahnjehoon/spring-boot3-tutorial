package com.jehoon.tutorial.config.security;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@Configuration
public class SecurityConfig {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService() {
        var userDetails = User.builder()
                .username("admin")
                .password("P@ssw0rd")
                .passwordEncoder(str -> passwordEncoder().encode(str))
                .build();
        return new InMemoryUserDetailsManager(userDetails);
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http

                // CORS CSRF 미사용처리
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)

                // 인증 활성화
                // .authorizeHttpRequests(auth -> auth
                //         .anyRequest().authenticated())

                // Form Login 활성화
                // .formLogin(Customizer.withDefaults())

                // 동일도메인 iframe 접근 활성화
                // .headers(header -> header
                //         .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))

                // Session 상태 없이 변경
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Jwt 인증 설정
                .oauth2ResourceServer(resourceServerConfig -> resourceServerConfig
                        .jwt(Customizer.withDefaults()))

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
        try {
            var keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048, new SecureRandom());
            var keyPair = keyPairGenerator.genKeyPair();
            var publicKey = (RSAPublicKey) keyPair.getPublic();
            var privateKey = (RSAPrivateKey) keyPair.getPrivate();
            var rsaKey = new RSAKey
                    .Builder(publicKey)
                    .privateKey(privateKey)
                    .keyID(UUID.randomUUID().toString())
                    .build();
            var jwkSet = new JWKSet(rsaKey);
            return new ImmutableJWKSet<>(jwkSet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
