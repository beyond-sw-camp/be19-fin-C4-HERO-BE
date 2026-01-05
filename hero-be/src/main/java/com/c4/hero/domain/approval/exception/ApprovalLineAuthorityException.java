package com.c4.hero.domain.approval.exception;

import com.c4.hero.common.exception.BusinessException;
import com.c4.hero.common.exception.ErrorCode;

/**
 * <pre>
 * Class Name  : ApprovalLineAuthorityException
 * Description : 결재선 권한 예외
 *               결재 권한이 없는 사용자가 결재 처리를 시도할 때 발생
 *
 * 발생 시점:
 *   - 결재자가 아닌 사용자가 승인/반려를 시도할 때
 *   - 결재 순서가 아닌 결재자가 먼저 승인하려고 할 때
 *   - 이미 처리된 결재선을 다시 처리하려고 할 때
 *
 * History
 *   2026/01/05 (민철) 클래스 설명 추가
 * </pre>
 *
 * @author 민철
 * @version 1.0
 */
public class ApprovalLineAuthorityException extends BusinessException {
    public ApprovalLineAuthorityException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ApprovalLineAuthorityException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}