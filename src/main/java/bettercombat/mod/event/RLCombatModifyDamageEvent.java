package bettercombat.mod.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class RLCombatModifyDamageEvent extends PlayerEvent {

    private final Entity targetEntity;
    private final float baseDamage;
    private final boolean offhand;
    private final ItemStack stack;
    private final float cooledStrength;
    private float damageModifier;

    private final double motionX;
    private final double motionY;
    private final double motionZ;

    /**
     * Modify attack damage (Pre or Post) cooldown multiplier and critical multiplier is added to it
     */
    private RLCombatModifyDamageEvent(EntityPlayer player, Entity targetEntity, boolean offhand, ItemStack stack, float damage, float cooledStrength, double motionX, double motionY, double motionZ) {
        super(player);
        this.targetEntity = targetEntity;
        this.offhand = offhand;
        this.baseDamage = damage;
        this.stack = stack;
        this.cooledStrength = cooledStrength;
        this.damageModifier = 0;
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
    }

    public static class Pre extends RLCombatModifyDamageEvent {

        /**
         * Modify attack damage before cooldown multiplier and critical multiplier is added to it
         */
        public Pre(EntityPlayer player, Entity targetEntity, boolean offhand, ItemStack stack, float damage, float cooledStrength, double motionX, double motionY, double motionZ) {
            super(player, targetEntity, offhand, stack, damage, cooledStrength, motionX, motionY, motionZ);
        }
    }

    public static class Post extends RLCombatModifyDamageEvent {

        private DamageSource dmgSource;

        /**
         * Modify attack damage after cooldown multiplier and critical multiplier is added to it
         * Allow changing the damage source as attacking will happen at this point
         */
        public Post(EntityPlayer player, Entity targetEntity, boolean offhand, ItemStack stack, float damage, float cooledStrength, double motionX, double motionY, double motionZ, DamageSource dmgSource) {
            super(player, targetEntity, offhand, stack, damage, cooledStrength, motionX, motionY, motionZ);
            this.dmgSource = dmgSource;
        }

        public void setDamageSource(DamageSource dmgSource) {
            this.dmgSource = dmgSource;
        }

        public DamageSource getDamageSource() {
            return this.dmgSource;
        }
    }

    public Entity getTarget() { return this.targetEntity; }
    public float getBaseDamage() { return this.baseDamage; }
    public boolean getOffhand() { return this.offhand; }
    public ItemStack getStack() { return this.stack; }
    public float getCooledStrength() { return this.cooledStrength; }
    public float getDamageModifier() { return this.damageModifier; }
    public void setDamageModifier(float modifier) { this.damageModifier = modifier; }

    public double getMotionX() { return this.motionX; }
    public double getMotionY() { return this.motionY; }
    public double getMotionZ() { return this.motionZ; }
}