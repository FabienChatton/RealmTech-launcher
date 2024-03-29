module ch.realmtech.launcher {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.net.http;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires org.commonmark;
    requires java.desktop;
    requires jdk.xml.dom;
    requires jdk.crypto.ec;

    opens ch.realmtech.launcher to javafx.fxml;
    opens ch.realmtech.launcher.ctrl to javafx.fxml;
    opens ch.realmtech.launcher.beans to com.fasterxml.jackson.databind;

    exports ch.realmtech.launcher;
    exports ch.realmtech.launcher.ctrl;
    exports ch.realmtech.launcher.wrk;
    exports ch.realmtech.launcher.beans;
    exports ch.realmtech.launcher.ihm;
}