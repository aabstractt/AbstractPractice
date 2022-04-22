package dev.thatsmybaby.practice.factory;

import cn.nukkit.Player;
import cn.nukkit.utils.TextFormat;
import dev.thatsmybaby.practice.object.GameKit;
import dev.thatsmybaby.practice.object.GameMap;
import dev.thatsmybaby.practice.object.GameMatch;
import dev.thatsmybaby.practice.object.match.DuelMatch;
import dev.thatsmybaby.shared.Placeholders;
import dev.thatsmybaby.shared.TaskUtils;
import dev.thatsmybaby.shared.factory.QueueFactory;
import dev.thatsmybaby.shared.object.GameQueue;
import dev.thatsmybaby.shared.provider.GameProvider;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class MatchFactory {

    @Getter private final static MatchFactory instance = new MatchFactory();

    private final Map<String, GameMatch> matchMap = new HashMap<>();

    public void init() {
        if (GameProvider.getInstance().enabled()) {
            return;
        }

        QueueFactory.getInstance().init(KitFactory.getInstance().getKits().values().stream().map(GameKit::getName).collect(Collectors.toList()));

        QueueFactory.handler = (queue, firstPlayer, secondPlayer) -> {
            GameMatch match = this.createMatch(DuelMatch.class, queue, null);

            if (match == null) {
                Placeholders.log("Match for " + queue.getQueueName() + " not found... Trying to search again in 5 seconds");

                firstPlayer.sendMessage(TextFormat.RED + "Tried to start a match but there are no available arenas.");
                secondPlayer.sendMessage(TextFormat.RED + "Tried to start a match but there are no available arenas.");

                queue.nextCheck = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5);

                return;
            }

            queue.getPlayers().remove(firstPlayer.getName());
            queue.getPlayers().remove(secondPlayer.getName());

            match.joinAsPlayer(firstPlayer);
            match.joinAsPlayer(secondPlayer);

            firstPlayer.sendMessage(Placeholders.replacePlaceholders("QUEUE_FOUND_OPPONENT", firstPlayer.getName(), secondPlayer.getName()));
            secondPlayer.sendMessage(Placeholders.replacePlaceholders("QUEUE_FOUND_OPPONENT", secondPlayer.getName(), firstPlayer.getName()));

            if (match.worldGenerated()) {
                match.start();
            } else {
                TaskUtils.runAsync(match::generateWorld);
            }
        };
    }

    public GameMatch createMatch(Class<? extends GameMatch> type, GameQueue queue, GameMap map) {
        GameMatch match = this.getRandomMatch(type, queue, map);

        if (match != null) {
            return match;
        }

        if (map == null && (map = MapFactory.getInstance().getRandomMap()) == null) {
            return null;
        }

        GameKit kit = KitFactory.getInstance().getKit(queue.getKitName());

        if (kit == null) {
            return null;
        }

        try {
            match = type.getDeclaredConstructor(GameMap.class, GameKit.class, Integer.class).newInstance(map, kit, map.increaseTimesPlayed());

            this.matchMap.put(match.getWorldName(), match);

            return match;
        } catch (Exception e) {
            return null;
        }
    }

    private GameMatch getRandomMatch(Class<? extends GameMatch> type, GameQueue queue, GameMap map) {
        Stream<GameMatch> stream = this.matchMap.values().stream().
                filter(match -> match.getClass().isAssignableFrom(type)).
                filter(GameMatch::isIdle).
                filter(match -> match.getKit().getName().equals(queue.getKitName()));

        if (map != null) {
            stream = stream.filter(match -> match.getMap().getMapName().equals(map.getMapName()));
        }

        return stream.findAny().orElse(null);
    }

    public GameMatch getPlayerMatch(Player player) {
        return this.matchMap.values().stream().filter(match -> match.inArena(player)).findAny().orElse(null);
    }

    public void unregisterMatch(String worldName) {
        this.matchMap.remove(worldName);
    }
}