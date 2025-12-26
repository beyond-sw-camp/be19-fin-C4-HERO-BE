package com.c4.hero.domain.approval.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tbl_document_templates")
public class ApprovalTemplates {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String title;

    private String category;

    private boolean bookmarking;
}
