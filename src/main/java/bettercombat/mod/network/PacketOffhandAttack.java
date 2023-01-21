package bettercombat.mod.network;

import bettercombat.mod.util.Helpers;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketOffhandAttack implements IMessage
{
    private int entityId;
    private double motionX;
    private double motionY;
    private double motionZ;

    @SuppressWarnings("unused")
    public PacketOffhandAttack() {}

    public PacketOffhandAttack(int parEntityId) {
        this.entityId = parEntityId;
        this.motionX = 0;
        this.motionY = 0;
        this.motionZ = 0;
    }

    public PacketOffhandAttack(int parEntityId, double motionX, double motionY, double motionZ) {
        this.entityId = parEntityId;
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityId = ByteBufUtils.readVarInt(buf, 4);
        this.motionX = buf.readDouble();
        this.motionY = buf.readDouble();
        this.motionZ = buf.readDouble();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeVarInt(buf, this.entityId, 4);
        buf.writeDouble(this.motionX);
        buf.writeDouble(this.motionY);
        buf.writeDouble(this.motionZ);
    }

    public static class Handler
            implements IMessageHandler<PacketOffhandAttack, IMessage>
    {
        @Override
        public IMessage onMessage(final PacketOffhandAttack message, final MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private static void handle(PacketOffhandAttack message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            Entity theEntity = player.world.getEntityByID(message.entityId);
            if( theEntity != null ) {
                Helpers.attackTargetEntityItem(player, theEntity, true, message.motionX, message.motionY, message.motionZ);
            }
            ((WorldServer) player.world).getEntityTracker().sendToTracking(player, new SPacketAnimation(player, 3));
        }
    }
}