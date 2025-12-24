package com.c4.hero.domain.department.controller;

import com.c4.hero.domain.department.dto.DepartmentDTO;
import com.c4.hero.domain.department.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping("/departments")
    public List<DepartmentDTO> getDepartments() {
        return departmentService.getDepartments();
    }
}
