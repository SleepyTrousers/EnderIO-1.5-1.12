package crazypants.enderio.base.filter.filters;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.network.NetworkUtil;

import crazypants.enderio.base.filter.IItemFilter;
import crazypants.enderio.base.filter.INetworkedInventory;
import crazypants.enderio.base.filter.gui.IItemFilterContainer;
import crazypants.enderio.base.filter.gui.IItemFilterGui;
import crazypants.enderio.base.filter.gui.ModItemFilterGui;
import crazypants.enderio.base.gui.GuiContainerBaseEIO;
import crazypants.enderio.util.Prep;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class ModItemFilter implements IItemFilter {

  private final String[] mods = new String[3];
  private boolean blacklist = false;

  public String setMod(int index, ItemStack itemStack) {
    if (index < 0 || index >= mods.length) {
      return null;
    }

    if (itemStack.isEmpty()) {
      setMod(index, (String) null);
      return null;
    }
    ResourceLocation ui = Item.REGISTRY.getNameForObject(itemStack.getItem());
    if (ui == null) {
      setMod(index, (String) null);
      return null;
    }
    String targetMod = ui.getResourceDomain();
    setMod(index, targetMod);
    return targetMod;
  }

  public void setMod(int index, String mod) {
    if (index < 0 || index >= mods.length) {
      return;
    }
    mods[index] = mod;
  }

  public String getModAt(int index) {
    if (index < 0 || index >= mods.length) {
      return null;
    }
    return mods[index];
  }

  public void setBlacklist(boolean value) {
    blacklist = value;
  }

  public boolean isBlacklist() {
    return blacklist;
  }

  @Override
  public boolean doesItemPassFilter(@Nullable INetworkedInventory inv, @Nonnull ItemStack item) {
    if (Prep.isInvalid(item)) {
      return false;
    }

    ResourceLocation ui = Item.REGISTRY.getNameForObject(item.getItem());
    if (ui == null) {
      return false;
    }
    String targetMod = ui.getResourceDomain();
    for (String mod : mods) {
      if (targetMod.equals(mod)) {
        return !blacklist;
      }
    }
    return blacklist;
  }

  @Override
  public boolean isSticky() {
    return false;
  }

  @Override
  public boolean isValid() {
    return true;
  }

  @Override
  public int getSlotCount() {
    return 0;
  }

  // @Override
  // @SideOnly(Side.CLIENT)
  // public IItemFilterGui getGui(GuiExternalConnection gui, IItemConduit itemConduit, boolean isInput) {
  // return new ModItemFilterGui(gui, itemConduit, isInput);
  // }

  @Override
  public void readFromNBT(@Nonnull NBTTagCompound nbtRoot) {
    for (int i = 0; i < mods.length; i++) {
      String mod = nbtRoot.getString("mod" + i);
      if (mod.isEmpty() || "-".equals(mod)) {
        mods[i] = null;
      } else {
        mods[i] = mod;
      }
      if (nbtRoot.hasKey("blacklist")) {
        blacklist = nbtRoot.getBoolean("blacklist");
      } else {
        blacklist = false;
      }
    }
  }

  @Override
  public void writeToNBT(@Nonnull NBTTagCompound nbtRoot) {
    for (int i = 0; i < mods.length; i++) {
      String mod = mods[i];
      if (mod == null || mod.trim().isEmpty()) {
        nbtRoot.setString("mod" + i, "-");
      } else {
        nbtRoot.setString("mod" + i, mod);
      }
    }
    nbtRoot.setBoolean("blacklist", blacklist);
  }

  @Override
  public void writeToByteBuf(@Nonnull ByteBuf buf) {
    NBTTagCompound root = new NBTTagCompound();
    writeToNBT(root);
    NetworkUtil.writeNBTTagCompound(root, buf);
  }

  @Override
  public void readFromByteBuf(@Nonnull ByteBuf buf) {
    NBTTagCompound tag = NetworkUtil.readNBTTagCompound(buf);
    readFromNBT(tag);
  }

  @Override
  public IItemFilterGui getGui(@Nonnull GuiContainerBaseEIO gui, @Nonnull IItemFilterContainer filterContainer, boolean isStickyModeAvailable) {
    return new ModItemFilterGui(gui, filterContainer, isStickyModeAvailable);
  }
}
