FROM openjdk:8-jre-alpine

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    JHIPSTER_SLEEP=0 \
    JAVA_OPTS=""

ARG profile
ARG port
ENV profile_env=$profile
ENV profile_port=$port
ENV AWS_CREDENTIAL_PROFILES_FILE=/etc/.aws/credentials
ADD src/main/resources/cert/clientcert.p12  /etc/ssl/certs/clientcert.p12
ADD src/main/resources/cert/_.dce.ec_Thawte_RSA_CA_2018_.cer /etc/ssl/certs/java/_.dce.ec_Thawte_RSA_CA_2018_.cer
ADD src/main/resources/.aws/credentials $AWS_CREDENTIAL_PROFILES_FILE

RUN keytool -import -file /etc/ssl/certs/java/_.dce.ec_Thawte_RSA_CA_2018_.cer -keystore $JAVA_HOME/lib/security/cacerts -alias chatbotcertbackend -noprompt  -storepass changeit

#RUN keytool -importkeystore -srckeystore /etc/ssl/certs/wildcard.dce.ec.pfx -srcstoretype pkcs12 -destkeystore /etc/ssl/certs/clientcert.jks -deststoretype JKS -destalias chatbotcertbackend -srcstorepass Pentasec1200 -destkeypass dinersbackend -noprompt

CMD echo "The application will start in ${JHIPSTER_SLEEP}s..." && \
   sleep ${JHIPSTER_SLEEP} && \
    java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar -Dspring.profiles.active=${profile_env} /app.war

EXPOSE ${profile_port}

ADD build/libs/*.war /app.war
