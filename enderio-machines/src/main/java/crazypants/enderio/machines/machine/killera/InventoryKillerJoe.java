package crazypants.enderio.machines.machine.killera;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.util.Prep;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;

public class InventoryKillerJoe extends InventoryPlayer {

  private final @Nonnull TileKillerJoe joe;

  public InventoryKillerJoe(EntityPlayer playerIn, @Nonnull TileKillerJoe killerJoe) {
    super(playerIn);
    this.joe = killerJoe;
  }

  @Override
  public @Nonnull ItemStack getCurrentItem() {
    return joe.getWeapon();
  }

  @Override
  public int getFirstEmptyStack() {
    return -1;
  }

  @Override
  public void setPickedItemStack(@Nonnull ItemStack stack) {
  }

  @Override
  public void pickItem(int index) {
  }

  @Override
  public int getSlotFor(@Nonnull ItemStack stack) {
    return -1;
  }

  @Override
  public int getBestHotbarSlot() {
    return 1;
  }

  @Override
  public void changeCurrentItem(int direction) {
  }

  @Override
  public int clearMatchingItems(@Nullable Item itemIn, int metadataIn, int removeCount, @Nullable NBTTagCompound itemNBT) {
    return 0;
  }

  @Override
  public void decrementAnimations() {
  }

  @Override
  public boolean addItemStackToInventory(@Nonnull ItemStack itemStackIn) {
    return false;
  }

  @Override
  public @Nonnull ItemStack decrStackSize(int index, int count) {
    return Prep.getEmpty();
  }

  @Override
  public void deleteStack(@Nonnull ItemStack stack) {
  }

  @Override
  public @Nonnull ItemStack removeStackFromSlot(int index) {
    return Prep.getEmpty();
  }

  @Override
  public void setInventorySlotContents(int index, @Nonnull ItemStack stack) {
  }

  @Override
  public float getStrVsBlock(@Nonnull IBlockState state) {
    return 1;
  }

  @Override
  public @Nonnull NBTTagList writeToNBT(@Nonnull NBTTagList nbtTagListIn) {
    return nbtTagListIn;
  }

  @Override
  public void readFromNBT(@Nonnull NBTTagList nbtTagListIn) {
  }

  @Override
  public int getSizeInventory() {
    return 1;
  }

  @Override
  public boolean isEmpty() {
    return Prep.isInvalid(joe.getWeapon());
  }

  @Override
  public @Nonnull ItemStack getStackInSlot(int index) {
    return joe.getWeapon();
  }

  @Override
  public @Nonnull String getName() {
    return super.getName();
  }

  @Override
  public boolean hasCustomName() {
    return super.hasCustomName();
  }

  @Override
  public @Nonnull ITextComponent getDisplayName() {
    return super.getDisplayName();
  }

  @Override
  public int getInventoryStackLimit() {
    return 1;
  }

  @Override
  public boolean canHarvestBlock(@Nonnull IBlockState state) {
    return false;
  }

  @Override
  public @Nonnull ItemStack armorItemInSlot(int slotIn) {
    return Prep.getEmpty();
  }

  @Override
  public void damageArmor(float damage) {
  }

  @Override
  public void dropAllItems() {
  }

  @Override
  public void markDirty() {
    joe.markDirty();
  }

  @Override
  public void setItemStack(@Nonnull ItemStack itemStackIn) {
  }

  @Override
  public @Nonnull ItemStack getItemStack() {
    return Prep.getEmpty();
  }

  @Override
  public boolean isUsableByPlayer(@Nonnull EntityPlayer playerIn) {
    return false;
  }

  @Override
  public boolean hasItemStack(@Nonnull ItemStack itemStackIn) {
    return itemStackIn.isItemEqual(joe.getWeapon());
  }

  @Override
  public void openInventory(@Nonnull EntityPlayer playerIn) {
  }

  @Override
  public void closeInventory(@Nonnull EntityPlayer playerIn) {
  }

  @Override
  public boolean isItemValidForSlot(int index, @Nonnull ItemStack stack) {
    return false;
  }

  @Override
  public void copyInventory(@Nonnull InventoryPlayer playerInventory) {
  }

  @Override
  public int getField(int id) {
    return 0;
  }

  @Override
  public void setField(int id, int value) {
  }

  @Override
  public int getFieldCount() {
    return 0;
  }

  @Override
  public void clear() {
  }

}
