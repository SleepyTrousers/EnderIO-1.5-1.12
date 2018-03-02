package crazypants.enderio.base.filter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.init.ModObjectRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IItemFilter {

  default void openGui(@Nonnull EntityPlayer player, @Nonnull ItemStack filter, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EnumFacing dir,
      int param1) {
    ModObjectRegistry.getModObjectNN(filter.getItem()).openGui(worldIn, pos, player, dir, param1);
  }

  void readFromNBT(@Nonnull NBTTagCompound nbtRoot);

  void writeToNBT(@Nonnull NBTTagCompound nbtRoot);

  void writeToByteBuf(@Nonnull ByteBuf buf);

  void readFromByteBuf(@Nonnull ByteBuf buf);

  /**
   * Checks if the given item passes the filter or not.
   * 
   * @param inv
   *          the attached inventory - or null when used without an inventory (eg for a GUI)
   * @param item
   *          the item to check
   * @return true if the item is allowed to pass
   */
  default boolean doesItemPassFilter(@Nullable INetworkedInventory inv, @Nonnull ItemStack item) {
    return getMaxCountThatPassesFilter(inv, item) > 0;
  };

  /**
   * Checks if the given item passes the filter or not.
   * 
   * @param inv
   *          the attached inventory - or null when used without an inventory (eg for a GUI)
   * @param item
   *          the item to check
   * @return false if the item is not allowed to pass, otherwise the maximum number of items that pass. If the filter doesn't impose a limit, the item's max
   *         stacksize is returned.
   */
  default int getMaxCountThatPassesFilter(@Nullable INetworkedInventory inv, @Nonnull ItemStack item) {
    return doesItemPassFilter(inv, item) ? item.getMaxStackSize() : 0;
  };

  boolean isValid();

  default boolean isSticky() {
    return false;
  };

  /**
   * @return true if getMaxCountThatPassesFilter() is implemented
   */
  default boolean isLimited() {
    return false;
  };

  public interface WithGhostSlots extends IItemFilter {

    void createGhostSlots(@Nonnull NNList<GhostSlot> slots, int xOffset, int yOffset, @Nullable Runnable cb);
  }

  int getSlotCount();

}
