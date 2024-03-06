package ch.realmtech.launcher;

import ch.realmtech.launcher.ctrl.MainLauncherCtrl;
import ch.realmtech.launcher.wrk.RealmTechData;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class RealmTechLauncher extends Application {
    private MainLauncherCtrl mainLauncherCtrl;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RealmTechLauncher.class.getResource("RealmTechLauncher.fxml"));
        Scene launcherScreen = new Scene(fxmlLoader.load(), 1024, 576);
        mainLauncherCtrl = fxmlLoader.getController();

        RealmTechData realmTechData = new RealmTechData(RealmTechData.RootPathClass.defaultRootPath());
        mainLauncherCtrl.setRealmTechData(realmTechData);

        mainLauncherCtrl.scanVersion();

        stage.setTitle("RealmTech Launcher");
        stage.setScene(launcherScreen);
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

    @Override
    public void stop() throws Exception {
        mainLauncherCtrl.close();
    }
}