package bettercombat.mod.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class RLCombatModifyDamageEvent extends PlayerEvent {

    private final Entity targetEntity;
    private final float baseDamage;
    private final boolean offhand;
    private final ItemStack stack;
    private float damageModifier;

    /**
     * Modify attack damage (Pre or Post) cooldown multiplier and critical multiplier is added to it
     */
    private RLCombatModifyDamageEvent(EntityPlayer player, Entity targetEntity, boolean offhand, ItemStack stack, float damage) {
        super(player);
        this.targetEntity = targetEntity;
        this.offhand = offhand;
        this.baseDamage = damage;
        this.stack = stack;
        this.damageModifier = 0;
    }

    public static class Pre extends RLCombatModifyDamageEvent {
        /**
         * Modify attack damage before cooldown multiplier and critical multiplier is added to it
         */
        public Pre(EntityPlayer player, Entity targetEntity, boolean offhand, ItemStack stack, float damage) {
            super(player, targetEntity, offhand, stack, damage);
        }
    }

    public static class Post extends RLCombatModifyDamageEvent {
        /**
         * Modify attack damage after cooldown multiplier and critical multiplier is added to it
         */
        public Post(EntityPlayer player, Entity targetEntity, boolean offhand, ItemStack stack, float damage) {
            super(player, targetEntity, offhand, stack, damage);
        }
    }

    public Entity getTarget() { return this.targetEntity; }
    public float getBaseDamage() { return this.baseDamage; }
    public boolean getOffhand() { return this.offhand; }
    public ItemStack getStack() { return this.stack; }
    public float getDamageModifier() { return this.damageModifier; }
    public void setDamageModifier(float modifier) { this.damageModifier = modifier; }
}