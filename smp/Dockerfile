FROM eclipse-temurin:21-alpine AS build-server
WORKDIR /app

COPY . ./
RUN --mount=type=cache,target=/root/.gradle ./gradlew testClasses jar --info

# The final image
FROM eclipse-temurin:21-jre-alpine AS final
RUN adduser -S user

WORKDIR /app

COPY certs ./certs
RUN chmod +r certs/*
RUN rm -fr /usr/sbin /bin/ch*

COPY --from=build-server /app/build/libs ./

USER user

ENV TZ=Europe/London
ENV JAVA_TOOL_OPTIONS="-Xss256K -Xmx330M -XX:+ExitOnOutOfMemoryError"
CMD PORT=80 java -jar *.jar

ENV PORT=80
EXPOSE $PORT