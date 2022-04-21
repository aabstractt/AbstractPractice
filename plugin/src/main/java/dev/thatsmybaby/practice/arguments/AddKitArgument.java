package dev.thatsmybaby.practice.arguments;

import cn.nukkit.Player;
import cn.nukkit.utils.TextFormat;
import dev.thatsmybaby.practice.factory.KitFactory;
import dev.thatsmybaby.practice.factory.MapFactory;
import dev.thatsmybaby.practice.object.GameKit;
import dev.thatsmybaby.practice.object.GameMap;
import dev.thatsmybaby.shared.command.PlayerArgument;

public final class AddKitArgument extends PlayerArgument {

    public AddKitArgument(String name, String description, String permission) {
        super(name, description, permission);
    }

    @Override
    public void execute(Player sender, String commandLabel, String argumentLabel, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(TextFormat.RED + "Usage: /" + commandLabel + " addkit <kit_name>");

            return;
        }

        GameMap map = MapFactory.getInstance().getMap(sender.getValidLevel().getFolderName());

        if (map == null) {
            sender.sendMessage(TextFormat.RED + "This map doest exists.");

            return;
        }

        GameKit kit = KitFactory.getInstance().getKit(args[0]);

        if (kit == null) {
            sender.sendMessage(TextFormat.RED + "Kit " + args[0] + " doest exists");

            return;
        }

        map.addKit(kit);
        map.forceSave();

        sender.sendMessage(TextFormat.GREEN + String.format("Kit %s added to %s map", kit.getName(), map.getMapName()));
    }
}