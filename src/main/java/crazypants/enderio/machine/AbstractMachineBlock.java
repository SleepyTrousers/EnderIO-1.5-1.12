package crazypants.enderio.machine;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.util.Util;

import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.IModObject;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.paint.PainterUtil2;
import crazypants.enderio.paint.render.PaintHelper;
import crazypants.enderio.render.EnumRenderMode;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.IOMode;
import crazypants.enderio.render.IRenderMapper;
import crazypants.enderio.render.ISmartRenderAwareBlock;
import crazypants.enderio.render.SmartModelAttacher;
import crazypants.enderio.render.TextureRegistry;
import crazypants.enderio.render.TextureRegistry.TextureSupplier;
import crazypants.enderio.render.pipeline.BlockStateWrapperBase;
import crazypants.enderio.waila.IWailaInfoProvider;
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

public abstract class AbstractMachineBlock<T extends AbstractMachineEntity> extends BlockEio<T> implements IGuiHandler, IResourceTooltipProvider,
    IWailaInfoProvider, ISmartRenderAwareBlock {
  
  public static final TextureSupplier selectedFaceIcon = TextureRegistry.registerTexture("blocks/overlays/selectedFace");

  protected final Random random;

  protected final IModObject modObject;

  static {
    PacketHandler.INSTANCE.registerMessage(PacketIoMode.class, PacketIoMode.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketItemBuffer.class, PacketItemBuffer.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketPowerStorage.class, PacketPowerStorage.class, PacketHandler.nextID(), Side.CLIENT);
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
    EnderIO.guiHandler.registerGuiHandler(getGuiId(), this);
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
  public IBlockState getStateFromMeta(int meta) {
    return getDefaultState();
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return 0;
  }

  @Override
  public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
    return getDefaultState();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public final IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
    if (state != null && world != null && pos != null) {
      IBlockStateWrapper blockStateWrapper = createBlockStateWrapper(state, world, pos);
      T tileEntity = getTileEntitySafe(world, pos);
      if (tileEntity != null) {
        setBlockStateWrapperCache(blockStateWrapper, world, pos, tileEntity);
      }
      blockStateWrapper.bakeModel();
      return blockStateWrapper;
    } else {
      return state;
    }
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
  protected boolean openGui(World world, BlockPos pos, EntityPlayer entityPlayer, EnumFacing side) {   
    if(!world.isRemote) {
      entityPlayer.openGui(EnderIO.instance, getGuiId(), world, pos.getX(), pos.getY(), pos.getZ());
    }
    return true;
  }
  
  @Override
  public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
    return false;
  }  

  @Override
  public boolean doNormalDrops(IBlockAccess world, BlockPos pos) { 
    return false;
  }

  @Override
  protected void processDrop(IBlockAccess world, BlockPos pos, @Nullable AbstractMachineEntity te, ItemStack drop) {
    if(te != null) {
      te.writeToItemStack(drop);
    }
  }

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack) {
    super.onBlockPlacedBy(world, pos, state, player, stack);
    if (world != null && pos != null) {
      AbstractMachineEntity te = getTileEntity(world, pos);
      if (te != null) {
        te.readFromItemStack(stack);
        if (player != null) {          
          te.setFacing(getFacingForHeading(player));
        }
      }
      if (world.isRemote) {
        return;
      }
      world.notifyBlockUpdate(pos, state, state, 3);
    }
  }

  protected EnumFacing getFacingForHeading(EntityLivingBase player) {
    return Util.getFacingFromEntity(player);
  }

  @Override
  public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
    super.onBlockAdded(world, pos,state);
    world.notifyBlockUpdate(pos, state, state, 3);
  }
  
  @Override
  public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighborBlock) {
    AbstractMachineEntity te = getTileEntity(world, pos);
    if (te != null) {
      te.onNeighborBlockChange(neighborBlock);
    }
  }

  
  
  
  @SideOnly(Side.CLIENT)
  @Override
  public void randomDisplayTick(IBlockState bs, World world, BlockPos pos, Random rand) {
    // If active, randomly throw some smoke around
    if (world != null && pos != null && rand != null) {
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
  }

  protected abstract int getGuiId();

  protected boolean isActive(@Nonnull IBlockAccess blockAccess, @Nonnull BlockPos pos) {
    AbstractMachineEntity te = getTileEntitySafe(blockAccess, pos);
    if (te != null) {
      return te.isActive();
    }
    return false;
  }
  
  @Deprecated
  protected boolean isActive(@Nonnull IBlockAccess blockAccess, int x, int y, int z) {
    return isActive(blockAccess, new BlockPos(x,y,z));
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack stack) {
    return getUnlocalizedName();
  }

  @Override
  public void getWailaInfo(List<String> tooltip, EntityPlayer player, World world, int x, int y, int z) {
  }

  @Override
  public int getDefaultDisplayMask(World world, int x, int y, int z) {
    return IWailaInfoProvider.ALL_BITS;
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
  public IRenderMapper.IItemRenderMapper getItemRenderMapper() {
    return RenderMappers.BODY_MAPPER;
  }

  @SideOnly(Side.CLIENT)
  public IRenderMapper.IBlockRenderMapper getBlockRenderMapper() {
    return RenderMappers.BODY_MAPPER;
  }

  // ///////////////////////////////////////////////////////////////////////
  // PAINT START
  // ///////////////////////////////////////////////////////////////////////

  public IBlockState getFacade(IBlockAccess world, BlockPos pos, EnumFacing side) {
    if (world == null || pos == null) {
      throw new NullPointerException("Hey, how should I get you a block state without a world or position?");
    }
    IBlockState paintSource = getPaintSource(getDefaultState(), world, pos);
    return paintSource != null ? paintSource : world.getBlockState(pos);
  }

  public void setPaintSource(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable IBlockState paintSource) {
    if (this instanceof IPaintable && world != null && pos != null) {
      T te = getTileEntity(world, pos);
      if (te instanceof IPaintable.IPaintableTileEntity) {
        ((IPaintable.IPaintableTileEntity) te).setPaintSource(paintSource);
      }
    }
  }

  public void setPaintSource(Block block, ItemStack stack, @Nullable IBlockState paintSource) {
    if (this instanceof IPaintable) {
      PainterUtil2.setSourceBlock(stack, paintSource);
    }
  }

  public @Nullable IBlockState getPaintSource(@Nullable IBlockState state, IBlockAccess world, BlockPos pos) {
    if (this instanceof IPaintable && world != null && pos != null) {
      T te = getTileEntitySafe(world, pos);
      if (te instanceof IPaintable.IPaintableTileEntity) {
        return ((IPaintable.IPaintableTileEntity) te).getPaintSource();
      }
    }
    return null;
  }

  public @Nullable IBlockState getPaintSource(Block block, ItemStack stack) {
    if (this instanceof IPaintable) {
      return PainterUtil2.getSourceBlock(stack);
    }
    return null;
  }

  @Override
  public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
    return this instanceof IPaintable ? true : super.canRenderInLayer(state, layer);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public boolean addHitEffects(IBlockState state, World world, RayTraceResult target, ParticleManager effectRenderer) {
    return PaintHelper.addHitEffects(state, world, target, effectRenderer);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager effectRenderer) {
    return PaintHelper.addDestroyEffects(world, pos, effectRenderer);
  }

  // ///////////////////////////////////////////////////////////////////////
  // PAINT END
  // ///////////////////////////////////////////////////////////////////////

}
