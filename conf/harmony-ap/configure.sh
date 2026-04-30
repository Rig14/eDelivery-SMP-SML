#!/bin/bash

authenticate() {
  local RESPONSE
  RESPONSE=$(curl -s -i -X POST http://localhost:8080/rest/security/authentication \
    -H "Content-Type: application/json" \
    -d '{"username":"harmony","password":"secret"}') || { echo "Auth request failed"; return 1; }

  local JSESSIONID XSRF_TOKEN
  JSESSIONID=$(echo "$RESPONSE" | grep -i '^Set-Cookie:' | grep -o 'JSESSIONID=[^;]*' | cut -d= -f2)
  XSRF_TOKEN=$(echo "$RESPONSE" | grep -i '^Set-Cookie:' | grep -o 'XSRF-TOKEN=[^;]*' | cut -d= -f2)

  [ -z "$JSESSIONID" ] || [ -z "$XSRF_TOKEN" ] && { echo "Failed to get cookies"; return 1; }

  echo "$JSESSIONID" "$XSRF_TOKEN"
}

upload_pmode() {
  local PARTY="$1"
  echo "Uploading PMODE for $PARTY"

  DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
  local PMODE_FILE="$DIR/pmode-$PARTY.xml"
  [ -f "$PMODE_FILE" ] || { echo "File $PMODE_FILE does not exist"; return 1; }

  read -r JSESSIONID XSRF_TOKEN < <(authenticate) || return 1

  local UPLOAD_RESPONSE
  UPLOAD_RESPONSE=$(curl -s -w "%{http_code}" -o /dev/null -X POST http://localhost:8080/rest/pmode \
    -H "Cookie: JSESSIONID=$JSESSIONID; XSRF-TOKEN=$XSRF_TOKEN" \
    -H "X-XSRF-TOKEN: $XSRF_TOKEN" \
    -F "file=@${PMODE_FILE};type=text/xml" \
    -F "description=PMODE configuration for $PARTY") || { echo "Upload request failed"; return 1; }

  if [ "$UPLOAD_RESPONSE" -ge 200 ] && [ "$UPLOAD_RESPONSE" -lt 300 ]; then
    echo "PMODE uploaded for $PARTY"
  else
    echo "PMODE upload failed with status $UPLOAD_RESPONSE"
    return 1
  fi
}

setup_plugin_user() {
  local NAME="$1"
  local PASSWORD="$2"

  read -r JSESSIONID XSRF_TOKEN < <(authenticate) || return 1
  UPLOAD_RESPONSE=$(curl -s -w "%{http_code}" -o /dev/null -X PUT http://localhost:8080/rest/plugin/users \
    -H "Cookie: JSESSIONID=$JSESSIONID; XSRF-TOKEN=$XSRF_TOKEN" \
    -H "X-XSRF-TOKEN: $XSRF_TOKEN" \
    -H "Content-Type: application/json" \
    --data-raw "[{\"status\":\"NEW\",\"userName\":\"$NAME\",\"active\":true,\"suspended\":false,\"authenticationType\":\"BASIC\",\"originalUser\":\"$NAME\",\"authRoles\":\"ROLE_ADMIN\",\"password\":\"$PASSWORD\"}]" \
    || { echo "Upload request failed"; return 1; })

  if [ "$UPLOAD_RESPONSE" -ge 200 ] && [ "$UPLOAD_RESPONSE" -lt 300 ]; then
    echo "Setup of plugin user succeeded"
  elif [ "$UPLOAD_RESPONSE" -eq 409 ]; then
    echo "Plugin user is already configured"
  else
    echo "Setting up plugin user failed with code $UPLOAD_RESPONSE"
    return 1
  fi
}

set_sml_zone() {
  local ZONE_NAME="$1"

  read -r JSESSIONID XSRF_TOKEN < <(authenticate) || return 1
  UPLOAD_RESPONSE=$(curl -s -w "%{http_code}" -o /dev/null -X PUT http://localhost:8080/rest/configuration/properties/domibus.smlzone?isDomain=true \
    -H "Cookie: JSESSIONID=$JSESSIONID; XSRF-TOKEN=$XSRF_TOKEN" \
    -H "X-XSRF-TOKEN: $XSRF_TOKEN" \
    -H "Content-Type: application/json" \
    --data-raw "\"$ZONE_NAME\"") || { echo "Upload request failed"; return 1; }

  if [ "$UPLOAD_RESPONSE" -ge 200 ] && [ "$UPLOAD_RESPONSE" -lt 300 ]; then
    echo "SML zone updated successfully"
  else
    echo "Updating SML zone failed with code $UPLOAD_RESPONSE"
    return 1
  fi
}

upload_store() {
  local LABEL="$1" FILE="$2" ENDPOINT="$3" SUCCESS_MSG="$4"
  [ -f "$FILE" ] || { echo "File $FILE does not exist"; return 1; }

  read -r JSESSIONID XSRF_TOKEN < <(authenticate) || return 1
  local STATUS
  STATUS=$(curl -s -w "%{http_code}" -o /dev/null -X POST "http://localhost:8080/rest/${ENDPOINT}/save" \
    -H "Cookie: JSESSIONID=$JSESSIONID; XSRF-TOKEN=$XSRF_TOKEN" \
    -H "X-XSRF-TOKEN: $XSRF_TOKEN" \
    -F "file=@${FILE};type=application/pkcs12" \
    -F "password=changeit") || { echo "Upload request failed"; return 1; }

  if [ "$STATUS" -ge 200 ] && [ "$STATUS" -lt 300 ]; then
    echo "$SUCCESS_MSG"
  else
    echo "${LABEL} upload failed with status $STATUS"
    return 1
  fi
}

set_keystore() {
  local PARTY="$1"
  upload_store "Keystore" "/etc/harmony-ap-certs/${PARTY}/keystore.p12" "keystore" "Keystore uploaded for $PARTY"
}

set_truststore() {
  local LABEL="$1" PARTY="$2"
  upload_store "Truststore" "/etc/harmony-ap-certs/${PARTY}/keystore.p12" "truststore" "Truststore uploaded for $LABEL"
}