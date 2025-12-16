package com.example.addon.modules;

import com.example.addon.Addon;
import com.example.addon.utils.NovaChatUtils;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import net.minecraft.text.Text;

import java.util.List;

public class AutoAccept extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public enum Mode {
        Friends,
        Whitelist,
        Blacklist,
        All
    }

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
            .name("mode")
            .description("Which players to accept requests from.")
            .defaultValue(Mode.Friends)
            .build());

    private final Setting<List<String>> whitelist = sgGeneral.add(new StringListSetting.Builder()
            .name("whitelist")
            .description("List of players to accept.")
            .visible(() -> mode.get() == Mode.Whitelist)
            .build());

    private final Setting<List<String>> blacklist = sgGeneral.add(new StringListSetting.Builder()
            .name("blacklist")
            .description("List of players to ignore.")
            .visible(() -> mode.get() == Mode.Blacklist)
            .build());

    public AutoAccept() {
        super(Addon.CATEGORY, "auto-accept", "Automatically accepts teleport requests.");
    }

    @Override
    public void onActivate() {
        NovaChatUtils.sendToggleMsg("AutoAccept", true);
    }

    @Override
    public void onDeactivate() {
        NovaChatUtils.sendToggleMsg("AutoAccept", false);
    }

    @EventHandler
    private void onMessageReceive(ReceiveMessageEvent event) {
        Text message = event.getMessage();
        String text = message.getString();

        if (text.contains("has requested to teleport") || text.contains("to teleport to you")
                || text.contains("has invited you to join them")) {
            String playerName = extractPlayerName(text);

            if (playerName != null) {
                if (shouldAccept(playerName)) {
                    info("Accepting teleport request from " + playerName);
                    ChatUtils.sendPlayerMsg("/tpy " + playerName);
                }
            }
        }
    }

    private boolean shouldAccept(String playerName) {
        switch (mode.get()) {
            case Friends:
                for (meteordevelopment.meteorclient.systems.friends.Friend friend : Friends.get()) {
                    if (friend.name.equalsIgnoreCase(playerName))
                        return true;
                }
                return false;
            case Whitelist:
                for (String name : whitelist.get()) {
                    if (name.equalsIgnoreCase(playerName))
                        return true;
                }
                return false;
            case Blacklist:
                for (String name : blacklist.get()) {
                    if (name.equalsIgnoreCase(playerName))
                        return false;
                }
                return true;
            case All:
                return true;
            default:
                return false;
        }
    }

    private String extractPlayerName(String text) {
        String[] parts = text.split(" ");
        if (parts.length > 0) {
            return parts[0];
        }
        return null;
    }
}
