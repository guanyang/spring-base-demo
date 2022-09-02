#!/usr/bin/env bash

# Constants
readonly MAIN_FILE_PATH=$(readlink -f $0)
readonly MAIN_FILE_DIR=$(dirname "${MAIN_FILE_PATH}")

readonly MAIN_ACTION_TIMESTAMP=$(date '+%Y-%m-%dT%H_%M_%S')
readonly MAIN_ACTION_TIMESTAMP_IN_SECONDS=$(date '+%s')
#maven打包追加参数，-Djava.io.tmpdir指定临时目录，作为javaagent缓存路径，避免重复下载
readonly MAIN_CONS_MAVEN_PACKAGE_ARGS=${GLOBAL_MAVEN_PACKAGE_ARGS:-"-Dmaven.test.skip=true -Djava.io.tmpdir=/tmp"}
#main::action::run运行目录，可通过GLOBAL_LAUNCHER_RUN_DIR自定义，默认/home/www目录
readonly MAIN_CONS_LAUNCHER_RUN_DIR=${GLOBAL_LAUNCHER_RUN_DIR:-"/home/www"}
#docker tag命名空间定义，可通过GLOBAL_DOCKER_TAG_NAMESPACE自定义，默认guanyangsunlight
readonly MAIN_CONS_DOCKER_TAG_NAMESPACE=${GLOBAL_DOCKER_TAG_NAMESPACE:-guanyangsunlight}

readonly MAIN_CONS_DEFAULT_SERVICE_PORT=8080
readonly MAIN_CONS_DEFAULT_SERVICE_JAVA_OPTS="-Dfile.encoding=UTF-8 -Duser.timezone=Asia/Shanghai -XX:InitialRAMPercentage=50.0 -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:MaxGCPauseMillis=150"

readonly MAIN_RUNTIME_LOG_DIR=${MAIN_FILE_DIR}/.log

# Variables init
ENABLE_CONSOLE_OUTPUT=true
SAVE_MAIN_LOG=false
MAIN_LOG_FILE="${MAIN_RUNTIME_LOG_DIR}/main_$(echo $$).log"

if [[ ${SAVE_MAIN_LOG} == true ]]; then
    set -e; mkdir -p "${MAIN_RUNTIME_LOG_DIR}"; set +e
fi

function main {
    readonly MAIN_ACTION=$1
    case $1 in
    "package" | "build-image" | "deploy-image" | "docker-run" | "run" )
        shift
        if [[ $# -eq 0 ]]; then
          main::func::print_action_usage "${MAIN_ACTION}"
          main::abort
        fi
        while [[ $# -gt 0 ]]
        do
            case $1 in
                "-n" | "--app-name")
                    PARAM_MODULE_NAME="$2"
                    shift
                    ;;
                "-mvn" | "--maven-package")
                    MAIN_MAVEN_PACKAGE_ARGS="$2"
                    shift
                    ;;
                "-tn" | "--tag-namespace")
                    MAIN_DOCKER_TAG_PREFIX="$2"
                    shift
                    ;;
                "-jo" | "--java-opts" )
                    MAIN_APP_JAVA_OPTIONS="$2"
                    shift
                    ;;
                "-p" | "--server-port" )
                    MAIN_APP_JAVA_PORT="$2"
                    shift
                    ;;
                "-jbs" | "--javaagent-bs" )
                    MAIN_APP_JAVAAGENT_BS="$2"
                    shift
                    ;;
                "-rd" | "--run-dir" )
                    MAIN_LAUNCHER_RUN_DIR="$2"
                    shift
                    ;;
                "-in" | "--image-name" )
                    PARAM_IMAGE_NAME="$2"
                    shift
                    ;;
                "-e" | "--app-env")
                    PARAM_APP_ENV="$2"
                    shift
                    ;;
                "-var" | "--var-config")
                    PARAM_VARIABLES="$2"
                    shift
                    ;;
                "-h" | "--help" )
                    main::func::print_action_usage "${MAIN_ACTION}"
                    echo ""
                    exit 0
                    ;;
                * )
                    echo "Unknow option $1"
                    echo "See '$0 ${MAIN_ACTION} --help'"
                    echo ""
                    exit 1
            esac
            shift
        done
        log_info "Current user: $(whoami)"
        main::action::${MAIN_ACTION} "${PARAM_MODULE_NAME}"
        ;;
    "-h" | "--help" )
        main:func::print_usage
        exit 0
        ;;
    * )
        echo "Unknow command $1"
        echo "See '$0 --help'"
        echo ""
        exit 0
    esac
}

