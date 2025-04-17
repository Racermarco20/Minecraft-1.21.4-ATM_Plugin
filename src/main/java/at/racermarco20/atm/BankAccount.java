package at.racermarco20.atm;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class BankAccount {

    private final Player player;
    private final Scoreboard scoreboard;
    private final Objective money;

    public BankAccount(Player player) {
        this.player = player;
        this.scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        this.money = getOrCreateMoneyObjective();
    }

    private Objective getOrCreateMoneyObjective() {
        Objective obj = scoreboard.getObjective("Money");
        if (obj == null) {
            obj = scoreboard.registerNewObjective("Money", "dummy", "Money");
        }
        return obj;
    }

    public int getMoney() {
        return money.getScore(player.getName()).getScore();
    }

    public void setMoney(int amount) {
        money.getScore(player.getName()).setScore(amount);
    }

    public void addMoney(int amount) {
        setMoney(getMoney() + amount);
    }

    public void subtractMoney(int amount) {
        setMoney(getMoney() - amount);
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public String toString() {
        return "BankAccount{" +
                "player=" + player.getName() +
                ", money=" + getMoney() +
                '}';
    }
}
