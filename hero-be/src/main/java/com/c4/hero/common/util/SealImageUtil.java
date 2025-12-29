package com.c4.hero.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * <pre>
 * Class Name: SealImageUtil
 * Description: 직인 이미지 생성 유틸리티
 *
 * History
 * 2025/12/29 (혜원) 최초 작성
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
@Slf4j
public class SealImageUtil {

    private static final int SEAL_SIZE = 300;
    private static final int CIRCLE_PADDING = 20;
    private static final int STROKE_WIDTH = 8;

    /**
     * 텍스트 직인 이미지 생성
     * Java AWT를 사용하여 Canvas 기반 직인 이미지 생성
     * - 크기: 300x300px
     * - 형식: PNG (투명 배경)
     * - 디자인: 빨간색 원형 테두리 + 세로 텍스트
     *
     * @param text 직인에 표시할 텍스트 (보통 이름)
     * @return PNG 형식의 이미지 바이트 배열
     * @throws IOException 이미지 생성 실패 시
     */
    public static byte[] generateSealImage(String text) throws IOException {
        log.debug("직인 이미지 생성 시작 - text: {}", text);

        // 투명 배경의 이미지 생성
        BufferedImage image = new BufferedImage(SEAL_SIZE, SEAL_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // 안티앨리어싱 설정 (부드러운 선과 텍스트)
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 빨간색 원형 그리기 (#DC2626)
        g2d.setColor(new Color(220, 38, 38));
        g2d.setStroke(new BasicStroke(STROKE_WIDTH));
        int circleDiameter = SEAL_SIZE - (CIRCLE_PADDING * 2);
        g2d.drawOval(CIRCLE_PADDING, CIRCLE_PADDING, circleDiameter, circleDiameter);

        // 텍스트 세로 배치
        g2d.setFont(new Font("맑은 고딕", Font.BOLD, 40));
        FontMetrics fm = g2d.getFontMetrics();

        int totalHeight = text.length() * 50;
        int startY = (SEAL_SIZE - totalHeight) / 2 + fm.getAscent();

        for (int i = 0; i < text.length(); i++) {
            String character = String.valueOf(text.charAt(i));
            int charWidth = fm.stringWidth(character);
            int x = (SEAL_SIZE - charWidth) / 2;
            int y = startY + (i * 50);
            g2d.drawString(character, x, y);
        }

        g2d.dispose();

        // PNG로 변환
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);

        log.debug("직인 이미지 생성 완료 - size: {} bytes", baos.size());
        return baos.toByteArray();
    }

    /**
     * byte[]를 MultipartFile로 변환
     *
     * @param content 파일 내용 (바이트 배열)
     * @param filename 파일명
     * @param contentType MIME 타입 (예: "image/png")
     * @return MultipartFile 객체
     */
    public static MultipartFile toMultipartFile(byte[] content, String filename, String contentType) {
        return new ByteArrayMultipartFile(content, filename, contentType);
    }

    /**
     * ByteArray를 MultipartFile로 변환하는 헬퍼 클래스
     */
    private static class ByteArrayMultipartFile implements MultipartFile {
        private final byte[] content;
        private final String name;
        private final String contentType;

        public ByteArrayMultipartFile(byte[] content, String name, String contentType) {
            this.content = content;
            this.name = name;
            this.contentType = contentType;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getOriginalFilename() {
            return name;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public boolean isEmpty() {
            return content == null || content.length == 0;
        }

        @Override
        public long getSize() {
            return content.length;
        }

        @Override
        public byte[] getBytes() {
            return content;
        }

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream(content);
        }

        @Override
        public void transferTo(java.io.File dest) throws IOException, IllegalStateException {
            throw new UnsupportedOperationException("transferTo is not supported");
        }
    }
}