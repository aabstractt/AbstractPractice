package dev.thatsmybaby.practice.object.player;

import cn.nukkit.Player;
import cn.nukkit.Server;
import dev.thatsmybaby.practice.object.GameMatch;
import dev.thatsmybaby.practice.object.player.scoreboard.ScoreboardBuilder;
import dev.thatsmybaby.shared.Placeholders;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor @Getter
public final class DuelPlayer {

    private final String xuid;
    private final String name;
    private final GameMatch match;
    private final ScoreboardBuilder scoreboardBuilder;

    @Setter private boolean spectating = false;

    public Player getInstance() {
        return Server.getInstance().getPlayerExact(this.name);
    }

    public void defaultAttributes() {
        Player instance = this.getInstance();

        if (instance == null) return;

        instance.setHealth(instance.getMaxHealth());
        instance.getFoodData().setLevel(20, 20);

        instance.resetFallDistance();

        instance.maxFireTicks = 20;
        instance.fireTicks = 0;

        instance.setExperience(0, 0);
        instance.setAllowFlight(false);
        instance.setGamemode(0);

        instance.getInventory().clearAll();
        instance.removeAllEffects();
        instance.clearTitle();

        instance.getInventory().setHeldItemSlot(0);
        instance.getInventory().setHeldItemIndex(0);
    }

    public void matchAttributes() {
        Player instance = this.getInstance();

        if (instance == null) return;

        this.defaultAttributes();

        this.match.getKit().giveAttributes(instance);

        instance.sendMessage(Placeholders.replacePlaceholders("MATCH_GIVE_KIT"));
    }
}