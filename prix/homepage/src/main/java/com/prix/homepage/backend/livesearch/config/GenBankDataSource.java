package com.prix.homepage.backend.livesearch.config;

import com.prix.homepage.backend.livesearch.mapper.PatternMatch.GenbankMapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@MapperScan(value = "com.prix.homepage.backend.livesearch.mapper.PatternMatch")
public class GenBankDataSource {

    /**
     * genbank 데이터베이스에 연결할 수 있도록 DataSource를 설정합니다.
     * MySQL 데이터베이스를 사용하며, GenBank 데이터베이스에 대한 URL, 사용자명, 비밀번호 등의 정보를 설정합니다.
     *
     * @return DataSource - genBank 데이터베이스 연결을 위한 DataSource 객체
     */
    @Bean(name = "genbankDataSrc")
    public DataSource genbankDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://166.104.110.37:3306/genbank");
        dataSource.setUsername("prix");
        dataSource.setPassword("Prix2024!@");
        return dataSource;
    }


    /**
     * MyBatis의 SqlSessionFactory를 설정합니다.
     * DataSource를 주입받아 GenBank 데이터베이스와의 SQL 세션을 생성할 수 있는 팩토리 객체를 생성합니다.
     *
     * @param dataSource - GenBank 데이터베이스 연결을 위한 DataSource
     * @return SqlSessionFactory - MyBatis에서 사용할 SQL 세션 팩토리 객체
     * @throws Exception - SQL 세션 팩토리 생성 시 예외 발생 가능
     */
    @Bean(name = "genbankSqlSessionFactory")
    public SqlSessionFactory genbankSqlSessionFactory(@Qualifier("genbankDataSrc") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        return sessionFactory.getObject();
    }

    /**
     * SqlSessionTemplate을 설정합니다.
     * SqlSessionFactory를 주입받아 MyBatis의 SQL 세션 템플릿을 생성하고, 이를 통해 SQL 쿼리 실행 및 트랜잭션 관리를 처리합니다.
     * 또한 GenbankMapper 인터페이스를 등록하여 매핑할 수 있도록 합니다.
     *
     * @param sqlSessionFactory - GenBank 데이터베이스와의 SQL 세션을 생성하기 위한 팩토리 객체
     * @return SqlSessionTemplate - MyBatis에서 사용할 SQL 세션 템플릿 객체
     */
    @Bean(name = "genbankSqlSessionTemplate")
    public SqlSessionTemplate genbankSqlSessionTemplate(@Qualifier("genbankSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
        sqlSessionTemplate.getConfiguration().addMapper(GenbankMapper.class);
        return sqlSessionTemplate;
    }
}
