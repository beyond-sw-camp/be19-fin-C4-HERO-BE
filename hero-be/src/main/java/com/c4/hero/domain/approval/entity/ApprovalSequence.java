package com.c4.hero.domain.approval.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "tbl_approval_sequence")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ApprovalSequence {

    @Id
    private String seqType; // 예: HERO-2025

    private Long currentVal; // 예: 105

    // 비즈니스 로직: 번호 증가
    public void increment() {
        this.currentVal++;
    }
}