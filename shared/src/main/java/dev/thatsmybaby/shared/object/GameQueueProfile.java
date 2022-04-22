package dev.thatsmybaby.shared.object;

import cn.nukkit.Player;
import cn.nukkit.Server;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class GameQueueProfile {

    @Getter private final String name;
    @Getter private final String kitName;
    @Getter private final String mapSelected;
    @Getter private final long startAt;

    public Player getInstance() {
        return Server.getInstance().getPlayerExact(this.name);
    }
}