package dev.thatsmybaby.shared.object;

import cn.nukkit.Player;
import dev.thatsmybaby.shared.Placeholders;
import dev.thatsmybaby.shared.factory.QueueFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;

@RequiredArgsConstructor @Getter
public final class GameQueue {

    private final String serverName;
    private final String kitName;
    private final boolean ranked;

    private final LinkedHashMap<String, GameQueueProfile> profileLinkedHashMap = new LinkedHashMap<>();

    public long nextCheck = 0;

    public void joinAsPlayer(Player player) {
        this.profileLinkedHashMap.put(player.getName(), new GameQueueProfile(player.getName(), this.kitName, null, System.currentTimeMillis()));

        // TODO: Give hotbar items again

        player.sendMessage(Placeholders.replacePlaceholders("PLAYER_QUEUE_JOIN_" + (this.ranked ? "RANKED" : "UNRANKED")));
    }

    public void remove(String name) {
        this.profileLinkedHashMap.remove(name);
    }

    public String getQueueName() {
        return (this.ranked ? "Ranked" : "Unranked") + " " + this.kitName;
    }

    public void update() {
        if (this.profileLinkedHashMap.size() < 2 || this.nextCheck > System.currentTimeMillis()) {
            return;
        }

        GameQueueProfile firstQueueProfile = this.profileLinkedHashMap.values().stream().findFirst().orElse(null);

        if (firstQueueProfile == null) {
            return;
        }

        Player firstPlayer = firstQueueProfile.getInstance();

        if (firstPlayer == null) {
            this.profileLinkedHashMap.remove(firstQueueProfile.getName());

            Placeholders.log("The " + firstQueueProfile.getName() + " player is not online... Discarded");

            return;
        }

        GameQueueProfile secondQueueProfile = this.profileLinkedHashMap.values().stream().filter(gameQueueProfile -> gameQueueProfile.getName().equalsIgnoreCase(firstQueueProfile.getName())).findAny().orElse(null);

        if (secondQueueProfile == null) {
            Placeholders.log("Second player not found...");

            return;
        }

        Player secondPlayer = secondQueueProfile.getInstance();

        if (secondPlayer == null) {
            this.profileLinkedHashMap.remove(secondQueueProfile.getName());

            Placeholders.log("The " + secondQueueProfile.getName() + " player is not online... Discarded");

            return;
        }

        QueueFactory.getHandler().handle(this, firstPlayer, secondPlayer);
    }
}