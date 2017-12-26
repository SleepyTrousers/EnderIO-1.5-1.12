package crazypants.enderio.base.block.infinity;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.base.BlockEio;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.TileEntityEio;
import crazypants.enderio.base.block.painted.BlockItemPaintedBlock;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.render.IDefaultRenderers;
import crazypants.enderio.base.render.ranged.InfinityParticle;
import crazypants.enderio.util.CapturedMob;
import info.loenwind.scheduler.Celeb;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockInfinity extends BlockEio<TileEntityEio> implements IDefaultRenderers, IResourceTooltipProvider {

  public static final @Nonnull PropertyInteger AGE = PropertyInteger.create("age", 0, 7);
  public static final @Nonnull PropertyBool HARMLESS = PropertyBool.create("harmless");

  public static BlockInfinity create(@Nonnull IModObject modObject) {
    BlockInfinity result = new BlockInfinity(modObject, false);
    return result;
  }

  protected BlockInfinity(@Nonnull IModObject modObject, boolean silent) {
    super(modObject, null, Material.BARRIER);
    setCreativeTab(EnderIOTab.tabEnderIOMaterials);
    // volume -1 gives effective volume of 0 when used by ItemBlock
    setSoundType(new SoundType(-1.0F, 1.0F, SoundEvents.BLOCK_CLOTH_BREAK, SoundEvents.BLOCK_CLOTH_STEP, SoundEvents.BLOCK_CLOTH_PLACE,
        SoundEvents.BLOCK_CLOTH_HIT, SoundEvents.BLOCK_CLOTH_FALL));
    initDefaultState();
  }

  protected void initDefaultState() {
    setDefaultState(this.blockState.getBaseState());
  }

  @Override
  protected @Nonnull BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { AGE, HARMLESS });
  }

  @Override
  public @Nonnull IBlockState getStateFromMeta(int meta) {
    return getDefaultState().withProperty(AGE, meta & 7).withProperty(HARMLESS, meta > 7);
  }

  @Override
  public int getMetaFromState(@Nonnull IBlockState state) {
    return state.getValue(AGE) + (state.getValue(HARMLESS) ? 8 : 0);
  }

  @Override
  public Item createBlockItem(@Nonnull IModObject modObject) {
    return modObject.apply(new BlockItemPaintedBlock(this));
  }

  @Override
  public boolean doNormalDrops(IBlockAccess world, BlockPos pos) {
    return false;
  }

  private final @Nonnull Random rand = new Random();

  @Override
  public int getWeakPower(@Nonnull IBlockState state, @Nonnull IBlockAccess blockAccess, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
    return rand.nextInt(16);
  }

  @Override
  public boolean canProvidePower(@Nonnull IBlockState state) {
    return rand.nextBoolean();
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
  public void onBlockPlacedBy(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityLivingBase player,
      @Nonnull ItemStack stack) {
    if (!world.isRemote) {
      world.scheduleBlockUpdate(pos, this, rand.nextInt(40) + 1, 0);
    }
  }

  @Override
  public void updateTick(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Random rnd) {
    if (!world.isRemote) {
      if (world.canBlockSeeSky(pos)) { // exposed to the sky?
        world.setBlockToAir(pos);
        world.createExplosion(null, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, .1f, true);
        return;
      }

      IBlockState newState = rnd.nextFloat() < .165f ? state.cycleProperty(AGE) : state;
      final Boolean isHarmless = state.getValue(HARMLESS);
      if (newState.getValue(AGE) == 7) { // die of old age
        world.setBlockToAir(pos);
        if (!isHarmless) {
          if (rnd.nextFloat() < .165f) {
            spawnResult(world, pos, rnd);
          }
          if (rnd.nextFloat() < .33f) {
            spawnResult(world, pos, rnd);
          }
          spawnResult(world, pos, rnd);
          if (rnd.nextFloat() < .05f) {
            spawnAsEntity(world, pos, new ItemStack(this));
          }
        }
        return;
      } else {
        if (rnd.nextFloat() < .33f) {
          NNList<BlockPos> airs = new NNList<>();
          for (NNIterator<EnumFacing> itr = NNList.FACING.fastIterator(); itr.hasNext();) {
            BlockPos neighbor = pos.offset(itr.next());
            if (world.isAirBlock(neighbor)) {
              airs.add(neighbor);
            } else {
              IBlockState neighborState = world.getBlockState(neighbor);
              if (neighborState.getBlock() == this && neighborState.getValue(HARMLESS)) {
                airs.add(neighbor);
              }
            }
          }
          if (airs.size() >= 2) {
            if (!isHarmless && airs.size() >= 3 && rnd.nextFloat() < .025f) { // split
              BlockPos targetPos1 = airs.remove(rand.nextInt(airs.size()));
              BlockPos targetPos2 = airs.remove(rand.nextInt(airs.size()));
              world.setBlockState(targetPos1, newState);
              world.scheduleBlockUpdate(targetPos1, this, rand.nextInt(40), 0);
              world.setBlockState(targetPos2, newState);
              world.scheduleBlockUpdate(targetPos2, this, rand.nextInt(40), 0);
              world.setBlockToAir(pos);
              return;
            } else if (!isHarmless && rnd.nextFloat() < .025f) { // spawn
              BlockPos targetPos1 = airs.remove(rand.nextInt(airs.size()));
              world.setBlockState(targetPos1, state.withProperty(AGE, 0));
              world.scheduleBlockUpdate(targetPos1, this, rand.nextInt(40), 0);
              world.setBlockState(pos, newState);
              world.scheduleBlockUpdate(pos, this, rand.nextInt(40), 0);
              return;
            } else if (rnd.nextFloat() < .50f) { // move
              BlockPos targetPos1 = airs.remove(rand.nextInt(airs.size()));
              world.setBlockState(targetPos1, newState);
              world.scheduleBlockUpdate(targetPos1, this, rand.nextInt(40), 0);
              world.setBlockToAir(pos);
              return;
            } else if (!isHarmless) { // spawn harmless
              BlockPos targetPos1 = airs.remove(rand.nextInt(airs.size()));
              world.setBlockState(targetPos1, state.withProperty(AGE, 0).withProperty(HARMLESS, true));
              world.scheduleBlockUpdate(targetPos1, this, rand.nextInt(40), 0);
            }
          } else if (!isHarmless) { // caged in? no thank you!
            world.setBlockToAir(pos);
            world.createExplosion(null, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, .1f, true);
            return;
          }
        }

        if (state != newState) {
          world.setBlockState(pos, newState);
        }
        world.scheduleBlockUpdate(pos, this, rand.nextInt(40), 0);
      }
    }
  }

  protected void spawnResult(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rnd) {
    if (rnd.nextFloat() < .13f && Celeb.C24.isOn()) {
      spawn(world, pos, "snowman");
    }
    if (rnd.nextFloat() < .10f) {
      spawn(world, pos, "endermite");
    }
    switch (rnd.nextInt(10)) {
    case 0:
    case 1:
      if (rnd.nextFloat() < .20f) {
        spawn(world, pos, "silverfish");
      }
    case 2:
      if (rnd.nextFloat() < .10f) {
        spawn(world, pos, "silverfish");
        spawn(world, pos, "silverfish");
      }
      return;
    case 3:
      if (rnd.nextFloat() < .20f) {
        spawn(world, pos, "slime");
      }
      return;
    case 4:
      if (rnd.nextFloat() < .33f) {
        spawn(world, pos, "enderman");
      } else if (rnd.nextFloat() < .01f) {
        spawn(world, pos, "enderman");
        spawn(world, pos, "enderman");
        spawn(world, pos, "enderman");
        spawn(world, pos, "enderman");
        spawn(world, pos, "enderman");
      }
      return;
    case 5:
    case 6:
      if (rnd.nextFloat() < .15f) {
        spawn(world, pos, "bat");
      }
    case 7:
      if (rnd.nextFloat() < .15f) {
        spawn(world, pos, "bat");
      }
      return;
    case 8:
      if (rnd.nextFloat() < .025f) {
        @SuppressWarnings("null")
        EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(world, pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, null);
        world.spawnEntity(entitytntprimed);
        world.playSound((EntityPlayer) null, entitytntprimed.posX, entitytntprimed.posY, entitytntprimed.posZ, SoundEvents.ENTITY_TNT_PRIMED,
            SoundCategory.BLOCKS, 1.0F, 1.0F);
      }
      return;
    default:
      if (rnd.nextFloat() < .25f) {
        spawn(world, pos, "endermite");
      }
      return;
    }
  }

  protected void spawn(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull String enitytype) {
    CapturedMob capturedMob = CapturedMob.create(new ResourceLocation("minecraft", enitytype));
    if (capturedMob != null) {
      Entity ent = capturedMob.getEntity(world, pos, world.getDifficultyForLocation(pos), false);
      if (ent != null) {
        world.spawnEntity(ent);
        world.playEvent(2004, pos, 0);
      }
    }
  }

  @Override
  public void onEntityCollidedWithBlock(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Entity entityIn) {
    entityIn.attackEntityFrom(DamageSource.OUT_OF_WORLD, 1.0F);
    if (!worldIn.isRemote) {
      if (rand.nextFloat() < .50f) {
        entityIn.setRotationYawHead(rand.nextFloat());
      }
      if (rand.nextFloat() < .50f) {
        entityIn.setVelocity(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
      }
      if (rand.nextFloat() < .10f) {
        entityIn.extinguish();
      }
      if (rand.nextFloat() < .10f) {
        entityIn.setFire(1);
      }
      if (rand.nextFloat() < .10f && entityIn instanceof EntityLivingBase) {
        ((EntityLivingBase) entityIn).heal(1f);
      }
      if (rand.nextFloat() < .05f && entityIn instanceof EntityLivingBase) {
        ((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 20, 0, true, true));
      }
      if (rand.nextFloat() < .05f && entityIn instanceof EntityLivingBase) {
        ((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.SPEED, 20, 0, true, true));
      }
      if (rand.nextFloat() < .05f && entityIn instanceof EntityLivingBase) {
        ((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 20, 0, true, true));
      }
      if (rand.nextFloat() < .05f && entityIn instanceof EntityLivingBase) {
        ((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 20, 0, true, true));
      }
      if (rand.nextFloat() < .05f && entityIn instanceof EntityLivingBase) {
        ((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.HUNGER, 20, 0, true, true));
      }
      if (rand.nextFloat() < .05f && entityIn instanceof EntityLivingBase) {
        ((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.GLOWING, 20, 0, true, true));
      }
      if (rand.nextFloat() < .33f && entityIn instanceof EntityLivingBase) {
        ((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.LEVITATION, 20, 0, true, true));
      }
    }
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void randomDisplayTick(@Nonnull IBlockState bs, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rnd) {
    for (int i = 0; i < 3; i++) {
      float offsetX = (.25f + .5f * rnd.nextFloat());
      float offsetY = (.25f + .5f * rnd.nextFloat());
      float offsetZ = (.25f + .5f * rnd.nextFloat());
      float maxSize = Math.min(Math.min(Math.min(1f - offsetX, offsetX), Math.min(1f - offsetY, offsetY)), Math.min(1f - offsetZ, offsetZ))
          * (.5f + .5f * rnd.nextFloat()) * 2;
      float color = (i == 0 && !bs.getValue(HARMLESS)) ? 0 : rnd.nextFloat();
      Minecraft.getMinecraft().effectRenderer
          .addEffect(new InfinityParticle(world, pos, new Vector4f(color, color, color, 0.4f), new Vector4f(offsetX, offsetY, offsetZ, maxSize)));
    }
  }

  @Override
  @Nonnull
  public String getUnlocalizedNameForTooltip(@Nonnull ItemStack itemStack) {
    return getUnlocalizedName();
  }

}