main::func::print_action_usage() {
    local action_name="${1}"
    if [[ -z ${action_name} ]]; then
        action_name="package"
    fi
    printf "Usage:  $0 ${action_name} [OPTIONS]\n\n"
    printf "Example:  $0 ${action_name} -n web-demo\n\n"
    printf "${action_name} for an application\n\n"
    printf 'Options:\n'

    #基础参数
    print_arg_usage '-n'    '--app-name'          "[Required]Set application name (e.g. -n 'web-demo')"
    print_arg_usage '-mvn'  '--maven-package'     "[Optional]Set maven package args, default value: -Dmaven.test.skip=true -Djava.io.tmpdir=/tmp (e.g. -mvn '-Dmaven.test.skip=true')"
    print_arg_usage '-var'  '--var-config'        "[Optional]Set system variables (e.g. -var 'APP_SRC_GROUP=DEMO')"
    #docker相关参数
    if [[ ${action_name} = "build-image" || ${action_name} = "deploy-image" || ${action_name} = "docker-run" ]]; then
        print_arg_usage '-tn'   '--tag-namespace'  "[Optional]Set docker tag namespace for repository, default value: guanyangsunlight (e.g. -tn 'your namespace')"
    fi
    if [[ ${action_name} = "deploy-image" || ${action_name} = "docker-run" ]]; then
        print_arg_usage '-in'   '--image-name'    "[Optional]Set special docker image name (e.g. -in 'web-demo:v1')"
    fi
    #运行相关参数
    if [[ ${action_name} = "docker-run" || ${action_name} = "run" ]]; then
        print_arg_usage '-jo'   '--java-opts'     "[Optional]Set Java VM Options for your application (e.g. -jo '-Xmx4g -Xms4g')"
        print_arg_usage '-p'    '--server-port'   "[Optional]SpringBoot web container port, port range [1024,65535], default value: 8080 (e.g. -p '8080')"
        print_arg_usage '-jbs'  '--javaagent-bs'  "[Optional]Set skywalking agent backend_service for your application (e.g. -jbs '127.0.0.1:11800')"
        print_arg_usage '-e'    '--app-env'       "[Optional]Set application environment [prod/gray/pre/test/dev] (e.g. -e dev)"
    fi
    if [[ ${action_name} = "run" ]]; then
        print_arg_usage '-rd'   '--run-dir'       "[Optional]Set web container run dir, default value: /home/www (e.g. -rd '/home/www')"
    fi
    print_arg_usage '-h'    '--help'              'Print usage'
}

main:func::print_usage() {
cat << EOF
Usage:  $0 COMMAND [arg...]

Commands:
    package             Maven package for an application
    build-image         Build docker image for an application
    deploy-image        Deploy docker image for an application
    docker-run          Docker run command for an application
    run                 Shell run  command for an application
EOF
}

main::action::run(){
  log_info "run action start."

  #执行package打包
  main::action::package "$@"

  #解压配置运行文件
  local app_module_launcher="${MAIN_APP_MODULE_PATH}/target/*.launcher.tar.gz"
  #备份旧数据
  local launcher_run_dir=${MAIN_LAUNCHER_RUN_DIR}
  if [[ -z ${launcher_run_dir} ]]; then
      launcher_run_dir=${MAIN_CONS_LAUNCHER_RUN_DIR}
  fi
  local app_run_path="${launcher_run_dir}/${MAIN_APP_MODULE_NAME}"
  if [[ -d "${app_run_path}" ]]; then
      local current_time_in_seconds=$(date '+%Y%m%d%H%M%S')
      local app_run_backup_path="${launcher_run_dir}/${MAIN_APP_MODULE_NAME}_backup_${current_time_in_seconds}"
      log_info "app launcher data backup path:  ${app_run_backup_path}"
      mv ${app_run_path} ${app_run_backup_path}
  fi

  tar -zxf ${app_module_launcher} -C ${launcher_run_dir}

  #构建launcher.sh启动参数
  main::check::check-launcher-sh "${app_run_path}"
  main::check::check-launcher-args "start"

  local java_port=${MAIN_LAUNCHER_RUN_PORT}
  local launcher_args=${MAIN_LAUNCHER_RUN_SH}
  #设置后台运行
  launcher_args+=" -d"

  eval "${launcher_args}"

  log_info "run action success. Visit 'http://127.0.0.1:${java_port}/hello' for more information."
}

