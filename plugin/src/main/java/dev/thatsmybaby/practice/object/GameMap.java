package dev.thatsmybaby.practice.object;

import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import dev.thatsmybaby.practice.AbstractPractice;
import dev.thatsmybaby.shared.Placeholders;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor @Getter
public final class GameMap {

    private final String mapName;
    private final List<GameKit> kits;

    @Setter private boolean partyAllowed;
    @Setter private Location firstPosition;
    @Setter private Location secondPosition;
    @Setter private Vector3 firstCorner;
    @Setter private Vector3 secondCorner;

    public void addKit(GameKit kit) {
        this.kits.add(kit);
    }

    public void removeKit(String kitName) {
        this.kits.removeIf(kit -> kit.getName().equalsIgnoreCase(kitName));
    }

    public void forceSave() {
        Config config = new Config(new File(AbstractPractice.getInstance().getDataFolder(), "maps.json"));

        config.set(this.mapName, new HashMap<String, Object>() {{
            put("kits", GameMap.this.kits.stream().map(GameKit::getName).collect(Collectors.toList()));

            put("partyAllowed", GameMap.this.partyAllowed);

            put("firstPosition", Placeholders.locationToString(GameMap.this.firstPosition));
            put("secondPosition", Placeholders.locationToString(GameMap.this.secondPosition));

            put("firstCorner", Placeholders.vectorToString(GameMap.this.firstCorner));
            put("secondCorner", Placeholders.vectorToString(GameMap.this.secondCorner));
        }});

        config.save();
    }
}