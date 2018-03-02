package crazypants.enderio.conduit.packet;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.ThreadedNetworkWrapper;

import crazypants.enderio.base.filter.network.PacketExistingItemFilterSnapshot;
import crazypants.enderio.base.filter.network.PacketModItemFilter;
import crazypants.enderio.conduit.EnderIOConduits;
import crazypants.enderio.conduit.liquid.PacketConduitFluidLevel;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {

  public static final @Nonnull ThreadedNetworkWrapper INSTANCE = new ThreadedNetworkWrapper(EnderIOConduits.MODID); // sic! not DOMAIN!

  private static int ID = 0;

  public static int nextID() {
    return ID++;
  }

  public static void sendToAllAround(IMessage message, TileEntity te) {
    INSTANCE.sendToAllAround(message, te);
  }

  public static void sendTo(@Nonnull IMessage message, EntityPlayerMP player) {
    INSTANCE.sendTo(message, player);
  }

  public static void sendToServer(@Nonnull IMessage message) {
    INSTANCE.sendToServer(message);
  }

  public static void init(FMLInitializationEvent event) {
    INSTANCE.registerMessage(PacketConduitFluidLevel.Handler.class, PacketConduitFluidLevel.class, nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketExtractMode.Handler.class, PacketExtractMode.class, PacketHandler.nextID(), Side.SERVER);
    INSTANCE.registerMessage(PacketConnectionMode.Handler.class, PacketConnectionMode.class, PacketHandler.nextID(), Side.SERVER);
    INSTANCE.registerMessage(PacketItemConduitFilter.Handler.class, PacketItemConduitFilter.class, PacketHandler.nextID(), Side.SERVER);
    INSTANCE.registerMessage(PacketExistingItemFilterSnapshot.Handler.class, PacketExistingItemFilterSnapshot.class, PacketHandler.nextID(), Side.SERVER);
    INSTANCE.registerMessage(PacketModItemFilter.Handler.class, PacketModItemFilter.class, PacketHandler.nextID(), Side.SERVER);
    INSTANCE.registerMessage(PacketFluidFilter.Handler.class, PacketFluidFilter.class, PacketHandler.nextID(), Side.SERVER);
    INSTANCE.registerMessage(PacketRedstoneConduitSignalColor.Handler.class, PacketRedstoneConduitSignalColor.class, PacketHandler.nextID(), Side.SERVER);
    INSTANCE.registerMessage(PacketRedstoneConduitOutputStrength.Handler.class, PacketRedstoneConduitOutputStrength.class, PacketHandler.nextID(), Side.SERVER);
    INSTANCE.registerMessage(PacketOpenConduitUI.Handler.class, PacketOpenConduitUI.class, PacketHandler.nextID(), Side.SERVER);
    INSTANCE.registerMessage(PacketSlotVisibility.Handler.class, PacketSlotVisibility.class, PacketHandler.nextID(), Side.SERVER);
  }

}
