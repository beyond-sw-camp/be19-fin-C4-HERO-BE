package com.c4.hero.domain.employee.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <pre>
 * Class Name: JobTitle
 * Description: 직책 정보를 담는 엔티티 클래스 (예: 팀장, 파트장)
 *
 * History
 * 2025/12/09 이승건 최초 작성
 * </pre>
 *
 * @author 이승건
 * @version 1.0
 */
@Entity
@Table(name = "tbl_job_title")
@Getter
@Setter
@NoArgsConstructor
public class JobTitle {

    /**
     * 직책 ID (PK)
     */
    @Id
    @Column(name = "job_title_id")
    private Integer jobTitleId;

    /**
     * 직책명 (예: 팀장, 파트장)
     */
    @Column(name = "job_title", nullable = false, length = 50)
    private String jobTitle;

    @Builder
    public JobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }
}
