package com.c4.hero.domain.retirement.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_exit_reason")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExitReasonMaster {

    @Id
    @Column(name = "exit_reason_id")
    private Integer exitReasonId;

    @Column(name = "reason_name", nullable = false, length = 50)
    private String reasonName;

    @Column(name = "reason_type", nullable = false, length = 20)
    private String reasonType;

    @Column(name = "detail_description", length = 255)
    private String detailDescription;

    @Column(name = "active_yn", nullable = false)
    private Integer activeYn;
}
