package dev.thatsmybaby.practice.factory;

import dev.thatsmybaby.practice.object.GameKit;
import dev.thatsmybaby.practice.object.GameMap;
import dev.thatsmybaby.practice.object.GameMatch;
import dev.thatsmybaby.practice.object.GameQueue;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public final class MatchFactory {

    @Getter private final static MatchFactory instance = new MatchFactory();

    private final Map<String, GameMatch> matchMap = new HashMap<>();

    public GameMatch createMatch(Class<? extends GameMatch> type, GameQueue queue, GameMap map) {
        if (map == null) {
            if ((map = MapFactory.getInstance().getRandomMap()) == null) {
                return null;
            }
        }

        try {
            GameMatch match = type.getDeclaredConstructor(GameMatch.class, GameKit.class, Integer.class).newInstance(map, queue.getKit(), map.increaseTimesPlayed());

            this.matchMap.put(match.getWorldName(), match);

            return match;
        } catch (Exception e) {
            return null;
        }
    }
}