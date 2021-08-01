FROM graalvm16-linux as post-analyzer-frontend

ENV JAVA_HOME=/graalvm/
ENV GRADLE_HOME=/usr/bin/${GRADLE_VERSION}
ENV PATH=$PATH:$GRADLE_HOME/bin
ENV PATH=$PATH:$JAVA_HOME/bin

WORKDIR ~/app/post-analyzer-top-twitter-api

COPY . .

ENV JAVA_TOOL_OPTIONS="-Xms512m"

#RUN gradle clean bootBuildImage

EXPOSE 8080

#CMD ["./build/post-analyzer-twitter-api-1.0-SNAPSHOT-runner", "-Xmn32m","-Djavax.net.ssl.trustStore=/tmp/ssl-libs/cacerts"]
