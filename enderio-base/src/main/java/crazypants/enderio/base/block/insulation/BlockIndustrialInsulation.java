package crazypants.enderio.base.block.insulation;

import java.util.List;
import java.util.Queue;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.google.common.collect.Lists;

import crazypants.enderio.base.BlockEio;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.TileEntityEio;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.render.IDefaultRenderers;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;

public class BlockIndustrialInsulation extends BlockEio<TileEntityEio> implements IResourceTooltipProvider, IDefaultRenderers {

  public static BlockIndustrialInsulation create(@Nonnull IModObject modObject) {
    BlockIndustrialInsulation result = new BlockIndustrialInsulation(modObject);
    result.init();
    return result;
  }

  protected BlockIndustrialInsulation(@Nonnull IModObject modObject) {
    super(modObject, Material.SPONGE);
    setSoundType(SoundType.CLOTH);
    setCreativeTab(EnderIOTab.tabEnderIO);
  }

  @Override
  @Nonnull
  public String getUnlocalizedNameForTooltip(@Nonnull ItemStack itemStack) {
    return getUnlocalizedName();
  }

  @Override
  public void onBlockAdded(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
    absorb(worldIn, pos);
  }

  @Override
  public void neighborChanged(@Nonnull IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Block blockIn, @Nonnull BlockPos fromPos) {
    absorb(worldIn, pos);
    super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
  }

  // Uses the sponge absorb method, easier than overriding the whole BlockSponge and removing all the BlockState code
  private boolean absorb(@Nonnull World worldIn, @Nonnull BlockPos pos) {
    Queue<Tuple<BlockPos, Integer>> queue = Lists.<Tuple<BlockPos, Integer>> newLinkedList();
    List<BlockPos> list = Lists.<BlockPos> newArrayList();
    queue.add(new Tuple(pos, Integer.valueOf(0)));
    int i = 0;

    while (!queue.isEmpty()) {
      Tuple<BlockPos, Integer> tuple = (Tuple) queue.poll();
      BlockPos blockpos = tuple.getFirst();
      int j = ((Integer) tuple.getSecond()).intValue();

      for (EnumFacing enumfacing : EnumFacing.values()) {
        BlockPos blockpos1 = blockpos.offset(enumfacing);

        IBlockState blockToCheck = worldIn.getBlockState(blockpos1);
        if (blockToCheck.getBlock() instanceof BlockFluidBase || blockToCheck.getMaterial() == Material.WATER || blockToCheck.getMaterial() == Material.LAVA) {
          worldIn.setBlockState(blockpos1, Blocks.AIR.getDefaultState(), 2);
          list.add(blockpos1);
          ++i;

          if (j < 6) {
            queue.add(new Tuple(blockpos1, j + 1));
          }
        }
      }

      if (i > 64) {
        break;
      }
    }

    for (BlockPos blockpos2 : list) {
      worldIn.notifyNeighborsOfStateChange(blockpos2, Blocks.AIR, false);
    }

    return i > 0;
  }

}
