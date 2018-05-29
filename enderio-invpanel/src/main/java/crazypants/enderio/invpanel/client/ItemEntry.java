package crazypants.enderio.invpanel.client;

import java.util.Locale;

import javax.annotation.Nonnull;

import crazypants.enderio.base.invpanel.database.ItemEntryBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;

public class ItemEntry extends ItemEntryBase {
  private String name;
  private String modId;
  private String lowerCaseLocName;
  private int count;
  private @Nonnull ItemStack stack;

  public ItemEntry(int dbID, int hash, int itemID, int meta, NBTTagCompound nbt) {
    super(dbID, hash, itemID, meta, nbt);
    stack = ItemStack.EMPTY;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
    stack = ItemStack.EMPTY;
  }

  @Nonnull
  public ItemStack makeItemStack() {
    if (stack.isEmpty()) {
      stack = new ItemStack(getItem(), getCount(), getMeta());
      stack.setTagCompound(getNbt());
    }
    return stack;
  }

  @Nonnull
  public String getUnlocName() {
    if (name == null) {
      findUnlocName();
    }
    return name;
  }

  @Nonnull
  public String getLowercaseUnlocName(Locale locale) {
    if (lowerCaseLocName == null) {
      lowerCaseLocName = I18n.translateToLocal(getUnlocName()).toLowerCase(locale);
    }
    return lowerCaseLocName;
  }

  private void findUnlocName() {
    ItemStack stack = makeItemStack();
    try {
      name = stack.getDisplayName();
      if (name.isEmpty()) {
        name = stack.getItem().getUnlocalizedName();
        if (name.isEmpty()) {
          name = stack.getItem().getClass().getName();
        }
      }
    } catch (Throwable ex) {
      name = "Exception: " + ex.getMessage();
    }
  }

  @Nonnull
  public String getModId() {
    if (modId == null) {
      findModId();
    }
    return modId;
  }

  private void findModId() {
    Item item = getItem();
    if (item != null) {
      ResourceLocation resourceName = item.delegate.name();
      if (resourceName != null) {
        modId = resourceName.getResourceDomain();
      }
    }
    if (modId == null) {
      modId = "Unknown";
    }
  }

  @Override
  public String toString() {
    return "ItemEntry [name=" + name + ", modId=" + modId + ", lowerCaseLocName=" + lowerCaseLocName + ", count=" + count + ", super=" + super.toString() + "]";
  }

}
