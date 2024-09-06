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
    @Bean(name = "genbankDataSrc")
    public DataSource genbankDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://166.104.110.37:3306/genbank");
        dataSource.setUsername("prix");
        dataSource.setPassword("Prix2024!@");
        return dataSource;
    }

    @Bean(name = "genbankSqlSessionFactory")
    public SqlSessionFactory genbankSqlSessionFactory(@Qualifier("genbankDataSrc") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        return sessionFactory.getObject();
    }

    @Bean(name = "genbankSqlSessionTemplate")
    public SqlSessionTemplate genbankSqlSessionTemplate(@Qualifier("genbankSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
        sqlSessionTemplate.getConfiguration().addMapper(GenbankMapper.class);
        return sqlSessionTemplate;
    }
}
