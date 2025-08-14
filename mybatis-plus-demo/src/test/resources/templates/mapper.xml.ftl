<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${package.Mapper}.${table.mapperName}">

    <#if enableCache>
    <!-- 开启二级缓存 -->
    <cache type="${cacheClassName}"/>
    </#if>

    <#if baseResultMap>
    <!-- 通用查询映射结果 -->
    <resultMap id="BaseResultMap" type="${package.Entity}.${entity}">
        <#list table.fields as field>
            <#if field.keyFlag><#--生成主键排在第一位-->
        <id column="${field.name}" property="${field.propertyName}" />
            </#if>
        </#list>
        <#list table.commonFields as field><#--生成公共字段 -->
        <result column="${field.name}" property="${field.propertyName}" />
        </#list>
        <#list table.fields as field>
            <#if !field.keyFlag><#--生成普通字段 -->
        <result column="${field.name}" property="${field.propertyName}" />
            </#if>
        </#list>
    </resultMap>
    </#if>

    <#if baseColumnList>
    <!-- 通用查询结果列 -->
    <sql id="Base_Column_List">
        <#list table.commonFields as field>
            ${field.columnName},
        </#list>
        ${table.fieldNames}
    </sql>
    </#if>

    <!-- 批量插入或更新（插入和更新都只处理非空字段，处理的字段必须一样） -->
    <insert id="batchUpsertSelective">
        INSERT INTO ${table.name}
        <!-- 批量插入或更新-字段列表sql定义 -->
        <include refid="BatchUpsert_Column_List"/>
        VALUES
        <!-- 批量插入或更新-插入值sql定义 -->
        <include refid="BatchUpsert_Column_Values"/>
        AS new
        ON DUPLICATE KEY UPDATE
        <!-- 批量插入或更新-更新字段sql定义 -->
        <include refid="BatchUpsert_Update_Set"/>
    </insert>

    <!-- 批量插入或更新-字段列表sql定义 -->
    <sql id="BatchUpsert_Column_List">
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <#list table.fields as field>
            <if test="list[0].${field.propertyName} != null">${field.name},</if>
            </#list>
        </trim>
    </sql>
    <!-- 批量插入或更新-插入值sql定义 -->
    <sql id="BatchUpsert_Column_Values">
        <foreach collection="list" item="item" separator=",">
            <trim prefix="(" suffix=")" suffixOverrides=",">
                <#list table.fields as field>
                <if test="item.${field.propertyName} != null">${r"#{item."}${field.propertyName}${r"}"},</if>
                </#list>
            </trim>
        </foreach>
    </sql>
    <!-- 批量插入或更新-更新字段sql定义 -->
    <sql id="BatchUpsert_Update_Set">
        <trim suffixOverrides=",">
            <#list table.fields as field>
                <#if !field.keyFlag>
            <if test="list[0].${field.propertyName} != null">${field.name} = new.${field.name},</if>
                </#if>
            </#list>
        </trim>
    </sql>
</mapper>