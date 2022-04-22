package dev.thatsmybaby.shared.provider.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public class ServerInfoRequestPacket extends RedisPacket {

    public String serverName;

    public ServerInfoRequestPacket() {
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
}