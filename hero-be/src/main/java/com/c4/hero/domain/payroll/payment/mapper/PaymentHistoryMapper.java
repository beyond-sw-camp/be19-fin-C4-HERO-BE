package com.c4.hero.domain.payroll.payment.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PaymentHistoryMapper {

    /**
     * 특정 bankAccountId를 참조하는 지급 이력이 존재하는지 확인
     * @return 존재하면 1, 없으면 0
     */
    int existsByBankAccountId(@Param("bankAccountId") Integer bankAccountId);
}
