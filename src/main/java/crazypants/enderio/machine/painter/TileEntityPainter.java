package crazypants.enderio.machine.painter;

import java.util.List;
import java.util.Map;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.SlotDefinition;

public class TileEntityPainter extends AbstractPoweredTaskEntity implements ISidedInventory {

//  private static final short MAX_POWER_USE_PER_TICK = 6;

  public TileEntityPainter() {
    // 0 = input slot, 1 = paint source, 2 = output slot
    super(new SlotDefinition(2, 1));
  }

  @Override
  public boolean canExtractItem(int i, ItemStack itemstack, int j) {
    return super.canExtractItem(i, itemstack, j) && PainterUtil.isMetadataEquivelent(itemstack, inventory[2]);
  }

  @Override
  public String getInventoryName() {
    return "Auto Painter";
  }

  @Override
  public boolean hasCustomInventoryName() {
    return false;
  }

  @Override
  public boolean isMachineItemValidForSlot(int i, ItemStack itemStack) {
    if(i > 1) {
      return false;
    }
    if(i == 0) {
      List<IMachineRecipe> recipes = MachineRecipeRegistry.instance.getRecipesForInput(getMachineName(), MachineRecipeInput.create(i, itemStack));
      if(inventory[1] == null) {
        return !recipes.isEmpty();
      } else {
        for(IMachineRecipe rec : recipes) {
          if(rec instanceof BasicPainterTemplate) {
            BasicPainterTemplate temp = (BasicPainterTemplate)rec;
            if(temp.isValidPaintSource(inventory[1])) {
              return true;
            }
          }
        }
      }
      return false;
    }
    if(inventory[0] == null) {      
      Map<String, IMachineRecipe> recipes = MachineRecipeRegistry.instance.getRecipesForMachine(getMachineName());
      for(IMachineRecipe rec : recipes.values()) {
        if(rec instanceof BasicPainterTemplate) {
          BasicPainterTemplate temp = (BasicPainterTemplate)rec;
          if(temp.isValidPaintSource(itemStack)) {
            return true;
          }
        }
      }      
      return PaintSourceValidator.instance.isValidSourceDefault(itemStack);
    }
    return MachineRecipeRegistry.instance.getRecipeForInputs(getMachineName(),
        i == 0 ? MachineRecipeInput.create(0, itemStack) : targetInput(), i == 1 ? MachineRecipeInput.create(1, itemStack) : paintSource()) != null;
  }

  @Override
  public String getMachineName() {
    return ModObject.blockPainter.unlocalisedName;
  }

  private MachineRecipeInput targetInput() {
    return MachineRecipeInput.create(0, inventory[0]);
  }

  private MachineRecipeInput paintSource() {
    return MachineRecipeInput.create(1, inventory[1]);
  }

  @Override
  protected int getNumCanMerge(ItemStack itemStack, ItemStack result) {
    if (result == null || !result.isItemEqual(inventory[2])) {
      // next result is a different item type
      return 0;
    } else if (result.hasTagCompound() && inventory[2].hasTagCompound()) {
      int cookedId = result.getTagCompound().getInteger(BlockPainter.KEY_SOURCE_BLOCK_ID);
      int invId = inventory[2].getTagCompound().getInteger(BlockPainter.KEY_SOURCE_BLOCK_ID);
      if (cookedId != invId) {
        // next result has a different source item than the current one
        return 0;
      }
    }
    return Math.min(itemStack.getMaxStackSize() - itemStack.stackSize, result.stackSize);
  }

}
