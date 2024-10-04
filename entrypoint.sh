#!/bin/sh

# Capture the argument passed from CMD (default: "dev") and set it to the Spring profile
SPRING_PROFILE=${SPRING_PROFILE:-dev}

# Check if the secrets.yaml file exists and print appropriate message
if [ -f ./config/secrets/secrets.yaml ]; then
  echo "HAS SECRET!"
else
  echo "OH MY GOD"
fi

# Run the Java application with the specified Spring profile
exec java -jar -Dspring.profiles.active=${SPRING_PROFILE} app.jar