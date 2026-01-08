package com.c4.hero.domain.employee.service;

import com.c4.hero.common.s3.S3Service;
import com.c4.hero.domain.employee.dto.request.ContactUpdateRequestDTO;
import com.c4.hero.domain.employee.dto.response.EmployeeProfileResponseDTO;
import com.c4.hero.domain.employee.mapper.EmployeeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <pre>
 * Class Name: EmployeeProfileQueryService
 * Description: 직원 프로필 서비스
 *              - 마이페이지에서 사용
 *
 * History
 * 2025/12/28 (혜원) 최초 작성
 * 2025/12/30 (승건) Mapper 메소드에 SecretKey 추가
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeProfileQueryService {

    private final EmployeeMapper employeeMapper;
    private final S3Service s3Service;

    @Value("${encryption.secret-key}")
    private String secretKey;

    // 모든 조회 메서드에서 Presigned URL 변환
    private void convertSealKeyToPresignedUrl(EmployeeProfileResponseDTO profile) {
        if(profile.getProfileImageUrl() != null) {
            String presignedUrl = s3Service.generatePresignedUrl(profile.getProfileImageUrl());
            profile.setProfileImageUrl(presignedUrl);
        }
        if (profile.getSealImageUrl() != null) {
            String presignedUrl = s3Service.generatePresignedUrl(profile.getSealImageUrl());
            profile.setSealImageUrl(presignedUrl);
        }
    }
    /**
     * 직원 ID로 프로필 정보 조회
     *
     * @param employeeId 직원 ID
     * @return EmployeeProfileResponseDTO 직원 프로필 정보
     * @throws IllegalArgumentException 직원 정보를 찾을 수 없는 경우
     */
    public EmployeeProfileResponseDTO getProfileByEmployeeId(Integer employeeId) {
        log.info("직원 프로필 조회 시작 - employeeId: {}", employeeId);

        EmployeeProfileResponseDTO profile = employeeMapper.findProfileByEmployeeId(employeeId, secretKey);

        if (profile == null) {
            log.error("직원 정보를 찾을 수 없음 - employeeId: {}", employeeId);
            throw new IllegalArgumentException("직원 정보를 찾을 수 없습니다. (ID: " + employeeId + ")");
        }


        convertSealKeyToPresignedUrl(profile);

        log.info("직원 프로필 조회 성공 - employeeNumber: {}, employeeName: {}",
                profile.getEmployeeNumber(), profile.getEmployeeName());

        return profile;
    }

    /**
     * 사원번호로 프로필 정보 조회
     *
     * @param employeeNumber 사원번호
     * @return EmployeeProfileResponseDTO 직원 프로필 정보
     * @throws IllegalArgumentException 직원 정보를 찾을 수 없는 경우
     */
    public EmployeeProfileResponseDTO getProfileByEmployeeNumber(String employeeNumber) {
        log.info("직원 프로필 조회 시작 - employeeNumber: {}", employeeNumber);

        if (employeeNumber == null || employeeNumber.trim().isEmpty()) {
            log.error("사원번호가 비어있음");
            throw new IllegalArgumentException("사원번호는 필수입니다.");
        }

        EmployeeProfileResponseDTO profile = employeeMapper.findProfileByEmployeeNumber(employeeNumber, secretKey);

        if (profile == null) {
            log.error("직원 정보를 찾을 수 없음 - employeeNumber: {}", employeeNumber);
            throw new IllegalArgumentException("직원 정보를 찾을 수 없습니다. (사원번호: " + employeeNumber + ")");
        }

        convertSealKeyToPresignedUrl(profile);

        log.info("직원 프로필 조회 성공 - employeeName: {}", profile.getEmployeeName());

        return profile;
    }

    /**
     * 연락처 정보 수정
     *
     * @param employeeId 직원 ID
     * @param requestDTO 수정할 연락처 정보
     * @throws IllegalArgumentException 직원 정보를 찾을 수 없는 경우
     */
    @Transactional
    public void updateContactInfo(Integer employeeId, ContactUpdateRequestDTO requestDTO) {
        log.info("연락처 정보 수정 시작 - employeeId: {}", employeeId);

        // 직원 존재 여부 확인
        EmployeeProfileResponseDTO profile = employeeMapper.findProfileByEmployeeId(employeeId, secretKey);
        if (profile == null) {
            log.error("직원 정보를 찾을 수 없음 - employeeId: {}", employeeId);
            throw new IllegalArgumentException("직원 정보를 찾을 수 없습니다.");
        }

        // 연락처 정보 업데이트
        int updated = employeeMapper.updateContactInfo(employeeId, requestDTO, secretKey);

        if (updated == 0) {
            log.error("연락처 정보 수정 실패 - employeeId: {}", employeeId);
            throw new RuntimeException("연락처 정보 수정에 실패했습니다.");
        }

        log.info("연락처 정보 수정 성공 - employeeId: {}", employeeId);
    }
}