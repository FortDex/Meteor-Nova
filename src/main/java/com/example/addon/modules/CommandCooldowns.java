package com.example.addon.modules;

import com.example.addon.Addon;
import com.example.addon.utils.NovaChatUtils;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.game.SendMessageEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import net.minecraft.util.Formatting;

public class CommandCooldowns extends Module {
    private final SettingGroup sgTpa = settings.createGroup("TPA");
    private final SettingGroup sgHome = settings.createGroup("Home");

    // TPA Settings
    private final Setting<Integer> tpaCooldown = sgTpa.add(new IntSetting.Builder()
            .name("tpa-cooldown")
            .description("Cooldown for /tpa command in seconds.")
            .defaultValue(300) // 5 minutes
            .min(0)
            .build());

    // Home Settings
    private final Setting<Integer> homeCooldown = sgHome.add(new IntSetting.Builder()
            .name("home-cooldown")
            .description("Cooldown for /home command in seconds.")
            .defaultValue(60) // 1 minute
            .min(0)
            .build());

    private int tpaTimer = -1;
    private int homeTimer = -1;

    public CommandCooldowns() {
        super(Addon.CATEGORY, "command-cooldowns", "Tracks cooldowns for TPA and Home commands.");
    }

    @Override
    public void onActivate() {
        NovaChatUtils.sendToggleMsg("CommandCooldowns", true);
    }

    @Override
    public void onDeactivate() {
        NovaChatUtils.sendToggleMsg("CommandCooldowns", false);
    }

    @EventHandler
    private void onSendMessage(SendMessageEvent event) {
        String msg = event.message.trim();

        if (msg.startsWith("/tpa ") || msg.equals("/tpa")) {
            if (tpaTimer < 0) { // タイマーが動いていない場合のみセット
                tpaTimer = tpaCooldown.get() * 20; // Seconds to Ticks
                info("TPA cooldown started: " + tpaCooldown.get() + "s");
            }
        } else if (msg.startsWith("/home ") || msg.equals("/home")) {
            if (homeTimer < 0) {
                homeTimer = homeCooldown.get() * 20;
                info("Home cooldown started: " + homeCooldown.get() + "s");
            }
        }
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        // TPA Timer
        if (tpaTimer >= 0) {
            tpaTimer--;
            if (tpaTimer == 0) {
                info(Formatting.GREEN + "TPA is ready!");
                tpaTimer = -1;
            }
        }

        // Home Timer
        if (homeTimer >= 0) {
            homeTimer--;
            if (homeTimer == 0) {
                info(Formatting.GREEN + "Home is ready!");
                homeTimer = -1;
            }
        }
    }
}
