package dev.thatsmybaby.practice.factory;

import cn.nukkit.utils.Config;
import dev.thatsmybaby.practice.AbstractPractice;
import dev.thatsmybaby.practice.object.GameMap;
import dev.thatsmybaby.shared.Placeholders;
import lombok.Getter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public final class MapFactory {

    @Getter private final static MapFactory instance = new MapFactory();

    private final Map<String, GameMap> maps = new HashMap<>();

    public void init() {
        Config config = new Config(new File(AbstractPractice.getInstance().getDataFolder(), "maps.json"));

        for (String mapName : config.getKeys()) {
            Map<String, Object> mapValues = (Map<String, Object>) config.get(mapName);

//            List<GameKit> kits = new ArrayList<>();
//            for (String kitName : (List<String>)mapValues.get("kits")) {
//                GameKit kit = KitFactory.getInstance().getKit(kitName);
//
//                if (kit == null) {
//                    continue;
//                }
//
//                kits.add(kit);
//            }

            this.registerNewMap(new GameMap(
                    mapName,
                    ((List<String>)mapValues.get("kits")).stream().map(kitName -> KitFactory.getInstance().getKit(kitName)).filter(Objects::nonNull).collect(Collectors.toList()),
                    (boolean) mapValues.get("partyAllowed"),
                    Placeholders.stringNullableToLocation(mapValues.get("firstPosition").toString()),
                    Placeholders.stringNullableToLocation(mapValues.get("secondPosition").toString()),
                    Placeholders.stringNullableToVector(mapValues.get("firstCorner").toString()),
                    Placeholders.stringNullableToVector(mapValues.get("secondCorner").toString())
            ), false);
        }
    }

    public void registerNewMap(GameMap map, boolean forceSave) {
        this.maps.put(map.getMapName().toLowerCase(), map);

        if (forceSave) {
            map.forceSave();
        }
    }

    public void copyMap(File from, File to) {
        try {
            FileUtils.copyDirectory(from, to);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GameMap getMap(String mapName) {
        return this.maps.get(mapName.toLowerCase());
    }
}