main::action::docker-run(){
  log_info "docker-run action start."

  local docker_image_name=${PARAM_IMAGE_NAME}
  if [[ -n ${docker_image_name} ]]; then
      #如果指定了镜像参数，则不需要再次构建镜像
      main::check::check-module "$@"
  else
      #构建镜像
      main::action::build-image "$@"
      docker_image_name=${MAIN_DOCKER_TAG_NAME}
  fi

  #构建launcher.sh启动参数
  main::check::check-launcher-sh "/usr/local"
  main::check::check-launcher-args "start"

  local java_port=${MAIN_LAUNCHER_RUN_PORT}
  local launcher_args=${MAIN_LAUNCHER_RUN_SH}
  #定义docker容器名称
  local current_time_in_seconds=$(date '+%Y%m%d%H%M%S')
  local docker_name=${MAIN_APP_MODULE_NAME}-${current_time_in_seconds}
  #运行docker容器
  docker run -d --net host --name "${docker_name}" -p ${java_port}:${java_port} "${docker_image_name}" bash -c "${launcher_args}"

  log_info "docker-run action success. Visit 'http://127.0.0.1:${java_port}/hello' for more information."
}

main::check::check-launcher-sh(){
    #构建launcher.sh脚本路径
    local launcher_path="${1}"
    if [[ -z ${launcher_path} ]]; then
        launcher_path="/usr/local"
    fi
    local launcher_sh="${launcher_path}/bin/launcher.sh"
#    if [[ ! -f "${launcher_sh}" ]]; then
#        log_info "app launcher file \"${launcher_sh}\" not exists."
#        main::abort
#    fi
    readonly MAIN_APP_LAUNCHER_PATH=${launcher_sh}

    log_info "Launcher path: ${MAIN_APP_LAUNCHER_PATH}"
}

main::check::check-launcher-args(){
    #构建launcher.sh启动参数
    local action_name="${1}"
    if [[ -z ${action_name} ]]; then
        action_name="start"
    fi
    local launcher_args="${MAIN_APP_LAUNCHER_PATH} ${action_name} -n ${MAIN_APP_MODULE_NAME}"
    if [[ ${action_name} = "start" ]]; then
        #添加jvm启动参数
        local java_options="${MAIN_APP_JAVA_OPTIONS:-${MAIN_CONS_DEFAULT_SERVICE_JAVA_OPTS}}"
        if [[ -n ${java_options} ]]; then
            launcher_args+=" -jo '${java_options}'"
        fi
        #设置端口，默认8080
        local java_port="${MAIN_APP_JAVA_PORT:-${MAIN_CONS_DEFAULT_SERVICE_PORT}}"
        main::check_port_range ${java_port}
        launcher_args+=" -a '--server.port=${java_port}'"

        #设置javaagent_bs
        local javaagent_bs=${MAIN_APP_JAVAAGENT_BS}
        if [[ -n ${javaagent_bs} ]]; then
            launcher_args+=" --javaagent-bs '${javaagent_bs}'"
        fi
        #设置环境
        local java_env="${PARAM_APP_ENV}"
        if [[ -n ${java_env} ]]; then
            launcher_args+=" -e ${java_env}"
        fi

        readonly MAIN_LAUNCHER_RUN_PORT=${java_port}
    fi
    #添加系统参数
    if [[ -n ${PARAM_VARIABLES} ]]; then
        launcher_args="${PARAM_VARIABLES} ${launcher_args}"
    fi
    readonly MAIN_LAUNCHER_RUN_SH=${launcher_args}

    log_info "Launcher run sh: ${MAIN_LAUNCHER_RUN_SH}"
}

main::action::deploy-image(){
  log_info "deploy-image action start."

  local docker_image_name=${PARAM_IMAGE_NAME}
  if [[ -z ${docker_image_name} ]]; then
      #构建镜像
      main::action::build-image "$@"
      docker_image_name=${MAIN_DOCKER_TAG_NAME}
  fi

  #上传到私服
  docker push "${docker_image_name}"
  #删除本地镜像，减少本地空间占用
  docker rmi -f "${docker_image_name}"

  log_info "deploy-image action success."
}

