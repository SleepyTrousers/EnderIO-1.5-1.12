package crazypants.enderio.powertools.machine.capbank.render;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;

import crazypants.enderio.base.power.PowerHandlerUtil;
import crazypants.enderio.base.render.ICacheKey;
import crazypants.enderio.base.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.base.render.property.EnumMergingBlockRenderMode;
import crazypants.enderio.base.render.util.ItemQuadCollector;
import crazypants.enderio.powertools.machine.capbank.BlockCapBank;
import crazypants.enderio.powertools.machine.capbank.CapBankType;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.base.render.property.EnumMergingBlockRenderMode.RENDER;

@SideOnly(Side.CLIENT)
public class CapBankItemRenderMapper implements IItemRenderMapper.IItemStateMapper, IItemRenderMapper.IDynamicOverlayMapper {

  public static final @Nonnull CapBankItemRenderMapper instance = new CapBankItemRenderMapper();

  private CapBankItemRenderMapper() {
  }

  @Override
  @SideOnly(Side.CLIENT)
  public List<Pair<IBlockState, ItemStack>> mapItemRender(@Nonnull Block block, @Nonnull ItemStack stack, @Nonnull ItemQuadCollector itemQuadCollector) {
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
  public ItemQuadCollector mapItemDynamicOverlayRender(@Nonnull Block block, @Nonnull ItemStack stack) {
    IEnergyStorage energyItem = PowerHandlerUtil.getCapability(stack, null);
    if (energyItem != null) {
      int maxEnergy = energyItem.getMaxEnergyStored();
      if (maxEnergy > 0) {
        int energy = energyItem.getEnergyStored();
        FillGaugeBakery gauge = new FillGaugeBakery(BlockCapBank.getGaugeIcon(), (double) energy / maxEnergy);
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
