package crazypants.enderio.network;

import com.enderio.core.common.network.ThreadedNetworkWrapper;

import crazypants.enderio.EnderIO;
import crazypants.enderio.item.PacketConduitProbe;
import crazypants.enderio.item.PacketConduitProbeMode;
import crazypants.enderio.item.YetaWrenchPacketProcessor;
import crazypants.enderio.machine.PacketRedstoneMode;
import crazypants.enderio.machine.generator.zombie.PacketNutrientTank;
import crazypants.enderio.xp.PacketExperienceContainer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {

  public static final ThreadedNetworkWrapper INSTANCE = new ThreadedNetworkWrapper(EnderIO.DOMAIN);

  private static int ID = 0;

  public static int nextID() {
    return ID++;
  }

  public static void sendToAllAround(IMessage message, TileEntity te, int range) {
    BlockPos p = te.getPos();
    INSTANCE.sendToAllAround(message, new TargetPoint(te.getWorld().provider.getDimension(), p.getX(), p.getY(), p.getZ(), range));
  }

  public static void sendToAllAround(IMessage message, TileEntity te) {
    sendToAllAround(message, te, 64);
  }

  public static void sendTo(IMessage message, EntityPlayerMP player) {
    INSTANCE.sendTo(message, player);
  }

  public static void init(FMLInitializationEvent event) {
    INSTANCE.registerMessage(PacketRedstoneMode.class, PacketRedstoneMode.class, nextID(), Side.SERVER);
    INSTANCE.registerMessage(GuiPacket.Handler.class, GuiPacket.class, nextID(), Side.SERVER);
    INSTANCE.registerMessage(PacketExperienceContainer.class, PacketExperienceContainer.class, nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketNutrientTank.class, PacketNutrientTank.class, nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketConduitProbe.class, PacketConduitProbe.class, PacketHandler.nextID(), Side.SERVER);
    INSTANCE.registerMessage(PacketConduitProbeMode.class, PacketConduitProbeMode.class, PacketHandler.nextID(), Side.SERVER);
    INSTANCE.registerMessage(YetaWrenchPacketProcessor.class, YetaWrenchPacketProcessor.class, PacketHandler.nextID(), Side.SERVER);
  }

}
