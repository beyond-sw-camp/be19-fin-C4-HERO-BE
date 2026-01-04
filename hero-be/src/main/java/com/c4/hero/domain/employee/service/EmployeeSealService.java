package com.c4.hero.domain.employee.service;

import com.c4.hero.common.util.SealImageUtil;
import com.c4.hero.domain.employee.dto.request.SealTextUpdateRequestDTO;
import com.c4.hero.domain.employee.mapper.EmployeeMapper;
import com.c4.hero.common.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * <pre>
 * Class Name: EmployeeSealService
 * Description: 직인 관리 서비스
 *
 * History
 * 2025/12/28 (혜원) 최초 작성
 * 2025/12/29 (혜원) 텍스트 직인 이미지 자동 생성 추가
 *
 * @author 혜원
 * @version 1.2
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeSealService {

    private final EmployeeMapper employeeMapper;
    private final S3Service s3Service;

    private static final String SEAL_DIRECTORY = "seals";

    /**
     * 텍스트 직인 업데이트
     * 직원 이름으로 직인 이미지를 생성하여 S3에 업로드하고 DB에 저장
     *
     * @param employeeId 직원 ID
     * @param requestDTO 텍스트 직인 정보
     */
    @Transactional
    public void updateSealText(Integer employeeId, SealTextUpdateRequestDTO requestDTO) {
        log.info("텍스트 직인 업데이트 시작 - employeeId: {}, text: {}", employeeId, requestDTO.getSealText());

        // 기존 이미지 직인이 있으면 삭제
        String existingSealUrl = employeeMapper.findSealImageUrlByEmployeeId(employeeId);
        if (existingSealUrl != null && !existingSealUrl.isEmpty()) {
            s3Service.deleteFile(existingSealUrl);
            log.info("기존 이미지 직인 삭제 - employeeId: {}", employeeId);
        }

        try {
            // 텍스트 직인 이미지 생성 (Util 사용)
            byte[] sealImageBytes = SealImageUtil.generateSealImage(requestDTO.getSealText());

            // ByteArray를 MultipartFile로 변환 (Util 사용)
            String filename = "seal_" + employeeId + "_" + System.currentTimeMillis() + ".png";
            MultipartFile sealFile = SealImageUtil.toMultipartFile(sealImageBytes, filename, "image/png");

            // S3에 업로드
            String s3Key = s3Service.uploadFile(sealFile, SEAL_DIRECTORY);

            // DB에 S3 키 저장
            int updated = employeeMapper.updateSealImageUrl(employeeId, s3Key);

            if (updated == 0) {
                s3Service.deleteFile(s3Key);
                log.error("텍스트 직인 업데이트 실패 - employeeId: {}", employeeId);
                throw new RuntimeException("텍스트 직인 업데이트에 실패했습니다.");
            }

            log.info("텍스트 직인 업데이트 성공 - employeeId: {}, S3 Key: {}", employeeId, s3Key);

        } catch (Exception e) {
            log.error("텍스트 직인 이미지 생성 실패 - employeeId: {}", employeeId, e);
            throw new RuntimeException("텍스트 직인 생성에 실패했습니다.", e);
        }
    }

    /**
     * 특정 직원의 직인 자동 생성
     * 직인이 없을 때 직원 이름으로 직인을 자동 생성
     *
     * @param employeeId 직원 ID
     * @throws RuntimeException 직원 정보를 찾을 수 없거나 직인 생성 실패 시
     */
    @Transactional
    public void generateSealForEmployee(Integer employeeId) {
        log.info("직인 자동 생성 요청 - employeeId: {}", employeeId);

        // 이미 직인이 있으면 skip
        String existingSealUrl = employeeMapper.findSealImageUrlByEmployeeId(employeeId);
        if (existingSealUrl != null && !existingSealUrl.isEmpty()) {
            log.info("직인이 이미 존재합니다 - employeeId: {}", employeeId);
            return;
        }

        // 직원 이름 조회
        String employeeName = employeeMapper.findEmployeeNameById(employeeId);
        if (employeeName == null || employeeName.isEmpty()) {
            log.error("직원 정보를 찾을 수 없습니다 - employeeId: {}", employeeId);
            throw new RuntimeException("직원 정보를 찾을 수 없습니다.");
        }

        // 직인 생성
        SealTextUpdateRequestDTO sealDTO = SealTextUpdateRequestDTO.builder()
                .sealText(employeeName)
                .build();

        updateSealText(employeeId, sealDTO);
        log.info("직인 자동 생성 완료 - employeeId: {}, name: {}", employeeId, employeeName);
    }

    /**
     * 이미지 직인 업로드
     * 사용자가 직접 업로드한 이미지 파일을 S3에 저장
     *
     * @param employeeId 직원 ID
     * @param file 직인 이미지 파일
     * @throws IllegalArgumentException 파일이 비어있거나 크기 초과 시
     * @throws RuntimeException 이미지 업로드 실패 시
     */
    @Transactional
    public void uploadSealImage(Integer employeeId, MultipartFile file) {
        log.info("이미지 직인 업로드 시작 - employeeId: {}, filename: {}", employeeId, file.getOriginalFilename());

        // 파일 검증
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        // 파일 크기 검증 (2MB)
        if (file.getSize() > 2 * 1024 * 1024) {
            throw new IllegalArgumentException("파일 크기는 2MB 이하여야 합니다.");
        }

        // 기존 이미지가 있으면 삭제
        String existingSealUrl = employeeMapper.findSealImageUrlByEmployeeId(employeeId);
        if (existingSealUrl != null && !existingSealUrl.isEmpty()) {
            s3Service.deleteFile(existingSealUrl);
            log.info("기존 직인 이미지 삭제 - employeeId: {}", employeeId);
        }

        // S3에 업로드 (키 반환)
        String s3Key = s3Service.uploadFile(file, SEAL_DIRECTORY);

        // DB에 S3 키 저장
        int updated = employeeMapper.updateSealImageUrl(employeeId, s3Key);

        if (updated == 0) {
            // 업데이트 실패 시 S3에서도 삭제
            s3Service.deleteFile(s3Key);
            log.error("이미지 직인 업데이트 실패 - employeeId: {}", employeeId);
            throw new RuntimeException("이미지 직인 업데이트에 실패했습니다.");
        }

        log.info("이미지 직인 업로드 성공 - employeeId: {}, S3 Key: {}", employeeId, s3Key);
    }

    /**
     * 직인 삭제
     * S3에서 파일 삭제 후 DB의 직인 URL을 NULL로 설정
     *
     * @param employeeId 직원 ID
     * @throws RuntimeException 직인 삭제 실패 시
     */
    @Transactional
    public void deleteSeal(Integer employeeId) {
        log.info("직인 삭제 시작 - employeeId: {}", employeeId);

        // 기존 이미지가 있으면 S3에서 삭제
        String existingSealUrl = employeeMapper.findSealImageUrlByEmployeeId(employeeId);
        if (existingSealUrl != null && !existingSealUrl.isEmpty()) {
            s3Service.deleteFile(existingSealUrl);
        }

        // DB에서 삭제
        int updated = employeeMapper.updateSealImageUrl(employeeId, null);

        if (updated == 0) {
            log.error("직인 삭제 실패 - employeeId: {}", employeeId);
            throw new RuntimeException("직인 삭제에 실패했습니다.");
        }

        log.info("직인 삭제 성공 - employeeId: {}", employeeId);
    }

    /**
     * 직원 직인 이미지 URL 조회
     * S3 키를 Presigned URL로 변환하여 반환
     *
     * @param employeeId 직원 ID
     * @return 직인 이미지 Presigned URL (없으면 null)
     */
    @Transactional(readOnly = true)
    public String getEmployeeSealUrl(Integer employeeId) {
        log.info("직인 이미지 URL 조회 - employeeId: {}", employeeId);

        // DB에서 S3 키 조회
        String s3Key = employeeMapper.findSealImageUrlByEmployeeId(employeeId);

        // 직인이 없으면 null 반환
        if (s3Key == null || s3Key.isEmpty()) {
            log.info("직인이 없습니다 - employeeId: {}", employeeId);
            return null;
        }

        // S3 Presigned URL 생성 (7일 유효)
        String presignedUrl = s3Service.generatePresignedUrl(s3Key);
        log.info("직인 Presigned URL 생성 완료 - employeeId: {}", employeeId);

        return presignedUrl;
    }
}