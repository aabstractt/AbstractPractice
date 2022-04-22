package dev.thatsmybaby.shared.provider.redis;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public final class ServerRequestKitsPacket extends RedisMessage {

    public String serverName;

    public ServerRequestKitsPacket() {
        super(0);
    }

    @Override
    public void decode(ByteArrayDataInput stream) {
        this.serverName = stream.readUTF();
    }

    @Override
    public void encode(ByteArrayDataOutput stream) {
        stream.writeUTF(this.serverName);
    }

    @Override
    public void handle() {

    }
}