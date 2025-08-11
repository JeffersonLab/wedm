ARG BUILD_IMAGE=gradle:9-jdk21-alpine
ARG RUN_IMAGE=tomcat:11-jdk21

################## Stage 0
FROM ${BUILD_IMAGE} AS builder
ARG CUSTOM_CRT_URL
USER root
WORKDIR /
RUN if [ -z "${CUSTOM_CRT_URL}" ] ; then echo "No custom cert needed"; else \
       wget -O /usr/local/share/ca-certificates/customcert.crt $CUSTOM_CRT_URL \
       && update-ca-certificates \
       && keytool -import -alias custom -file /usr/local/share/ca-certificates/customcert.crt -cacerts -storepass changeit -noprompt \
       && export OPTIONAL_CERT_ARG=--cert=/etc/ssl/certs/ca-certificates.crt \
    ; fi
COPY . /app
RUN cd /app && gradle build -x test -x spotlessJavaCheck --no-watch-fs $OPTIONAL_CERT_ARG

################## Stage 1
FROM ${RUN_IMAGE} AS prod
ARG CUSTOM_CRT_URL
ARG RUN_USER=tomcat
ARG APP_HOME=/usr/local/tomcat/webapps
USER root
COPY --from=builder /app/build/libs /usr/local/tomcat/webapps
COPY --from=builder /app/examples/edl /edl
RUN useradd -m tomcat \
    && if [ -z "${CUSTOM_CRT_URL}" ] ; then echo "No custom cert needed"; else \
       mkdir -p /usr/local/share/ca-certificates \
       && curl -o /usr/local/share/ca-certificates/customcert.crt $CUSTOM_CRT_URL \
       && update-ca-certificates \
    ; fi \
    && chown -R ${RUN_USER}:0 ${APP_HOME} \
    && chmod -R g+rw ${APP_HOME}
USER ${RUN_USER}

################## Stage 2
FROM prod AS dev
USER root
RUN apt update \
    && apt install tini git openjdk-21-jdk -y
# Tomcat prioritizes JRE_HOME over JAVA_HOME and we run with jdk8 and build wtih jdk17
ENV JRE_HOME=/opt/java/openjdk
ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
ENTRYPOINT ["tini", "--", "sleep", "infinity"]