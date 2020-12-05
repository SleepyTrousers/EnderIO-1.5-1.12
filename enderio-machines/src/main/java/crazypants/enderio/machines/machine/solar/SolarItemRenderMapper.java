package crazypants.enderio.machines.machine.solar;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;

import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.base.render.property.EnumMergingBlockRenderMode;
import crazypants.enderio.base.render.util.ItemQuadCollector;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.base.render.property.EnumMergingBlockRenderMode.RENDER;

public class SolarItemRenderMapper implements IRenderMapper.IItemRenderMapper.IItemStateMapper {

  public static final @Nonnull SolarItemRenderMapper instance = new SolarItemRenderMapper();

  protected SolarItemRenderMapper() {
  }

  @Override
  @SideOnly(Side.CLIENT)
  public List<Pair<IBlockState, ItemStack>> mapItemRender(@Nonnull Block block, @Nonnull ItemStack stack, @Nonnull ItemQuadCollector itemQuadCollector) {
    List<Pair<IBlockState, ItemStack>> states = new ArrayList<Pair<IBlockState, ItemStack>>();
    IBlockState defaultState = block.getDefaultState();
    ISolarType bankType = ISolarType.getTypeFromMeta(stack.getItemDamage());
    defaultState = defaultState.withProperty(SolarType.KIND, bankType);

    states.add(Pair.of(defaultState.withProperty(RENDER, EnumMergingBlockRenderMode.sides), (ItemStack) null));

    for (EnumFacing facing : EnumFacing.Plane.HORIZONTAL) {
      states.add(Pair.of(defaultState.withProperty(RENDER, EnumMergingBlockRenderMode.get(facing, EnumFacing.DOWN)), (ItemStack) null));
      states.add(Pair.of(defaultState.withProperty(RENDER, EnumMergingBlockRenderMode.get(facing, facing.rotateYCCW(), EnumFacing.DOWN)), (ItemStack) null));
    }
    return states;
  }

}
