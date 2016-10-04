FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/xpertview.jar /xpertview/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/xpertview/app.jar"]
