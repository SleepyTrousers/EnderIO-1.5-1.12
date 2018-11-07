package crazypants.enderio.base.filter.item;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.network.NetworkUtil;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.autosave.BaseHandlers;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.util.CapturedMob;
import crazypants.enderio.util.Prep;
import info.loenwind.autosave.Reader;
import info.loenwind.autosave.Writer;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandler;

@Storable
public class SoulFilter implements IItemFilter.WithGhostSlots {

  public static final int GUI_ROWS = 5;

  @Store
  private final @Nonnull NNList<CapturedMob> souls = new NNList<>();
  @Store
  private boolean blacklist = false;
  @Store
  private boolean sticky = false;
  @Store
  private int slotCount = 9;

  @Override
  public boolean isValid() {
    return !isEmpty();
  }

  @Override
  public int getSlotCount() {
    return slotCount;
  }

  @Override
  public boolean isSticky() {
    return sticky;
  }

  public boolean isBlacklist() {
    return blacklist;
  }

  public void setBlacklist(boolean blacklist) {
    this.blacklist = blacklist;
  }

  public void setSticky(boolean sticky) {
    this.sticky = sticky;
  }

  public void setSlotCount(int slotCount) {
    this.slotCount = slotCount;
  }

  public NNList<CapturedMob> getSouls() {
    return souls;
  }

  @Override
  public void readFromNBT(@Nonnull NBTTagCompound nbtRoot) {
    Reader.read(BaseHandlers.REGISTRY, nbtRoot, this);
  }

  @Override
  public void writeToNBT(@Nonnull NBTTagCompound nbtRoot) {
    Writer.write(BaseHandlers.REGISTRY, nbtRoot, this);
  }

  @Override
  public void writeToByteBuf(@Nonnull ByteBuf buf) {
    NBTTagCompound tag = new NBTTagCompound();
    writeToNBT(tag);
    NetworkUtil.writeNBTTagCompound(tag, buf);
  }

  @Override
  public void readFromByteBuf(@Nonnull ByteBuf buf) {
    readFromNBT(NetworkUtil.readNBTTagCompound(buf));
  }

  @Override
  public boolean isEmpty() {
    return souls.isEmpty();
  }

  @Override
  public boolean doesItemPassFilter(@Nullable IItemHandler inv, @Nonnull ItemStack item) {
    if (CapturedMob.containsSoul(item)) {
      CapturedMob input = CapturedMob.create(item);
      for (CapturedMob mob : souls) {
        if (mob.isSameType(input)) {
          return !blacklist;
        }
      }
    }
    return blacklist;
  }

  @Override
  public void setInventorySlotContents(int slot, @Nonnull ItemStack stack) {
    CapturedMob input = null;
    if (CapturedMob.containsSoul(stack)) {
      input = CapturedMob.create(stack);
    }
    if (input != null) {
      if (slot < souls.size()) {
        souls.set(slot, input);
        for (CapturedMob existing : souls) {
          if (existing != input && existing.isSameType(input)) {
            souls.remove(slot);
            return; // CME!
          }
        }
      } else {
        for (CapturedMob existing : souls) {
          if (existing.isSameType(input)) {
            return;
          }
        }
        souls.add(input);
      }
    } else if (slot < souls.size()) {
      souls.remove(slot);
    }
  }

  @Override
  @Nonnull
  public ItemStack getInventorySlotContents(int slot) {
    if (slot < souls.size()) {
      return souls.get(slot).toGenericStack(ModObject.itemSoulVial.getItemNN(), 1, 1);
    }
    return Prep.getEmpty();
  }

  @Override
  public void createGhostSlots(@Nonnull NNList<GhostSlot> slots, int xOffset, int yOffset, @Nullable Runnable cb) {
    int row = 0, col = 0;
    for (int i = 0; i < slotCount; i++) {
      int x = xOffset + col * 81;
      int y = yOffset + row * 18;

      slots.add(new SoulFilterGhostSlot(i, x, y, cb));

      row++;
      if (row >= GUI_ROWS) {
        row = 0;
        col++;
      }
    }
  }

  public class SoulFilterGhostSlot extends GhostSlot {
    private final Runnable cb;

    SoulFilterGhostSlot(int slot, int x, int y, Runnable cb) {
      this.setX(x);
      this.setY(y);
      this.setSlot(slot);
      this.cb = cb;
      this.setDisplayStdOverlay(false);
      this.setStackSizeLimit(1);
    }

    @Override
    public void putStack(@Nonnull ItemStack stack, int realsize) {
      setInventorySlotContents(getSlot(), stack);
      cb.run();
    }

    @Override
    public @Nonnull ItemStack getStack() {
      return getInventorySlotContents(getSlot());
    }
  }

}
