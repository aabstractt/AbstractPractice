package dev.thatsmybaby.practice.listener;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockPlaceEvent;
import dev.thatsmybaby.practice.factory.MatchFactory;
import dev.thatsmybaby.practice.object.match.GameMatch;

public final class BlockPlaceListener implements Listener {

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent ev) {
        GameMatch match = MatchFactory.getInstance().getPlayerMatch(ev.getPlayer());

        if (match != null && match.getStatus() == GameMatch.MatchStatus.IN_GAME) {
            return;
        }

        ev.setCancelled();
    }
}