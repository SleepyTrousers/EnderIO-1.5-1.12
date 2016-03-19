package crazypants.enderio.machine.painter.blocks;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;

public class RecipePaintedPressurePlate implements IRecipe {

  static {
    RecipeSorter.register("EnderIO:PaintedPressurePlate", RecipePaintedPressurePlate.class, Category.SHAPELESS, "before:minecraft:shaped");
  }

  public RecipePaintedPressurePlate() {
  }

  @Override
  public boolean matches(InventoryCrafting inv, World worldIn) {

    boolean foundPlate = false, foundWool = false;

    for (int i = 0; i < inv.getSizeInventory(); ++i) {
      ItemStack itemstack = inv.getStackInSlot(i);
      if (itemstack != null) {
        Block block = Block.getBlockFromItem(itemstack.getItem());
        if (block == Blocks.wool) {
          if (foundWool) {
            return false;
          }
          foundWool = true;
        } else if (block instanceof BlockPaintedPressurePlate) {
          if (foundPlate || EnumPressurePlateType.getSilentFromMeta(itemstack.getMetadata())) {
            return false;
          }
          foundPlate = true;
        } else {
          return false;
        }
      }
    }

    return foundPlate && foundWool;
  }

  @Override
  public ItemStack getCraftingResult(InventoryCrafting inv) {
    for (int i = 0; i < inv.getSizeInventory(); ++i) {
      ItemStack itemstack = inv.getStackInSlot(i);
      if (itemstack != null) {
        Block block = Block.getBlockFromItem(itemstack.getItem());
        if (block instanceof BlockPaintedPressurePlate) {

          ItemStack result = itemstack.copy();
          result.setItemDamage(EnumPressurePlateType.getMetaFromType(EnumPressurePlateType.getTypeFromMeta(itemstack.getMetadata()), true));
          return result;
        }
      }
    }
    return null;
  }

  @Override
  public int getRecipeSize() {
    return 2;
  }

  @Override
  public ItemStack getRecipeOutput() {
    return null;
  }

  @Override
  public ItemStack[] getRemainingItems(InventoryCrafting inv) {
    return new ItemStack[inv.getSizeInventory()];
  }

}
