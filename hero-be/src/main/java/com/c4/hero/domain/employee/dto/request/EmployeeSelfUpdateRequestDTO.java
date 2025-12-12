package com.c4.hero.domain.employee.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class EmployeeSelfUpdateRequestDTO {
    private String email;
    private String phone;
    private String address;
    private LocalDate birthDate;
}
