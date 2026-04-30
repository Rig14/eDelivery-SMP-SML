#!/bin/bash
set -euo pipefail

: "${NODE_ID:=$HOSTNAME}"
: "${LOG_LEVEL:=INFO}"
: "${LOG_OUTPUT:=stderr}"
: "${LOG_TIMESTAMP_FMT:=%Y-%m-%dT%H:%M:%S.%6NZ}"
: "${LOG_FILE:=/var/log/harmony.log}"
export NODE_ID LOG_LEVEL LOG_OUTPUT LOG_TIMESTAMP_FMT LOG_FILE

source /usr/lib/harmony-common

# shellcheck disable=SC2034 # Used in sourced scripts
readonly STAGE="harmony-entrypoint"

HARMONY_VERSION="${HARMONY_VERSION:-UNDEFINED}"

log INFO "Starting Harmony Access Point version $HARMONY_VERSION"
log INFO "    User UID: $(id -u)"
log INFO "    User GID: $(id -g)"

# If the user passes "init" in the CLI, we activate the special mode and
# remove the argument before calling /init so that s6-overlay starts normally.
if [ "${1:-}" = "init" ]; then
  log WARN "Harmony Access Point init mode activated"
  export HARMONY_MODE=init
  shift
fi

# auto config side process
(
  until [ "$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080)" = "200" ]; do
    sleep 1
    echo "Service is starting, please wait"
  done

  echo "Service ready, starting auto configuration"

  DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
  source "$DIR/configure.sh"
  upload_pmode "$PARTY_NAME"
  set_keystore "$PARTY_NAME"
  set_smp_truststore "$PARTY_NAME"
  [ -n "$SML_ZONE" ] && set_sml_zone "$SML_ZONE"
  [ -n "$PLUGIN_USER_NAME" ] && [ -n "$PLUGIN_USER_PASSWORD" ] && setup_plugin_user "$PLUGIN_USER_NAME" "$PLUGIN_USER_PASSWORD"

  echo "Configuration finished"
) &

exec /init "$@"