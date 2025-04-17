package at.racermarco20.atm;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PINManager {

    private static File file;
    private static FileConfiguration config;

    public static void setup(AtmPlugin plugin) {
        file = new File(plugin.getDataFolder(), "pins.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().warning("Konnte pins.yml nicht erstellen!");
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public static boolean hasPin(Player player) {
        return config.contains("players." + player.getUniqueId().toString() + ".pin");
    }

    public static void setPin(Player player, String pin) {
        config.set("players." + player.getUniqueId().toString() + ".pin", pin);
        save();
    }

    public static boolean checkPin(Player player, String input) {
        return input.equals(getPin(player));
    }

    public static String getPin(Player player) {
        return config.getString("players." + player.getUniqueId().toString() + ".pin");
    }

    public static void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
