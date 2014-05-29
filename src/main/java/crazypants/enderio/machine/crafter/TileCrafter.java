package crazypants.enderio.machine.crafter;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import crazypants.enderio.Config;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.power.Capacitors;

public class TileCrafter extends AbstractMachineEntity {

  DummyCraftingGrid craftingGrid = new DummyCraftingGrid();
  
  public TileCrafter() {
    super(new SlotDefinition(9, 1));    
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
    if(!redstoneCheckPassed || !craftingGrid.hasValidRecipe() || !canMergeOutput() || !hasPower()) {      
      return false;
    }
    if(capacitorType == Capacitors.BASIC_CAPACITOR && worldObj.getTotalWorldTime() % 20 != 0) {
      return false;
    } else if(capacitorType == Capacitors.ACTIVATED_CAPACITOR && worldObj.getTotalWorldTime() % 10 != 0) {
      return false;
    } else if(worldObj.getTotalWorldTime() % 2 != 0) {
      return false;
    }
    
    List<ItemStack> required = new ArrayList<ItemStack>();
    craftingGrid.copyRequiredInputs(required);
    if(hasRequiredInput(required)) {      
      craftRecipe();
      double used = Math.min(powerHandler.getEnergyStored(), Config.crafterMjPerCraft);    
      powerHandler.setEnergy(powerHandler.getEnergyStored() - used);
    }     
    return false;
  }

  private void craftRecipe() {    
    List<ItemStack> required = new ArrayList<ItemStack>();
    craftingGrid.copyRequiredInputs(required);
    for(ItemStack req : required) {
      for(int i=0;i<9 && req.stackSize > 0; i++) {
        ItemStack avail = inventory[i];
        if(avail != null && avail.stackSize > 0 && avail.isItemEqual(req)) {
          req.stackSize--;
          avail = avail.copy();
          avail.stackSize--;
          if(avail.stackSize <= 0) {
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
    for(int i=0;i<9;i++) {
      ItemStack is = inventory[i];
      if(is != null) {
        available.add(is.copy());
      }      
    }    
    for(ItemStack req : required) {
      boolean foundReq = false;
      for(ItemStack avail : available) {
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
  public void readCommon(NBTTagCompound nbtRoot) {
    super.readCommon(nbtRoot);
    NBTTagCompound craftRoot = nbtRoot.getCompoundTag("craftingGrid");
    craftingGrid.readFromNBT(craftRoot);
  }

  @Override
  public void writeCommon(NBTTagCompound nbtRoot) {
    super.writeCommon(nbtRoot);    
    NBTTagCompound craftingRoot = new NBTTagCompound();
    craftingGrid.writeToNBT(craftingRoot);
    nbtRoot.setTag("craftingGrid", craftingRoot);
  }

  public void updateCraftingOutput() {
    InventoryCrafting inv = new InventoryCrafting(new Container() {
      
      @Override
      public boolean canInteractWith(EntityPlayer var1) {        
        return false;
      }
    }, 3, 3);
    
    for(int i=0;i<9;i++) {
      inv.setInventorySlotContents(i, craftingGrid.getStackInSlot(i));
    }
    ItemStack matches = CraftingManager.getInstance().findMatchingRecipe(inv, worldObj);
    craftingGrid.setInventorySlotContents(9, matches);
    markDirty();
    
  }
  
  

}
