package bettercombat.mod.util;

import bettercombat.mod.handler.EventHandlers;
import bettercombat.mod.network.PacketOffhandCooldown;
import net.minecraft.entity.player.EntityPlayer;

public class ScheduledPacketTask implements Runnable {

    private final EntityPlayer player;
    private final PacketOffhandCooldown message;

    public ScheduledPacketTask(EntityPlayer player, PacketOffhandCooldown message) {
        this.player = player;
        this.message = message;
    }

    @Override
    public void run() {
        if(this.player == null) return;

        Helpers.execNullable(this.player.getCapability(EventHandlers.TUTO_CAP, null), stg -> stg.setOffhandCooldown(this.message.cooldown));
        Helpers.execNullable(this.player.getCapability(EventHandlers.TUTO_CAP, null), stg -> stg.setOffhandBeginningCooldown(this.message.cooldownBeginning));
    }
}