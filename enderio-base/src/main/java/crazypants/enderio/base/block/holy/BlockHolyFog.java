package crazypants.enderio.base.block.holy;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.BlockEio;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.TileEntityEio;
import crazypants.enderio.base.render.IDefaultRenderers;
import crazypants.enderio.base.render.ranged.InfinityParticle;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.MaterialTransparent;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockHolyFog extends BlockEio<TileEntityEio> implements IDefaultRenderers, IResourceTooltipProvider {

  public static final @Nonnull PropertyInteger GEN = PropertyInteger.create("amount", 0, 15);

  public static BlockHolyFog create(@Nonnull IModObject modObject) {
    BlockHolyFog result = new BlockHolyFog(modObject, false);
    return result;
  }

  protected BlockHolyFog(@Nonnull IModObject modObject, boolean silent) {
    super(modObject, new MaterialTransparent(MapColor.AIR)); // not Material.AIR because AIR doesn't get update ticks
    setCreativeTab(EnderIOTab.tabEnderIOMaterials);
    // volume -1 gives effective volume of 0 when used by ItemBlock
    setSoundType(new SoundType(-1.0F, 1.0F, SoundEvents.BLOCK_CLOTH_BREAK, SoundEvents.BLOCK_CLOTH_STEP, SoundEvents.BLOCK_CLOTH_PLACE,
        SoundEvents.BLOCK_CLOTH_HIT, SoundEvents.BLOCK_CLOTH_FALL));
    initDefaultState();
    setShape(mkShape(BlockFaceShape.UNDEFINED));
    setLightLevel(1);
  }

  protected void initDefaultState() {
    setDefaultState(getBlockState().getBaseState().withProperty(GEN, 15));
  }

  @Override
  protected @Nonnull BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { GEN });
  }

  @Override
  public @Nonnull IBlockState getStateFromMeta(int meta) {
    return getDefaultState().withProperty(GEN, meta);
  }

  @Override
  public int getMetaFromState(@Nonnull IBlockState state) {
    return state.getValue(GEN);
  }

  @Override
  public @Nonnull IBlockState getStateForPlacement(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, float hitX, float hitY,
      float hitZ, int meta, @Nonnull EntityLivingBase placer) {
    return getDefaultState();
  }

  @Override
  public boolean canRenderInLayer(@Nonnull IBlockState state, @Nonnull BlockRenderLayer layer) {
    return false;
  }

  @Override
  public @Nonnull EnumBlockRenderType getRenderType(@Nonnull IBlockState state) {
    return EnumBlockRenderType.INVISIBLE;
  }

  @Override
  @Nullable
  public AxisAlignedBB getCollisionBoundingBox(@Nonnull IBlockState blockStateIn, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
    return NULL_AABB;
  }

  @Override
  public boolean isOpaqueCube(@Nonnull IBlockState state) {
    return false;
  }

  @Override
  public boolean canCollideCheck(@Nonnull IBlockState state, boolean hitIfLiquid) {
    return false;
  }

  @Override
  public void dropBlockAsItemWithChance(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, float chance, int fortune) {
  }

  @Override
  public boolean isReplaceable(@Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
    return true;
  }

  @Override
  public boolean isFullCube(@Nonnull IBlockState state) {
    return false;
  }

  @Override
  public boolean isAir(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    return true;
  }

  @Override
  public void onBlockPlaced(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityLivingBase player,
      @Nonnull ItemStack stack) {
    if (!world.isRemote) {
      world.scheduleBlockUpdate(pos, this, 5, 0);
    } else {
      for (int i = 0; i < 5; i++) {
        makeParticle(world, pos, world.rand);
      }
    }
  }

  @Override
  public void neighborChanged(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Block blockIn, @Nonnull BlockPos fromPos) {
    if (!world.isRemote) {
      world.scheduleBlockUpdate(pos, this, 5, 0);
    }
  }

  private int getQuanta(IBlockState state) {
    return state == null || !(state.getBlock() instanceof BlockHolyFog) ? 0 : state.getValue(GEN) + 1;
  }

  @Override
  public void updateTick(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Random rnd) {
    if (!world.isRemote) {

      // (1) try to drop down
      IBlockState down = world.getBlockState(pos.down());
      if (down.getBlock() == Blocks.AIR) { // not isAir() on purpose. We do not want to flow into "fake air" here
        world.setBlockToAir(pos);
        world.setBlockState(pos.down(), state);
        world.scheduleBlockUpdate(pos.down(), this, 5, 0);
        return;
      }

      // (2) try to flow down
      int downQuanta = getQuanta(down);
      if (downQuanta > 0 && downQuanta < 16) {
        downQuanta += getQuanta(state);
        if (downQuanta <= 16) {
          world.setBlockToAir(pos);
          world.setBlockState(pos.down(), state.withProperty(GEN, downQuanta - 1));
          world.scheduleBlockUpdate(pos.down(), this, 5, 0);
        } else {
          int myQuanta = downQuanta - 16;
          downQuanta = 16;
          world.setBlockState(pos, state.withProperty(GEN, myQuanta - 1));
          world.scheduleBlockUpdate(pos, this, 6, 0); // give the down block a chance to flow away before we flow again
          world.setBlockState(pos.down(), state.withProperty(GEN, downQuanta - 1));
          world.scheduleBlockUpdate(pos.down(), this, 5, 0);
        }
        return;
      }

      // (3) try to flow outwards by equalizing
      int totalQuanta = getQuanta(state);
      NNList<Pair<BlockPos, IBlockState>> list = new NNList<>();
      for (NNIterator<EnumFacing> itr = NNList.FACING_HORIZONTAL.fastIterator(); itr.hasNext();) {
        EnumFacing facing = itr.next();
        IBlockState neighbor = world.getBlockState(pos.offset(facing));
        if (neighbor.getBlock() == Blocks.AIR) { // see above
          list.add(Pair.of(pos.offset(facing), neighbor));
        } else if (neighbor.getBlock() instanceof BlockHolyFog) {
          totalQuanta += getQuanta(neighbor);
          list.add(Pair.of(pos.offset(facing), neighbor));
        }
      }

      // (3.1) no neighbors to flow into
      if (list.isEmpty()) {
        return;
      }

      // (3.2) calculate how much to distribute
      list.add(0, Pair.of(pos, state));
      int quantaPerBlock = totalQuanta / list.size();
      int extraQuanta = totalQuanta - (quantaPerBlock * list.size());

      // (3.3) less than 1 quanta per block?
      if (quantaPerBlock == 0) {
        // there's not enough for all targets, remove some air blocks until there is (or there's no more air)
        for (NNIterator<Pair<BlockPos, IBlockState>> itr = list.iterator(); itr.hasNext();) {
          Pair<BlockPos, IBlockState> next = itr.next();
          if (next.getValue().getBlock() == Blocks.AIR) {
            itr.remove();
            quantaPerBlock = totalQuanta / list.size();
            extraQuanta = totalQuanta - (quantaPerBlock * list.size());
            if (quantaPerBlock > 0) {
              break;
            }
          }
        }
      }

      // (3.4) need to put different amounts into different places?
      if (extraQuanta > 0) {
        // avoid oscillation by keeping those extras in place
        for (NNIterator<Pair<BlockPos, IBlockState>> itr = list.iterator(); itr.hasNext();) {
          Pair<BlockPos, IBlockState> next = itr.next();
          if (getQuanta(next.getValue()) == quantaPerBlock + 1) {
            itr.remove();
            extraQuanta--;
            if (extraQuanta == 0) {
              break;
            }
          }
        }
      }

      // (4) now set the blocks
      for (NNIterator<Pair<BlockPos, IBlockState>> itr = list.iterator(); itr.hasNext();) {
        Pair<BlockPos, IBlockState> next = itr.next();
        int q = quantaPerBlock;
        if (extraQuanta > 0) {
          q++;
          extraQuanta--;
        }
        @SuppressWarnings("null")
        final @Nonnull BlockPos target = next.getKey();
        if (getQuanta(next.getValue()) != q) {
          if (q < 1) {
            world.setBlockToAir(target);
          } else {
            world.setBlockState(target, state.withProperty(GEN, q - 1));
            world.scheduleBlockUpdate(target, this, 5, 0);
          }
        }
      }
    }
  }

  @Override
  public void onEntityCollidedWithBlock(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Entity entityIn) {
    if (!worldIn.isRemote && entityIn instanceof EntityZombie && RANDOM.nextFloat() < 0.05f) {
      entityIn.attackEntityFrom(DamageSource.HOT_FLOOR, 1.0F);
    }
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void randomDisplayTick(@Nonnull IBlockState bs, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rnd) {
    final int quanta = getQuanta(bs);
    if (rnd.nextFloat() < ((quanta == 16 ? 1f : quanta > 10 ? 0.01f : 0.001f) * quanta)) {
      makeParticle(world, pos, rnd);
    }
  }

  @SideOnly(Side.CLIENT)
  protected static void makeParticle(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rnd) {
    float offsetX = (.25f + .5f * rnd.nextFloat());
    float offsetY = (.25f + .5f * rnd.nextFloat());
    float offsetZ = (.25f + .5f * rnd.nextFloat());
    float maxSize = Math.min(Math.min(Math.min(1f - offsetX, offsetX), Math.min(1f - offsetY, offsetY)), Math.min(1f - offsetZ, offsetZ))
        * (.5f + .5f * rnd.nextFloat()) / 4f;
    Minecraft.getMinecraft().effectRenderer
        .addEffect(new InfinityParticle(world, pos, new Vector4f(0xFD / 255f, 1, 0, 0.4f), new Vector4f(offsetX, offsetY, offsetZ, maxSize)));
  }

  @Override
  @Nonnull
  public String getUnlocalizedNameForTooltip(@Nonnull ItemStack itemStack) {
    return getUnlocalizedName();
  }

}
