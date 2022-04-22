package dev.thatsmybaby.practice.object;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import dev.thatsmybaby.practice.AbstractPractice;
import dev.thatsmybaby.practice.factory.MapFactory;
import dev.thatsmybaby.practice.factory.MatchFactory;
import dev.thatsmybaby.practice.object.match.task.GameMatchCountDownUpdateTask;
import dev.thatsmybaby.practice.object.player.GamePlayer;
import dev.thatsmybaby.shared.Placeholders;
import dev.thatsmybaby.shared.task.TaskHandlerStorage;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class GameMatch extends TaskHandlerStorage {

    public enum MatchStatus {
        IDLE(),
        STARTING(),
        IN_GAME(),
        RESTARTING()
    }

    @Getter protected final GameMap map;
    @Getter protected final GameKit kit;

    @Getter protected final int id;
    @Getter protected final String worldName;

    @Getter @Setter private MatchStatus status = MatchStatus.STARTING;

    @Getter private final Map<String, GamePlayer> players = new HashMap<>();

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
        GamePlayer gamePlayer = this.fetchGamePlayer(player);

        this.players.put(gamePlayer.getXuid(), gamePlayer);

        gamePlayer.defaultAttributes();

        player.dataPacket(gamePlayer.getScoreboardBuilder().initialize());

        this.pushScoreboardUpdate();
    }
    
    public GamePlayer getPlayer(Player player) {
        return this.players.get(player.getLoginChainData().getXUID());
    }

    public boolean inArenaAsPlayer(Player player) {
        GamePlayer gamePlayer = this.getPlayer(player);

        return gamePlayer != null && !gamePlayer.isSpectating();
    }

    public boolean inArenaAsSpectator(Player player) {
        GamePlayer gamePlayer = this.getPlayer(player);

        return gamePlayer != null && gamePlayer.isSpectating();
    }
    
    public boolean inArena(Player player) {
        return this.getPlayer(player) != null;
    }

    public Set<GamePlayer> getPlayersAlive() {
        return this.players.values().stream().filter(player -> !player.isSpectating()).collect(Collectors.toSet());
    }

    public Set<GamePlayer> getSpectators() {
        return this.players.values().stream().filter(GamePlayer::isSpectating).collect(Collectors.toSet());
    }
    
    public void broadcastMessage(String message, String... args) {
        for (Player player : this.getWorld().getPlayers().values()) {
            player.sendMessage(Placeholders.replacePlaceholders(message, args));
        }
    }

    public void start() {
        this.status = MatchStatus.STARTING;

        if (this.getLastScheduler() instanceof GameMatchCountDownUpdateTask) {
            return;
        }

        this.scheduleRepeating(new GameMatchCountDownUpdateTask(this), 20);
    }

    public void end(boolean reset) {
        for (GamePlayer player : this.players.values()) {
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

    public abstract GamePlayer fetchGamePlayer(Player player);
}