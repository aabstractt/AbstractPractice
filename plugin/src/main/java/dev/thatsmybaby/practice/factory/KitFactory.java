package dev.thatsmybaby.practice.factory;

import cn.nukkit.item.Item;
import cn.nukkit.utils.Config;
import dev.thatsmybaby.practice.AbstractPractice;
import dev.thatsmybaby.practice.object.GameKit;
import dev.thatsmybaby.shared.Placeholders;
import lombok.Getter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public final class KitFactory {

    @Getter private final static KitFactory instance = new KitFactory();

    private final Map<String, GameKit> kits = new HashMap<>();

    public void init() {
        Config config = new Config(new File(AbstractPractice.getInstance().getDataFolder(), "kits.json"));

        for (String kitName : config.getKeys()) {
            Map<Integer, Item> contents = new HashMap<>();

            for (Map.Entry<Integer, String> entry : ((Map<Integer, String>) config.get(kitName)).entrySet()) {
                contents.put(entry.getKey(), Placeholders.stringToItem(entry.getValue()));
            }

            this.registerNewKit(new GameKit(kitName, contents), false);
        }
    }

    public void registerNewKit(GameKit kit, boolean forceSave) {
        this.kits.put(kit.getName(), kit);

        if (forceSave) {
            kit.forceSave();
        }
    }

    public GameKit getKit(String kitName) {
        return this.kits.get(kitName.toLowerCase());
    }
}