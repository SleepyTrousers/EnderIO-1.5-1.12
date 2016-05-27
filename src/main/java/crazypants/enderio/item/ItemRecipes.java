package crazypants.enderio.item;

import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.painter.blocks.EnumPressurePlateType;
import crazypants.enderio.machine.painter.blocks.RecipePaintedPressurePlate;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemRecipes {

  public static void addRecipes() {
    GameRegistry.addRecipe(new RecipePaintedPressurePlate()); // plate + wool = silent plate (keeps nbt)
    for (EnumPressurePlateType type : EnumPressurePlateType.values()) {
      // these are just for JEI, the RecipePaintedPressurePlate has higher priority
      GameRegistry.addShapedRecipe(new ItemStack(EnderIO.blockPaintedPressurePlate, 1, type.getMetaFromType(true)), "p", "w", 'p', new ItemStack(
          EnderIO.blockPaintedPressurePlate, 1, type.getMetaFromType(false)), 'w', Blocks.WOOL);
    }
  }

}
