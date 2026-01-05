package com.c4.hero.domain.approval.exception;

import com.c4.hero.common.exception.BusinessException;
import com.c4.hero.common.exception.ErrorCode;

/**
 * <pre>
 * Class Name  : InvalidApprovalStateException
 * Description : 잘못된 결재 상태 예외
 *               현재 문서/결재선의 상태에서 수행할 수 없는 작업을 시도할 때 발생
 *
 * 발생 시점:
 *   - DRAFT 상태가 아닌 문서를 수정하려고 할 때
 *   - INPROGRESS 상태가 아닌 문서를 회수하려고 할 때
 *   - 유효하지 않은 결재 액션(APPROVE, REJECT 외)을 시도할 때
 *   - 반려 시 반려 사유가 누락되었을 때
 *   - 이미 처리된 결재선(PENDING 아님)을 다시 처리하려고 할 때
 *
 * History
 *   2026/01/05 (민철) 클래스 설명 추가
 * </pre>
 *
 * @author 민철
 * @version 1.0
 */
public class InvalidApprovalStateException extends BusinessException {

    public InvalidApprovalStateException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InvalidApprovalStateException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}