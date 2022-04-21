package dev.thatsmybaby.practice.arguments;

import cn.nukkit.Player;
import cn.nukkit.level.Location;
import cn.nukkit.utils.TextFormat;
import dev.thatsmybaby.practice.factory.MapFactory;
import dev.thatsmybaby.practice.object.GameMap;
import dev.thatsmybaby.shared.command.PlayerArgument;

public final class SpawnArgument extends PlayerArgument {

    public SpawnArgument(String name, String description, String permission) {
        super(name, description, permission);
    }

    @Override
    public void execute(Player sender, String commandLabel, String argumentLabel, String[] args) {
        if (args.length == 0 || (!args[0].equalsIgnoreCase("first") && !args[0].equalsIgnoreCase("second"))) {
            sender.sendMessage(TextFormat.RED + "Usage: /" + commandLabel + " spawn <first|second>");

            return;
        }

        GameMap map = MapFactory.getInstance().getMap(sender.getValidLevel().getFolderName());

        if (map == null) {
            sender.sendMessage(TextFormat.RED + "This map does exists.");

            return;
        }

        Location loc = sender.getLocation();

        if (args[0].equalsIgnoreCase("first")) {
            map.setFirstPosition(loc);
        } else {
            map.setSecondPosition(loc);
        }

        map.forceSave();

        sender.sendMessage(TextFormat.colorize(String.format("&9%s spawn set to &6X: &b%s &6Y: &b%s &6Z: &b%s &6Yaw: &b%s &6Pitch: &b%s", args[0], loc.getFloorX(), loc.getFloorY(), loc.getFloorZ(), loc.yaw, loc.pitch)));
    }
}