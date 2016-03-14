package crazypants.enderio.machine.painter;

import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import com.enderio.core.common.util.ItemUtil;

import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.machine.painter.recipe.BasicPainterTemplate;
import crazypants.enderio.paint.PaintSourceValidator;

public class TileEntityPainter extends AbstractPoweredTaskEntity {

  public TileEntityPainter() {
    // 0 = input slot, 1 = paint source, 2 = output slot
    super(new SlotDefinition(2, 1));
  }

  @Override
  public boolean canExtractItem(int i, ItemStack itemstack, EnumFacing side) {
    return super.canExtractItem(i, itemstack, side) && ItemUtil.areStackMergable(itemstack, inventory[2]);
  }

  @Override
  public String getName() {
    return "Auto Painter";
  }

  @Override
  public boolean hasCustomName() {
    return false;
  }

  @Override
  public boolean isMachineItemValidForSlot(int i, ItemStack itemStack) {
    if(i > 1) {
      return false;
    }
    ItemStack paint = i == 0 ? inventory[1] : itemStack;
    ItemStack targt = i == 0 ? itemStack : inventory[0];

    if (paint != null && !PaintSourceValidator.instance.isValidSourceDefault(paint)) {
      return false;
    }

    Map<String, IMachineRecipe> recipes = MachineRecipeRegistry.instance.getRecipesForMachine(getMachineName());
    for (IMachineRecipe rec : recipes.values()) {
      if (rec instanceof BasicPainterTemplate) {
        BasicPainterTemplate temp = (BasicPainterTemplate) rec;
        if (temp.isPartialRecipe(paint, targt)) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public String getMachineName() {
    return ModObject.blockPainter.unlocalisedName;
  }

  @Override
  protected int getNumCanMerge(ItemStack itemStack, ItemStack result) {
    if (!ItemUtil.areStackMergable(itemStack, result)) {
      // next result is a different item type
      return 0;
    }
    return Math.min(itemStack.getMaxStackSize() - itemStack.stackSize, result.stackSize);
  }

}
