package dev.thatsmybaby.practice.object.player.scoreboard;

import cn.nukkit.Player;
import cn.nukkit.utils.TextFormat;
import dev.thatsmybaby.practice.AbstractPractice;
import dev.thatsmybaby.practice.object.GameMatch;
import dev.thatsmybaby.practice.object.player.GamePlayer;
import dev.thatsmybaby.practice.object.player.scoreboard.packets.RemoveObjectivePacket;
import dev.thatsmybaby.practice.object.player.scoreboard.packets.SetDisplayObjectivePacket;
import dev.thatsmybaby.practice.object.player.scoreboard.packets.SetScorePacket;
import dev.thatsmybaby.practice.object.player.scoreboard.packets.entry.ScorePacketEntry;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class ScoreboardBuilder {

    public static String LIST = "list";
    public static String SIDEBAR = "sidebar";

    public static int ASCENDING = 0;
    public static int DESCENDING = 1;

    private GamePlayer player;
    private String displayName;
    private Map<String, List<String>> scoreboardLines;
    private String objectiveName;
    private String displaySlot;
    private int sortOrder;

    public SetDisplayObjectivePacket initialize() {
        return new SetDisplayObjectivePacket() {{
            this.displaySlot = ScoreboardBuilder.this.displaySlot;

            this.objectiveName = ScoreboardBuilder.this.objectiveName;
            this.displayName = ScoreboardBuilder.this.displayName;

            this.criteriaName = "dummy";

            this.sortOrder = ScoreboardBuilder.this.sortOrder;
        }};
    }

    public void update() {
        Player instance = this.player.getInstance();

        if (instance == null || !instance.isOnline()) return;

        GameMatch match = this.player.getMatch();

        List<String> lines = new ArrayList<>();

        for (String line : this.scoreboardLines.get("default")) {
            List<String> otherLines = this.scoreboardLines.getOrDefault(line.replace("%", ""), new ArrayList<>());

            if (otherLines.isEmpty()) {
                lines.add(line);

                continue;
            }

            lines.addAll(otherLines);
        }

        this.removeLines();

        int slot = 1;

        for (String text : lines) {
            if ((text = replacePlaceholders(text, match)) == null) {
                continue;
            }

            this.setLine(slot++, text);
        }
    }

    public RemoveObjectivePacket remove() {
        return new RemoveObjectivePacket() {{
            this.objectiveName = ScoreboardBuilder.this.objectiveName;
        }};
    }

    private void removeLines() {
        Player instance = this.player.getInstance();

        for (int i = 0; i < 15; i++) {
            if (instance == null || !instance.isOnline()) {
                return;
            }

            instance.dataPacket(getPackets(i, "", SetScorePacket.TYPE_REMOVE));
        }
    }

    private String replacePlaceholders(String text, GameMatch match) {
        int starting = 0;

//        if (!match.isStarted()) {
//            MatchCountDownUpdateTask task = match.forceGetScheduler(MatchCountDownUpdateTask.class);
//
//            if (task != null) {
//                starting = task.getCountdown();
//            }
//        }

        String finalStartingString = String.valueOf(starting);
        Map<String, String> map = new HashMap<String, String>() {{
            put("%refilled%", String.valueOf(false));
            put("%refill_time%", "05:00");
            put("%server_name%", AbstractPractice.getServerName());
            put("%players_count%", String.valueOf(match.getPlayersAlive().size()));
            put("%starting_time%", finalStartingString);
        }};

        for (Map.Entry<String, String> entry : map.entrySet()) {
            text = text.replaceAll(entry.getKey(), entry.getValue());
        }

        return shouldDisplay(text);
    }

    private String shouldDisplay(String text) {
        if (!text.contains("<display=")) {
            return text;
        }

        String[] split = text.split("<display=");

        if (split[1].equals("!false") || split[1].equals("true")) {
            return split[0];
        }

        return null;
    }

    public void setLine(int scoreboardId, String text) {
        Player instance = this.player.getInstance();

        if (instance == null || !instance.isOnline()) return;

        instance.dataPacket(getPackets(scoreboardId, text, SetScorePacket.TYPE_REMOVE));

        instance.dataPacket(getPackets(scoreboardId, text, SetScorePacket.TYPE_CHANGE));
    }

    public SetScorePacket getPackets(int scoreboardId, String text, byte type) {
        SetScorePacket pk = new SetScorePacket();

        pk.type = type;

        List<ScorePacketEntry> entries = new ArrayList<>();

        ScorePacketEntry entry0 = new ScorePacketEntry();

        entry0.objectiveName = this.objectiveName;

        entry0.score = scoreboardId;

        entry0.scoreboardId = scoreboardId;

        if (type == SetScorePacket.TYPE_CHANGE) {
            entry0.type = ScorePacketEntry.TYPE_FAKE_PLAYER;

            entry0.customName = TextFormat.colorize(text);
        }

        entries.add(entry0);

        pk.entries = entries.toArray(new ScorePacketEntry[0]);

        return pk;
    }
}