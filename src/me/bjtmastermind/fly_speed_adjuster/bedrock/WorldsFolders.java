package me.bjtmastermind.fly_speed_adjuster.bedrock;

import java.io.File;

public class WorldsFolders {
    private final String worldsPath;
    private final String windows = "C:/Users/"+System.getProperty("user.name")+"/Local Settings/Application Data/Packages/Microsoft.MinecraftUWP_8wekyb3d8bbwe/LocalState/games/com.mojang/minecraftWorlds/";
    private final String mac = "/Users/"+System.getProperty("user.name")+"/Library/Application Support/mcpelauncher/games/com.mojang/minecraftWorlds/";
    private final String linuxFlatpak = "/home/"+System.getProperty("user.name")+"/.var/app/io.mrarm.mcpelauncher/data/mcpelauncher/games/com.mojang/minecraftWorlds/";
    private final String linuxAppimage = "/home/"+System.getProperty("user.name")+"/.local/share/mcpelauncher/games/com.mojang/minecraftWorlds/";

    public WorldsFolders() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.startsWith("win")) {
            worldsPath = windows;
            return;
        }

        if (os.startsWith("mac")) {
            worldsPath = mac;
            return;
        }

        if (new File(linuxFlatpak).exists() && new File(linuxFlatpak).listFiles().length > 0) {
            worldsPath = linuxFlatpak;
        } else {
            worldsPath = linuxAppimage;
        }
    }

    public String getWorldsPath() {
        return worldsPath;
    }
}
