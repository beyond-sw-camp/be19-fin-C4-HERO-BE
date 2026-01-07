package com.c4.hero.domain.approval.exception;

import com.c4.hero.common.exception.BusinessException;
import com.c4.hero.common.exception.ErrorCode;

/**
 * <pre>
 * Class Name  : ApprovalFileDeleteException
 * Description : 첨부파일 삭제 실패 예외
 *               S3에서 파일을 삭제하는 중 오류가 발생했을 때 발생
 *
 * 발생 시점:
 *   - S3 파일 삭제 API 호출 실패 시
 *   - 네트워크 오류로 S3 접근 불가 시
 *   - 삭제 권한이 없는 파일에 접근 시
 *   - 이미 삭제된 파일을 다시 삭제하려고 할 때
 *
 * History
 *   2026/01/05 (민철) 클래스 설명 추가
 * </pre>
 *
 * @author 민철
 * @version 1.0
 */
public class ApprovalFileDeleteException extends BusinessException {
    public ApprovalFileDeleteException(ErrorCode errorCode) {
        super(errorCode);
    }
}