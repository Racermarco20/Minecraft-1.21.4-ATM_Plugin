package at.racermarco20.atm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ATMManager {

    private static final Logger log = LogManager.getLogger(ATMManager.class);

    public static ItemStack createRedByte(int amount) {
        ItemStack redByte = new ItemStack(Material.EMERALD, amount);
        ItemMeta meta = redByte.getItemMeta();
        meta.setItemName(ChatColor.DARK_RED + "" + ChatColor.ITALIC + "RedByte");

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.AQUA + "8 RedBits = 1 RedByte");
        lore.add(ChatColor.GRAY + "Value: " + ChatColor.DARK_PURPLE + ChatColor.MAGIC + "123 c8nmß012.,4092");

        meta.setLore(lore);
        redByte.setItemMeta(meta);
        return redByte;
    }

    public static ItemStack createRedBit(int amount) {
        ItemStack redBit = new ItemStack(Material.GOLD_NUGGET, amount);
        ItemMeta meta = redBit.getItemMeta();
        meta.setItemName(ChatColor.RED + "" + ChatColor.ITALIC + "RedBit");

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.AQUA + "8 RedBits = 1 RedByte");
        lore.add(ChatColor.GRAY + "Value: " + ChatColor.DARK_PURPLE + ChatColor.MAGIC + "7nm4093214,ß72x1,");

        meta.setLore(lore);
        redBit.setItemMeta(meta);
        return redBit;
    }

    public static boolean isNearATM(Player player) {
        Location loc = player.getLocation();
        for (Entity entity : loc.getWorld().getNearbyEntities(loc, 5, 5, 13)) {
            if (entity instanceof ArmorStand armorStand && armorStand.getScoreboardTags().contains("ATM-Machines")) {
                return true;
            }
        }
        return false;
    }


    public static ItemStack convertRedBytesToBits(ItemStack redByteStack) {
        return createRedBit(redByteStack.getAmount() * 8);
    }

    public static ItemStack convertRedBitsToBytes(ItemStack redBitStack, Player player) {
        int amount = redBitStack.getAmount();

        if (amount < 8 || amount % 8 != 0) {
            player.sendMessage(ChatColor.DARK_RED + "❌ Du brauchst mindestens 8 RedBits für einen RedByte!");
            log.info("[ATM] {} tried to convert {} RedBits to a RedByte.", player.getName(), amount);
            return null;
        }

        return createRedByte(amount / 8);
    }

    public static void depositToBank(Player player, int redBitAmount) {
        BankAccount account = new BankAccount(player);
        account.addMoney(redBitAmount);
        player.sendMessage(ChatColor.GREEN + "💰 " + redBitAmount + " RedBits eingezahlt. Neuer Kontostand: " + account.getMoney());
    }

    public static int getBalance(Player player) {
        BankAccount account = new BankAccount(player);
        return account.getMoney();
    }
}
