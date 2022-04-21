package dev.thatsmybaby.practice.object.match;

import cn.nukkit.Player;
import dev.thatsmybaby.practice.object.GameKit;
import dev.thatsmybaby.practice.object.GameMap;
import dev.thatsmybaby.practice.object.GameMatch;
import dev.thatsmybaby.practice.object.player.DuelPlayer;

public final class DuelMatch extends GameMatch {

    public DuelMatch(GameMap map, GameKit kit, int id) {
        super(map, kit, id);
    }

    @Override
    public DuelPlayer fetchDuelPlayer(Player player) {
        return new DuelPlayer(player.getLoginChainData().getXUID(), player.getName(), this);
    }
}