FROM ubuntu:bionic

RUN apt-get update && \
    # Prerequisites
	apt-get install -y curl less vim apt-transport-https ca-certificates curl gnupg-agent software-properties-common sudo git && \
    # Docker CLI
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | apt-key add - && \
    add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" && \
    apt-get install -y docker-ce-cli && \
    # kubectl
    curl -Lo /usr/local/bin/kubectl https://storage.googleapis.com/kubernetes-release/release/v1.14.0/bin/linux/amd64/kubectl && \
    chmod 755 /usr/local/bin/kubectl && \
    # helm & tiller
    curl -L https://storage.googleapis.com/kubernetes-helm/helm-v2.14.0-linux-amd64.tar.gz | tar xvzC /usr/bin linux-amd64/helm linux-amd64/tiller --strip 1 && \
    # docker-compose
    curl -L "https://github.com/docker/compose/releases/download/1.24.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose && \
    chmod 755 /usr/local/bin/docker-compose && \
    # kind
    curl -L https://github.com/kubernetes-sigs/kind/releases/download/v0.4.0/kind-linux-amd64 -o /usr/local/bin/kind && \
    chmod 755 /usr/local/bin/kind && \
    # Network debugging essentials
    apt-get install -y iputils-ping inetutils-traceroute net-tools && \
    # JDK
    apt-get install -y openjdk-8-jdk-headless && \
    # cleanup
    rm -rf /var/lib/apt/lists/*

ADD bin/* /usr/local/bin/

ENTRYPOINT ["entrypoint"]
