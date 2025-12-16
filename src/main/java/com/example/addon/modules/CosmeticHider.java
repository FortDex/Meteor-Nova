package com.example.addon.modules;

import com.example.addon.Addon;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;

import java.util.List;

public class CosmeticHider extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> hideArmorStands = sgGeneral.add(new BoolSetting.Builder()
            .name("hide-armor-stands")
            .description("Hide all armor stands (common for cosmetics).")
            .defaultValue(true)
            .build());

    private final Setting<List<String>> nameFilter = sgGeneral.add(new StringListSetting.Builder()
            .name("name-filter")
            .description("Hide entities containing these strings in their name.")
            .defaultValue(List.of("Cosmetic", "Balloon"))
            .build());

    public CosmeticHider() {
        super(Addon.CATEGORY, "cosmetic-hider", "Removes cosmetic entities to improve visibility.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.world == null)
            return;

        for (Entity entity : mc.world.getEntities()) {
            if (shouldRemove(entity)) {
                entity.remove(Entity.RemovalReason.DISCARDED);
            }
        }
    }

    private boolean shouldRemove(Entity entity) {
        if (entity instanceof ArmorStandEntity && hideArmorStands.get()) {
            return true;
        }

        if (entity.getCustomName() != null) {
            String name = entity.getCustomName().getString();
            for (String filter : nameFilter.get()) {
                if (name.contains(filter))
                    return true;
            }
        }

        return false;
    }
}
