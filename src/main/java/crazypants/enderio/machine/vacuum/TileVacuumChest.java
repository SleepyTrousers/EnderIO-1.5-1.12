package crazypants.enderio.machine.vacuum;

import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.annotations.Store.StoreFor;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.ITextComponent;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.common.util.ItemUtil;
import com.google.common.base.Predicate;

import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.TileEntityEio;
import crazypants.enderio.conduit.item.FilterRegister;
import crazypants.enderio.conduit.item.filter.IItemFilter;
import crazypants.enderio.conduit.item.filter.ItemFilter;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.IRedstoneModeControlable;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.paint.YetaUtil;

@Storable
public class TileVacuumChest extends TileEntityEio implements Predicate<EntityItem>, IInventory, IRedstoneModeControlable, IPaintable.IPaintableTileEntity {

  public static final int ITEM_ROWS = 3;
  public static final int ITEM_SLOTS = 9 * ITEM_ROWS;
  public static final int FILTER_SLOTS = 5;

  @Store
  private final ItemStack[] inv = new ItemStack[ITEM_SLOTS];
  @Store
  private int range = Config.vacuumChestRange;
  private ItemFilter filter;
  @Store
  private ItemStack filterItem;

  @Store
  protected RedstoneControlMode redstoneControlMode = RedstoneControlMode.IGNORE;
  protected boolean redstoneCheckPassed;
  private boolean redstoneStateDirty = true;

  @Override
  public void doUpdate() {
    if (worldObj.isRemote) {
      YetaUtil.refresh(this);
    }
    if (redstoneStateDirty) {
      updateRedstoneStatus();
    }
    if (redstoneCheckPassed && !isFull()) {
      doHoover();
    }
  }

  private void updateRedstoneStatus() {
    boolean prevRedstoneCheckPassed = redstoneCheckPassed;
    redstoneCheckPassed = RedstoneControlMode.isConditionMet(redstoneControlMode, this);
    redstoneStateDirty = false;
    if (redstoneCheckPassed != prevRedstoneCheckPassed) {
      updateBlock();
    }
  }

  public void onNeighborBlockChange(Block blockId) {
    redstoneStateDirty = true;
  }

  @Override
  public boolean apply(@Nullable EntityItem entity) {
    if (entity == null || entity.isDead) {
      return false;
    }
    if (entity instanceof IProjectile) {
      return entity.motionY < 0.01;
    }
    return true;
  }

