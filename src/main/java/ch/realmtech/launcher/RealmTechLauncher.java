package ch.realmtech.launcher;

import ch.realmtech.launcher.ctrl.MainLauncherCtrl;
import ch.realmtech.launcher.wrk.VersionApplicationProcess;
import ch.realmtech.launcher.wrk.RealmTechData;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class RealmTechLauncher extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RealmTechLauncher.class.getResource("RealmTechLauncher.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1024, 576);
        MainLauncherCtrl mainLauncherCtrl = fxmlLoader.getController();

        RealmTechData realmTechData = new RealmTechData(RealmTechData.RootPathClass.defaultRootPath());
        VersionApplicationProcess versionApplicationProcess = new VersionApplicationProcess();
        mainLauncherCtrl.setRealmTechData(realmTechData);
        mainLauncherCtrl.setApplicationProcess(versionApplicationProcess);

        mainLauncherCtrl.scanVersion();

        stage.setTitle("RealmTech Launcher");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            launch();
        } else {
            System.out.println("No support for cli now. Execute this application without args.");
            Platform.exit();
        }
    }
}