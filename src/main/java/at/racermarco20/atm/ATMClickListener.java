package at.racermarco20.atm;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.slf4j.LoggerFactory;

import java.util.logging.Logger;

public class ATMClickListener implements Listener {

    private static final Logger logger = Logger.getLogger(ATMClickListener.class.getName());
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ATMClickListener.class);

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getView().getTitle().equals(ChatColor.GOLD + "ATM")) return;

        int rawSlot = event.getRawSlot();
        int guiSize = event.getInventory().getSize();

        if (rawSlot >= guiSize) {
            return;
        }

        if (rawSlot == 23) {

            ItemStack cursor = event.getCursor();

            if (cursor == null || cursor.getType() == Material.AIR) return;

            if (!isValidItem(cursor, player)) {
                event.setCancelled(true);
                logger.info("[ATM] " + player.getName() + " tried to insert invalid item");
                player.sendMessage(ChatColor.DARK_RED + "Invalid item, only RedBytes/RedBits are allowed!");
            }

            return;
        }

        event.setCancelled(true);

        switch (rawSlot) {
            case 10 -> {
                logger.info("[ATM] " + player.getName() + " clicked deposit");

                ItemStack item = event.getInventory().getItem(23);

                if (item == null || item.getType() == Material.AIR || !isValidItem(item, player)) {
                    player.sendMessage(ChatColor.RED + "❌ Kein gültiges Item zum Einzahlen");
                    logger.info("[ATM] " + player.getName() + " tried to insert invalid item");
                    return;
                }

                if (item.getType() == Material.EMERALD) {
                    ATMManager.depositToBank(player, item.getAmount() * 8);
                    logger.info("[ATM] " + player.getName() + " inserted " + item.getAmount() * 8 + " RedBits (" + item.getAmount() + " RedBytes)");

                    event.getInventory().setItem(23, null);

                    return;
                }
                if (item.getType() == Material.GOLD_NUGGET) {
                    ATMManager.depositToBank(player, item.getAmount());
                    logger.info("[ATM] " + player.getName() + " inserted " + item.getAmount() + " RedBits");

                    event.getInventory().setItem(23, null);
                    return;
                }

                logger.info("[ATM] " + player.getName() + " tried to insert invalid item");
            }
            case 13 -> {
                logger.info("[ATM] " + player.getName() + " clicked balance");

                int balance = ATMManager.getBalance(player);
                player.sendMessage(ChatColor.GOLD + "💰 " + balance + " RedBits");
                logger.info("[ATM] " + player.getName() + " has " + balance + " RedBits");
                event.getInventory().close();
            }
            case 15 -> {
                logger.info("[ATM] " + player.getName() + " clicked withdraw");

                Inventory withdrawGui = Bukkit.createInventory(null, 27, ChatColor.DARK_RED + "Withdraw");

                withdrawGui.setItem(10, ATMManager.createRedByte(1));
                withdrawGui.setItem(12, ATMManager.createRedByte(8));
                withdrawGui.setItem(14, ATMManager.createRedBit(16));

                player.openInventory(withdrawGui);
            }
            case 22 -> logger.info("[ATM] " + player.getName() + " clicked convert");

            case 24 -> {
                logger.info("[ATM] " + player.getName() + " clicked confirm");

                ItemStack item = event.getInventory().getItem(23);

                if (item == null || item.getType() == Material.AIR || !isValidItem(item, player)) {
                    player.sendMessage(ChatColor.RED + "❌ Kein gültiges Item zum Umwandeln");
                    return;
                }

                if (item.getType() == Material.EMERALD) {
                    event.getInventory().setItem(23, ATMManager.convertRedBytesToBits(item));
                    return;
                }

                if (item.getType() == Material.GOLD_NUGGET) {
                    ItemStack converted = ATMManager.convertRedBitsToBytes(item, player);
                    if (converted != null) {
                        event.getInventory().setItem(23, converted);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onWithdrawClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getView().getTitle().equals(ChatColor.DARK_RED + "Withdraw")) return;

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        int payout = clicked.getAmount();
        Material type = clicked.getType();
        int cost;

        if (type == Material.EMERALD) {
            cost = payout * 8;
        } else if (type == Material.GOLD_NUGGET) {
            cost = payout;
        } else {
            player.sendMessage(ChatColor.RED + "❌ Nur RedBits und RedBytes sind erlaubt.");
            return;
        }

        BankAccount account = new BankAccount(player);
        if (account.getMoney() < cost) {
            player.sendMessage(ChatColor.RED + "❌ Nicht genug Guthaben! (" + cost + " RedBits benötigt)");
            return;
        }

        account.subtractMoney(cost);

        ItemStack payoutItem = (type == Material.EMERALD)
                ? ATMManager.createRedByte(payout)
                : ATMManager.createRedBit(payout);

        player.getInventory().addItem(payoutItem);
        player.sendMessage(ChatColor.GREEN + "✅ Du hast " + payout + " " + (type == Material.EMERALD ? "RedBytes" : "RedBits") + " abgehoben.");
        player.closeInventory();
    }


    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;

        Inventory closedInv = event.getInventory();

        if (!event.getView().getTitle().equals(ChatColor.GOLD + "ATM")) return;

        ItemStack item = closedInv.getItem(23);
        if (item != null && !item.getType().isAir()) {
            player.getInventory().addItem(item);

            closedInv.setItem(23, null);
        }
    }

    private boolean isValidItem(ItemStack item, Player player) {
        if (!item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();

        logger.info("[ATM] " + player.getName() + " tried to insert: " + item.getType() + " | Display: " + meta.getItemName());

        if (item.getType() == Material.EMERALD && meta.hasItemName() && meta.getItemName().contains("RedByte")) {
            return true;
        }

        if (item.getType() == Material.GOLD_NUGGET && meta.hasItemName() && meta.getItemName().contains("RedBit")) {
            return true;
        }

        return false;
    }

}
