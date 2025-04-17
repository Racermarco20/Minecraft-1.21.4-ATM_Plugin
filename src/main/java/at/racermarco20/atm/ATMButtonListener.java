package at.racermarco20.atm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class ATMButtonListener implements Listener {

    private static final Logger log = LogManager.getLogger(ATMButtonListener.class);

    @EventHandler
    public void onButtonPress(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;

        Block block = event.getClickedBlock();
        Material type = block.getType();

        if (type == Material.STONE_BUTTON) {
            Player player = event.getPlayer();

            if (ATMManager.isNearATM(player)) {
                ATMGui.openPinGui(player);
            } else {
                log.info("[ATM] {} tried to open the ATM but was too far away. ❌", player.getName());
            }
        }
    }
}
