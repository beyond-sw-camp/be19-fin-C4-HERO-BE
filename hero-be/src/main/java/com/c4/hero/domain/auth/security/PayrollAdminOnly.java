package com.c4.hero.domain.auth.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * Annotation Name : PayrollAdminOnly
 * Description     : 급여(Admin) 기능 접근을 제한하는 보안 애노테이션
 *
 * History
 *   2026/01/03 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','HR_MANAGER','HR_PAYROLL')")
public @interface PayrollAdminOnly {
}
