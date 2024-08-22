package com.prix.homepage.backend.livesearch.mapper;

import com.prix.homepage.backend.livesearch.pojo.UserSetting;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserSettingMapper {

    @Select("SELECT * FROM px_user_setting WHERE user_id = #{id} AND engine = 'modeye'")
    UserSetting findByUserId(Integer id);

    @Select("SELECT name FROM px_account WHERE id = #{id}")
    String findAccountNameById(Integer id);

    @Select("SELECT COUNT(*) > 0 FROM px_user_setting WHERE user_id = #{id}")
    boolean existsByUserId(@Param("id") Integer id);

    // dbond 관련 update
    @Update("UPDATE px_user_setting SET enzyme=#{enzyme}, mmc=#{mmc}, ptol=#{ptol}, ptol_unit=#{ptolUnit}, ftol=#{ftol}, engine=#{engine}, data_format=#{dataFormat}, instrument=#{instrument} WHERE user_id=#{userId}")
    void updateDbondSetting(@Param("userId") Integer userId, @Param("enzyme") String enzyme, @Param("mmc") String mmc,
                            @Param("ptol") String ptol, @Param("ptolUnit") String ptolUnit, @Param("ftol") String ftol,
                            @Param("engine") String engine, @Param("dataFormat") String dataFormat, @Param("instrument") String instrument);

    @Insert("INSERT INTO px_user_setting (user_id, enzyme, mmc, ptol, ptol_unit, ftol, engine, data_format, instrument) VALUES (#{userId}, #{enzyme}, #{mmc}, #{ptol}, #{ptolUnit}, #{ftol}, #{engine}, #{dataFormat}, #{instrument})")
    void insertDbondSetting(@Param("userId") Integer userId, @Param("enzyme") String enzyme, @Param("mmc") String mmc,
                            @Param("ptol") String ptol, @Param("ptolUnit") String ptolUnit, @Param("ftol") String ftol,
                            @Param("engine") String engine, @Param("dataFormat") String dataFormat, @Param("instrument") String instrument);

    // 다른 엔진 관련 update
    @Update("UPDATE px_user_setting SET enzyme=#{enzyme}, mmc=#{mmc}, met=#{met}, ptol=#{ptol}, ptol_unit=#{ptolUnit}, ftol=#{ftol}, mm_min=#{mmMin}, mm_max=#{mmMax}, engine=#{engine}, data_format=#{dataFormat}, instrument=#{instrument}, ms_resolution=#{msResolution}, msms_resolution=#{msmsResolution} WHERE user_id=#{userId}")
    void updateOtherSetting(@Param("userId") Integer userId, @Param("enzyme") String enzyme, @Param("mmc") String mmc,
                            @Param("met") String met, @Param("ptol") String ptol, @Param("ptolUnit") String ptolUnit,
                            @Param("ftol") String ftol, @Param("mmMin") String mmMin, @Param("mmMax") String mmMax,
                            @Param("engine") String engine, @Param("dataFormat") String dataFormat,
                            @Param("instrument") String instrument, @Param("msResolution") String msResolution,
                            @Param("msmsResolution") String msmsResolution);

    @Insert("INSERT INTO px_user_setting (user_id, enzyme, mmc, met, ptol, ptol_unit, ftol, mm_min, mm_max, engine, data_format, instrument, ms_resolution, msms_resolution) VALUES (#{userId}, #{enzyme}, #{mmc}, #{met}, #{ptol}, #{ptolUnit}, #{ftol}, #{mmMin}, #{mmMax}, #{engine}, #{dataFormat}, #{instrument}, #{msResolution}, #{msmsResolution})")
    void insertOtherSetting(@Param("userId") Integer userId, @Param("enzyme") String enzyme, @Param("mmc") String mmc,
                            @Param("met") String met, @Param("ptol") String ptol, @Param("ptolUnit") String ptolUnit,
                            @Param("ftol") String ftol, @Param("mmMin") String mmMin, @Param("mmMax") String mmMax,
                            @Param("engine") String engine, @Param("dataFormat") String dataFormat,
                            @Param("instrument") String instrument, @Param("msResolution") String msResolution,
                            @Param("msmsResolution") String msmsResolution);

}