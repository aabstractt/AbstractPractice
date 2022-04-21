package dev.thatsmybaby.shared.command;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;

public abstract class PlayerArgument extends Argument {

    public PlayerArgument(String name, String description, String permission) {
        super(name, description, permission);
    }

    public PlayerArgument(String name, String description, String permission, String... aliases) {
        super(name, description, permission, aliases);
    }

    @Override
    public void execute(CommandSender sender, String commandLabel, String argumentLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(TextFormat.RED + "Run this command in-game");

            return;
        }

        this.execute((Player) sender, commandLabel, argumentLabel, args);
    }

    public abstract void execute(Player sender, String commandLabel, String argumentLabel, String[] args);
}