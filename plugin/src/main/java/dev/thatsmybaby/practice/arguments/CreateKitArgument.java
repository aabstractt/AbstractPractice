package dev.thatsmybaby.practice.arguments;

import cn.nukkit.Player;
import cn.nukkit.utils.TextFormat;
import dev.thatsmybaby.practice.factory.KitFactory;
import dev.thatsmybaby.practice.object.GameKit;
import dev.thatsmybaby.shared.command.PlayerArgument;

public final class CreateKitArgument extends PlayerArgument {

    public CreateKitArgument(String name, String description, String permission) {
        super(name, description, permission);
    }

    @Override
    public void execute(Player sender, String commandLabel, String argumentLabel, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(TextFormat.RED + "Usage: /" + commandLabel + " createkit <kit_name>");

            return;
        }
        
        if (KitFactory.getInstance().getKit(args[0]) != null) {
            sender.sendMessage(TextFormat.RED + "Kit " + args[0] + " already exists");

            return;
        }

        KitFactory.getInstance().registerNewKit(new GameKit(args[0], sender.getInventory().getContents()), true);

        sender.sendMessage(TextFormat.GREEN + "Kit " + args[0] + " successfully created!");
    }
}