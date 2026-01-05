package com.c4.hero.common.exception;

/**
 * <pre>
 * Class Name  : EntityNotFoundException
 * Description : 엔티티 미존재 공통 예외
 *               데이터베이스에서 요청한 엔티티를 찾을 수 없을 때 발생하는 최상위 예외
 *               도메인별 NotFound 예외들의 부모 클래스로 사용
 *
 * 발생 시점:
 *   - 존재하지 않는 ID로 엔티티를 조회할 때
 *   - 이미 삭제된 엔티티에 접근할 때
 *   - 잘못된 참조 키로 조회를 시도할 때
 *
 * 사용 예:
 *   - ApprovalDocumentNotFoundException extends EntityNotFoundException
 *   - EmployeeNotFoundException extends EntityNotFoundException
 *
 * History
 *   2026/01/05 (민철) EntityNotFoundException 생성
 * </pre>
 *
 * @author 민철
 * @version 1.0
 */
public class EntityNotFoundException extends BusinessException {

    public EntityNotFoundException() {
        super(ErrorCode.ENTITY_NOT_FOUND);
    }

    public EntityNotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public EntityNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}