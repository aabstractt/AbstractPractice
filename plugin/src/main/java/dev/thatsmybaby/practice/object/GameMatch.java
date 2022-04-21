package dev.thatsmybaby.practice.object;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import dev.thatsmybaby.practice.object.match.task.GameCountDownUpdateTask;
import dev.thatsmybaby.practice.object.player.DuelPlayer;
import dev.thatsmybaby.shared.task.TaskHandlerStorage;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public abstract class GameMatch extends TaskHandlerStorage {

    public enum MatchStatus {
        STARTING(),
        IN_GAME(),
        RESTARTING()
    }

    @Getter private final GameMap map;
    @Getter private final GameKit kit;

    @Getter private final int id;
    @Getter private final String worldName;

    @Getter @Setter private MatchStatus status = MatchStatus.STARTING;

    @Getter private final Map<String, DuelPlayer> players = new HashMap<>();

    public GameMatch(GameMap map, GameKit kit, int id) {
        this.map = map;
        this.kit = kit;

        this.id = id;

        this.worldName = map.getMapName() + "-" + kit.getName() + "-" + id;
    }

    public Level getWorld() {
        return Server.getInstance().getLevelByName(this.worldName);
    }

    public void generateWorld() {
        // TODO: Stuff
    }

    public void joinAsPlayer(Player player) {
        DuelPlayer duelPlayer = this.fetchDuelPlayer(player);

        this.players.put(duelPlayer.getXuid(), duelPlayer);

        duelPlayer.defaultAttributes();

        this.pushScoreboardUpdate();
    }

    public void pushScoreboardUpdate() {

    }

    public abstract DuelPlayer fetchDuelPlayer(Player player);
}