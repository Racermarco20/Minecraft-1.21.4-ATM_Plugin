package at.racermarco20.atm;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ATMGui {

    private static final Logger log = LoggerFactory.getLogger(ATMGui.class);

    public ATMGui(Player player, int size) {
        log.info("[ATMGui] Opening ATM for {}", player.getName());

        Inventory atmGui = Bukkit.createInventory(null, size, ChatColor.GOLD + "ATM");

        ItemStack filler = createFiller();

        for (int i = 0; i < size; i++) {
            if (i == 23) continue;
            atmGui.setItem(i, filler);
        }

        // Deposit
        atmGui.setItem(10, createButton(Material.EMERALD, ChatColor.DARK_GREEN + "Deposit"));
        atmGui.setItem(11, createInfo("Lege RedBits oder RedBytes in Slot 23 und drücke 'Deposit'."));

        // Balance
        atmGui.setItem(13, createButton(Material.GOLD_INGOT, ChatColor.GOLD + "Balance"));
        atmGui.setItem(14, createInfo("Zeigt dein aktuelles Guthaben in RedBits an."));

        // Withdraw
        atmGui.setItem(15, createButton(Material.DIAMOND, ChatColor.DARK_RED + "Withdraw"));
        atmGui.setItem(16, createInfo("Wähle einen Betrag aus und hebe Geld ab."));

        // Convert
        atmGui.setItem(22, createButton(Material.PAPER, ChatColor.YELLOW + "Convert"));
        atmGui.setItem(24, createButton(Material.LIME_WOOL, ChatColor.GREEN + "Confirm"));

        player.openInventory(atmGui);
    }

    public ATMGui(Player player, boolean isSetup) {

        Inventory pinGui = Bukkit.createInventory(null, 45, isSetup ? ChatColor.DARK_GRAY + "🔒 PIN Setup" : ChatColor.DARK_GRAY + "🔒 PIN Eingabe");

        // Filler
        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);
        for (int i = 0; i < pinGui.getSize(); i++) {
            pinGui.setItem(i, filler);
        }

        // Slot für Keycard (Slot 4)
        ItemStack keycardSlotInfo = new ItemStack(Material.PAPER);
        ItemMeta infoMeta = keycardSlotInfo.getItemMeta();
        infoMeta.setDisplayName(ChatColor.AQUA + "🔐 Lege hier deine Keycard ein");
        keycardSlotInfo.setItemMeta(infoMeta);
        pinGui.setItem(4, null); // Slot freilassen
        pinGui.setItem(13, keycardSlotInfo); // Info darunter

        // Zahlen-Buttons (1–9)
        for (int i = 1; i <= 9; i++) {
            ItemStack number = new ItemStack(Material.RED_WOOL);
            ItemMeta meta = number.getItemMeta();
            meta.setDisplayName(ChatColor.RED + String.valueOf(i));
            number.setItemMeta(meta);

            int row = (i - 1) / 3;
            int col = (i - 1) % 3;
            int slot = 19 + col + row * 9;
            pinGui.setItem(slot, number);
        }

        // 0
        ItemStack zero = new ItemStack(Material.RED_WOOL);
        ItemMeta zeroMeta = zero.getItemMeta();
        zeroMeta.setDisplayName(ChatColor.RED + "0");
        zero.setItemMeta(zeroMeta);
        pinGui.setItem(40, zero);

        // Enter
        ItemStack enter = new ItemStack(Material.LIME_WOOL);
        ItemMeta enterMeta = enter.getItemMeta();
        enterMeta.setDisplayName(isSetup ? ChatColor.GREEN + "✔ Setzen" : ChatColor.GREEN + "✔ Eingeben");
        enter.setItemMeta(enterMeta);
        pinGui.setItem(34, enter);

        // Cancel
        ItemStack cancel = new ItemStack(Material.BARRIER);
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.setDisplayName(ChatColor.RED + "✖ Abbrechen");
        cancel.setItemMeta(cancelMeta);
        pinGui.setItem(44, cancel);

        player.openInventory(pinGui);
    }

    public static void openGui(Player player) {
        new ATMGui(player, 36);
    }

    public static void openPinGui(Player player) {
        new ATMGui(player, false);
    }

    public static void openPinSetupGui(Player player) {
        new ATMGui(player, true);
    }

    private ItemStack createButton(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createInfo(String text) {
        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta meta = paper.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY + "Info");
        meta.setLore(List.of(ChatColor.WHITE + text));
        paper.setItemMeta(meta);
        return paper;
    }

    private ItemStack createFiller() {
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(" ");
        glass.setItemMeta(meta);
        return glass;
    }
}
