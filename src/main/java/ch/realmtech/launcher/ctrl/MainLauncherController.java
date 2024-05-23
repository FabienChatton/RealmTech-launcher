package ch.realmtech.launcher.ctrl;

import ch.realmtech.launcher.beans.LauncherRelease;
import ch.realmtech.launcher.beans.SceneController;
import ch.realmtech.launcher.helper.PopupHelper;
import ch.realmtech.launcher.ihm.WebViewListener;
import ch.realmtech.launcher.wrk.ApplicationProcess;
import ch.realmtech.launcher.wrk.GetLauncherUpdate;
import ch.realmtech.launcher.wrk.RealmTechData;
import ch.realmtech.launcher.wrk.UpdateLauncher;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainLauncherController implements SceneController, Initializable {
    @FXML
    public WebView webView;
    @FXML
    private ComboBox<String> versionList;
    @FXML
    static NavController navController;

    private RealmTechData realmTechData;
    private ApplicationProcess applicationProcess;
    private UpdateLauncher launcherUpdate;
    private GetLauncherUpdate getLauncherUpdate;
    private Stage stage;

    public void setRealmTechData(RealmTechData realmTechData) {
        this.realmTechData = realmTechData;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void onLaunched(MouseEvent mouseEvent) {
        Optional<File> selectedVersionFile = getSelectedVersionFile();
        if (selectedVersionFile.isPresent()) {
            try {
                applicationProcess = ApplicationProcess.launchVersionFile(selectedVersionFile.get(), () -> Platform.runLater(this::onProcessClose));
                Platform.setImplicitExit(false);
                stage.hide();
            } catch (Exception e) {
                PopupHelper.builderError("Impossible de lancer la version", e).show();
            }
        } else {
            PopupHelper.builderInformation("Aucun version n'est sélectionnée").show();
        }
    }

    public void onProcessClose() {
        stage.show();
        Platform.setImplicitExit(true);
    }

    public void close() throws Exception {
        if (applicationProcess != null) {
            applicationProcess.close();
        }
    }

    public void scanVersion() {
        List<String> versions = realmTechData.listVersion();

        versionList.getItems().setAll(versions);
        if (versionList.getSelectionModel().isEmpty()) {
            versionList.getSelectionModel().selectFirst();
        }
    }

    public Optional<File> getSelectedVersionFile() {
        String selectedItem = versionList.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            return Optional.empty();
        }

        return realmTechData.getVersionFile(selectedItem);
    }

    public NavController getNavController() {
        return navController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            webView.getEngine().setUserDataDirectory(Files.createTempDirectory("RealmTech-web-views-userDataDirectory").toFile());
        } catch (Exception e) { }
        webView.getEngine().getLoadWorker().stateProperty().addListener(new WebViewListener(webView));
    }

    @Override
    public void onShow() {
        // exit programme if success
        handlerNewVersion();
        scanVersion();

        webView.getEngine().load("https://chattonf01.emf-informatique.ch/RealmTech");
    }

    private void handlerNewVersion() {
        Optional<LauncherRelease> launcherReleaseOpt;
        try {
            launcherReleaseOpt = getLauncherUpdate.getLauncherUpdate();
        } catch (Exception e) {
            PopupHelper.builderError("Impossible de trouver la nouvelle version du launcher", e).show();
            return;
        }

        if (launcherReleaseOpt.isPresent()) {
            LauncherRelease launcherRelease = launcherReleaseOpt.get();
            if (PopupHelper.builderConfirmation(String.format("Il y a une nouvelle version du launcher, la version %s est disponible, sortie le %s. Voulez la télécharger", launcherRelease.version, launcherRelease.publishedAt.toString()))
                    .showAndWait()) {
                try {
                    // exit programme if success
                    launcherUpdate.update();
                } catch (Exception e) {
                    PopupHelper.builderError("Impossible de lancer la mise à jour", e).show();
                }
            }
        }
    }

    public void setGetLauncherUpdate(GetLauncherUpdate getLauncherUpdate) {
        this.getLauncherUpdate = getLauncherUpdate;
    }

    public void setUpdateLauncher(UpdateLauncher launcherUpdate) {
        this.launcherUpdate = launcherUpdate;
    }

    public void onRefreshCache(ActionEvent actionEvent) {
        try {
            getLauncherUpdate.forceRefreshCache();
            PopupHelper.builderInformation("Le cache à été refresh").show();
        } catch (Exception e) {
            PopupHelper.builderError("Impossible de rafraîchir le cache", e).show();
        }
    }
}