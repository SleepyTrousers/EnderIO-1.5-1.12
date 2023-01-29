package crazypants.enderio.machine.vacuum;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.common.util.ItemUtil;

import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.TileEntityEio;
import crazypants.enderio.conduit.item.FilterRegister;
import crazypants.enderio.conduit.item.filter.IItemFilter;
import crazypants.enderio.conduit.item.filter.ItemFilter;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.IRedstoneModeControlable;
import crazypants.enderio.machine.RedstoneControlMode;

public class TileVacuumChest extends TileEntityEio implements IEntitySelector, IInventory, IRedstoneModeControlable {

    public static final int ITEM_ROWS = 3;
    public static final int ITEM_SLOTS = 9 * ITEM_ROWS;
    public static final int FILTER_SLOTS = 5;

    private final ItemStack[] inv = new ItemStack[ITEM_SLOTS];
    private int range = Config.vacuumChestRange;
    private ItemFilter filter;
    private ItemStack filterItem;

    protected RedstoneControlMode redstoneControlMode = RedstoneControlMode.IGNORE;
    protected boolean redstoneCheckPassed;
    private boolean redstoneStateDirty = true;

    @Override
    public void doUpdate() {
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
    public boolean isEntityApplicable(Entity entity) {
        if (entity.isDead) {
            return false;
        }
        if (entity instanceof IProjectile) {
            return entity.motionY < 0.01;
        }
        if (entity instanceof EntityItem) {
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private void doHoover() {

        int rangeSqr = range * range;
        BoundingBox bb = new BoundingBox(getLocation());
        AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
        aabb = aabb.expand(range, range, range);
        List<EntityItem> interestingItems = worldObj.selectEntitiesWithinAABB(EntityItem.class, aabb, this);

        for (EntityItem entity : interestingItems) {
            if (filter == null || filter.doesItemPassFilter(entity.getEntityItem())) {
                double x = (xCoord + 0.5D - entity.posX);
                double y = (yCoord + 0.5D - entity.posY);
                double z = (zCoord + 0.5D - entity.posZ);

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
        if (fromStack.stackTagCompound != null) {
            result.stackTagCompound = (NBTTagCompound) fromStack.stackTagCompound.copy();
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
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int var1, ItemStack var2) {
        return true;
    }

    public boolean isItemValidForFilter(ItemStack itemstack) {
        return itemstack != null && itemstack.getItem() == EnderIO.itemBasicFilterUpgrade
                && itemstack.getItemDamage() == 0;
    }

    public int getRange() {
        return range;
    }

    private int limitRange(int range) {
        return Math.max(1, Math.min(Config.vacuumChestRange, range));
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
    public void readCustomNBT(NBTTagCompound nbtRoot) {
        readContentsFromNBT(nbtRoot);
        redstoneCheckPassed = nbtRoot.getBoolean("redstoneCheckPassed");
    }

    public void readContentsFromNBT(NBTTagCompound nbtRoot) {
        NBTTagList itemList = (NBTTagList) nbtRoot.getTag("Items");
        if (itemList != null) {
            for (int i = 0; i < itemList.tagCount(); i++) {
                NBTTagCompound itemStack = itemList.getCompoundTagAt(i);
                byte slot = itemStack.getByte("Slot");
                if (slot >= 0 && slot < inv.length) {
                    inv[slot] = ItemStack.loadItemStackFromNBT(itemStack);
                }
            }
        }
        if (nbtRoot.hasKey("range")) {
            range = limitRange(nbtRoot.getInteger("range"));
        } else {
            range = Config.vacuumChestRange;
        }
        if (nbtRoot.hasKey("filter")) {
            NBTTagCompound filterTag = (NBTTagCompound) nbtRoot.getTag("filter");
            filterItem = ItemStack.loadItemStackFromNBT(filterTag);
            IItemFilter flt = FilterRegister.getFilterForUpgrade(filterItem);
            if (flt instanceof ItemFilter) {
                filter = (ItemFilter) flt;
            } else {
                filterItem = null;
            }
        } else {
            filterItem = null;
            filter = null;
        }

        int rsContr = nbtRoot.getInteger("redstoneControlMode");
        if (rsContr < 0 || rsContr >= RedstoneControlMode.values().length) {
            rsContr = 0;
        }
        redstoneControlMode = RedstoneControlMode.values()[rsContr];
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbtRoot) {
        writeContentsToNBT(nbtRoot);
        nbtRoot.setBoolean("redstoneCheckPassed", redstoneCheckPassed);
    }

    public void writeContentsToNBT(NBTTagCompound nbtRoot) {
        NBTTagList itemList = new NBTTagList();
        for (int i = 0; i < inv.length; i++) {
            if (inv[i] != null) {
                NBTTagCompound itemStackNBT = new NBTTagCompound();
                itemStackNBT.setByte("Slot", (byte) i);
                inv[i].writeToNBT(itemStackNBT);
                itemList.appendTag(itemStackNBT);
            }
        }
        nbtRoot.setTag("Items", itemList);
        nbtRoot.setInteger("range", range);
        if (filterItem != null) {
            NBTTagCompound filterNBT = new NBTTagCompound();
            filterItem.writeToNBT(filterNBT);
            nbtRoot.setTag("filter", filterNBT);
        }
        nbtRoot.setInteger("redstoneControlMode", redstoneControlMode.ordinal());
    }
}
