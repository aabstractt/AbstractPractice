package dev.thatsmybaby.shared;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.TransferPacket;
import cn.nukkit.utils.TextFormat;

public final class Placeholders {

    public static Location stringNullableToLocation(String string) {
        return null;
    }

    public static Vector3 stringNullableToVector(String string) {
        return null;
    }

    public static String locationToString(Location location) {
        return vectorToString(location) + String.format(":%s:%s", location.yaw, location.pitch);
    }

    public static String vectorToString(Vector3 vec) {
        return String.format("%s:%s:%s", vec.getFloorX(), vec.getFloorY(), vec.getFloorZ());
    }

    public static String itemToString(Item item) {
        return null;
    }

    public static Item stringToItem(String string) {
        return null;
    }

    public static String replacePlaceholders(String k, String... args) {
        return null;
    }

    public static void log(String message) {
        Server.getInstance().getLogger().info(TextFormat.AQUA + message);
    }

    public static void connectTo(Player player, String serverName) {
        player.dataPacket(new TransferPacket() {{
            this.address = serverName;
        }});
    }
}