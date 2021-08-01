FROM graalvm16-linux as build

ENV JAVA_HOME=/graalvm/
ENV GRADLE_HOME=/usr/bin/${GRADLE_VERSION}
ENV PATH=$PATH:$GRADLE_HOME/bin
ENV PATH=$PATH:$JAVA_HOME/bin

WORKDIR ~/app/bookstore

COPY . .

RUN gradle clean buildNative

EXPOSE 8080

CMD ["./build/bookstore-1.0-SNAPSHOT-runner", "-Xmn32m","-Djavax.net.ssl.trustStore=/tmp/ssl-libs/cacerts"]
