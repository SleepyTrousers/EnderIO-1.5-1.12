package crazypants.enderio.conduit.gui.me;

import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import appeng.api.parts.IPartItem;

import com.google.common.collect.Sets;

import crazypants.enderio.conduit.me.IMEConduit;

public class InventoryBus implements IInventory {

  private static Set<Class<?>> supportedParts;

  static {
    supportedParts = Sets.newHashSet();

    try {
      supportedParts.add(Class.forName("appeng.parts.automation.PartImportBus"));
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("Could not load ME conduit GUI", e);
    }
  }

  private ItemStack bus;
  
  private ForgeDirection dir;
  private IMEConduit conduit;
  
  public InventoryBus(IMEConduit conduit, ForgeDirection dir) {
    this.conduit = conduit;
    this.dir = dir;
    
    this.bus = conduit.getBus(dir);
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
      conduit.setBus(bus, dir);
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
      return supportedParts.contains(((IPartItem)stack.getItem()).createPartFromItemStack(stack).getClass());
    }
    return false;
  }

}
