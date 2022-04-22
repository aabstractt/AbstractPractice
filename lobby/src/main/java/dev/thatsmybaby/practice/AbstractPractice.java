package dev.thatsmybaby.practice;

import cn.nukkit.plugin.PluginBase;
import dev.thatsmybaby.shared.provider.GameProvider;
import lombok.Getter;

public final class AbstractPractice extends PluginBase {

    @Getter private static AbstractPractice instance;

    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();

        GameProvider.getInstance().init(this.getConfig().getString("redis.address"), this.getConfig().getString("redis.password"), new PacketHandler());
    }
}