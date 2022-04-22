package dev.thatsmybaby.practice;

import cn.nukkit.plugin.PluginBase;
import dev.thatsmybaby.shared.Placeholders;
import dev.thatsmybaby.shared.TaskUtils;
import dev.thatsmybaby.shared.command.Argument;
import dev.thatsmybaby.shared.command.CoreAdminCommand;
import dev.thatsmybaby.shared.factory.QueueFactory;
import dev.thatsmybaby.shared.object.CrossServerGameMap;
import dev.thatsmybaby.shared.provider.GameProvider;
import dev.thatsmybaby.shared.provider.packet.ServerInfoRequestPacket;
import dev.thatsmybaby.shared.test.QueueJoinArgument;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

public final class AbstractPractice extends PluginBase {

    @Getter private static AbstractPractice instance;

    @Getter @Setter private List<CrossServerGameMap> crossServerGameMaps = new LinkedList<>();

    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();

        GameProvider.getInstance().init(this.getConfig().getString("redis.address"), this.getConfig().getString("redis.password"), new PacketHandler());

        QueueFactory.setHandler((queue, firstPlayer, secondPlayer) -> {
            firstPlayer.sendMessage(Placeholders.replacePlaceholders("QUEUE_FOUND_OPPONENT", firstPlayer.getName(), secondPlayer.getName()));
            secondPlayer.sendMessage(Placeholders.replacePlaceholders("QUEUE_FOUND_OPPONENT", secondPlayer.getName(), firstPlayer.getName()));

            queue.remove(firstPlayer.getName());
            queue.remove(secondPlayer.getName());

            TaskUtils.runAsync(() -> {
                GameProvider.getInstance().setPlayerCrossServer(firstPlayer.getName(), queue.getKitName(), queue.isRanked(), secondPlayer.getName(), getServerName());
                GameProvider.getInstance().setPlayerCrossServer(secondPlayer.getName(), queue.getKitName(), queue.isRanked(), firstPlayer.getName(), getServerName());

                Placeholders.connectTo(firstPlayer, queue.getServerName());
                Placeholders.connectTo(secondPlayer, queue.getServerName());
            });
        });

        GameProvider.publish(new ServerInfoRequestPacket() {{
            this.serverName = AbstractPractice.getServerName();
        }});

        this.getServer().getCommandMap().register("coreadmin", new CoreAdminCommand("coreadmin", "Abstract Practice management command", "", new String[]{"ca"}));

        if (released()) return;

        this.getLogger().warning("Registering 'QueueJoinArgument' because this is a non released version");

        registerArguments(
                new QueueJoinArgument("queuejoin", "Join to the queue on non released", "abstract.practice.queue")
        );
    }

    @Override
    public void onDisable() {
        GameProvider.getInstance().close();
    }

    private void registerArguments(Argument... arguments) {
        ((CoreAdminCommand) this.getServer().getCommandMap().getCommand("coreadmin")).registerArgument(arguments);
    }

    public static String getServerName() {
        return instance.getConfig().getString("settings.server-name");
    }

    public static boolean released() {
        return false;
    }
}