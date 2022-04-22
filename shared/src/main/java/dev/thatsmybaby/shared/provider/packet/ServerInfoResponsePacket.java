package dev.thatsmybaby.shared.provider.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import dev.thatsmybaby.shared.object.CrossServerGameMap;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ServerInfoResponsePacket extends RedisPacket {

    public String serverName;

    public List<CrossServerGameMap> crossServerGameMaps = new LinkedList<>();
    public List<String> kits = new ArrayList<>();

    public ServerInfoResponsePacket() {
        super(1);
    }

    @Override
    public void decode(ByteArrayDataInput stream) {
        this.serverName = stream.readUTF();

        for (int i = 0; i < stream.readInt(); i++) {
            String mapName = stream.readUTF();

            List<String> kits = new ArrayList<>();

            for (int j = 0; j < stream.readInt(); j++) {
                kits.add(stream.readUTF());
            }

            this.crossServerGameMaps.add(new CrossServerGameMap(mapName, kits, stream.readBoolean()));
        }
    }

    @Override
    public void encode(ByteArrayDataOutput stream) {
        stream.writeUTF(this.serverName);

        stream.writeInt(this.crossServerGameMaps.size());

        for (CrossServerGameMap crossServerGameMap : this.crossServerGameMaps) {
            stream.writeUTF(crossServerGameMap.getMapName());

            stream.writeInt(crossServerGameMap.getKits().size());

            for (String kitName : crossServerGameMap.getKits()) {
                stream.writeUTF(kitName);
            }

            stream.writeBoolean(crossServerGameMap.isPartyAllowed());
        }
    }
}