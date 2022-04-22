package dev.thatsmybaby.practice;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.network.protocol.TransferPacket;
import cn.nukkit.utils.TextFormat;
import dev.thatsmybaby.shared.Placeholders;
import dev.thatsmybaby.shared.TaskUtils;
import dev.thatsmybaby.shared.factory.QueueFactory;
import dev.thatsmybaby.shared.object.GameQueue;
import dev.thatsmybaby.shared.provider.GameProvider;
import dev.thatsmybaby.shared.provider.IPacketHandler;
import dev.thatsmybaby.shared.provider.packet.MatchResponsePacket;
import dev.thatsmybaby.shared.provider.packet.RedisPacket;
import dev.thatsmybaby.shared.provider.packet.ServerResponseKitsPacket;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class PacketHandler implements IPacketHandler {

    @Override
    public void handle(RedisPacket packet) {
        if (packet instanceof ServerResponseKitsPacket) {
            Placeholders.log("Received response from " + ((ServerResponseKitsPacket) packet).serverName + " loading queue!");

            QueueFactory.getInstance().init(((ServerResponseKitsPacket) packet).kits);

            return;
        }

        if (packet instanceof MatchResponsePacket) {
            this.handleMatchResponsePacket((MatchResponsePacket) packet);

            return;
        }
    }

    private void handleMatchResponsePacket(MatchResponsePacket packet) {
        List<Player> players = packet.players.stream().map(playerName -> Server.getInstance().getPlayerExact(playerName)).filter(Objects::nonNull).collect(Collectors.toList());

        if (players.size() < 2 || packet.worldName == null) {
            Placeholders.log("Match for '" + packet.kitName + "' not found... Trying to search again in 5 seconds");

            GameQueue queue = QueueFactory.getInstance().getQueueType(packet.kitName, false);

            if (queue == null) return;

            for (Player player : players) {
                player.sendMessage(TextFormat.RED + "Tried to start a match but there are no available arenas.");

                queue.joinAsPlayer(player);
            }

            return;
        }

        TaskUtils.runAsync(() -> {
            for (Player player : players) {
                GameProvider.getInstance().setPlayerMatch(player.getName(), packet.worldName, AbstractPractice.getServerName());

                player.dataPacket(new TransferPacket() {{
                    this.address = packet.serverName;
                }});
            }
        });

        // TODO: Transfer player to the target server and insert into redis
    }
}