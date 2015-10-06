package crazypants.enderio.conduit.item.filter;

import crazypants.enderio.conduit.gui.GuiExternalConnection;
import crazypants.enderio.conduit.gui.item.IItemFilterGui;
import crazypants.enderio.conduit.gui.item.PowerItemFilterGui;
import crazypants.enderio.conduit.item.IItemConduit;
import io.netty.buffer.ByteBuf;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import cofh.api.energy.IEnergyContainerItem;

import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.network.NetworkUtil;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.conduit.item.NetworkedInventory;

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
  public boolean doesItemPassFilter(NetworkedInventory inv, ItemStack item) {
    if(item != null && item.getItem() instanceof IEnergyContainerItem) {
      IEnergyContainerItem chargable = (IEnergyContainerItem) item.getItem();

      int max = chargable.getMaxEnergyStored(item);
      int cur = chargable.getEnergyStored(item);
      int ref = (int)((long)max * level / MAX_LEVEL);

      switch (mode) {
        case LESS:       return cur <  ref;
        case LESS_EQUAL: return cur <= ref;
        case EQUAL:      return cur == ref;
        case MORE_EQUAL: return cur >= ref;
        case MORE:       return cur >  ref;
        default:         return false;
      }
    }
    return false;
  }

  @Override
  public boolean doesFilterCaptureStack(NetworkedInventory inv, ItemStack item) {
    boolean res = sticky && doesItemPassFilter(inv, item);
    return res;
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
  public void createGhostSlots(List<GhostSlot> slots, int xOffset, int yOffset, Runnable cb) {
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

  @Override
  @SideOnly(Side.CLIENT)
  public IItemFilterGui getGui(GuiExternalConnection gui, IItemConduit itemConduit, boolean isInput) {
    return new PowerItemFilterGui(gui, itemConduit, isInput);
  }

  @Override
  public void readFromNBT(NBTTagCompound nbtRoot) {
    readSettingsFromNBT(nbtRoot);
  }

  protected void readSettingsFromNBT(NBTTagCompound nbtRoot) {
    mode = CmpMode.values()[nbtRoot.getByte("mode") & 255];
    level = nbtRoot.getShort("level");
    sticky = nbtRoot.getBoolean("sticky");
  }

  @Override
  public void writeToNBT(NBTTagCompound nbtRoot) {
    writeSettingToNBT(nbtRoot);
  }

  protected void writeSettingToNBT(NBTTagCompound nbtRoot) {
    nbtRoot.setByte("mode", (byte)mode.ordinal());
    nbtRoot.setShort("level", (short)level);
    nbtRoot.setBoolean("sticky", sticky);
  }

  @Override
  public void writeToByteBuf(ByteBuf buf) {
    NBTTagCompound settingsTag = new NBTTagCompound();
    writeSettingToNBT(settingsTag);
    NetworkUtil.writeNBTTagCompound(settingsTag, buf);
  }

  @Override
  public void readFromByteBuf(ByteBuf buf) {
    NBTTagCompound settingsTag = NetworkUtil.readNBTTagCompound(buf);
    readSettingsFromNBT(settingsTag);
  }
}
