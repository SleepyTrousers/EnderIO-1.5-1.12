package crazypants.enderio.machine.capbank.render;

import crazypants.enderio.machine.capbank.BlockCapBank;
import crazypants.enderio.machine.capbank.CapBankType;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.enderio.render.ICacheKey;
import crazypants.enderio.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.render.property.EnumMergingBlockRenderMode;
import crazypants.enderio.render.util.ItemQuadCollector;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static crazypants.enderio.ModObject.blockCapBank;
import static crazypants.enderio.render.property.EnumMergingBlockRenderMode.RENDER;

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
    IEnergyStorage energyItem = PowerHandlerUtil.getCapability(stack, null);
    if (energyItem != null) {
      int maxEnergy = energyItem.getMaxEnergyStored();
      if (maxEnergy > 0) {
        int energy = energyItem.getEnergyStored();
        FillGaugeBakery gauge = new FillGaugeBakery(((BlockCapBank) blockCapBank.getBlock()).getGaugeIcon(), (double) energy / maxEnergy);
        if (gauge.canRender()) {
          ItemQuadCollector result = new ItemQuadCollector();
          List<BakedQuad> quads = new ArrayList<BakedQuad>();
          gauge.bake(quads);
          result.addQuads(null, quads);
          return result;
        }
      }
    }
    return null;
  }

  @Override
  public @Nonnull ICacheKey getCacheKey(@Nonnull Block block, @Nonnull ItemStack stack, @Nonnull ICacheKey cacheKey) {
    return cacheKey;
  }

}
