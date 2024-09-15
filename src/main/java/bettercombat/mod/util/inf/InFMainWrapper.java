package bettercombat.mod.util.inf;

import com.github.alexthe666.iceandfire.entity.EntityMutlipartPart;
import net.minecraft.entity.Entity;

public abstract class InFMainWrapper {

    public static boolean isMultipart(Entity entity) {
        return entity instanceof EntityMutlipartPart;
    }

    public static Entity getMultipartParent(Entity entity) {
        if(entity instanceof EntityMutlipartPart) {
            return ((EntityMutlipartPart)entity).getParent();
        }
        return entity;
    }
}