## spring-base-demo

### Quickstart Guide
#### 相关依赖
- 本工程依赖[guanyang/spring-base-parent](https://github.com/guanyang/spring-base-parent) 相关组件
- 将`spring-base-parent`下载到本地，执行`mvn clean install -Dmaven.test.skip=true`，将相关组件生成到本地

#### 可以通过根目录`main.sh`快速使用

```
Usage:  $0 COMMAND [arg...]

Commands:
    package             Maven package for an application
    build-image         Build docker image for an application
    deploy-image        Deploy docker image for an application
    docker-run          Docker run command for an application
    run                 Shell run  command for an application
```

### 模块构建

#### mvn打包

```
$> APP_MODE_NAME=webflux-demo
$> mvn -T 4 -B -pl ${APP_MODE_NAME} -am clean package -Dmaven.test.skip=true -U -e
```

- 将`APP_MODE_NAME`替换成对应模块名称

#### 构建docker镜像
```
$> APP_MODE_NAME=webflux-demo
$> WORKSPACE=$PWD
$> cd ${WORKSPACE}/${APP_MODE_NAME}
$> DOCKER_TAG_NAME=guanyangsunlight/spring-base-demo:webflux-demo-0.0.1
$> docker build -t ${DOCKER_TAG_NAME} --pull=true --file=${WORKSPACE}/${APP_MODE_NAME}/target/Dockerfile .
```

- 将`APP_MODE_NAME`替换成对应模块名称
- `WORKSPACE`自动读取当前路径，需要切换到工程根路径，或者自行指定路径
- 将`DOCKER_TAG_NAME`替换成自定义的`TAG`

### 模块启动

#### docker启动

##### 基于`launcher.sh`脚本

```
$> APP_MODE_NAME=webflux-demo
$> DOCKER_TAG_NAME=guanyangsunlight/spring-base-demo:webflux-demo-0.0.1
$> docker run -dt --name ${APP_MODE_NAME} -p 8088:8088 ${DOCKER_TAG_NAME} bash -c "/usr/local/bin/launcher.sh start -n ${APP_MODE_NAME} -jo '-Dfile.encoding=UTF-8 -Duser.timezone=Asia/Shanghai -XX:InitialRAMPercentage=50.0 -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:MaxGCPauseMillis=150' -a '--server.port=8088'"
```

- 探活入口：`http://127.0.0.1:8088/hello`

##### 基于自定义Dockerfile启动

```
$> APP_MODE_NAME=webflux-demo-agent

$> DOCKER_TAG_NAME=guanyangsunlight/spring-base-demo:webflux-demo-agent

$> JAVA_OPTS="-Dfile.encoding=UTF-8 -Duser.timezone=Asia/Shanghai -XX:InitialRAMPercentage=50.0 -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:MaxGCPauseMillis=150"

$> JAVA_ARG="--server.port=8090"

$> JAVA_AGENT="-javaagent:/skywalking/agent/skywalking-agent.jar -Dskywalking.agent.service_name=${APP_MODE_NAME} -Dskywalking.collector.backend_service=skywalking:11800"

$> docker run --name ${APP_MODE_NAME} -d -p 8090:8090 --link skywalking:skywalking -e "JAVA_OPTS=${JAVA_OPTS}" -e "JAVA_AGENT=${JAVA_AGENT}" ${DOCKER_TAG_NAME} ${JAVA_ARG}
```

- 探活入口：`http://127.0.0.1:8090/hello`

### 可观测性
#### 挂载skywalking
```
-javaagent:/Users/guanyang/skywalking/skywalking-agent/skywalking-agent.jar -Dskywalking.agent.service_name=dev::kafka-demo -Dskywalking.collector.backend_service=dev.skywalking:11800
```

### 参考文档
- [guanyang/spring-launcher-parent](https://github.com/guanyang/spring-launcher-parent)
- [gs-spring-boot-docker](https://github.com/spring-guides/gs-spring-boot-docker)
- [Topical Guide on Docker](https://spring.io/guides/topicals/spring-boot-docker)