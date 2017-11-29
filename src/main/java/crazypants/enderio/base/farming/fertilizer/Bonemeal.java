package crazypants.enderio.base.farming.fertilizer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Bonemeal extends AbstractFertilizer {
  public Bonemeal(@Nonnull ItemStack stack) {
    super(stack);
  }

  public Bonemeal(@Nullable Block block) {
    super(block);
  }

  public Bonemeal(@Nullable Item item) {
    super(item);
  }

  @Override
  public Result apply(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos bc) {
    ItemStack before = player.getHeldItem(EnumHand.MAIN_HAND);
    player.setHeldItem(EnumHand.MAIN_HAND, stack);
    EnumActionResult res = stack.getItem().onItemUse(player, world, bc, EnumHand.MAIN_HAND, EnumFacing.UP, 0.5f, 0.5f, 0.5f);
    ItemStack after = player.getHeldItem(EnumHand.MAIN_HAND);
    player.setHeldItem(EnumHand.MAIN_HAND, before);
    return new Result(after, res != EnumActionResult.PASS);
  }

}