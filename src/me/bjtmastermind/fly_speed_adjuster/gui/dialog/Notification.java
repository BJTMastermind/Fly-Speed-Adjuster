package me.bjtmastermind.fly_speed_adjuster.gui.dialog;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

public class Notification {

    public static void show(AlertType type, String message, ButtonType... buttons) {
        Alert alert = new Alert(type, message, buttons);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
