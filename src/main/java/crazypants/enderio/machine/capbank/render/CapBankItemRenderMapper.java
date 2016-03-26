package crazypants.enderio.machine.capbank.render;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.commons.lang3.tuple.Pair;

import crazypants.enderio.machine.capbank.CapBankType;
import crazypants.enderio.render.EnumMergingBlockRenderMode;
import crazypants.enderio.render.ICacheKey;
import crazypants.enderio.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.render.pipeline.ItemQuadCollector;
import static crazypants.enderio.render.EnumMergingBlockRenderMode.RENDER;

public class CapBankItemRenderMapper implements IItemRenderMapper.IItemStateMapper, IItemRenderMapper.IDynamicOverlayMapper {

  public CapBankItemRenderMapper() {
  }

  @Override
  @SideOnly(Side.CLIENT)
  public List<Pair<IBlockState, ItemStack>> mapItemRender(Block block, ItemStack stack, ItemQuadCollector itemQuadCollector) {
    List<Pair<IBlockState, ItemStack>> states = new ArrayList<Pair<IBlockState, ItemStack>>();
    IBlockState defaultState = block.getDefaultState();
    states.add(Pair.of(defaultState.withProperty(RENDER, EnumMergingBlockRenderMode.sides).withProperty(CapBankType.KIND, CapBankType.NONE), (ItemStack) null));
    CapBankType bankType = CapBankType.getTypeFromMeta(stack.getItemDamage());
    defaultState = defaultState.withProperty(CapBankType.KIND, bankType);
    for (EnumFacing facing : EnumFacing.Plane.HORIZONTAL) {
      states.add(Pair.of(defaultState.withProperty(RENDER, EnumMergingBlockRenderMode.get(facing, EnumFacing.UP)), (ItemStack) null));
      states.add(Pair.of(defaultState.withProperty(RENDER, EnumMergingBlockRenderMode.get(facing, EnumFacing.DOWN)), (ItemStack) null));
      states.add(Pair.of(defaultState.withProperty(RENDER, EnumMergingBlockRenderMode.get(facing, facing.rotateYCCW())), (ItemStack) null));
      states.add(Pair.of(defaultState.withProperty(RENDER, EnumMergingBlockRenderMode.get(facing, facing.rotateYCCW(), EnumFacing.UP)), (ItemStack) null));
      states.add(Pair.of(defaultState.withProperty(RENDER, EnumMergingBlockRenderMode.get(facing, facing.rotateYCCW(), EnumFacing.DOWN)), (ItemStack) null));
    }
    return states;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public ItemQuadCollector mapItemDynamicOverlayRender(Block block, ItemStack stack) {
    // TODO: Create a fill level overlay here
    return null;
  }

  @Override
  public ICacheKey getCacheKey(Block block, ItemStack stack, ICacheKey cacheKey) {
    return cacheKey;
  }

}
