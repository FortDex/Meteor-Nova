package com.example.addon.modules;

import com.example.addon.Addon;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;

public class ModuleExample extends Module {
    public ModuleExample() {
        super(Addon.CATEGORY, "example-module", "An example module that sends a message.");
    }

    @Override
    public void onActivate() {
        info("Example module activated!");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        // Code to run every tick
    }
}
