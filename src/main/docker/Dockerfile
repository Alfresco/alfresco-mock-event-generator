FROM alfresco/alfresco-base-java:8u161-oracle-centos-7

ENV SERVICE_JAR=*.jar
ADD $SERVICE_JAR ./
EXPOSE 8080
ENTRYPOINT java -jar $SERVICE_JAR