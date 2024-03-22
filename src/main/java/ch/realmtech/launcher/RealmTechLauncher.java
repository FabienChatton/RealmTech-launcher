package ch.realmtech.launcher;

import ch.realmtech.launcher.ctrl.MainLauncherController;
import ch.realmtech.launcher.ctrl.VersionListController;
import ch.realmtech.launcher.helper.PopupHelper;
import ch.realmtech.launcher.wrk.RealmTechData;
import ch.realmtech.launcher.wrk.ReleasesWrk;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class RealmTechLauncher extends Application {
    public final static String LAUNCHER_VERSION = "0.1.2";
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
        releasesWrk.setRealmTechData(realmTechData);

        mainLauncherController.getNavController().setStage(stage);
        mainLauncherController.getNavController().setLauncherScene(launcherScene, mainLauncherController);
        mainLauncherController.getNavController().setVersionScene(versionListScene, versionListController);

        mainLauncherController.setRealmTechData(realmTechData);
        mainLauncherController.setStage(stage);

        versionListController.setReleasesWrk(releasesWrk);
        versionListController.reloadReleaseVersion();
        versionListController.setRealmTechData(realmTechData);

        stage.setTitle("RealmTech Launcher " + LAUNCHER_VERSION);
        stage.setScene(launcherScene);
        stage.show();
        mainLauncherController.onShow();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                mainLauncherController.close();
            } catch (Exception e) {
                Platform.runLater(() -> PopupHelper.builderError("Can not close application after shutdown", e).show());
            }
        }));
        releasesWrk.hasNewRemoteVersion().ifPresent((newRemoteVersionMessage) -> PopupHelper.builderInformation(newRemoteVersionMessage).show());
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