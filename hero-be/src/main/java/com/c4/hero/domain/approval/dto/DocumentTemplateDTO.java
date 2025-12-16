package com.c4.hero.domain.approval.dto;
/**
 * <pre>
 * Class Name  : DocumentTemplateDTO
 * Description : 전자결재 문서 템플릿 정보를 전달하기 위한 DTO
 *
 * History
 * 2025/12/15 (변민철) 최초 작성
 * </pre>
 *
 * @author 변민철
 * @version 1.0
 */
import com.c4.hero.domain.approval.entity.ApprovalFormTemplate;
import lombok.Builder;
import lombok.Data;

/**
 * 전자결재 문서 템플릿 DTO
 */
@Data
@Builder
public class DocumentTemplateDTO {

    /** 문서 템플릿 ID */
    private Integer id;

    /** 문서 템플릿 명 */
    private String name;

    /** 문서 카테고리(영어) */
    private String templateKey;

    /* 문서 카테고리(한글) */
    private String category;

    /* 문서 서식 설명 */
    private String description;

    /** 즐겨찾기 여부 */
    private boolean bookmarking;

    // Entity -> DTO 변환 메서드
    public static DocumentTemplateDTO from(ApprovalFormTemplate entity) {
        return DocumentTemplateDTO.builder()
                .id(entity.getTemplateId())
                .name(entity.getName())
                .templateKey(entity.getTemplateKey())
                .category(entity.getCategory())
                .description(entity.getDescription())
                .build();
    }
}