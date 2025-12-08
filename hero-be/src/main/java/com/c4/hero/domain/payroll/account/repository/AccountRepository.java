package com.c4.hero.domain.payroll.account.repository;

import com.c4.hero.domain.payroll.account.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<AccountEntity, Integer> {

    List<AccountEntity> findByEmployeeIdOrderByCreatedAtDesc(Integer employeeId);

    List<AccountEntity> findByEmployeeId(Integer employeeId);

    boolean existsByEmployeeIdAndIsPrimary(Integer employeeId, Integer isPrimary);
}