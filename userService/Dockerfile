FROM openjdk:8u141-jre
ADD ./build/install/app/ /opt/app/
ENV REMOTE_HOST user-alias-service
ENTRYPOINT ["/opt/app/bin/run.sh"]
