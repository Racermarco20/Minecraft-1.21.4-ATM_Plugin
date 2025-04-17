package at.racermarco20.atm;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class ATMCommand implements CommandExecutor {

    private static final Logger logger = Logger.getLogger(ATMCommand.class.getName());

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (!(commandSender instanceof Player)) {
            logger.info("[ATM] Only players can use this command");
            return true;
        }

        ATMGui.openGui((Player) commandSender);

        return true;
    }
}
