package me.bjtmastermind.fly_speed_adjuster.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import me.bjtmastermind.fly_speed_adjuster.Main;

public class Window extends Application {
    private Scene scene;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = loadFXML("Fly-Speed-Adjuster");
        scene = new Scene(loader.load(), 800, 500);
        stage.setTitle("Fly Speed Adjuster");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private FXMLLoader loadFXML(String fxml) throws IOException {
        return new FXMLLoader(Main.class.getResource("fxml/" + fxml + ".fxml"));
    }

    public Scene getScene() {
        return scene;
    }

    public void open() {
        launch();
    }
}
