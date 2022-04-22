package dev.thatsmybaby.shared.provider.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public final class MatchResponsePacket extends RedisPacket {

    public String serverName;

    public String worldName;

    public MatchResponsePacket() {
        super(3);
    }

    @Override
    public void decode(ByteArrayDataInput stream) {
        this.serverName = stream.readUTF();

        this.worldName = stream.readUTF();
    }

    @Override
    public void encode(ByteArrayDataOutput stream) {
        stream.writeUTF(this.serverName);

        stream.writeUTF(this.worldName);
    }
}