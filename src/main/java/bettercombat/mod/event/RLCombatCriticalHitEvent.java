package bettercombat.mod.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.CriticalHitEvent;

public class RLCombatCriticalHitEvent extends CriticalHitEvent {

    private final boolean offhand;

    /**
     * Extend CriticalHitEvent and add offhand boolean for compatibility with things such as enchantments
     */
    public RLCombatCriticalHitEvent(EntityPlayer player, Entity target, float damageModifier, boolean vanillaCritical, boolean offhand) {
        super(player, target, damageModifier, vanillaCritical);
        this.offhand = offhand;
    }

    public boolean getOffhand() {
        return this.offhand;
    }
}