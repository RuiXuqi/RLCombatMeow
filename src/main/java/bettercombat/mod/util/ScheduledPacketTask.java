package bettercombat.mod.util;

import bettercombat.mod.capability.CapabilityOffhandCooldown;
import bettercombat.mod.handler.EventHandlers;
import net.minecraft.entity.player.EntityPlayer;

public class ScheduledPacketTask implements Runnable {

    private final EntityPlayer player;
    
    public ScheduledPacketTask(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public void run() {
        if(this.player == null) return;
        Helpers.execNullable(this.player.getCapability(EventHandlers.OFFHAND_COOLDOWN, null), CapabilityOffhandCooldown::resetTicksSinceLastSwing);
    }
}