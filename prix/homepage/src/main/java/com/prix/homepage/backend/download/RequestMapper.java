package com.prix.homepage.backend.download;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RequestMapper {

    @Insert("INSERT INTO px_software_request (date, name, affiliation, title, email, instrument, software, state) " +
            "VALUES (NOW(), #{name}, #{affiliation}, #{title}, #{email}, #{instrument}, #{software}, 0)")
    void insertSoftwareRequest(String name, String affiliation, String title, String email, String instrument, String software);
}
