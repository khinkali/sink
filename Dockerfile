FROM khinkali/keycloak-adapter-wildfly:0.0.1

MAINTAINER Robert Brem <brem_robert@hotmail.com>

ADD target/sink.war /opt/jboss/wildfly/standalone/deployments/