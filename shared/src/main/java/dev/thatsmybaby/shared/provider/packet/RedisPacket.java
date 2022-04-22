package dev.thatsmybaby.shared.provider.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;

public abstract class RedisPacket {

    @Getter protected int id;

    public RedisPacket(int id) {
        this.id = id;
    }

    public abstract void decode(ByteArrayDataInput stream);

    public abstract void encode(ByteArrayDataOutput stream);
}