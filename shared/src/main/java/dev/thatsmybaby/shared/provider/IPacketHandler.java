package dev.thatsmybaby.shared.provider;

import dev.thatsmybaby.shared.provider.redis.RedisMessage;

public interface IPacketHandler {

    void handle(RedisMessage packet);
}