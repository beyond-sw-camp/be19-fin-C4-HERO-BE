package com.c4.hero.domain.approval.exception;

import com.c4.hero.common.exception.BusinessException;
import com.c4.hero.common.exception.ErrorCode;

/**
 * <pre>
 * Class Name  : ApprovalDocumentNumberFailedException
 * Description : 문서 번호 생성 실패 예외
 *               문서 번호(docNo) 생성 중 오류가 발생했을 때 발생
 *
 * 발생 시점:
 *   - 시퀀스 조회 실패 시
 *   - 문서 번호 포맷팅 중 오류 발생 시
 *   - 동시성 제어 중 예상치 못한 오류 발생 시
 *
 * History
 *   2026/01/05 (민철) 클래스 설명 추가
 * </pre>
 *
 * @author 민철
 * @version 1.0
 */
public class ApprovalDocumentNumberFailedException extends BusinessException {
    public ApprovalDocumentNumberFailedException(ErrorCode errorCode) {
        super(errorCode);
    }
}