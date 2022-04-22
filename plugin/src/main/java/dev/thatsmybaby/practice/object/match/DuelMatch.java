package dev.thatsmybaby.practice.object.match;

import cn.nukkit.Player;
import dev.thatsmybaby.practice.object.GameKit;
import dev.thatsmybaby.practice.object.GameMap;
import dev.thatsmybaby.practice.object.player.GamePlayer;

public final class DuelMatch extends GameMatch {

    public DuelMatch(GameMap map, GameKit kit, int id) {
        super(map, kit, id);
    }

    @Override
    public void start() {
        boolean first = true;

        for (GamePlayer gamePlayer : this.getPlayersAlive()) {
            gamePlayer.getInstance().teleport(first ? this.map.getFirstPosition() : this.map.getSecondPosition());

            first = !first;
        }

        super.start();
    }

    @Override
    public GamePlayer fetchGamePlayer(Player player) {
        return new GamePlayer(player.getLoginChainData().getXUID(), player.getName(), this);
    }
}