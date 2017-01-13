FROM nginx:alpine

MAINTAINER Stefan Negru <stefan.negru@helsinki.fi>

EXPOSE 5039
EXPOSE 5639

RUN apk add --update \
    openjdk8-jre \
    bash \
    wget \
  && rm -rf /var/cache/apk/*

VOLUME ["/data"]

ENV version="0.3.38" plugin="0.1"

COPY nginx.conf /etc/nginx/nginx.conf

RUN wget -P /var/lib  https://dl.bintray.com/linkedin/maven/com/linkedin/pygradle/pivy-importer/${version}/pivy-importer-${version}-all.jar

RUN wget -P /var http://github.com/blankdots/ivy-pypi/releases/download/v${plugin}/ivy_pipy-${plugin}.tar \
    && tar -xf /var/ivy_pipy-${plugin}.tar -C /var

COPY web /data/web

CMD nginx & \
    ./var/ivy_pipy-${plugin}/bin/ivy_pipy
