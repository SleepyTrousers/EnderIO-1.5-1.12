package crazypants.enderio.machine.vacuum;

import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.render.BoundingBox;
import crazypants.util.BlockCoord;
import crazypants.util.ItemUtil;

public class TileVacuumChest extends TileEntity implements  IEntitySelector, IInventory {

  
  private static final double RANGE = Config.vacuumChestRange;
  private ItemStack[] inv = new ItemStack[27];

  @Override
  public void updateEntity() {
    if(!isFull()) {
      doHoover();
    }
  }
  
  @Override
  public boolean isEntityApplicable(Entity entity) {
    if(entity.isDead) {
      return false;
    }
    if(entity instanceof IProjectile) {
      return entity.motionY < 0.01;
    }
    if(entity instanceof EntityItem) {
      ItemStack stack = ((EntityItem) entity).getEntityItem();
      return true;
    }
    return false;
  }
  

  private void doHoover() {

    BoundingBox bb = new BoundingBox(getLocation());
    AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
    aabb = aabb.expand(RANGE, RANGE, RANGE);
    List<EntityItem> interestingItems = worldObj.selectEntitiesWithinAABB(EntityItem.class, aabb, this);

    for (EntityItem entity : interestingItems) {
      double x = (xCoord + 0.5D - entity.posX);
      double y = (yCoord + 0.5D - entity.posY);
      double z = (zCoord + 0.5D - entity.posZ);

      double distance = Math.sqrt(x * x + y * y + z * z);
      if(distance < 1.25) {
        hooverEntity(entity);
      } else {
        double speed = 0.035;
        entity.motionX += x / distance * speed;
        entity.motionY += y * speed;
        if(y > 0) {
          entity.motionY = 0.12;
        }
        entity.motionZ += z / distance * speed;
      }

    }
  }
  
  private void hooverEntity(Entity entity) {
    if(!worldObj.isRemote) {
      if(entity instanceof EntityItem && !entity.isDead) {
        EntityItem item = (EntityItem) entity;
        ItemStack stack = item.getEntityItem().copy();

        int numInserted = ItemUtil.doInsertItem(this, stack);
        stack.stackSize -= numInserted;
        item.setEntityItemStack(stack);
        if(stack.stackSize == 0) {
          item.setDead();
        }
      }
    }
  }
  
  private boolean isFull() {
    for(ItemStack stack : inv) {
      if(stack == null || stack.stackSize < stack.getMaxStackSize()) {
        return false;
      }
    }
    return true;
  }

  private BlockCoord getLocation() {
    return new BlockCoord(this);
  }

  @Override
  public int getSizeInventory() {
    return inv.length;
  }

  @Override
  public int getInventoryStackLimit() {
    return 64;
  }

  @Override
  public ItemStack getStackInSlot(int slot) {
    if(slot < 0 || slot >= inv.length) {
      return null;
    }
    return inv[slot];
  }

  @Override
  public ItemStack decrStackSize(int fromSlot, int amount) {
    ItemStack fromStack = inv[fromSlot];
    if(fromStack == null) {
      return null;
    }
    if(fromStack.stackSize <= amount) {
      inv[fromSlot] = null;
      return fromStack;
    }
    ItemStack result = new ItemStack(fromStack.getItem(), amount, fromStack.getItemDamage());
    if(fromStack.stackTagCompound != null) {
      result.stackTagCompound = (NBTTagCompound) fromStack.stackTagCompound.copy();
    }
    fromStack.stackSize -= amount;
    return result;
  }

  @Override
  public void setInventorySlotContents(int slot, ItemStack contents) {
    
    if(slot < 0 || slot >= inv.length) {
      System.out.println("TileVacumChest.setInventorySlotContents: " + slot);
      return;
    }
    
    if(contents == null) {
      inv[slot] = contents;
    } else {
      inv[slot] = contents.copy();
    }

    if(contents != null && contents.stackSize > getInventoryStackLimit()) {
      contents.stackSize = getInventoryStackLimit();
    }
  }

  @Override
  public ItemStack getStackInSlotOnClosing(int var1) {
    return null;
  }
 
  @Override
  public String getInventoryName() {    
    return ModObject.blockVacuumChest.unlocalisedName;
  }

  @Override
  public boolean hasCustomInventoryName() {
    return false;
  }

  @Override
  public boolean isUseableByPlayer(EntityPlayer var1) {    
    return true;
  }

  @Override
  public void openInventory() {    
  }

  @Override
  public void closeInventory() {    
  }

  @Override
  public boolean isItemValidForSlot(int var1, ItemStack var2) {
    return true;
  }

  @Override
  public void readFromNBT(NBTTagCompound nbtRoot) {
    super.readFromNBT(nbtRoot);    
    readContentsFromNBT(nbtRoot);
  }
  
  public void readContentsFromNBT(NBTTagCompound nbtRoot) {
    NBTTagList itemList = (NBTTagList) nbtRoot.getTag("Items");
    if(itemList != null) {
      for (int i = 0; i < itemList.tagCount(); i++) {
        NBTTagCompound itemStack = itemList.getCompoundTagAt(i);
        byte slot = itemStack.getByte("Slot");
        if(slot >= 0 && slot < inv.length) {
          inv[slot] = ItemStack.loadItemStackFromNBT(itemStack);
        }
      }
    }
  }

  @Override
  public void writeToNBT(NBTTagCompound nbtRoot) {    
    super.writeToNBT(nbtRoot);
    writeContentsToNBT(nbtRoot);
  } 
  
  public void writeContentsToNBT(NBTTagCompound nbtRoot) {
    NBTTagList itemList = new NBTTagList();
    for (int i = 0; i < inv.length; i++) {
      if(inv[i] != null) {
        NBTTagCompound itemStackNBT = new NBTTagCompound();
        itemStackNBT.setByte("Slot", (byte) i);
        inv[i].writeToNBT(itemStackNBT);
        itemList.appendTag(itemStackNBT);
      }
    }
    nbtRoot.setTag("Items", itemList);
  }
    
}
