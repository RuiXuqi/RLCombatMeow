package bettercombat.mod.util;
/*
import meldexun.reachfix.util.ReachFixUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.util.List;


Class based on ReachFix EntityRendererHook by Meldexun https://github.com/Meldexun/ReachFix/blob/1.12/src/main/java/meldexun/reachfix/hook/client/EntityRendererHook.java

public abstract class ReachFixFuzzyUtil {

    public static RayTraceResult pointedObject(Entity viewEntity, EntityPlayer player, EnumHand hand, World world, float partialTicks) {
        Vec3d start = viewEntity.getPositionEyes(partialTicks);
        Vec3d look = viewEntity.getLook(partialTicks);
        Vec3d end = start.add(look.scale(ReachFixUtil.getBlockReach(player, hand)));
        RayTraceResult pointedBlock = world.rayTraceBlocks(start, end, false, false, false);

        if(pointedBlock != null && start.distanceTo(pointedBlock.hitVec) <= ReachFixUtil.getEntityReach(player, hand)) {
            BetterCombatMod.LOG.log(Level.INFO, "Pointed block limiting reach");

            RayTraceResult pointedEntity = null;
            if(ConfigurationHandler.enableSweepingTargetting) {
                for(int i=-1;i<2;i++) {//Run 3 checks, swing left-to-right or right-to-left depending on hand
                    BetterCombatMod.LOG.log(Level.INFO, "Ratio: " + (i*(hand.equals(EnumHand.MAIN_HAND) ? -1 : 1)*ConfigurationHandler.sweepingRatio));
                    pointedEntity = getPointedEntity(viewEntity, world, start, getPerpendicularPointFromRatio(start, pointedBlock.hitVec, i*(hand.equals(EnumHand.MAIN_HAND) ? -1 : 1)*ConfigurationHandler.sweepingRatio), partialTicks);
                    if(pointedEntity!=null) break;
                }
            }
            else pointedEntity = getPointedEntity(viewEntity, world, start, pointedBlock.hitVec, partialTicks);

            if(pointedEntity == null) {
                BetterCombatMod.LOG.log(Level.INFO, "Pointed block limiting reach, pointed entity null");
            }
            else {
                BetterCombatMod.LOG.log(Level.INFO, "Pointed block limiting reach, pointed entity not null");
            }

            return pointedEntity == null ? pointedBlock : pointedEntity;
        }
        else {
            BetterCombatMod.LOG.log(Level.INFO, "Pointed block null or too far");
            Vec3d end1 = start.add(look.scale(ReachFixUtil.getEntityReach(player, hand)));
            RayTraceResult pointedEntity = null;
            if(ConfigurationHandler.enableSweepingTargetting) {
                for(int i=-1;i<2;i++) {//Run 3 checks, swing left-to-right or right-to-left depending on hand
                    BetterCombatMod.LOG.log(Level.INFO, "Ratio: " + (i*(hand.equals(EnumHand.MAIN_HAND) ? -1 : 1)*ConfigurationHandler.sweepingRatio));
                    pointedEntity = getPointedEntity(viewEntity, world, start, getPerpendicularPointFromRatio(start, end1, i*(hand.equals(EnumHand.MAIN_HAND) ? -1 : 1)*ConfigurationHandler.sweepingRatio), partialTicks);
                    if(pointedEntity!=null) {
                        break;
                    }
                }
            }
            else pointedEntity = getPointedEntity(viewEntity, world, start, end1, partialTicks);

            if(pointedEntity!=null) {
                BetterCombatMod.LOG.log(Level.INFO, "Pointed block null or too far, pointed entity not null");
                return pointedEntity;
            }
        }

        BetterCombatMod.LOG.log(Level.INFO, "All failed, returning miss");

        return new RayTraceResult(RayTraceResult.Type.MISS, end, null, new BlockPos(end));
    }

    @Nullable
    private static RayTraceResult getPointedEntity(Entity viewEntity, World world, Vec3d start, Vec3d end, float partialTicks) {
        AxisAlignedBB aabb = new AxisAlignedBB(start, end).grow(1.0D);
        Entity lowestRidingEntity = viewEntity.getLowestRidingEntity();
        List<Entity> possibleEntities = world.getEntitiesInAABBexcluding(viewEntity, aabb, entity -> {
            if (!EntitySelectors.NOT_SPECTATING.apply(entity)) {
                return false;
            }
            if (!entity.canBeCollidedWith()) {
                return false;
            }
            if (lowestRidingEntity != entity.getLowestRidingEntity()) {
                return true;
            }
            return entity.canRiderInteract();
        });

        BetterCombatMod.LOG.log(Level.INFO, "Checking pointed entity, entities in aabb: " + possibleEntities.size());

        RayTraceResult result = null;
        Entity pointedEntity = null;
        double min = Double.MAX_VALUE;
        for (Entity entity : possibleEntities) {

            BetterCombatMod.LOG.log(Level.INFO, "Iterating entity: " + entity.getName());

            AxisAlignedBB entityAabb = getInterpolatedAABB(entity, partialTicks);
            if (entityAabb.contains(start)) {
                BetterCombatMod.LOG.log(Level.INFO, "Returning contains start");
                return new RayTraceResult(entity, start);
            }

            RayTraceResult rtr = entityAabb.calculateIntercept(start, end);
            if (rtr == null || rtr.typeOfHit == RayTraceResult.Type.MISS) {
                BetterCombatMod.LOG.log(Level.INFO, "RTR " + (rtr==null ? "null" : rtr.typeOfHit) + ", continuing");
                continue;
            }
            BetterCombatMod.LOG.log(Level.INFO, "RTR not null or miss");

            double dist = start.squareDistanceTo(rtr.hitVec);
            if (dist < min) {
                BetterCombatMod.LOG.log(Level.INFO, "Dist less than min");
                result = rtr;
                pointedEntity = entity;
                min = dist;
            }
        }

        if(result == null || result.typeOfHit == RayTraceResult.Type.MISS) {
            return getFuzzyFallbackRTR(viewEntity, world, start, end, partialTicks);
        }

        BetterCombatMod.LOG.log(Level.INFO, "Returning entity");

        return new RayTraceResult(pointedEntity, result.hitVec);
    }

    @Nullable
    private static RayTraceResult getFuzzyFallbackRTR(Entity viewEntity, World world, Vec3d start, Vec3d end, float partialTicks) {
        if(ConfigurationHandler.enableFuzzyTargetting) {

            BetterCombatMod.LOG.log(Level.INFO, "Running fuzzy fallback");

            Entity lowestRidingEntity = viewEntity.getLowestRidingEntity();
            //AxisAlignedBB aabb = new AxisAlignedBB(end.x-0.05, end.y-0.05, end.z-0.05,end.x+0.05, end.y+0.05, end.z+0.05);
            AxisAlignedBB aabb = new AxisAlignedBB(end.x-ConfigurationHandler.fuzzyRadius, end.y-ConfigurationHandler.fuzzyRadius, end.z-ConfigurationHandler.fuzzyRadius,end.x+ConfigurationHandler.fuzzyRadius, end.y+ConfigurationHandler.fuzzyRadius, end.z+ConfigurationHandler.fuzzyRadius);
            List<Entity> possibleEntities = world.getEntitiesInAABBexcluding(viewEntity, aabb, entity -> {
                if (!EntitySelectors.NOT_SPECTATING.apply(entity)) {
                    return false;
                }
                if (!entity.canBeCollidedWith()) {
                    return false;
                }
                if (lowestRidingEntity != entity.getLowestRidingEntity()) {
                    return true;
                }
                return entity.canRiderInteract();
            });

            BetterCombatMod.LOG.log(Level.INFO, "EntitiesInAABB size: " + possibleEntities.size());

            for(Entity entity : possibleEntities) {

                BetterCombatMod.LOG.log(Level.INFO, "Checking entity: " + entity.getName());

                AxisAlignedBB interp = getInterpolatedAABB(entity, partialTicks);
                if(aabb.intersects(interp)) {
                    AxisAlignedBB intersect = aabb.intersect(getInterpolatedAABB(entity, partialTicks));

                    BetterCombatMod.LOG.log(Level.INFO, "Returning intersects: " + entity.getName());

                    return new RayTraceResult(entity, new Vec3d((intersect.maxX + intersect.minX)/2,(intersect.maxY + intersect.minY)/2,(intersect.maxZ + intersect.minZ)/2));
                }
            }
        }
        return null;
    }

    private static AxisAlignedBB getInterpolatedAABB(Entity entity, float partialTicks) {
        AxisAlignedBB aabb = entity.getEntityBoundingBox();
        float collisionBorderSize = entity.getCollisionBorderSize();
        if (collisionBorderSize != 0.0F) {
            aabb = aabb.grow(collisionBorderSize);
        }
        if (partialTicks != 0.0F) {
            double x = -(entity.posX - entity.lastTickPosX) * (1.0D - partialTicks);
            double y = -(entity.posY - entity.lastTickPosY) * (1.0D - partialTicks);
            double z = -(entity.posZ - entity.lastTickPosZ) * (1.0D - partialTicks);
            aabb = aabb.offset(x, y, z);
        }
        return aabb;
    }

    private static Vec3d getPerpendicularPointFromRatio(Vec3d start, Vec3d end, double ratio) {
        if(ratio==0) return end;
        double distAB = Math.sqrt(Math.pow(end.x-start.x, 2) + Math.pow(end.z-start.z, 2));
        double dist = distAB*ratio;
        double dx = -(end.z-start.z) / distAB;
        double dz = (end.x-start.x) / distAB;

        return new Vec3d(end.x + dist*dx, end.y, end.z + dist*dz);
    }
}

 */