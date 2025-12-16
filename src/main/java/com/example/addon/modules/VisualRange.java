package com.example.addon.modules;

import com.example.addon.Addon;
import com.example.addon.utils.NovaChatUtils;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;

public class VisualRange extends Module {
    public VisualRange() {
        super(Addon.CATEGORY, "visual-range", "Notifies when a player enters visual range.");
    }

    @Override
    public void onActivate() {
        NovaChatUtils.sendToggleMsg("VisualRange", true);
    }

    @Override
    public void onDeactivate() {
        NovaChatUtils.sendToggleMsg("VisualRange", false);
    }

    @EventHandler
    private void onEntityAdded(EntityAddedEvent event) {
        if (event.entity instanceof PlayerEntity player && player != mc.player) {
            // フレンドは除外
            if (!Friends.get().isFriend(player)) {
                ChatUtils.info(Formatting.RED + "Visual Range Alert: " + Formatting.RESET + player.getName().getString()
                        + " detected!");

                // 必要なら音を鳴らすことも可能
                // mc.player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            }
        }
    }
}
