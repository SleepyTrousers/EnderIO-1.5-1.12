package crazypants.enderio.machines.machine.killera;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import com.enderio.core.api.client.render.VertexTransform;
import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.VertexRotation;
import com.enderio.core.client.render.VertexRotationFacing;
import com.enderio.core.client.render.VertexScale;
import com.enderio.core.client.render.VertexTranslation;
import com.enderio.core.common.fluid.SmartTank;
import com.enderio.core.common.vecmath.Vector3d;

import crazypants.enderio.base.fluid.ItemTankHelper;
import crazypants.enderio.base.machine.base.te.AbstractMachineEntity;
import crazypants.enderio.base.machine.render.MachineRenderMapper;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.ICacheKey;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.base.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.base.render.property.EnumRenderMode;
import crazypants.enderio.base.render.registry.TextureRegistry.TextureSupplier;
import crazypants.enderio.base.render.util.HalfBakedQuad.HalfBakedList;
import crazypants.enderio.base.render.util.ItemQuadCollector;
import crazypants.enderio.base.render.util.QuadCollector;
import crazypants.enderio.base.render.util.TankRenderHelper;
import crazypants.enderio.machines.machine.generator.zombie.BlockZombieGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class KillerJoeRenderMapper extends MachineRenderMapper
    implements IRenderMapper.IBlockRenderMapper.IRenderLayerAware, IItemRenderMapper.IDynamicOverlayMapper {

  private final TextureSupplier head1, head2;

  @Nonnull
  public static final KillerJoeRenderMapper killerJoe = new KillerJoeRenderMapper(BlockKillerJoe.textureHead1, BlockKillerJoe.textureHead2);

  @Nonnull
  public static final KillerJoeRenderMapper zombieGen = new KillerJoeRenderMapper(BlockZombieGenerator.textureHead1, BlockZombieGenerator.textureHead2);

  @Nonnull
  public static final KillerJoeRenderMapper enderGen = new KillerJoeRenderMapper(BlockZombieGenerator.textureHeadEnder1, BlockZombieGenerator.textureHeadEnder2);

  protected KillerJoeRenderMapper(TextureSupplier head1, TextureSupplier head2) {
    super(null);
    this.head1 = head1;
    this.head2 = head2;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public List<IBlockState> mapBlockRender(@Nonnull IBlockStateWrapper state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, BlockRenderLayer blockLayer,
      @Nonnull QuadCollector quadCollector) {
    if (blockLayer == BlockRenderLayer.TRANSLUCENT) {
      return Collections.singletonList(state.getBlock().getDefaultState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT_ON));
    } else if (blockLayer == BlockRenderLayer.SOLID) {
      quadCollector.addQuads(null, BlockRenderLayer.SOLID, renderHead(state));
      return Collections.singletonList(state.getBlock().getDefaultState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT));
    }
    return null;
  }

  private static final double px = 1d / 16d;
  private static final @Nonnull Vector3d CENTER = new Vector3d(8 * px, 8 * px, 8 * px);
  private static final @Nonnull double[] ROTS = { 0, Math.PI / 2, Math.PI, Math.PI / 2 * 3 };

  private static final @Nonnull BoundingBox bb = new BoundingBox(4 * px, 4 * px, 4 * px, 12 * px, 12 * px, 12 * px);
  private static final @Nonnull VertexTransform sca = new VertexScale(.9, .9, .9, CENTER);
  private static final @Nonnull VertexTransform rotx = new VertexRotation(0.03054326, new Vector3d(1, 0, 0), CENTER);
  private static final @Nonnull VertexTransform roty = new VertexRotation(0.17453290, new Vector3d(0, 1, 0), CENTER);
  private static final @Nonnull VertexTransform rotz = new VertexRotation(0.23928460, new Vector3d(0, 0, 1), CENTER);
  private static final @Nonnull VertexTransform mov = new VertexTranslation(0.25 * px, -1 * px, 0);

  private List<BakedQuad> renderHead(@Nullable IBlockStateWrapper state) {
    EnumFacing facing = EnumFacing.NORTH;
    if (state != null) {
      TileEntity tileEntity = state.getTileEntity();
      if (tileEntity instanceof AbstractMachineEntity) {
        facing = ((AbstractMachineEntity) tileEntity).getFacing();
        if (facing.getAxis() == EnumFacing.Axis.X) {
          facing = facing.getOpposite();
        }
      }
    }

    VertexRotationFacing rot = new VertexRotationFacing(EnumFacing.NORTH);
    rot.setCenter(CENTER);
    rot.setRotation(facing);

    TextureAtlasSprite tex1 = head1.get(TextureAtlasSprite.class);
    TextureAtlasSprite tex2 = head2.get(TextureAtlasSprite.class);

    HalfBakedList buffer = new HalfBakedList();

    buffer.add(bb, EnumFacing.NORTH, 0f, .5f, 0f, .5f, tex1, null);
    buffer.add(bb, EnumFacing.EAST, .5f, 1f, 0f, .5f, tex1, null);
    buffer.add(bb, EnumFacing.SOUTH, 0f, .5f, .5f, 1f, tex1, null);
    buffer.add(bb, EnumFacing.WEST, .5f, 1f, .5f, 1f, tex1, null);
    buffer.add(bb, EnumFacing.UP, 0f, .5f, 0f, .5f, tex2, null);
    buffer.add(bb, EnumFacing.DOWN, .5f, 1f, 0f, .5f, tex2, null);

    List<BakedQuad> quads = new ArrayList<BakedQuad>();
    buffer.bake(quads, sca, rotx, roty, rotz, mov, rot);

    for (double angle : ROTS) {
      buffer = new HalfBakedList();

      BoundingBox bb1 = new BoundingBox(4.5 * px, 10.5 * px, 3 * px, 5.5 * px, 11.5 * px, 4 * px);
      BoundingBox bb2 = new BoundingBox(7.5 * px, 9.5 * px, 3 * px, 8.5 * px, 10.5 * px, 4 * px);
      BoundingBox bb3 = new BoundingBox(10.5 * px, 10.5 * px, 3 * px, 11.5 * px, 11.5 * px, 4 * px);

      for (EnumFacing face : EnumFacing.values()) {
        buffer.add(bb1, face, (face.ordinal() + 1) * px, (face.ordinal() + 2) * px, 9 * px, 10 * px, tex2, null);
        buffer.add(bb2, face, (face.ordinal() + 1) * px, (face.ordinal() + 2) * px, 10 * px, 11 * px, tex2, null);
        buffer.add(bb3, face, (face.ordinal() + 1) * px, (face.ordinal() + 2) * px, 11 * px, 12 * px, tex2, null);
      }

      VertexTransform rota = new VertexRotation(angle, new Vector3d(0, 1, 0), CENTER);
      buffer.bake(quads, sca, rota, rotx, roty, rotz, mov, rot);
    }

    return quads;
  }

  private List<BakedQuad> renderFuel(ItemStack stack) {
    if (stack.hasTagCompound()) {
      SmartTank tank = ItemTankHelper.getTank(stack);
      HalfBakedList buffer = TankRenderHelper.mkTank(tank, 2.51, 1, 14, false);
      if (buffer != null) {
        List<BakedQuad> quads = new ArrayList<BakedQuad>();
        buffer.bake(quads);
        return quads;
      }
    }
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public List<Pair<IBlockState, ItemStack>> mapItemRender(@Nonnull Block block, @Nonnull ItemStack stack, @Nonnull ItemQuadCollector itemQuadCollector) {
    List<Pair<IBlockState, ItemStack>> states = new ArrayList<Pair<IBlockState, ItemStack>>();
    states.add(Pair.of(block.getStateFromMeta(stack.getMetadata()).withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT), stack));
    if (!stack.hasTagCompound()) {
      // glass needs to be rendered on top of fluid
      states.add(Pair.of(block.getStateFromMeta(stack.getMetadata()).withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT_ON), stack));
    }
    itemQuadCollector.addQuads(null, renderHead(null));
    return states;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull ICacheKey getCacheKey(@Nonnull Block block, @Nonnull ItemStack stack, @Nonnull ICacheKey cacheKey) {
    return cacheKey.addCacheKey(stack.hasTagCompound());
  }

  @Override
  @SideOnly(Side.CLIENT)
  public ItemQuadCollector mapItemDynamicOverlayRender(@Nonnull Block block, @Nonnull ItemStack stack) {
    if (stack.hasTagCompound()) {
      ItemQuadCollector result = new ItemQuadCollector();
      result.addQuads(null, renderFuel(stack));
      result.addBlockState(block.getStateFromMeta(stack.getMetadata()).withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT_ON), stack);
      // TODO: render sword here if it's a killer joe
      return result;
    } else {
      return null;
    }
  }

}
