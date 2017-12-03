package crazypants.enderio.machines.network;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.ThreadedNetworkWrapper;

import crazypants.enderio.base.config.PacketConfigSyncNew;
import crazypants.enderio.base.config.PacketConfigSyncNew.PacketConfigSyncNewHandler;
import crazypants.enderio.machines.EnderIOMachines;
import crazypants.enderio.machines.config.Config;
import crazypants.enderio.machines.machine.generator.zombie.PacketNutrientTank;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {

  public static final @Nonnull ThreadedNetworkWrapper INSTANCE = new ThreadedNetworkWrapper(EnderIOMachines.MODID); // sic! not DOMAIN!

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
    INSTANCE.registerMessage(new PacketConfigSyncNewHandler(Config.F), PacketConfigSyncNew.class, PacketHandler.nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketNutrientTank.class, PacketNutrientTank.class, nextID(), Side.CLIENT);
  }

}
