package dev.thatsmybaby.practice.object.match.task;

import cn.nukkit.scheduler.Task;
import dev.thatsmybaby.practice.AbstractPractice;
import dev.thatsmybaby.practice.factory.MapFactory;
import dev.thatsmybaby.practice.object.GameMatch;
import dev.thatsmybaby.practice.object.player.GamePlayer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public final class GameMatchCountDownUpdateTask extends Task {

    private final GameMatch match;

    @Getter private int countdown = MapFactory.getInstance().getInitialCountdown();
    private int withoutUsage = 40;

    @Override
    public void onRun(int i) {
        if (this.match.getStatus().ordinal() <= GameMatch.MatchStatus.STARTING.ordinal()) {
            this.cancel();

            return;
        }

        if (this.match.canEnd()) {
            if (this.countdown != MapFactory.getInstance().getInitialCountdown()) {
                this.countdown = MapFactory.getInstance().getInitialCountdown();

                this.match.broadcastMessage("START_CANCELLED_NOT_ENOUGH_PLAYERS");

                this.match.end(false);
            }

            if (this.withoutUsage++ > 40) {
                this.match.forceClose();

                AbstractPractice.getInstance().getLogger().warning("Closing a game because have 40 seconds without any usage");
            }

            return;
        }

        if (this.withoutUsage != 0) {
            this.withoutUsage = 0;
        }

        this.match.getPlayers().values().forEach(player -> player.getScoreboardBuilder().update());

        if ((this.countdown > 0 && this.countdown < 6) || Arrays.asList(60, 50, 40, 30, 20, 15, 10).contains(this.countdown)) {
            this.match.broadcastMessage("GAME_STARTING", String.valueOf(this.countdown));
        }

        this.countdown--;

        if (this.countdown > 0) {
            return;
        }

        for (GamePlayer player : this.match.getPlayers().values()) {
            player.matchAttributes();

            player.getScoreboardBuilder().update();
        }

        this.match.scheduleRepeating(new GameMatchUpdateTask(this.match), 20);

        this.cancel();
    }
}