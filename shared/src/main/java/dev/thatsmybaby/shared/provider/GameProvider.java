package dev.thatsmybaby.shared.provider;

import cn.nukkit.Server;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import dev.thatsmybaby.shared.provider.redis.RedisMessage;
import dev.thatsmybaby.shared.provider.redis.ServerRequestKitsPacket;
import dev.thatsmybaby.shared.provider.redis.ServerResponseKitsPacket;
import lombok.Getter;
import redis.clients.jedis.*;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings({"UnstableApiUsage", "deprecation"})
public final class GameProvider {

    @Getter private final static GameProvider instance = new GameProvider();

    private IPacketHandler handler = null;

    private static final Map<Integer, Class<? extends RedisMessage>> messagesPool = new HashMap<>();

    private JedisPool jedisPool;
    private Subscription jedisPubSub = null;

    private String password = null;

    public void init(String address, String password, IPacketHandler handler) {
        if (address == null) {
            return;
        }

        String[] addressSplit = address.split(":");
        String host = addressSplit[0];
        int port = addressSplit.length > 1 ? Integer.parseInt(addressSplit[1]) : Protocol.DEFAULT_PORT;

        this.password = password != null && password.length() > 0 ? password : null;

        this.jedisPool = new JedisPool(new JedisPoolConfig(), host, port, 30_000, this.password, 0, null);

        this.handler = handler;

        ForkJoinPool.commonPool().execute(() -> execute(jedis -> {
            jedis.subscribe(this.jedisPubSub = new Subscription(), "SurvivalSync".getBytes(StandardCharsets.UTF_8));
        }));

        if (this.enabled()) {
            Server.getInstance().getLogger().info("Redis is now successfully connected and synchronization is ready!");
        } else {
            Server.getInstance().getLogger().info("Could not connect to redis, synchronization won't work with other servers");
        }

        registerMessage(new ServerRequestKitsPacket(), new ServerResponseKitsPacket());
    }

    public void publish(RedisMessage pk) {
        CompletableFuture.runAsync(() -> execute(jedis -> {
            ByteArrayDataOutput stream = ByteStreams.newDataOutput();

            stream.writeInt(pk.getId());
            pk.encode(stream);

            jedis.publish("SurvivalSync".getBytes(StandardCharsets.UTF_8), stream.toByteArray());
        }));
    }

    public static <T> T execute(Function<Jedis, T> action) {
        if (!instance.enabled()) {
            throw new RuntimeException("Jedis was disconnected");
        }

        try (Jedis jedis = instance.jedisPool.getResource()) {
            if (instance.password != null && !instance.password.isEmpty()) {
                jedis.auth(instance.password);
            }

            return action.apply(jedis);
        }
    }

    public static void execute(Consumer<Jedis> action) {
        if (!instance.enabled()) {
            return;
        }

        try (Jedis jedis = instance.jedisPool.getResource()) {
            if (instance.password != null && !instance.password.isEmpty()) {
                jedis.auth(instance.password);
            }

            action.accept(jedis);
        }
    }

    public void close() {
        if (this.jedisPubSub != null) {
            this.jedisPubSub.unsubscribe();
        }

        if (this.jedisPool != null) {
            this.jedisPool.destroy();
        }
    }

    public void registerMessage(RedisMessage... pools) {
        for (RedisMessage pool : pools) {
            messagesPool.put(pool.getId(), pool.getClass());
        }
    }

    private RedisMessage constructMessage(int pid) {
        Class<? extends RedisMessage> instance = messagesPool.get(pid);

        if (instance == null) {
            return null;
        }

        try {
            return instance.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean enabled() {
        return this.jedisPool != null && !this.jedisPool.isClosed();
    }

    protected class Subscription extends BinaryJedisPubSub {

        @Override
        public void onMessage(byte[] channel, byte[] message) {
            ByteArrayDataInput stream = ByteStreams.newDataInput(message);

            RedisMessage pk = constructMessage(stream.readInt());

            if (pk == null) {
                Server.getInstance().getLogger().warning("Redis packet received is null");

                return;
            }

            pk.decode(stream);

            GameProvider.this.handler.handle(pk);

            Server.getInstance().getLogger().info("Packet " + pk.getClass().getName() + " decoded and handled!");
        }
    }
}