package crazypants.enderio.machines.machine.painter;

import java.util.Map;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.ItemUtil;

import crazypants.enderio.machine.baselegacy.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.baselegacy.SlotDefinition;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.recipe.IMachineRecipe;
import crazypants.enderio.recipe.MachineRecipeRegistry;
import crazypants.enderio.recipe.painter.AbstractPainterTemplate;
import info.loenwind.autosave.annotations.Storable;
import net.minecraft.item.ItemStack;

import static crazypants.enderio.capacitor.CapacitorKey.LEGACY_ENERGY_BUFFER;
import static crazypants.enderio.capacitor.CapacitorKey.LEGACY_ENERGY_INTAKE;
import static crazypants.enderio.capacitor.CapacitorKey.LEGACY_ENERGY_USE;

@Storable
public class TileEntityPainter extends AbstractPoweredTaskEntity implements IPaintable.IPaintableTileEntity {

  public TileEntityPainter() {
    // 0 = input slot, 1 = paint source, 2 = output slot
    super(new SlotDefinition(2, 1), LEGACY_ENERGY_INTAKE,LEGACY_ENERGY_BUFFER, LEGACY_ENERGY_USE);
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
