FROM centos:7
MAINTAINER "Sami Kallio" <sami.kallio@gofore.com>

RUN yum -y install sudo git rpm-build java-1.8.0-openjdk-headless
RUN yum clean all
RUN sed -i 's/requiretty/!requiretty/' /etc/sudoers

CMD ["sh", "-c", "/xroad/src/packages/build-xroad-rpm.sh && /xroad/src/packages/build-jetty-rpm.sh" ]

