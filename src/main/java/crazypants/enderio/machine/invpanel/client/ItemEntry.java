package crazypants.enderio.machine.invpanel.client;

import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.machine.invpanel.ItemEntryBase;
import java.util.Locale;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

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
    stack.stackTagCompound = nbt;
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
      lowerCaseLocName = StatCollector.translateToLocal(getUnlocName()).toLowerCase(locale);
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
    GameRegistry.UniqueIdentifier id = GameRegistry.findUniqueIdentifierFor(item);
    if (id != null && id.modId != null) {
      modId = id.modId;
    } else {
      modId = "Unknown";
    }
  }

}
