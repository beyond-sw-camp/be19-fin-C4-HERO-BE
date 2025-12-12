package com.c4.hero.domain.employee.type.handler;

import com.c4.hero.domain.employee.type.EmployeeStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// @MappedTypes(EmployeeStatus.class) 어노테이션을 사용하여 등록할 수도 있습니다.
public class EmployeeStatusTypeHandler extends BaseTypeHandler<EmployeeStatus> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, EmployeeStatus parameter, JdbcType jdbcType) throws java.sql.SQLException {
        // Java Enum(ACTIVE) -> DB String("A") 변환 (Mapper에서 쿼리 파라미터로 사용될 때)
        ps.setString(i, parameter.getCode());
    }

    @Override
    public EmployeeStatus getNullableResult(ResultSet rs, String columnName) throws java.sql.SQLException {
        // DB String("A") -> Java Enum(ACTIVE) 변환 (조회 결과)
        String code = rs.getString(columnName);
        if (code == null) {
            return null;
        }
        // EmployeeStatusConverter의 로직 재사용
        return EmployeeStatus.fromCode(code); // 아래 EmployeeStatus에 fromCode 메서드를 추가해야 함
    }

    @Override
    public EmployeeStatus getNullableResult(ResultSet rs, int columnIndex) throws java.sql.SQLException {
        // 오버로드 메서드 구현
        String code = rs.getString(columnIndex);
        if (code == null) {
            return null;
        }
        return EmployeeStatus.fromCode(code);
    }

    @Override
    public EmployeeStatus getNullableResult(CallableStatement cs, int columnIndex) throws java.sql.SQLException {
        // 오버로드 메서드 구현
        String code = cs.getString(columnIndex);
        if (code == null) {
            return null;
        }
        return EmployeeStatus.fromCode(code);
    }
}
