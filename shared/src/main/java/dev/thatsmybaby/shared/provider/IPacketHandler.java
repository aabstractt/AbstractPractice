package dev.thatsmybaby.shared.provider;

import dev.thatsmybaby.shared.provider.packet.RedisPacket;

public interface IPacketHandler {

    void handle(RedisPacket packet);
}