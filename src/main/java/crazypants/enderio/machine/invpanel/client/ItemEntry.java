package crazypants.enderio.machine.invpanel.client;

import java.util.Locale;

import crazypants.enderio.machine.invpanel.ItemEntryBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;

public class ItemEntry extends ItemEntryBase {
  String name;
  String modId;
  String lowerCaseLocName;
  int count;

  public ItemEntry(int dbID, int hash, int itemID, int meta, NBTTagCompound nbt) {
    super(dbID, hash, itemID, meta, nbt);
  }

  public int getCount() {
    return count;
  }

  public ItemStack makeItemStack() {
    ItemStack stack = new ItemStack(getItem(), count, meta);
    stack.setTagCompound(nbt);
    return stack;
  }

  public String getUnlocName() {
    if (name == null) {
      findUnlocName();
    }
    return name;
  }

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
      if (name == null || name.isEmpty()) {
        name = stack.getItem().getUnlocalizedName();
        if (name == null || name.isEmpty()) {
          name = stack.getItem().getClass().getName();
        }
      }
    } catch (Throwable ex) {
      name = "Exception: " + ex.getMessage();
    }
  }

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
    modId = "Unknown";
  }

  @Override
  public String toString() {
    return "ItemEntry [name=" + name + ", modId=" + modId + ", lowerCaseLocName=" + lowerCaseLocName + ", count=" + count + ", super=" + super.toString() + "]";
  }

}
