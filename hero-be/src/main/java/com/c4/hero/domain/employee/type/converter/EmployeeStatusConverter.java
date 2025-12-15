package com.c4.hero.domain.employee.type.converter;

import com.c4.hero.domain.employee.type.EmployeeStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.stream.Stream;

/**
 * <pre>
 * Class Name: EmployeeStatusConverter
 * Description: EmployeeStatus Enum을 데이터베이스의 코드 값과 변환하는 컨버터
 *
 * History
 * 2025/12/09 이승건 최초 작성
 * </pre>
 *
 * @author 이승건
 * @version 1.0
 */
@Converter(autoApply = true)
public class EmployeeStatusConverter implements AttributeConverter<EmployeeStatus, String> {

    /**
     * EmployeeStatus Enum을 DB에 저장될 문자열 코드로 변환합니다.
     * {@inheritDoc}
     *
     * @param status 변환할 Enum (e.g., EmployeeStatus.ACTIVE)
     * @return DB에 저장될 코드 값 (e.g., "A")
     */
    @Override
    public String convertToDatabaseColumn(EmployeeStatus status) {
        if (status == null) {
            return null;
        }
        return status.getCode();
    }

    /**
     * DB의 문자열 코드를 EmployeeStatus Enum으로 변환합니다.
     * {@inheritDoc}
     *
     * @param code DB에서 읽어온 코드 값 (e.g., "A")
     * @return 변환된 EmployeeStatus Enum
     */
    @Override
    public EmployeeStatus convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }
        return Stream.of(EmployeeStatus.values())
                .filter(c -> c.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
