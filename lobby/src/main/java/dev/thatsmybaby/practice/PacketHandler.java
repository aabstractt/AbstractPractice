package dev.thatsmybaby.practice;

import dev.thatsmybaby.shared.Placeholders;
import dev.thatsmybaby.shared.factory.QueueFactory;
import dev.thatsmybaby.shared.provider.IPacketHandler;
import dev.thatsmybaby.shared.provider.packet.RedisPacket;
import dev.thatsmybaby.shared.provider.packet.ServerResponseKitsPacket;

public final class PacketHandler implements IPacketHandler {

    @Override
    public void handle(RedisPacket packet) {
        if (packet instanceof ServerResponseKitsPacket) {
            Placeholders.log("Received response from " + ((ServerResponseKitsPacket) packet).serverName + " loading queue!");

            QueueFactory.getInstance().init(((ServerResponseKitsPacket) packet).kits);

            return;
        }
    }
}