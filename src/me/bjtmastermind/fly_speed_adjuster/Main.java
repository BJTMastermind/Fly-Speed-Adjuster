package me.bjtmastermind.fly_speed_adjuster;

import me.bjtmastermind.fly_speed_adjuster.bedrock.WorldsFolders;
import me.bjtmastermind.fly_speed_adjuster.gui.Window;

public class Main {
    public static final String VERSION = "v1.1.0";
    public static final String worldPath = new WorldsFolders().getWorldsPath();

    public static void main(String[] args) {
        Window window = new Window();
        window.open();
    }
}
