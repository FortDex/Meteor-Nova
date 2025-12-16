package com.example.addon.modules;

import com.example.addon.Addon;
import com.example.addon.utils.NovaChatUtils;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
import meteordevelopment.meteorclient.events.entity.EntityRemovedEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PopCounter extends Module {
    private final Map<UUID, Integer> pops = new HashMap<>();

    public PopCounter() {
        super(Addon.CATEGORY, "pop-counter", "Counts totem pops of players.");
    }

    @Override
    public void onActivate() {
        NovaChatUtils.sendToggleMsg("PopCounter", true);
        pops.clear();
    }

    @Override
    public void onDeactivate() {
        NovaChatUtils.sendToggleMsg("PopCounter", false);
    }

    @EventHandler
    private void onPacketReceive(PacketEvent.Receive event) {
        if (event.packet instanceof EntityStatusS2CPacket packet) {
            if (packet.getStatus() == 35) { // Totem pop status
                Entity entity = packet.getEntity(mc.world);

                if (entity instanceof PlayerEntity player && entity != mc.player) {
                    UUID id = player.getUuid();
                    int count = pops.getOrDefault(id, 0) + 1;
                    pops.put(id, count);

                    NovaChatUtils.sendInfoMsg("PopCounter",
                            Formatting.GOLD + player.getName().getString() + Formatting.GRAY + " popped "
                                    + Formatting.RED + count + Formatting.GRAY + " totem(s)!");
                }
            }
        }
    }

    @EventHandler
    private void onEntityRemoved(EntityRemovedEvent event) {
        if (event.entity instanceof PlayerEntity player) {
            UUID id = player.getUuid();
            if (pops.containsKey(id)) {
                int count = pops.get(id);
                // 死亡判定は厳密には難しい（ログアウトかデスポーンか区別が怪しい）が、
                // 基本的に戦闘中に消える＝死亡orログアウトなので通知する
                NovaChatUtils.sendInfoMsg("PopCounter", Formatting.RED + player.getName().getString() + Formatting.GRAY
                        + " died (or logged) after popping " + Formatting.GOLD + count + Formatting.GRAY + " totems!");
                pops.remove(id);
            }
        }
    }
}
