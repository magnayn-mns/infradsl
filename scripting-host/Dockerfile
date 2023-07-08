FROM azul/zulu-openjdk-alpine:17

RUN apk add --no-cache curl


RUN curl -fsSL https://get.pulumi.com | sh

RUN mkdir /pulumi
RUN mkdir -p /github/workspace


COPY build/libs/scripting-host.jar /pulumi/script.jar
COPY src/main/scripts/pulumi /pulumi

WORKDIR /pulumi

ENTRYPOINT ["pulumi", "preview", "--non-interactive"]

