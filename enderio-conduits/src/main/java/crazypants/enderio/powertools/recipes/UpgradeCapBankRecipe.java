package crazypants.enderio.powertools.recipes;

import javax.annotation.Nonnull;

import crazypants.enderio.powertools.machine.capbank.BlockItemCapBank;
import crazypants.enderio.util.Prep;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class UpgradeCapBankRecipe extends ShapedOreRecipe {

  public UpgradeCapBankRecipe(@Nonnull ItemStack result, Object... recipe) {
    super(null, result, recipe);
  }

  @Override
  public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World world) {
    for (int y = 0; y < 3; y++) {
      for (int x = 0; x < 3; x++) {
        ItemStack st = inv.getStackInRowAndColumn(x, y);
        final NBTTagCompound tagCompound = st.getTagCompound();
        if (tagCompound != null && tagCompound.hasKey("Items")) { // FIXME: Check that that still is the correct key
          return false;
        }
      }
    }
    return super.matches(inv, world);
  }

  @Override
  public @Nonnull ItemStack getCraftingResult(@Nonnull InventoryCrafting var1) {
    long energy = 0;
    for (int y = 0; y < 3; y++) {
      for (int x = 0; x < 3; x++) {
        ItemStack st = var1.getStackInRowAndColumn(x, y);
        if (Prep.isValid(st)) {
          energy += BlockItemCapBank.getStoredEnergyForItem(st);
        }
      }
    }

    ItemStack res = super.getCraftingResult(var1);
    BlockItemCapBank.setStoredEnergyForItem(res, (int) Math.min(Integer.MAX_VALUE, energy));
    return res;
  }

}
