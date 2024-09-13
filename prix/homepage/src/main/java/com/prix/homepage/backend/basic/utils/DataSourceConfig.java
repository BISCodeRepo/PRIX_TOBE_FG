package com.prix.homepage.backend.basic.utils;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    /**
     * Primary DataSource Bean을 생성합니다.
     * MySQL 데이터베이스에 연결하기 위한 정보(드라이버, URL, 사용자명, 비밀번호)를 설정합니다.
     *
     * @return DataSource - MySQL 연결을 위한 데이터 소스 객체
     */
    @Primary
    @Bean(name = "prixDataSource")
    public DataSource prixDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://166.104.110.37:3306/prix");
        dataSource.setUsername("prix");
        dataSource.setPassword("Prix2024!@");
        return dataSource;
    }
    /**
     * MyBatis의 SqlSessionFactory Bean을 생성합니다.
     * 앞서 정의한 DataSource를 사용하여 SQL 세션을 생성할 수 있는 팩토리 객체를 생성합니다.
     *
     * @param dataSource - prixDataSource를 주입받아 사용
     * @return SqlSessionFactory - MyBatis의 SQL 세션 팩토리 객체
     * @throws Exception
     */
    @Primary
    @Bean(name = "prixSqlSessionFactory")
    public SqlSessionFactory prixSqlSessionFactory(@Qualifier("prixDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        return sessionFactory.getObject();
    }
    /**
     * SqlSessionTemplate Bean을 생성합니다.
     * SqlSessionFactory를 사용하여 SQL 세션 템플릿을 생성하며, 이는 MyBatis와 데이터베이스 간의 상호작용을 간편하게 도와줍니다.
     *
     * @param sqlSessionFactory - prixSqlSessionFactory를 주입받아 사용
     * @return SqlSessionTemplate - SQL 세션 템플릿 객체
     */
    @Primary
    @Bean(name = "prixSqlSessionTemplate")
    public SqlSessionTemplate prixSqlSessionTemplate(@Qualifier("prixSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }




}
