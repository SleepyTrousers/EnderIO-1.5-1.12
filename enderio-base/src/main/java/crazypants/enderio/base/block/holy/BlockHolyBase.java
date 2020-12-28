package crazypants.enderio.base.block.holy;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.BlockEio;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.TileEntityEio;
import crazypants.enderio.base.init.ModObjectRegistry;
import crazypants.enderio.base.render.IDefaultRenderers;
import crazypants.enderio.base.render.ranged.InfinityParticle;
import crazypants.enderio.util.NNPair;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialTransparent;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
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

public abstract class BlockHolyBase extends BlockEio<TileEntityEio> implements IDefaultRenderers, IResourceTooltipProvider {

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

  public BlockHolyBase(@Nonnull IModObject modObject) {
    super(modObject, new MaterialTransparent(MapColor.AIR)); // not Material.AIR because AIR doesn't get update ticks
    setCreativeTab(EnderIOTab.tabEnderIOMaterials);
    // volume -1 gives effective volume of 0 when used by ItemBlock
    setSoundType(new SoundType(-1.0F, 1.0F, SoundEvents.BLOCK_CLOTH_BREAK, SoundEvents.BLOCK_CLOTH_STEP, SoundEvents.BLOCK_CLOTH_PLACE,
        SoundEvents.BLOCK_CLOTH_HIT, SoundEvents.BLOCK_CLOTH_FALL));
    initDefaultState();
    setShape(mkShape(BlockFaceShape.UNDEFINED));
    setLightLevel(1);
    setTickRandomly(true);
  }

  protected abstract void setQuanta(@Nonnull World world, @Nonnull BlockPos pos, int quanta, int delay);

  protected abstract void initDefaultState();

  @Override
  public boolean canRenderInLayer(@Nonnull IBlockState state, @Nonnull BlockRenderLayer layer) {
    return false;
  }

