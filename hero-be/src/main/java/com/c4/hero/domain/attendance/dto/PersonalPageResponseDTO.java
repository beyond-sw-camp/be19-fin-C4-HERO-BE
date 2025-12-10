package com.c4.hero.domain.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PersonalPageResponseDTO {

    private List<PersonalDTO> items;
    private int page;
    private int size;
    private int totalCount;
    private int totalPages;
}
