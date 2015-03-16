package crazypants.enderio.machine.crafter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.AbstractPowerConsumerEntity;
import crazypants.enderio.machine.IItemBuffer;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.power.Capacitors;
import crazypants.enderio.power.ICapacitor;
import crazypants.util.ItemUtil;

public class TileCrafter extends AbstractPowerConsumerEntity implements IItemBuffer {

  DummyCraftingGrid craftingGrid = new DummyCraftingGrid();

  private List<ItemStack> containerItems;

  private ICapacitor capacitor;
  
  private boolean bufferStacks = true;

  private long ticksSinceLastCraft = 0;

  public TileCrafter() {
    super(new SlotDefinition(9, 1));
    containerItems = new ArrayList<ItemStack>();    
    setCapacitor(Capacitors.BASIC_CAPACITOR);    
  }

  @Override
  public ICapacitor getCapacitor() {
    return capacitor;
  }

  @Override
  public void setCapacitor(Capacitors capacitorType) {    
    ICapacitor refCap = capacitorType.capacitor;    
    int maxUse = getPowerUsePerTick(capacitorType);
    int io = Math.max(maxUse, refCap.getMaxEnergyExtracted());
    capacitor = new BasicCapacitor(io * 4, refCap.getMaxEnergyStored(), io);
    super.setCapacitor(capacitorType);             
  }

  @Override
  public String getMachineName() {
    return ModObject.blockCrafter.unlocalisedName;
  }

  @Override
  protected boolean isMachineItemValidForSlot(int slot, ItemStack itemstack) {
    if(!slotDefinition.isInputSlot(slot)) {
      return false;
    }
    return craftingGrid.inv[slot] != null && craftingGrid.inv[slot].isItemEqual(itemstack);
  }

  @Override
  public boolean isActive() {
    return false;
  }

  @Override
  public float getProgress() {
    return 0.5f;
  }

  @Override
  protected boolean processTasks(boolean redstoneCheckPassed) {
    ticksSinceLastCraft++;
    if(!redstoneCheckPassed || !craftingGrid.hasValidRecipe() || !canMergeOutput() || !hasRequiredPower()) {
      return false;
    }
    int ticksPerCraft = getTicksPerCraft(getCapacitorType());
    if(ticksSinceLastCraft <= ticksPerCraft) {
      return false;
    }
    ticksSinceLastCraft = 0;

    // process buffered container items
    if(!containerItems.isEmpty()) {
      Iterator<ItemStack> iter = containerItems.iterator();
      while (iter.hasNext()) {
        ItemStack stack = iter.next();
        if(inventory[9] == null) {
          inventory[9] = stack;
          iter.remove();
        } else if(ItemUtil.areStackMergable(inventory[9], stack) && inventory[9].stackSize + stack.stackSize <= inventory[9].getMaxStackSize()) {
          inventory[9].stackSize += stack.stackSize;
          iter.remove();
        }
      }
      return false;
    }

    List<ItemStack> required = new ArrayList<ItemStack>();
    craftingGrid.copyRequiredInputs(required);
    if(hasRequiredInput(required)) {
      craftRecipe();
      int used = Math.min(getEnergyStored(), Config.crafterRfPerCraft);
      setEnergyStored(getEnergyStored() - used);
    }
    return false;
  }

  private boolean hasRequiredPower() {
    return getEnergyStored() >= Config.crafterRfPerCraft;
  }

  @Override
  public int getPowerUsePerTick() {
    return getPowerUsePerTick(getCapacitorType());
  }
  
  public int getPowerUsePerTick(Capacitors type) {
    int ticks = getTicksPerCraft(type);    
    return (int)Math.ceil(Config.crafterRfPerCraft / (double)ticks);
  }

  public int getTicksPerCraft(Capacitors type) {
    if(type == Capacitors.BASIC_CAPACITOR) {
      return 20;
    } else if(type == Capacitors.ACTIVATED_CAPACITOR) {
      return 10;
    } else {
      return 2;
    }    
  }

  private void craftRecipe() {

    // (1) Find the items to craft with and put a copy into a temp crafting grid;
    //     also record what was used to destroy it later
    InventoryCrafting inv = new InventoryCrafting(new Container() {
      @Override
      public boolean canInteractWith(EntityPlayer var1) {
        return false;
      }
    }, 3, 3);

    ItemStack[] shadowInventory = new ItemStack[9];
    for (int i = 0; i < 9; i++) {
      shadowInventory[i] = inventory[i] == null ? null : inventory[i].copy();
    }
    
    for (int j = 0; j < 9; j++) {
      ItemStack req = craftingGrid.getStackInSlot(j);
      for (int i = 0; i < 9 && req != null; i++) {
        if (shadowInventory[i] != null && shadowInventory[i].stackSize > 0 && shadowInventory[i].isItemEqual(req)) {
          req = null;
          shadowInventory[i].stackSize--;
          ItemStack craftingItem = shadowInventory[i].copy();
          craftingItem.stackSize = 1;
          inv.setInventorySlotContents(j, craftingItem);
        }
      }
    }

    // (2) Try to craft with the temp grid
    ItemStack output = CraftingManager.getInstance().findMatchingRecipe(inv, worldObj);
    
    // (3) If we got a result, ...
    if (output != null) {

      // (3a) ... remove the used up items and ...
      for (int i = 0; i < 9; i++) {
        while (shadowInventory[i] != null && inventory[i] != null && shadowInventory[i].stackSize < inventory[i].stackSize) {
          setInventorySlotContents(i, eatOneItemForCrafting(inventory[i].copy()));
        }
      }

      // (3b) ... put the result into its slot
      if (inventory[9] == null) {
        setInventorySlotContents(9, output);
      } else if (ItemUtil.areStackMergable(inventory[9], output)) {
        ItemStack cur = inventory[9].copy();
        cur.stackSize += output.stackSize;
        if (cur.stackSize > cur.getMaxStackSize()) {
          // we check beforehand that there is enough free space, but some mod may return different
          // amounts based on the nbt of the input items (e.g. magical wood)
          ItemStack overflow = cur.copy();
          overflow.stackSize = cur.stackSize - cur.getMaxStackSize();
          cur.stackSize = cur.getMaxStackSize();
          containerItems.add(overflow);
        }
        setInventorySlotContents(9, cur);
      } else {
        // some mod may return different nbt based on the nbt of the input items (e.g. TE machines?)
        containerItems.add(output);
      }
    } else {
      // Crafting failed. This is not supposed to happen, but if a recipe is nbt-sensitive, it can.
      // To avoid being stuck in a dead loop, we flush the non-working input items.
      for (int j = 0; j < 9; j++) {
        if (shadowInventory[j] != null) {
          setInventorySlotContents(j, shadowInventory[j].stackSize > 0 ? shadowInventory[j] : null);
        }
        if (craftingGrid.getStackInSlot(j) != null) {
          containerItems.add(craftingGrid.getStackInSlot(j));
        }
      }
    }
  }