  @Override
  @Nonnull
  public EnumBlockRenderType getRenderType(@Nonnull IBlockState state) {
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

  protected abstract int getQuanta(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state);

  private long nextTick = 0;
  private int countPerTick = 0;

  @Override
  public void updateTick(final @Nonnull World world, final @Nonnull BlockPos pos, final @Nonnull IBlockState state, final @Nonnull Random rnd) {
    if (!world.isRemote) {
      // (0) Updating the world with blocks that give off light can induce lag.
      // We try to avoid this by
      // (a) only updating every 1/4 second
      // (b) concentrating the updates into those ticks
      // (c) limiting the number of updates
      final long currentTick = EnderIO.proxy.getServerTickCount();
      if (currentTick % 5 != 0) {
        world.scheduleBlockUpdate(pos, this, (int) (5 - (currentTick % 5)), -1);
        return;
      } else if (nextTick <= currentTick) {
        nextTick = currentTick + 1;
        countPerTick = 0;
      }
      if (countPerTick > 40) {
        world.scheduleBlockUpdate(pos, this, 5, -2);
        return;
      }

      final int ourQuanta = getQuanta(world, pos, state);

      // (1) try to drop down
      IBlockState down = world.getBlockState(pos.down());
      if (down.getBlock() == Blocks.AIR) { // not isAir() on purpose. We do not want to flow into "fake air" here
        setQuanta(world, pos, 0, 0);
        setQuanta(world, pos.down(), ourQuanta, 5);
        countPerTick++;
        return;
      }

      // (1.2) Liquids absorb dust
      if (down.getMaterial().isLiquid()) {
        setQuanta(world, pos, 0, 0);
        countPerTick++;
        return;
      }

      // (2) try to flow down
      int downQuanta = getQuanta(world, pos.down(), down);
      if (downQuanta > 0 && downQuanta < getMaxQuanta()) {
        downQuanta += ourQuanta;
        if (downQuanta <= getMaxQuanta()) {
          setQuanta(world, pos, 0, 0);
          setQuanta(world, pos.down(), downQuanta, 5);
        } else {
          int myQuanta = downQuanta - getMaxQuanta();
          downQuanta = getMaxQuanta();
          setQuanta(world, pos, myQuanta, 6); // give the down block a chance to flow away before we flow again
          setQuanta(world, pos.down(), downQuanta, 5);
        }
        countPerTick++;
        return;
      }

      // (3) try to flow outwards by equalizing
      int totalQuanta = ourQuanta;
      boolean nearWater = false;
      NNList<NNPair<BlockPos, IBlockState>> list = new NNList<>();
      for (NNIterator<EnumFacing> itr = NNList.FACING_HORIZONTAL.fastIterator(); itr.hasNext();) {
        EnumFacing facing = itr.next();
        BlockPos neighborPos = pos.offset(facing);
        if (world.isBlockLoaded(neighborPos)) {
          IBlockState neighbor = world.getBlockState(neighborPos);
          if (neighbor.getBlock() == Blocks.AIR) { // see above
            list.add(NNPair.of(neighborPos, neighbor));
          } else {
            int neighborQuanta = getQuanta(world, neighborPos, neighbor);
            if (neighborQuanta > 0) {
              totalQuanta += neighborQuanta;
              list.add(NNPair.of(neighborPos, neighbor));
            } else if (neighbor.getMaterial().isLiquid()) {
              nearWater = true;
            }
          }
        }
      }

      // (3.1) no neighbors to flow into
      if (!nearWater && list.isEmpty()) {
        return;
      }

      // (3.1.2) more water
      if (nearWater) {
        totalQuanta /= 2;
      }

      // (3.2) calculate how much to distribute
      list.add(0, NNPair.of(pos, state));
      int quantaPerBlock = totalQuanta / list.size();

      // (3.3) less than 1 quanta per block?
      if (totalQuanta > 0 && quantaPerBlock == 0) {
        // there's not enough for all targets, remove some air blocks until there is (or there's no more air)
        for (NNIterator<NNPair<BlockPos, IBlockState>> itr = list.iterator(); itr.hasNext();) {
          NNPair<BlockPos, IBlockState> next = itr.next();
          if (next.getValue().getBlock() == Blocks.AIR) {
            itr.remove();
            quantaPerBlock = totalQuanta / list.size();
            if (quantaPerBlock > 0) {
              break;
            }
          }
        }
      }

      // (3.4) need to put different amounts into different places?
      int extraQuanta = totalQuanta - (quantaPerBlock * list.size());
      if (extraQuanta > 0) {
        // avoid oscillation by keeping those extras in place
        for (NNIterator<NNPair<BlockPos, IBlockState>> itr = list.iterator(); itr.hasNext();) {
          NNPair<BlockPos, IBlockState> next = itr.next();
          if (getQuanta(world, next.getLeft(), next.getValue()) == quantaPerBlock + 1) {
            itr.remove();
            extraQuanta--;
            if (extraQuanta == 0) {
              break;
            }
          }
        }
      }

      // (4) now set the blocks
      boolean hasUpdated = false;
      for (NNIterator<NNPair<BlockPos, IBlockState>> itr = list.iterator(); itr.hasNext();) {
        NNPair<BlockPos, IBlockState> next = itr.next();
        int q = quantaPerBlock;
        if (extraQuanta > 0) {
          q++;
          extraQuanta--;
        }
        @SuppressWarnings("null")
        final @Nonnull BlockPos target = next.getKey();
        if (getQuanta(world, next.getLeft(), next.getValue()) != q) {
          setQuanta(world, target, q, 5);
          hasUpdated = true;
        }
      }
      if (hasUpdated) {
        countPerTick++;
      }
    }
  }

  protected abstract int getMaxQuanta();

  @Override
  public void onEntityCollidedWithBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Entity entityIn) {
    if (!world.isRemote && entityIn instanceof EntityLivingBase && ((EntityLivingBase) entityIn).getCreatureAttribute() == EnumCreatureAttribute.UNDEAD
        && world.rand.nextFloat() < 0.05f) {
      entityIn.attackEntityFrom(DamageSource.HOT_FLOOR, 1.0F);
    }
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void randomDisplayTick(@Nonnull IBlockState bs, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random random) {
    final int quanta = getQuanta(world, pos, bs)
        + (Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() == ModObjectRegistry.getItem(ModObjectRegistry.getModObjectNN(this)) ? 20 : 0);
    if (random.nextFloat() < ((quanta >= getMaxQuanta() ? 1f : quanta > 10 ? 0.01f : 0.001f) * quanta)) {
      makeParticle(world, pos, random);
    }
  }

  public BlockHolyBase(@Nonnull IModObject modObject, @Nonnull Material mat, MapColor mapColor) {
    super(modObject, mat, mapColor);
  }

  @Override
  @Nonnull
  public String getUnlocalizedNameForTooltip(@Nonnull ItemStack itemStack) {
    return getUnlocalizedName();
  }

  @Override
  public void randomTick(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Random random) {
    if (world.isRainingAt(pos) && random.nextFloat() < .1f) {
      setQuanta(world, pos, (int) (getQuanta(world, pos, state) * random.nextFloat()), 0);
    }
  }

  @Override
  public void fillWithRain(@Nonnull World world, @Nonnull BlockPos pos) {
    // will never be called for us because we are an air block
    // see randomTick() instead
  }

}