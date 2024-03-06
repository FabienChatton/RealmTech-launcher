package ch.realmtech.launcher.ctrl;

import ch.realmtech.launcher.wrk.VersionApplicationProcess;
import ch.realmtech.launcher.wrk.RealmTechData;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class MainLauncherCtrl {
    @FXML
    private ComboBox<String> versionList;

    private RealmTechData realmTechData;
    private VersionApplicationProcess versionApplicationProcess;

    public void setRealmTechData(RealmTechData realmTechData) {
        this.realmTechData = realmTechData;
    }

    public void setApplicationProcess(VersionApplicationProcess versionApplicationProcess) {
        this.versionApplicationProcess = versionApplicationProcess;
    }

    @FXML
    public void onLaunched(MouseEvent mouseEvent) {
        Optional<File> selectedVersionFile = getSelectedVersionFile();
        if (selectedVersionFile.isPresent()) {
            try {
                versionApplicationProcess.launchVersionFile(selectedVersionFile.get());
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        } else {
            System.out.println("Aucun version n'est sélectionnée");
        }
    }

    public void scanVersion() {
        List<String> versions = realmTechData.listVersion();

        versionList.getItems().clear();
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
}