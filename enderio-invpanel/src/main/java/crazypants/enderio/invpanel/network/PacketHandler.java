package crazypants.enderio.invpanel.network;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.ThreadedNetworkWrapper;

import crazypants.enderio.invpanel.EnderIOInvPanel;
import crazypants.enderio.invpanel.remote.PacketPrimeInventoryPanelRemote;
import crazypants.enderio.invpanel.network.sensor.PacketActive;
import crazypants.enderio.invpanel.network.sensor.PacketItemCount;
import crazypants.enderio.invpanel.network.sensor.PacketItemToCheck;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {

  public static final @Nonnull ThreadedNetworkWrapper INSTANCE = new ThreadedNetworkWrapper(EnderIOInvPanel.MODID); // sic! not DOMAIN!

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
    INSTANCE.registerMessage(PacketItemInfo.Handler.class, PacketItemInfo.class, PacketHandler.nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketItemList.Handler.class, PacketItemList.class, PacketHandler.nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketRequestMissingItems.Handler.class, PacketRequestMissingItems.class, PacketHandler.nextID(), Side.SERVER);
    INSTANCE.registerMessage(PacketFetchItem.Handler.class, PacketFetchItem.class, PacketHandler.nextID(), Side.SERVER);
    INSTANCE.registerMessage(PacketMoveItems.Handler.class, PacketMoveItems.class, PacketHandler.nextID(), Side.SERVER);
    INSTANCE.registerMessage(PacketDatabaseReset.Handler.class, PacketDatabaseReset.class, PacketHandler.nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketGuiSettings.Handler.class, PacketGuiSettings.class, PacketHandler.nextID(), Side.SERVER);
    INSTANCE.registerMessage(PacketStoredCraftingRecipe.Handler.class, PacketStoredCraftingRecipe.class, PacketHandler.nextID(), Side.SERVER);
    INSTANCE.registerMessage(PacketSetExtractionDisabled.Handler.class, PacketSetExtractionDisabled.class, PacketHandler.nextID(), Side.SERVER);
    INSTANCE.registerMessage(PacketUpdateExtractionDisabled.Handler.class, PacketUpdateExtractionDisabled.class, PacketHandler.nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketPrimeInventoryPanelRemote.Handler.class, PacketPrimeInventoryPanelRemote.class, PacketHandler.nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketGuiSettingsUpdated.Handler.class, PacketGuiSettingsUpdated.class, PacketHandler.nextID(), Side.CLIENT);

    INSTANCE.registerMessage(PacketActive.Handler.class, PacketActive.class, PacketHandler.nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketItemToCheck.Handler.class, PacketItemToCheck.class, PacketHandler.nextID(), Side.SERVER);
    INSTANCE.registerMessage(PacketItemCount.Handler.class, PacketItemCount.class, PacketHandler.nextID(), Side.SERVER);
  }

}
