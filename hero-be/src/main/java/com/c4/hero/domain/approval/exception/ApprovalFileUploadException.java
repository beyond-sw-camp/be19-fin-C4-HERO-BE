package com.c4.hero.domain.approval.exception;

import com.c4.hero.common.exception.BusinessException;
import com.c4.hero.common.exception.ErrorCode;

/**
 * <pre>
 * Class Name  : ApprovalFileUploadException
 * Description : 첨부파일 업로드 실패 예외
 *               S3에 파일을 업로드하는 중 오류가 발생했을 때 발생
 *
 * 발생 시점:
 *   - S3 파일 업로드 API 호출 실패 시
 *   - 네트워크 오류로 S3 접근 불가 시
 *   - 파일 크기가 제한을 초과했을 때
 *   - 허용되지 않는 파일 형식을 업로드할 때
 *   - S3 버킷 용량 부족 시
 *
 * History
 *   2026/01/05 (민철) 클래스 설명 추가
 * </pre>
 *
 * @author 민철
 * @version 1.0
 */
public class ApprovalFileUploadException extends BusinessException {
    public ApprovalFileUploadException(ErrorCode errorCode) {
        super(errorCode);
    }
}