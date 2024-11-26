package bettercombat.mod.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.CriticalHitEvent;

public class RLCombatCriticalHitEvent extends CriticalHitEvent {

    private final boolean offhand;
    private final float cooledStrength;

    /**
     * Extend CriticalHitEvent and add offhand boolean and cooledstrength float for compatibility with things such as enchantments
     */
    public RLCombatCriticalHitEvent(EntityPlayer player, Entity target, float damageModifier, boolean vanillaCritical, boolean offhand, float cooledStrength) {
        super(player, target, damageModifier, vanillaCritical);
        this.offhand = offhand;
        this.cooledStrength = cooledStrength;
    }

    public boolean getOffhand() {
        return this.offhand;
    }
    
    public float getCooledStrength() {
        return this.cooledStrength;
    }
}