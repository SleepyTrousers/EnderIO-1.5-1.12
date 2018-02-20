package crazypants.enderio.base.filter;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.conduit.IConduit;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;

public interface INetworkedInventory {

  /**
   * @return The IItemHandler of the Inventory Network
   */
  @Nullable
  IItemHandler getInventory();

  boolean hasTarget(@Nonnull IConduit conduit, @Nonnull EnumFacing dir);

  /**
   * @return This inventory's conduit
   */
  @Nonnull
  IConduit getCon();

  /**
   * @return The facing of this inventory's conduit
   */
  @Nonnull
  EnumFacing getConDir();

  /**
   * @param stack
   * @return
   */
  int insertIntoTargets(@Nonnull ItemStack stack);

  /**
   * Checks if the given conduit can insert items
   *
   * @return true if the conduit can insert
   */
  boolean canInsert();

  /**
   * Checks if the given conduit can extract items
   *
   * @return true if the conduit can extract
   */
  boolean canExtract();

  boolean isSticky();

  /**
   * @return Priority of the Inventory Network
   */
  int getPriority();

  /**
   * @return Name of the inventory (after localisaton)
   */
  @Nonnull
  String getLocalizedInventoryName();

  /**
   * @return Location of the inventory
   */
  @Nonnull
  BlockPos getLocation();

  @Nonnull
  List<?> getSendPriority();

  int insertItem(@Nonnull ItemStack stack);

  void onTick();

  void updateInsertOrder();

}
