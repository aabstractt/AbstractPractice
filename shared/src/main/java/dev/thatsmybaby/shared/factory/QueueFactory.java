package dev.thatsmybaby.shared.factory;

import cn.nukkit.Player;
import cn.nukkit.Server;
import dev.thatsmybaby.shared.object.GameQueue;
import dev.thatsmybaby.shared.Callback;
import lombok.Getter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("deprecation")
public final class QueueFactory {

    @Getter private final static QueueFactory instance = new QueueFactory();

    private final Set<GameQueue> queueSet = new HashSet<>();

    public static Callback handler = null;

    public void init(List<String> kits) {
        for (String kitName : kits) {
            this.queueSet.add(new GameQueue(kitName, false));

            this.queueSet.add(new GameQueue(kitName, true));
        }

        Server.getInstance().getScheduler().scheduleRepeatingTask(() -> queueSet.forEach(GameQueue::update), 20, true);
    }

    public GameQueue getQueueType(String kitName, boolean ranked) {
        return this.queueSet.stream().filter(queue -> queue.getKitName().equalsIgnoreCase(kitName) && queue.isRanked() == ranked).findFirst().orElse(null);
    }

    public GameQueue getPlayerQueue(Player player) {
        return this.queueSet.stream().filter(queue -> queue.getPlayers().contains(player.getName())).findFirst().orElse(null);
    }
}