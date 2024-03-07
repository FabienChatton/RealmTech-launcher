package ch.realmtech.launcher;

import ch.realmtech.launcher.ctrl.MainLauncherController;
import ch.realmtech.launcher.ctrl.VersionListController;
import ch.realmtech.launcher.wrk.RealmTechData;
import ch.realmtech.launcher.wrk.ReleasesWrk;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class RealmTechLauncher extends Application {
    private MainLauncherController mainLauncherController;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoaderMain = new FXMLLoader(RealmTechLauncher.class.getResource("RealmTechLauncher.fxml"));
        Scene launcherScene = new Scene(fxmlLoaderMain.load(), 1024, 576);
        mainLauncherController = fxmlLoaderMain.getController();

        FXMLLoader fxmlLoaderVersionList = new FXMLLoader(RealmTechLauncher.class.getResource("VersionListController.fxml"));
        Scene versionListScene = new Scene(fxmlLoaderVersionList.load(), 1024, 576);
        VersionListController versionListController = fxmlLoaderVersionList.getController();

        RealmTechData realmTechData = new RealmTechData(RealmTechData.RootPathClass.defaultRootPath());
        ReleasesWrk releasesWrk = new ReleasesWrk();

        mainLauncherController.getNavController().setStage(stage);
        mainLauncherController.getNavController().setLauncherScene(launcherScene, mainLauncherController);
        mainLauncherController.getNavController().setVersionScene(versionListScene, versionListController);

        mainLauncherController.setRealmTechData(realmTechData);
        mainLauncherController.scanVersion();

        versionListController.setReleasesWrk(releasesWrk);
        versionListController.reloadReleaseVersion();
        versionListController.setRealmTechData(realmTechData);

        stage.setTitle("RealmTech Launcher");
        stage.setScene(launcherScene);
        stage.show();
        mainLauncherController.getNavController().onShow();
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
        mainLauncherController.close();
    }
}