  private ItemStack eatOneItemForCrafting(ItemStack avail) {
    // 101 special cases for container items
    if (avail.getItem().hasContainerItem(avail)) {
      ItemStack used = avail.getItem().getContainerItem(avail);
      if (used == null) {
        // The promised container item does not exist
        avail.stackSize--;
      } else if (used.isItemStackDamageable() && used.getItemDamage() > used.getMaxDamage()) {
        // Container item was used up
        used = null;
        avail.stackSize--;
      } else if (used.isItemEqual(avail)) {
        if (used.isItemStackDamageable()) {
          // Container item is the same, but may have a different damage value
          if (avail.stackSize == 1) {
            // itemstack was used up, container item can replace it
            avail = used;
          } else {
            // itemstack was not used up, container item goes into overflow
            // (impossible case with vanilla and mods that play by the rules)
            containerItems.add(used.copy());
            avail.stackSize--;
          }
        } else {
          // Container item is exactly the same: item should not be used up
          // Nothing to do here
        }
      } else {
        // Container item is different (e.g. bucket for lava bucket) and goes into the overflow
        containerItems.add(used.copy());
        avail.stackSize--;
      }
    } else {
      // no container item, use up one item of the stack
      avail.stackSize--;
    }

    if (avail.stackSize == 0) {
      avail = null;
    }
    return avail;
  }

  private boolean canMergeOutput() {
    if(inventory[9] == null) {
      return true;
    }
    ItemStack output = craftingGrid.getOutput();
    if(!ItemUtil.areStackMergable(inventory[9], output)) {
      return false;
    }
    return output.getMaxStackSize() >= (inventory[9].stackSize + output.stackSize);
  }

  private boolean hasRequiredInput(List<ItemStack> required) {
    List<ItemStack> available = new ArrayList<ItemStack>();
    for (int i = 0; i < 9; i++) {
      ItemStack is = inventory[i];
      if(is != null) {
        available.add(is.copy());
      }
    }
    for (ItemStack req : required) {
      boolean foundReq = false;
      for (ItemStack avail : available) {
        if(req.isItemEqual(avail) && avail.stackSize > 0) {
          avail.stackSize--;
          foundReq = true;
          break;
        }
      }
      if(!foundReq) {
        return false;
      }
    }
    return true;
  }

  @Override
  public int getInventoryStackLimit() {
    return bufferStacks ? 64 : 1;
  }

  @Override
  public boolean isBufferStacks() {
    return bufferStacks;
  }

  @Override
  public void setBufferStacks(boolean bufferStacks) {
    this.bufferStacks = bufferStacks;
  }

  @Override
  public void readCommon(NBTTagCompound nbtRoot) {
    super.readCommon(nbtRoot);
    NBTTagCompound craftRoot = nbtRoot.getCompoundTag("craftingGrid");
    craftingGrid.readFromNBT(craftRoot);

    if(nbtRoot.hasKey("bufferStacks")) {
      bufferStacks = nbtRoot.getBoolean("bufferStacks");
    } else {
      bufferStacks = true;
    }
  }

  @Override
  public void writeCommon(NBTTagCompound nbtRoot) {
    super.writeCommon(nbtRoot);
    NBTTagCompound craftingRoot = new NBTTagCompound();
    craftingGrid.writeToNBT(craftingRoot);
    nbtRoot.setTag("craftingGrid", craftingRoot);

    nbtRoot.setBoolean("bufferStacks", bufferStacks);
  }

  public void updateCraftingOutput() {
    InventoryCrafting inv = new InventoryCrafting(new Container() {

      @Override
      public boolean canInteractWith(EntityPlayer var1) {
        return false;
      }
    }, 3, 3);

    for (int i = 0; i < 9; i++) {
      inv.setInventorySlotContents(i, craftingGrid.getStackInSlot(i));
    }
    ItemStack matches = CraftingManager.getInstance().findMatchingRecipe(inv, worldObj);
    craftingGrid.setInventorySlotContents(9, matches);
    markDirty();

  }

}
