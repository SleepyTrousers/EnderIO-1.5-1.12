package crazypants.enderio.machine.painter.recipe;

import javax.annotation.Nonnull;

import crazypants.enderio.item.darksteel.ItemDarkSteelArmor;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.paint.PaintTooltipUtil;
import crazypants.util.Prep;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class HelmetPainterTemplate extends AbstractPainterTemplate<ItemDarkSteelArmor> {

  private final @Nonnull ItemDarkSteelArmor helmet;

  public HelmetPainterTemplate(@Nonnull ItemDarkSteelArmor helmet) {
    this.helmet = helmet;
  }

  @Override
  public boolean isValidTarget(@Nonnull ItemStack target) {
    return target.getItem() == helmet;
  }

  @Override
  public @Nonnull ResultStack[] getCompletedResult(@Nonnull ItemStack paintSource, @Nonnull ItemStack target) {
    if (Prep.isInvalid(target) || Prep.isInvalid(paintSource)) {
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
  public boolean isRecipe(@Nonnull ItemStack paintSource, @Nonnull ItemStack target) {
    return isValidTarget(target) && isValidPaint(paintSource);
  }

  @Override
  public boolean isPartialRecipe(@Nonnull ItemStack paintSource, @Nonnull ItemStack target) {
    return isValidTarget(target) || isValidPaint(paintSource);
  }

  protected boolean isValidPaint(@Nonnull ItemStack paintSource) {
    return paintSource.getItem() instanceof ItemBlock || isValidTarget(paintSource);
  }

  @Override
  public boolean isValidInput(@Nonnull MachineRecipeInput input) {
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
    PaintTooltipUtil.registerPaintable(helmet);
  }

}
