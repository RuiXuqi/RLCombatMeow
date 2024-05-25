package bettercombat.mod.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;

public class DefaultImplSecondHurtTimer implements ISecondHurtTimer {

    private int hurtTime;

    @Override
    public int getHurtTimerBCM() {
        return this.hurtTime;
    }

    @Override
    public void setHurtTimerBCM(int hurtTimer) {
        this.hurtTime = hurtTimer;
    }

    @Override
    public void tick() {
        if(this.hurtTime > 0) this.hurtTime -= 1;
    }

    @Override
    public boolean attackEntityFromOffhand(Entity target, DamageSource dmgSrc, float amount) {
        if(target.isEntityInvulnerable(dmgSrc) || this.hurtTime > 0 || target.world.isRemote || !(target instanceof EntityLivingBase || target instanceof MultiPartEntityPart)) {
            return false;
        }

        EntityLivingBase targetMain;
        if(target instanceof MultiPartEntityPart) targetMain = (EntityLivingBase)(((MultiPartEntityPart)target).parent);//use/change values from parent, but attack part
        else targetMain = (EntityLivingBase)target;

        if(targetMain.getHealth() <= 0.0F) return false;

        boolean successfulAttack = false;
        if(this.hurtTime <= 0) {
            Entity trueSrc = dmgSrc.getTrueSource();
            ItemStack buf;

            if(trueSrc instanceof EntityPlayer) { // switch offhand item to mainhand, so entities can properly determine what item hit them
                EntityPlayer player = (EntityPlayer)trueSrc;
                buf = player.getHeldItemMainhand();
                player.setHeldItem(EnumHand.MAIN_HAND, player.getHeldItemOffhand());
                player.setHeldItem(EnumHand.OFF_HAND, buf);
            }

            // save current hit times and set the value to 0 for the entity to allow hitting with the off-hand
            int mainHurtTime = targetMain.hurtTime;
            int mainHurtResistance = targetMain.hurtResistantTime;
            targetMain.hurtTime = 0;
            targetMain.hurtResistantTime = 0;

            //attack entity
            successfulAttack = target.attackEntityFrom(dmgSrc, amount);//attack non-cast, incase its multipart like the ender dragon
            if(successfulAttack) this.hurtTime = 10;

            // reset current hit times to the entity
            targetMain.hurtTime = mainHurtTime;
            targetMain.hurtResistantTime = mainHurtResistance;

            if(trueSrc instanceof EntityPlayer) { // reset held items to their proper slots
                EntityPlayer player = (EntityPlayer) trueSrc;
                buf = player.getHeldItemOffhand();
                player.setHeldItem(EnumHand.OFF_HAND, player.getHeldItemMainhand());
                player.setHeldItem(EnumHand.MAIN_HAND, buf);
            }
        }
        return successfulAttack;
    }
}