package dev.thatsmybaby.shared;

import cn.nukkit.Player;
import dev.thatsmybaby.shared.object.GameQueue;

public interface Callback {

    void handle(GameQueue queue, Player firstPlayer, Player secondPlayer);
}