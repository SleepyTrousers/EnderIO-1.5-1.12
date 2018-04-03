package crazypants.enderio.base.conduit.redstone;

import javax.annotation.Nonnull;

import com.enderio.core.common.BlockEnder;
import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.api.redstone.IRedstoneConnectable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ConnectivityTool {

  private static final @Nonnull Things CONNECTABLES = new Things().add(Blocks.REDSTONE_BLOCK).add(Blocks.REDSTONE_LAMP).add(Blocks.LIT_REDSTONE_LAMP)
      .add(Blocks.REDSTONE_TORCH).add(Blocks.LEVER).add(Blocks.STONE_BUTTON).add(Blocks.WOODEN_BUTTON).add(Blocks.POWERED_COMPARATOR)
      .add(Blocks.UNPOWERED_COMPARATOR).add(Blocks.POWERED_REPEATER).add(Blocks.UNPOWERED_REPEATER).add(Blocks.DAYLIGHT_DETECTOR)
      .add(Blocks.DAYLIGHT_DETECTOR_INVERTED).add(Blocks.DISPENSER).add(Blocks.DROPPER).add(Blocks.IRON_TRAPDOOR).add(Blocks.TRAPDOOR).add(Blocks.ACACIA_DOOR)
      .add(Blocks.BIRCH_DOOR).add(Blocks.DARK_OAK_DOOR).add(Blocks.IRON_DOOR).add(Blocks.JUNGLE_DOOR).add(Blocks.OAK_DOOR).add(Blocks.SPRUCE_DOOR)
      .add(Blocks.TRAPPED_CHEST).add(Blocks.TRIPWIRE_HOOK).add(Blocks.ACTIVATOR_RAIL).add(Blocks.DETECTOR_RAIL).add(Blocks.GOLDEN_RAIL)
      .add(Blocks.REDSTONE_WIRE);

  public static boolean shouldAutoConnectRedstone(@Nonnull IBlockState state) {
    return CONNECTABLES.contains(state.getBlock());
  }

  public static boolean shouldAutoConnectRedstone(@Nonnull World world, @Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull EnumFacing from) {
    if (state.getBlock().canConnectRedstone(state, world, pos, from) || shouldAutoConnectRedstone(state)) {
      return true;
    }
    if (state.getBlock() instanceof IRedstoneConnectable) {
      return ((IRedstoneConnectable) state.getBlock()).shouldRedstoneConduitConnect(world, pos, from);
    }
    IRedstoneConnectable redstoneConnectable = BlockEnder.getAnyTileEntitySafe(world, pos, IRedstoneConnectable.class);
    if (redstoneConnectable != null) {
      redstoneConnectable.shouldRedstoneConduitConnect(world, pos, from);
    }
    return false;
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
