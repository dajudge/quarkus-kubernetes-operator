FROM oracle/graalvm-ce

ADD *.jar /app/app.jar
ADD lib/* /app/lib/

CMD ["java", "-jar", "/app/app.jar"]