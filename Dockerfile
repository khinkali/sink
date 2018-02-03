FROM jboss/wildfly:11.0.0.Final

MAINTAINER Robert Brem <brem_robert@hotmail.com>

ADD target/sink.war /opt/jboss/wildfly/standalone/deployments/