FROM eclipse-temurin:25-alpine AS build-server
WORKDIR /app

COPY dynamic-discovery ./
RUN --mount=type=cache,target=/root/.gradle ./gradlew testClasses jar --info

# The final image
FROM eclipse-temurin:25-jre-alpine AS final
RUN adduser -S user

WORKDIR /app

COPY conf/certs/smp ./certs
RUN rm -fr /usr/sbin /bin/ch*

COPY --from=build-server /app/build/libs ./

USER user

ENV TZ=Europe/London
ENV JAVA_TOOL_OPTIONS="-Xss256K -Xmx330M -XX:+ExitOnOutOfMemoryError"
ENV PORT=80
ENV SML_PORT=53

CMD java -jar *.jar

EXPOSE $PORT
EXPOSE $SML_PORT/udp