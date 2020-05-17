module windchopper.tools.password.drop {

    opens com.github.windchopper.tools.password.drop;
    opens com.github.windchopper.tools.password.drop.book;
    opens com.github.windchopper.tools.password.drop.crypto;
    opens com.github.windchopper.tools.password.drop.i18n;
    opens com.github.windchopper.tools.password.drop.images;
    opens com.github.windchopper.tools.password.drop.ui;

    requires kotlin.stdlib;
    requires kotlin.stdlib.jdk8;

    requires kotlinx.coroutines.core;

    requires java.desktop;
    requires java.prefs;
    requires java.logging;

    requires java.xml.bind;

    requires javafx.controls;
    requires javafx.fxml;

    requires jakarta.inject.api;
    requires jakarta.enterprise.cdi.api;

    requires weld.se.core;
    requires weld.environment.common;
    requires weld.core.impl;

    requires windchopper.common.fx;
    requires windchopper.common.fx.cdi;
    requires windchopper.common.cdi;
    requires windchopper.common.preferences;
    requires windchopper.common.util;

}