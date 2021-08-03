FROM graalvm16-linux as post-analyzer-processer

ENV JAVA_HOME=/graalvm/
ENV PATH=$PATH:$JAVA_HOME/bin

WORKDIR ~/app/post-analyzer-processor

RUN mkdir src

COPY . src

RUN cd src \
    && chmod +x gradlew \
    && rm -rf build \
    && ./gradlew clean build \
    && cp build/libs/*-SNAPSHOT.jar ../post-analyzer-processor.jar \
    && rm -rf *

RUN native-image -jar post-analyzer-processor.jar post-analyzer-processor

RUN rm -rf ~/.gradle

FROM post-analyzer-processer as runner

USER 1001

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    JAVA_OPTS="-Xms256m -Xmx2048m" \
    SLEEP=1 \
    SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

CMD echo "The twitter post processor will start in ${SLEEP}s..." && \
    sleep ${SLEEP} && \
    ./post-analyzer-processor

