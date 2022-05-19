FROM apache/skywalking-java-agent:8.5.0-jdk8

VOLUME /tmp

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} ${JAVA_AGENT} -jar app.jar ${0} ${@}"]