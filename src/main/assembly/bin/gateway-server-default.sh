#!/bin/bash

ulimit -s 20480
ulimit -c unlimited
export PATH=$PATH:/usr/sbin

PRG="$0"
PRGDIR=$(dirname "$PRG")
BASEDIR=$(
  cd "$PRGDIR/.." >/dev/null
  pwd
)

STATUS_FILE=${PRGDIR}/status
PID_FILE=${PRGDIR}/PID

LOGDIR=${BASEDIR}/logs
APP_LISTEN_PORT=8080
JMX_PORT=10086
SERVER_API=StreamSQL-gateway
START_TIME=120
TARGET_VERSION=1.8

USAGE() {
  echo "usage: $0 start|stop|restart|status|info [-p|--port port] [-j|--jmx-port port] [-l|--log-dir dir] [-s|--server-api name] [-t|--start-timeout time] [-r|--restful-port port] [-e|--environment environment] [-a|--aysnc fiber] [additional jvm args, e.g. -Dnoah.loglevel=111 -Xmx2048m]"
}

if [ $# -lt 1 ]; then
  USAGE
  exit -1
fi

CMD="$1"
shift

while true; do
  case "$1" in
  -p | --port)
    APP_LISTEN_PORT="$2"
    shift 2
    ;;
  -j | --jmx-port)
    JMX_PORT="$2"
    shift 2
    ;;
  -l | --log-dir)
    LOGDIR="$2"
    shift 2
    ;;
  -s | --server-api)
    SERVER_API="$2"
    shift 2
    ;;
  -t | --start-timeout)
    START_TIME="$2"
    shift 2
    ;;
  -e | --environment)
    RUN_ENVIRONMENT="$2"
    shift 2
    ;;
  *) break ;;
  esac
done

ADDITIONAL_OPTS=$*

JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')

if [[ "$RUN_ENVIRONMENT" == "dev" ]]; then
  ENVIRONMENT_MEM="-Xms512m -Xmx512m -Xss256K -XX:MaxDirectMemorySize=1024m"
else
  ENVIRONMENT_MEM=${ENVIRONMENT_MEM:-"-Xmx9182m -XX:MaxDirectMemorySize=1024m"}
fi

if [ -d /dev/shm/ ]; then
  GC_LOG_FILE=/dev/shm/gc-$SERVER_API-$APP_LISTEN_PORT.log
else
  GC_LOG_FILE=${LOGDIR}/gc-$SERVER_API-$APP_LISTEN_PORT.log
fi

JAVA_OPTS="-XX:+PrintCommandLineFlags -XX:-OmitStackTraceInFastThrow -XX:-UseBiasedLocking -XX:-UseCounterDecay -XX:AutoBoxCacheMax=20000 -XX:+PerfDisableSharedMem  -Djava.security.egd=file:/dev/./urandom"
MEM_OPTS="-server ${ENVIRONMENT_MEM}  -XX:+ParallelRefProcEnabled -XX:+AlwaysPreTouch -XX:MaxTenuringThreshold=10"
GCLOG_OPTS="-Xloggc:${GC_LOG_FILE} -XX:+UseG1GC -XX:-UseGCOverheadLimit -XX:+UseAdaptiveSizePolicy -XX:+UseCompressedOops -XX:+UseCompressedClassPointers -verbose:gc -XX:+PrintGCApplicationStoppedTime -XX:+PrintGCApplicationConcurrentTime -XX:+PrintGCDateStamps -XX:+PrintGCDetails -XX:+PrintAdaptiveSizePolicy -XX:+PrintCommandLineFlags -XX:+ExplicitGCInvokesConcurrent  -XX:HeapDumpPath=/apps/logs/vipcloud"
CRASH_OPTS="-XX:ErrorFile=${LOGDIR}/hs_err_%p.log -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${LOGDIR}/"
JMX_OPTS="-Dcom.sun.management.jmxremote.port=${JMX_PORT} -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dsun.rmi.transport.tcp.threadKeepAliveTime=75000 -Djava.rmi.server.hostname=127.0.0.1"
OTHER_OPTS="-Dstart.check.outfile=${STATUS_FILE} -Dserver.port=$APP_LISTEN_PORT -Djava.net.preferIPv4Stack=true"

#if [[ "$JAVA_VERSION" < "1.7" ]]; then
#    echo "Error: Unsupported the java version $JAVA_VERSION , please use the version $TARGET_VERSION and above."
#    exit -1;
#fi

if [[ "$JAVA_VERSION" < "1.8" ]]; then
  MEM_OPTS="$MEM_OPTS -XX:PermSize=256m -XX:MaxPermSize=256m -XX:ReservedCodeCacheSize=240M -XX:+PrintGCApplicationConcurrentTime"
else
  MEM_OPTS="$MEM_OPTS -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=256m -XX:ReservedCodeCacheSize=240M"
  JAVA_OPTS="$JAVA_OPTS -XX:-TieredCompilation "
fi

