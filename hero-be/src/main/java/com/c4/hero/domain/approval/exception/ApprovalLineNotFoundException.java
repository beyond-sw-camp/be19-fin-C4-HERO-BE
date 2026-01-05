package com.c4.hero.domain.approval.exception;

import com.c4.hero.common.exception.BusinessException;
import com.c4.hero.common.exception.ErrorCode;

/**
 * <pre>
 * Class Name  : ApprovalLineNotFoundException
 * Description : 결재선 미존재 예외
 *               요청한 결재선 ID에 해당하는 결재선을 찾을 수 없을 때 발생
 *
 * 발생 시점:
 *   - 존재하지 않는 결재선 ID로 조회를 시도할 때
 *   - 이미 삭제된 결재선에 접근하려고 할 때
 *   - 잘못된 결재선 ID 파라미터로 요청했을 때
 *
 * History
 *   2026/01/05 (민철) 클래스 설명 추가
 * </pre>
 *
 * @author 민철
 * @version 1.0
 */
public class ApprovalLineNotFoundException extends BusinessException {
    public ApprovalLineNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}