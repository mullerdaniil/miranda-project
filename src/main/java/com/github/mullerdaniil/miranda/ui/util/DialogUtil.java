package com.github.mullerdaniil.miranda.ui.util;

import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;

public class DialogUtil {
    // TODO: 17.10.2023 make it more beautiful
    public static void showExceptionDialog(Exception e) {
        new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
    }

    // TODO: 17.10.2023 maybe deprecated? (because all exceptions should arise from Service layer?)

    public static void showErrorDialog(String message) {
        new Alert(Alert.AlertType.ERROR, message).show();
    }

    public static TextInputDialog createTextInputDialog(String title,
                                                        String contextText) {
        var textInputDialog = new TextInputDialog();
        textInputDialog.setTitle(title);
        textInputDialog.setContentText(contextText);
        textInputDialog.setHeaderText(null);
        textInputDialog.setGraphic(null);

        return textInputDialog;
    }
}