#!/usr/bin/env bash

# Constants
readonly MAIN_FILE_PATH=$(readlink -f $0)
readonly MAIN_FILE_DIR=$(dirname "${MAIN_FILE_PATH}")

readonly MAIN_ACTION_TIMESTAMP=$(date '+%Y%m%d%H%M%S')
readonly MAIN_ACTION_TIMESTAMP_IN_SECONDS=$(date '+%s')

readonly MAIN_CONS_DEFAULT_SERVICE_PORT=8080
readonly MAIN_CONS_DEFAULT_SERVICE_JAVA_OPTS="-Dfile.encoding=UTF-8 -Duser.timezone=Asia/Shanghai -XX:InitialRAMPercentage=25.0 -XX:MaxRAMPercentage=50.0 -XX:+UseG1GC -XX:MaxGCPauseMillis=150"

function main {
    readonly MAIN_ACTION=$1
    case $1 in
    "start" | "stop" | "restart" | "status" )
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
                "-jo" | "--java-opts" )
                    PARAM_JAVA_OPTIONS="$2"
                    shift
                    ;;
                "-p" | "--server-port" )
                    PARAM_JAVA_PORT="$2"
                    shift
                    ;;
                "-jbs" | "--javaagent-bs" )
                    PARAM_JAVAAGENT_BS="$2"
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
        echo "Current user: $(whoami)"
        main::func::action "${MAIN_ACTION}"
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
        action_name="start"
    fi
    printf "Usage:  $0 ${action_name} [OPTIONS]\n\n"
    printf "Example:  $0 ${action_name} -n web-demo\n\n"
    printf "${action_name} for an application\n\n"
    printf 'Options:\n'

    #基础参数
    main::print_arg_usage '-n'    '--app-name'          "[Required]Set application name (e.g. -n web-sample)"
    main::print_arg_usage '-var'  '--var-config'        "[Optional]Set system variables (e.g. -var 'APP_SRC_GROUP=DEMO')"
    #运行相关参数
    if [[ ${action_name} = "start" ]]; then
        main::print_arg_usage '-jo'   '--java-opts'     "[Optional]Set Java VM Options for your application (e.g. -jo '-Xmx4g -Xms4g')"
        main::print_arg_usage '-p'    '--server-port'   "[Optional]SpringBoot web container port, port range [1024,65535], default value: 8080 (e.g. -p '8080')"
        main::print_arg_usage '-jbs'  '--javaagent-bs'  "[Optional]Set skywalking agent backend_service for your application (e.g. -jbs '127.0.0.1:11800')"
        main::print_arg_usage '-e'    '--app-env'       "[Optional]Set application environment [prod/gray/pre/test/dev] (e.g. -e dev)"
    fi
    main::print_arg_usage '-h'    '--help'              'Print usage'
}

main::func::action() {
    echo "app ${1} action start."
    local action_name="${1}"
    if [[ -z ${action_name} ]]; then
        action_name="start"
    fi
    #构建模块路径
    main::func::build-module
    #构建运行脚本路径
    main::func::build-launcher-sh
    #构建运行参数
    main::func::build-launcher-args "${action_name}"


    local launcher_args=${MAIN_LAUNCHER_RUN_SH}
    eval "${launcher_args}"

    if [[ ${action_name} = "start" ]]; then
        local java_port=${MAIN_LAUNCHER_RUN_PORT}
        echo "app start success. Visit 'http://127.0.0.1:${java_port}/hello' for more information."
    fi

    echo "app ${1} action end."
}

main::func::build-launcher-sh(){
    #构建launcher.sh脚本路径
    local launcher_sh="${MAIN_APP_MODULE_PATH}/bin/launcher.sh"
    if [[ ! -f "${launcher_sh}" ]]; then
        echo "app launcher file \"${launcher_sh}\" not exists."
        main::abort
    fi
    readonly MAIN_APP_LAUNCHER_PATH=${launcher_sh}

    echo "Launcher path: ${MAIN_APP_LAUNCHER_PATH}"
}

main::func::build-launcher-args(){
    #构建launcher.sh启动参数
    local action_name="${1}"
    local launcher_args="${MAIN_APP_LAUNCHER_PATH} ${action_name} -n ${MAIN_APP_MODULE_NAME}"

    if [[ ${action_name} = "start" ]]; then
          #设置后台运行
          launcher_args+=" -d"
          #添加jvm启动参数
          local java_options="${PARAM_JAVA_OPTIONS:-${MAIN_CONS_DEFAULT_SERVICE_JAVA_OPTS}}"
          if [[ -n ${java_options} ]]; then
              launcher_args+=" -jo '${java_options}'"
          fi

          #设置端口，默认8080
          local java_port="${PARAM_JAVA_PORT:-${MAIN_CONS_DEFAULT_SERVICE_PORT}}"
          main::check_port_range "${java_port}"
          launcher_args+=" -a '--server.port=${java_port}'"

          #设置javaagent_bs
          local javaagent_bs=${PARAM_JAVAAGENT_BS}
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

    echo "Launcher run sh: ${MAIN_LAUNCHER_RUN_SH}"
}

main::func::build-module(){
    if [[ -n ${MAIN_APP_MODULE_NAME} && -n ${MAIN_APP_MODULE_PATH} ]]; then
        echo "app path: ${MAIN_APP_MODULE_PATH} , app name: ${MAIN_APP_MODULE_NAME}"
        return
    fi

    local app_module_name="${PARAM_MODULE_NAME}"
    if [[ -z "${app_module_name}" ]]; then
        echo "app-name arg not set."
        main::abort
    fi

    local app_module_path="${MAIN_FILE_DIR}/${app_module_name}"
    if [[ ! -d "${app_module_path}" ]]; then
        echo "app  path \"${app_module_path}\" not exists."
        main::abort
    fi

    readonly MAIN_APP_MODULE_NAME=${app_module_name}
    readonly MAIN_APP_MODULE_PATH=${app_module_path}

    echo "app path: ${MAIN_APP_MODULE_PATH} , app name: ${MAIN_APP_MODULE_NAME}"
}

main:func::print_usage() {
cat << EOF
Usage:  $0 COMMAND [arg...]

Commands:
    start       Start an application
    stop        Stop an application
    restart     Restart an application
    status      Show application status
EOF
}

main::print_arg_usage() {
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

main::check_port_range() {
    local port=${1}
    if [[ ${port} =~ ^[0-9]+$ && ${port} -gt 0 && ${port} -lt 65535 ]];then
        return 0
    else
        echo "Invalid port '${port}', port should be a number between 1024 to 65535"
        main::abort
    fi
}

main::abort() {
    echo "Terminate main command."
    exit 1
}

readonly MAIN_COMMAND_ARGS="$@"

echo "launcher service args: ${MAIN_COMMAND_ARGS}"

main "$@"