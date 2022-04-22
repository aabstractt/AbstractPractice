package dev.thatsmybaby.shared.provider.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import java.util.ArrayList;
import java.util.List;

public class MatchRequestPacket extends RedisPacket {

    public String serverName;

    public List<String> players = new ArrayList<>();

    public String mapName = null;
    public String kitName;

    public MatchRequestPacket() {
        super(2);
    }

    @Override
    public void decode(ByteArrayDataInput stream) {
        this.serverName = stream.readUTF();

        for (int i = 0; i < stream.readInt(); i++) {
            this.players.add(stream.readUTF());
        }

        this.mapName = stream.readUTF();

        this.kitName = stream.readUTF();
    }

    @Override
    public void encode(ByteArrayDataOutput stream) {
        stream.writeUTF(this.serverName);

        for (String playerName : this.players) {
            stream.writeUTF(playerName);
        }

        stream.writeUTF(this.mapName);

        stream.writeUTF(this.kitName);
    }
}