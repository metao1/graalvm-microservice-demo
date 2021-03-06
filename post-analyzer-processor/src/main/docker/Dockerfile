FROM graalvm16-linux as post-analyzer-processer

ENV JAVA_HOME=/graalvm/
ENV PATH=$PATH:$JAVA_HOME/bin

WORKDIR /code

COPY . .

RUN curl https://repo1.maven.org/maven2/io/fabric8/run-java-sh/${RUN_JAVA_VERSION}/run-java-sh-${RUN_JAVA_VERSION}-sh.sh -o run-java.sh

RUN chmod +x gradlew \
    && ./gradlew clean build \
    && cp build/libs/*-SNAPSHOT.jar app.jar

FROM post-analyzer-processer as runner

WORKDIR /application

ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en'

COPY --from=post-analyzer-processer /code/app.jar /application/app.jar
COPY --from=post-analyzer-processer /code/run-java.sh /application/run-java.sh

RUN   chown 1001 /application \
    && chmod "g+rwX" /application \
    && chown 1001:root /application \
    && chown 1001 /application/run-java.sh \
    && chmod 540 /application/run-java.sh \
    && echo "securerandom.source=file:/dev/urandom" >> /etc/alternatives/jre/lib/security/java.security

ENV JAVA_OPTIONS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"

USER 1001

EXPOSE 8080

ENTRYPOINT [ "/bin/run-java.sh" ]
