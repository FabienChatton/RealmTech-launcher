module ch.realmtech.launcher {
    requires javafx.controls;
    requires javafx.fxml;

    opens ch.realmtech.launcher to javafx.fxml;
    exports ch.realmtech.launcher;
    exports ch.realmtech.launcher.ctrl;
    exports ch.realmtech.launcher.wrk;
    opens ch.realmtech.launcher.ctrl to javafx.fxml;
    exports ch.realmtech.launcher.wrk.process;
}