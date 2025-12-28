package com.c4.hero.domain.approval.dto.response;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VacationTypeResponseDTO {

    Integer vacationTypeId;
    String vacationTypeName;
}
