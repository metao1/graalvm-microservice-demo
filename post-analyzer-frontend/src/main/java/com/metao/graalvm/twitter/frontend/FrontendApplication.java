package com.metao.graalvm.twitter.frontend;


import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class FrontendApplication {
    public static void main(String ... args) {
        System.out.println("Running frontend service");
        Quarkus.run(args);
    }
}
