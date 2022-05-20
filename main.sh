#!/usr/bin/env bash

# Constants
readonly MAIN_FILE_PATH=$(readlink -f $0)
readonly MAIN_FILE_DIR=$(dirname "${MAIN_FILE_PATH}")

readonly MAIN_ACTION_TIMESTAMP=$(date '+%Y-%m-%dT%H_%M_%S')
readonly MAIN_ACTION_TIMESTAMP_IN_SECONDS=$(date '+%s')
#maven打包追加参数，-Djava.io.tmpdir指定临时目录，作为javaagent缓存路径，避免重复下载
readonly MAIN_MAVEN_PACKAGE_ARGS=${GLOBAL_MAVEN_PACKAGE_ARGS:-"-Dmaven.test.skip=true -Djava.io.tmpdir=/tmp"}
#main::action::run运行目录，可通过GLOBAL_LAUNCHER_RUN_DIR自定义，默认/home/www目录
readonly MAIN_LAUNCHER_RUN_DIR=${GLOBAL_LAUNCHER_RUN_DIR:-"/home/www"}

readonly MAIN_DOCKER_TAG_PREFIX=${GLOBAL_DOCKER_TAG_PREFIX:-guanyangsunlight}

readonly MAIN_RUNTIME_LOG_DIR=${MAIN_FILE_DIR}/.log

# Variables init
ENABLE_CONSOLE_OUTPUT=true
SAVE_MAIN_LOG=false
MAIN_LOG_FILE="${MAIN_RUNTIME_LOG_DIR}/main_$(echo $$).log"

if [[ ${SAVE_MAIN_LOG} == true ]]; then
    set -e; mkdir -p "${MAIN_RUNTIME_LOG_DIR}"; set +e
fi

main::action::run(){
  log_info "run action start."

  #执行package打包
  main::action::package "$@"

  #解压配置运行文件
  local app_module_launcher="${MAIN_APP_MODULE_PATH}/target/*.launcher.tar.gz"
  #备份旧数据
  local app_run_path="${MAIN_LAUNCHER_RUN_DIR}/${MAIN_APP_MODULE_NAME}"
  if [[ -d "${app_run_path}" ]]; then
      local app_run_backup_path="${MAIN_LAUNCHER_RUN_DIR}/${MAIN_APP_MODULE_NAME}_backup_${MAIN_ACTION_TIMESTAMP_IN_SECONDS}"
      log_info "app launcher data backup path:  ${app_run_backup_path}"
      mv ${app_run_path} ${app_run_backup_path}
  fi

  tar -zxf ${app_module_launcher} -C ${MAIN_LAUNCHER_RUN_DIR}
  #删除旧软链
  rm /usr/local/bin/launcher.sh
  ln -s ${MAIN_LAUNCHER_RUN_DIR}/${MAIN_APP_MODULE_NAME}/bin/launcher.sh /usr/local/bin/launcher.sh

  #构建launcher.sh启动参数
  main::action::build-launcher-args

  local java_port=${MAIN_LAUNCHER_RUN_PORT}
  local launcher_args=${MAIN_LAUNCHER_RUN_SH}

  eval ${launcher_args} -d

  log_info "run action success. Visit 'http://127.0.0.1:${java_port}/hello' for more information."
}

main::action::docker-run(){
  log_info "docker-run action start."
  #构建镜像
  main::action::build-image "$@"

  #构建launcher.sh启动参数
  main::action::build-launcher-args

  local java_port=${MAIN_LAUNCHER_RUN_PORT}
  local launcher_args=${MAIN_LAUNCHER_RUN_SH}
  #定义docker容器名称
  local docker_name=${MAIN_APP_MODULE_NAME}-${MAIN_APP_MODULE_JAR_VERSION}
  #运行docker容器
  docker run -dt --name "${docker_name}" -p ${java_port}:${java_port} "${MAIN_DOCKER_TAG_NAME}" bash -c "${launcher_args}"

  log_info "docker-run action success. Visit 'http://127.0.0.1:${java_port}/hello' for more information."
}

main::action::build-launcher-args(){
    #构建launcher.sh启动参数
    local launcher_args="/usr/local/bin/launcher.sh start -n ${MAIN_APP_MODULE_NAME}"
    #添加jvm启动参数
    local java_options="-Dfile.encoding=UTF-8 -Duser.timezone=Asia/Shanghai -XX:InitialRAMPercentage=50.0 -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:MaxGCPauseMillis=150"
    if [[ -n ${java_options} ]]; then
        launcher_args+=" -jo '${java_options}'"
    fi
    #设置端口
    local java_port=8080
    main::check_port_range ${java_port}
    launcher_args+=" -a '--server.port=${java_port}'"

    #设置javaagent_bs
    local javaagent_bs="127.0.0.1:11800"
    if [[ -n ${javaagent_bs} ]]; then
        launcher_args+=" --javaagent-bs '${javaagent_bs}'"
    fi
    readonly MAIN_LAUNCHER_RUN_PORT=${java_port}
    readonly MAIN_LAUNCHER_RUN_SH=${launcher_args}
}

