package com.c4.hero.domain.approval.repository;

import com.c4.hero.domain.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalEmployeeRepository extends JpaRepository<Employee, Integer> {
    Employee findByEmployeeId(Integer employeeId);
}
