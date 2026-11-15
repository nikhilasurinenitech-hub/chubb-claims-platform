#!/bin/sh
set -eu
RESOLVER="$(awk '$1 == "nameserver" {print $2; exit}' /etc/resolv.conf)"
test -n "$RESOLVER"
case "$RESOLVER" in *:*) RESOLVER="[$RESOLVER]";; esac
export RESOLVER
# Substitute only the resolver; preserve Nginx variables in the template.
envsubst '$RESOLVER $CLAIMS_UPSTREAM $INSIGHTS_UPSTREAM' < /etc/nginx/templates/default.conf.template > /tmp/default.conf
exec nginx -c /etc/nginx/claimspace-nginx.conf -g 'daemon off;'
