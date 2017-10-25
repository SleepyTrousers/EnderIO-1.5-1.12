package crazypants.enderio.filter.filters;

import java.util.List;

import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.network.NetworkUtil;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.filter.IItemFilter;
import crazypants.enderio.filter.INetworkedInventory;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModItemFilter implements IItemFilter {

  private final String[] mods = new String[3];
  private boolean blacklist = false;

  public String setMod(int index, ItemStack itemStack) {
    if(index < 0 || index >= mods.length) {
      return null;
    }

    if(itemStack.isEmpty()) {
      setMod(index, (String) null);
      return null;
    }        
    ResourceLocation ui = Item.REGISTRY.getNameForObject(itemStack.getItem());    
    if(ui == null) {
      setMod(index, (String) null);
      return null;
    }
    String targetMod = ui.getResourceDomain();
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

  public void setBlacklist(boolean value) {
    blacklist = value;
  }

  public boolean isBlacklist() {
    return blacklist;
  }

  @Override
  public boolean doesItemPassFilter(INetworkedInventory inv, ItemStack item) {
    if(item == null || item.getItem() == null) {
      return false;
    }
    
    ResourceLocation ui = Item.REGISTRY.getNameForObject(item.getItem());
    if(ui == null) {
      return false;
    }
    String targetMod = ui.getResourceDomain();
    if (targetMod != null) {
      for (String mod : mods) {
        if (targetMod.equals(mod)) {
          return !blacklist;
        }
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
  public void createGhostSlots(NNList<GhostSlot> slots, int xOffset, int yOffset, Runnable cb) {
  }

  @Override
  public int getSlotCount() {
    return 0;
  }

//  @Override
//  @SideOnly(Side.CLIENT)
//  public IItemFilterGui getGui(GuiExternalConnection gui, IItemConduit itemConduit, boolean isInput) {
//    return new ModItemFilterGui(gui, itemConduit, isInput);
//  }

  @Override
  public void readFromNBT(NBTTagCompound nbtRoot) {
    for (int i = 0; i < mods.length; i++) {
      String mod = nbtRoot.getString("mod" + i);
      if(mod == null || mod.isEmpty() || "-".equals(mod)) {
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
  public void writeToNBT(NBTTagCompound nbtRoot) {
    for (int i = 0; i < mods.length; i++) {
      String mod = mods[i];
      if(mod == null || mod.trim().isEmpty()) {
        nbtRoot.setString("mod" + i, "-");
      } else {
        nbtRoot.setString("mod" + i, mod);
      }
    }
    nbtRoot.setBoolean("blacklist", blacklist);
  }

  @Override
  public void writeToByteBuf(ByteBuf buf) {
    NBTTagCompound root = new NBTTagCompound();
    writeToNBT(root);
    NetworkUtil.writeNBTTagCompound(root, buf);
  }

  @Override
  public void readFromByteBuf(ByteBuf buf) {
    NBTTagCompound tag = NetworkUtil.readNBTTagCompound(buf);
    readFromNBT(tag);
  }

}
