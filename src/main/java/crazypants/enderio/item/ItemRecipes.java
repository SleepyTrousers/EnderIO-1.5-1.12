package crazypants.enderio.item;

import java.util.ArrayList;
import java.util.List;

import crazypants.enderio.EnderIO;
import crazypants.enderio.block.BlockSelfResettingLever;
import crazypants.enderio.machine.painter.blocks.EnumPressurePlateType;
import crazypants.enderio.machine.painter.blocks.RecipePaintedPressurePlate;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

import static crazypants.util.RecipeUtil.addShapeless;

public class ItemRecipes {

  public static void addRecipes() {
    GameRegistry.addRecipe(new RecipePaintedPressurePlate()); // plate + wool = silent plate (keeps nbt)
    for (EnumPressurePlateType type : EnumPressurePlateType.values()) {
      // these are just for JEI, the RecipePaintedPressurePlate has higher priority
      GameRegistry.addShapedRecipe(new ItemStack(EnderIO.blockPaintedPressurePlate, 1, type.getMetaFromType(true)), "p", "w", 'p', new ItemStack(
          EnderIO.blockPaintedPressurePlate, 1, type.getMetaFromType(false)), 'w', Blocks.WOOL);
    }

    List<BlockSelfResettingLever> levers = BlockSelfResettingLever.getBlocks();
    List<BlockSelfResettingLever> usedLevers = new ArrayList<BlockSelfResettingLever>();
    int rs = 0;
    for (BlockSelfResettingLever block : levers) {
      addShapelessWT(block, Blocks.LEVER, "dustRedstone", ++rs);
      int rsl = rs;
      for (Block block0 : usedLevers) {
        addShapelessWT(block, block0, "dustRedstone", --rsl);
      }
      usedLevers.add(block);
    }
  }

  private static void addShapelessWT(Block block, Object in0, Object in, int count) {
    if (block != null && count >= 0 && count < 9) {
      Object[] ingr = new Object[count + 1];
      ingr[0] = in0;
      for (int i = 1; i <= count; i++) {
        ingr[i] = in;
      }
      addShapeless(block, ingr);
    }
  }

}
