#!/usr/bin/env bash
# not support reload

if [[ "${!TEST_ENV[@]}" ]]; then
  source /apps/sh/test_env.sh
fi

sed -i 's#^JAVA_OPTS="-XX#JAVA_OPTS="$JAVA_OPTS -XX#' bin/gateway-server-default.sh

/bin/bash bin/gateway-server-default.sh $@
