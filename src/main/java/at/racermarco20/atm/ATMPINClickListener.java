package at.racermarco20.atm;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.UUID;

public class ATMPINClickListener implements Listener {

    private static final Logger log = LoggerFactory.getLogger(ATMPINClickListener.class);
    private final HashMap<UUID, StringBuilder> pinMap = new HashMap<>();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) throws InterruptedException {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getView().getTitle().contains(ChatColor.DARK_GRAY + "🔒 PIN Eingabe")) return;

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
            case "✔ Eingeben" -> {
                if (pin.length() != 4) {
                    player.sendMessage(ChatColor.YELLOW + "⚠ Bitte gib eine 4-stellige PIN ein.");
                    return;
                }

                String pinCode = pin.toString();
                log.info("[ATM] {} entered PIN: {}", player.getName(), pinCode);

                if (!PINManager.hasPin(player)) {
                    player.sendMessage(ChatColor.YELLOW + "Du hast noch keinen PIN gesetzt. Bitte wähle einen.");
                    ItemStack keyCardBack = event.getInventory().getItem(4);
                    if (keyCardBack != null && keyCardBack.getType() == Material.NAME_TAG) {
                        player.getInventory().addItem(keyCardBack); // Gib die Karte zurück
                    }
                    ATMGui.openPinSetupGui(player); // eigene GUI, wie PINGui, nur zum erstmaligen Setzen
                } else {
                    if (PINManager.checkPin(player, pinCode)) {
                        log.info("[ATM] {} entered the correct PIN", player.getName());
                        player.sendMessage(ChatColor.DARK_GREEN + "✅ PIN korrekt. Zugriff gewährt.");
                        ItemStack keyCardBack = event.getInventory().getItem(4);
                        if (keyCardBack != null && keyCardBack.getType() == Material.NAME_TAG) {
                            player.getInventory().addItem(keyCardBack); // Gib die Karte zurück
                        }

                        player.closeInventory();
                        ATMGui.openGui(player);
                    } else {
                        log.info("[ATM] {} entered the wrong PIN", player.getName());
                        player.sendMessage(ChatColor.DARK_RED + "❌ PIN falsch. Versuch es erneut.");
                        event.getView().setTitle(ChatColor.DARK_GRAY + "🔒 PIN Eingabe");
                        pin.delete(0, pin.length());
                    }
                }

                pinMap.remove(player.getUniqueId());
            }

            case "✖ Abbrechen" -> {
                player.sendMessage(ChatColor.RED + "⏹ PIN-Eingabe abgebrochen.");
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
                    event.getView().setTitle(ChatColor.DARK_GRAY + "🔒 PIN Eingabe");
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
