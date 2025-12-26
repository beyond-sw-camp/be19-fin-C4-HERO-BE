package com.c4.hero.domain.approval.dto.organization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name  : OrganizationTreeResponseDTO
 * Description : 조직도 전체 응답 DTO
 *
 * History
 * 2025/12/26 (민철) 최초 작성
 *
 * </pre>
 *
 * @author 민철
 * @version 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationTreeResponseDTO {

    /** 최상위 루트 노드 (본부) */
    private OrganizationTreeNodeDTO root;
}