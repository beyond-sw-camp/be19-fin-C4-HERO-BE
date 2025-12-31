package com.c4.hero.domain.payroll.adjustment.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PayrollRaiseCommandMapper {
    int updateEmployeeBaseSalary(
            @Param("employeeId") Integer employeeId,
            @Param("baseSalary") Integer baseSalary
    );
}
