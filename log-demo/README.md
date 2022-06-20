## log-demo

### 配置log4j2异步支持
#### 去掉SpringBoot默认日志
```
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
  <exclusions>
    <exclusion>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-logging</artifactId>
    </exclusion>
  </exclusions>
</dependency>
```

#### 引入log4j2依赖
```
<!-- 引入log4j2依赖 -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-log4j2</artifactId>
</dependency>
<!--log4j2异步AsyncLogger需要这个依赖,否则AsyncLogger日志打印不出来-->
  <dependency>
    <groupId>com.lmax</groupId>
    <artifactId>disruptor</artifactId>
    <version>3.4.4</version>
  </dependency>
```

#### 设置异步模式
##### 完全异步模式
- 方式一：在启动程序设置如下参数
```
System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
```
- 方式二：启动参数添加如下参数
```
-DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector
```

##### 异步/同步混合模式
- 在配置文件中Logger使用`<asyncRoot>` 或`<asyncLogger>`,而且`<asyncRoot>`或`<asyncLogger>`可以和`<root>`或`<logger>`混合使用
```
<AsyncLogger name="org.gy.demo.log" level="INFO" additivity="false">
  <appender-ref ref="Console" />
  <appender-ref ref="RollingFileInfo" />
</AsyncLogger>
<Root level="INFO">
  <appender-ref ref="RollingFileInfo" />
</Root>
```

#### 配置文件加载
- 在`application.properties`文件添加如下配置
```
##设置log4j2加载的配置文件,如果没有设置的话，默认找classpath:log4j2.xml
#logging.config=classpath:log4j2.xml
```

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.6.2/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.6.2/maven-plugin/reference/html/#build-image)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.6.2/reference/htmlsingle/#boot-features-developing-web-applications)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)

