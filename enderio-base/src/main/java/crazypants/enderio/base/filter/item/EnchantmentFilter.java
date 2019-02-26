package crazypants.enderio.base.filter.item;

import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.network.NetworkUtil;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.autosave.BaseHandlers;
import crazypants.enderio.base.integration.jei.IHaveGhostTargets.IEnchantmentGhostSlot;
import crazypants.enderio.util.Prep;
import info.loenwind.autosave.Reader;
import info.loenwind.autosave.Writer;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import io.netty.buffer.ByteBuf;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandler;

@Storable
public class EnchantmentFilter implements IItemFilter.WithGhostSlots {

  public static final int GUI_ROWS = 5;

  @Store
  private final @Nonnull NNList<Enchantment> enchantments = new NNList<>();
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

  public NNList<Enchantment> getEnchantments() {
    return enchantments;
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
    return enchantments.isEmpty();
  }

  @Override
  public boolean doesItemPassFilter(@Nullable IItemHandler inv, @Nonnull ItemStack item) {
    Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(item);
    if (!map.isEmpty()) {
      for (Enchantment enchantment : enchantments) {
        if (map.containsKey(enchantment)) {
          return !blacklist;
        }
      }
    }
    return blacklist;
  }

  @Override
  public void setInventorySlotContents(int slot, @Nonnull ItemStack stack) {
    Set<Enchantment> set = EnchantmentHelper.getEnchantments(stack).keySet();
    if (!set.isEmpty()) {
      ENCH: for (Enchantment input : set) {
        for (Enchantment existing : enchantments) {
          if (existing == input) {
            continue ENCH;
          }
        }
        if (slot < enchantments.size()) {
          enchantments.set(slot, input);
        } else {
          enchantments.add(input);
        }
        slot = Integer.MAX_VALUE;
      }
      while (enchantments.size() > slotCount) {
        enchantments.remove(slotCount);
      }
    } else if (slot < enchantments.size()) {
      enchantments.remove(slot);
    }
  }

  public void setEnchantment(int slot, @Nonnull Enchantment enchantment) {
    if (!enchantments.contains(enchantment)) {
      if (slot < enchantments.size()) {
        enchantments.set(slot, enchantment);
      } else {
        enchantments.add(enchantment);
      }
      while (enchantments.size() > slotCount) {
        enchantments.remove(slotCount);
      }
    } else if (slot < enchantments.size()) {
      enchantments.remove(slot);
    }
  }

  @Override
  @Nonnull
  public ItemStack getInventorySlotContents(int slot) {
    if (slot < enchantments.size()) {
      EnchantmentData enchantmentData = new EnchantmentData(enchantments.get(slot), 1);
      ItemStack output = new ItemStack(Items.ENCHANTED_BOOK);
      ItemEnchantedBook.addEnchantment(output, enchantmentData);
      return output;
    }
    return Prep.getEmpty();
  }

  @Override
  public void createGhostSlots(@Nonnull NNList<GhostSlot> slots, int xOffset, int yOffset, @Nullable Runnable cb) {
    int row = 0, col = 0;
    for (int i = 0; i < slotCount; i++) {
      int x = xOffset + col * 81;
      int y = yOffset + row * 18;

      slots.add(new EnchantmentFilterGhostSlot(i, x, y, cb));

      row++;
      if (row >= GUI_ROWS) {
        row = 0;
        col++;
      }
    }
  }

  public class EnchantmentFilterGhostSlot extends GhostSlot implements IEnchantmentGhostSlot {
    private final Runnable cb;

    EnchantmentFilterGhostSlot(int slot, int x, int y, Runnable cb) {
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

    @Override
    public void putEnchantment(@Nonnull Enchantment enchantment) {
      setEnchantment(getSlot(), enchantment);
      cb.run();
    }
  }

}
