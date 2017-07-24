#!/bin/bash

environmentName="$1"
targetVersion="$2"
healthcheckUrl="$3"
region="eu-west-1"

echo "target version: $targetVersion"
echo "health check url: $healthcheckUrl"

attempts=0

until [ $attempts -gt 100 ]; do
  environment="$(aws elasticbeanstalk describe-environments --region "$region" --environment-name "$environmentName" | jq '.Environments[0]')"
  version="$(echo "$environment" | jq -r .VersionLabel)"
  status="$(echo "$environment" | jq -r .Status)"
  echo "version: $version, status: $status"
  ((attempts+=1))
  if [ "$version" == "$targetVersion" ] && [ "$status" == 'Ready' ]; then
    echo "new version is live"
    if curl -k --fail $healthcheckUrl; then
      echo "all good"
      exit 0;
    else
      echo "healthcheck failed"
      exit 1;
    fi
  fi
  echo "waiting for deployment, attempt $attempts"
  sleep 15
done

echo "giving up waiting"
exit 1
