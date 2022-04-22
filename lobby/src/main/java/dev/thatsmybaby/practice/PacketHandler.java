package dev.thatsmybaby.practice;

import dev.thatsmybaby.shared.Placeholders;
import dev.thatsmybaby.shared.factory.QueueFactory;
import dev.thatsmybaby.shared.provider.IPacketHandler;
import dev.thatsmybaby.shared.provider.redis.RedisMessage;
import dev.thatsmybaby.shared.provider.redis.ServerResponseKitsPacket;

public final class PacketHandler implements IPacketHandler {

    @Override
    public void handle(RedisMessage packet) {
        if (packet instanceof ServerResponseKitsPacket) {
            Placeholders.log("Received response from " + ((ServerResponseKitsPacket) packet).serverName + " loading queue!");

            QueueFactory.getInstance().init(((ServerResponseKitsPacket) packet).kits);

            return;
        }
    }
}