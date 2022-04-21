package dev.thatsmybaby.practice;

import cn.nukkit.plugin.PluginBase;
import dev.thatsmybaby.practice.arguments.CornerArgument;
import dev.thatsmybaby.practice.arguments.CreateArgument;
import dev.thatsmybaby.practice.arguments.CreateKitArgument;
import dev.thatsmybaby.practice.arguments.SpawnArgument;
import dev.thatsmybaby.practice.factory.KitFactory;
import dev.thatsmybaby.practice.factory.MapFactory;
import dev.thatsmybaby.shared.command.Argument;
import dev.thatsmybaby.shared.command.CoreAdminCommand;
import lombok.Getter;

public final class AbstractPractice extends PluginBase {

    @Getter private static AbstractPractice instance;

    @Override
    public void onEnable() {
        instance = this;

        KitFactory.getInstance().init();
        MapFactory.getInstance().init();

        this.registerArguments(
                new CreateArgument("create", "Create a new practice map", "abstract.practice.create"),
                new CreateKitArgument("createkit", "Create a new practice kit", "abstract.practice.create.kit"),
                new SpawnArgument("spawn", "Set map spawn", "abstract.practice.spawn"),
                new CornerArgument("corner", "Set map corner", "abstract.practice.corner")
        );
    }

    private void registerArguments(Argument... arguments) {
        CoreAdminCommand command = new CoreAdminCommand("coreadmin", "Abstract Practice management command", "", new String[]{"ca"});

        command.registerArgument(arguments);

        this.getServer().getCommandMap().register("coreadmin", command);
    }

    public static boolean isSchematic() {
        return instance.getConfig().getBoolean("settings.schematic", false);
    }
}