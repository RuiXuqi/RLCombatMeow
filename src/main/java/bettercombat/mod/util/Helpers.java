/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package bettercombat.mod.util;

import bettercombat.mod.event.RLCombatCriticalHitEvent;
import bettercombat.mod.event.RLCombatModifyDamageEvent;
import bettercombat.mod.event.RLCombatSweepEvent;
import bettercombat.mod.handler.EventHandlers;
import bettercombat.mod.capability.CapabilityOffhandCooldown;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import meldexun.reachfix.util.ReachFixUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDamage;
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
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public final class Helpers
{
    private Helpers() {}

    public static <T> void execNullable(@Nullable T obj, Consumer<T> onNonNull) {
        if( obj != null ) {
            onNonNull.accept(obj);
        }
    }

    public static <T, R> R execNullable(@Nullable T obj, Function<T, R> onNonNull, R orElse) {
        if( obj != null ) {
            return onNonNull.apply(obj);
        }

        return orElse;
    }

    public static void clearOldModifiers(EntityLivingBase entity, ItemStack stack) {
        if(!stack.isEmpty() && entity != null) {
            Multimap<String, AttributeModifier> modifiersToRemove = HashMultimap.create();
            for(Map.Entry<String, AttributeModifier> modifier : stack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND).entries()) {
                if(modifier.getKey().contains("attackDamage") || modifier.getKey().contains("attackSpeed") || modifier.getKey().contains("reachDistance")) {
                    modifiersToRemove.put(modifier.getKey(), modifier.getValue());
                }
            }
            if(Loader.isModLoaded("qualitytools")) QualityToolsHandler.clearOldModifiersQualityTools(entity, stack, modifiersToRemove);
            if(!modifiersToRemove.isEmpty()) entity.getAttributeMap().removeAttributeModifiers(modifiersToRemove);
        }
    }

    public static void addNewModifiers(EntityLivingBase entity, ItemStack stack) {
        if(!stack.isEmpty() && entity != null) {
            for(Map.Entry<String, AttributeModifier> modifier : stack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND).entries()) {
                if(modifier.getKey().contains("attackDamage") ) {
                    IAttributeInstance entityAttribute = entity.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE);
                    if(!entityAttribute.hasModifier(modifier.getValue())) entityAttribute.applyModifier(modifier.getValue());
                }
                else if(modifier.getKey().contains("attackSpeed") ) {
                    IAttributeInstance entityAttribute = entity.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_SPEED);
                    if(!entityAttribute.hasModifier(modifier.getValue())) entityAttribute.applyModifier(modifier.getValue());
                }
                else if(modifier.getKey().contains("reachDistance") ) {
                    IAttributeInstance entityAttribute = entity.getAttributeMap().getAttributeInstance(EntityPlayer.REACH_DISTANCE);
                    if(!entityAttribute.hasModifier(modifier.getValue())) entityAttribute.applyModifier(modifier.getValue());
                }
            }
            if(Loader.isModLoaded("qualitytools")) QualityToolsHandler.addNewModifiersQualityTools(entity, stack);
        }
    }

    public static float getOffhandDamage(EntityPlayer player) {
        float attack = (float)player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        return attack * (ConfigurationHandler.weakerOffhand ? ConfigurationHandler.offHandEfficiency : 1.0F);
    }

    public static int getOffhandCooldown(EntityPlayer player) {
        float speed = (float)player.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).getAttributeValue();
        return (int) (((1.0F/speed)*20.0F)+0.5F);
    }

    public static int getOffhandFireAspect(EntityPlayer player) {
        NBTTagList tagList = player.getHeldItemOffhand().getEnchantmentTagList();

        for( int i = 0; i < tagList.tagCount(); i++ ) {
            NBTTagCompound tag = tagList.getCompoundTagAt(i);
            if( tag.getInteger("id") == Enchantment.getEnchantmentID(Enchantments.FIRE_ASPECT) ) {
                return tag.getInteger("lvl");
            }
        }

        return 0;
    }

    public static int getOffhandKnockback(EntityPlayer player) {
        NBTTagList tagList = player.getHeldItemOffhand().getEnchantmentTagList();

        for( int i = 0; i < tagList.tagCount(); i++ ) {
            NBTTagCompound tag = tagList.getCompoundTagAt(i);
            if( tag.getInteger("id") == Enchantment.getEnchantmentID(Enchantments.KNOCKBACK) ) {
                return tag.getInteger("lvl");
            }
        }

        return 0;
    }

    public static void attackTargetEntityItem(EntityPlayer player, Entity targetEntity, boolean offhand) {
        if( !ForgeHooks.onPlayerAttackTarget(player, targetEntity) ) {
            return;
        }

        if( targetEntity.canBeAttackedWithItem() ) {
            if( !targetEntity.hitByEntity(player) ) {
                float damage;
                int cooldown = 0;
                double reach;

                if(offhand) {
                    clearOldModifiers(player, player.getHeldItemMainhand());
                    addNewModifiers(player, player.getHeldItemOffhand());

                    damage = getOffhandDamage(player);
                    cooldown = getOffhandCooldown(player);
                    reach = ReachFixUtil.getEntityReach(player, EnumHand.OFF_HAND);

                    clearOldModifiers(player, player.getHeldItemOffhand());
                    addNewModifiers(player, player.getHeldItemMainhand());
                }
                else {
                    damage = (float) player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
                    reach = ReachFixUtil.getEntityReach(player, EnumHand.MAIN_HAND);
                }

                float cMod;
                if( targetEntity instanceof EntityLivingBase ) {
                    cMod = EnchantmentHelper.getModifierForCreature(offhand ? player.getHeldItemOffhand() : player.getHeldItemMainhand(), ((EntityLivingBase) targetEntity).getCreatureAttribute());
                }
                else {
                    cMod = EnchantmentHelper.getModifierForCreature(offhand ? player.getHeldItemOffhand() : player.getHeldItemMainhand(), EnumCreatureAttribute.UNDEFINED);
                }

                float cooledStr;
                if(offhand) {
                    if(cooldown > 0) cooledStr = 1.0F - Helpers.execNullable(player.getCapability(EventHandlers.TUTO_CAP, null), CapabilityOffhandCooldown::getOffhandCooldown, 0) / (float) cooldown;
                    else cooledStr = 1.0F;
                }
                else {
                    cooledStr = player.getCooledAttackStrength(0.5F);
                }

                //Post event to get any other modifiers before multiply by cooldown required for compat
                RLCombatModifyDamageEvent modifyResultPre = new RLCombatModifyDamageEvent.Pre(player, targetEntity, offhand, offhand ? player.getHeldItemOffhand() : player.getHeldItemMainhand(), damage, cooledStr);
                MinecraftForge.EVENT_BUS.post(modifyResultPre);
                damage += modifyResultPre.getDamageModifier();

                damage *= (0.2F + cooledStr * cooledStr * 0.8F);
                cMod *= cooledStr;

                if( offhand ) {
                    CapabilityOffhandCooldown coh = player.getCapability(EventHandlers.TUTO_CAP, null);
                    if(coh != null) {
                        coh.setOffhandCooldown(cooldown);
                        coh.setOffhandBeginningCooldown(cooldown);
                        if(!player.world.isRemote) coh.sync();//Sync once here, instead of every tick that there is a cooldown in livingupdate, hopefully works fine?
                    }
                } else {
                    player.resetCooldown();
                }

                if( damage > 0.0F || cMod > 0.0F) {
                    boolean isStrong = cooledStr > 0.9F;
                    boolean knockback = false;
                    boolean isCrit;
                    int knockbackMod = offhand ? getOffhandKnockback(player) : EnchantmentHelper.getKnockbackModifier(player);
                    int fireAspect = offhand ? getOffhandFireAspect(player) : EnchantmentHelper.getFireAspectModifier(player);

                    if( player.isSprinting() && isStrong ) {
                        player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, player.getSoundCategory(), 1.0F, 1.0F);
                        knockbackMod++;
                        knockback = true;
                    }

                    if( ConfigurationHandler.randomCrits ) {
                        isCrit = player.getRNG().nextFloat() < ConfigurationHandler.critChance && !player.isSprinting() && (!ConfigurationHandler.requireEnergyToRandomCrit || isStrong);
                        //Allow forced jump crits at close range
                        if(!isCrit) isCrit = player.getDistance(targetEntity) < 2.0D && isStrong && player.fallDistance > 0.0F && !player.onGround && !player.isOnLadder() &&
                                !player.isInWater() && !player.isPotionActive(MobEffects.BLINDNESS) && !player.isRiding() &&
                                targetEntity instanceof EntityLivingBase && !player.isSprinting();
                    } else {
                        isCrit = isStrong && player.fallDistance > 0.0F && !player.onGround && !player.isOnLadder() && !player.isInWater()
                                         && !player.isPotionActive(MobEffects.BLINDNESS) && !player.isRiding() && targetEntity instanceof EntityLivingBase
                                         && !player.isSprinting();
                    }

                    RLCombatCriticalHitEvent hitResult = new RLCombatCriticalHitEvent(player, targetEntity, isCrit ? 1.5F : 1.0F, isCrit, offhand);
                    MinecraftForge.EVENT_BUS.post(hitResult);
                    if(!(hitResult.getResult() == Event.Result.ALLOW || (isCrit && hitResult.getResult() == Event.Result.DEFAULT))) hitResult = null;

                    isCrit = hitResult != null;
                    if( isCrit ) {
                        damage *= hitResult.getDamageModifier();
                    }

                    damage += cMod;

                    //Post event to get any other modifiers after multiply by cooldown and crit required for compat
                    RLCombatModifyDamageEvent.Post modifyResultPost = new RLCombatModifyDamageEvent.Post(player, targetEntity, offhand, offhand ? player.getHeldItemOffhand() : player.getHeldItemMainhand(), damage, cooledStr, DamageSource.causePlayerDamage(player));
                    MinecraftForge.EVENT_BUS.post(modifyResultPost);
                    damage += modifyResultPost.getDamageModifier();
                    DamageSource dmgSource = modifyResultPost.getDamageSource();//Allow for changing the damage source to custom for compat with mods like SpartanWeaponry

                    boolean doSweepingIgnoreSword = false;
                    boolean doSweeping = false;
                    double tgtDistDelta = player.distanceWalkedModified - player.prevDistanceWalkedModified;
                    if( isStrong && !isCrit && !knockback && player.onGround && tgtDistDelta < player.getAIMoveSpeed() ) {
                        ItemStack ohItem = player.getHeldItem(offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
                        doSweepingIgnoreSword = true;
                        if( ohItem.getItem() instanceof ItemSword ) {
                            doSweeping = true;
                        }
                    }

                    float tgtHealth = 0.0F;
                    boolean burnInflicted = false;
                    if( targetEntity instanceof EntityLivingBase ) {
                        tgtHealth = ((EntityLivingBase) targetEntity).getHealth();
                        if( fireAspect > 0 && !targetEntity.isBurning() ) {
                            targetEntity.setFire(1);
                            burnInflicted = true;
                        }
                    }

                    double tgtMotionX = targetEntity.motionX;
                    double tgtMotionY = targetEntity.motionY;
                    double tgtMotionZ = targetEntity.motionZ;
                    boolean attacked;

                    if( offhand ) {
                        final float attackDmgFinal = damage;
                        attacked = execNullable(targetEntity.getCapability(EventHandlers.SECONDHURTTIMER_CAP, null),
                                                sht -> sht.attackEntityFromOffhand(targetEntity, dmgSource, attackDmgFinal), false);
                    } else {
                        attacked = targetEntity.attackEntityFrom(dmgSource, damage);
                    }
                    if( attacked ) {
                        if( knockbackMod > 0 ) {
                            if( targetEntity instanceof EntityLivingBase ) {
                                ((EntityLivingBase) targetEntity).knockBack(player, knockbackMod * 0.5F, MathHelper.sin(player.rotationYaw * 0.017453292F), -MathHelper.cos(player.rotationYaw * 0.017453292F));
                            } else {
                                targetEntity.addVelocity(-MathHelper.sin(player.rotationYaw * 0.017453292F) * knockbackMod * 0.5F, 0.1D, MathHelper.cos(player.rotationYaw * 0.017453292F) * knockbackMod * 0.5F);
                            }
                            player.motionX *= 0.6D;
                            player.motionZ *= 0.6D;
                            if( !ConfigurationHandler.moreSprint ) {
                                player.setSprinting(false);
                            }
                        }

                        RLCombatSweepEvent sweepResult = new RLCombatSweepEvent(player, targetEntity, damage, offhand, player.getHeldItem(offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND), doSweepingIgnoreSword, doSweeping, EnchantmentHelper.getSweepingDamageRatio(player), targetEntity.getEntityBoundingBox().grow(1.0D, 0.25D, 1.0D), DamageSource.causePlayerDamage(player));
                        MinecraftForge.EVENT_BUS.post(sweepResult);
                        doSweeping = sweepResult.getDoSweep();

                        if( doSweeping ) {
                            float sweepingDamage = 1.0F + (sweepResult.getSweepModifier() * damage);
                            AxisAlignedBB sweepingAABB = sweepResult.getSweepingAABB();
                            DamageSource sweepingDamageSource = sweepResult.getSweepingDamageSource();

                            for( EntityLivingBase living : player.world.getEntitiesWithinAABB(EntityLivingBase.class, sweepingAABB) ) {
                                if( living != player && living != targetEntity && !player.isOnSameTeam(living) && player.getDistanceSq(living) < (reach * reach) ) {
                                    living.knockBack(player, 0.4F, MathHelper.sin(player.rotationYaw * 0.017453292F), -MathHelper.cos(player.rotationYaw * 0.017453292F));
                                    if( offhand ) {
                                        execNullable(living.getCapability(EventHandlers.SECONDHURTTIMER_CAP, null),
                                                     sht -> sht.attackEntityFromOffhand(living, sweepingDamageSource, sweepingDamage));
                                    } else {
                                        living.attackEntityFrom(sweepingDamageSource, sweepingDamage);
                                    }
                                }
                            }
                            player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1.0F, 1.0F);
                            player.spawnSweepParticles();
                        }

                        if( targetEntity instanceof EntityPlayerMP && targetEntity.velocityChanged ) {
                            ((EntityPlayerMP) targetEntity).connection.sendPacket(new SPacketEntityVelocity(targetEntity));
                            targetEntity.velocityChanged = false;
                            targetEntity.motionX = tgtMotionX;
                            targetEntity.motionY = tgtMotionY;
                            targetEntity.motionZ = tgtMotionZ;
                        }

                        if( isCrit ) {
                            if( offhand ) {
                                player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, player.getSoundCategory(), 1.0F, 1.0F);
                            }
                            player.onCriticalHit(targetEntity);
                        }

                        boolean playSound = true;
                        ItemStack heldItem = offhand ? player.getHeldItemOffhand() : player.getHeldItemMainhand();
                        if( !heldItem.isEmpty() ) {
                            if( heldItem.getItem() instanceof ItemSpade ) {
                                playSound = false;
                            }
                            if( playSound ) {
                                if( ConfigurationHandler.hitSound && (!ConfigurationHandler.critSound || !isCrit) ) {
                                    player.world.playSound(null, player.posX, player.posY, player.posZ, Sounds.SWORD_SLASH, player.getSoundCategory(), 1.0F, 1.0F);
                                }
                                if( ConfigurationHandler.critSound && isCrit ) {
                                    player.world.playSound(null, player.posX, player.posY, player.posZ, Sounds.CRITICAL_STRIKE, player.getSoundCategory(), 1.0F, 1.0F);
                                }
                            }
                        }

                        if( !isCrit && !doSweeping ) {
                            if( isStrong ) {
                                player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, player.getSoundCategory(), 1.0F, 1.0F);
                            } else {
                                player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, player.getSoundCategory(), 1.0F, 1.0F);
                            }
                        }

                        if( cMod > 0.0F ) {
                            player.onEnchantmentCritical(targetEntity);
                        }

                        if( !player.world.isRemote && targetEntity instanceof EntityPlayer ) {
                            EntityPlayer entityplayer = (EntityPlayer) targetEntity;
                            ItemStack activeItem = entityplayer.isHandActive() ? entityplayer.getActiveItemStack() : ItemStack.EMPTY;
                            if( heldItem.getItem() instanceof ItemAxe && activeItem.getItem() instanceof ItemShield ) {
                                float efficiency = 0.25F + EnchantmentHelper.getEfficiencyModifier(player) * 0.05F;
                                if( knockback ) {
                                    efficiency += 0.75F;
                                }

                                if( player.getRNG().nextFloat() < efficiency ) {
                                    entityplayer.getCooldownTracker().setCooldown(activeItem.getItem(), 100);
                                    player.world.setEntityState(entityplayer, (byte) 30);
                                }
                            }
                        }

                        player.setLastAttackedEntity(targetEntity);

                        if( targetEntity instanceof EntityLivingBase ) {
                            EnchantmentHelper.applyThornEnchantments((EntityLivingBase) targetEntity, player);
                        }

                        if(offhand && !heldItem.isEmpty()) {
                            NBTTagList nbttaglist = heldItem.getEnchantmentTagList();

                            for(int i = 0; i < nbttaglist.tagCount(); ++i) {
                                int j = nbttaglist.getCompoundTagAt(i).getShort("id");
                                int k = nbttaglist.getCompoundTagAt(i).getShort("lvl");

                                if(Enchantment.getEnchantmentByID(j) instanceof EnchantmentDamage) {
                                    EnchantmentDamage ench = (EnchantmentDamage)Enchantment.getEnchantmentByID(j);
                                    if(ench.damageType == 2) ench.onEntityDamaged(player, targetEntity, k);
                                }
                            }
                        }
                        else EnchantmentHelper.applyArthropodEnchantments(player, targetEntity);

                        Entity entity = targetEntity;

                        if( targetEntity instanceof MultiPartEntityPart ) {
                            IEntityMultiPart ientitymultipart = ((MultiPartEntityPart) targetEntity).parent;
                            if( ientitymultipart instanceof EntityLivingBase ) {
                                entity = (EntityLivingBase) ientitymultipart;
                            }
                        }

                        if( !heldItem.isEmpty() && entity instanceof EntityLivingBase ) {
                            ItemStack beforeHitCopy = heldItem.copy();
                            heldItem.hitEntity((EntityLivingBase) entity, player);
                            if( heldItem.isEmpty() ) {
                                player.setHeldItem(offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, ItemStack.EMPTY);
                                ForgeEventFactory.onPlayerDestroyItem(player, beforeHitCopy, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
                            }
                        }

                        if( targetEntity instanceof EntityLivingBase ) {
                            float healthDelta = tgtHealth - ((EntityLivingBase) targetEntity).getHealth();
                            player.addStat(StatList.DAMAGE_DEALT, Math.round(healthDelta * 10.0F));

                            if( fireAspect > 0 ) {
                                targetEntity.setFire(fireAspect * 4);
                            }

                            if( player.world instanceof WorldServer && healthDelta > 2.0F ) {
                                int k = (int) (healthDelta * 0.5D);
                                ((WorldServer) player.world).spawnParticle(EnumParticleTypes.DAMAGE_INDICATOR, targetEntity.posX, targetEntity.posY + targetEntity.height * 0.5F, targetEntity.posZ, k, 0.1D, 0.0D, 0.1D, 0.2D);
                            }
                        }

                        player.addExhaustion(0.3F);
                    } else {
                        player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, player.getSoundCategory(), 1.0F, 1.0F);

                        if( burnInflicted ) {
                            targetEntity.extinguish();
                        }
                    }

                    if(Loader.isModLoaded("spartanweaponry")){
                        SpartanWeaponryHandler.handleSpartanQuickStrike(player.getHeldItem(offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND), targetEntity);
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
