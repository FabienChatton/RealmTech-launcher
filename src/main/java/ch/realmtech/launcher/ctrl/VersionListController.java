package ch.realmtech.launcher.ctrl;

import ch.realmtech.launcher.beans.RemoteReleaseVersion;
import ch.realmtech.launcher.beans.SceneController;
import ch.realmtech.launcher.wrk.RealmTechData;
import ch.realmtech.launcher.wrk.ReleasesWrk;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class VersionListController implements Initializable, SceneController {

    @FXML
    private ScrollPane messageScrollPane;
    @FXML
    private ListView<RemoteReleaseVersion> versionListView;
    @FXML
    private Label messageLabel;
    private ReleasesWrk releasesWrk;
    private RealmTechData realmTechData;

    public void setReleasesWrk(ReleasesWrk releasesWrk) {
        this.releasesWrk = releasesWrk;
    }

    public void setRealmTechData(RealmTechData realmTechData) {
        this.realmTechData = realmTechData;
    }

    public void reloadReleaseVersion() {
        try {
            List<RemoteReleaseVersion> versionsReleases = releasesWrk.getVersionsReleases();
            versionListView.getItems().setAll(versionsReleases);
        } catch (Error e) {
            System.err.println("Can not fetch releases version. Error: " + e.getMessage());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        versionListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            messageLabel.setText(newValue.message);
        });

    }

    @Override
    public void onShow() {
        if (versionListView.getSelectionModel().getSelectedItem() == null) {
            versionListView.getSelectionModel().selectFirst();
        }
    }

    @Override
    public void onResize(double width, double height) {

    }
}
