package com.c4.hero.domain.approval.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_exit_reason")
@Getter
@NoArgsConstructor
public class ApprovalResignType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exit_reason_id")
    private Integer resignTypeId;

    @Column(name = "reason_name")
    private String resignTypeName;

}
