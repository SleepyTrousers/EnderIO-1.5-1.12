package crazypants.enderio.base.machine.base.block;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.util.Util;

import crazypants.enderio.base.BlockEio;
import crazypants.enderio.base.gui.handler.IEioGuiHandler;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.machine.base.te.AbstractMachineEntity;
import crazypants.enderio.base.machine.interfaces.IClearableConfiguration;
import crazypants.enderio.base.machine.modes.IoMode;
import crazypants.enderio.base.machine.render.RenderMappers;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.paint.render.PaintHelper;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.base.render.ISmartRenderAwareBlock;
import crazypants.enderio.base.render.pipeline.BlockStateWrapperBase;
import crazypants.enderio.base.render.property.EnumRenderMode;
import crazypants.enderio.base.render.property.IOMode;
import crazypants.enderio.base.render.registry.SmartModelAttacher;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class AbstractMachineBlock<T extends AbstractMachineEntity> extends BlockEio<T>
    implements IEioGuiHandler.WithPos, IResourceTooltipProvider, ISmartRenderAwareBlock, IClearableConfiguration {

  protected final @Nonnull Random random;
  protected boolean isEnhanced = false;

  protected AbstractMachineBlock(@Nonnull IModObject mo, @Nonnull Material mat) {
    super(mo, mat);
    setHardness(2.0F);
    setSoundType(SoundType.METAL);
    setHarvestLevel("pickaxe", 0);
    random = new Random();
    initDefaultState();
  }

  protected void initDefaultState() {
    setDefaultState(getBlockState().getBaseState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.AUTO));
  }

  protected AbstractMachineBlock(@Nonnull IModObject mo) {
    this(mo, new Material(MapColor.IRON));
  }

  @Override
  protected void init() {
    super.init();
    registerInSmartModelAttacher();
  }

  protected void registerInSmartModelAttacher() {
    SmartModelAttacher.register(this);
  }

  @Override
  protected @Nonnull BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { EnumRenderMode.RENDER });
  }

  @Override
  public @Nonnull IBlockState getStateFromMeta(int meta) {
    return getDefaultState();
  }

  @Override
  public int getMetaFromState(@Nonnull IBlockState state) {
    return 0;
  }

  @Override
  public @Nonnull IBlockState getActualState(@Nonnull IBlockState state, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
    return getDefaultState();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public final @Nonnull IBlockState getExtendedState(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    IBlockStateWrapper blockStateWrapper = createBlockStateWrapper(state, world, pos);
    T tileEntity = getTileEntitySafe(world, pos);
    if (tileEntity != null) {
      setBlockStateWrapperCache(blockStateWrapper, world, pos, tileEntity);
    }
    blockStateWrapper.bakeModel();
    return blockStateWrapper;
  }

  protected abstract void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull T tileEntity);

  protected @Nonnull BlockStateWrapperBase createBlockStateWrapper(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    return new BlockStateWrapperBase(state, world, pos, getBlockRenderMapper());
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull BlockRenderLayer getBlockLayer() {
    return BlockRenderLayer.CUTOUT;
  }

  @Override
  public boolean canSilkHarvest(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer player) {
    return false;
  }

  @Override
  public void onBlockPlaced(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityLivingBase player, @Nonnull T te) {
    te.setFacing(getFacingForHeading(player));
    if (player instanceof EntityPlayer && !world.isRemote) {
      te.setOwner((EntityPlayer) player);
    }
    if (world.isRemote) {
      return;
    }
    world.notifyBlockUpdate(pos, state, state, 3);

    // Enhanced machine extensions
    Block block = getEnhancedExtensionBlock();
    if (isEnhanced && block != null) {
      world.setBlockState(pos.up(), block.getDefaultState());
    }
  }

  protected @Nonnull EnumFacing getFacingForHeading(@Nonnull EntityLivingBase player) {
    return Util.getFacingFromEntity(player);
  }

  @Override
  public void onBlockAdded(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
    super.onBlockAdded(world, pos, state);
    world.notifyBlockUpdate(pos, state, state, 3);
  }

  @Override
  public void neighborChanged(@Nonnull IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Block blockIn, @Nonnull BlockPos fromPos) {
    AbstractMachineEntity te = getTileEntity(worldIn, pos);
    if (te != null) {
      te.onNeighborBlockChange(state, worldIn, pos, blockIn, fromPos);
    }

    // Enhanced machines extensions
    Block block = getEnhancedExtensionBlock();
    if (isEnhanced && block != null) {
      if (worldIn.getBlockState(pos.up()).getBlock() != block) {
        if (super.canPlaceBlockAt(worldIn, pos.up())) {
          worldIn.setBlockState(pos.up(), block.getDefaultState());
        } else {
          // impossible error state a.k.a. someone ripped the machine apart. And what do machines that are ripped apart do? They explode. Violently.
          worldIn.createExplosion(null, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, 3f, true); // 3 == normal Creeper
        }
      }
    }
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void randomDisplayTick(@Nonnull IBlockState bs, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
    // If active, randomly throw some smoke around
    if (isActive(world, pos)) {
      float startX = pos.getX() + 1.0F;
      float startY = pos.getY() + 1.0F;
      float startZ = pos.getZ() + 1.0F;
      for (int i = 0; i < 4; i++) {
        float xOffset = -0.2F - rand.nextFloat() * 0.6F;
        float yOffset = -0.1F + rand.nextFloat() * 0.2F;
        float zOffset = -0.2F - rand.nextFloat() * 0.6F;
        world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, startX + xOffset, startY + yOffset, startZ + zOffset, 0.0D, 0.0D, 0.0D);
      }
    }
  }

  protected boolean isActive(@Nonnull IBlockAccess blockAccess, @Nonnull BlockPos pos) {
    AbstractMachineEntity te = getTileEntitySafe(blockAccess, pos);
    if (te != null) {
      return te.isActive();
    }
    return false;
  }

  @Override
  public @Nonnull String getUnlocalizedNameForTooltip(@Nonnull ItemStack stack) {
    return getUnlocalizedName();
  }

  @SideOnly(Side.CLIENT)
  public IOMode.EnumIOMode mapIOMode(IoMode mode, EnumFacing side) {
    switch (mode) {
    case NONE:
      return IOMode.EnumIOMode.NONE;
    case PULL:
      return IOMode.EnumIOMode.PULL;
    case PUSH:
      return IOMode.EnumIOMode.PUSH;
    case PUSH_PULL:
      return IOMode.EnumIOMode.PUSHPULL;
    case DISABLED:
      return IOMode.EnumIOMode.DISABLED;
    }
    throw new RuntimeException("Hey, leave our enums alone!");
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull IRenderMapper.IItemRenderMapper getItemRenderMapper() {
    return RenderMappers.BODY_MAPPER;
  }

  @SideOnly(Side.CLIENT)
  public IRenderMapper.IBlockRenderMapper getBlockRenderMapper() {
    return RenderMappers.BODY_MAPPER;
  }

  // ///////////////////////////////////////////////////////////////////////
  // PAINT START
  // ///////////////////////////////////////////////////////////////////////

  @Override
  public boolean canRenderInLayer(@Nonnull IBlockState state, @Nonnull BlockRenderLayer layer) {
    return this instanceof IPaintable ? true : super.canRenderInLayer(state, layer);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public boolean addHitEffects(@Nonnull IBlockState state, @Nonnull World world, @Nonnull RayTraceResult target, @Nonnull ParticleManager effectRenderer) {
    return PaintHelper.addHitEffects(state, world, target, effectRenderer);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public boolean addDestroyEffects(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull ParticleManager effectRenderer) {
    return PaintHelper.addDestroyEffects(world, pos, effectRenderer);
  }

  // ///////////////////////////////////////////////////////////////////////
  // PAINT END
  // ///////////////////////////////////////////////////////////////////////

  @Override
  public @Nullable Container getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1) {
    T te = getTileEntity(world, pos);
    if (te != null) {
      return getServerGuiElement(player, world, pos, facing, param1, te);
    }
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nullable GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1) {
    T te = getTileEntity(world, pos);
    if (te != null) {
      return getClientGuiElement(player, world, pos, facing, param1, te);
    }
    return null;
  }

  public abstract @Nullable Container getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos,
      @Nullable EnumFacing facing, int param1, @Nonnull T te);

  @SideOnly(Side.CLIENT)
  public abstract @Nullable GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos,
      @Nullable EnumFacing facing, int param1, @Nonnull T te);

  protected final @Nonnull IShape<T> mkShape(@Nonnull BlockFaceShape down, @Nonnull BlockFaceShape up, @Nonnull BlockFaceShape front,
      @Nonnull BlockFaceShape allSides) {
    return new IShape<T>() {
      @Override
      @Nonnull
      public BlockFaceShape getBlockFaceShape(@Nonnull IBlockAccess worldIn, @Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull EnumFacing face,
          @Nonnull T te) {
        IBlockState paintSource = te.getPaintSource();
        if (paintSource != null) {
          try {
            return paintSource.getBlockFaceShape(worldIn, pos, face);
          } catch (Exception e) {
          }
        }
        return face == te.getFacing() ? front : IShape.super.getBlockFaceShape(worldIn, state, pos, face, te);
      }

      @Override
      @Nonnull
      public BlockFaceShape getBlockFaceShape(@Nonnull IBlockAccess worldIn, @Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull EnumFacing face) {
        return face == EnumFacing.UP ? up : face == EnumFacing.DOWN ? down : allSides;
      }
    };
  }

  @Override
  public boolean canConnectRedstone(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nullable EnumFacing side) {
    return true;
  }

  // Enhanced Machines

  @Override
  public boolean canPlaceBlockAt(@Nonnull World world, @Nonnull BlockPos pos) {
    return super.canPlaceBlockAt(world, pos) && (!isEnhanced || (pos.getY() < 255 && super.canPlaceBlockAt(world, pos.up())));
  }

  // Null be default. Enhanced machines can override this for their needed machine objects.
  @Nullable
  public Block getEnhancedExtensionBlock() {
    return null;
  }
}
