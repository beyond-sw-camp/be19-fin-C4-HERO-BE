package com.c4.hero.domain.vacation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Entity Name: VacationType
 * Description: 휴가 유형(연차, 반차, 병가 등)의 마스터 정보를 저장하는 엔티티
 *
 * History
 * 2025/12/16 (이지윤) 최초 작성 및 백엔드 코딩 컨벤션 적용
 * </pre>
 *
 * VacationLog 엔티티에서 참조하는 휴가 유형 코드/이름 정보를 관리합니다.
 *
 * @author 이지윤
 * @version 1.0
 */
@Entity
@Table(name = "tbl_vacation_type")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class VacationType {

    /** 휴가 유형 PK (식별자) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vacation_type_id")
    private Integer vacationTypeId;

    /** 휴가 유형 이름 (예: 연차, 반차, 병가 등) */
    @Column(name = "vacation_type_name")
    private String vacationTypeName;
}
