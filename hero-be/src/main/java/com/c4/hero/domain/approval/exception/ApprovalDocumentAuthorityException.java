package com.c4.hero.domain.approval.exception;

import com.c4.hero.common.exception.BusinessException;
import com.c4.hero.common.exception.ErrorCode;

/**
 * <pre>
 * Class Name  : ApprovalDocumentAuthorityException
 * Description : 결재 문서 권한 예외
 *               문서에 대한 권한이 없는 사용자가 문서를 수정/삭제/상신하려고 할 때 발생
 *
 * 발생 시점:
 *   - 작성자가 아닌 사용자가 임시저장 문서를 수정하려고 할 때
 *   - 작성자가 아닌 사용자가 임시저장 문서를 상신하려고 할 때
 *   - 작성자가 아닌 사용자가 문서를 삭제하려고 할 때
 *
 * History
 *   2026/01/05 (민철) 클래스 설명 추가
 * </pre>
 *
 * @author 민철
 * @version 1.0
 */
public class ApprovalDocumentAuthorityException extends BusinessException {
    public ApprovalDocumentAuthorityException(ErrorCode errorCode) {
        super(errorCode);
    }
}