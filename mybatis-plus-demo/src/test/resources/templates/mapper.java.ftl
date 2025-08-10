package ${package.Mapper};

import ${package.Entity}.${entity};
import ${superMapperClassPackage};
<#if mapperAnnotation>
    import org.apache.ibatis.annotations.Mapper;
</#if>
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* <p>
* ${table.comment!} Mapper 接口
* </p>
*
* @author ${author}
* @since ${date}
*/
<#if mapperAnnotation>
@Mapper
</#if>
<#if kotlin>
interface ${table.mapperName} : ${superMapperClass}<${entity}>
<#else>
public interface ${table.mapperName} extends ${superMapperClass}<${entity}> {
</#if>

    /**
    * 单个插入或更新（插入和更新都只处理非空字段）
    * @param entity 要插入或更新的实体
    * @return 影响的行数
    */
    int upsertSelective(${entity} entity);

    /**
    * 批量插入或更新（插入和更新都只处理非空字段）
    * @param list 要插入或更新的实体列表
    * @return 影响的行数
    */
    int batchUpsertSelective(@Param("list") List<${entity}> list);

<#if kotlin>
}
<#else>
}
</#if>