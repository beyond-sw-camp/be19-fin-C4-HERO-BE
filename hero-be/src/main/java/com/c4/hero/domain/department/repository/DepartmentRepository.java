package com.c4.hero.domain.department.repository;

import com.c4.hero.domain.employee.entity.EmployeeDepartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DepartmentRepository extends JpaRepository<EmployeeDepartment, Integer> {
}
