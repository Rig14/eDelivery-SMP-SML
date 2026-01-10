FROM eclipse-temurin:21-alpine AS build-server
WORKDIR /app

COPY . ./
RUN --mount=type=cache,target=/root/.gradle ./gradlew testClasses jar --info

# The final image
FROM eclipse-temurin:21-jre-alpine AS final
RUN adduser -S user
RUN rm -fr /usr/sbin /bin/ch*

WORKDIR /app
COPY --from=build-server /app/build/libs ./

ARG VERSION=dev
ENV VERSION=$VERSION

USER user

ENV TZ=Europe/London
ENV JAVA_TOOL_OPTIONS="-Xss256K -Xmx330M -XX:+ExitOnOutOfMemoryError"
CMD java -jar *.jar

ENV PORT=8080
EXPOSE $PORT