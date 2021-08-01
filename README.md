# Graalvm Microservice Demo

# JDK 16, Gradle Spring Boot native, Quarkus native image using Docker and container microservice

### WHAT IS A NATIVE IMAGE? 
Using Graalvm can help to create native applications.
It means you can run the code directly on the machine using its machine codes.
The application compiling to the host machine executable and can directly run over the host machine.

## History of native images and JVM perspective
Traditionally applications required to be multi-agent, means running on multiple machines 
with different architecture and Operating system, but written once. 
Java applications for instance used JDK to develop java applications, and Java compiler 
called JIT compiler (Just-In-Time compiler) responsible to compile Java codes into 
classes formally formatted into bytecodes, an intermediate language format only being
used in JVM. JRE (Java runtime environment) is responsible to load classes and runs 
machine specific codes in JVM. 

As time passes, the size of the JVM host in the machine grosses as well.
So is when using microservice architecture which only runs one application,
JVM is big and created to run as many applications as possible within a machine.
So JVM is potentially able to allocate any resources to a specific Java application
at runtime, when required and gain-back resources allocated when applications finish job.
Therefore, JVM is a physical machine represented virtually to the application,
and can potentially deliver a massive features to any kind of need.

As time grown, and the need of microservice architecture sensed,
it felt unnecessary to load a huge JVM with all its capabilities to serve a single application.
Because as microservice architecture stated, each microservice should only do one thing,
and it means only one application need to run on a machine.
In other hand, containers showed some interest among developers,
where one can use it as a way of sharing resources in a huge cluster, Cloud or server (or main-frame).
Docker as a powerful tool to helped to create these containers and helped to defined images to deliver
abstractions over creation of these containers for different applications with different definitions.
For an enterprise business as an example, as the number of services increased over time, the number of containers 
increased dramatically. Since all containers are shipped in a Cluster node which could be 
one or many servers, technically sharing resources like RAM and CPU was a challenge.

As we said, JVM required more resources, so for a Java application to run a simple 
task a minimum of hundreds of Megabyte RAM needed, nevertheless, some Java applications 
are written using common frameworks like Spring Boot, Quarkus, Micronaut and so on,
and with using these frameworks eventually adds more resources needless to say because of all the dependencies and libraries bundled wth application.
This ends up to occupies more RAM around some Gigabytes for each application in Cluster node.
Since cluster node have finite amount of RAM, one is limited to number of applications 
run on it as well. There is also crucial to say more RAM means more cost for general Cloud providers. 
In other hand limits our services,since we're limited by the number of applications sharing one RAM. 

The solution would be using native approach. This helps to only compile codes of applications
to the remote host specific machine code, and generate executable.
With that we achieve is a single small executable application and this has several benefits for us.
Not only we shrank the application size dramatically, we also adopted our applications 
to use RAM friendly. Also, JRE does not need to load classes for us in runtime.
This has also some benefits including security. (security ?? yes)
Loading classes in runtime is dangerous. It can lead to an unprotected way of 
loading malicious codes transferred to bytecode blindly and runes in runtime 
and can lead to several unwilling actions. 

This is a great feature, though as time passed, the JVM role in microservices architecture changed 
to a statistically bounded libraries attached to the host application.
Another worth mentioning aspect of  native images is , it actually only include those libraries, and even class and method within 
classes when needed. This helps all together to shrink the size of the final executable application
to some megabytes.

### Quarkus, Spring Boot, micronaut all run in native.
This project contains different microservices project using different frameworks like Quarkus, Spring Boot , Micronaut.

### Demo app
Demo consists of multiple projects. It in a real production ready scenario.

### Docker and Quarkus native and Spring Boot native applications
Enabling projects in a dockerized environment building containers using Docker images
is a hard work. I wanted to compare the performance of each framework using this
demo project. It based-on JDK 16, Gradle 7, Spring Boot 2.5.2 and Quarkus 2.0.1
as sub-projects, using single based Docker image for all sub-projects.
This is very efficient when developing more sub-projects since one need
to derive the base-docker image and just add few lines of Docker code to 
make the project native image compatible!





