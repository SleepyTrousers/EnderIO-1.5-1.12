package crazypants.enderio.base.paint;

import javax.annotation.Nonnull;

import com.enderio.core.common.transform.SimpleMixin;

import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;

@SimpleMixin(IPaintable.ITexturePaintableBlock.class)
@SimpleMixin(IPaintable.IBlockPaintableBlock.class)
@SimpleMixin(IPaintable.IBlockPaintableBlock.ISolidBlockPaintableBlock.class)
@SimpleMixin(IPaintable.IBlockPaintableBlock.INonSolidBlockPaintableBlock.class)
public abstract class MobilityFlagForPaintedBlocksMixin extends Block {

  private MobilityFlagForPaintedBlocksMixin(Material materialIn) {
    super(materialIn);
  }

  @Override
  public @Nonnull EnumPushReaction getMobilityFlag(@Nonnull IBlockState state) {
    // Some mods coremod vanilla to move blocks with TEs, so let's try to enforce it.
    return EnumPushReaction.BLOCK;
  }

}
