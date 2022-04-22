package dev.thatsmybaby.shared.provider.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import java.util.ArrayList;
import java.util.List;

public class ServerResponseKitsPacket extends RedisPacket {

    public String serverName;

    public List<String> kits = new ArrayList<>();

    public ServerResponseKitsPacket() {
        super(1);
    }

    @Override
    public void decode(ByteArrayDataInput stream) {
        this.serverName = stream.readUTF();

        for (int i = 0; i < stream.readInt(); i++) {
            this.kits.add(stream.readUTF());
        }
    }

    @Override
    public void encode(ByteArrayDataOutput stream) {
        stream.writeUTF(this.serverName);

        stream.writeInt(this.kits.size());

        for (String kitName : this.kits) {
            stream.writeUTF(kitName);
        }
    }
}