package bettercombat.mod.util;

import com.github.alexthe666.iceandfire.entity.EntityMutlipartPart;
import net.minecraft.entity.Entity;

public abstract class InFHandler {

    public static boolean isMultipart(Entity entity) {
        return entity instanceof EntityMutlipartPart;
    }

    public static Entity getMultipartParent(Entity entity) {
        if(entity instanceof EntityMutlipartPart) {
            return (Entity)((EntityMutlipartPart)entity).getParent();
        }
        return entity;
    }
}