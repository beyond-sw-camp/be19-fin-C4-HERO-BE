package com.c4.hero.domain.employee.type.converter;

import com.c4.hero.domain.employee.type.RoleType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.stream.Stream;

/**
 * <pre>
 * Class Name: RoleTypeConverter
 * Description: RoleType Enum을 데이터베이스의 코드 값과 변환하는 컨버터
 *
 * History
 * 2025/12/09 이승건 최초 작성
 * </pre>
 *
 * @author 이승건
 * @version 1.0
 */
@Converter(autoApply = true)
public class RoleTypeConverter implements AttributeConverter<RoleType, String> {

    /**
     * RoleType Enum을 DB에 저장될 문자열 코드로 변환합니다.
     * {@inheritDoc}
     *
     * @param roleType 변환할 Enum (e.g., RoleType.EMPLOYEE)
     * @return DB에 저장될 코드 값 (e.g., "EMPLOYEE")
     */
    @Override
    public String convertToDatabaseColumn(RoleType roleType) {
        if (roleType == null) {
            return null;
        }
        return roleType.getCode();
    }

    /**
     * DB의 문자열 코드를 RoleType Enum으로 변환합니다.
     * {@inheritDoc}
     *
     * @param code DB에서 읽어온 코드 값 (e.g., "EMPLOYEE")
     * @return 변환된 RoleType Enum
     */
    @Override
    public RoleType convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }
        return Stream.of(RoleType.values())
                .filter(c -> c.getCode().equals(code))
                .findFirst()
//                .orElseThrow(IllegalArgumentException::new);
                .orElse(null);
    }
}
