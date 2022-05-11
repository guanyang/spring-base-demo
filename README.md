### spring-base-demo

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
