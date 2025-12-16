package com.example.addon.modules;

import com.example.addon.Addon;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;

import java.util.HashSet;
import java.util.Set;

public class DeathLog extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> ignoreSelf = sgGeneral.add(new BoolSetting.Builder()
            .name("ignore-self")
            .description("Ignore your own deaths.")
            .defaultValue(true)
            .build());

    private final Set<Integer> diedPlayers = new HashSet<>();

    public DeathLog() {
        super(Addon.CATEGORY, "death-log", "Logs coordinates when a player dies nearby.");
    }

    @Override
    public void onActivate() {
        diedPlayers.clear();
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.world == null)
            return;

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (ignoreSelf.get() && player == mc.player)
                continue;

            if (player.getHealth() <= 0 || player.isDead()) {
                if (!diedPlayers.contains(player.getId())) {
                    logDeath(player);
                    diedPlayers.add(player.getId());
                }
            } else {
                // 生き返ったらリストから削除（リスポーン対応）
                diedPlayers.remove(player.getId());
            }
        }
    }

    private void logDeath(PlayerEntity player) {
        int x = (int) player.getX();
        int y = (int) player.getY();
        int z = (int) player.getZ();

        info(Formatting.RED + player.getName().getString() + Formatting.GRAY + " died at " +
                Formatting.GOLD + x + ", " + y + ", " + z);
    }
}
