package crazypants.enderio.conduit.item.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import crazypants.enderio.conduit.item.NetworkedInventory;

public class ModItemFilter implements IItemFilter {

  private final String[] mods = new String[3];

  public String setMod(int index, ItemStack itemStack) {
    if(index < 0 || index >= mods.length) {
      return null;
    }

    if(itemStack == null || itemStack.getItem() == null) {
      setMod(index, (String) null);
      return null;
    }
    UniqueIdentifier ui = GameRegistry.findUniqueIdentifierFor(itemStack.getItem());
    if(ui == null) {
      setMod(index, (String) null);
      return null;
    }
    String targetMod = ui.modId;
    if(targetMod == null) {
      setMod(index, (String) null);
      return null;
    }
    setMod(index, targetMod);
    return targetMod;
  }

  public void setMod(int index, String mod) {
    if(index < 0 || index >= mods.length) {
      return;
    }
    mods[index] = mod;
  }

  public String getModAt(int index) {
    if(index < 0 || index >= mods.length) {
      return null;
    }
    return mods[index];
  }

  @Override
  public boolean doesItemPassFilter(NetworkedInventory inv, ItemStack item) {
    if(item == null || item.getItem() == null) {
      return false;
    }
    UniqueIdentifier ui = GameRegistry.findUniqueIdentifierFor(item.getItem());
    if(ui == null) {
      return false;
    }
    String targetMod = ui.modId;
    if(targetMod == null) {
      return false;
    }
    for (String mod : mods) {
      if(targetMod.equals(mod)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean doesFilterCaptureStack(NetworkedInventory inv, ItemStack item) {
    return false;
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
  public List<Slot> getSlots() {
    return Collections.emptyList();
  }

  @Override
  public int getSlotCount() {
    return 0;
  }

  @Override
  public void readFromNBT(NBTTagCompound nbtRoot) {
    for (int i = 0; i < mods.length; i++) {
      String mod = nbtRoot.getString("mod" + i);
      if(mod == null || mod.isEmpty() || "-".equals(mod)) {
        mods[i] = null;
      } else {
        mods[i] = mod;
      }
    }
  }

  @Override
  public void writeToNBT(NBTTagCompound nbtRoot) {
    for (int i = 0; i < mods.length; i++) {
      String mod = mods[i];
      if(mod == null || mod.trim().isEmpty()) {
        nbtRoot.setString("mod" + i, "-");
      } else {
        nbtRoot.setString("mod" + i, mod);
      }
    }
  }

}
