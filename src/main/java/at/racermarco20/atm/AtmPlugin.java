package at.racermarco20.atm;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class AtmPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("ATM Plugin enabled");

        getServer().getPluginManager().registerEvents(new ATMButtonListener(), this);
        getServer().getPluginManager().registerEvents(new ATMClickListener(), this);
        getServer().getPluginManager().registerEvents(new ATMPINClickListener(), this);
        getServer().getPluginManager().registerEvents(new ATMPINSetupClickListener(), this);

        PINManager.setup(this);

        getCommand("atm").setExecutor(new ATMCommand());


    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("ATM Plugin disabled");
    }
}
