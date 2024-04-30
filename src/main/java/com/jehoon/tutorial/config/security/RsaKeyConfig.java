package com.jehoon.tutorial.config.security;

import com.nimbusds.jose.jwk.RSAKey;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Slf4j
@Configuration
public class RsaKeyConfig {

  private final String KEY_ALGORITHM = "RSA";
  private final int KEY_SIZE = 4096;
  @Value("${public-key-path:#{null}}")
  private String PUBLIC_KEY_PATH;
  @Value("${private-key-path:#{null}}")
  private String PRIVATE_KEY_PATH;

  @Bean
  RSAKey rsaKeyPair() {
    var keyPair = init();
    return new RSAKey
        .Builder((RSAPublicKey) keyPair.getPublic())
        .privateKey((RSAPrivateKey) keyPair.getPrivate())
        .build();
  }

  public KeyPair init() {
    if (StringUtils.isEmpty(PUBLIC_KEY_PATH) || StringUtils.isEmpty(PRIVATE_KEY_PATH)) {
      return generateKeyPair();
    }

    String stringPrivateKey = getKeyFromFilePath(new ClassPathResource(PRIVATE_KEY_PATH));
    String stringPublicKey = getKeyFromFilePath(new ClassPathResource(PUBLIC_KEY_PATH));

    var publicKey = getPublicKeyFromString(stringPublicKey);
    var privateKey = getPrivateKeyFromString(stringPrivateKey);

    return new KeyPair(publicKey, privateKey);
  }

  private KeyPair generateKeyPair() {
    KeyPair result = null;
    try {
      var secureRandom = new SecureRandom();
      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
      keyPairGenerator.initialize(KEY_SIZE, secureRandom);

      result = keyPairGenerator.genKeyPair();

      log.info("PUBLIC KEY: {}", Base64.getEncoder().encodeToString(result.getPublic().getEncoded()));
      log.info("PRIVATE KEY: {}", Base64.getEncoder().encodeToString(result.getPrivate().getEncoded()));
    } catch (Exception e) {
      log.error("JwtConfiguration.generateKey ERROR", e);
    }
    return result;
  }

  private String getKeyFromFilePath(Resource filePath) {
    String stringKey = null;
    try {
      stringKey = new String(Files.readAllBytes(Paths.get(filePath.getURI())));
    } catch (IOException e) {
      log.warn("unprocessable file path: {}", filePath);
    }
    return stringKey;
  }

  private PublicKey getPublicKeyFromString(String param) {
    PublicKey publicKey = null;
    try {
      var keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(param));
      var keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
      publicKey = keyFactory.generatePublic(keySpec);
    } catch (Exception ex) {
      log.error("ERROR getPublicKeyFromString", ex);
    }
    return publicKey;
  }

  private PrivateKey getPrivateKeyFromString(String param) {
    PrivateKey privateKey = null;
    try {
      var keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(param));
      var keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
      privateKey = keyFactory.generatePrivate(keySpec);
    } catch (Exception ex) {
      log.error("ERROR getPrivateKeyFromString", ex);
    }
    return privateKey;
  }
}
