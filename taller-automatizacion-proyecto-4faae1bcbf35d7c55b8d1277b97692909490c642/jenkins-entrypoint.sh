#!/bin/bash
SONAR_TOKEN_FILE="/sonar-init/sonar-token.txt"
if [ -f "$SONAR_TOKEN_FILE" ]; then
  export SONAR_TOKEN=$(cat $SONAR_TOKEN_FILE)
fi
exec /usr/bin/tini -- /usr/local/bin/jenkins.sh "$@"

