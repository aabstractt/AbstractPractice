package dev.thatsmybaby.practice.factory;

import cn.nukkit.Player;
import dev.thatsmybaby.practice.object.GameKit;
import dev.thatsmybaby.practice.object.GameMap;
import dev.thatsmybaby.practice.object.GameMatch;
import dev.thatsmybaby.practice.object.GameQueue;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public final class MatchFactory {

    @Getter private final static MatchFactory instance = new MatchFactory();

    private final Map<String, GameMatch> matchMap = new HashMap<>();

    public GameMatch createMatch(Class<? extends GameMatch> type, GameQueue queue, GameMap map) {
        GameMatch match = this.getRandomMatch(type, queue, map);

        if (match != null) {
            return match;
        }

        if (map == null && (map = MapFactory.getInstance().getRandomMap()) == null) {
            return null;
        }

        try {
            match = type.getDeclaredConstructor(GameMatch.class, GameKit.class, Integer.class).newInstance(map, queue.getKit(), map.increaseTimesPlayed());

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
                filter(match -> match.getKit().getName().equals(queue.getKit().getName()));

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