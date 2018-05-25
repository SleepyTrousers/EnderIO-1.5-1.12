package crazypants.enderio.invpanel.conduit.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.conduit.IServerConduit;
import crazypants.enderio.base.invpanel.capability.InventoryDatabaseSource;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandler;

public interface IDataConduit extends IClientConduit, IServerConduit {

  public static final @Nonnull String ICON_KEY = "blocks/data_conduit";
  public static final @Nonnull String ICON_CORE_KEY = "blocks/data_conduit_core";

  /**
   * Gets the inventory this conduit connection is attached to
   * 
   * @param dir
   *          Direction of the conduit connection
   * @return The ItemHandler for the given direction
   */
  @Nullable
  IItemHandler getExternalInventory(@Nonnull EnumFacing dir);

  /**
   * Adds a source to the map of inventories this conduit is attached to.
   * 
   * @param dir
   *          Direction of the source
   * @param source
   *          Source location and handler
   */
  void addSource(@Nonnull EnumFacing dir, @Nonnull InventoryDatabaseSource source);

  void removeSource(@Nonnull EnumFacing dir);

  void checkConnections(@Nonnull EnumFacing dir);

}
