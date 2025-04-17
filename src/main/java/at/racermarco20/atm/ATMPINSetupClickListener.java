package at.racermarco20.atm;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.UUID;

public class ATMPINSetupClickListener implements Listener {

    private static final Logger log = LoggerFactory.getLogger(ATMPINSetupClickListener.class);
    private final HashMap<UUID, StringBuilder> pinMap = new HashMap<>();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) throws InterruptedException {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getView().getTitle().contains(ChatColor.DARK_GRAY + "🔒 PIN Setup")) return;

        int slot = event.getRawSlot();
        int guiSize = event.getInventory().getSize();

        if (slot >= guiSize) {
            return;
        }

        if (slot != 4) {
            event.setCancelled(true);
        }

        ItemStack keyCard = event.getInventory().getItem(4);
        boolean hasValidKeycard = keyCard != null &&
                keyCard.getType() == Material.NAME_TAG;

        if (!hasValidKeycard) {
            player.sendMessage(ChatColor.RED + "❌ Du musst zuerst deine Keycard einlegen!");
            return;
        }

        if (event.getCursor() != null && event.getCursor().getType() != Material.AIR) {
            player.setItemOnCursor(null);
        }

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        String displayName = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
        pinMap.putIfAbsent(player.getUniqueId(), new StringBuilder());
        StringBuilder pin = pinMap.get(player.getUniqueId());

        switch (displayName) {
            case "✔ Setzen" -> {
                if (pin.length() != 4) {
                    player.sendMessage(ChatColor.YELLOW + "⚠ Bitte gib eine 4-stellige PIN ein.");
                    return;
                }

                PINManager.setPin(player, pin.toString());

                String pinCode = pin.toString();
                log.info("[ATM] {} set PIN: {}", player.getName(), pinCode);

                player.sendMessage(ChatColor.GREEN + "✅ PIN erfolgreich gesetzt: " + pinCode);
                ItemStack keyCardBack = event.getInventory().getItem(4);
                if (keyCardBack != null && keyCardBack.getType() == Material.NAME_TAG) {
                    player.getInventory().addItem(keyCardBack); // Gib die Karte zurück
                }
                player.closeInventory();
                pinMap.remove(player.getUniqueId());
            }

            case "✖ Abbrechen" -> {
                player.sendMessage(ChatColor.RED + "⏹ PIN-Setup abgebrochen.");
                ItemStack keyCardBack = event.getInventory().getItem(4);
                if (keyCardBack != null && keyCardBack.getType() == Material.NAME_TAG) {
                    player.getInventory().addItem(keyCardBack); // Gib die Karte zurück
                }
                player.closeInventory();
                pinMap.remove(player.getUniqueId());
            }

            default -> {
                if (!displayName.matches("\\d")) return;
                if (pin.length() >= 4) {
                    player.sendMessage(ChatColor.YELLOW + "⚠ Die PIN darf nur 4 Ziffern lang sein.");
                    event.getView().setTitle(ChatColor.DARK_GRAY + "🔒 PIN Setup");
                    pin.delete(0, pin.length());
                    return;
                }

                event.getView().setTitle(event.getView().getTitle() + " *");

                pin.append(displayName);
                log.info("[ATM] {} entered digit '{}'. Current PIN: {}", player.getName(), displayName, pin);
            }
        }
    }
}
