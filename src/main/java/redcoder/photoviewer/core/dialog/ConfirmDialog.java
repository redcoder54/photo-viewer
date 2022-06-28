package redcoder.photoviewer.core.dialog;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

public class ConfirmDialog extends Dialog<ConfirmDialog.State> {

    public ConfirmDialog() {
        this(null, null);
    }

    public ConfirmDialog(String title, String contentText) {
        setTitle(title);
        setContentText(contentText);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        setResultConverter((dialogButton) -> dialogButton == ButtonType.OK ? State.OK : State.CANCEL);
    }

    public enum State {
        OK, CANCEL
    }
}
