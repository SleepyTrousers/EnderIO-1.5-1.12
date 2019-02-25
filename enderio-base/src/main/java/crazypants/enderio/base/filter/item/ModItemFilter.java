package crazypants.enderio.base.filter.item;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.network.NetworkUtil;

import com.enderio.core.common.util.NNList;
import crazypants.enderio.util.Prep;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandler;

public class ModItemFilter implements IItemFilter.WithGhostSlots {

  private final String[] mods = new String[3];
  private boolean blacklist = false;

  public String setMod(int index, @Nonnull ItemStack itemStack) {
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
  public boolean doesItemPassFilter(@Nullable IItemHandler inventory, @Nonnull ItemStack item) {
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
  public void setInventorySlotContents(int slot, @Nonnull ItemStack stack) {
    setMod(slot, stack);
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
  public boolean isEmpty() {
    return true;
  }

  @Override
  public void createGhostSlots(@Nonnull NNList<GhostSlot> slots, int xOffset, int yOffset, @Nullable Runnable cb) {
    int index = 0;
    for (int row = 0; row < mods.length; row++) {
      slots.add(new ModFilterGhostSlot(index, xOffset, yOffset + 22 * row, cb));
      index++;
    }
  }

  class ModFilterGhostSlot extends GhostSlot {
    private final Runnable cb;

    ModFilterGhostSlot(int slot, int x, int y, Runnable cb) {
      this.setX(x);
      this.setY(y);
      this.setSlot(slot);
      this.cb = cb;
    }

    @Override
    public void putStack(@Nonnull ItemStack stack, int realsize) {
      setInventorySlotContents(getSlot(), stack);
      cb.run();
    }

    @Override
    public @Nonnull ItemStack getStack() {
      return Prep.getEmpty();
    }
  }
}
