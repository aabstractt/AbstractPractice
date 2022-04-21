package dev.thatsmybaby.practice.factory;

import cn.nukkit.Server;
import dev.thatsmybaby.practice.AbstractPractice;
import dev.thatsmybaby.practice.object.GameKit;
import dev.thatsmybaby.practice.object.GameQueue;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

public final class QueueFactory {

    @Getter private final static QueueFactory instance = new QueueFactory();

    private final Set<GameQueue> queueSet = new HashSet<>();

    public void init() {
        for (GameKit kit : KitFactory.getInstance().getKits().values()) {
            this.queueSet.add(new GameQueue(kit, false));

            this.queueSet.add(new GameQueue(kit, true));
        }

        Server.getInstance().getScheduler().scheduleRepeatingTask(AbstractPractice.getInstance(), () -> {
            queueSet.forEach(GameQueue::update);
        }, 20, true);
    }
}