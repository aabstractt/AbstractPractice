package dev.thatsmybaby.practice.listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.utils.TextFormat;
import dev.thatsmybaby.practice.factory.MatchFactory;
import dev.thatsmybaby.practice.object.GameMatch;
import dev.thatsmybaby.practice.object.match.DuelMatch;
import dev.thatsmybaby.shared.TaskUtils;
import dev.thatsmybaby.shared.object.CrossServerPlayer;
import dev.thatsmybaby.shared.provider.GameProvider;

public final class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent ev) {
        Player player = ev.getPlayer();

        if (!GameProvider.getInstance().enabled()) return;

        TaskUtils.runAsync(() -> {
            CrossServerPlayer crossServerPlayer = GameProvider.getInstance().getPlayerCrossServer(player.getLoginChainData().getXUID());

            if (crossServerPlayer == null) {
                return;
            }

            GameMatch match = MatchFactory.getInstance().getPlayerMatch(crossServerPlayer.getOpponent());

            if (match == null) {
                match = MatchFactory.getInstance().createMatch(DuelMatch.class, crossServerPlayer.getKitName(), null);

                if (match == null) {
                    player.kick(TextFormat.RED + "Tried to start a match but there are no available arenas.");

                    return;
                }

                if (!match.worldGenerated()) {
                    match.generateWorld();
                }
            }

            match.joinAsPlayer(player);

            if (match.getPlayersAlive().size() < 2) return;

            match.start();
        });
    }
}