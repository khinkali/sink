FROM khinkali/keycloak-adapter-wildfly:0.0.4

MAINTAINER Robert Brem <brem_robert@hotmail.com>

ADD target/sink.war /opt/jboss/wildfly/standalone/deployments/