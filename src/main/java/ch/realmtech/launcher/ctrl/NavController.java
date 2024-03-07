package ch.realmtech.launcher.ctrl;

import ch.realmtech.launcher.beans.SceneController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class NavController implements Initializable, SceneController {
    private static Stage stage;
    private static Scene launcherScene;
    private static MainLauncherController mainLauncherController;
    private static Scene versionScene;
    private static VersionListController versionListController;
    @FXML
    public Button launcherBtn;
    @FXML
    public Button versionBtn;

    public void setStage(Stage stage) {
        NavController.stage = stage;
    }

    public void setLauncherScene(Scene launcherScene, MainLauncherController mainLauncherController) {
        NavController.launcherScene = launcherScene;
        NavController.mainLauncherController = mainLauncherController;
    }

    public void setVersionScene(Scene versionScene, VersionListController versionListController) {
        NavController.versionScene = versionScene;
        NavController.versionListController = versionListController;
    }

    @FXML
    private void navLauncher(MouseEvent mouseEvent) {
        stage.setScene(launcherScene);
        mainLauncherController.onShow();
    }

    @FXML
    private void navVersion(MouseEvent mouseEvent) {
        stage.setScene(versionScene);
        versionListController.onShow();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (MainLauncherController.navController == null) {
            MainLauncherController.navController = this;
        }
    }

    @Override
    public void onShow() {
        stage.widthProperty().addListener((observable, oldValue, newValue) -> {
            onResize(newValue.doubleValue(), 0);
        });
        stage.heightProperty().addListener((observable, oldValue, newValue) -> {
            onResize(0, newValue.doubleValue());
        });
    }

    @Override
    public void onResize(double width, double height) {
        mainLauncherController.onResize(width, height);
        versionListController.onResize(width, height);
    }
}
