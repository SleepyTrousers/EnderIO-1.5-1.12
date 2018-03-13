package crazypants.enderio.machines.machine.ihopper;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.base.render.property.EnumRenderMode;
import crazypants.enderio.base.render.util.ItemQuadCollector;
import crazypants.enderio.base.render.util.QuadCollector;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ImpulseRenderMapper implements IRenderMapper.IBlockRenderMapper, IRenderMapper.IItemRenderMapper.IItemStateMapper {

  public static final @Nonnull ImpulseRenderMapper instance = new ImpulseRenderMapper();

  public ImpulseRenderMapper() {
  }

  @Override
  @SideOnly(Side.CLIENT)
  @Nullable
  public List<Pair<IBlockState, ItemStack>> mapItemRender(@Nonnull Block block, @Nonnull ItemStack stack, @Nonnull ItemQuadCollector itemQuadCollector) {
    List<Pair<IBlockState, ItemStack>> states = new ArrayList<Pair<IBlockState, ItemStack>>();
    states.add(Pair.of(block.getStateFromMeta(stack.getMetadata()).withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT), stack));
    return states;
  }

  protected List<IBlockState> render(IBlockState state, IBlockAccess world, BlockPos pos, BlockRenderLayer blockLayer, TileImpulseHopper tileEntity,
      BlockImpulseHopper block) {
    List<IBlockState> states = new ArrayList<IBlockState>();

    states.add(state.withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT));

    return states;
  }

  @Override
  @SideOnly(Side.CLIENT)
  @Nullable
  public List<IBlockState> mapBlockRender(@Nonnull IBlockStateWrapper state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, BlockRenderLayer blockLayer,
      @Nonnull QuadCollector quadCollector) {
    TileEntity te = state.getTileEntity();
    Block block = state.getBlock();

    if ((te instanceof TileImpulseHopper) && (block instanceof BlockImpulseHopper)) {
      return render(state.getState(), world, pos, blockLayer, (TileImpulseHopper) te, (BlockImpulseHopper) block);
    }

    return null;
  }

}
