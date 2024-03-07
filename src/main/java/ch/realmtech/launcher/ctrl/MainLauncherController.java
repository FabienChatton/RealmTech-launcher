package ch.realmtech.launcher.ctrl;

import ch.realmtech.launcher.beans.SceneController;
import ch.realmtech.launcher.wrk.ApplicationProcess;
import ch.realmtech.launcher.wrk.RealmTechData;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class MainLauncherController implements SceneController {
    @FXML
    private ComboBox<String> versionList;

    static NavController navController;

    private RealmTechData realmTechData;
    private ApplicationProcess applicationProcess;

    public void setRealmTechData(RealmTechData realmTechData) {
        this.realmTechData = realmTechData;
    }

    @FXML
    public void onLaunched(MouseEvent mouseEvent) {
        Optional<File> selectedVersionFile = getSelectedVersionFile();
        if (selectedVersionFile.isPresent()) {
            try {
                applicationProcess = ApplicationProcess.launchVersionFile(selectedVersionFile.get());
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        } else {
            System.out.println("Aucun version n'est sélectionnée");
        }
    }

    public void close() throws Exception {
        if (applicationProcess != null) {
            applicationProcess.close();
        }
    }

    public void scanVersion() {
        List<String> versions = realmTechData.listVersion();

        versionList.getItems().addAll(versions);
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
    public void onShow() {

    }

    @Override
    public void onResize(double width, double height) {

    }
}