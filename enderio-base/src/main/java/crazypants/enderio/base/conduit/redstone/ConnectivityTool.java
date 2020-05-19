package crazypants.enderio.base.conduit.redstone;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.stackable.Things;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ConnectivityTool {

  private static final @Nonnull Things CONNECTABLES = new Things().add(Blocks.REDSTONE_LAMP).add(Blocks.LIT_REDSTONE_LAMP)
      .add(Blocks.DISPENSER).add(Blocks.DROPPER).add(Blocks.IRON_TRAPDOOR).add(Blocks.TRAPDOOR).add(Blocks.ACACIA_DOOR)
      .add(Blocks.BIRCH_DOOR).add(Blocks.DARK_OAK_DOOR).add(Blocks.IRON_DOOR).add(Blocks.JUNGLE_DOOR).add(Blocks.OAK_DOOR).add(Blocks.SPRUCE_DOOR)
      .add(Blocks.ACTIVATOR_RAIL).add(Blocks.HOPPER).add(Blocks.OAK_FENCE_GATE).add(Blocks.ACACIA_FENCE_GATE).add(Blocks.BIRCH_FENCE_GATE)
      .add(Blocks.DARK_OAK_FENCE_GATE).add(Blocks.JUNGLE_FENCE_GATE).add(Blocks.SPRUCE_FENCE_GATE).add(Blocks.PISTON).add(Blocks.STICKY_PISTON)
      .add(Blocks.GOLDEN_RAIL).add(Blocks.TNT).add(Blocks.NOTEBLOCK);

  public static boolean shouldAutoConnectRedstone(@Nonnull IBlockState state) {
    return CONNECTABLES.contains(state.getBlock());
  }

  public static boolean shouldAutoConnectRedstone(@Nonnull World world, @Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull EnumFacing from) {
    return state.getBlock().canConnectRedstone(state, world, pos, from) || shouldAutoConnectRedstone(state);
  }

  public static void registerRedstoneAware(@Nonnull Block block) {
    CONNECTABLES.add(block);
  }

  public static void registerRedstoneAware(@Nonnull IBlockState state) {
    registerRedstoneAware(state.getBlock());
  }

  public static void registerRedstoneAware(@Nonnull String value) {
    CONNECTABLES.add(value);
  }

}
