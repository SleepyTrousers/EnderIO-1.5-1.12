package crazypants.enderio.rail;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.EnderIO;
import io.netty.buffer.ByteBuf;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class PacketTeleportEffects implements IMessage, IMessageHandler<PacketTeleportEffects, IMessage> {

    double x;
    double y;
    double z;

    public PacketTeleportEffects() {}

    public PacketTeleportEffects(Entity ent) {
        x = ent.posX;
        y = ent.posY;
        z = ent.posZ;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readDouble();
        y = buf.readDouble();
        z = buf.readDouble();
    }

    @Override
    public IMessage onMessage(PacketTeleportEffects message, MessageContext ctx) {
        EntityPlayer player = EnderIO.proxy.getClientPlayer();
        World world = player.worldObj;
        Random rand = world.rand;
        for (int i = 0; i < 15; i++) {
            double xOff = (rand.nextDouble() - 0.5) * 1.1;
            double yOff = (rand.nextDouble() - 0.5) * 0.2;
            double zOff = (rand.nextDouble() - 0.5) * 1.1;
            Minecraft.getMinecraft()
                    .theWorld
                    .spawnParticle(
                            "portal",
                            message.x + xOff,
                            message.y + yOff,
                            message.z + zOff,
                            (rand.nextDouble() - 0.5) * 1.5,
                            -rand.nextDouble(),
                            (rand.nextDouble() - 0.5) * 1.5);
        }
        return null;
    }
}
