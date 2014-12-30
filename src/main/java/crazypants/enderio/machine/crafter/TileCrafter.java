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
        } else if(ItemStack.areItemStacksEqual(inventory[9], stack) && inventory[9].stackSize + stack.stackSize <= inventory[9].getMaxStackSize()) {
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
    List<ItemStack> required = new ArrayList<ItemStack>();
    craftingGrid.copyRequiredInputs(required);
    for (ItemStack req : required) {
      for (int i = 0; i < 9 && req.stackSize > 0; i++) {
        ItemStack avail = inventory[i];
        if(avail != null && avail.stackSize > 0 && avail.isItemEqual(req)) {
          req.stackSize--;
          avail = avail.copy();
          avail.stackSize--;
          if(avail.stackSize <= 0) {
            ItemStack used = avail.getItem().getContainerItem(avail);
            if(used != null) {
              if(used.isItemEqual(avail)) {
                avail.stackSize++;
              } else {
                containerItems.add(used.copy());
                avail = null;
              }
            }
          }
          if(avail != null && avail.stackSize == 0) {
            avail = null;
          }
          setInventorySlotContents(i, avail);
        }
      }
    }
    ItemStack output = craftingGrid.getOutput().copy();
    if(inventory[9] == null) {
      setInventorySlotContents(9, output);
    } else {
      ItemStack cur = inventory[9].copy();
      cur.stackSize += output.stackSize;
      setInventorySlotContents(9, cur);
    }
  }

  private boolean canMergeOutput() {
    if(inventory[9] == null) {
      return true;
    }
    ItemStack output = craftingGrid.getOutput();
    if(!inventory[9].isItemEqual(output)) {
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
