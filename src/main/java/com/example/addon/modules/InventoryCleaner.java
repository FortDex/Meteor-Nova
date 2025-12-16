package com.example.addon.modules;

import com.example.addon.Addon;
import com.example.addon.utils.NovaChatUtils;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.ItemListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import java.util.List;

public class InventoryCleaner extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<List<Item>> items = sgGeneral.add(new ItemListSetting.Builder()
            .name("items")
            .description("Items to throw away.")
            .defaultValue(Items.DIRT, Items.COBBLESTONE, Items.ANDESITE, Items.DIORITE, Items.GRANITE)
            .build());

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
            .name("delay")
            .description("Delay in ticks between throwing items.")
            .defaultValue(2)
            .min(0)
            .build());

    private int timer = 0;

    public InventoryCleaner() {
        super(Addon.CATEGORY, "inventory-cleaner", "Automatically throws away specified items.");
    }

    @Override
    public void onActivate() {
        NovaChatUtils.sendToggleMsg("InventoryCleaner", true);
        timer = 0;
    }

    @Override
    public void onDeactivate() {
        NovaChatUtils.sendToggleMsg("InventoryCleaner", false);
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (timer > 0) {
            timer--;
            return;
        }

        // インベントリのメインスロット（9〜35）とホットバー（0〜8）を走査
        // ※ InvUtilsの実装依存ですが、通常ループで回します

        for (int i = 9; i < 36; i++) { // メインインベントリ優先
            if (checkAndDrop(i))
                return;
        }
        for (int i = 0; i < 9; i++) { // ホットバー
            if (checkAndDrop(i))
                return;
        }
    }

    private boolean checkAndDrop(int slot) {
        if (mc.player == null)
            return false;

        Item item = mc.player.getInventory().getStack(slot).getItem();
        if (item != Items.AIR && items.get().contains(item)) {
            InvUtils.drop().slot(slot);
            timer = delay.get();
            return true;
        }
        return false;
    }
}
