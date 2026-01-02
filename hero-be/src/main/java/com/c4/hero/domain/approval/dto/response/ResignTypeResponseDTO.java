package com.c4.hero.domain.approval.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResignTypeResponseDTO {

    private Integer resignTypeId;
    private String resignTypeName;
}
