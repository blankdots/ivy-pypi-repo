FROM nginx:alpine

MAINTAINER Stefan Negru <stefan.negru@helsinki.fi>

RUN apk add --update \
    && apk add --no-cache openjdk8-jre \
    && apk add --no-cache bash \
    && apk add --no-cache wget \
    && apk add --no-cache supervisor \
    && rm -rf /var/cache/apk/*

ARG IVY=0.6.12
ARG PLUGIN=0.2.6
ENV GOSU_VERSION 1.10

RUN set -x \
    && apk add --no-cache --virtual .gosu-deps \
        dpkg \
        gnupg \
        openssl \
    && dpkgArch="$(dpkg --print-architecture | awk -F- '{ print $NF }')" \
    && wget -O /usr/local/bin/gosu "https://github.com/tianon/gosu/releases/download/$GOSU_VERSION/gosu-$dpkgArch" \
    && wget -O /usr/local/bin/gosu.asc "https://github.com/tianon/gosu/releases/download/$GOSU_VERSION/gosu-$dpkgArch.asc" \
    && export GNUPGHOME="$(mktemp -d)" \
    && gpg --keyserver ipv4.pool.sks-keyservers.net --recv-keys B42F6819007F00F88E364FD4036A9C25BF357DD4 \
    && gpg --batch --verify /usr/local/bin/gosu.asc /usr/local/bin/gosu \
    && rm -r "$GNUPGHOME" /usr/local/bin/gosu.asc \
    && chmod +x /usr/local/bin/gosu \
    && gosu nobody true \
    && apk del .gosu-deps

RUN wget https://dl.bintray.com/linkedin/maven/com/linkedin/pygradle/pivy-importer/${IVY}/pivy-importer-${IVY}-all.jar --no-check-certificate \
    && mv pivy-importer-${IVY}-all.jar /var/lib/pivy-importer-all.jar

RUN wget -P /var http://github.com/blankdots/ivy-pypi-repo/releases/download/v${PLUGIN}/ivy-pypi-repo-${PLUGIN}.tar \
    && tar -xf /var/ivy-pypi-repo-${PLUGIN}.tar -C /var \
    && mv /var/ivy-pypi-repo-${PLUGIN} /var/ivy-pypi-repo

# Adding user without root privileges alongside a user group
RUN addgroup -S nonroot \
    && adduser -S -g nonroot nonroot

# COPY nginx, supervisord specific configurations
# default.conf is needed to have access to port 8080 as port 80 is not allowed with
# to be used by a user with lower privileges
COPY nginx.conf /etc/nginx/nginx.conf
COPY default.conf /etc/nginx/conf.d/default.conf
COPY supervisor.conf /etc/supervisor/supervisord.conf
COPY web /var/web
COPY entrypoint.sh /

RUN touch /var/run/nginx.pid \
    && mkdir -p /data \
    && chmod +x /entrypoint.sh

# The volume needs to be set after the directory has been created to avoid
# permissions issues
VOLUME ["/data"]

# setting environment variables for entrypoint script
# setting also directories to be owned by the nonroot user
ENV GOSU_USER nonroot:nonroot
ENV GOSU_CHOWN /var /data /etc/supervisor /var/run/nginx.pid /etc/nginx/ /var/run/nginx.pid

EXPOSE 5039
EXPOSE 5639

ENTRYPOINT ["/entrypoint.sh"]
