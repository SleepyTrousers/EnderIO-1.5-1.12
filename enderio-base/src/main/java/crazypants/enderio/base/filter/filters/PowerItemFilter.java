package crazypants.enderio.base.filter.filters;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.network.NetworkUtil;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.filter.IItemFilter;
import crazypants.enderio.base.filter.INetworkedInventory;
import crazypants.enderio.base.filter.gui.IItemFilterContainer;
import crazypants.enderio.base.filter.gui.IItemFilterGui;
import crazypants.enderio.base.filter.gui.PowerItemFilterGui;
import crazypants.enderio.base.gui.GuiContainerBaseEIO;
import crazypants.enderio.base.power.PowerHandlerUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.IEnergyStorage;

/**
 *
 * @author matthias
 */
public class PowerItemFilter implements IItemFilter {

  public enum CmpMode {
    LESS,
    LESS_EQUAL,
    EQUAL,
    MORE_EQUAL,
    MORE;

    public CmpMode next() {
      CmpMode[] values = values();
      return values[(ordinal() + 1) % values.length];
    }
  }

  public static final int MAX_LEVEL = 4;

  CmpMode mode = CmpMode.EQUAL;
  boolean sticky;
  int level = MAX_LEVEL;

  @Override
  public boolean doesItemPassFilter(@Nullable INetworkedInventory inv, @Nonnull ItemStack item) {
    IEnergyStorage chargable = PowerHandlerUtil.getCapability(item, null);
    if (chargable != null) {
      int max = chargable.getMaxEnergyStored();
      int cur = chargable.getEnergyStored();
      int ref = (int) ((long) max * level / MAX_LEVEL);

      switch (mode) {
      case LESS:
        return cur < ref;
      case LESS_EQUAL:
        return cur <= ref;
      case EQUAL:
        return cur == ref;
      case MORE_EQUAL:
        return cur >= ref;
      case MORE:
        return cur > ref;
      default:
        return false;
      }
    }
    return false;
  }

  @Override
  public boolean isValid() {
    return true;
  }

  @Override
  public boolean isSticky() {
    return sticky;
  }

  public void setSticky(boolean sticky) {
    this.sticky = sticky;
  }

  @Override
  public void createGhostSlots(@Nonnull NNList<GhostSlot> slots, int xOffset, int yOffset, @Nullable Runnable cb) {
  }

  @Override
  public int getSlotCount() {
    return 0;
  }

  public CmpMode getMode() {
    return mode;
  }

  public void setMode(CmpMode mode) {
    this.mode = mode;
  }

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  // @Override
  // @SideOnly(Side.CLIENT)
  // public IItemFilterGui getGui(GuiExternalConnection gui, IItemConduit itemConduit, boolean isInput) {
  // return new PowerItemFilterGui(gui, itemConduit, isInput);
  // }

  @Override
  public void readFromNBT(@Nonnull NBTTagCompound nbtRoot) {
    readSettingsFromNBT(nbtRoot);
  }

  protected void readSettingsFromNBT(NBTTagCompound nbtRoot) {
    mode = CmpMode.values()[nbtRoot.getByte("mode") & 255];
    level = nbtRoot.getShort("level");
    sticky = nbtRoot.getBoolean("sticky");
  }

  @Override
  public void writeToNBT(@Nonnull NBTTagCompound nbtRoot) {
    writeSettingToNBT(nbtRoot);
  }

  protected void writeSettingToNBT(NBTTagCompound nbtRoot) {
    nbtRoot.setByte("mode", (byte) mode.ordinal());
    nbtRoot.setShort("level", (short) level);
    nbtRoot.setBoolean("sticky", sticky);
  }

  @Override
  public void writeToByteBuf(@Nonnull ByteBuf buf) {
    NBTTagCompound settingsTag = new NBTTagCompound();
    writeSettingToNBT(settingsTag);
    NetworkUtil.writeNBTTagCompound(settingsTag, buf);
  }

  @Override
  public void readFromByteBuf(@Nonnull ByteBuf buf) {
    NBTTagCompound settingsTag = NetworkUtil.readNBTTagCompound(buf);
    readSettingsFromNBT(settingsTag);
  }

  @Override
  public IItemFilterGui getGui(@Nonnull GuiContainerBaseEIO gui, @Nonnull IItemFilterContainer filterContainer, boolean isStickyModeAvailable, int xOffset,
      int yOffset) {
    return new PowerItemFilterGui(gui, filterContainer, isStickyModeAvailable, xOffset, yOffset);
  }
}
