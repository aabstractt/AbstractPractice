package dev.thatsmybaby.practice;

import dev.thatsmybaby.practice.AbstractPractice;
import dev.thatsmybaby.practice.factory.KitFactory;
import dev.thatsmybaby.practice.object.GameKit;
import dev.thatsmybaby.shared.Placeholders;
import dev.thatsmybaby.shared.provider.GameProvider;
import dev.thatsmybaby.shared.provider.IPacketHandler;
import dev.thatsmybaby.shared.provider.packet.RedisPacket;
import dev.thatsmybaby.shared.provider.packet.ServerRequestKitsPacket;
import dev.thatsmybaby.shared.provider.packet.ServerResponseKitsPacket;

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
    }
}