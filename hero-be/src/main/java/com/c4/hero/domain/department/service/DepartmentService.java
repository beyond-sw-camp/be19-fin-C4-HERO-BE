package com.c4.hero.domain.department.service;

import com.c4.hero.domain.department.dto.DepartmentDTO;
import com.c4.hero.domain.department.repository.DepartmentRepository;
import com.c4.hero.domain.employee.entity.EmployeeDepartment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public List<DepartmentDTO> getDepartments() {
        List<EmployeeDepartment> departments = departmentRepository.findAll();

        return departments.stream()
                .map(d -> new DepartmentDTO(d.getDepartmentId(), d.getDepartmentName()))
                .toList();
    }
}
