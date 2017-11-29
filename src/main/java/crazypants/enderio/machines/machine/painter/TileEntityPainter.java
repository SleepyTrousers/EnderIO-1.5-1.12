package crazypants.enderio.machines.machine.painter;

import java.util.Map;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.ItemUtil;

import crazypants.enderio.base.machine.baselegacy.AbstractPoweredTaskEntity;
import crazypants.enderio.base.machine.baselegacy.SlotDefinition;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.recipe.IMachineRecipe;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.painter.AbstractPainterTemplate;
import info.loenwind.autosave.annotations.Storable;
import net.minecraft.item.ItemStack;

import static crazypants.enderio.machines.capacitor.CapacitorKey.PAINTER_POWER_BUFFER;
import static crazypants.enderio.machines.capacitor.CapacitorKey.PAINTER_POWER_INTAKE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.PAINTER_POWER_USE;

@Storable
public class TileEntityPainter extends AbstractPoweredTaskEntity implements IPaintable.IPaintableTileEntity {

  public TileEntityPainter() {
    // 0 = input slot, 1 = paint source, 2 = output slot
    super(new SlotDefinition(2, 1), PAINTER_POWER_INTAKE, PAINTER_POWER_BUFFER, PAINTER_POWER_USE);
  }


  @Override
  public boolean isMachineItemValidForSlot(int i, ItemStack itemStack) {
    if(i > 1) {
      return false;
    }
    ItemStack paint = i == 0 ? inventory[1] : itemStack;
    ItemStack targt = i == 0 ? itemStack : inventory[0];


    Map<String, IMachineRecipe> recipes = MachineRecipeRegistry.instance.getRecipesForMachine(getMachineName());
    for (IMachineRecipe rec : recipes.values()) {
      if (rec instanceof AbstractPainterTemplate<?>) {
        AbstractPainterTemplate<?> temp = (AbstractPainterTemplate<?>) rec;
        if (temp.isPartialRecipe(paint, targt)) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public @Nonnull String getMachineName() {
    return MachineRecipeRegistry.PAINTER;
  }

  @Override
  protected int getNumCanMerge(ItemStack itemStack, ItemStack result) {
    if (!ItemUtil.areStackMergable(itemStack, result)) {
      // next result is a different item type
      return 0;
    }
    return Math.min(itemStack.getMaxStackSize() - itemStack.getCount(), result.getCount());
  }

}
