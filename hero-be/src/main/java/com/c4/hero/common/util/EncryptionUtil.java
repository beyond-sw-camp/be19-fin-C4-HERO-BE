package com.c4.hero.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * <pre>
 * Class Name: EncryptionUtil
 * Description: 데이터 암호화/복호화 유틸리티
 *
 * - AES-128-ECB 알고리즘 사용 (MariaDB 호환)
 * - 개인정보(이메일, 전화번호, 주소 등) 암호화
 * - DB에 저장 시 VARBINARY 타입으로 저장
 *
 * 주의: SECRET_KEY는 반드시 환경변수로 관리할 것
 *
 * History
 * 2025/11/28 (혜원) 최초 작성
 * 2025/12/30 (승건) mariaDB의 AES 로직과 호환되도록 수정
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
@Slf4j
@Component
public class EncryptionUtil {

    // MariaDB aes-128-ecb와 호환되는 설정
    // Java에서는 AES/ECB/PKCS5Padding을 사용
    private final String ALGORITHM = "AES/ECB/PKCS5Padding";
    private final String SECRET_KEY;

    public EncryptionUtil(
            @Value("${encryption.secret-key}") String secretKey
    ) {
        SECRET_KEY = secretKey;
    }

    /**
     * 문자열을 AES 암호화하여 바이트 배열로 반환
     * DB의 VARBINARY 컬럼에 저장하기 위한 용도
     *
     * @param plainText 암호화할 평문
     * @return 암호화된 바이트 배열
     * @throws RuntimeException 암호화 실패 시
     */
    public byte[] encrypt(String plainText) {

        if(plainText == null) return null;

        try {
            // 암호화 키 생성 (128비트 = 16바이트로 조정)
            SecretKeySpec secretKey = getSecretKeySpec();

            // Cipher 인스턴스 생성 및 암호화 모드 설정
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            // 평문을 바이트로 변환 후 암호화
            return cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("암호화 실패", e);
        }
    }

    /**
     * 암호화된 바이트 배열을 복호화하여 원본 문자열 반환
     * DB의 VARBINARY 컬럼에서 읽은 데이터를 복호화하기 위한 용도
     *
     * @param encryptedData 암호화된 바이트 배열
     * @return 복호화된 평문
     * @throws RuntimeException 복호화 실패 시
     */
    public String decrypt(byte[] encryptedData) {
        if (encryptedData == null) return null;
        try {
            // 복호화 키 생성 (128비트 = 16바이트로 조정)
            SecretKeySpec secretKey = getSecretKeySpec();

            // Cipher 인스턴스 생성 및 복호화 모드 설정
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedData = cipher.doFinal(encryptedData);
            return new String(decryptedData, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("복호화 실패", e);
        }
    }

    /**
     * 문자열을 AES 암호화 후 Base64 인코딩하여 문자열로 반환
     * 테스트나 로그 출력 시 사용 (실제 DB 저장에는 encrypt() 사용 권장)
     *
     * @param plainText 암호화할 평문
     * @return Base64 인코딩된 암호화 문자열
     */
    public String encryptToBase64(String plainText) {
        byte[] encrypted = encrypt(plainText);
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * Base64 인코딩된 암호화 문자열을 디코딩 후 복호화
     * encryptToBase64()로 암호화한 데이터를 복호화할 때 사용
     *
     * @param base64Encrypted Base64 인코딩된 암호화 문자열
     * @return 복호화된 평문
     */
    public String decryptFromBase64(String base64Encrypted) {
        byte[] decoded = Base64.getDecoder().decode(base64Encrypted);
        return decrypt(decoded);
    }

    /**
     * MariaDB 호환 키 생성 메서드
     * 키 길이가 16바이트(128비트)가 되도록 조정
     * - 짧으면 0으로 패딩
     * - 길면 XOR 연산으로 압축 (MariaDB 방식)
     */
    private SecretKeySpec getSecretKeySpec() {
        byte[] keyBytes = new byte[16]; // 128비트
        byte[] sourceKey = SECRET_KEY.getBytes(StandardCharsets.UTF_8);

        for (int i = 0; i < sourceKey.length; i++) {
            keyBytes[i % 16] ^= sourceKey[i];
        }

        return new SecretKeySpec(keyBytes, "AES");
    }
}