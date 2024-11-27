/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package bettercombat.mod.util;

import bettercombat.mod.compat.*;
import bettercombat.mod.event.RLCombatAttackEntityEvent;
import bettercombat.mod.event.RLCombatCriticalHitEvent;
import bettercombat.mod.event.RLCombatModifyDamageEvent;
import bettercombat.mod.event.RLCombatSweepEvent;
import bettercombat.mod.handler.EventHandlers;
import bettercombat.mod.capability.CapabilityOffhandCooldown;
import bettercombat.mod.handler.SoundHandler;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import meldexun.reachfix.util.ReachFixUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public final class Helpers {

    public static <T> void execNullable(@Nullable T obj, Consumer<T> onNonNull) {
        if(obj != null) onNonNull.accept(obj);
    }

    public static <T, R> R execNullable(@Nullable T obj, Function<T, R> onNonNull, R orElse) {
        if(obj != null) return onNonNull.apply(obj);
        return orElse;
    }

    public static void clearOldModifiers(EntityLivingBase entity, ItemStack stack, boolean damage, boolean speed, boolean reach) {
        if(!stack.isEmpty() && entity != null) {
            Multimap<String, AttributeModifier> modifiersToRemove = HashMultimap.create();
            for(Map.Entry<String, AttributeModifier> modifier : stack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND).entries()) {
                if((damage && modifier.getKey().equals(SharedMonsterAttributes.ATTACK_DAMAGE.getName())) ||
                        (speed && modifier.getKey().equals(SharedMonsterAttributes.ATTACK_SPEED.getName())) ||
                        (reach && modifier.getKey().equals(EntityPlayer.REACH_DISTANCE.getName()))) {
                    modifiersToRemove.put(modifier.getKey(), modifier.getValue());
                }
            }
            if(ModLoadedUtil.isQualityToolsLoaded()) QualityToolsHandler.clearOldModifiersQualityTools(entity, stack, modifiersToRemove, damage, speed, reach);
            if(!modifiersToRemove.isEmpty()) entity.getAttributeMap().removeAttributeModifiers(modifiersToRemove);
        }
    }

    public static void addNewModifiers(EntityLivingBase entity, ItemStack stack, boolean damage, boolean speed, boolean reach) {
        if(!stack.isEmpty() && entity != null) {
            for(Map.Entry<String, AttributeModifier> modifier : stack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND).entries()) {
                if(damage && modifier.getKey().equals(SharedMonsterAttributes.ATTACK_DAMAGE.getName())) {
                    IAttributeInstance entityAttribute = entity.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE);
                    if(!entityAttribute.hasModifier(modifier.getValue())) entityAttribute.applyModifier(modifier.getValue());
                }
                else if(speed && modifier.getKey().equals(SharedMonsterAttributes.ATTACK_SPEED.getName())) {
                    IAttributeInstance entityAttribute = entity.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_SPEED);
                    if(!entityAttribute.hasModifier(modifier.getValue())) entityAttribute.applyModifier(modifier.getValue());
                }
                else if(reach && modifier.getKey().equals(EntityPlayer.REACH_DISTANCE.getName())) {
                    IAttributeInstance entityAttribute = entity.getAttributeMap().getAttributeInstance(EntityPlayer.REACH_DISTANCE);
                    if(!entityAttribute.hasModifier(modifier.getValue())) entityAttribute.applyModifier(modifier.getValue());
                }
            }
            if(ModLoadedUtil.isQualityToolsLoaded()) QualityToolsHandler.addNewModifiersQualityTools(entity, stack, damage, speed, reach);
        }
    }

    public static float getOffhandDamage(EntityPlayer player) {
        float attack = (float)player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        return attack * (ConfigurationHandler.server.weakerOffhand ? ConfigurationHandler.server.offhandEfficiency : 1.0F);
    }

    public static boolean isHandActive(EntityPlayer player, EnumHand hand) {
        return player.isHandActive() && player.getItemInUseCount() > 0 && player.getActiveHand().equals(hand);
    }

    public static void attackTargetEntityItem(EntityPlayer player, Entity targetEntity, boolean offhand, double motionX, double motionY, double motionZ) {
        ItemStack weapon = offhand ? player.getHeldItemOffhand() : player.getHeldItemMainhand();
        boolean attackEntityEventCancel = MinecraftForge.EVENT_BUS.post(new RLCombatAttackEntityEvent(player, targetEntity, offhand));
        if(attackEntityEventCancel) return;
        if(!weapon.isEmpty() && weapon.getItem().onLeftClickEntity(weapon, player, targetEntity)) return;

        //Check for a Reskillable lock ourselves, since reskillable normally only blocks it *after* this handling is ran
        if(ModLoadedUtil.isReskillableLoaded() && ReskillableHandler.shouldLockAttack(player, weapon)) return;

        if(targetEntity.canBeAttackedWithItem()) {
            if(!targetEntity.hitByEntity(player)) {
                float damage;
                float cooldown = 0.0F;
                double reach;

                if(offhand) {
                    clearOldModifiers(player, player.getHeldItemMainhand(), true, true, true);
                    addNewModifiers(player, player.getHeldItemOffhand(), true, true, true);

                    damage = getOffhandDamage(player);
                    cooldown = player.getCooldownPeriod();
                    reach = ReachFixUtil.getEntityReach(player, EnumHand.OFF_HAND);

                    clearOldModifiers(player, player.getHeldItemOffhand(), true, true, true);
                    addNewModifiers(player, player.getHeldItemMainhand(), true, true, true);
                }
                else {
                    damage = (float)player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
                    reach = ReachFixUtil.getEntityReach(player, EnumHand.MAIN_HAND);
                }

                float cMod;
                if(targetEntity instanceof EntityLivingBase) {
                    cMod = EnchantmentHelper.getModifierForCreature(weapon, ((EntityLivingBase) targetEntity).getCreatureAttribute());
                }
                else {
                    cMod = EnchantmentHelper.getModifierForCreature(weapon, EnumCreatureAttribute.UNDEFINED);
                }

                float cooledStr = 0.0F;
                if(offhand) {
                    CapabilityOffhandCooldown coh = player.getCapability(EventHandlers.OFFHAND_COOLDOWN, null);
                    if(coh != null) cooledStr = MathHelper.clamp(((float)coh.getTicksSinceLastSwing() + 0.5F) / cooldown, 0.0F, 1.0F);
                }
                else {
                    cooledStr = player.getCooledAttackStrength(0.5F);
                }
                boolean cancelCooldown = ConfigurationHandler.server.requireFullEnergy && cooledStr < 0.95F;

                //Post event to get any other modifiers before multiplying by cooldown and adding enchantment damage
                RLCombatModifyDamageEvent modifyResultPre = new RLCombatModifyDamageEvent.Pre(player, targetEntity, offhand, weapon, damage, cooledStr, motionX, motionY, motionZ);
                boolean cancelPre = MinecraftForge.EVENT_BUS.post(modifyResultPre);
                damage += modifyResultPre.getDamageModifier();

                damage *= (0.2F + cooledStr * cooledStr * 0.8F);
                cMod *= cooledStr;

                if(offhand) {
                    CapabilityOffhandCooldown coh = player.getCapability(EventHandlers.OFFHAND_COOLDOWN, null);
                    if(coh != null) {
                        coh.resetTicksSinceLastSwing();
                        if(!player.world.isRemote) coh.sync();//Sync once here, instead of every tick that there is a cooldown in livingupdate, hopefully works fine?
                    }
                }
                else {
                    player.resetCooldown();
                }
                
                //Cancel after cooldowns for consistency, as mainhand cooldown will still get reset from arm swinging handling
                if(cancelCooldown || cancelPre) return;

                if(damage > 0.0F || cMod > 0.0F) {
                    boolean isStrong = cooledStr > 0.9F;
                    boolean knockback = false;
                    boolean isCrit;
                    
                    EnchantCompatHandler.knockbackFromOffhand = offhand;
                    EnchantCompatHandler.knockbackCooledStrength = cooledStr;
                    int knockbackMod = EnchantmentHelper.getKnockbackModifier(player);
                    EnchantCompatHandler.knockbackFromOffhand = false;
                    EnchantCompatHandler.knockbackCooledStrength = 1.0F;
                    
                    EnchantCompatHandler.fireAspectFromOffhand = offhand;
                    EnchantCompatHandler.fireAspectCooledStrength = cooledStr;
                    int fireAspect = EnchantmentHelper.getFireAspectModifier(player);
                    EnchantCompatHandler.fireAspectFromOffhand = false;
                    EnchantCompatHandler.fireAspectCooledStrength = 1.0F;

                    if(player.isSprinting() && isStrong) {
                        player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, player.getSoundCategory(), 1.0F, 1.0F);
                        knockbackMod++;
                        knockback = true;
                    }

                    if(ConfigurationHandler.server.randomCrits) {
                        isCrit = player.getRNG().nextFloat() < ConfigurationHandler.server.critChance && !player.isSprinting() && (!ConfigurationHandler.server.requireEnergyToRandomCrit || isStrong);
                        //Allow forced jump crits at close range
                        if(!isCrit) isCrit = (!ConfigurationHandler.server.requireEnergyToJumpCrit || isStrong) && player.getDistance(targetEntity) < ConfigurationHandler.server.distanceToJumpCrit && player.fallDistance > 0.0F && !player.onGround && !player.isOnLadder() &&
                                !player.isInWater() && !player.isPotionActive(MobEffects.BLINDNESS) && !player.isRiding() &&
                                targetEntity instanceof EntityLivingBase && !player.isSprinting();
                    }
                    else {
                        isCrit = isStrong && player.fallDistance > 0.0F && !player.onGround && !player.isOnLadder() && !player.isInWater()
                                         && !player.isPotionActive(MobEffects.BLINDNESS) && !player.isRiding() && targetEntity instanceof EntityLivingBase
                                         && !player.isSprinting();
                    }

                    RLCombatCriticalHitEvent hitResult = new RLCombatCriticalHitEvent(player, targetEntity, isCrit ? 1.5F : 1.0F, isCrit, offhand, cooledStr);
                    MinecraftForge.EVENT_BUS.post(hitResult);
                    if(!(hitResult.getResult() == Event.Result.ALLOW || (isCrit && hitResult.getResult() == Event.Result.DEFAULT))) hitResult = null;

                    isCrit = hitResult != null;
                    if(isCrit) {
                        damage *= hitResult.getDamageModifier();
                    }

                    damage += cMod;

                    //Post event to get any other modifiers after multiply by cooldown and crit required for compat
                    RLCombatModifyDamageEvent.Post modifyResultPost = new RLCombatModifyDamageEvent.Post(player, targetEntity, offhand, weapon, damage, cooledStr, motionX, motionY, motionZ, DamageSource.causePlayerDamage(player));
                    boolean cancelPost = MinecraftForge.EVENT_BUS.post(modifyResultPost);
                    damage += modifyResultPost.getDamageModifier();
                    DamageSource dmgSource = modifyResultPost.getDamageSource();//Allow for changing the damage source to custom for compat with mods like SpartanWeaponry

                    if(cancelPost) return;
                    
                    boolean doSweepingIgnoreSword = false;
                    boolean doSweeping = false;
                    double tgtDistDelta = player.distanceWalkedModified - player.prevDistanceWalkedModified;
                    if(isStrong && !isCrit && !knockback && player.onGround && tgtDistDelta < player.getAIMoveSpeed()) {
                        doSweepingIgnoreSword = true;
                        if(weapon.getItem() instanceof ItemSword) {
                            doSweeping = true;
                        }
                    }

                    float tgtHealth = 0.0F;
                    boolean burnInflicted = false;
                    if(targetEntity instanceof EntityLivingBase) {
                        tgtHealth = ((EntityLivingBase)targetEntity).getHealth();
                        if(fireAspect > 0 && !targetEntity.isBurning()) {
                            targetEntity.setFire(1);
                            burnInflicted = true;
                        }
                    }

                    double tgtMotionX = targetEntity.motionX;
                    double tgtMotionY = targetEntity.motionY;
                    double tgtMotionZ = targetEntity.motionZ;
                    boolean attacked;
                    
                    EnchantCompatHandler.attackEntityFromCooledStrength = cooledStr;
                    if(offhand) {
                        Entity targetEntCap = targetEntity;
                        if(targetEntCap instanceof MultiPartEntityPart) targetEntCap = (Entity)(((MultiPartEntityPart)targetEntCap).parent);
                        final float attackDmgFinal = damage;
                        attacked = execNullable(targetEntCap.getCapability(EventHandlers.OFFHAND_HURTRESISTANCE, null),
                                                sht -> sht.attackEntityFromOffhand(targetEntity, dmgSource, attackDmgFinal), false);
                    }
                    else {
                        attacked = targetEntity.attackEntityFrom(dmgSource, damage);
                        
                        if(attacked && ModLoadedUtil.isSpartanWeaponryLoaded()) {
                            SpartanWeaponryHandler.handleSpartanQuickStrike(weapon, targetEntity);
                        }
                    }
                    EnchantCompatHandler.attackEntityFromCooledStrength = 1.0F;
                    
                    if(attacked) {
                        if(knockbackMod > 0) {
                            if(targetEntity instanceof EntityLivingBase) {
                                ((EntityLivingBase)targetEntity).knockBack(player, knockbackMod * 0.5F, MathHelper.sin(player.rotationYaw * 0.017453292F), -MathHelper.cos(player.rotationYaw * 0.017453292F));
                            }
                            else {
                                targetEntity.addVelocity(-MathHelper.sin(player.rotationYaw * 0.017453292F) * knockbackMod * 0.5F, 0.1D, MathHelper.cos(player.rotationYaw * 0.017453292F) * knockbackMod * 0.5F);
                            }
                            player.motionX *= 0.6D;
                            player.motionZ *= 0.6D;
                            if(!ConfigurationHandler.server.dontInterruptSprint) {
                                player.setSprinting(false);
                            }
                        }
                        
                        EnchantCompatHandler.sweepingFromOffhand = offhand;
                        EnchantCompatHandler.sweepingCooledStrength = cooledStr;
                        float sweepingRatio = EnchantmentHelper.getSweepingDamageRatio(player);
                        EnchantCompatHandler.sweepingFromOffhand = false;
                        EnchantCompatHandler.sweepingCooledStrength = 1.0F;
                        
                        RLCombatSweepEvent sweepResult = new RLCombatSweepEvent(player, targetEntity, damage, offhand, weapon, doSweepingIgnoreSword, doSweeping, sweepingRatio, targetEntity.getEntityBoundingBox().grow(1.0D, 0.25D, 1.0D), DamageSource.causePlayerDamage(player), cooledStr);
                        MinecraftForge.EVENT_BUS.post(sweepResult);
                        doSweeping = sweepResult.getDoSweep();

                        if(doSweeping) {
                            float sweepingDamage = 1.0F + (sweepResult.getSweepModifier() * damage);
                            AxisAlignedBB sweepingAABB = sweepResult.getSweepingAABB();
                            DamageSource sweepingDamageSource = sweepResult.getSweepingDamageSource();
                            
                            for(EntityLivingBase living : player.world.getEntitiesWithinAABB(EntityLivingBase.class, sweepingAABB)) {
                                EnchantCompatHandler.attackEntityFromCooledStrength = cooledStr;
                                if(living != player && living != targetEntity && !player.isOnSameTeam(living) && player.getDistanceSq(living) < (reach * reach)) {
                                    living.knockBack(player, 0.4F, MathHelper.sin(player.rotationYaw * 0.017453292F), -MathHelper.cos(player.rotationYaw * 0.017453292F));
                                    if(offhand) {
                                        execNullable(living.getCapability(EventHandlers.OFFHAND_HURTRESISTANCE, null),
                                                     sht -> sht.attackEntityFromOffhand(living, sweepingDamageSource, sweepingDamage));
                                    }
                                    else {
                                        boolean att = living.attackEntityFrom(sweepingDamageSource, sweepingDamage);
                                        
                                        if(att && ModLoadedUtil.isSpartanWeaponryLoaded()) {
                                            SpartanWeaponryHandler.handleSpartanQuickStrike(weapon, targetEntity);
                                        }
                                    }
                                }
                                EnchantCompatHandler.attackEntityFromCooledStrength = 1.0F;
                            }
                            player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1.0F, 1.0F);
                            player.spawnSweepParticles();
                        }

                        if(targetEntity instanceof EntityPlayerMP && targetEntity.velocityChanged) {
                            ((EntityPlayerMP)targetEntity).connection.sendPacket(new SPacketEntityVelocity(targetEntity));
                            targetEntity.velocityChanged = false;
                            targetEntity.motionX = tgtMotionX;
                            targetEntity.motionY = tgtMotionY;
                            targetEntity.motionZ = tgtMotionZ;
                        }

                        if(isCrit) {
                            if(weapon.isEmpty() || !ConfigurationHandler.server.additionalCritSound) {
                                player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, player.getSoundCategory(), 1.0F, 1.0F);
                            }
                            player.onCriticalHit(targetEntity);
                        }

                        if(!weapon.isEmpty()) {
                            if(ConfigurationHandler.server.additionalHitSound && (!ConfigurationHandler.server.additionalCritSound || !isCrit)) {
                                player.world.playSound(null, player.posX, player.posY, player.posZ, SoundHandler.SWORD_SLASH, player.getSoundCategory(), 1.0F, 1.0F);
                            }
                            if(ConfigurationHandler.server.additionalCritSound && isCrit) {
                                player.world.playSound(null, player.posX, player.posY, player.posZ, SoundHandler.CRITICAL_STRIKE, player.getSoundCategory(), 1.0F, 1.0F);
                            }
                        }

                        if(!isCrit && !doSweeping) {
                            if(isStrong) {
                                player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, player.getSoundCategory(), 1.0F, 1.0F);
                            }
                            else {
                                player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, player.getSoundCategory(), 1.0F, 1.0F);
                            }
                        }

                        if(cMod > 0.0F) {
                            player.onEnchantmentCritical(targetEntity);
                        }

                        player.setLastAttackedEntity(targetEntity);

                        if(targetEntity instanceof EntityLivingBase) {
                            EnchantCompatHandler.thornsFromOffhand = offhand;
                            EnchantCompatHandler.thornsCooledStrength = cooledStr;
                            EnchantmentHelper.applyThornEnchantments((EntityLivingBase)targetEntity, player);
                            EnchantCompatHandler.thornsFromOffhand = false;
                            EnchantCompatHandler.thornsCooledStrength = 1.0F;
                        }
                        
                        EnchantCompatHandler.arthropodFromOffhand = offhand;
                        EnchantCompatHandler.arthropodCooledStrength = cooledStr;
                        EnchantmentHelper.applyArthropodEnchantments(player, targetEntity);
                        EnchantCompatHandler.arthropodFromOffhand = false;
                        EnchantCompatHandler.arthropodCooledStrength = 1.0F;

                        Entity entity = targetEntity;

                        if(targetEntity instanceof MultiPartEntityPart) {
                            IEntityMultiPart ientitymultipart = ((MultiPartEntityPart)targetEntity).parent;
                            if(ientitymultipart instanceof EntityLivingBase) {
                                entity = (EntityLivingBase)ientitymultipart;
                            }
                        }

                        if(!weapon.isEmpty() && entity instanceof EntityLivingBase) {
                            ItemStack beforeHitCopy = weapon.copy();
                            weapon.hitEntity((EntityLivingBase)entity, player);
                            if(weapon.isEmpty()) {
                                player.setHeldItem(offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, ItemStack.EMPTY);
                                ForgeEventFactory.onPlayerDestroyItem(player, beforeHitCopy, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
                            }
                        }

                        if(targetEntity instanceof EntityLivingBase) {
                            float healthDelta = tgtHealth - ((EntityLivingBase)targetEntity).getHealth();
                            player.addStat(StatList.DAMAGE_DEALT, Math.round(healthDelta * 10.0F));

                            if(fireAspect > 0) {
                                targetEntity.setFire(fireAspect * 4);
                            }

                            if(player.world instanceof WorldServer && healthDelta > 2.0F) {
                                int k = (int)(healthDelta * 0.5D);
                                ((WorldServer)player.world).spawnParticle(EnumParticleTypes.DAMAGE_INDICATOR, targetEntity.posX, targetEntity.posY + targetEntity.height * 0.5F, targetEntity.posZ, k, 0.1D, 0.0D, 0.1D, 0.2D);
                            }
                        }

                        player.addExhaustion(0.3F);
                    }
                    else {
                        player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, player.getSoundCategory(), 1.0F, 1.0F);

                        if(burnInflicted) {
                            targetEntity.extinguish();
                        }
                    }
                }
            }
        }
    }

    /**
     * This returns a null value for those final variables that have their values injected during runtime.
     * Prevents IDEs from warning the user of potential NullPointerExceptions on code using those variables.
     * @param <T> any type
     * @return null
     */
    @Nonnull
    @SuppressWarnings("ConstantConditions")
    public static <T> T getNull() {
        return null;
    }
}