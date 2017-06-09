package crazypants.enderio.machine.base.block;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.util.Util;

import crazypants.enderio.BlockEio;
import crazypants.enderio.GuiID;
import crazypants.enderio.init.IModObject;
import crazypants.enderio.machine.base.te.AbstractMachineEntity;
import crazypants.enderio.machine.baselegacy.PacketLegacyPowerStorage;
import crazypants.enderio.machine.modes.IoMode;
import crazypants.enderio.machine.modes.PacketIoMode;
import crazypants.enderio.machine.render.RenderMappers;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.paint.PainterUtil2;
import crazypants.enderio.paint.render.PaintHelper;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.IRenderMapper;
import crazypants.enderio.render.ISmartRenderAwareBlock;
import crazypants.enderio.render.pipeline.BlockStateWrapperBase;
import crazypants.enderio.render.property.EnumRenderMode;
import crazypants.enderio.render.property.IOMode;
import crazypants.enderio.render.registry.SmartModelAttacher;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class AbstractMachineBlock<T extends AbstractMachineEntity> extends BlockEio<T>
    implements IGuiHandler, IResourceTooltipProvider, ISmartRenderAwareBlock {

  protected final @Nonnull Random random;

  protected final @Nonnull IModObject modObject;

  static {
    PacketHandler.INSTANCE.registerMessage(PacketIoMode.Handler.class, PacketIoMode.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketLegacyPowerStorage.Handler.class, PacketLegacyPowerStorage.class, PacketHandler.nextID(), Side.CLIENT);
  }

  protected AbstractMachineBlock(@Nonnull IModObject mo, @Nullable Class<T> teClass, @Nonnull Material mat) {
    super(mo.getUnlocalisedName(), teClass, mat);
    modObject = mo;
    setHardness(2.0F);
    setSoundType(SoundType.METAL);
    setHarvestLevel("pickaxe", 0);
    random = new Random();
    initDefaultState();
  }

  protected void initDefaultState() {
    setDefaultState(this.blockState.getBaseState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.AUTO));
  }

  protected AbstractMachineBlock(@Nonnull IModObject mo, @Nullable Class<T> teClass) {
    this(mo, teClass, new Material(MapColor.IRON));
  }

  @Override
  protected void init() {
    super.init();
    GuiID.registerGuiHandler(getGuiId(), this);
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
  protected boolean openGui(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer entityPlayer, @Nonnull EnumFacing side) {
    getGuiId().openGui(world, pos, entityPlayer, side);
    return true;
  }

  @Override
  public boolean canSilkHarvest(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer player) {
    return false;
  }

  @Override
  public boolean doNormalDrops(IBlockAccess world, BlockPos pos) {
    return false;
  }

  @Override
  protected void processDrop(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nullable AbstractMachineEntity te, @Nonnull ItemStack drop) {
    if (te != null) {
      te.writeToItemStack(drop);
    }
  }

  @Override
  public void onBlockPlacedBy(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityLivingBase player,
      @Nonnull ItemStack stack) {
    super.onBlockPlacedBy(world, pos, state, player, stack);
    AbstractMachineEntity te = getTileEntity(world, pos);
    if (te != null) {
      te.readFromItemStack(stack);
      te.setFacing(Util.getFacingFromEntity(player));
      if (player instanceof EntityPlayer && !world.isRemote) {
        te.setOwner((EntityPlayer) player);
      }
    }
    if (world.isRemote) {
      return;
    }
    world.notifyBlockUpdate(pos, state, state, 3);
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
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void randomDisplayTick(@Nonnull IBlockState bs, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
    // If active, randomly throw some smoke around
    int x = pos.getX();
    int y = pos.getY();
    int z = pos.getZ();
    if (isActive(world, pos)) {
      float startX = x + 1.0F;
      float startY = y + 1.0F;
      float startZ = z + 1.0F;
      for (int i = 0; i < 4; i++) {
        float xOffset = -0.2F - rand.nextFloat() * 0.6F;
        float yOffset = -0.1F + rand.nextFloat() * 0.2F;
        float zOffset = -0.2F - rand.nextFloat() * 0.6F;
        world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, startX + xOffset, startY + yOffset, startZ + zOffset, 0.0D, 0.0D, 0.0D);
      }
    }
  }

  protected abstract @Nonnull GuiID getGuiId();

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

  public @Nonnull IBlockState getFacade(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nullable EnumFacing side) {
    NullHelper.notnull(world, "Hey, how should I get you a block state without a world or position?");
    NullHelper.notnull(pos, "Hey, how should I get you a block state without a world or position?");
    IBlockState paintSource = getPaintSource(getDefaultState(), world, pos);
    return paintSource != null ? paintSource : NullHelper.notnullM(world.getBlockState(pos), "world.getBlockState(pos)");
  }

  public void setPaintSource(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nullable IBlockState paintSource) {
    if (this instanceof IPaintable) {
      T te = getTileEntity(world, pos);
      if (te instanceof IPaintable.IPaintableTileEntity) {
        ((IPaintable.IPaintableTileEntity) te).setPaintSource(paintSource);
      }
    }
  }

  public void setPaintSource(@Nonnull Block block, @Nonnull ItemStack stack, @Nullable IBlockState paintSource) {
    if (this instanceof IPaintable) {
      PainterUtil2.setSourceBlock(stack, paintSource);
    }
  }

  public @Nullable IBlockState getPaintSource(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    if (this instanceof IPaintable) {
      T te = getTileEntitySafe(world, pos);
      if (te instanceof IPaintable.IPaintableTileEntity) {
        return ((IPaintable.IPaintableTileEntity) te).getPaintSource();
      }
    }
    return null;
  }

  public @Nullable IBlockState getPaintSource(@Nonnull Block block, @Nonnull ItemStack stack) {
    if (this instanceof IPaintable) {
      return PainterUtil2.getSourceBlock(stack);
    }
    return null;
  }

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

}
