package com.c4.hero.common.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import javax.sql.DataSource;

/**
 * <pre>
 * Class Name: SecurityConfig
 * Description: Spring Security 보안 설정
 *
 * - JWT 기반 인증 방식 사용
 * - CORS 설정
 * - 비밀번호 암호화 설정
 *
 * History
 * 2025/11/28 (혜원) 최초 작성
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
@Configuration
@MapperScan(basePackages = "com.c4.hero.domain.**.mapper")
public class MyBatisConfig {

    /**
     * SqlSessionFactory 빈 생성
     * MyBatis의 핵심 설정 및 Mapper XML 위치 설정
     *
     * @param dataSource 데이터베이스 연결 정보
     * @return SqlSessionFactory
     * @throws Exception Mapper 설정 중 발생할 수 있는 예외
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);

        // MyBatis 동작 옵션 설정
        org.apache.ibatis.session.Configuration configuration =
                new org.apache.ibatis.session.Configuration();

        // DB 컬럼명(snake_case) → 자바 필드명(camelCase) 자동 변환
        // 예시: user_name → userName
        configuration.setMapUnderscoreToCamelCase(true);

        // null 값도 setter 메서드 호출 (기본값은 호출 안 함)
        configuration.setCallSettersOnNulls(true);

        // null 값을 DB에 저장할 때 JDBC 타입 지정
        configuration.setJdbcTypeForNull(org.apache.ibatis.type.JdbcType.NULL);

        sessionFactory.setConfiguration(configuration);

        // Mapper XML 파일 위치 설정
        // classpath:mapper/**/*.xml 패턴으로 모든 Mapper XML 파일 자동 로드
        // 주석 해제 시 사용:
         PathMatchingResourcePatternResolver resolver =
             new PathMatchingResourcePatternResolver();
         sessionFactory.setMapperLocations(
             resolver.getResources("classpath:mapper/**/*.xml")
         );

        // Type Alias 패키지 설정 (XML에서 풀 패키지명 대신 클래스명만 사용 가능)
        // 예: com.c4.hero.domain.employee.dto.EmployeeDto → EmployeeDto
        sessionFactory.setTypeAliasesPackage("com.c4.hero.domain");

        return sessionFactory.getObject();
    }
}