package ch.realmtech.launcher.helper;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class PopupHelper {

    private static void showPopup(PopupData popupData) {
        Alert alert = new Alert(popupData.alertType);
        alert.setContentText(popupData.message);
        alert.setHeaderText(popupData.headerMessage);

        alert.show();
    }

    private static Optional<ButtonType> showPopupAndWait(PopupData popupData) {
        Alert alert = new Alert(popupData.alertType);
        alert.setContentText(popupData.message);
        alert.setHeaderText(popupData.headerMessage);

        return alert.showAndWait();
    }

    public static PopupInformationBuilder builderInformation(String message) {
        return new PopupInformationBuilder(Alert.AlertType.INFORMATION, message);
    }

    public static PopupInformationBuilder builderError(String message, Throwable e) {
        return new PopupInformationBuilder(Alert.AlertType.ERROR, e.getMessage()).setHeaderMessage(message);
    }

    public static PopupConfirmation builderConfirmation(String message) {
        return new PopupConfirmation(Alert.AlertType.CONFIRMATION, message);
    }

    public static class PopupInformationBuilder extends PopupBuilder<PopupInformationBuilder> {

        public PopupInformationBuilder(Alert.AlertType alertType, String message) {
            super(alertType, message);
        }

        public void show() {
            showPopup(getPopupData());
        }
    }

    public static class PopupConfirmation extends PopupBuilder<PopupConfirmation> {
        public PopupConfirmation(Alert.AlertType alertType, String message) {
            super(alertType, message);
        }

        public boolean showAndWait() {
            return showPopupAndWait(getPopupData())
                    .map(buttonType -> buttonType == ButtonType.OK)
                    .orElse(false);
        }
    }

    private static class PopupData {
        private final Alert.AlertType alertType;
        private final String message;
        private String headerMessage;
        private String title;

        public PopupData(Alert.AlertType alertType, String message) {
            this.alertType = alertType;
            this.message = message;
        }
    }

    @SuppressWarnings("unchecked")
    private static class PopupBuilder<T extends PopupBuilder<T>> {
        private final PopupData popupData;

        public PopupBuilder(Alert.AlertType alertType, String message) {
            popupData = new PopupData(alertType, message);
        }

        public T setHeaderMessage(String headerMessage) {
            this.popupData.headerMessage = headerMessage;
            return (T) this;
        }

        public T setTitle(String title) {
            this.popupData.title = title;
            return (T) this;
        }

        protected PopupData getPopupData() {
            return popupData;
        }
    }
}
