package com.prix.homepage.backend.livesearch.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import com.prix.homepage.backend.livesearch.mapper.PatternMatch.SwissProtMapper;

import javax.sql.DataSource;
@Configuration
@MapperScan(value = "com.prix.homepage.backend.livesearch.mapper.PatternMatch")
public class SwissProtDataSource {


    @Bean(name = "swissProtDataSrc")
    public DataSource swissProtDataSource () {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://166.104.110.37:3306/swiss_prot");
        dataSource.setUsername("prix");
        dataSource.setPassword("Prix2024!@");
        return dataSource;
    }

    @Bean(name = "swissProtSqlSessionFactory")
    public SqlSessionFactory swissProtSqlSessionFactory(@Qualifier("swissProtDataSrc") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        return sessionFactory.getObject();
    }

    @Bean(name = "swissProtSqlSessionTemplate")
    public SqlSessionTemplate swissProtSqlSessionTemplate(@Qualifier("swissProtSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
        sqlSessionTemplate.getConfiguration().addMapper(SwissProtMapper.class);
        return sqlSessionTemplate;

    }


}
