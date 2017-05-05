package crazypants.enderio.machine.painter.recipe;

import crazypants.enderio.item.darksteel.DarkSteelItems;
import crazypants.enderio.item.darksteel.ItemDarkSteelArmor;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.paint.PaintTooltipUtil;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class HelmetPainterTemplate extends AbstractPainterTemplate<ItemDarkSteelArmor> {

  @Override
  public boolean isValidTarget(ItemStack target) {
    return target != null && target.getItem() instanceof ItemDarkSteelArmor && ((ItemDarkSteelArmor) target.getItem()).armorType == EntityEquipmentSlot.HEAD;
  }

  @Override
  public ResultStack[] getCompletedResult(ItemStack paintSource, ItemStack target) {
    if (target == null || paintSource == null) {
      return new ResultStack[0];
    }
    if (isValidTarget(paintSource)) {
      ItemStack result = target.copy();
      NBTTagCompound tagCompound = result.getTagCompound();
      if (tagCompound != null) {
        tagCompound.removeTag("DSPAINT");
      }
      return new ResultStack[] { new ResultStack(result) };
    }
    if (paintSource.getItem() instanceof ItemBlock) {
      ItemStack result = target.copy();
      NBTTagCompound tagCompound = result.getTagCompound();
      if (tagCompound == null) {
        tagCompound = new NBTTagCompound();
        result.setTagCompound(tagCompound);
      }
      tagCompound.setTag("DSPAINT", paintSource.writeToNBT(tagCompound.getCompoundTag("DSPAINT")));
      return new ResultStack[] { new ResultStack(result) };
    }
    return new ResultStack[0];
  }

  @Override
  public boolean isRecipe(ItemStack paintSource, ItemStack target) {
    return isValidTarget(target) && isValidPaint(paintSource);
  }

  @Override
  public boolean isPartialRecipe(ItemStack paintSource, ItemStack target) {
    return isValidTarget(target) || isValidPaint(paintSource);
  }

  protected boolean isValidPaint(ItemStack paintSource) {
    return (paintSource != null && paintSource.getItem() instanceof ItemBlock) || isValidTarget(paintSource);
  }

  @Override
  public boolean isValidInput(MachineRecipeInput input) {
    if (input == null) {
      return false;
    }
    if (input.slotNumber == 0) {
      return isValidTarget(input.item);
    }
    if (input.slotNumber == 1) {
      return isValidPaint(input.item);
    }
    return false;
  }

  @Override
  protected void registerTargetsWithTooltipProvider() {
    PaintTooltipUtil.registerPaintable(DarkSteelItems.itemDarkSteelHelmet);
  }

}
