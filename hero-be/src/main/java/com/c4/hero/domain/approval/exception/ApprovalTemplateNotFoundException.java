package com.c4.hero.domain.approval.exception;

import com.c4.hero.common.exception.BusinessException;
import com.c4.hero.common.exception.ErrorCode;

/**
 * <pre>
 * Class Name  : ApprovalTemplateNotFoundException
 * Description : 결재 양식 미존재 예외
 *               요청한 템플릿 ID 또는 템플릿 키에 해당하는 양식을 찾을 수 없을 때 발생
 *
 * 발생 시점:
 *   - 존재하지 않는 템플릿 ID로 조회를 시도할 때
 *   - 존재하지 않는 템플릿 키(formType)로 문서를 생성하려고 할 때
 *   - 이미 삭제된 템플릿에 접근하려고 할 때
 *
 * History
 *   2026/01/05 (민철) 클래스 설명 추가
 * </pre>
 *
 * @author 민철
 * @version 1.0
 */
public class ApprovalTemplateNotFoundException extends BusinessException {
    public ApprovalTemplateNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ApprovalTemplateNotFoundException(Integer templateId) {
        super(ErrorCode.TEMPLATE_NOT_FOUND,  templateId + "번 문서를 찾을 수 없습니다.");
    }
}