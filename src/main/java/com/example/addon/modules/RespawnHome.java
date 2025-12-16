package com.example.addon.modules;

import com.example.addon.Addon;
import com.example.addon.utils.NovaChatUtils;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import net.minecraft.client.gui.screen.DeathScreen;

import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.events.world.TickEvent;

public class RespawnHome extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<String> homeName = sgGeneral.add(new StringSetting.Builder()
            .name("home-name")
            .description("Name of the home to teleport to after respawning.")
            .defaultValue("base")
            .build());

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
            .name("delay")
            .description("Delay in ticks before sending the command.")
            .defaultValue(10)
            .min(0)
            .build());

    private int timer = -1;

    public RespawnHome() {
        super(Addon.CATEGORY, "respawn-home", "Automatically respawns and teleports home after death.");
    }

    @Override
    public void onActivate() {
        NovaChatUtils.sendToggleMsg("RespawnHome", true);
        timer = -1;
    }

    @Override
    public void onDeactivate() {
        NovaChatUtils.sendToggleMsg("RespawnHome", false);
    }

    @EventHandler
    private void onOpenScreen(OpenScreenEvent event) {
        if (event.screen instanceof DeathScreen) {
            if (mc.player != null) {
                mc.player.requestRespawn();
                event.cancel();
                timer = delay.get();
            }
        }
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (timer >= 0) {
            timer--;
            if (timer == 0) {
                ChatUtils.sendPlayerMsg("/home " + homeName.get());
                timer = -1;
            }
        }
    }
}
