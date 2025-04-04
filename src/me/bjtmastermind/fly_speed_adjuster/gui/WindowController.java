package me.bjtmastermind.fly_speed_adjuster.gui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import me.bjtmastermind.fly_speed_adjuster.Main;
import me.bjtmastermind.fly_speed_adjuster.bedrock.LevelDB;
import me.bjtmastermind.fly_speed_adjuster.gui.dialog.Notification;
import me.bjtmastermind.nbt.io.NBTUtil;
import me.bjtmastermind.nbt.io.NamedTag;
import me.bjtmastermind.nbt.tag.CompoundTag;

public class WindowController {
    private float defaultSpeed = 0.05f;
    private float currentSpeed;

    @FXML
    private ListView<VBox> worldList;
    @FXML
    private Label versionLabel;
    @FXML
    private Spinner<Integer> modifierSpinner;
    @FXML
    private Button applyBtn;
    @FXML
    private Button resetBtn;

    public void apply() {
        for (VBox worldBox : worldList.getSelectionModel().getSelectedItems()) {
            String world = Main.worldPath+"/"+((Label) ((VBox) ((HBox) worldBox.getChildren().get(0)).getChildren().get(1)).getChildren().get(1)).getText();
            Label flySpeedLabel = (Label) ((VBox) ((HBox) worldBox.getChildren().get(0)).getChildren().get(1)).getChildren().get(2);

            LevelDB db = new LevelDB(world+"/db/");
            byte[] playerData = db.get("~local_player".getBytes());
            try {
                NamedTag tag = NBTUtil.readLE(new ByteArrayInputStream(playerData), false);
                CompoundTag root = (CompoundTag) tag.getTag();
                CompoundTag abilities = root.getCompoundTag("abilities");
                float currentSpeed = abilities.getFloat("flySpeed");

                currentSpeed = defaultSpeed * modifierSpinner.getValueFactory().valueProperty().getValue();
                abilities.remove("flySpeed");
                abilities.put("flySpeed", currentSpeed);

                try {
                    ByteArrayOutputStream updatedData = NBTUtil.writeLE(root, false);
                    db.update("~local_player".getBytes(), updatedData.toByteArray());

                    flySpeedLabel.setText("Fly speed modifier: "+((int) (currentSpeed / defaultSpeed)));
                } catch (IOException e) {
                    Notification.show(AlertType.ERROR, e.toString(), ButtonType.CLOSE);
                    e.printStackTrace();
                }
            } catch (IOException e) {
                Notification.show(AlertType.ERROR, e.toString(), ButtonType.CLOSE);
                e.printStackTrace();
            }
            db.close();
        }
        Notification.show(AlertType.INFORMATION, "Successfully applied fly speed to selected worlds!", ButtonType.OK);
    }

    public void reset() {
        for (VBox worldBox : worldList.getSelectionModel().getSelectedItems()) {
            String world = Main.worldPath+"/"+((Label) ((VBox) ((HBox) worldBox.getChildren().get(0)).getChildren().get(1)).getChildren().get(1)).getText();
            Label flySpeedLabel = (Label) ((VBox) ((HBox) worldBox.getChildren().get(0)).getChildren().get(1)).getChildren().get(2);

            LevelDB db = new LevelDB(world+"/db/");
            byte[] playerData = db.get("~local_player".getBytes());
            try {
                NamedTag tag = NBTUtil.readLE(new ByteArrayInputStream(playerData), false);
                CompoundTag root = (CompoundTag) tag.getTag();
                CompoundTag abilities = root.getCompoundTag("abilities");
                float currentSpeed = abilities.getFloat("flySpeed");

                currentSpeed = defaultSpeed;
                abilities.remove("flySpeed");
                abilities.put("flySpeed", currentSpeed);

                try {
                    ByteArrayOutputStream updatedData = NBTUtil.writeLE(root, false);
                    db.update("~local_player".getBytes(), updatedData.toByteArray());

                    flySpeedLabel.setText("Fly speed modifier: "+((int) (currentSpeed / defaultSpeed)));
                    modifierSpinner.getValueFactory().setValue(1);
                } catch (IOException e) {
                    Notification.show(AlertType.ERROR, e.toString(), ButtonType.CLOSE);
                    e.printStackTrace();
                }
            } catch (IOException e) {
                Notification.show(AlertType.ERROR, e.toString(), ButtonType.CLOSE);
                e.printStackTrace();
            }
            db.close();
        }
        Notification.show(AlertType.INFORMATION, "Successfully reset fly speed of selected worlds!", ButtonType.OK);
    }

    public void initSelect() {
        if (modifierSpinner.isDisabled()) {
            modifierSpinner.setDisable(false);
            applyBtn.setDisable(false);
            resetBtn.setDisable(false);
        }
    }

    private void populateWorldList(Path worldsPath) {
        for (File world : worldsPath.toFile().listFiles()) {
            File level = new File(world, "level.dat");
            File levelName = new File(world, "levelname.txt");
            File icon = new File(world, "world_icon.jpeg");

            if (!level.exists()) {
                continue;
            }

            if (!icon.exists()) {
                icon = new File(Main.class.getResource("icons/world_no_icon.jpg").getFile());
            }

            String worldsFolderName = world.getName();

            String worldName = "";
            try {
                worldName = Files.readString(levelName.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }

            LevelDB db = new LevelDB(world.getAbsolutePath()+"/db/");
            byte[] playerData = db.get("~local_player".getBytes());
            try {
                NamedTag tag = NBTUtil.readLE(new ByteArrayInputStream(playerData), false);
                CompoundTag root = (CompoundTag) tag.getTag();
                CompoundTag abilities = root.getCompoundTag("abilities");
                currentSpeed = abilities.getFloat("flySpeed");
            } catch (IOException e) {
                Notification.show(AlertType.ERROR, e.toString(), ButtonType.CLOSE);
                e.printStackTrace();
            }
            db.close();

            VBox worldBox = new VBox();

            HBox hbox = new HBox();
            hbox.getChildren().add(new ImageView(new Image(icon.toURI().toString(), 145.0, 80.0, true, true)));
            hbox.setPrefSize(145.0, 80.0);

            VBox vbox = new VBox();
            vbox.setPrefSize(189.0, 73.0);
            vbox.alignmentProperty().setValue(Pos.CENTER_LEFT);
            vbox.paddingProperty().setValue(new Insets(0, 0, 0, 5));

            Label worldNameLabel = new Label(worldName);
            worldNameLabel.setFont(new Font("System Bold", 13));
            worldNameLabel.setTextFill(Paint.valueOf("#252525"));

            Label worldFolderLabel = new Label(worldsFolderName);
            worldFolderLabel.setFont(new Font("System Italic", 13));
            worldFolderLabel.setTextFill(Paint.valueOf("#696969"));

            Label currentSpeedLabel = new Label("Fly speed modifier: "+((int) (currentSpeed / defaultSpeed)));
            currentSpeedLabel.setFont(new Font("System Italic", 13));
            currentSpeedLabel.setTextFill(Paint.valueOf("#696969"));
            vbox.getChildren().addAll(worldNameLabel, worldFolderLabel, currentSpeedLabel);

            hbox.getChildren().add(vbox);
            worldBox.getChildren().add(hbox);

            worldList.getItems().add(worldBox);
            worldList.getStylesheets().add(Main.class.getResource("css/listview.css").toExternalForm());
        }
    }

    @FXML
    private void initialize() {
        versionLabel.setText(Main.VERSION);
        populateWorldList(Paths.get(Main.worldPath));
        worldList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }
}
