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

ENV version="0.4.1" plugin="0.1.1"

COPY nginx.conf /etc/nginx/nginx.conf

RUN wget -P /var/lib  https://dl.bintray.com/linkedin/maven/com/linkedin/pygradle/pivy-importer/${version}/pivy-importer-${version}-all.jar

RUN wget -P /var http://github.com/blankdots/ivy-pypi-repo/releases/download/v${plugin}/ivy-pypi-repo-${plugin}.tar \
    && tar -xf /var/ivy-pypi-repo-${plugin}.tar -C /var

COPY web /var/web

CMD nginx & \
    ./var/ivy-pypi-repo-${plugin}/bin/ivy-pypi-repo
