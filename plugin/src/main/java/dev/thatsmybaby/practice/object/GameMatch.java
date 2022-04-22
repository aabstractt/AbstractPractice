package dev.thatsmybaby.practice.object;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import dev.thatsmybaby.practice.AbstractPractice;
import dev.thatsmybaby.practice.factory.MapFactory;
import dev.thatsmybaby.practice.factory.MatchFactory;
import dev.thatsmybaby.practice.object.match.task.GameMatchCountDownUpdateTask;
import dev.thatsmybaby.practice.object.player.DuelPlayer;
import dev.thatsmybaby.shared.task.TaskHandlerStorage;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public abstract class GameMatch extends TaskHandlerStorage {

    public enum MatchStatus {
        IDLE(),
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

    public boolean isIdle() {
        return this.status == MatchStatus.IDLE;
    }

    public Level getWorld() {
        return Server.getInstance().getLevelByName(this.worldName);
    }

    public boolean worldGenerated() {
        return this.getWorld() != null;
    }

    public void generateWorld() {
        MapFactory.getInstance().copyMap(
                new File(AbstractPractice.getInstance().getDataFolder(), "backups/" + this.map.getMapName()),
                new File(Server.getInstance().getDataPath(), "worlds/" + this.worldName)
        );

        this.start();
    }

    public void joinAsPlayer(Player player) {
        DuelPlayer duelPlayer = this.fetchDuelPlayer(player);

        this.players.put(duelPlayer.getXuid(), duelPlayer);

        duelPlayer.defaultAttributes();

        this.pushScoreboardUpdate();
    }

    public void broadcastMessage(String message, String... args) {

    }

    public void start() {
        this.status = MatchStatus.STARTING;

        if (this.getLastScheduler() instanceof GameMatchCountDownUpdateTask) {
            return;
        }

        this.scheduleRepeating(new GameMatchCountDownUpdateTask(this), 20);
    }

    public void end(boolean reset) {
        for (DuelPlayer player : this.players.values()) {
            Player instance = player.getInstance();

            if (instance == null) {
                continue;
            }

            player.defaultAttributes();

            instance.teleport(Server.getInstance().getDefaultLevel().getSpawnLocation());
        }

        if (!reset) return;

        this.status = MatchStatus.RESTARTING;

        // TODO: Reset task
    }

    public boolean canEnd() {
        return false;
    }

    public void forceClose() {
        this.cancelTasks();

        this.players.clear();

        // TODO: Remove map

        MatchFactory.getInstance().unregisterMatch(this.worldName);
    }

    public void pushScoreboardUpdate() {

    }

    public abstract DuelPlayer fetchDuelPlayer(Player player);
}