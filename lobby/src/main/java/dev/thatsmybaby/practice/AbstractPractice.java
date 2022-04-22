package dev.thatsmybaby.practice;

import cn.nukkit.plugin.PluginBase;
import dev.thatsmybaby.shared.Placeholders;
import dev.thatsmybaby.shared.TaskUtils;
import dev.thatsmybaby.shared.factory.QueueFactory;
import dev.thatsmybaby.shared.object.CrossServerGameMap;
import dev.thatsmybaby.shared.provider.GameProvider;
import dev.thatsmybaby.shared.provider.packet.ServerInfoRequestPacket;
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
                GameProvider.getInstance().setPlayerMatchStatus(firstPlayer.getName(), queue.getKitName(), queue.isRanked(), getServerName());
                GameProvider.getInstance().setPlayerMatchStatus(secondPlayer.getName(), queue.getKitName(), queue.isRanked(), getServerName());

                Placeholders.connectTo(firstPlayer, queue.getServerName());
                Placeholders.connectTo(secondPlayer, queue.getServerName());
            });
        });

        GameProvider.publish(new ServerInfoRequestPacket() {{
            this.serverName = AbstractPractice.getServerName();
        }});
    }

    public static String getServerName() {
        return instance.getConfig().getString("settings.server-name");
    }
}