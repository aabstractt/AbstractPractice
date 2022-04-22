package dev.thatsmybaby.practice;

import dev.thatsmybaby.shared.Placeholders;
import dev.thatsmybaby.shared.factory.QueueFactory;
import dev.thatsmybaby.shared.provider.IPacketHandler;
import dev.thatsmybaby.shared.provider.packet.RedisPacket;
import dev.thatsmybaby.shared.provider.packet.ServerInfoResponsePacket;

public final class PacketHandler implements IPacketHandler {

    @Override
    public void handle(RedisPacket packet) {
        if (packet instanceof ServerInfoResponsePacket) {
            Placeholders.log("Request received, loading data");

            AbstractPractice.getInstance().setCrossServerGameMaps(((ServerInfoResponsePacket) packet).crossServerGameMaps);

            QueueFactory.getInstance().init(((ServerInfoResponsePacket) packet).kits, ((ServerInfoResponsePacket) packet).serverName);
        }
    }
}