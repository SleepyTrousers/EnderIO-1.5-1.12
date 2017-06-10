package crazypants.enderio.block.painted;

import javax.annotation.Nonnull;

import crazypants.enderio.init.IModObject;
import crazypants.enderio.init.ModObject;
import crazypants.enderio.recipe.MachineRecipeRegistry;
import crazypants.enderio.recipe.painter.BasicPainterTemplate;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockPaintedSlabManager {

  private BlockPaintedSlabManager() {
  }

  public static Block create_wood(@Nonnull IModObject modObject) {
    return create(modObject, Material.WOOD, SoundType.WOOD, Blocks.WOODEN_SLAB);
  }

  public static Block create_stone(@Nonnull IModObject modObject) {
    return create(modObject, Material.ROCK, SoundType.STONE, Blocks.STONE_SLAB, Blocks.STONE_SLAB2);
  }

  public static Block create_wood_double(@Nonnull IModObject modObject) {
    return create_double(modObject, Material.WOOD, SoundType.WOOD);
  }

  public static Block create_stone_double(@Nonnull IModObject modObject) {
    return create_double(modObject, Material.ROCK, SoundType.STONE);
  }

  private static Block create(@Nonnull IModObject modObject, @Nonnull Material material, @Nonnull SoundType sound, @Nonnull Block... paintables) {
    BlockPaintedSlab.BlockPaintedHalfSlab halfSlab = new BlockPaintedSlab.BlockPaintedHalfSlab(modObject, material, sound);
    halfSlab.setHardness(2.0F).setResistance(5.0F);
    halfSlab.init(modObject);
    GameRegistry.register(modObject.apply(new BlockItemPaintedSlab(halfSlab)));
    MachineRecipeRegistry.instance.registerRecipe(MachineRecipeRegistry.PAINTER, new BasicPainterTemplate<BlockPaintedSlab>(halfSlab, paintables));
    return halfSlab;
  }

  private static Block create_double(@Nonnull IModObject modObject, @Nonnull Material material, @Nonnull SoundType sound) {
    if (modObject instanceof ModObject) {
      final ModObject halfSlabObject = ModObject.values()[((ModObject) modObject).ordinal() - 1];
      BlockPaintedSlab.BlockPaintedHalfSlab halfSlabBlock = (BlockPaintedSlab.BlockPaintedHalfSlab) halfSlabObject.getBlockNN();

      BlockPaintedSlab.BlockPaintedDoubleSlab doubleSlab = new BlockPaintedSlab.BlockPaintedDoubleSlab(modObject, material, halfSlabBlock, sound);
      doubleSlab.setHardness(2.0F).setResistance(5.0F);
      doubleSlab.init(modObject);

      BlockItemPaintedSlab halfSlabItem = (BlockItemPaintedSlab) halfSlabObject.getItemNN();
      halfSlabItem.addDoubleSlab(doubleSlab);

      return doubleSlab;
    } else {
      throw new RuntimeException("Bad block initialization, " + modObject + " is not a ModObject!");
    }
  }

}
