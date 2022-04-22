package dev.thatsmybaby.shared.object;

import cn.nukkit.Player;
import cn.nukkit.Server;
import dev.thatsmybaby.shared.Placeholders;
import dev.thatsmybaby.shared.factory.QueueFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.LinkedList;

@RequiredArgsConstructor @Getter
public final class GameQueue {

    private final String kitName;
    private final boolean ranked;

    private final LinkedList<String> players = new LinkedList<>();

    public long nextCheck = 0;

    public void joinAsPlayer(Player player) {
        this.players.add(player.getName());

        // TODO: Give hotbar items again

        player.sendMessage(Placeholders.replacePlaceholders("PLAYER_QUEUE_JOIN_" + (this.ranked ? "RANKED" : "UNRANKED")));
    }

    public String getQueueName() {
        return (this.ranked ? "Ranked" : "Unranked") + " " + this.kitName;
    }

    public void update() {
        if (this.players.size() < 2 || this.nextCheck > System.currentTimeMillis()) {
            return;
        }

        String firstPlayerName = this.players.stream().findAny().orElse(null);
        Player firstPlayer = Server.getInstance().getPlayerExact(firstPlayerName);

        if (firstPlayer == null) {
            this.players.remove(firstPlayerName);

            Placeholders.log("The " + firstPlayerName + " player is not online... Discarded");

            return;
        }

        String secondPlayerName = this.players.stream().filter(playerName -> !playerName.equalsIgnoreCase(firstPlayerName)).findAny().orElse(null);

        if (secondPlayerName == null) {
            Placeholders.log("Second player not found...");

            return;
        }

        Player secondPlayer = Server.getInstance().getPlayerExact(secondPlayerName);
        if (secondPlayer == null) {
            this.players.remove(secondPlayerName);

            Placeholders.log("The " + secondPlayerName + " player is not online... Discarded");

            return;
        }

        QueueFactory.handler.handle(this, firstPlayer, secondPlayer);
    }
}