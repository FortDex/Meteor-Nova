package com.example.addon.modules;

import com.example.addon.Addon;
import com.example.addon.utils.NovaChatUtils;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;
import net.minecraft.entity.EquipmentSlot;

public class ArmorNotify extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> threshold = sgGeneral.add(new IntSetting.Builder()
            .name("threshold")
            .description("Durability threshold in percent.")
            .defaultValue(20)
            .min(1)
            .max(100)
            .build());

    private boolean alerted = false;

    public ArmorNotify() {
        super(Addon.CATEGORY, "armor-notify", "Notifies you when armor durability is low.");
    }

    @Override
    public void onActivate() {
        NovaChatUtils.sendToggleMsg("ArmorNotify", true);
        alerted = false;
    }

    @Override
    public void onDeactivate() {
        NovaChatUtils.sendToggleMsg("ArmorNotify", false);
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player == null)
            return;

        boolean low = false;

        ItemStack[] armorWithNulls = new ItemStack[] {
                mc.player.getEquippedStack(EquipmentSlot.HEAD),
                mc.player.getEquippedStack(EquipmentSlot.CHEST),
                mc.player.getEquippedStack(EquipmentSlot.LEGS),
                mc.player.getEquippedStack(EquipmentSlot.FEET)
        };
        for (ItemStack stack : armorWithNulls) {
            if (stack.isEmpty())
                continue;

            if (stack.isDamageable()) {
                double percent = ((double) (stack.getMaxDamage() - stack.getDamage()) / stack.getMaxDamage()) * 100;

                if (percent <= threshold.get()) {
                    low = true;
                    break;
                }
            }
        }

        if (low) {
            if (!alerted) {
                warning("Your armor is low! (< " + threshold.get() + "%)");
                alerted = true;
            }
        } else {
            alerted = false;
        }
    }
}
