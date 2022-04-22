package dev.thatsmybaby.practice;

import dev.thatsmybaby.practice.factory.KitFactory;
import dev.thatsmybaby.practice.factory.MapFactory;
import dev.thatsmybaby.practice.object.GameKit;
import dev.thatsmybaby.practice.object.GameMap;
import dev.thatsmybaby.shared.Placeholders;
import dev.thatsmybaby.shared.object.CrossServerGameMap;
import dev.thatsmybaby.shared.provider.GameProvider;
import dev.thatsmybaby.shared.provider.IPacketHandler;
import dev.thatsmybaby.shared.provider.packet.RedisPacket;
import dev.thatsmybaby.shared.provider.packet.ServerInfoRequestPacket;
import dev.thatsmybaby.shared.provider.packet.ServerInfoResponsePacket;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class PacketHandler implements IPacketHandler {

    @Override
    public void handle(RedisPacket packet) {
        if (packet instanceof ServerInfoRequestPacket) {
            Placeholders.log(((ServerInfoRequestPacket) packet).serverName + " server requests the info from '" + AbstractPractice.getServerName() + "' server");

            List<CrossServerGameMap> list = new ArrayList<>();

            for (GameMap map : MapFactory.getInstance().getMaps().values()) {
                list.add(new CrossServerGameMap(
                        map.getMapName(),
                        map.getKits().stream().map(GameKit::getName).collect(Collectors.toList()),
                        map.isPartyAllowed()
                ));
            }

            GameProvider.publish(new ServerInfoResponsePacket() {{
                this.serverName = AbstractPractice.getServerName();

                this.crossServerGameMaps = list;
                this.kits = KitFactory.getInstance().getKits().values().stream().map(GameKit::getName).collect(Collectors.toList());
            }});

            return;
        }
    }
}