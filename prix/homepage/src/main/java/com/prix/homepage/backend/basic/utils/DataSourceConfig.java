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

    @Primary
    @Bean(name = "prixSqlSessionFactory")
    public SqlSessionFactory prixSqlSessionFactory(@Qualifier("prixDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        return sessionFactory.getObject();
    }

    @Primary
    @Bean(name = "prixSqlSessionTemplate")
    public SqlSessionTemplate prixSqlSessionTemplate(@Qualifier("prixSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }




}