  private void doHoover() {

    int rangeSqr = range * range;
    BoundingBox bb = new BoundingBox(getPos());
    AxisAlignedBB aabb = new AxisAlignedBB(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
    aabb = aabb.expand(range, range, range);
    List<EntityItem> interestingItems = worldObj.getEntitiesWithinAABB(EntityItem.class, aabb, this);

    for (EntityItem entity : interestingItems) {
      if (filter == null || filter.doesItemPassFilter(entity.getEntityItem())) {
        double x = (pos.getX() + 0.5D - entity.posX);
        double y = (pos.getY() + 0.5D - entity.posY);
        double z = (pos.getZ() + 0.5D - entity.posZ);

        double distance = Math.sqrt(x * x + y * y + z * z);
        if (distance < 1.25) {
          hooverEntity(entity);
        } else {
          double speed = 0.06;
          double distScale = 1.0 - Math.min(0.9, distance / rangeSqr);
          distScale *= distScale;

          entity.motionX += x / distance * distScale * speed;
          entity.motionY += y / distance * distScale * 0.2;
          entity.motionZ += z / distance * distScale * speed;
        }
      }
    }
  }

  private void hooverEntity(Entity entity) {
    if (!worldObj.isRemote) {
      if (entity instanceof EntityItem && !entity.isDead) {
        EntityItem item = (EntityItem) entity;
        ItemStack stack = item.getEntityItem().copy();

        int numInserted = ItemUtil.doInsertItem(this, stack, null);
        stack.stackSize -= numInserted;
        item.setEntityItemStack(stack);
        if (stack.stackSize == 0) {
          item.setDead();
        }
      }
    }
  }

  private boolean isFull() {
    for (ItemStack stack : inv) {
      if (stack == null || stack.stackSize < stack.getMaxStackSize()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean isUseableByPlayer(EntityPlayer player) {
    return canPlayerAccess(player);
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
    if (slot < 0 || slot >= inv.length) {
      return null;
    }
    return inv[slot];
  }

  @Override
  public ItemStack decrStackSize(int fromSlot, int amount) {
    ItemStack fromStack = inv[fromSlot];
    if (fromStack == null) {
      return null;
    }
    if (fromStack.stackSize <= amount) {
      inv[fromSlot] = null;
      return fromStack;
    }
    ItemStack result = new ItemStack(fromStack.getItem(), amount, fromStack.getItemDamage());
    if (fromStack.getTagCompound() != null) {
      result.setTagCompound((NBTTagCompound) fromStack.getTagCompound().copy());
    }
    fromStack.stackSize -= amount;
    return result;
  }

  @Override
  public void setInventorySlotContents(int slot, ItemStack contents) {

    if (slot < 0 || slot >= inv.length) {
      System.out.println("TileVacumChest.setInventorySlotContents: " + slot);
      return;
    }

    if (contents == null) {
      inv[slot] = contents;
    } else {
      inv[slot] = contents.copy();
    }

    if (contents != null && contents.stackSize > getInventoryStackLimit()) {
      contents.stackSize = getInventoryStackLimit();
    }
  }

  @Override
  public ItemStack removeStackFromSlot(int index) {
    ItemStack fromStack = inv[index];
    inv[index] = null;
    return fromStack;
  }

  @Override
  public void clear() {
    for (int i = 0; i < inv.length; i++) {
      inv[i] = null;
    }
  }

  @Override
  public String getName() {
    return ModObject.blockVacuumChest.getUnlocalisedName();
  }

  @Override
  public boolean hasCustomName() {
    return false;
  }

  @Override
  public void openInventory(EntityPlayer player) {
  }

  @Override
  public void closeInventory(EntityPlayer player) {
  }

  @Override
  public boolean isItemValidForSlot(int var1, ItemStack var2) {
    return true;
  }

  public boolean isItemValidForFilter(ItemStack itemstack) {
    return itemstack != null && itemstack.getItem() == EnderIO.itemBasicFilterUpgrade && itemstack.getItemDamage() == 0;
  }

  public int getRange() {
    return range;
  }

  private int limitRange(int rangeIn) {
    return Math.max(1, Math.min(Config.vacuumChestRange, rangeIn));
  }

  public void setRange(int range) {
    this.range = limitRange(range);
    updateBlock();
  }

  public ItemStack getFilterItem() {
    return filterItem;
  }

  public void setFilterItem(ItemStack filterItem) {
    IItemFilter newFilter = FilterRegister.getFilterForUpgrade(filterItem);
    if (newFilter == null || newFilter instanceof ItemFilter) {
      this.filterItem = filterItem;
      this.filter = (ItemFilter) newFilter;
      updateBlock();
    }
  }

  public void setFilterBlacklist(boolean isBlacklist) {
    if (filter != null) {
      filter.setBlacklist(isBlacklist);
      updateFilterItem();
    }
  }

  public void setFilterMatchMeta(boolean matchMeta) {
    if (filter != null) {
      filter.setMatchMeta(matchMeta);
      updateFilterItem();
    }
  }

  public boolean hasItemFilter() {
    return filter != null;
  }

  public ItemFilter getItemFilter() {
    return filter;
  }

  public void setItemFilterSlot(int slot, ItemStack stack) {
    if (slot >= 0 && slot < FILTER_SLOTS && filter != null) {
      filter.setInventorySlotContents(slot, stack);
      updateFilterItem();
    }
  }

  private void updateFilterItem() {
    FilterRegister.writeFilterToStack(filter, filterItem);
    updateBlock();
  }

  @Override
  public RedstoneControlMode getRedstoneControlMode() {
    return redstoneControlMode;
  }

  @Override
  public void setRedstoneControlMode(RedstoneControlMode redstoneControlMode) {
    this.redstoneControlMode = redstoneControlMode;
    redstoneStateDirty = true;
    updateBlock();
  }

  @Override
  protected void onAfterDataPacket() {
    refreshFilter();
    updateBlock();
  }

  private void refreshFilter() {
    IItemFilter flt = FilterRegister.getFilterForUpgrade(filterItem);
    if (flt instanceof ItemFilter) {
      filter = (ItemFilter) flt;
    } else {
      filterItem = null;
    }
  }

  @Override
  protected void readCustomNBT(NBTTagCompound root) {
    super.readCustomNBT(root);
    refreshFilter();
  }

  @Override
  public void readContentsFromNBT(NBTTagCompound nbtRoot) {
    super.readContentsFromNBT(nbtRoot);
    refreshFilter();
  }

  @Override
  public ITextComponent getDisplayName() {
    return hasCustomName() ? new TextComponentString(getName()) : new TextComponentTranslation(getName(), new Object[0]);
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
  public boolean equals(@Nullable Object obj) {
    return super.equals(obj);
  }

  @Store({ StoreFor.CLIENT, StoreFor.SAVE })
  protected IBlockState sourceBlock;

  @Override
  public IBlockState getPaintSource() {
    return sourceBlock;
  }

  @Override
  public void setPaintSource(IBlockState sourceBlock) {
    this.sourceBlock = sourceBlock;
    markDirty();
    updateBlock();
  }

}
