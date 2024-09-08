package bettercombat.mod.util.inf;

import com.github.alexthe666.iceandfire.entity.util.EntityMultipartPart;
import net.minecraft.entity.Entity;

public abstract class InFLightningWrapper {

    public static boolean isMultipart(Entity entity) {
        return entity instanceof EntityMultipartPart;
    }

    public static Entity getMultipartParent(Entity entity) {
        if(entity instanceof EntityMultipartPart) {
            return ((EntityMultipartPart)entity).getParent();
        }
        return entity;
    }
}