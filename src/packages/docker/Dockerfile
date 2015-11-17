FROM centos:7
MAINTAINER "Sami Kallio" <sami.kallio@gofore.com>

RUN yum -y install epel-release 
RUN yum -y install @development sudo rpmdevtools java-1.8.0-openjdk
RUN sed -i 's/requiretty/!requiretty/' /etc/sudoers

CMD ["sh", "-c", "/xroad/src/packages/build-xroad-rpm.sh && /xroad/src/packages/build-jetty-rpm.sh" ]

