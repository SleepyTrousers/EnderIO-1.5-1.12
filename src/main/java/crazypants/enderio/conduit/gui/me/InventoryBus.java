package crazypants.enderio.conduit.gui.me;

import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import appeng.api.parts.IPartItem;

import com.google.common.collect.Sets;

import crazypants.enderio.conduit.me.IMEConduit;
import crazypants.enderio.conduit.me.MEUtil;

public class InventoryBus implements IInventory {

  private ItemStack bus;
  
  private ForgeDirection dir;
  private IMEConduit conduit;
  
  private EntityPlayer player;
  
  public InventoryBus(EntityPlayer player, IMEConduit conduit, ForgeDirection dir) {
    this.conduit = conduit;
    this.dir = dir;
    this.player = player;
    
    this.bus = conduit.getPartStack(dir);
  }

  @Override
  public int getSizeInventory() {
    return 1;
  }

  @Override
  public ItemStack getStackInSlot(int idx) {
    return idx == 0 ? bus : null;
  }

  @Override
  public ItemStack decrStackSize(int slot, int amnt) {
    ItemStack ret = bus.copy();
    setInventorySlotContents(0, null);
    return ret;
  }

  @Override
  public ItemStack getStackInSlotOnClosing(int slot) {
    return null;
  }

  @Override
  public void setInventorySlotContents(int slot, ItemStack stack) {
    if(slot == 0) {
      bus = stack;
      conduit.setPart(player, bus, dir);
    }
  }

  @Override
  public String getInventoryName() {
    return "MEBus";
  }

  @Override
  public boolean hasCustomInventoryName() {
    return false;
  }

  @Override
  public int getInventoryStackLimit() {
    return 1;
  }

  @Override
  public void markDirty() {
    conduit.getBundle().getEntity().markDirty();
  }

  @Override
  public boolean isUseableByPlayer(EntityPlayer player) {
    return true;
  }

  @Override
  public void openInventory() {
  }

  @Override
  public void closeInventory() {
  }

  @Override
  public boolean isItemValidForSlot(int slot, ItemStack stack) {
    if(slot == 0 && stack.getItem() instanceof IPartItem) {
      return MEUtil.isSupportedPart(stack);
    }
    return false;
  }

}
