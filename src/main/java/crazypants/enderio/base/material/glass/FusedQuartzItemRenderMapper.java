package crazypants.enderio.base.material.glass;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;

import com.enderio.core.common.fluid.SmartTank;

import crazypants.enderio.base.render.ICacheKey;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.base.render.property.EnumMergingBlockRenderMode;
import crazypants.enderio.base.render.util.ItemQuadCollector;
import crazypants.enderio.base.render.util.TankRenderHelper;
import crazypants.enderio.base.render.util.HalfBakedQuad.HalfBakedList;
import crazypants.enderio.util.NbtValue;
import info.loenwind.autosave.helpers.SmartTankItemAccess;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.base.render.property.EnumMergingBlockRenderMode.RENDER;

public class FusedQuartzItemRenderMapper implements IRenderMapper.IItemRenderMapper.IItemStateMapper {

  public static final @Nonnull FusedQuartzItemRenderMapper instance = new FusedQuartzItemRenderMapper();

  protected FusedQuartzItemRenderMapper() {
  }

  @Override
  @SideOnly(Side.CLIENT)
  public List<Pair<IBlockState, ItemStack>> mapItemRender(@Nonnull Block block, @Nonnull ItemStack stack, @Nonnull ItemQuadCollector itemQuadCollector) {

    if (NbtValue.FAKE.hasTag(stack)) {
      // this is for the TOP overlay, kind of a hack putting it here, but the alternative would be adding a new item just for this...
      SmartTank tank = SmartTankItemAccess.getTank(stack);
      HalfBakedList buffer = TankRenderHelper.mkTank(tank, 0, 0, 16, true);
      if (buffer != null) {
        List<BakedQuad> quads = new ArrayList<BakedQuad>();
        buffer.bake(quads);
        itemQuadCollector.addQuads(null, quads);
      }
      return null;
    }

    List<Pair<IBlockState, ItemStack>> states = new ArrayList<Pair<IBlockState, ItemStack>>();
    IBlockState defaultState = block.getDefaultState();
    FusedQuartzType bankType = FusedQuartzType.getTypeFromMeta(stack.getItemDamage());
    defaultState = defaultState.withProperty(FusedQuartzType.KIND, bankType);

    states.add(Pair.of(defaultState.withProperty(RENDER, EnumMergingBlockRenderMode.sides), (ItemStack) null));

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
  public @Nonnull ICacheKey getCacheKey(@Nonnull Block block, @Nonnull ItemStack stack, @Nonnull ICacheKey cacheKey) {
    return cacheKey;
  }

}
