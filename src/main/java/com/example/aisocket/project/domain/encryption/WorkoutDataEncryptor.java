package com.example.aisocket.project.domain.encryption;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public final class WorkoutDataEncryptor {

    private static final String KEY_ENV = "WORKOUT_DATA_ENCRYPTION_KEY_BASE64";
    private static final String LOCAL_DEVELOPMENT_KEY_SEED = "local-workout-data-encryption-key";
    private static final String ALGORITHM = "AES-256-GCM";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String KEY_ID = "workout-data-key-v1";
    private static final int IV_BYTES = 12;
    private static final int TAG_BITS = 128;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final SecretKeySpec SECRET_KEY = new SecretKeySpec(resolveKey(), "AES");

    private WorkoutDataEncryptor() {
    }

    public static String encrypt(String plaintext) {
        if (plaintext == null) {
            return null;
        }

        byte[] iv = new byte[IV_BYTES];
        SECURE_RANDOM.nextBytes(iv);

        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY, new GCMParameterSpec(TAG_BITS, iv));
            byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            int tagBytes = TAG_BITS / Byte.SIZE;
            int ciphertextLength = encrypted.length - tagBytes;
            byte[] ciphertext = new byte[ciphertextLength];
            byte[] tag = new byte[tagBytes];
            System.arraycopy(encrypted, 0, ciphertext, 0, ciphertextLength);
            System.arraycopy(encrypted, ciphertextLength, tag, 0, tagBytes);

            return OBJECT_MAPPER.writeValueAsString(new EncryptedEnvelope(
                    ALGORITHM,
                    KEY_ID,
                    Base64.getEncoder().encodeToString(iv),
                    Base64.getEncoder().encodeToString(ciphertext),
                    Base64.getEncoder().encodeToString(tag)
            ));
        } catch (GeneralSecurityException | JsonProcessingException exception) {
            throw new IllegalStateException("운동 민감 데이터 암호화에 실패했습니다.", exception);
        }
    }

    public static String decrypt(String encryptedEnvelope) {
        if (encryptedEnvelope == null) {
            return null;
        }
        if (!encryptedEnvelope.trim().startsWith("{")) {
            return encryptedEnvelope;
        }

        try {
            EncryptedEnvelope envelope = OBJECT_MAPPER.readValue(encryptedEnvelope, EncryptedEnvelope.class);
            if (!ALGORITHM.equals(envelope.alg())) {
                throw new IllegalArgumentException("지원하지 않는 운동 데이터 암호화 알고리즘입니다.");
            }

            byte[] ciphertext = Base64.getDecoder().decode(envelope.ciphertext());
            byte[] tag = Base64.getDecoder().decode(envelope.tag());
            byte[] encrypted = new byte[ciphertext.length + tag.length];
            System.arraycopy(ciphertext, 0, encrypted, 0, ciphertext.length);
            System.arraycopy(tag, 0, encrypted, ciphertext.length, tag.length);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(
                    Cipher.DECRYPT_MODE,
                    SECRET_KEY,
                    new GCMParameterSpec(TAG_BITS, Base64.getDecoder().decode(envelope.iv()))
            );
            return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
        } catch (GeneralSecurityException | JsonProcessingException exception) {
            throw new IllegalStateException("운동 민감 데이터 복호화에 실패했습니다.", exception);
        }
    }

    private static byte[] resolveKey() {
        String configuredKey = System.getenv(KEY_ENV);
        if (configuredKey != null && !configuredKey.isBlank()) {
            byte[] decoded = Base64.getDecoder().decode(configuredKey);
            if (decoded.length != 32) {
                throw new IllegalStateException(KEY_ENV + "는 32바이트 AES-256 키를 Base64로 인코딩한 값이어야 합니다.");
            }
            return decoded;
        }
        if (isProductionProfile()) {
            throw new IllegalStateException("운영 환경에서는 " + KEY_ENV + " 설정이 필요합니다.");
        }
        return sha256(LOCAL_DEVELOPMENT_KEY_SEED);
    }

    private static boolean isProductionProfile() {
        String activeProfiles = System.getenv("SPRING_PROFILES_ACTIVE");
        return activeProfiles != null && activeProfiles.contains("prod");
    }

    private static byte[] sha256(String value) {
        try {
            return MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8));
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("로컬 운동 데이터 암호화 키 생성에 실패했습니다.", exception);
        }
    }

    private record EncryptedEnvelope(
            String alg,
            String keyId,
            String iv,
            String ciphertext,
            String tag
    ) {
    }
}
