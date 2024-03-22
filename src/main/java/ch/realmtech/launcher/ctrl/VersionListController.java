package ch.realmtech.launcher.ctrl;

import ch.realmtech.launcher.beans.RemoteReleaseVersion;
import ch.realmtech.launcher.beans.SceneController;
import ch.realmtech.launcher.helper.PopupHelper;
import ch.realmtech.launcher.ihm.WebViewListener;
import ch.realmtech.launcher.wrk.GetVersionsReleases;
import ch.realmtech.launcher.wrk.RealmTechData;
import ch.realmtech.launcher.wrk.ReleasesWrk;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.IntConsumer;

public class VersionListController implements Initializable, SceneController {
    @FXML
    public WebView webView;
    @FXML
    private ListView<RemoteReleaseVersion> versionListView;
    @FXML
    public GridPane browserGrid;
    @FXML
    private Button downloadVersionButton;
    @FXML
    private Button deleteVersionButton;
    private ReleasesWrk releasesWrk;
    private RealmTechData realmTechData;
    private GetVersionsReleases getVersionsReleases;
    private final Parser markdownParser;
    private final HtmlRenderer htmlRenderer;


    public VersionListController() {
        markdownParser = Parser.builder().build();
        htmlRenderer = HtmlRenderer.builder().build();
    }

    public void setReleasesWrk(ReleasesWrk releasesWrk) {
        this.releasesWrk = releasesWrk;
    }

    public void setRealmTechData(RealmTechData realmTechData) {
        this.realmTechData = realmTechData;
    }

    public void reloadReleaseVersion() {
        try {
            List<RemoteReleaseVersion> versionsReleases = getVersionsReleases.getVersionsReleases();
            versionListView.getItems().setAll(versionsReleases);
        } catch (Exception e) {
            PopupHelper.builderError("Can not fetch releases version.", e).show();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        webView.getEngine().getLoadWorker().stateProperty().addListener(new WebViewListener(webView));

        try {
            webView.getEngine().setUserDataDirectory(Files.createTempDirectory("RealmTech-web-views-userDataDirectory").toFile());
        } catch (Exception e) { }

        versionListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            setVersionContent(newValue);
        });

        versionListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<RemoteReleaseVersion> call(ListView<RemoteReleaseVersion> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(RemoteReleaseVersion item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            item.isInstalled = realmTechData.isReleasePresentOnLocal(item);
                            setText(item.toString());
                            if (versionListView.getSelectionModel().getSelectedItem() == item) {
                                setVersionContent(item);
                            }
                        }
                    }
                };
            }
        });
    }

    private void setVersionContent(RemoteReleaseVersion selectedVersion) {
        Node markdownDocument = markdownParser.parse(selectedVersion.message);
        String markdownRender = htmlRenderer.render(markdownDocument);

        webView.getEngine().loadContent(markdownRender);

        downloadVersionButton.setDisable(selectedVersion.isInstalled);
        deleteVersionButton.setDisable(!selectedVersion.isInstalled);
    }

    @FXML
    private void onVersionDownload(MouseEvent mouseEvent) {
        RemoteReleaseVersion selectedVersion = versionListView.getSelectionModel().getSelectedItem();
        try {
            releasesWrk.downloadVersionRelease(selectedVersion.remoteReleaseAsset.downloadUrl, selectedVersion.remoteReleaseAsset.name, onVersionDownloadSuccess(selectedVersion), onVersionDownloadFail(selectedVersion));
            PopupHelper.builderInformation("Nouvelle version téléchargé, version: " + selectedVersion.remoteReleaseAsset.name).show();
        } catch (Exception e) {
            PopupHelper.builderError("Can not download version release", e).show();
        }
        onShow();
    }

    @FXML
    private void onVersionDelete(MouseEvent mouseEvent) {
        RemoteReleaseVersion selectedVersion = versionListView.getSelectionModel().getSelectedItem();
        boolean confirmation = PopupHelper.builderConfirmation("Voulez vous supprimer la version " + selectedVersion.remoteReleaseAsset.name + " ?").showAndWait();
        if (confirmation) {
            releasesWrk.deleteVersionRelease(selectedVersion);
        }
        onShow();
    }

    private Runnable onVersionDownloadSuccess(RemoteReleaseVersion releaseVersion) {
        return () -> PopupHelper.builderInformation("Nouvelle version téléchargé " + releaseVersion.remoteReleaseAsset.name).show();
    }

    private IntConsumer onVersionDownloadFail(RemoteReleaseVersion releaseVersion) {
        return (statusCode) -> PopupHelper.builderError("Impossible de télécharger la version " + releaseVersion.remoteReleaseAsset.name, new Throwable("status code: " + statusCode)).show();
    }

    @Override
    public void onShow() {
        if (versionListView.getSelectionModel().getSelectedItem() == null) {
            versionListView.getSelectionModel().selectFirst();
        }
        versionListView.refresh();
    }

    public void setGetVersionsReleases(GetVersionsReleases getVersionsReleases) {
        this.getVersionsReleases = getVersionsReleases;
    }
}
