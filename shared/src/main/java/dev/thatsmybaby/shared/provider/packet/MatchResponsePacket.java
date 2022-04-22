package dev.thatsmybaby.shared.provider.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import java.util.ArrayList;
import java.util.List;

public class MatchResponsePacket extends RedisPacket {

    public String serverName;
    public String kitName;

    public String worldName;

    public List<String> players = new ArrayList<>();

    public MatchResponsePacket() {
        super(3);
    }

    @Override
    public void decode(ByteArrayDataInput stream) {
        this.serverName = stream.readUTF();
        this.kitName = stream.readUTF();

        this.worldName = stream.readUTF();

        for (int i = 0; i < stream.readInt(); i++) {
            this.players.add(stream.readUTF());
        }
    }

    @Override
    public void encode(ByteArrayDataOutput stream) {
        stream.writeUTF(this.serverName);
        stream.writeUTF(this.kitName);

        stream.writeUTF(this.worldName);

        for (String playerName : this.players) {
            stream.writeUTF(playerName);
        }
    }
}