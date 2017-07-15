package crazypants.enderio.integration.botania;

import javax.annotation.Nonnull;

import crazypants.enderio.farming.fertilizer.Bonemeal;
import crazypants.enderio.farming.fertilizer.Result;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MagicalFertilizer extends Bonemeal {
  public MagicalFertilizer(Item item) {
    super(item);
  }

  @Override
  public Result apply(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos bc) {
    BlockPos below = bc.down();
    Block belowBlock = world.getBlockState(below).getBlock();
    if (belowBlock == Blocks.DIRT || belowBlock == Blocks.GRASS) {
      return super.apply(stack, player, world, below);
    }
    return new Result(stack, false);
  }

  @Override
  public boolean applyOnAir() {
    return true;
  }

  @Override
  public boolean applyOnPlant() {
    return false;
  }
}