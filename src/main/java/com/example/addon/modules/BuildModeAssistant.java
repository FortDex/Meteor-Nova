package com.example.addon.modules;

import com.example.addon.Addon;
import com.example.addon.utils.NovaChatUtils;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;

import java.util.List;

public class BuildModeAssistant extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<String> command = sgGeneral.add(new StringSetting.Builder()
            .name("command")
            .description("Command to run when reaching the target.")
            .defaultValue("/buildermode home base")
            .build());

    private final Setting<Integer> x = sgGeneral.add(new IntSetting.Builder()
            .name("x")
            .description("Target X coordinate.")
            .defaultValue(0)
            .build());

    private final Setting<Integer> z = sgGeneral.add(new IntSetting.Builder()
            .name("z")
            .description("Target Z coordinate.")
            .defaultValue(0)
            .build());

    private final Setting<Integer> radius = sgGeneral.add(new IntSetting.Builder()
            .name("radius")
            .description("Radius to trigger the command.")
            .defaultValue(10)
            .build());

    private boolean triggered = false;

    public BuildModeAssistant() {
        super(Addon.CATEGORY, "build-mode-assistant", "Automatically runs a command when reaching a location.");
    }

    @Override
    public void onActivate() {
        NovaChatUtils.sendToggleMsg("BuildModeAssistant", true);
        triggered = false;
    }

    @Override
    public void onDeactivate() {
        NovaChatUtils.sendToggleMsg("BuildModeAssistant", false);
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player == null)
            return;

        double dist = Math.sqrt(Math.pow(mc.player.getX() - x.get(), 2) + Math.pow(mc.player.getZ() - z.get(), 2));

        if (dist <= radius.get()) {
            if (!triggered) {
                ChatUtils.sendPlayerMsg(command.get());
                info("Reached target! Executed command.");
                triggered = true;
            }
        } else {
            // 範囲外に出たらリセット（再入場時にまた実行するため）
            // 必要なければこのelseブロックを削除で「1回のみ実行」になります
            triggered = false;
        }
    }
}
