#!/bin/sh
BENNU_DIR=~/workspace/git/bennu-core
mvn clean install
cp target/vaadin-framework-0.1.jar $BENNU_DIR/modules/bennu-vaadin/web/WEB-INF/lib/
