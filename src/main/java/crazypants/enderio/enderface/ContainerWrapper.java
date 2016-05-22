package crazypants.enderio.enderface;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerWrapper extends net.minecraft.inventory.Container {

  public net.minecraft.inventory.Container wrapped;

  public ContainerWrapper(net.minecraft.inventory.Container wrapped) {
    this.wrapped = wrapped;
    this.inventoryItemStacks = wrapped.inventoryItemStacks;
    this.inventorySlots = wrapped.inventorySlots;
    this.windowId = wrapped.windowId;
  }

  @Override
  public boolean canDragIntoSlot(Slot par1Slot) {
    return wrapped.canDragIntoSlot(par1Slot);
  }

  @Override
  public boolean canInteractWith(net.minecraft.entity.player.EntityPlayer arg0) {
    return true;
  }

  @Override
  public net.minecraft.inventory.Slot getSlot(int arg0) {
    return wrapped.getSlot(arg0);
  }
  
  @Override
  public List<ItemStack> getInventory() {
    return wrapped.getInventory();
  }

  @Override
  public boolean enchantItem(net.minecraft.entity.player.EntityPlayer arg0, int arg1) {
    return wrapped.enchantItem(arg0, arg1);
  }

  
  
  @Override
  public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {    
    return wrapped.slotClick(slotId, dragType, clickTypeIn, player);
  }

  @Override
  public void detectAndSendChanges() {    
    wrapped.detectAndSendChanges();
  }

  @Override
  public void addListener(IContainerListener listener) {
    wrapped.addListener(listener);
  }
  
  @SideOnly(Side.CLIENT)
  public void removeListener(IContainerListener listener) {    
    wrapped.removeListener(listener);    
  }

  @Override
  public net.minecraft.inventory.Slot getSlotFromInventory(net.minecraft.inventory.IInventory arg0, int arg1) {
    return wrapped.getSlotFromInventory(arg0, arg1);
  }

  @Override
  public net.minecraft.item.ItemStack transferStackInSlot(net.minecraft.entity.player.EntityPlayer arg0, int arg1) {
    return wrapped.transferStackInSlot(arg0, arg1);
  }

  @Override
  public void onCraftMatrixChanged(net.minecraft.inventory.IInventory arg0) {
    wrapped.onCraftMatrixChanged(arg0);
  }

  @Override
  public void putStackInSlot(int arg0, net.minecraft.item.ItemStack arg1) {
    wrapped.putStackInSlot(arg0, arg1);
  }

  @Override
  public void putStacksInSlots(net.minecraft.item.ItemStack[] arg0) {
    wrapped.putStacksInSlots(arg0);
  }

  @Override
  public void updateProgressBar(int arg0, int arg1) {
    wrapped.updateProgressBar(arg0, arg1);
  }

  @Override
  public short getNextTransactionID(net.minecraft.entity.player.InventoryPlayer arg0) {
    return wrapped.getNextTransactionID(arg0);
  }

  @Override
  public boolean canMergeSlot(ItemStack stack, Slot p_94530_2_) {
    return wrapped.canMergeSlot(stack, p_94530_2_);
  }

  @Override
  public void onContainerClosed(EntityPlayer playerIn) {
    wrapped.onContainerClosed(playerIn);
  }

  @Override
  public boolean getCanCraft(EntityPlayer p_75129_1_) {
    return wrapped.getCanCraft(p_75129_1_);
  }

  @Override
  public void setCanCraft(EntityPlayer p_75128_1_, boolean p_75128_2_) {
    wrapped.setCanCraft(p_75128_1_, p_75128_2_);
  }

  

}