main::action::deploy-image(){
  log_info "deploy-image action start."

  #构建镜像
  main::action::build-image "$@"

  #上传到私服
  docker push "${MAIN_DOCKER_TAG_NAME}"
  #删除本地镜像，减少本地空间占用
  docker rmi -f "${MAIN_DOCKER_TAG_NAME}"

  log_info "deploy-image action success."
}

main::action::build-image(){
  log_info "build-image action start."

  #执行package打包
  main::action::package "$@"
  #dockerfile文件检查
  main::action::check-docker "$@"
  #检查并配置docker tag
  main::action::check-docker-tag "$@"

  #构建镜像
  local dockerfile_context="${MAIN_APP_MODULE_PATH}"
  local dockerfile_path="${MAIN_APP_MODULE_DOCKERFILE}"
  #更新镜像的新版本
  #docker build -t "${MAIN_DOCKER_TAG_NAME}" --pull=true --file="${dockerfile_path}" "${dockerfile_context}"
  docker build -t "${MAIN_DOCKER_TAG_NAME}" --file="${dockerfile_path}" "${dockerfile_context}"

  log_info "dockerfile context: ${MAIN_APP_MODULE_PATH}"

  log_info "build-image action success."
}

main::action::check-docker-tag(){
    log_info "check-docker-tag action start."

    local docker_tag_prefix="${2}"
    if [[ -z ${docker_tag_prefix} && -n ${MAIN_DOCKER_TAG_PREFIX} ]]; then
        docker_tag_prefix="${MAIN_DOCKER_TAG_PREFIX}"
    fi
    if [[ -z "${docker_tag_prefix}" ]]; then
        log_error "docker_tag_prefix arg not set."
        main::abort
    fi

    if [[ -z ${MAIN_APP_MODULE_NAME} || -z ${MAIN_APP_PARENT_NAME} || -z ${MAIN_APP_MODULE_JAR_VERSION} ]]; then
        log_error "Please do package action first."
        main::abort
    fi

    readonly MAIN_DOCKER_TAG_NAME="${docker_tag_prefix}/${MAIN_APP_PARENT_NAME}:${MAIN_APP_MODULE_NAME}-${MAIN_APP_MODULE_JAR_VERSION}-${MAIN_ACTION_TIMESTAMP_IN_SECONDS}"

    log_info "app docker tag: ${MAIN_DOCKER_TAG_NAME}"

    log_info "check-docker-tag action success."
}

