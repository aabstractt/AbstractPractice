package dev.thatsmybaby.practice;

import dev.thatsmybaby.practice.factory.KitFactory;
import dev.thatsmybaby.practice.factory.MatchFactory;
import dev.thatsmybaby.practice.object.GameKit;
import dev.thatsmybaby.practice.object.GameMatch;
import dev.thatsmybaby.practice.object.match.DuelMatch;
import dev.thatsmybaby.shared.Placeholders;
import dev.thatsmybaby.shared.provider.GameProvider;
import dev.thatsmybaby.shared.provider.IPacketHandler;
import dev.thatsmybaby.shared.provider.packet.*;

import java.util.stream.Collectors;

public final class PacketHandler implements IPacketHandler {

    @Override
    public void handle(RedisPacket packet) {
        if (packet instanceof ServerRequestKitsPacket) {
            Placeholders.log("Server '" + ((ServerRequestKitsPacket) packet).serverName + "' request kits");

            GameProvider.getInstance().publish(new ServerResponseKitsPacket() {{
                this.serverName = AbstractPractice.getServerName();

                this.kits = KitFactory.getInstance().getKits().values().stream().map(GameKit::getName).collect(Collectors.toList());
            }});

            return;
        }

        if (packet instanceof MatchRequestPacket) {
            this.handleMatchRequestPacket((MatchRequestPacket) packet);
        }
    }

    private void handleMatchRequestPacket(MatchRequestPacket packet) {
        GameMatch match = MatchFactory.getInstance().createMatch(DuelMatch.class, packet.kitName, null);

        GameProvider.getInstance().publish(new MatchResponsePacket() {{
            this.serverName = AbstractPractice.getServerName();
            this.kitName = packet.kitName;

            this.worldName = match == null ? null : match.getWorldName();

            this.players = packet.players;
        }});
    }
}