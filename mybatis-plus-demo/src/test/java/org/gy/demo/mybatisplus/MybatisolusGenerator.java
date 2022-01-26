package org.gy.demo.mybatisplus;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.TemplateType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import java.util.Collections;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2021/11/26 14:58
 */
public class MybatisolusGenerator {


    // 数据源 url
    static final String url = "jdbc:mysql://localhost:3306/demo?useUnicode=true&characterEncoding=utf8";
    // 数据库用户名
    static final String username = "root";
    // 数据库密码
    static final String password = "12345678";

    //源码路径
    static final String SRC_PATH = "/src/main/java";

    //资源路径
    static final String RESOURCE_PATH = "/src/main/resources";

    //项目根路径
    static final String path =
        System.getProperty("user.dir") + System.getProperty("file.separator");

    public static void main(String[] args) {
        String author = "gy";
        String basePackage = "org.gy.demo.mybatisplus";
        String mavenProjectName = "mybatis-plus-demo";
        String sourcePath = path + mavenProjectName + SRC_PATH;
        String mapperXmlPath = path + mavenProjectName + RESOURCE_PATH + "/mappers";
        String[] tableNames = {"hello_world"};
        generator(author, basePackage, sourcePath, mapperXmlPath, url, username, password, tableNames);
    }

    /**
     * 功能描述：代码生成
     *
     * @param author 作者
     * @param basePackage package根路径
     * @param sourcePath 源码路径
     * @param mapperXmlPath mapperXml路径
     * @param url 数据库url
     * @param username 用户名
     * @param password 密码
     * @param tableNames 表名
     * @author gy
     * @version 1.0.0
     */
    private static void generator(String author, String basePackage, String sourcePath, String mapperXmlPath,
        String url, String username, String password, String... tableNames) {
        FastAutoGenerator.create(url, username, password)
            .globalConfig(builder -> {
                builder.author(author)                    // 设置作者
                    .fileOverride()                     // 覆盖已生成文件
                    .outputDir(sourcePath); // 指定输出目录
            })
            .packageConfig(builder -> {
                builder.parent(basePackage) // 设置父包名
                    .pathInfo(Collections
                        .singletonMap(OutputFile.mapperXml, mapperXmlPath)); // 设置mapperXml生成路径
            })
            .strategyConfig(builder -> {
                builder.addInclude(tableNames)   //设置表名
                    .entityBuilder()
                    .idType(IdType.AUTO)            //id自增
                    .enableLombok()                 //开启lombok
                    .enableChainModel()
//                    .logicDeleteColumnName("is_deleted")    //逻辑删除数据库字段
//                    .logicDeletePropertyName("isDeleted")   //逻辑删除属性名称
                    .enableActiveRecord().build()           //开启 activeRecord 模式
                    .mapperBuilder()
                    .enableBaseColumnList()     //XML columList
                    .enableBaseResultMap().build();     //XML ResultMap
            })
            .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
            .templateConfig(builder -> {
                builder.disable(TemplateType.CONTROLLER);   //禁用controller生成
            })
            .execute();
    }

}
