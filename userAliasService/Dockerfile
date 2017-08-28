FROM openjdk:8u141-jre
ADD ./build/install/app/ /opt/app/
ENV SERVICE_HOST_BIND user-alias-service
ENTRYPOINT ["/opt/app/bin/run.sh"]
