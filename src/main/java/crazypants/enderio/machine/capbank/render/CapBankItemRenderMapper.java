package crazypants.enderio.machine.capbank.render;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.render.property.EnumMergingBlockRenderMode.RENDER;

import org.apache.commons.lang3.tuple.Pair;

import cofh.api.energy.IEnergyContainerItem;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.capbank.CapBankType;
import crazypants.enderio.render.ICacheKey;
import crazypants.enderio.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.render.property.EnumMergingBlockRenderMode;
import crazypants.enderio.render.util.ItemQuadCollector;

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
    if (stack.getItem() instanceof IEnergyContainerItem) {
      IEnergyContainerItem energyItem = (IEnergyContainerItem) stack.getItem();
      int maxEnergy = energyItem.getMaxEnergyStored(stack);
      if (maxEnergy > 0) {
        int energy = energyItem.getEnergyStored(stack);
        FillGaugeBakery gauge = new FillGaugeBakery(EnderIO.blockCapBank.getGaugeIcon(), (double) energy / maxEnergy);
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
