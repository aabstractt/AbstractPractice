package dev.thatsmybaby.practice.object;

import cn.nukkit.Player;
import cn.nukkit.Server;
import dev.thatsmybaby.practice.AbstractPractice;
import dev.thatsmybaby.practice.factory.MatchFactory;
import dev.thatsmybaby.practice.object.match.DuelMatch;
import dev.thatsmybaby.shared.Placeholders;
import dev.thatsmybaby.shared.TaskUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor @Getter
public final class GameQueue {

    private final GameKit kit;
    private final boolean ranked;

    private final LinkedList<String> players = new LinkedList<>();

    private long nextCheck = 0;

    public void addPlayer(Player player) {
        this.players.add(player.getName());

        // TODO: Give hotbar items again

        player.sendMessage(Placeholders.replacePlaceholders("PLAYER_QUEUE_JOIN_" + (this.ranked ? "RANKED" : "UNRANKED")));
    }

    public String getQueueName() {
        return (this.ranked ? "Ranked" : "Unranked") + " " + this.kit.getName();
    }

    public void update() {
        if (this.players.size() < 2 || this.nextCheck > System.currentTimeMillis()) {
            return;
        }

        String firstPlayerName = this.players.stream().findAny().orElse(null);
        Player firstPlayer = Server.getInstance().getPlayerExact(firstPlayerName);

        if (firstPlayer == null) {
            this.players.remove(firstPlayerName);

            AbstractPractice.debug("Player " + firstPlayerName + " non is online... Discarding!");

            return;
        }

        String secondPlayerName = this.players.stream().filter(playerName -> !playerName.equalsIgnoreCase(firstPlayerName)).findAny().orElse(null);

        if (secondPlayerName == null) {
            AbstractPractice.debug("Second player not found...");

            return;
        }

        Player secondPlayer = Server.getInstance().getPlayerExact(secondPlayerName);

        if (secondPlayer == null) {
            this.players.remove(secondPlayerName);

            AbstractPractice.debug("Player " + secondPlayerName + " non is online... Discarding!");

            return;
        }

        GameMatch match = MatchFactory.getInstance().createMatch(DuelMatch.class, this, null);

        if (match == null) {
            AbstractPractice.debug("Match for " + this.getQueueName() + " not found... Trying to search again in 5 seconds");

            this.nextCheck = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5);

            return;
        }

        this.players.remove(firstPlayerName);
        this.players.remove(secondPlayerName);

        match.joinAsPlayer(firstPlayer);
        match.joinAsPlayer(secondPlayer);

        // TODO: When execute Match#generateWorld() going to move start the match? idk going to see how make it
        TaskUtils.runAsync(match::generateWorld);
    }
}