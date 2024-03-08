package ch.realmtech.launcher.ihm;

import ch.realmtech.launcher.helper.PopupHelper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.web.WebView;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLAnchorElement;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class WebViewListener implements ChangeListener<Worker.State> {
    private final WebView webView;
    public WebViewListener(WebView webView) {
        this.webView = webView;
    }

    public void setDesktopLink() {
        Document document = webView.getEngine().getDocument();
        if (document == null) return;
        NodeList anchor = document.getElementsByTagName("a");

        for (int i = 0; i < anchor.getLength(); i++) {
            org.w3c.dom.Node node = anchor.item(i);
            EventTarget eventTarget = (EventTarget) node;

            eventTarget.addEventListener("click", (event) -> {
                HTMLAnchorElement anchorElement = (HTMLAnchorElement) event.getCurrentTarget();
                event.preventDefault();
                String href = anchorElement.getHref();

                try {
                    Desktop.getDesktop().browse(URI.create(href));
                } catch (IOException e) {
                    PopupHelper.builderError("Can not open link", e);
                }
            }, false);
        }
    }

    @Override
    public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
        if (newValue == Worker.State.SUCCEEDED) {
            setDesktopLink();
        }
    }
}