main::action::package(){
  log_info "package action start."

  main::action::check-maven
  main::action::check-module "$@"

  #执行maven打包
  mvn -T 4 -B -pl "${MAIN_APP_MODULE_NAME}" -am clean package ${MAIN_MAVEN_PACKAGE_ARGS} -U -e

  readonly MAIN_APP_MODULE_JAR_NAME=$(find "${MAIN_APP_MODULE_PATH}"/*/"${MAIN_APP_MODULE_NAME}"-*.jar | awk -F / '{print $NF}')
  readonly MAIN_APP_MODULE_JAR_VERSION=$(echo "${MAIN_APP_MODULE_JAR_NAME}" | awk -F "${MAIN_APP_MODULE_NAME}"'-' '{print $2}' | awk -F '.jar' '{print $1}')

  log_info "app module jar file: ${MAIN_APP_MODULE_JAR_NAME} , jar version: ${MAIN_APP_MODULE_JAR_VERSION}"

  log_info "package action success."
}

main::action::check-docker(){
    log_info "check-docker action start."

    main::action::check-module "$@"

    local app_module_dockerfile="${MAIN_APP_MODULE_PATH}/target/Dockerfile"
    if [[ ! -f "${app_module_dockerfile}" ]]; then
        log_error "Please add [launcher-maven-plugin] support. Visit 'https://github.com/guanyang/spring-launcher-parent' for more information."
        main::abort
    fi
    readonly MAIN_APP_MODULE_DOCKERFILE=${app_module_dockerfile}

    log_info "app module dockerfile: ${MAIN_APP_MODULE_DOCKERFILE}"

    log_info "check-docker action success."
}

main::action::check-module(){
    log_info "check-module action start."

    if [[ -n ${MAIN_APP_MODULE_NAME} && -n ${MAIN_APP_MODULE_PATH} ]]; then
        log_info "Module path: ${MAIN_APP_MODULE_PATH} , Module name: ${MAIN_APP_MODULE_NAME}"
        return
    fi

    local app_module_name="${1}"
    if [[ -z ${app_module_name} && -n ${GLOBAL_APP_MODULE_NAME} ]]; then
        app_module_name="${GLOBAL_APP_MODULE_NAME}"
    fi
    if [[ -z "${app_module_name}" ]]; then
        log_error "app_module_name arg not set."
        main::abort
    fi

    local app_module_path="${MAIN_FILE_DIR}/${app_module_name}"
    if [[ ! -d "${app_module_path}" ]]; then
        log_error "app module path \"${app_module_path}\" not exists."
        main::abort
    fi

    readonly MAIN_APP_PARENT_NAME=$(echo "${MAIN_FILE_DIR}" | awk -F '/' '{print $NF}')
    readonly MAIN_APP_MODULE_NAME=${app_module_name}
    readonly MAIN_APP_MODULE_PATH=${app_module_path}

    log_info "Module path: ${MAIN_APP_MODULE_PATH}, Parent name: ${MAIN_APP_PARENT_NAME} , Module name: ${MAIN_APP_MODULE_NAME}"

    log_info "check-module action success."
}

main::action::check-maven(){
  #检查maven版本
  log_info "check-maven action start."
  local mvn_ver=$(mvn -version)

  if [[ -z "${mvn_ver}" ]]; then
      log_error "mvn var not set."
      main::abort
  else
      echo "${mvn_ver}"
  fi

  log_info "check-maven action success."
}

main::clean_on_exit() {
    # sleep 0.1s 解决末尾日志打印丢失的问题,
    sleep 0.1

    local LAUNCHER_tmp_log_file="${MAIN_LOG_FILE}"
    if [[ -f "${launcher_tmp_log_file}" ]]; then
        log_info "Delete main temporary log file: ${launcher_tmp_log_file}"
        rm -f "${launcher_tmp_log_file}"
    fi

    # sleep解决使用Systemd时，最后日志输出被吞掉的问题
    sleep 0.1
}

main::abort() {
    log_warn "Terminate main start."
    exit 1
}

main::check_port_range() {
    local port=${1}
    if [[ ${port} =~ ^[0-9]+$ && ${port} -gt 0 && ${port} -lt 65535 ]];then
        return 0
    else
        log_error "Invalid port '${port}', port should be a number between 1024 to 65535"
        main::abort
    fi
}

##########################日志相关 start###################################

log_info(){
    log_generic "INFO" "$@"
}

log_warn(){
    log_generic "WARN" "$@"
}

log_error(){
    log_generic "ERROR" "$@"
}

# 日志
# Globals:
#   MAIN_LOG_FILE: MAIN日志文件位置
#   SAVE_MAIN_LOG: 是否保存日志文件到${MAIN_LOG_FILE}中
# Arguments:
#   $1: 日志级别
#   $2: 日志文本
# Returns:
#   None
log_generic (){
    local log_msg=$(create_log_msg "$@")

    if [[ ${SAVE_MAIN_LOG} == true ]]; then
        echo "${log_msg}" >> "${MAIN_LOG_FILE}"
    fi

    [[ ${ENABLE_CONSOLE_OUTPUT} == true ]] && print_msg_with_colored_log_level "${log_msg}"
    return 0
}

print_msg_with_colored_log_level() {
    local log_msg="${1}"

    if [[ ${MAIN_COLORED_CONSOLE_OUTPUT} == false || ${MAIN_COLORED_CONSOLE_OUTPUT} == 0 ]]; then
        echo "${log_msg}"
        return
    fi

    local color="\033[0m"
    if [[ ${log_msg} == *" | ERROR "* ]]; then
        color="\033[1;31m"
    elif [[ ${log_msg} == *" | WARN "* ]]; then
        color="\033[1;33m"
    fi
    printf "${color}%s\n" "${log_msg}"
}

create_log_msg() {
    local log_level=$1
    local logger
    local log_content
    if [[ $# == 3 ]]; then
        logger=$2
        log_content=$3
    else
        logger="main"
        log_content=$2
    fi

    local log_pattern="$(date '+%Y-%m-%dT%H:%M:%S.%3N%z') | %-5s | N/A | launcher | %s | %s\n"
    printf "${log_pattern}" "${log_level}" "${logger}" "${log_content}"
}

print_arg_usage() {
    local short_arg=$1
    local long_arg=$2
    local arg_desc=$3
    if [[ $# == 2 ]]; then
        if [[ $1 == --* ]]; then
            short_arg=""
            long_arg=$1
        else
            short_arg=$1
            long_arg=""
        fi
        arg_desc=$2
    fi
    if [[ -n ${short_arg} ]]; then
        short_arg="${short_arg},"
    fi
    printf '    '
    printf '%-5s' "${short_arg}"
    printf ' '
    printf '%-20s' "${long_arg}"
    printf '          '
    printf  '%s' "${arg_desc}"
    printf  '\n'
}

##########################日志相关 end###################################

trap main::clean_on_exit EXIT

readonly MAIN_COMMAND_ARGS="$@"

log_info "app action args: ${MAIN_COMMAND_ARGS}"

main::action::deploy-image "$@"