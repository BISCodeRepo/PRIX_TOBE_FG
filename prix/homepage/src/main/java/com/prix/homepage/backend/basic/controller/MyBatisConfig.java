package com.prix.homepage.backend.basic.controller;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = {
        "com.prix.homepage.backend",
//        "com.prix.homepage.backend.livesearch.mapper",
//        "com.prix.homepage.backend.livesearch.mapper.PatternMatch",
//        "com.prix.homepage.backend.livesearch.mapper.dbond",
//        "com.prix.homepage.backend.account.mapper",
//        "com.prix.homepage.backend.admin",
//        "com.prix.homepage.backend.download",
})
public class MyBatisConfig {

    @Bean
    public ConfigurationCustomizer mybatisConfigurationCustomizer() {
        return new ConfigurationCustomizer() {
            @Override
            public void customize(org.apache.ibatis.session.Configuration configuration) {
                configuration.setMapUnderscoreToCamelCase(true);
            }
        };
    }
}
