package dev.thatsmybaby.practice;

import cn.nukkit.plugin.PluginBase;
import dev.thatsmybaby.shared.Placeholders;
import dev.thatsmybaby.shared.provider.packet.MatchRequestPacket;
import dev.thatsmybaby.shared.factory.QueueFactory;
import dev.thatsmybaby.shared.provider.GameProvider;
import lombok.Getter;

import java.util.Arrays;

public final class AbstractPractice extends PluginBase {

    @Getter private static AbstractPractice instance;

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

            GameProvider.getInstance().publish(new MatchRequestPacket() {{
                this.serverName = getServerName();

                this.players = Arrays.asList(firstPlayer.getName(), secondPlayer.getName());

                this.kitName = queue.getKitName();
            }});

            // TODO: Add to a array waiting a response for a Match
        });
    }

    public static String getServerName() {
        return instance.getConfig().getString("settings.server-name");
    }
}