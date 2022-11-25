package bettercombat.mod.util;

import com.google.common.collect.Multimap;
import com.tmtravlr.qualitytools.QualityToolsHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public abstract class QualityToolsHandler {

    public static void addNewModifiersQualityTools(EntityLivingBase entity, ItemStack stack) {
        NBTTagCompound tag = com.tmtravlr.qualitytools.QualityToolsHelper.getQualityTag(stack);
        if(!tag.isEmpty()) {
            boolean hasSlot = false;
            NBTTagList slots = tag.getTagList("Slots", 8);
            for(int i = 0; i < slots.tagCount(); ++i) {
                if(slots.getStringTagAt(i).equalsIgnoreCase(EntityEquipmentSlot.MAINHAND.getName())) hasSlot = true;
            }
            if(!hasSlot) return;

            NBTTagList attributeList = tag.getTagList("AttributeModifiers", 10);
            for(int i = 0; i < attributeList.tagCount(); ++i) {
                String attributeName = attributeList.getCompoundTagAt(i).getString("AttributeName");
                if(attributeName.contains("attackDamage") || attributeName.contains("attackSpeed") || attributeName.contains("reachDistance")) {
                    AttributeModifier modifier = SharedMonsterAttributes.readAttributeModifierFromNBT(attributeList.getCompoundTagAt(i));
                    IAttributeInstance entityAttribute = entity.getAttributeMap().getAttributeInstanceByName(attributeName);
                    if(entityAttribute != null && modifier != null && !entityAttribute.hasModifier(modifier)) {
                        entityAttribute.applyModifier(modifier);
                    }
                }
            }
        }
    }

    public static void clearOldModifiersQualityTools(EntityLivingBase entity, ItemStack stack, Multimap<String, AttributeModifier> modifiersToRemove) {
        NBTTagCompound tag = QualityToolsHelper.getQualityTag(stack);
        if(!tag.isEmpty()) {
            boolean hasSlot = false;
            NBTTagList slots = tag.getTagList("Slots", 8);
            for(int i = 0; i < slots.tagCount(); ++i) {
                if(slots.getStringTagAt(i).equalsIgnoreCase(EntityEquipmentSlot.MAINHAND.getName())) hasSlot = true;
            }
            if(hasSlot) {
                NBTTagList attributeList = tag.getTagList("AttributeModifiers", 10);
                for(int i = 0; i < attributeList.tagCount(); ++i) {
                    String attributeName = attributeList.getCompoundTagAt(i).getString("AttributeName");
                    if(attributeName.contains("attackDamage") || attributeName.contains("attackSpeed") || attributeName.contains("reachDistance")) {
                        AttributeModifier modifier = SharedMonsterAttributes.readAttributeModifierFromNBT(attributeList.getCompoundTagAt(i));
                        if(modifier != null) modifiersToRemove.put(attributeName, modifier);
                    }
                }
            }
        }
    }
}
