#!/bin/sh

# Capture the argument passed from CMD (default: "dev") and set it to the Spring profile
SPRING_PROFILE=${SPRING_PROFILE:-dev}

# Run the Java application with the specified Spring profile
exec java -jar -Dspring.profiles.active=${SPRING_PROFILE} app.jar