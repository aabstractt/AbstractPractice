package dev.thatsmybaby.practice.object;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.utils.Config;
import dev.thatsmybaby.practice.AbstractPractice;
import dev.thatsmybaby.shared.Placeholders;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor @Getter
public final class GameKit {

    private final String name;
    @Setter private Map<Integer, Item> contents;

    public void giveAttributes(Player player) {
        player.getInventory().clearAll();
        player.getUIInventory().clearAll();
        player.getCursorInventory().clearAll();
        player.getOffhandInventory().clearAll();

        player.getInventory().setContents(this.contents);
    }

    public void forceSave() {
        Config config = new Config(new File(AbstractPractice.getInstance().getDataFolder(), "kits.json"));

        Map<Integer, String> contents = new HashMap<>();

        for (Map.Entry<Integer, Item> entry : this.contents.entrySet()) {
            contents.put(entry.getKey(), Placeholders.itemToString(entry.getValue()));
        }

        config.set(this.name, contents);
        config.save();
    }
}