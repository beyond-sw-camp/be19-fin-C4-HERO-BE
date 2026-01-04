package com.c4.hero.domain.employee.type.converter;

import com.c4.hero.domain.employee.type.AccountStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.stream.Stream;

/**
 * <pre>
 * Class Name: AccountStatusConverter
 * Description: AccountStatus Enum을 데이터베이스의 코드 값과 변환하는 컨버터
 *
 * History
 * 2025/12/09 이승건 최초 작성
 * </pre>
 *
 * @author 이승건
 * @version 1.0
 */
@Converter(autoApply = true)
public class AccountStatusConverter implements AttributeConverter<AccountStatus, String> {

    /**
     * AccountStatus Enum을 DB에 저장될 문자열 코드로 변환합니다.
     * {@inheritDoc}
     *
     * @param status 변환할 Enum (e.g., AccountStatus.ACTIVE)
     * @return DB에 저장될 코드 값 (e.g., "ACTIVE")
     */
    @Override
    public String convertToDatabaseColumn(AccountStatus status) {
        if (status == null) {
            return null;
        }
        return status.getCode();
    }

    /**
     * DB의 문자열 코드를 AccountStatus Enum으로 변환합니다.
     * {@inheritDoc}
     *
     * @param code DB에서 읽어온 코드 값 (e.g., "ACTIVE")
     * @return 변환된 AccountStatus Enum
     */
    @Override
    public AccountStatus convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }
        return Stream.of(AccountStatus.values())
                .filter(c -> c.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
