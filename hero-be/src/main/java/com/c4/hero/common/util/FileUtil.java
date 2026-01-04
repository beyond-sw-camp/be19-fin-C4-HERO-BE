package com.c4.hero.common.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * <pre>
 * Class Name: FileUtil
 * Description: 파일 처리 유틸리티
 *
 * - 파일 업로드/삭제
 * - 파일명 생성 (UUID 사용)
 * - 파일 크기 검증
 * - 파일 확장자 검증
 *
 * History
 * 2025/11/28 (혜원) 최초 작성
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
public class FileUtil {

    /** 기본 업로드 디렉토리 */
    private static final String UPLOAD_DIR = "./uploads/";

    /**
     * 파일 업로드
     * UUID를 포함한 고유한 파일명으로 저장
     *
     * @param file 업로드할 파일
     * @param subDir 하위 디렉토리 (예: "profile", "document")
     * @return 저장된 파일 경로 (예: "profile/uuid_filename.jpg")
     * @throws IOException 파일 저장 실패 시
     */
    public static String uploadFile(MultipartFile file, String subDir) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        // 디렉토리 생성 (없으면 자동 생성)
        String uploadPath = UPLOAD_DIR + subDir + "/";
        File directory = new File(uploadPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 고유한 파일명 생성 (UUID + 원본파일명)
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String savedFilename = UUID.randomUUID().toString() + extension;

        // 파일 저장
        Path filePath = Paths.get(uploadPath + savedFilename);
        Files.write(filePath, file.getBytes());

        return subDir + "/" + savedFilename;
    }

    /**
     * 날짜별 폴더 구조로 파일 업로드
     * 업로드 경로: baseDir/YYYY/MM/DD/uuid_filename.ext
     *
     * @param file 업로드할 파일
     * @param baseDir 기본 디렉토리 (예: "attachments")
     * @return 저장된 파일 경로 (예: "attachments/2024/11/28/uuid_filename.pdf")
     * @throws IOException 파일 저장 실패 시
     */
    public static String uploadFileByDate(MultipartFile file, String baseDir) throws IOException {
        LocalDateTime now = LocalDateTime.now();
        String datePath = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return uploadFile(file, baseDir + "/" + datePath);
    }

    /**
     * 파일 삭제
     *
     * @param filePath 삭제할 파일 경로 (UPLOAD_DIR 기준 상대 경로)
     * @return 삭제 성공 여부
     */
    public static boolean deleteFile(String filePath) {
        try {
            Path path = Paths.get(UPLOAD_DIR + filePath);
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 파일 확장자 추출
     *
     * @param filename 파일명
     * @return 확장자 (예: ".jpg", ".pdf")
     */
    private static String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    /**
     * 파일 크기 검증
     *
     * @param file 검증할 파일
     * @param maxSizeMB 최대 파일 크기 (MB)
     * @return 파일 크기가 제한 이하인지 여부
     */
    public static boolean validateFileSize(MultipartFile file, long maxSizeMB) {
        long maxSizeBytes = maxSizeMB * 1024 * 1024;
        return file.getSize() <= maxSizeBytes;
    }

    /**
     * 파일 확장자 검증
     * 허용된 확장자 목록에 포함되는지 확인
     *
     * @param filename 파일명
     * @param allowedExtensions 허용된 확장자 배열 (예: [".jpg", ".png", ".pdf"])
     * @return 허용된 확장자인지 여부
     */
    public static boolean validateFileExtension(String filename, String[] allowedExtensions) {
        String extension = getFileExtension(filename).toLowerCase();
        for (String allowed : allowedExtensions) {
            if (extension.equals(allowed.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}