BACKUP_GC_LOG() {
  GCLOG_DIR=${LOGDIR}
  BACKUP_FILE="${GCLOG_DIR}/gc-${SERVER_API}-${APP_LISTEN_PORT}_$(date +'%Y%m%d_%H%M%S').log"

  if [ -f ${GC_LOG_FILE} ]; then
    echo "saving gc log ${GC_LOG_FILE} to ${BACKUP_FILE}"
    mv ${GC_LOG_FILE} ${BACKUP_FILE}
  fi
}

GET_PID_BY_ALL_PORT() {
  echo $(lsof -n -P -i :${APP_LISTEN_PORT},${JMX_PORT} | grep LISTEN | awk '{print $2}' | head -n 1)
}

STOP() {
  BACKUP_GC_LOG

  if [ -f $PID_FILE ]; then
    PID=$(cat $PID_FILE)
  else
    PID=$(GET_PID_BY_ALL_PORT)
  fi
  if [ "$PID" != "" ]; then
    if [ -d /proc/$PID ]; then
      LISTEN_STATUS=$(cat ${STATUS_FILE})
      echo "$SERVER_API stopping, ${LISTEN_STATUS}."
      kill $PID
      sleep 9

      if [ x"$PID" != x ]; then
        echo -e "$SERVER_API still running as process:$PID \c"
      fi

      while [ -d /proc/$PID ]; do
        echo -e ".\c"
        sleep 1
      done

      echo -e "\n$SERVER_API stop successfully"
    else
      echo "$SERVER_API is not running."
    fi
  else
    echo "$SERVER_API is not running."
  fi
}

START() {
  BACKUP_GC_LOG
  echo "" >${STATUS_FILE}

  if [ -f $PID_FILE ]; then
    PID=$(cat $PID_FILE)
  fi
  if [ "$PID" != "" ]; then
    if [ -d /proc/$PID ]; then
      echo "$SERVER_API is running, please stop it first!!"
      exit -1
    fi
  fi

  if [ ! -d $LOGDIR ]; then
    echo "Warning! The logdir: $LOGDIR not existed! Try to create the dir automatically."
    mkdir $LOGDIR
    if [ -d $LOGDIR ]; then
      echo "Create logdir: $LOGDIR successed!"
    else
      echo "Create logdir: $LOGDIR failed, please check it!"
      exit -1
    fi
  fi

  LISTEN_STATUS="port is ${APP_LISTEN_PORT}, JMX port is ${JMX_PORT}"

  echo "$SERVER_API starting, ${LISTEN_STATUS}."

  java $JAVA_OPTS $MEM_OPTS $GCLOG_OPTS $JMX_OPTS $CRASH_OPTS $OTHER_OPTS $ADDITIONAL_OPTS -jar ${BASEDIR}/StreamSQL-gateway.jar
  # java $JAVA_OPTS $MEM_OPTS $GCLOG_OPTS $JMX_OPTS $CRASH_OPTS $OTHER_OPTS $ADDITIONAL_OPTS -jar ${BASEDIR}/StreamSQL-gateway.jar >>$LOGDIR/$SERVER_API.out 2>&1

  PID=$!
  echo $PID >$PID_FILE

  sleep 3

  # #903 "if[ ! -d /proc/$PID ]" will not evaluate correctly on MACOS, change to "if ! ps -p $PID > /dev/null";
  if ! ps -p $PID >/dev/null; then
    echo -e "\n$SERVER_API start may be unsuccessful, process exited immediately after starting, might be JVM parameter problem or JMX port occupation! See ${LOGDIR}/${SERVER_API}.out for more information."
    exit -1
  fi

  CHECK_STATUS=$(cat ${STATUS_FILE})
  starttime=0
  while [ x"$CHECK_STATUS" == x ]; do
    if [[ "$starttime" -lt ${START_TIME} ]]; then
      sleep 1
      ((starttime++))
      echo -e ".\c"
      CHECK_STATUS=$(curl 127.0.0.1:$APP_LISTEN_PORT/_health -s)
    else
      echo -e "\n$SERVER_API start maybe unsuccess, start checking not finished until reach the starting timeout! See ${LOGDIR}/${SERVER_API}.out for more information."
      exit -1
    fi
  done
  echo "gateway Server status is $CHECK_STATUS"
  if [ $CHECK_STATUS = "ok" ]; then
    echo -e "\n$SERVER_API start successfully, running as process:$PID."
    echo ${LISTEN_STATUS} >${STATUS_FILE}
  fi

  if [ $CHECK_STATUS != "ok" ]; then
    kill $PID
    echo -e "\n$SERVER_API start failed ! See ${LOGDIR}/${SERVER_API}.out for more information."
  fi

}

STATUS() {
  if [ -f $PID_FILE ]; then
    PID=$(cat $PID_FILE)
  fi
  if [ "$PID" != "" ]; then
    if [ -d /proc/$PID ]; then
      LISTEN_STATUS=$(cat ${STATUS_FILE})
      echo "$SERVER_API is running ,${LISTEN_STATUS}."
      exit 0
    fi
  fi
  echo "$SERVER_API is not running."
}

INFO() {
  echo "gateway server shell."
  exit 0
}

case "$CMD" in
stop) STOP ;;
start) START ;;
restart)
  STOP
  sleep 3
  START
  ;;
status) STATUS ;;
info) INFO ;;
help) USAGE ;;
*) USAGE ;;
esac
