FROM graalvm16-linux as post-analyzer-api

ENV JAVA_HOME=/graalvm/
ENV PATH=$PATH:$JAVA_HOME/bin

WORKDIR ~/app/post-analyzer-api

RUN mkdir src

COPY . src

RUN cd src \
    && chmod +x gradlew \
    && rm -rf build \
    && ./gradlew clean build \
    && cp build/libs/*-SNAPSHOT.jar ../post-analyzer-api.jar \
    && rm -rf *

RUN native-image -jar post-analyzer-api.jar post-analyzer-api

RUN rm -rf ~/.gradle

FROM post-analyzer-api as runner

USER 1001

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    JAVA_OPTS="-Xms256m -Xmx2048m" \
    SLEEP=1 \
    SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

CMD echo "The post analyzer api will start in ${SLEEP}s..." && \
    sleep ${SLEEP} && \
    ./post-analyzer-api

