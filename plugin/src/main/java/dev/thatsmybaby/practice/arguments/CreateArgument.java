package dev.thatsmybaby.practice.arguments;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.utils.TextFormat;
import dev.thatsmybaby.practice.AbstractPractice;
import dev.thatsmybaby.practice.factory.KitFactory;
import dev.thatsmybaby.practice.factory.MapFactory;
import dev.thatsmybaby.practice.object.GameKit;
import dev.thatsmybaby.practice.object.GameMap;
import dev.thatsmybaby.shared.TaskUtils;
import dev.thatsmybaby.shared.command.PlayerArgument;

import java.io.File;
import java.util.ArrayList;

public final class CreateArgument extends PlayerArgument {

    public CreateArgument(String name, String description, String permission) {
        super(name, description, permission);
    }

    @Override
    public void execute(Player sender, String commandLabel, String argumentLabel, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(TextFormat.RED + "Usage: /" + commandLabel + " create <kit_name>");

            return;
        }

        Level level = sender.getValidLevel();

        if (level == Server.getInstance().getDefaultLevel()) {
            sender.sendMessage(TextFormat.RED + "You can't setup maps in the lobby.");

            return;
        }

        String folderName = level.getFolderName();

        if (MapFactory.getInstance().getMap(folderName) != null) {
            sender.sendMessage(TextFormat.RED + "Map " + folderName + " already exists");

            return;
        }

        GameKit kit = KitFactory.getInstance().getKit(args[0]);

        if (kit == null) {
            sender.sendMessage(TextFormat.RED + "Kit " + args[0] + " doest exists");

            return;
        }

        level.requireProvider().updateLevelName(folderName);

        level.setTime(Level.TIME_DAY);
        level.stopTime();
        level.save(true);

        Server.getInstance().unloadLevel(level);
        Server.getInstance().loadLevel(folderName);

        if (AbstractPractice.isSchematic()) {
            // TODO: Do schematic backup
            return;
        }

        MapFactory.getInstance().registerNewMap(new GameMap(
                folderName,
                new ArrayList<>(),
                true,
                null,
                null,
                null,
                null
        ), true);

        sender.sendMessage(TextFormat.GREEN + "New map registered!");
        sender.teleport(Server.getInstance().getLevelByName(folderName).getSpawnLocation());

        TaskUtils.runAsync(() -> MapFactory.getInstance().copyMap(new File(Server.getInstance().getDataPath(), "worlds/" + folderName), new File(AbstractPractice.getInstance().getDataFolder(), "backups/" + folderName)));
    }
}