main::action::build-image(){
  log_info "build-image action start."

  #执行package打包
  main::action::package "$@"
  #dockerfile文件检查
  main::check::check-docker "$@"
  #检查并配置docker tag
  main::check::check-docker-tag

  #构建镜像
  local dockerfile_context="${MAIN_APP_MODULE_PATH}"
  local dockerfile_path="${MAIN_APP_MODULE_DOCKERFILE}"
  #更新镜像的新版本
  #docker build -t "${MAIN_DOCKER_TAG_NAME}" --pull=true --file="${dockerfile_path}" "${dockerfile_context}"
  docker build -t "${MAIN_DOCKER_TAG_NAME}" --file="${dockerfile_path}" "${dockerfile_context}"

  log_info "dockerfile context: ${MAIN_APP_MODULE_PATH}"

  log_info "build-image action success."
}

main::check::check-docker-tag(){
    local docker_tag_prefix=${MAIN_DOCKER_TAG_PREFIX}
    if [[ -z ${docker_tag_prefix} && -n ${MAIN_CONS_DOCKER_TAG_NAMESPACE} ]]; then
        docker_tag_prefix="${MAIN_CONS_DOCKER_TAG_NAMESPACE}"
    fi
    if [[ -z "${docker_tag_prefix}" ]]; then
        log_error "docker_tag_prefix arg not set."
        main::abort
    fi

    if [[ -z ${MAIN_APP_MODULE_NAME} || -z ${MAIN_APP_PARENT_NAME} || -z ${MAIN_APP_MODULE_JAR_VERSION} ]]; then
        log_error "Please do package action first."
        main::abort
    fi
    local current_time_in_seconds=$(date '+%Y%m%d%H%M%S')
    readonly MAIN_DOCKER_TAG_NAME="${docker_tag_prefix}/${MAIN_APP_PARENT_NAME}:${MAIN_APP_MODULE_NAME}-${MAIN_APP_MODULE_JAR_VERSION}-${current_time_in_seconds}"

    log_info "app docker tag: ${MAIN_DOCKER_TAG_NAME}"
}

main::action::package(){
  log_info "package action start."

  main::check::check-maven
  main::check::check-module "$@"

  local maven_args=${MAIN_MAVEN_PACKAGE_ARGS}
  if [[ -z ${maven_args} ]]; then
      maven_args=${MAIN_CONS_MAVEN_PACKAGE_ARGS}
  fi

  #执行maven打包
  mvn -T 4 -B -pl "${MAIN_APP_MODULE_NAME}" -am clean package ${maven_args} -U -e

  readonly MAIN_APP_MODULE_JAR_PATH=$(find "${MAIN_APP_MODULE_PATH}"/*/"${MAIN_APP_MODULE_NAME}"-*.jar | awk '!/-sources/')
  readonly MAIN_APP_MODULE_JAR_NAME=$(find "${MAIN_APP_MODULE_PATH}"/*/"${MAIN_APP_MODULE_NAME}"-*.jar | awk '!/-sources/' | awk -F / '{print $NF}')
  readonly MAIN_APP_MODULE_JAR_VERSION=$(echo "${MAIN_APP_MODULE_JAR_NAME}" | awk -F "${MAIN_APP_MODULE_NAME}"'-' '{print $2}' | awk -F '.jar' '{print $1}')

  log_info "app module jar path: ${MAIN_APP_MODULE_JAR_PATH}"
  log_info "app module jar name: ${MAIN_APP_MODULE_JAR_NAME} , jar version: ${MAIN_APP_MODULE_JAR_VERSION}"

  log_info "package action success."
}

main::check::check-docker(){
    main::check::check-module "$@"

    local app_module_dockerfile="${MAIN_APP_MODULE_PATH}/target/Dockerfile"
    if [[ ! -f "${app_module_dockerfile}" ]]; then
        log_error "Please add [launcher-maven-plugin] support. Visit 'https://github.com/guanyang/spring-launcher-parent' for more information."
        main::abort
    fi
    readonly MAIN_APP_MODULE_DOCKERFILE=${app_module_dockerfile}

    log_info "app module dockerfile: ${MAIN_APP_MODULE_DOCKERFILE}"
}

main::check::check-module(){
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
}

main::check::check-maven(){
  #检查maven版本
  local mvn_ver=$(mvn -version)

  if [[ -z "${mvn_ver}" ]]; then
      log_error "mvn var not set."
      main::abort
  else
      echo "${mvn_ver}"
  fi
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
    log_warn "Terminate main command."
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

log_info "main func args: ${MAIN_COMMAND_ARGS}"

main "$@"