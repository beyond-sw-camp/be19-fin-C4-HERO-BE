package com.c4.hero.domain.approval.exception;

import com.c4.hero.common.exception.EntityNotFoundException;
import com.c4.hero.common.exception.ErrorCode;

/**
 * <pre>
 * Class Name  : ApprovalDocumentNotFoundException
 * Description : 결재 문서 미존재 예외
 *               요청한 문서 ID에 해당하는 문서를 찾을 수 없을 때 발생
 *
 * 발생 시점:
 *   - 존재하지 않는 문서 ID로 조회를 시도할 때
 *   - 이미 삭제된 문서에 접근하려고 할 때
 *   - 잘못된 문서 ID 파라미터로 요청했을 때
 *
 * History
 *   2026/01/05 (민철) 클래스 설명 추가
 * </pre>
 *
 * @author 민철
 * @version 1.0
 */
public class ApprovalDocumentNotFoundException extends EntityNotFoundException {

    public ApprovalDocumentNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ApprovalDocumentNotFoundException(Integer docId) {
        super(ErrorCode.DOCUMENT_NOT_FOUND, docId + "번 문서를 찾을 수 없습니다.");
    }

    public ApprovalDocumentNotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}