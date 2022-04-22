package dev.thatsmybaby.practice;

import cn.nukkit.plugin.PluginBase;
import dev.thatsmybaby.practice.arguments.*;
import dev.thatsmybaby.practice.arguments.test.QueueJoinArgument;
import dev.thatsmybaby.practice.factory.KitFactory;
import dev.thatsmybaby.practice.factory.MapFactory;
import dev.thatsmybaby.practice.factory.QueueFactory;
import dev.thatsmybaby.practice.listener.BlockBreakListener;
import dev.thatsmybaby.practice.listener.BlockPlaceListener;
import dev.thatsmybaby.shared.command.Argument;
import dev.thatsmybaby.shared.command.CoreAdminCommand;
import lombok.Getter;

public final class AbstractPractice extends PluginBase {

    @Getter private static AbstractPractice instance;

    @Override
    public void onEnable() {
        instance = this;

        KitFactory.getInstance().init();
        QueueFactory.getInstance().init();
        MapFactory.getInstance().init();

        this.getServer().getCommandMap().register("coreadmin", new CoreAdminCommand("coreadmin", "Abstract Practice management command", "", new String[]{"ca"}));

        this.getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
        this.getServer().getPluginManager().registerEvents(new BlockPlaceListener(), this);

        this.registerArguments(
                new CreateArgument("create", "Create a new practice map", "abstract.practice.create"),
                new CreateKitArgument("createkit", "Create a new practice kit", "abstract.practice.create.kit"),
                new SpawnArgument("spawn", "Set map spawn", "abstract.practice.spawn"),
                new CornerArgument("corner", "Set map corner", "abstract.practice.corner"),
                new AddKitArgument("addkit", "Add kit to a map", "abstract.practice.add.kit")
        );

        if (released()) return;

        this.getLogger().warning("Registering 'QueueJoinArgument' because this is a non released version");

        registerArguments(
                new QueueJoinArgument("queuejoin", "Join to the queue", "abstract.practice.queue")
        );
    }

    private void registerArguments(Argument... arguments) {
        ((CoreAdminCommand) this.getServer().getCommandMap().getCommand("coreadmin")).registerArgument(arguments);
    }

    public static String getServerName() {
        return instance.getConfig().getString("settings.server-name");
    }

    public static void debug(String message) {
        instance.getLogger().info(message);
    }

    public static boolean isSchematic() {
        return instance.getConfig().getBoolean("settings.schematic", false);
    }

    public static boolean released() {
        return false;
    }
}